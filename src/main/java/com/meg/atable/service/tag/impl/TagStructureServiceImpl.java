package com.meg.atable.service.tag.impl;

import com.meg.atable.api.model.FatTag;
import com.meg.atable.api.model.TagType;
import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.data.entity.TagRelationEntity;
import com.meg.atable.data.entity.TagSearchGroupEntity;
import com.meg.atable.data.repository.TagRelationRepository;
import com.meg.atable.data.repository.TagRepository;
import com.meg.atable.data.repository.TagSearchGroupRepository;
import com.meg.atable.service.TagCache;
import com.meg.atable.service.tag.TagStructureService;
import com.meg.atable.service.tag.TagSwapout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
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

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    TagCache tagCache;


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
        Optional<TagRelationEntity> tagRelationOpt = tagRelationRepository.findByChild(childTag);
        TagRelationEntity tagRelation = new TagRelationEntity();
        if (tagRelationOpt.isPresent()) {
            // no parent yet
            tagRelation = tagRelationOpt.get();
        } else {
            tagRelation.setChild(childTag);
        }

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

    public List<TagEntity> getSiblingTags(TagEntity tag) {
        // get parent
        TagEntity parent = getParentTag(tag);
        // get children for parent
        List<TagEntity> children = getChildren(parent);
        if (children == null) {
            return new ArrayList<>();
        }
        // strip passed tag (can't be a sibling to itself)
        // and return results
        return children.stream()
                .filter(t -> !t.getId().equals(tag.getId()))
                .collect(Collectors.toList());

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
        List<TagEntity> grandparents = getAscendantTags(parentTag, false);
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

    @Override
    public List<TagSearchGroupEntity> buildGroupAssignments(Long groupId, List<TagEntity> members) {
        List<TagSearchGroupEntity> newGroupAssignments = new ArrayList<>();
        members.forEach(t -> {
            TagSearchGroupEntity newSearchGroup = new TagSearchGroupEntity(groupId, t.getId());
            newGroupAssignments.add(newSearchGroup);
        });
        return newGroupAssignments;
    }

    @Override
    public void createGroupsForMember(Long memberId, List<Long> groupsToAdd) {
        List<TagSearchGroupEntity> newGroups = new ArrayList<>();
        groupsToAdd.forEach(i -> {
            TagSearchGroupEntity newSearchGroup = new TagSearchGroupEntity(i, memberId);
            newGroups.add(newSearchGroup);
        });
        tagSearchGroupRepository.save(newGroups);
    }

    @Transactional
    @Override
    public void removeGroupsForMember(Long memberid, List<Long> groupIds) {
        deleteTagGroupsByGroupAndMember(groupIds, Collections.singletonList(memberid));
    }

    @Override
    public void createMembersForGroup(Long groupId, List<Long> membersToAdd) {
        List<TagSearchGroupEntity> newGroups = new ArrayList<>();
        membersToAdd.forEach(i -> {
            TagSearchGroupEntity newMemberForGroup = new TagSearchGroupEntity(groupId, i);
            newGroups.add(newMemberForGroup);
        });
        tagSearchGroupRepository.save(newGroups);
    }

    @Override
    public void removeMembersForGroup(Long groupId, List<Long> membersToDelete) {
        deleteTagGroupsByGroupAndMember(Collections.singletonList(groupId), membersToDelete);
    }

    @Override
    public List<FatTag> getTagsWithChildren(List<TagType> tagTypes) {
        // get relationship lookups
        Map<Long, List<Long>> parentToChildren = getTagRelationshipLookup(tagTypes);

        // get base tags
        List<Long> baseTagIds = parentToChildren.containsKey(0L) ? parentToChildren.get(0L) : new ArrayList<>();

        // fill in starting with base tags
        List<FatTag> filledIn = new ArrayList<>();
        for (Long tagId : baseTagIds) {
            FatTag fatTag = fillInTag(tagId, parentToChildren);
            filledIn.add(fatTag);
        }
        return filledIn;
    }

    private Map<Long, List<Long>> getTagRelationshipLookup(List<TagType> tagTypes) {
        List<Object[]> rawRelations = new ArrayList<>();
        if (tagTypes == null) {
            rawRelations = tagRelationRepository.getAllTagRelationships();
        } else {
            rawRelations = tagRelationRepository.getTagRelationshipsForTagType(tagTypes.stream().map(tt -> tt.name()).collect(Collectors.toList()));
        }

        Map<Long, List<Long>> parentToChildren = new HashMap<>();
        for (Object[] result : rawRelations) {
            Long parentId = ((BigInteger) result[0]) == null? 0L:((BigInteger) result[0]).longValue();
            Long childId = ((BigInteger) result[1]).longValue();
            if (!parentToChildren.containsKey(parentId)) {
                parentToChildren.put(parentId, new ArrayList<Long>());
            }
            List children = parentToChildren.get(parentId);
            children.add(childId);
            parentToChildren.put(parentId, children);
        }

        return parentToChildren;
    }

    private FatTag fillInTag(Long tagId, Map<Long, List<Long>> parentToChildren)  {
        // check for tag in cache
        FatTag fatTag = tagCache.get(tagId);
        if (fatTag != null) {
            return fatTag;
        }

        // create new FatTag with passed tag
        TagEntity tag = tagRepository.findOne(tagId);
        fatTag = new FatTag(tag);
        // process children

        List<FatTag> filledChildren = new ArrayList<>();
        if (parentToChildren.containsKey(fatTag.getId())) {
            List<Long> childrenIds = parentToChildren.get(fatTag.getId());
            for (Long childId : childrenIds) {
                // get tag
                TagEntity child = tagRepository.findOne(childId);
                // fill tag
                FatTag fatChild = fillInTag(childId, parentToChildren);
                // set parent in tag
                fatChild.setParentId(fatTag.getId());
                // add to list
                filledChildren.add(fatChild);
            }
        }

        // insert children in FatTag
        fatTag.addChildren(filledChildren);

        // save in cache
        tagCache.set(fatTag);
        // return FatTag
        return fatTag;
    }

    @Transactional
    @Override
    public void deleteTagGroupsByGroupAndMember(List<Long> groupTagIds, List<Long> memberTagIds) {
        tagSearchGroupRepository.deleteByGroupAndMember(groupTagIds, memberTagIds);
    }


    @Override
    public HashMap<Long, List<Long>> getSearchGroupsForTagIds(Set<Long> allTags) {
        List<TagSearchGroupEntity> rawSearchGroups = tagSearchGroupRepository.findByGroupIdIn(allTags);

// put the results in a HashMap
        HashMap<Long, List<Long>> results = new HashMap<>();
        for (TagSearchGroupEntity result : rawSearchGroups) {
            if (!results.containsKey(result.getGroupId())) {
                results.put(result.getGroupId(), new ArrayList<>());
            }
            results.get(result.getGroupId()).add(result.getMemberId());
        }
        return results;
    }

    @Override
    public List<Long> getSearchGroupIdsByMember(Long memberid) {
        List<TagSearchGroupEntity> groups = tagSearchGroupRepository.findByMemberId(memberid);
        return groups.stream().map(TagSearchGroupEntity::getGroupId).collect(Collectors.toList());
    }

    @Override
    public List<Long> getSearchMemberIdsByGroup(Long groupId) {
        List<TagSearchGroupEntity> members = tagSearchGroupRepository.findByGroupId(groupId);
        return members.stream().map(TagSearchGroupEntity::getMemberId).collect(Collectors.toList());
    }

    @Override
    public HashMap<Long, TagSwapout> getTagSwapouts(List<Long> dishIds, List<String> tagListForSlot) {

        List<Long> tagIdsAsLongs = tagListForSlot.stream().map(t -> Long.valueOf(t)).collect(Collectors.toList());
        List<Object[]> rawResults = tagSearchGroupRepository.getTagSwapoutsByDishesAndGroups(dishIds, tagIdsAsLongs);
        HashMap<Long, TagSwapout> resultMap = new HashMap<Long, TagSwapout>();
        for (Object[] result : rawResults) {
            Long dishId = ((BigInteger) result[0]).longValue();
            String searchTag = ((BigInteger) result[1]).toString();
            String foundTag = ((BigInteger) result[2]).toString();
            if (!resultMap.containsKey(dishId)) {
                resultMap.put(dishId, new TagSwapout());
            }
            TagSwapout swapoutsForDish = resultMap.get(dishId);
            swapoutsForDish.addSearchFound(searchTag, foundTag);
            resultMap.put(dishId, swapoutsForDish);
        }

        return resultMap;
    }

}
