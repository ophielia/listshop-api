package com.meg.atable.service.tag.impl;

import com.meg.atable.api.model.TagType;
import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.data.entity.TagRelationEntity;
import com.meg.atable.data.entity.TagSearchGroupEntity;
import com.meg.atable.data.repository.TagRelationRepository;
import com.meg.atable.data.repository.TagRepository;
import com.meg.atable.data.repository.TagSearchGroupRepository;
import com.meg.atable.service.tag.TagStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
public class TagStructureServiceImpl implements TagStructureService {

@Autowired
private TagSearchGroupRepository tagSearchGroupRepository;

    @Autowired
    private TagRelationRepository tagRelationRepository;

    @Override
    public TagRelationEntity createRelation(TagEntity parentTag, TagEntity childTag) {
        TagRelationEntity relation = new TagRelationEntity(parentTag, childTag);
        return tagRelationRepository.save(relation);
    }

    @Override
    public TagEntity assignTagToParent(TagEntity childTag, TagEntity newParentTag) {
        if (childTag == null || newParentTag == null) {
            return null;
        }
        // check for circular reference
        if (hasCircularReference(newParentTag, childTag)) {
            return null;
        }
        // get tag relation for tag
        TagRelationEntity tagRelation = tagRelationRepository.findByChild(childTag).get();
        if (tagRelation == null) {
            return null;
        }


        // replace parent in tag relation
        tagRelation.setParent(newParentTag);
        tagRelation = tagRelationRepository.save(tagRelation);


        if (tagRelation == null) {
            // some sort of error here
            return null;
        }

        return tagRelation.getParent();
        /*
        maintainTagSystemUponParentChange(origParentTag,newParentTag,childTag);
        // return true
        return tagRelation != null;
        */

    }

    @Override
    public List<TagEntity> getChildren(TagEntity parent) {
        List<TagRelationEntity> childrenrelations = tagRelationRepository.findByParent(parent);
        return childrenrelations
                .stream()
                .map(TagRelationEntity::getChild)
                .collect(Collectors.toList());
    }

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
    public boolean assignTagToTopLevel(TagEntity tag) {

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
    public List<TagEntity> getAscendantTags(TagEntity tag, Boolean searchSelectOnly) {

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

    @Override
    public List<TagEntity> getDescendantTags(TagEntity tag, Boolean searchSelectOnly) {

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

    @Override
    public TagEntity getParentTag(TagEntity tag) {
        Optional<TagRelationEntity> parentId = tagRelationRepository.findByChild(tag);
        return parentId
                .map(TagRelationEntity::getParent)
                .orElse(null);
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

    public List<TagEntity> getBaseTagList(List<TagType> tagTypes) {

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


    private void maintainTagSystemUponSelectChange(TagEntity updatedTag) {
      /*  List<TagEntity> parentTags = tagStructureService.getAscendantTags(updatedTag,true );
        parentTags.add(updatedTag);
        List<TagEntity> childrenTags = tagStructureService.getDescendantTags(updatedTag, true);
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
        return;*/

    }

    private void maintainTagSystemUponParentChange(TagEntity origParentTag, TagEntity newParentTag, TagEntity childTag) {
        /*
        // assignSelect - original Parent
        // if we just removed the last child, the assign select should be set to false
        List<TagEntity> oldParentChildren = tagStructureService.getDescendantTags(origParentTag, false);
        if (oldParentChildren == null || oldParentChildren.isEmpty()) {
            // now this is a selectable tag, because it doesn't have children
            origParentTag.setAssignSelect(false);
        }

        // assignSelect - new Parent
        newParentTag.setAssignSelect(true);

        // assignSelect - child - no changes made / necessary

        // searchSelect - only interesting if the childTag is searchselectable
        if (childTag.getSearchSelect()) {
            List<TagEntity> childrenTags = tagStructureService.getDescendantTags(childTag, true);
            childrenTags.add(childTag);
            // remove tags (and children) from oldParent
            if (origParentTag.getSearchSelect())  {
                // get parent tags
                List<TagEntity> origParentTags = tagStructureService.getAscendantTags(origParentTag,true );
                origParentTags.add(origParentTag);
                List<Long> groupTagIds = origParentTags.stream().map(t -> t.getId()).collect(Collectors.toList());
                List<Long> memberTagIds = childrenTags.stream().map(t -> t.getId()).collect(Collectors.toList());
                // delete tag groups with groups in the parenttag, and children in the children tag
                tagSearchGroupRepository.deleteByGroupAndMember(groupTagIds,memberTagIds);
            }

            // add tags to newParent - if new Parent is SearchSelect
            if (newParentTag.getSearchSelect()) {
                List<TagEntity> newParentTags = tagStructureService.getAscendantTags(newParentTag,true );
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
        */
    }

    @Override
    public List<TagSearchGroupEntity> buildGroupAssignments(Long groupId, List<TagEntity> members) {
        List<TagSearchGroupEntity> newGroupAssignments = new ArrayList<>();
        members.forEach(t -> {
            TagSearchGroupEntity newSearchGroup = new TagSearchGroupEntity(groupId,t.getId());
            newGroupAssignments.add(newSearchGroup);
        });
        return newGroupAssignments;
    }

    @Transactional
    @Override
    public void deleteTagGroupsByGroupAndMember(List<Long> groupTagIds, List<Long> memberTagIds) {
        tagSearchGroupRepository.deleteByGroupAndMember(groupTagIds,memberTagIds);
    }

}
