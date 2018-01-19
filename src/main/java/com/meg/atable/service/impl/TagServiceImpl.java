package com.meg.atable.service.impl;

import com.meg.atable.api.model.TagFilterType;
import com.meg.atable.api.model.TagType;
import com.meg.atable.data.entity.DishEntity;
import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.data.entity.TagRelationEntity;
import com.meg.atable.data.entity.TagSearchGroupEntity;
import com.meg.atable.data.repository.DishRepository;
import com.meg.atable.data.repository.TagRelationRepository;
import com.meg.atable.data.repository.TagRepository;
import com.meg.atable.data.repository.TagSearchGroupRepository;
import com.meg.atable.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagRelationRepository tagRelationRepository;


    @Autowired
    private TagSearchGroupRepository tagSearchGroupRepository;

    @Autowired
    private DishRepository dishRepository;


    @Override
    public TagEntity save(TagEntity tag) {
        return tagRepository.save(tag);
    }

    @Override
    public Optional<TagEntity> getTagById(Long tagId) {
        return Optional.ofNullable(tagRepository.findOne(tagId));
    }

    @Override
    public void deleteTagFromDish(Long dishId, Long tagId) {
        if (tagId == null) {
            return;
        }
        // get dish
        DishEntity dish = dishRepository.findOne(dishId);
        if (dish == null) {
            return;
        }
        // filter tag to be deleted from dish
        List<TagEntity> dishTags = tagRepository.findTagsByDishes(dish);
        List<TagEntity> dishTagsDeletedTag = dishTags.stream()
                .filter(t -> t.getId() != tagId)
                .collect(Collectors.toList());
        // add tags to dish
        dish.setTags(dishTagsDeletedTag);
        dishRepository.save(dish);
    }

    @Override
    public Map<Long, TagEntity> getDictionaryForIdList(List<Long> tagIds) {
        List<TagEntity> tags = tagRepository.findAll(tagIds);
       if (!tags.isEmpty()) {
        return  tags.stream().collect(Collectors.toMap(TagEntity::getId,
                        c -> c));

       }
       return new HashMap<Long,TagEntity>();
    }

    @Transactional
    @Override
    public TagEntity updateTag(Long tagId, TagEntity toUpdate) {
        // get tag from db
        TagEntity dbTag = tagRepository.findOne(tagId);
        if (dbTag == null) {
            return null;
        }
        // compare to passed tag
        // determine if change was made to search assign
        boolean searchSelectChanged = toUpdate.getSearchSelect() != dbTag.getSearchSelect();

        // save changes to tag
        dbTag.setName(toUpdate.getName());
        dbTag.setDescription(toUpdate.getDescription());
        dbTag.setAssignSelect(toUpdate.getAssignSelect());
        dbTag.setSearchSelect(toUpdate.getSearchSelect());
        dbTag = tagRepository.save(dbTag);

        // if change, maintain change
        if (searchSelectChanged) {
            maintainTagSystemUponSelectChange(dbTag);
        }

        return dbTag;
    }

    private void maintainTagSystemUponSelectChange(TagEntity updatedTag) {
        List<TagEntity> parentTags = getAscendantTags(updatedTag,true );
        parentTags.add(updatedTag);
        List<TagEntity> childrenTags = getDescendantTags(updatedTag, true);
            childrenTags.add(updatedTag);

        if (!updatedTag.getSearchSelect()) {
            List<Long> groupTagIds = parentTags.stream().map(t -> t.getId()).collect(Collectors.toList());
            List<Long> memberTagIds = childrenTags.stream().map(t -> t.getId()).collect(Collectors.toList());
            // delete tag groups with groups in the parenttag, and children in the children tag
            tagSearchGroupRepository.deleteByGroupAndMember(groupTagIds,memberTagIds);
            return;
        }
        // no change of parent here - just either adding to groups or subtracting from groups
        // we'll need parent tags and direct descendant tags ( that have assign search set to true)

            // this tag is now a search select.
            // add all children tags to a group with this tag as the group
            List<TagSearchGroupEntity> groupAssignments = buildGroupAssignments(updatedTag.getId(),childrenTags);
            // also add all children tags to any parent groups
            for (TagEntity parentTag: parentTags) {
                groupAssignments.addAll(buildGroupAssignments(parentTag.getId(),childrenTags));
            }
            tagSearchGroupRepository.save(groupAssignments);
            return;

    }

    private void maintainTagSystemUponParentChange(TagEntity origParentTag, TagEntity newParentTag, TagEntity childTag) {
        // assignSelect - original Parent
        // if we just removed the last child, the assign select should be set to false
        List<TagEntity> oldParentChildren = getDescendantTags(origParentTag, false);
        if (oldParentChildren == null || oldParentChildren.isEmpty()) {
            // now this is a selectable tag, because it doesn't have children
            origParentTag.setAssignSelect(false);
        }

        // assignSelect - new Parent
        newParentTag.setAssignSelect(true);

        // assignSelect - child - no changes made / necessary

        // searchSelect - only interesting if the childTag is searchselectable
        if (childTag.getSearchSelect()) {
                List<TagEntity> childrenTags = getDescendantTags(childTag, true);
                childrenTags.add(childTag);
            // remove tags (and children) from oldParent
            if (origParentTag.getSearchSelect())  {
                // get parent tags
                List<TagEntity> origParentTags = getAscendantTags(origParentTag,true );
                origParentTags.add(origParentTag);
                List<Long> groupTagIds = origParentTags.stream().map(t -> t.getId()).collect(Collectors.toList());
                List<Long> memberTagIds = childrenTags.stream().map(t -> t.getId()).collect(Collectors.toList());
                // delete tag groups with groups in the parenttag, and children in the children tag
                tagSearchGroupRepository.deleteByGroupAndMember(groupTagIds,memberTagIds);
            }

            // add tags to newParent - if new Parent is SearchSelect
            if (newParentTag.getSearchSelect()) {
                List<TagEntity> newParentTags = getAscendantTags(newParentTag,true );
                newParentTags.add(newParentTag);
                // add all children tags to a group with this tag as the group
                List<TagSearchGroupEntity> groupAssignments = new ArrayList<>();
                // also add all children tags to any parent groups
                for (TagEntity parentTag: newParentTags) {
                    groupAssignments.addAll(buildGroupAssignments(parentTag.getId(),childrenTags));
                }
                tagSearchGroupRepository.save(groupAssignments);
            }
        }


        // save origParentTag, newParentTag, childTag
        tagRepository.save(origParentTag);
        tagRepository.save(newParentTag);
    }

    private List<TagSearchGroupEntity> buildGroupAssignments(Long groupId, List<TagEntity> members) {
        List<TagSearchGroupEntity> newGroupAssignments = new ArrayList<>();
        members.forEach(t -> {
            TagSearchGroupEntity newSearchGroup = new TagSearchGroupEntity(groupId,t.getId());
            newGroupAssignments.add(newSearchGroup);
        });
        return newGroupAssignments;
    }
    @Override
    public List<TagEntity> getTagList(TagFilterType tagFilterType, List<TagType> tagTypes) {

        // skim off base tag requests
        if (tagFilterType != null && TagFilterType.BaseTags.equals(tagFilterType)) {
            return getBaseTagList(tagTypes);
        }
        // this is by selectable tags - assign
        if (tagFilterType != null && TagFilterType.ForSelectAssign.equals(tagFilterType)) {
            return getSelectableTagList(tagTypes);
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


    private List<TagEntity> getBaseTagList(List<TagType> tagTypes) {

        if (tagTypes != null) {
            return tagRelationRepository.findByParentIsNullAndTagTypeIn(tagTypes)
                    .stream()
                    .map(TagRelationEntity::getChild)
                    .collect(Collectors.toList());
        }
        return tagRelationRepository.findByParentIsNull()
                .stream()
                .map(TagRelationEntity::getChild)
                .collect(Collectors.toList());

    }


    private List<TagEntity> getSelectableTagList(List<TagType> tagTypes) {
        if (tagTypes != null) {
            List<String> tagTypeStrings = tagTypes.stream().map(TagType::name).collect(Collectors.toList());
            return tagRepository.findTagsBySearchSelectAndTagTypeIsIn(true,tagTypeStrings);
        } else {
            return tagRepository.findTagsBySearchSelect(true);
        }

    }


    @Override
    @Transactional
    public void deleteAll() {
        List<TagEntity> tags = tagRepository.findAll();

        tagRepository.deleteInBatch(tags);
    }

    @Override
    @Transactional
    public void deleteAllRelationships() {
        tagRelationRepository.deleteAll();
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

        return createTag(parent,tagEntity);
    }

    @Override
    public TagEntity createTag(TagEntity parent, TagEntity newtag) {
        TagEntity parentTag = getParentForNewTag(parent, newtag);
        newtag.setRatingFamily(parentTag.getRatingFamily());
        newtag.setAutoTagFlag(parentTag.getAutoTagFlag());
        newtag.setAssignSelect(true);
        newtag.setSearchSelect(false);
        TagEntity saved = tagRepository.save(newtag);


        TagRelationEntity relation = new TagRelationEntity(parentTag, saved);
        tagRelationRepository.save(relation);
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
        List<TagEntity> results = new ArrayList<>();
        DishEntity dish = dishRepository.findOne(dishId);

        if (dish == null) {
            return results;
        }

        return tagRepository.findTagsByDishes(dish);
    }

    @Override
    public boolean assignTagToParent(Long tagId, Long parentId) {
        // get tag and parent
        Optional<TagEntity> tagOptional = getTagById(tagId);
        TagEntity tag = tagOptional.isPresent() ? tagOptional.get() : null;


        Optional<TagEntity> parentTagOptional = getTagById(parentId);
        TagEntity parentTag = parentTagOptional.isPresent() ? parentTagOptional.get() : null;

        if (tag == null || parentTag == null) {
            return false;
        }

        return assignTagToParent(tag,parentTag);
    }

    private boolean assignTagToParent(TagEntity childTag, TagEntity newParentTag) {
        if (childTag == null || newParentTag == null) {
            return false;
        }
        // check for circular reference
        if (hasCircularReference(newParentTag, childTag)) {
            return false;
        }
        // get tag relation for tag
        TagRelationEntity tagRelation = tagRelationRepository.findByChild(childTag).get();
        if (tagRelation == null) {
            return false;
        }

        TagEntity origParentTag = tagRelation.getParent();

        // replace parent in tag relation
        tagRelation.setParent(newParentTag);
        tagRelation = tagRelationRepository.save(tagRelation);
        if ((childTag.isParentTag()== null || !childTag.isParentTag()) ) {
            // copy tag flag, family from parent
            childTag.setRatingFamily(newParentTag.getRatingFamily());
            childTag.setAutoTagFlag(newParentTag.getAutoTagFlag());
            tagRepository.save(childTag);
        }

        if (tagRelation == null) {
            // some sort of error here
            return false;
        }

        // maintain the tag system
        maintainTagSystemUponParentChange(origParentTag,newParentTag,childTag);
        // return true
        return tagRelation != null;

    }



    @Override
    public boolean assignChildrenToParent(Long parentId, List<Long> childrenIds) {
        // get parent id
        Optional<TagEntity> parentTagOptional = getTagById(parentId);
        TagEntity parentTag = parentTagOptional.isPresent() ? parentTagOptional.get() : null;

        if (parentTag == null) {
            return false;
        }

        // update tag relation
        for (Long tagId : childrenIds) {
            Optional<TagEntity> tagOptional = getTagById(tagId);
            TagEntity tag = tagOptional.isPresent() ? tagOptional.get() : null;
            assignTagToParent(tag, parentTag);
        }
        return true;
    }


    @Override
    public boolean assignTagToTopLevel(Long tagId) {
        // get tag and parent
        Optional<TagEntity> tagOptional = getTagById(tagId);
        TagEntity tag = tagOptional.isPresent() ? tagOptional.get() : null;

        if (tag == null) {
            return false;
        }
        // get tag relation for tag
        TagRelationEntity tagRelation = tagRelationRepository.findByChild(tag).get();
        if (tagRelation == null) {
            return false;
        }
        // replace parent in tag relation
        tagRelation.setParent(null);
        tagRelation = tagRelationRepository.save(tagRelation);
        // return true
        return tagRelation != null;

    }

    @Override
    public List<TagEntity> fillInRelationshipInfo(List<TagEntity> tags) {
        for (TagEntity tag : tags) {
            // find parent for tag
            TagEntity parent = getParentTag(tag);
            // find direct descendants of tag
            List<TagEntity> children = getChildren(tag);
            List<Long> childrenids = children
                    .stream()
                    .map(TagEntity::getId)
                    .collect(Collectors.toList());
            // add info to entity
            tag.setParentId(parent != null ? parent.getId() : null);
            tag.setChildrenIds(childrenids);

        }
        return tags;
    }

    @Override
    public void addTagToDish(Long dishId, Long tagId) {
        // get dish
        DishEntity dish = dishRepository.findOne(dishId);
        // get tag
        TagEntity tag = tagRepository.findOne(tagId);
        List<TagEntity> dishTags = tagRepository.findTagsByDishes(dish);
        dishTags.add(tag);
        // add tags to dish
        dish.setTags(dishTags);
        dishRepository.save(dish);
    }


    private List<TagEntity> retrieveAllTags() {
        // note - this means all tags which have a relationship.  orphans are left out
        List<TagRelationEntity> relations = tagRelationRepository.findAll();
        return relations.stream()
                .map(TagRelationEntity::getChild)
                .collect(Collectors.toList());
    }

    private boolean hasCircularReference(TagEntity parentTag, TagEntity tag) {
        // circular reference exists if ascendants of parentTag include
        // the (new) childTag
        List<TagEntity> grandparents = getAscendantTags(parentTag,false );
        List<Long> exists = grandparents.stream()
                .filter(t -> t.getId() == tag.getId())
                .map(TagEntity::getId)
                .collect(Collectors.toList());
        return !exists.isEmpty();
    }

    private List<TagEntity> getChildren(TagEntity parent) {
        List<TagRelationEntity> childrenrelations = tagRelationRepository.findByParent(parent);
        return childrenrelations
                .stream()
                .map(TagRelationEntity::getChild)
                .collect(Collectors.toList());
    }

    private List<TagEntity> getAscendantTags(TagEntity tag, Boolean searchSelectOnly) {

        // get parent of tag
        Optional<TagRelationEntity> parenttag = tagRelationRepository.findByChild(tag);
        if (parenttag.isPresent() && parenttag.get().getParent() != null) {
            if (searchSelectOnly && parenttag.get().getParent().getSearchSelect()) {
            // if parenttag is not null, add to list, and call for parent tag
            List<TagEntity> nextCall = getAscendantTags(parenttag.get().getParent(), searchSelectOnly);
            nextCall.add(parenttag.get().getParent());
            return nextCall;
            }
        }

        return new ArrayList<>();
    }

    private List<TagEntity> getDescendantTags(TagEntity tag, Boolean searchSelectOnly) {

        // get children of tag
        List<TagRelationEntity> childrentags = tagRelationRepository.findByParent(tag);
        if (childrentags != null && !childrentags.isEmpty()) {
            // if parenttag is not null, add to list, and call for parent tag
            List<TagEntity> nextCall = new ArrayList<>();
            for (TagRelationEntity tagRelation : childrentags) {
                if (searchSelectOnly && !tagRelation.getChild().getSearchSelect()) {
                    continue;
                }
                nextCall.addAll(getDescendantTags(tagRelation.getChild(), searchSelectOnly));
                nextCall.add(tagRelation.getChild());
            }
            return nextCall;
        }

        return new ArrayList<>();
    }

    private TagEntity getParentTag(TagEntity tag) {
        Optional<TagRelationEntity> parentId = tagRelationRepository.findByChild(tag);
        return parentId
                .map(TagRelationEntity::getParent)
                .orElse(null);
    }

    private TagEntity getDefaultGroup(TagType tagType) {
        List<TagEntity> defaults = tagRepository.findTagsByTagTypeAndTagTypeDefault(tagType, true);
        if (defaults != null && !defaults.isEmpty()) {
            return defaults.get(0);
        }
        return null;
    }

}
