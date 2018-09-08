package com.meg.atable.service.tag.impl;

import com.meg.atable.api.model.TagFilterType;
import com.meg.atable.api.model.TagType;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.data.repository.UserRepository;
import com.meg.atable.data.entity.DishEntity;
import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.data.repository.DishRepository;
import com.meg.atable.data.repository.TagRepository;
import com.meg.atable.service.DishSearchCriteria;
import com.meg.atable.service.DishSearchService;
import com.meg.atable.service.tag.TagChangeListener;
import com.meg.atable.service.tag.TagService;
import com.meg.atable.service.tag.TagStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
public class TagServiceImpl implements TagService {

    private final List<TagChangeListener> listeners = new CopyOnWriteArrayList<>();
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private TagStructureService tagStructureService;
    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DishSearchService dishSearchService;

    @Override
    public TagEntity save(TagEntity tag) {
        return tagRepository.save(tag);
    }

    @Override
    public TagEntity getTagById(Long tagId) {

        Optional<TagEntity> tagOpt =  tagRepository.findById(tagId);

        return tagOpt.isPresent()?tagOpt.get():null;
    }

    @Override
    public void deleteTagFromDish(Long dishId, Long tagId) {
        if (tagId == null) {
            return;
        }
        // get dish
        Optional<DishEntity> dishOpt = dishRepository.findById(dishId);
        if (!dishOpt.isPresent()) {
            return;
        }
        DishEntity dish = dishOpt.get();
        // filter tag to be deleted from dish
        List<TagEntity> dishTags = tagRepository.findTagsByDishes(dish);
        List<TagEntity> dishTagsDeletedTag = dishTags.stream()
                .filter(t -> !t.getId().equals(tagId))
                .collect(Collectors.toList());
        // add tags to dish
        dish.setTags(dishTagsDeletedTag);
        dishRepository.save(dish);
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

    @Transactional
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
        dbTag = tagRepository.save(dbTag);

        // if change, maintain change
        fireTagUpdatedEvent(beforeChange, dbTag);

        // fire tag changed event
        return dbTag;
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


    @Override
    public List<TagEntity> getTagList(TagFilterType tagFilterType, List<TagType> tagTypes) {

        // skim off base tag requests
        if (tagFilterType != null && TagFilterType.BaseTags.equals(tagFilterType)) {
            return tagStructureService.getBaseTagList(tagTypes);
        }
        // this is by selectable tags - assign
        if (tagFilterType != null && TagFilterType.ForSelectAssign.equals(tagFilterType)) {
            return getAssignSelectableTagList(tagTypes);
        }
        // this is by selectable tags - search
        if (tagFilterType != null && TagFilterType.ForSelectSearch.equals(tagFilterType)) {
            return getSearchSelectableTagList(tagTypes);
        }
        // this is by parent tags
        if (tagFilterType != null && TagFilterType.ParentTags.equals(tagFilterType)) {
            return getParentTagList(tagTypes);
        }
        // get by tag type
        if (tagTypes != null) {
            return tagRepository.findTagsByTagTypeInOrderByName(tagTypes);
        }
        return tagRepository.findAll(new Sort(Sort.Direction.ASC, "name"));
    }


    private List<TagEntity> getParentTagList(List<TagType> tagTypes) {
        if (tagTypes != null) {
            List<String> tagTypeStrings = tagTypes.stream().map(TagType::name).collect(Collectors.toList());
            return tagRepository.findParentTagsByTagTypes(tagTypeStrings);
        } else {
            return tagRepository.findParentTags();
        }
    }


    private List<TagEntity> getSearchSelectableTagList(List<TagType> tagTypes) {
        if (tagTypes == null) {
            return tagRepository.findTagsBySearchSelect(true);
        } else {
            return tagRepository.findTagsBySearchSelectAndTagTypeIsIn(true, tagTypes);
        }

    }


    private List<TagEntity> getAssignSelectableTagList(List<TagType> tagTypes) {
        if (tagTypes == null) {
            return tagRepository.findTagsByAssignSelect(true);
        } else {
            return tagRepository.findTagsByAssignSelectAndTagTypeIsIn(true, tagTypes);
        }

    }


    @Override
    public TagEntity createTag(TagEntity parent, String name) {
        return createTag(parent, name, null);

    }

    @Override
    public TagEntity createTag(TagEntity parent, String name, String description) {
        TagEntity tagEntity = new TagEntity();
        tagEntity.setName(name);
        tagEntity.setDescription(description);

        return createTag(parent, tagEntity);
    }

    @Override
    public TagEntity createTag(TagEntity parent, TagEntity newtag) {
        TagEntity parentTag = getParentForNewTag(parent, newtag);
        newtag.setAssignSelect(true);
        newtag.setSearchSelect(false);
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
    public List<TagEntity> getTagsForDish(Long dishId) {
        return getTagsForDish(dishId, null);
    }

    @Override
    public List<TagEntity> getTagsForDish(Long dishId, List<TagType> tagtypes) {
        List<TagEntity> results = new ArrayList<>();
        Optional<DishEntity> dishOpt = dishRepository.findById(dishId);

        if (!dishOpt.isPresent()) {
            return results;
        }

        DishEntity dish = dishOpt.get();
        results = tagRepository.findTagsByDishes(dish);

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

    private TagEntity getDefaultGroup(TagType tagType) {
        List<TagEntity> defaults = tagRepository.findTagsByTagTypeAndTagTypeDefault(tagType, true);
        if (defaults != null && !defaults.isEmpty()) {
            return defaults.get(0);
        }
        return null;
    }


    @Override
    public void addTagToDish(Long dishId, Long tagId) {
        // get dish
        Optional<DishEntity> dishOpt = dishRepository.findById(dishId);
        if (!dishOpt.isPresent()) {
            return;
        }
        // get tag
        Optional<TagEntity> tagOpt = tagRepository.findById(tagId);
        if (!tagOpt.isPresent()) {
            return;
        }
        DishEntity dish = dishOpt.get();
        TagEntity tag = tagOpt.get();

        List<TagEntity> dishTags = tagRepository.findTagsByDishes(dish);
        dishTags.add(tag);
        // if rating tag, remove related tags
        if (tag.getTagType().equals(TagType.Rating)) {
            dishTags = removeRelatedTags(dishTags, tag);
        }
        // add tags to dish
        dish.setTags(dishTags);
        dishRepository.save(dish);
    }

    @Override
    public void addTagsToDish(Long dishId, Set<Long> tagIds) {
        tagIds.forEach(t -> addTagToDish(dishId, t));
    }

    @Override
    public void removeTagsFromDish(Long dishId, Set<Long> tagIds) {
        tagIds.forEach(t -> deleteTagFromDish(dishId, t));
    }

    private List<TagEntity> removeRelatedTags(List<TagEntity> dishTags, TagEntity tag) {
        // get sibling tags for dish
        List<TagEntity> siblings = tagStructureService.getSiblingTags(tag);
        return dishTags.stream()
                .filter(t -> !siblings.contains(t))
                .collect(Collectors.toList());
    }

    @Override
    public void replaceTagInDishes(String name, Long fromTagId, Long toTagId) {
        UserAccountEntity user = userRepository.findByUsername(name);
        List<DishEntity> dishes = null;
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
            dishRepository.save(dish);
        }
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

}
