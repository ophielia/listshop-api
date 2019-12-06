package com.meg.atable.lmt.service.tag.impl;

import com.meg.atable.auth.data.entity.UserEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.lmt.api.exception.ActionInvalidException;
import com.meg.atable.lmt.api.model.*;
import com.meg.atable.lmt.data.entity.DishEntity;
import com.meg.atable.lmt.data.entity.TagEntity;
import com.meg.atable.lmt.data.entity.TagExtendedEntity;
import com.meg.atable.lmt.data.repository.TagExtendedRepository;
import com.meg.atable.lmt.data.repository.TagRepository;
import com.meg.atable.lmt.service.*;
import com.meg.atable.lmt.service.tag.TagChangeListener;
import com.meg.atable.lmt.service.tag.TagReplaceService;
import com.meg.atable.lmt.service.tag.TagService;
import com.meg.atable.lmt.service.tag.TagStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
@Transactional
public class TagServiceImpl implements TagService {

    @Value("${service.tagservice.delete.tag.immediately:false}")
    boolean deleteImmediately;

    private final List<TagChangeListener> listeners = new CopyOnWriteArrayList<>();

    private DishService dishService;
    private ListTagStatisticService tagStatisticService;
    private TagExtendedRepository tagExtendedRepository;
    private TagReplaceService tagReplaceService;
    private TagRepository tagRepository;
    private TagStructureService tagStructureService;
    private UserService userService;
    private ListLayoutService listLayoutService;
    private DishSearchService dishSearchService;


    @Autowired
    public TagServiceImpl(ListTagStatisticService tagStatisticService,
                          @Lazy DishService dishService,
                          TagStructureService tagStructureService,
                          @Lazy TagReplaceService tagReplaceService,
                          TagExtendedRepository tagExtendedRepository,
                          TagRepository tagRepository,
                          UserService userService,
                          DishSearchService dishSearchService) {
        this.dishService = dishService;
        this.tagStatisticService = tagStatisticService;
        this.tagExtendedRepository = tagExtendedRepository;
        this.tagReplaceService = tagReplaceService;
        this.tagRepository = tagRepository;
        this.tagStructureService = tagStructureService;
        this.userService = userService;
        this.dishSearchService = dishSearchService;

    }




    @Override
    public void deleteTagFromDish(String userName, Long dishId, Long tagId) {
        if (tagId == null) {
            return;
        }
        // get dish
        DishEntity dish = dishService.getDishForUserById(userName, dishId);
        if (dish == null) {
            return;
        }
        // filter tag to be deleted from dish
        List<TagEntity> dishTags = tagRepository.findTagsByDishes(dish);
        List<TagEntity> dishTagsDeletedTag = dishTags.stream()
                .filter(t -> !t.getId().equals(tagId))
                .collect(Collectors.toList());
        // add tags to dish
        dish.setTags(dishTagsDeletedTag);
        dishService.save(dish, false);
    }

    @Override
    public TagEntity save(TagEntity tag) {
        return tagRepository.save(tag);
    }

    @Override
    public TagEntity getTagById(Long tagId) {

        return getTagById(tagId, false);
    }

    @Override
    public TagEntity getTagById(Long tagId, Boolean swapIfReplaced) {

        Optional<TagEntity> tagOpt = tagRepository.findById(tagId);

        if (!tagOpt.isPresent()) {
            return null;
        }

        TagEntity tag = tagOpt.get();
        if (tag.isToDelete()) {
            Long newTagId = tag.getReplacementTagId();
            return getTagById(newTagId);
        }
        return tag;
    }

    public RatingUpdateInfo getRatingUpdateInfoForDishIds(String userName, List<Long> dishIdList) {
        // get ratings structure
        List<FatTag> ratingTagsWithChildren = tagStructureService.getTagsWithChildren(Collections.singletonList(TagType.Rating));

        // put into lookup - rating info for child tags and create headers set
        LookupInformation parsedInfo = parseRatingTags(ratingTagsWithChildren);
        Map<Long, RatingInfo> ratingInfoById = parsedInfo.getTagToRatingInfo();
        Set<RatingInfo> headers = parsedInfo.getParentRatings();

        // get all tags for dishes
        List<DishEntity> dishes = dishService.getDishes(userName, dishIdList);

        // fill temporary hash with info objects
        Map<DishRatingInfo, Set<RatingInfo>> unorderedInfo = processDishRatings(dishes, ratingInfoById);

        // fill the MealRatingInfo objects in order of the headers
        for (RatingInfo headerTag : headers) {
            for (Map.Entry<DishRatingInfo, Set<RatingInfo>> entry : unorderedInfo.entrySet()) {
                if (entry.getValue().contains(headerTag)) {
                    // dish has this rating - add it to the DishRatingInfo
                    Optional<RatingInfo> ratingInfoOpt = entry.getValue()
                            .stream()
                            .filter(r -> r.equals(headerTag)).findFirst();
                    ratingInfoOpt.ifPresent(ratingInfo -> entry.getKey().addRating(ratingInfo));
                } else {
                    // dish does not have this rating -
                    // create this tag for the dish, and add it to the DishRatingInfo
                    Optional<FatTag> tagOpt = ratingTagsWithChildren.stream().filter(ft -> ft.getId().equals(headerTag.getRatingTagId())).findFirst();
                    if (tagOpt.isPresent()) {
                        Long assignTagId = getDefaultTagIdForRating(tagOpt.get());
                        if (assignTagId != null) {
                            addTagToDish(userName, entry.getKey().getDishId(), assignTagId);
                            RatingInfo ratingInfo = ratingInfoById.get(assignTagId);
                            entry.getKey().addRating(ratingInfo);
                        }
                    }
                }
            }
        }

        return new RatingUpdateInfo(headers, unorderedInfo.keySet());
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
        if (!dbTagOpt.isPresent()) {
            return null;
        }
        TagEntity dbTag = dbTagOpt.get();

        // save changes to tag
        TagEntity beforeChange = dbTag.copy();
        dbTag.setName(toUpdate.getName());
        dbTag.setDescription(toUpdate.getDescription());
        dbTag.setAssignSelect(toUpdate.getAssignSelect());
        dbTag.setSearchSelect(toUpdate.getSearchSelect());
        dbTag.setPower(toUpdate.getPower());
        dbTag.setToDelete(toUpdate.isToDelete());
        dbTag.setRemovedOn(toUpdate.getRemovedOn());
        dbTag.setCreatedOn(toUpdate.getCreatedOn());
        dbTag.setCategoryUpdatedOn(toUpdate.getCategoryUpdatedOn());
        dbTag.setReplacementTagId(toUpdate.getReplacementTagId());

        dbTag.setUpdatedOn(new Date());
        dbTag = tagRepository.save(dbTag);


        // if change, maintain change
        fireTagUpdatedEvent(beforeChange, dbTag);

        // fire tag changed event
        return dbTag;
    }



    @Override
    public List<TagEntity> getTagList(TagFilterType tagFilterType, List<TagType> tagTypes) {
        // assign_select
        Boolean assignSelect = tagFilterType == TagFilterType.ForSelectAssign ? true : null;
        Boolean searchSelect = tagFilterType == TagFilterType.ForSelectSearch ? true : null;
        return tagRepository.findTagsByCriteria(tagTypes, assignSelect, searchSelect);

    }

    @Override
    public List<TagExtendedEntity> getTagExtendedList(TagFilterType tagFilterType, List<TagType> tagTypes) {
        // assign_select
        Boolean parentsOnly = tagFilterType == TagFilterType.ParentTags ? true : null;
        return tagExtendedRepository.findTagsByCriteria(tagTypes, parentsOnly);

    }


    @Override
    public TagEntity createTag(TagEntity parent, String name) {
        TagEntity tagEntity = new TagEntity();
        tagEntity.setName(name);

        return createTag(parent, tagEntity);
    }

    @Override
    public TagEntity createTag(TagEntity parent, TagEntity newtag) {
        TagEntity parentTag = getParentForNewTag(parent, newtag);
        newtag.setAssignSelect(true);
        newtag.setSearchSelect(false);
        newtag.setToDelete(false);
        newtag.setCreatedOn(new Date());
        TagEntity saved = tagRepository.save(newtag);

        tagStructureService.createRelation(parentTag, saved);


        fireTagAddedEvent(saved);
        return newtag;
    }

    private TagEntity getParentForNewTag(TagEntity parent, TagEntity newtag) {
        if (parent != null) {
            return parent;
        }
        TagType tagType = newtag.getTagType();
        if (tagType == null) {
            tagType = TagType.TagType;
        }
        List<TagEntity> defaults = tagRepository.findTagsByTagTypeAndTagTypeDefault(tagType, true);
        if (defaults != null && !defaults.isEmpty()) {
            return defaults.get(0);
        }
        return null;
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
        //MM TEST THIS
        tag.setReplacementTagId(replacement.getId());
        // update and replace usage
        updateTag(tagId, tag);
        tagReplaceService.replaceTag(tag.getId(), tag.getReplacementTagId());

    }

    public void incrementDishRating(String name, Long dishId, Long ratingId, SortOrMoveDirection moveDirection) {
        // get dish
        DishEntity dish = dishService.getDishForUserById(name, dishId);
        if (dish == null) {
            throw new ActionInvalidException("Can't find dish for id [" + dishId + "]");
        }

        // get current assigned tag for parent rating id
        TagEntity currentTag = tagRepository.getAssignedTagForRating(dishId, ratingId);

        // get new tag (depending upon direction)
        TagEntity nextTag = null;
        if (SortOrMoveDirection.UP.equals(moveDirection)) {
            nextTag = tagRepository.getNextRatingUp(ratingId, currentTag.getId());
        } else {
            nextTag = tagRepository.getNextRatingDown(ratingId, currentTag.getId());
        }
        // assign new tag
        addTagToDish(name, dishId, nextTag.getId());

    }

    @Override
    public List<TagEntity> getTagsForDish(String username, Long dishId) {
        return getTagsForDish(username, dishId, null);
    }

    private Map<DishRatingInfo, Set<RatingInfo>> processDishRatings(List<DishEntity> dishes, Map<Long, RatingInfo> ratingInfoById) {
        Map<DishRatingInfo, Set<RatingInfo>> unorderedInfo = new HashMap<>();
        for (DishEntity dish : dishes) {
            DishRatingInfo dishInfo = new DishRatingInfo(dish.getId(), dish.getDishName());
            if (!unorderedInfo.containsKey(dishInfo)) {
                unorderedInfo.put(dishInfo, new HashSet<>());
            }

            List<TagEntity> tagsForDish = getTagsForDish(dish, Collections.singletonList(TagType.Rating));
            for (TagEntity ratingTag : tagsForDish) {
                if (!ratingInfoById.containsKey(ratingTag.getId())) {
                    continue;
                }

// create the RatingUpdateInfo object
                RatingInfo info = ratingInfoById.get(ratingTag.getId());
                unorderedInfo.get(dishInfo).add(info);
            }

        }
        return unorderedInfo;
    }

    @Override
    public List<TagEntity> getTagsForDish(String username, Long dishId, List<TagType> tagtypes) {
        List<TagEntity> results = new ArrayList<>();
        DishEntity dish = dishService.getDishForUserById(username, dishId);

        if (dish == null) {
            return results;
        }

        return getTagsForDish(dish, tagtypes);
    }

    @Override
    public void addTagToDish(String userName, Long dishId, Long tagId) {
        // get dish
        DishEntity dish = dishService.getDishForUserById(userName, dishId);
        if (dish == null) {
            return;
        }
        // get tag
        TagEntity tag = getTagById(tagId, true);


        List<TagEntity> dishTags = tagRepository.findTagsByDishes(dish);

        // check if tag exists already
        if (dishTags.contains(tag)) {
            return;
        }
        dishTags.add(tag);
        // if rating tag, remove related tags
        if (tag.getTagType().equals(TagType.Rating)) {
            dishTags = removeRelatedTags(dishTags, tag);
        }
        // add tags to dish
        dish.setTags(dishTags);
        dishService.save(dish, false);
        // update statistic
        tagStatisticService.countTagAddedToDish(dish.getUserId(), tagId);
    }


    private List<TagEntity> getTagsForDish(DishEntity dish, List<TagType> tagtypes) {
        List<TagEntity> results = tagRepository.findTagsByDishes(dish);

        if (tagtypes == null) {
            return results;
        }
        return results.stream()
                .filter(t -> tagtypes.contains(t.getTagType()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean assignTagToParent(Long tagId, Long parentId) {
        // get tag and parent
        TagEntity tag = getTagById(tagId);


        TagEntity parentTag = getTagById(parentId);


        return assignTagToParent(tag, parentTag);
    }


    @Override
    public boolean assignTagToParent(TagEntity childTag, TagEntity newParentTag) {
        // get original parent (parent before reassign)
        TagEntity originalParent = tagStructureService.getParentTag(childTag);
        // assign Child tag to parent tag
        TagEntity parentTag = tagStructureService.assignTagToParent(childTag, newParentTag);

        childTag.setUpdatedOn(new Date());
        // fire tag changed event
        fireTagParentChangedEvent(originalParent, parentTag, childTag);

        return true;
    }

    @Override
    public boolean assignChildrenToParent(Long parentId, List<Long> childrenIds) {
        // get parent id
        TagEntity parentTag = getTagById(parentId);

        if (parentTag == null) {
            return false;
        }

        // update tag relation
        for (Long tagId : childrenIds) {
            TagEntity tag = getTagById(tagId);
            assignTagToParent(tag, parentTag);
        }
        return true;
    }

    @Override
    public void addTagsToDish(String userName, Long dishId, Set<Long> tagIds) {
        tagIds.forEach(t -> addTagToDish(userName, dishId, t));
    }

    @Override
    public void removeTagsFromDish(String userName, Long dishId, Set<Long> tagIds) {
        tagIds.forEach(t -> deleteTagFromDish(userName, dishId, t));
    }

    @Override
    public void replaceTagInDishes(String name, Long fromTagId, Long toTagId) {
        UserEntity user = userService.getUserByUserEmail(name);
        List<DishEntity> dishes;
        TagEntity toTag = getTagById(toTagId);

        if (fromTagId.equals(0L)) {
            // this is a request to assign unassigned tags
            TagEntity parent = tagStructureService.getParentTag(toTag);
            List<TagEntity> allChildren = tagStructureService.getChildren(parent);
            List<Long> excludeTags = allChildren.stream().map(TagEntity::getId).collect(Collectors.toList());
            DishSearchCriteria criteria = new DishSearchCriteria(user.getId());
            criteria.setExcludedTagIds(excludeTags);
            dishes = dishSearchService.findDishes(criteria);
        } else {
            DishSearchCriteria criteria = new DishSearchCriteria(user.getId());
            criteria.setIncludedTagIds(Collections.singletonList(fromTagId));
            dishes = dishSearchService.findDishes(criteria);
        }

        // for each dish
        for (DishEntity dish : dishes) {
            List<TagEntity> dishTags = tagRepository.findTagsByDishes(dish);
            dishTags.add(toTag);
            // if rating tag, remove related tags
            if (!fromTagId.equals(0L)) {
                dishTags = dishTags.stream()
                        .filter(t -> !t.getId().equals(fromTagId))
                        .collect(Collectors.toList());
            }
            // add tags to dish
            dish.setTags(dishTags);
            dishService.save(dish, false);
        }
    }

    private List<TagEntity> removeRelatedTags(List<TagEntity> dishTags, TagEntity tag) {
        // get sibling tags for dish
        List<TagEntity> siblings = tagStructureService.getSiblingTags(tag);
        return dishTags.stream()
                .filter(t -> !siblings.contains(t))
                .collect(Collectors.toList());
    }

    private LookupInformation parseRatingTags(List<FatTag> ratingTagsWithChildren) {

        Map<Long, RatingInfo> ratingInfoById = new HashMap<>();
        Set<RatingInfo> headers = new HashSet<>();

        for (FatTag parentTag : ratingTagsWithChildren) {
            // sort children
            List<FatTag> sortedChildren = parentTag.getChildren()
                    .stream()
                    .sorted(Comparator.comparing(FatTag::getPower))
                    .collect(Collectors.toList());
            parentTag.setChildren(sortedChildren);
            int order = 1;
            int maxPower = sortedChildren.size();
            for (FatTag childTag : sortedChildren) {
                RatingInfo childInfo = new RatingInfo(parentTag.getId(), parentTag.getName(), order);
                childInfo.setMaxPower(maxPower);
                ratingInfoById.put(childTag.getId(), childInfo);
                order++;
            }
            // put into header
            if (!sortedChildren.isEmpty()) {
                RatingInfo header = new RatingInfo(parentTag.getId(), parentTag.getName());
                header.setMaxPower(sortedChildren.size());
                headers.add(header);
            }
        }
        return new LookupInformation(ratingInfoById, headers);
    }


    @Override
    public void addTagChangeListener(TagChangeListener tagChangeListener) {
        listeners.add(tagChangeListener);
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

    private void fireTagAddedEvent(TagEntity changed) {
        for (TagChangeListener listener : listeners) {
            listener.onTagAdd(changed);
        }
    }

    private class LookupInformation {
        private Map<Long, RatingInfo> tagToRatingInfo;
        private Set<RatingInfo> parentRatings;

        public LookupInformation(Map<Long, RatingInfo> tagToRatingInfo, Set<RatingInfo> parentRatings) {
            this.tagToRatingInfo = tagToRatingInfo;
            this.parentRatings = parentRatings;
        }

        public Map<Long, RatingInfo> getTagToRatingInfo() {
            return tagToRatingInfo;
        }

        public Set<RatingInfo> getParentRatings() {
            return parentRatings;
        }
    }

    private Long getDefaultTagIdForRating(FatTag ratingParent) {
        if (ratingParent.getChildren() == null || ratingParent.getChildren().isEmpty()) {
            return null;
        }
        // get size of ratings children
        int ratingsSize = ratingParent.getChildren().size();
        // get middle
        Double order = Math.ceil((Double.valueOf(ratingsSize)) / 2.0);
        // get the middle tag
        int index = order.intValue();
        FatTag middle = ratingParent.getChildren().get(index - 1);
        // return the middle tag id

        return middle.getId();
    }

}
