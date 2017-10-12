package com.meg.atable.service.impl;

import com.meg.atable.api.model.TagFilterType;
import com.meg.atable.api.model.TagType;
import com.meg.atable.data.entity.DishEntity;
import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.data.entity.TagRelationEntity;
import com.meg.atable.data.repository.DishRepository;
import com.meg.atable.data.repository.TagRelationRepository;
import com.meg.atable.data.repository.TagRepository;
import com.meg.atable.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    public List<TagEntity> getTagList() {
        return getTagList(TagFilterType.All, null);
    }


    @Override
    public List<TagEntity> getTagList(TagFilterType tagFilterType, TagType tagType) {

        // skim off base tag requests
        if (tagFilterType != null && TagFilterType.BaseTags.equals(tagFilterType)) {
            return getBaseTagList(tagType);
        }
        // get by tag type
        if (tagType != null) {
            return tagRepository.findTagsByTagTypeOrderByName(tagType);
        }
        return tagRepository.findAll(new Sort(Sort.Direction.ASC,"name"));
    }

    private List<TagEntity> getBaseTagList(TagType tagType) {

        if (tagType != null) {
            return  tagRelationRepository.findByParentIsNullAndTagType(tagType)
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
    public List<TagEntity> getTagList(TagType tagTypeFilter) {
        return getTagList(TagFilterType.All,tagTypeFilter);
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
        TagEntity newtag = new TagEntity(name, description);
        newtag = tagRepository.save(newtag);

        TagRelationEntity relation = new TagRelationEntity(parent, newtag);
        tagRelationRepository.save(relation);
        return newtag;
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
        TagEntity tag = tagOptional.isPresent()?tagOptional.get():null;


        Optional<TagEntity> parentTagOptional = getTagById(parentId);
        TagEntity parentTag = parentTagOptional.isPresent()?parentTagOptional.get():null;

        if (tag == null || parentTag == null) {
            return false;
        }
        // check for circular reference
        if (hasCircularReference(parentTag, tag)) {
            return false;
        }
        // get tag relation for tag
        TagRelationEntity tagRelation = tagRelationRepository.findByChild(tag).get();
        if (tagRelation == null) {
            return false;
        }
        // replace parent in tag relation
        tagRelation.setParent(parentTag);
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
        List<TagEntity> grandparents = getAscendantTags(parentTag);
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

    private List<TagEntity> getAscendantTags(TagEntity tag) {

        // get parent of tag
        Optional<TagRelationEntity> parenttag = tagRelationRepository.findByChild(tag);
        if (parenttag.isPresent() && parenttag.get().getParent() != null) {
            // if parenttag is not null, add to list, and call for parent tag
            List<TagEntity> nextCall = getAscendantTags(parenttag.get().getParent());
            nextCall.add(parenttag.get().getParent());
            return nextCall;
        }

        return new ArrayList<>();
    }

    private List<TagEntity> getDescendantTags(TagEntity tag) {

        // get children of tag
        List<TagRelationEntity> childrentags = tagRelationRepository.findByParent(tag);
        if (childrentags != null && !childrentags.isEmpty()) {
            // if parenttag is not null, add to list, and call for parent tag
            List<TagEntity> nextCall = new ArrayList<>();
            for (TagRelationEntity tagRelation : childrentags) {
                nextCall.addAll(getDescendantTags(tagRelation.getChild()));
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

}
