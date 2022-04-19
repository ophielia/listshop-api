package com.meg.listshop.lmt.service.tag.impl;

import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.entity.TagRelationEntity;
import com.meg.listshop.lmt.data.entity.TagSearchGroupEntity;
import com.meg.listshop.lmt.data.repository.TagRelationRepository;
import com.meg.listshop.lmt.data.repository.TagSearchGroupRepository;
import com.meg.listshop.lmt.service.tag.TagStructureService;
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

    //MM replace TagSearchGroupEntity with recursive query use
//MM tag work
    private final TagSearchGroupRepository tagSearchGroupRepository;

    private final TagRelationRepository tagRelationRepository;

    @Autowired
    public TagStructureServiceImpl(
            TagSearchGroupRepository tagSearchGroupRepository,
            TagRelationRepository tagRelationRepository
    ) {
        this.tagSearchGroupRepository = tagSearchGroupRepository;
        this.tagRelationRepository = tagRelationRepository;
    }

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
        // TODO replace this with call to method which throws exception
        // get tag relation for tag
        Optional<TagRelationEntity> tagRelationOpt = tagRelationRepository.findByChild(tag);
        if (!tagRelationOpt.isPresent()) {
            return false;
        }
        TagRelationEntity tagRelation = tagRelationOpt.get();
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
            boolean stopForSearch = searchSelectOnly && (parenttag.get().getParent().getSearchSelect());
            if (!stopForSearch) {
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
        if (parentTag.getId().equals(tag.getId())) {
            return true;
        }
        // circular reference exists if ascendants of parentTag include
        // the (new) childTag
        List<TagEntity> grandparents = getAscendantTags(parentTag, false);
        List<Long> exists = grandparents.stream()
                .filter(t -> t.getId() == tag.getId())
                .map(TagEntity::getId)
                .collect(Collectors.toList());
        return !exists.isEmpty();
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
        tagSearchGroupRepository.saveAll(newGroups);
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
        tagSearchGroupRepository.saveAll(newGroups);
    }

    @Override
    public void removeMembersForGroup(Long groupId, List<Long> membersToDelete) {
        deleteTagGroupsByGroupAndMember(Collections.singletonList(groupId), membersToDelete);
    }

    public Set<Long> getDescendantsOfTag(Long tagId) {
        List<Long> tagAndDescendants = tagRelationRepository.getTagWithDescendants(tagId);
        if (tagAndDescendants == null) {
            return new HashSet<>();
        }
        return new HashSet<>(tagAndDescendants);
    }

    @Transactional
    @Override
    public void deleteTagGroupsByGroupAndMember(List<Long> groupTagIds, List<Long> memberTagIds) {
        tagSearchGroupRepository.deleteByGroupAndMember(groupTagIds, memberTagIds);
    }


    @Override
    public Map<Long, List<Long>> getSearchGroupsForTagIds(Set<Long> allTags) {
        // MM replace with recursive query here
        // recursive query in tag repository
        // will need integration test to test hit on db - in com.meg.listshop.lmt.api.DishRestControllerTest.testFindDishes
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


}
