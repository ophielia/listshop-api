/**
 * Created by margaretmartin on 13/05/2017.
 */
package com.meg.listshop.lmt.service.tag.impl;

import com.meg.listshop.lmt.api.exception.ActionInvalidException;
import com.meg.listshop.lmt.api.exception.BadParameterException;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.api.model.DishRatingInfo;
import com.meg.listshop.lmt.api.model.RatingUpdateInfo;
import com.meg.listshop.lmt.api.model.SortOrMoveDirection;
import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.data.entity.*;
import com.meg.listshop.lmt.data.pojos.ICountResult;
import com.meg.listshop.lmt.data.pojos.LongTagIdPairDTO;
import com.meg.listshop.lmt.data.pojos.TagInfoDTO;
import com.meg.listshop.lmt.data.pojos.TagSearchCriteria;
import com.meg.listshop.lmt.data.repository.DishItemRepository;
import com.meg.listshop.lmt.data.repository.TagInfoCustomRepository;
import com.meg.listshop.lmt.data.repository.TagRepository;
import com.meg.listshop.lmt.service.DishSearchCriteria;
import com.meg.listshop.lmt.service.DishSearchService;
import com.meg.listshop.lmt.service.DishService;
import com.meg.listshop.lmt.service.ListTagStatisticService;
import com.meg.listshop.lmt.service.food.FoodService;
import com.meg.listshop.lmt.service.tag.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@Transactional
public class TagServiceImpl implements TagService {

    @Value("${service.tagservice.delete.tag.immediately:false}")
    boolean deleteImmediately;

    private final List<TagChangeListener> listeners = new CopyOnWriteArrayList<>();

    private final DishService dishService;
    private final ListTagStatisticService tagStatisticService;
    private final TagReplaceService tagReplaceService;
    private final TagRepository tagRepository;
    private final DishItemRepository dishItemRepository;
    private final TagStructureService tagStructureService;
    private final DishSearchService dishSearchService;
    private final TagInfoCustomRepository tagInfoCustomRepository;
    private final FoodService foodService;


    @Value("${shopping.list.properties.default_list_layout_id:5}")
    private Long defaultLayoutId;
    private long cachedRatingStructureExpires;
    private RatingStructureTree cachedRatingStructureTree;
    @Value("${tag.service.rating.cache.lifetime:5}")
    private long cacheRatingStructureLifetime;

    @Autowired
    public TagServiceImpl(ListTagStatisticService tagStatisticService,
                          @Lazy DishService dishService,
                          TagStructureService tagStructureService,
                          @Lazy TagReplaceService tagReplaceService,
                          TagRepository tagRepository,
                          TagInfoCustomRepository tagInfoCustomRepository,
                          DishSearchService dishSearchService,
                          DishItemRepository dishItemRepository,
                          FoodService foodService
    ) {
        this.dishService = dishService;
        this.tagStatisticService = tagStatisticService;
        this.tagReplaceService = tagReplaceService;
        this.tagRepository = tagRepository;
        this.tagStructureService = tagStructureService;
        this.dishSearchService = dishSearchService;
        this.tagInfoCustomRepository = tagInfoCustomRepository;
        this.dishItemRepository = dishItemRepository;
        this.foodService = foodService;

    }


    @Override
    public int deleteTagFromDish(Long userId, Long dishId, Long tagId) {
        return removeTagsFromDish(userId, dishId, Collections.singleton(tagId));
    }

    @Override
    public TagEntity save(TagEntity tag) {
        return tagRepository.save(tag);
    }

    @Override
    public TagEntity getTagById(Long tagId) {

        Optional<TagEntity> tagOpt = tagRepository.findById(tagId);

        if (tagOpt.isEmpty()) {
            return null;
        }

        TagEntity tag = tagOpt.get();
        if (tag.isToDelete() != null && tag.isToDelete()) {
            Long newTagId = tag.getReplacementTagId();
            return newTagId != null ? getTagById(newTagId) : null;
        }
        return tag;
    }

    public RatingUpdateInfo getRatingUpdateInfoForDishIds(List<Long> dishIdList) {
        // put into lookup - rating info for child tags
        RatingStructureTree ratingStructure = getRatingStructure();

        Set<DishRatingInfo> ratingsForDishIds = processRatingInfo(ratingStructure, dishIdList);

        return new RatingUpdateInfo(Collections.emptySet(), ratingsForDishIds);
    }

    private RatingStructureTree getRatingStructure() {
        // check for cache
        if (cachedRatingStructureExpires > new Date().getTime()) {
            return cachedRatingStructureTree;
        }

        // get ratings structure
        List<TagInfoDTO> ratings = getTagInfoList(null, Collections.singletonList(TagType.Rating));

        // put into lookup - rating info for child tags and create headers set
        cachedRatingStructureTree = new RatingStructureTree(ratings);
        cachedRatingStructureExpires = new Date().getTime() + (cacheRatingStructureLifetime * 60 * 60 * 1000);


        return cachedRatingStructureTree;
    }

    private Set<DishRatingInfo> processRatingInfo(RatingStructureTree ratingInfoProvider, List<Long> dishIdList) {
        Set<DishRatingInfo> dishRatingInfoSet = new HashSet<>();

        // make set - loop through, adding to set
        dishIdList.forEach(dishId -> {
            // get tags for dish
            List<TagInfoDTO> tagsForDish = tagInfoCustomRepository.retrieveRatingInfoForDish(dishId);
            // use builder to retrieve dishinfo - defaults, or value from persisted rating tags
            DishRatingInfo dishRatingInfo = new DishRatingBuilder(ratingInfoProvider)
                    .withDishId(dishId)
                    .buildRatingInfo(tagsForDish);
            dishRatingInfoSet.add(dishRatingInfo);
        });

        return dishRatingInfoSet;
    }

    @Override
    public Map<Long, TagEntity> getDictionaryForIds(Set<Long> tagIds) {
        List<TagEntity> tags = tagRepository.findAllById(tagIds);
        if (!tags.isEmpty()) {
            return tags.stream().collect(Collectors.toMap(TagEntity::getId,
                    c -> c));

        }
        return new HashMap<>();
    }

    @Override
    public List<TagEntity> getReplacedTagsFromIds(Set<Long> tagKeys) {
        List<TagEntity> tags = tagRepository.findTagsToBeReplaced(tagKeys);
        if (tags == null) {
            return new ArrayList<>();
        }
        return tags;
    }

    @Override
    public TagEntity updateTag(Long tagId, TagEntity toUpdate) {
        // get tag from db
        Optional<TagEntity> dbTagOpt = tagRepository.findById(tagId);
        if (dbTagOpt.isEmpty()) {
            return null;
        }
        TagEntity dbTag = dbTagOpt.get();

        // save changes to tag
        TagEntity beforeChange = dbTag.copy();
        if (toUpdate.getName() != null) {
            dbTag.setName(toUpdate.getName());
        }
        if (toUpdate.getDescription() != null) {
            dbTag.setDescription(toUpdate.getDescription());
        }
        dbTag.setIsGroup(toUpdate.getIsGroup());
        if (toUpdate.getPower() != null) {
            dbTag.setPower(toUpdate.getPower());
        }
        if (toUpdate.isToDelete() != null) {
            dbTag.setToDelete(toUpdate.isToDelete());
        }
        if (toUpdate.getRemovedOn() != null) {
            dbTag.setRemovedOn(toUpdate.getRemovedOn());
        }
        if (toUpdate.getCreatedOn() != null) {
            dbTag.setCreatedOn(toUpdate.getCreatedOn());
        }
        if (toUpdate.getCategoryUpdatedOn() != null) {
            dbTag.setCategoryUpdatedOn(toUpdate.getCategoryUpdatedOn());
        }
        if (toUpdate.getReplacementTagId() != null) {
            dbTag.setReplacementTagId(toUpdate.getReplacementTagId());
        }

        dbTag.setUpdatedOn(new Date());
        dbTag = tagRepository.save(dbTag);


        // if changed, maintain change
        fireTagUpdatedEvent(beforeChange, dbTag);

        // fire tag changed event
        return dbTag;
    }

    public List<TagInfoDTO> getTagInfoList(Long userId, List<TagType> tagTypes) {
        return tagInfoCustomRepository.retrieveTagInfoByUser(userId, tagTypes);
    }

    public List<TagInfoDTO> getTagInfoList(TagSearchCriteria criteria){
        return tagInfoCustomRepository.findTagInfoByCriteria(criteria);
    }


    public List<LongTagIdPairDTO> getStandardUserDuplicates(Long userId, Set<Long> tagKeys) {
        return tagRepository.getStandardUserDuplicates(userId, tagKeys);
    }

    @Override
    public List<TagEntity> getIngredientTagsForDishes(List<Long> dishIdList) {
        return tagRepository.getIngredientTagsForDishes(dishIdList);
    }

    @Override
    public TagEntity createTag(Long parentId, TagEntity newTag, Long tagUserId) throws BadParameterException {
        if (parentId == null) {
            throw new BadParameterException("Parent Id is required to create tag.");
        }
        TagEntity parent = getTagById(parentId);
        if (parent == null) {
            throw new BadParameterException("Parent not found while creating tag.");
        }

        // check for duplicate
        TagEntity existingTag = getExistingTag(newTag, tagUserId);
        if (existingTag != null) {
            return existingTag;
        }

        TagEntity parentTag = getParentForNewTag(parent, newTag);
        newTag.setToDelete(false);
        newTag.setCreatedOn(new Date());
        newTag.setUserId(tagUserId);
        TagEntity saved = tagRepository.save(newTag);

        tagStructureService.createRelation(parentTag, saved);

        fireTagAddedEvent(parentTag, saved);
        return newTag;
    }

    private TagEntity getExistingTag(TagEntity newtag, Long userId) {
        Optional<TagEntity> tag = tagRepository.findTagDuplicate(newtag.getName().toLowerCase().trim(), newtag.getTagType(), newtag.getIsGroup(), userId);
        return tag.orElse(null);
    }

    @Override
    public void saveTagForDelete(Long tagId, Long replacementTagId) {
        TagEntity tag = getTagById(tagId);
        TagEntity replacement = getTagById(replacementTagId);

        // do validation
        //      tag is found
        if (tag == null) {
            throw new ActionInvalidException("TAG not found for id [" + tagId + "].");
        }
        //      tag to be deleted doesn't have children
        List<TagEntity> children = tagStructureService.getChildren(tag);
        if (children != null && !children.isEmpty()) {
            throw new ActionInvalidException("TAG [" + tag.getId() + "] can't be deleted because it has children.");
        }
        // replacement tag exists
        if (replacementTagId == null) {
            throw new ActionInvalidException("Replacement TAG Id is null");
        }
        // tag is not same as replacement tag
        if (replacementTagId.equals(tagId)) {
            throw new ActionInvalidException("TAG cannot replace itself.");

        }

        // mark tag to be deleted
        tag.setToDelete(true);
        tag.setRemovedOn(new Date());

        tag.setReplacementTagId(replacement.getId());
        // update and replace usage
        updateTag(tagId, tag);
        tagReplaceService.replaceTag(tag.getId(), tag.getReplacementTagId());

    }

    public void incrementDishRating(Long userId, Long dishId, Long ratingId, SortOrMoveDirection moveDirection) {
        // get dish
        DishEntity dish = dishService.getDishForUserById(userId, dishId);
        if (dish == null) {
            throw new ActionInvalidException("Can't find dish for id [" + dishId + "]");
        }
        RatingStructureTree ratingStructure = getRatingStructure();

        // get current assigned tag for parent rating id
        TagEntity currentTag = tagRepository.getAssignedTagForRating(dishId, ratingId);
        // if no tag is available, get the default
        Long currentTagId;
        if (currentTag == null) {
            currentTagId = ratingStructure.getDefaultTagIdForRating(ratingId);
        } else {
            currentTagId = currentTag.getId();
        }

        // get new tag (depending upon direction)
        TagEntity nextTag;
        if (SortOrMoveDirection.UP.equals(moveDirection)) {
            nextTag = tagRepository.getNextRatingUp(ratingId, currentTagId);
        } else {
            nextTag = tagRepository.getNextRatingDown(ratingId, currentTagId);
        }
        // assign new tag
        addTagToDish(dish, nextTag);

    }

    public void setDishRating(Long userId, Long dishId, Long ratingId, Integer step) {
        // get dish
        DishEntity dish = dishService.getDishForUserById(userId, dishId);
        if (dish == null) {
            throw new ActionInvalidException("Can't find dish for id [" + dishId + "]");
        }

        // get new step tag from db
        Long newTagId = tagRepository.findRatingTagIdForStep(ratingId, step);
        if (newTagId == null) {
            throw new ObjectNotFoundException("Can't find step [" + step + "] for ratingId [" + ratingId + "]");
        }
        Optional<TagEntity> tag = tagRepository.findById(newTagId);
        if (tag.isEmpty()) {
            throw new ObjectNotFoundException("Shouldn't happen: Can't retrieve tag for tag_id [" + newTagId + "]");
        }
        // assign new tag
        addTagToDish(dish, tag.get());
    }


    @Override
    public List<DishItemEntity> getItemsForDish(String username, Long dishId) {
        return getItemsForDish(username, dishId, null);
    }


    @Override
    public List<DishItemEntity> getItemsForDish(Long userId, Long dishId) {
        return getItemsForDish(userId, dishId, null);
    }

    private List<DishItemEntity> getItemsForDish(String username, Long dishId, List<TagType> tagtypes) {
        List<DishItemEntity> results = new ArrayList<>();
        DishEntity dish = dishService.getDishForUserById(username, dishId);

        if (dish == null) {
            return results;
        }

        return getItemsForDish(dish, tagtypes);
    }

    @Override
    public List<TagEntity> getTagsForDish(Long userId, Long dishId, List<TagType> tagtypes) {
        List<TagEntity> results = new ArrayList<>();
        DishEntity dish = dishService.getDishForUserById(userId, dishId);

        if (dish == null) {
            return results;
        }

        return getTagsForDish(dish, tagtypes);
    }

    @Override
    public List<DishItemEntity> getItemsForDish(Long userId, Long dishId, List<TagType> tagtypes) {
        List<DishItemEntity> results = new ArrayList<>();
        DishEntity dish = dishService.getDishForUserById(userId, dishId);

        if (dish == null) {
            return results;
        }

        return getItemsForDish(dish, tagtypes);
    }


    @Override
    public void addTagToDish(Long userId, Long dishId, Long tagId) {
        TagEntity tag = getTagById(tagId);
        if (dishId == null || tagId == null) {
            return;
        }

        // get dish
        DishEntity dish = dishService.getDishForUserById(userId, dishId);

        addTagToDish(dish, tag);
    }

    @Override
    public void assignTagToParent(Long tagId, Long parentId) {
        // get tag and parent
        TagEntity tag = getTagById(tagId);


        TagEntity parentTag = getTagById(parentId);


        assignTagToParent(tag, parentTag);
    }

    @Override
    public void assignChildrenToParent(Long parentId, List<Long> childrenIds) {
        // get parent id
        TagEntity parentTag = getTagById(parentId);

        if (parentTag == null) {
            return;
        }

        // update tag relation
        for (Long tagId : childrenIds) {
            TagEntity tag = getTagById(tagId);
            assignTagToParent(tag, parentTag);
        }
    }

    @Override
    public void addTagsToDish(Long userId, Long dishId, Set<Long> tagIds) {
        addTagsToDish(userId, dishId, tagIds, true);
    }

    private void addTagsToDish(Long userId, Long dishId, Set<Long> tagIds, boolean checkRatingSiblings) {
        DishEntity dish = dishService.getDishForUserById(userId, dishId);
        if (dish == null) {
            return;
        }

        List<DishItemEntity> dishItems = dish.getItems();
        Set<Long> existingTagIds = dishItems.stream()
                .map(DishItemEntity::getTag)
                .map(TagEntity::getId)
                .collect(Collectors.toSet());

        Set<Long> tagIdsToAddFiltered = tagIds.stream()
                .filter(t -> !existingTagIds.contains(t))
                .collect(Collectors.toSet());

        List<TagEntity> tagList = tagRepository.getTagsForIdList(tagIdsToAddFiltered);
        List<TagEntity> ratingChecks = new ArrayList<>();

        for (TagEntity addTag : tagList) {
            // need to clean up ratings afterwards?
            if (addTag.getTagType() == TagType.Rating) {
                ratingChecks.add(addTag);
            }
            DishItemEntity item = new DishItemEntity();
            item.setDish(dish);
            item.setTag(addTag);
            dishItemRepository.save(item);
            dishItems.add(item);
        }

        // if rating tags exist, remove related tags
        if (!ratingChecks.isEmpty() && checkRatingSiblings) {
            for (TagEntity ratingTag : ratingChecks) {
                dishItems = removeRelatedItems(dishItems, ratingTag);
            }
        }

        // add tags to dish
        dish.setItems(dishItems);
        dishService.save(dish, false);
    }

    @Override
    public int removeTagsFromDish(Long userId, Long dishId, Set<Long> tagIds) {
        // get dish
        DishEntity dish = dishService.getDishForUserById(userId, dishId);
        if (dish == null) {
            return 0;
        }
        Set<Long> validatedRemovals = determineValidTagsToRemove(dishId, tagIds);

        // if nothing is validated - we return
        if (validatedRemovals.isEmpty()) {
            return 0;
        }

        // filter tag to be deleted from dish
        List<DishItemEntity> dishItems = dish.getItems();
        List<DishItemEntity> dishItemsToRemove = dishItems.stream()
                .filter(t -> (validatedRemovals.contains(t.getTag().getId())))
                .collect(Collectors.toList());

        // if nothing to remove, return
        if (dishItemsToRemove.isEmpty()) {
            return 0;
        }

        // add tags to dish
        dish.removeItems(dishItemsToRemove);
        dishItemRepository.deleteAll(dishItemsToRemove);

        dishService.save(dish, false);
        return dishItemsToRemove.size();
    }

    public void assignDefaultRatingsToDish(Long userId, Long dishId) {
        RatingStructureTree ratingStructureTree = getRatingStructure();
        Set<Long> defaultTagIds = ratingStructureTree.getAllDefaultTagIds();

        // add the tags to the dish
        addTagsToDish(userId, dishId, defaultTagIds, false);
    }

    public List<FoodEntity> getSuggestedFoods(Long tagId) {
        // get tag
        TagEntity tag = getTagById(tagId);
        if (tag == null) {
            return new ArrayList<>();
        }

        // first find applicable food category for tag
        List<FoodEntity> suggestions;
        List<FoodEntity> preferredList = new ArrayList<>();
        List<FoodEntity> otherList = new ArrayList<>();
        FoodCategoryEntity categoryMatch = findClosestFoodCategory(tag);
            suggestions = foodService.foodMatches(tag.getName());
        if (categoryMatch != null) {
            suggestions.stream().forEach( f -> {
                if (f.getCategoryId().equals(categoryMatch.getId())) {
                    preferredList.add(f);
                } else {
                    otherList.add(f);
                }
            });
            preferredList.addAll(otherList);
        } else {
            preferredList.addAll(suggestions);
        }

        return preferredList;
    }

    private FoodCategoryEntity findClosestFoodCategory(TagEntity tag) {
        List<Long> tagSearchIds = new ArrayList<>();
        tagSearchIds.add(tag.getId());
        tagSearchIds.addAll(tagStructureService.getAscendantTags(tag).stream()
                .map(TagEntity::getId)
                .collect(Collectors.toList()));

        TagSearchCriteria searchCriteria = new TagSearchCriteria();
        searchCriteria.setTagIds(tagSearchIds);
        List<TagInfoDTO> allTagInfo = tagInfoCustomRepository.findTagInfoByCriteria(searchCriteria);
        return foodService.getCategoryMatchForTag(tag.getId() , allTagInfo);
    }

    private Set<Long> determineValidTagsToRemove(Long dishId, Set<Long> tagIds) {
        List<ICountResult> remainingCounts = tagRepository.countRemainingDishTypeTags(dishId, tagIds);
        if (remainingCounts == null || remainingCounts.isEmpty()) {
            return tagIds;
        }
        int remainingCount = remainingCounts.get(0).getCountResult();
        if (remainingCount >= 1) {
            // last dish type tag not removed in this set
            return tagIds;
        }

        // deleting this set would result in a dish without any dish tag. We'll remove
        // all dish type tags, so that the algorithm won't "decide" which tag stays
        List<TagEntity> tagsToBeDeleted = tagRepository.findAllById(tagIds);
        return tagsToBeDeleted.stream()
                .filter(t -> !t.getTagType().equals(TagType.DishType))
                .map(TagEntity::getId)
                .collect(Collectors.toSet());
    }


    @Override
    public void replaceTagInDishes(Long userId, Long fromTagId, Long toTagId) {
        // MM dish items needs help

        List<DishEntity> dishes;
        TagEntity toTag = getTagById(toTagId);

        if (fromTagId.equals(0L)) {
            // this is a request to assign unassigned tags
            TagEntity parent = tagStructureService.getParentTag(toTag);
            List<TagEntity> allChildren = tagStructureService.getChildren(parent);
            List<Long> excludeTags = allChildren.stream().map(TagEntity::getId).collect(Collectors.toList());
            var criteria = new DishSearchCriteria(userId);
            criteria.setExcludedTagIds(excludeTags);
            dishes = dishSearchService.findDishes(criteria);
        } else {
            var criteria = new DishSearchCriteria(userId);
            criteria.setIncludedTagIds(Collections.singletonList(fromTagId));
            dishes = dishSearchService.findDishes(criteria);
        }

        // for each dish
        for (DishEntity dish : dishes) {


            // new item
            DishItemEntity newItem = new DishItemEntity();
            newItem.setTag(toTag);
            newItem.setDish(dish);

            List<DishItemEntity> items = dish.getItems();
            items.add(newItem);
            if (!fromTagId.equals(0L)) {
                items = items.stream()
                        .filter(t -> !t.getTag().getId().equals(fromTagId))
                        .collect(Collectors.toList());
            }
            dish.setItems(items);
            dishService.save(dish, false);
        }
    }


    @Override
    public void addTagChangeListener(TagChangeListener tagChangeListener) {
        listeners.add(tagChangeListener);
    }

    public void assignTagsToUser(Long userId, List<Long> tagIds) {
        List<Long> tagsToUpdate = tagIds.stream()
                        .filter(t -> !usedByOtherUsers(userId, t))
                                .collect(Collectors.toList());

        tagRepository.assignTagsToUser(userId, tagsToUpdate);
    }
    private boolean usedByOtherUsers(Long userId, Long tagId) {
        if (tagRepository.findUsersWithTagInDish(userId, tagId) > 0) {
            return true;
        }
        return tagRepository.findUsersWithTagInList(userId, tagId) > 0;
    }

    public void setTagsAsVerified(List<Long> tagIds) {
        tagRepository.setTagsAsVerified(tagIds);
    }

    public void createStandardTagsFromUserTags(List<Long> tagIds) {
        Set<Long> copySet = determineTagsEligibleForStandard(tagIds);
        // get tags to copy
        List<TagEntity> tagsToCopy = tagRepository.getTagsForIdList(copySet);
        // get standard parents for tags
        Map<Long, TagEntity> parentsForTags = getStandardParentsForTags(copySet);
        // get standard categories for tags
        Map<Long, Long> categoriesForTags = getStandardCategoriesForTags(copySet);
        // get default tag parent
        Map<TagType, TagEntity> defaultTagParentsByType = getDefaultTagParentsByType();

        List<TagEntity> addedTags = new ArrayList<>();
        // copy tags
        for (TagEntity tagToCopy : tagsToCopy) {
            Long copyId = tagToCopy.getId();
            //      copy interesting tag info, and create tag
            TagEntity newTag = copyTagIntoNewTag(tagToCopy);
            //      create tag
            newTag = tagRepository.save(newTag);
            //      create membership to tag group
            TagEntity parentTag;
            if (parentsForTags.containsKey(copyId)) {
                parentTag = parentsForTags.get(copyId);
            } else {
                parentTag = defaultTagParentsByType.get(tagToCopy.getTagType());
            }
            tagStructureService.assignTagToParent(newTag, parentTag);

            // set original to isVerified = true
            tagToCopy.setVerified(true);

            addedTags.add(newTag);

            // fire listener, which will update category
            fireTagCopiedEvent(newTag, categoriesForTags.get(copyId));
        }

        tagRepository.saveAll(addedTags);

    }

    private Set<Long> determineTagsEligibleForStandard(List<Long> tagIds) {
        List<Long> alreadyExisting =  tagRepository.findDuplicateStandardTags(tagIds );
        return tagIds.stream()
                .filter(t -> !alreadyExisting.contains(t))
                .collect(Collectors.toSet());
    }


    private Map<TagType, TagEntity> getDefaultTagParentsByType() {
        List<TagEntity> defaultParents = tagRepository.findTagsByTagTypeDefaultTrue();
        return defaultParents.stream()
                .collect(Collectors.toMap(TagEntity::getTagType, Function.identity()));
    }

    private Map<Long, Long> getStandardCategoriesForTags(Set<Long> copySet) {

        List<Object[]> categoryRelations = tagRepository.getStandardCategoriesForTags(copySet, defaultLayoutId);
        return categoryRelations.stream().map(o -> {
            BigInteger tagId = (BigInteger) o[0];
            BigInteger parentId = (BigInteger) o[1];
            return new LongTagIdPairDTO(tagId.longValue(), parentId.longValue());
        }).collect(Collectors.toMap(LongTagIdPairDTO::getLeftId, LongTagIdPairDTO::getRightId));
    }

    private Map<Long, TagEntity> getStandardParentsForTags(Set<Long> copySet) {
        List<Object[]> parentRelations = tagRepository.getStandardParentsForTags(copySet);
        Map<Long, Long> parentRelationIds = parentRelations.stream().map(o -> {
            BigInteger tagId = (BigInteger) o[0];
            BigInteger parentId = (BigInteger) o[1];
            return new LongTagIdPairDTO(tagId.longValue(), parentId.longValue());
        }).collect(Collectors.toMap(LongTagIdPairDTO::getLeftId, LongTagIdPairDTO::getRightId));

        List<TagEntity> parentTags = tagRepository.getTagsForIdList(new HashSet<>(parentRelationIds.values()));
        Map<Long, TagEntity> parentDictionary = parentTags.stream()
                .collect(Collectors.toMap(TagEntity::getId, Function.identity()));

        Map<Long, TagEntity> parentsForTagIds = new HashMap<>();
        parentRelationIds.entrySet().stream()
                .filter(e -> parentDictionary.containsKey(e.getKey()))
                .forEach(e -> parentsForTagIds.put(e.getKey(), parentDictionary.get(e.getKey())));
        return parentsForTagIds;
    }

    private TagEntity copyTagIntoNewTag(TagEntity tagToCopy) {
        TagEntity newTag = new TagEntity();
        newTag.setVerified(true);
        newTag.setUserId(null);
        newTag.setTagType(tagToCopy.getTagType());
        newTag.setName(tagToCopy.getName());
        newTag.setDescription(tagToCopy.getDescription());
        newTag.setPower(tagToCopy.getPower());
        newTag.setCreatedOn(new Date());
        newTag.setIsGroup(tagToCopy.getIsGroup());

        return newTag;
    }

    private void fireTagParentChangedEvent(TagEntity oldParent, TagEntity newParent, TagEntity changedTag) {
        for (TagChangeListener listener : listeners) {
            listener.onParentChange(oldParent, newParent, changedTag);
        }
    }


    private void fireTagUpdatedEvent(TagEntity beforeChange, TagEntity changed) {
        for (TagChangeListener listener : listeners) {
            listener.onTagUpdate(beforeChange, changed);
        }
    }

    private void fireTagAddedEvent(TagEntity parentTag, TagEntity changed) {
        for (TagChangeListener listener : listeners) {
            listener.onTagAdd(changed, parentTag);
        }
    }

    private void fireTagCopiedEvent(TagEntity copiedTag, Long categoryId) {
        for (TagChangeListener listener : listeners) {
            listener.onTagCopy(copiedTag, categoryId);
        }
    }


    private List<DishItemEntity> removeRelatedItems(List<DishItemEntity> dishItems, TagEntity tag) {
        // get sibling tags for dish
        List<Long> siblings = tagStructureService.getSiblingTags(tag).stream()
                .map(TagEntity::getId)
                .collect(Collectors.toList());

        List<DishItemEntity> itemsToRemove = dishItems.stream()
                .filter(t -> siblings.contains(t.getTag().getId()))
                .collect(Collectors.toList());

        dishItemRepository.deleteAll(itemsToRemove);

        return dishItems.stream()
                .filter(t -> !siblings.contains(t.getTag().getId()))
                .collect(Collectors.toList());
    }


    private void addTagToDish(DishEntity dish, TagEntity tag) {
        if (dish == null || tag == null) {
            return;
        }

        List<DishItemEntity> dishItems = dish.getItems();

        // check if tag exists already
        TagEntity existingTag = dishItems.stream()
                .map(DishItemEntity::getTag)
                .filter(t -> t.getId().equals(tag.getId()))
                .findFirst()
                .orElse(null);

        if (existingTag != null) {
            return;
        }

        DishItemEntity newItem = new DishItemEntity();
        newItem.setDish(dish);
        newItem.setTag(tag);
        dishItemRepository.save(newItem);
        dishItems.add(newItem);

        // if rating tag, remove related tags
        if (tag.getTagType().equals(TagType.Rating)) {
            dishItems = removeRelatedItems(dishItems, tag);
        }
        // add tags to dish
        dish.setItems(dishItems);
        dishService.save(dish, false);
        // update statistic
        tagStatisticService.countTagAddedToDish(dish.getUserId(), tag.getId());
    }


    private List<TagEntity> getTagsForDish(DishEntity dish, List<TagType> tagtypes) {
        return filterItemsByTagType(dish.getItems(), tagtypes).stream()
                .map(DishItemEntity::getTag)
                .collect(Collectors.toList());
    }

    private List<DishItemEntity> getItemsForDish(DishEntity dish, List<TagType> tagtypes) {
        return filterItemsByTagType(dish.getItems(), tagtypes);
    }

    private List<DishItemEntity> filterItemsByTagType(List<DishItemEntity> dishItems, List<TagType> tagtypes) {
        if (tagtypes == null) {
            return dishItems;
        }
        if (dishItems == null) {
            return new ArrayList<>();
        }
        return dishItems.stream()
                .filter(t -> tagtypes.contains(t.getTag().getTagType()))
                .collect(Collectors.toList());
    }

    private void assignTagToParent(TagEntity childTag, TagEntity newParentTag) {
        // get original parent (parent before reassign)
        TagEntity originalParent = tagStructureService.getParentTag(childTag);
        // assign Child tag to parent tag
        TagEntity parentTag = tagStructureService.assignTagToParent(childTag, newParentTag);

        childTag.setUpdatedOn(new Date());
        // fire tag changed event
        fireTagParentChangedEvent(originalParent, parentTag, childTag);


    }

    private TagEntity getParentForNewTag(TagEntity parent, TagEntity newtag) {
        if (parent != null) {
            return parent;
        }
        var tagType = newtag.getTagType();
        if (tagType == null) {
            tagType = TagType.TagType;
        }
        List<TagEntity> defaults = tagRepository.findTagsByTagTypeAndTagTypeDefault(tagType, true);
        if (defaults != null && !defaults.isEmpty()) {
            return defaults.get(0);
        }
        return null;
    }
}
