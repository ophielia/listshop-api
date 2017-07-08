package com.meg.atable.service.impl;

import com.meg.atable.model.Dish;
import com.meg.atable.model.Tag;
import com.meg.atable.model.TagInfo;
import com.meg.atable.model.TagRelation;
import com.meg.atable.repository.DishRepository;
import com.meg.atable.repository.TagRelationRepository;
import com.meg.atable.repository.TagRepository;
import com.meg.atable.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
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
    private DishRepository dishRepository;



    @Override
    public Tag save(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    public Optional<Tag> getTagById(Long tagId) {
        return Optional.ofNullable(tagRepository.findOne(tagId));
    }

    @Override
    public Collection<Tag> getTagList() {
        return tagRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteAll() {
        List<Tag> tags = tagRepository.findAll();

        tagRepository.deleteInBatch(tags);
    }

    @Override
    @Transactional
    public void deleteAllRelationships() {
        tagRelationRepository.deleteAll();
    }

    @Override
    public Tag createTag(Tag parent, String name) {
        return createTag(parent,name, null);

    }

    @Override
    public Tag createTag(Tag parent, String name, String description) {
        Tag newtag = new Tag(name,description);
        newtag = tagRepository.save(newtag);

        TagRelation relation = new TagRelation(parent,newtag);
        tagRelationRepository.save(relation);
        return newtag;
    }

    @Override
    public TagInfo getTagInfo(Long tagId) {
        Optional<Tag> tagO = getTagById(tagId);
        if (!tagO.isPresent()) {return null;}
        Tag tag = tagO.get();

        return getTagInfo(tag);
    }

    @Override
    public List<Tag> getTagsForDish(Long dishId) {
        List<Tag> results = new ArrayList<>();
        Dish dish = dishRepository.findOne(dishId);

        if (dish == null) {return results;}

        return tagRepository.findTagsByDishes(dish);
    }


    @Override
    public boolean assignTagToParent(Long tagId, Long parentId) {
        // get tag and parent
        Tag tag = getTagById(tagId).get();
        Tag parentTag = getTagById(parentId).get();
        if (tag == null || parentTag == null) {
            return false;
        }
        // check for circular reference
        if (hasCircularReference(parentTag,tag)) {
            return false;
        }
        // get tag relation for tag
        TagRelation tagRelation = tagRelationRepository.findByChild(tag).get();
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
    public List<TagInfo> getTagInfoList(boolean rootOnly) {
        // get the targeted tags (root only, or all tags)
        List<Tag> targetedTags = retrieveAllTags();
        // for each of the targeted tags, get the TagInfo for the tag
        List<TagInfo> tagInfos = new ArrayList<>();
        for (Tag tag: targetedTags) {
            TagInfo info = getTagInfo(tag);
            tagInfos.add(info);
        }
        return tagInfos;
    }

    @Override
    public void addTagToDish(Long dishId, Long tagId) {
        // get dish
        Dish dish = dishRepository.findOne(dishId);
        // get tag
        Tag tag = tagRepository.findOne(tagId);
        List<Tag> dishTags = tagRepository.findTagsByDishes(dish);
        dishTags.add(tag);
        // add tags to dish
        dish.setTags(dishTags);
        dishRepository.save(dish);
    }


    private TagInfo getTagInfo(Tag tag) {
        // get parent
        Tag parent = getParentTag(tag);
        // get siblings
        List<Tag> siblings = getChildren(parent);
        // get children
        List<Tag> children = getChildren(tag);

        // put TagInfo together
        TagInfo tagInfo = new TagInfo(tag);
        if (parent!=null && parent.getId()!=null) {tagInfo.setParentId(parent.getId());} else {
            tagInfo.setParentId(0L);
        }
        tagInfo.setChildrenIds(children.stream()
                .map(Tag::getId)
                .collect(Collectors.toList()));
        tagInfo.setSiblingIds(siblings.stream()
                .filter(t -> t.getId() != tag.getId())
                .map(Tag::getId)
                .collect(Collectors.toList()));

        return tagInfo;
    }

    private List<Tag> retrieveAllTags() {
        // note - this means all tags which have a relationship.  orphans are left out
        List<TagRelation> relations = tagRelationRepository.findAll();
        List<Tag> alltags = relations.stream()
                .map(TagRelation::getChild)
                .collect(Collectors.toList());
        return alltags;
    }

    private boolean hasCircularReference(Tag parentTag, Tag tag) {
        // circular reference exists if ascendants of parentTag include
        // the (new) childTag
        List<Tag> grandparents = getAscendantTags(parentTag);
        List<Long> exists = grandparents.stream()
                .filter(t -> t.getId() == tag.getId())
                .map(Tag::getId)
                .collect(Collectors.toList());
        return !exists.isEmpty();
    }

    private List<Tag> getChildren(Tag parent) {
        List<TagRelation> childrenrelations = tagRelationRepository.findByParent(parent);
        return childrenrelations
                .stream()
                .map(TagRelation::getChild)
                .collect(Collectors.toList());
    }

    private List<Tag> getAscendantTags(Tag tag) {

        // get parent of tag
        Optional<TagRelation> parenttag = tagRelationRepository.findByChild(tag);
        if (parenttag.isPresent() && parenttag.get().getParent()!=null) {
        // if parenttag is not null, add to list, and call for parent tag
            List<Tag> nextCall = getAscendantTags(parenttag.get().getParent());
            nextCall.add(parenttag.get().getParent());
            return nextCall;
        }

        return new ArrayList<>();
    }

    private List<Tag> getDescendantTags(Tag tag) {

        // get children of tag
        List<TagRelation> childrentags = tagRelationRepository.findByParent(tag);
        if (childrentags !=null && !childrentags.isEmpty()) {
            // if parenttag is not null, add to list, and call for parent tag
            List<Tag> nextCall = new ArrayList<>();
            for (TagRelation tagRelation: childrentags) {
                nextCall.addAll(getDescendantTags(tagRelation.getChild()));
                nextCall.add(tagRelation.getChild());
            }
            return nextCall;
        }

        return new ArrayList<>();
    }

    private Tag getParentTag(Tag tag) {
        Optional<TagRelation> parentId = tagRelationRepository.findByChild(tag);
        return parentId
                .map(TagRelation::getParent)
                .orElse(null);
    }

}
