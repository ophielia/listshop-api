package com.meg.atable.lmt.service.tag.impl;

import com.meg.atable.lmt.api.model.TagType;
import com.meg.atable.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.atable.lmt.data.entity.TagEntity;
import com.meg.atable.lmt.service.ListLayoutService;
import com.meg.atable.lmt.service.tag.TagChangeListener;
import com.meg.atable.lmt.service.tag.TagService;
import com.meg.atable.lmt.service.tag.TagStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
public class TagChangeToCategoryListener implements TagChangeListener {


    @Autowired
    private TagService tagService;

    @Autowired
    private TagStructureService tagStructureService;

    @Autowired
    private ListLayoutService listLayoutService;


    @PostConstruct
    public void init() {
        tagService.addTagChangeListener(this);
    }


    @Override
    public void onParentChange(TagEntity origParentTag, TagEntity newParentTag, TagEntity childTag) {
        // for ingredient tags and non-edible tags
        if (!TagType.Ingredient.equals(childTag.getTagType()) &&
                !TagType.NonEdible.equals(childTag.getTagType())) {
            return;
        }

        // if the original parent was not the default, return
        if (origParentTag == null) {
            return;
        }
        boolean origTagDefault = (origParentTag.getTagTypeDefault() == null) ? false : origParentTag.getTagTypeDefault();
        if (!origTagDefault) {
            return;
        }

        // we know that this tag is being assigned out of the default
        // we'll look for siblings in the new parent, then assign the first category we find there
        // to this tag for every available list layout

        // get sibling
        List<TagEntity> siblings = tagStructureService.getDescendantTags(newParentTag, false);
        if (siblings == null) {
            return;
        }
        // filter out child tag, because it could be part of siblings
        siblings = siblings.stream().filter(t -> !t.getId().equals(childTag.getId()))
                .collect(Collectors.toList());
        if (siblings.isEmpty()) {
            return;
        }
        TagEntity sibling = siblings.get(0);
        List<Long> toAdd = new ArrayList<>();
        toAdd.add(childTag.getId());

        // get list layouts
        List<ListLayoutCategoryEntity> tagCategories = listLayoutService.getCategoriesForTag(sibling);
        for (ListLayoutCategoryEntity tagCategory : tagCategories) {
            // assign childTag to this layout
            listLayoutService.addTagsToCategory(tagCategory.getLayoutId(), tagCategory.getId(), toAdd);
        }

    }

    @Override
    public void onTagUpdate(TagEntity beforeChange, TagEntity updatedTag) {
        // no implentation for this listener
    }

    @Override
    public void onTagAdd(TagEntity newTag) {
        // not used in this implementation
    }

    @Override
    public void onTagDelete(TagEntity deletedTag) {
// not used in this implementation
    }
}
