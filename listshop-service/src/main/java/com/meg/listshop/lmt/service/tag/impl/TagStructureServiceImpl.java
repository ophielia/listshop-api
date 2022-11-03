package com.meg.listshop.lmt.service.tag.impl;

import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.entity.TagRelationEntity;
import com.meg.listshop.lmt.data.repository.TagRelationRepository;
import com.meg.listshop.lmt.data.repository.TagRepository;
import com.meg.listshop.lmt.service.tag.TagStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
public class TagStructureServiceImpl implements TagStructureService {

    private final TagRelationRepository tagRelationRepository;

    private final TagRepository tagRepository;

    @Autowired
    public TagStructureServiceImpl(
            TagRepository tagRepository,
            TagRelationRepository tagRelationRepository
    ) {
        this.tagRelationRepository = tagRelationRepository;
        this.tagRepository = tagRepository;
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
    public List<TagEntity> getAscendantTags(TagEntity tag) {
        if (tag == null) {
            return new ArrayList<>();
        }

        // get children of tag
        List<Long> tagIds = tagRelationRepository.getTagWithAscendants(tag.getId());
        return tagRepository.findAllById(tagIds);
    }

    @Override
    public List<TagEntity> getDescendantTags(TagEntity tag) {
        if (tag == null) {
            return new ArrayList<>();
        }

        // get children of tag
        List<Long> tagIds = tagRelationRepository.getTagWithDescendants(tag.getId());
        return tagRepository.findAllById(tagIds);
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
        List<TagEntity> grandparents = getAscendantTags(parentTag);
        List<Long> exists = grandparents.stream()
                .filter(t -> t.getId() == tag.getId())
                .map(TagEntity::getId)
                .collect(Collectors.toList());
        return !exists.isEmpty();
    }

    public Set<Long> getLegacyDescendantsTagIds(Set<Long> tagIdSet) {
        Map<Long, List<Long>> descendantMap = getDescendantTagIds(tagIdSet, null);
        return descendantMap.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
    }

    public Set<Long> getDescendantTagIds(Long tagId) {
        List<Long> tagAndDescendants = tagRelationRepository.getTagWithDescendants(tagId);
        if (tagAndDescendants == null) {
            return new HashSet<>();
        }
        return new HashSet<>(tagAndDescendants);
    }

    public List<TagEntity> getDescendantTags(Long tagId) {
        if (tagId == null) {
            return new ArrayList<>();
        }

        // get children of tag
        List<Long> tagIds = tagRelationRepository.getTagWithDescendants(tagId);
        return tagRepository.findAllById(tagIds);
    }


    public Map<Long, List<Long>> getDescendantTagIds(Set<Long> tagIds, Long userId) {
        return tagRelationRepository.getDescendantMap(tagIds, userId);
    }

    @Override
    public Map<Long, List<Long>> getSearchGroupsForTagIds(Set<Long> allTags, Long userId) {
        HashMap<Long, List<Long>> results = new HashMap<>();
        for (Long tagId : allTags) {
            Set<Long> descendantIds = getDescendantTagIds(tagId);
            results.put(tagId, new ArrayList<>(descendantIds));
        }
        return results;
    }

    public Map<Long, List<Long>> getRatingsWithSiblingsByPower(List<Long> filterTagIds, boolean isExclude, Long userId) {
        return tagRelationRepository.getRatingsWithSiblingsByPower(filterTagIds, isExclude, userId);
    }

}
