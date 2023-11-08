package com.meg.listshop.lmt.service.tag.impl;

import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.service.LayoutService;
import com.meg.listshop.lmt.service.tag.TagChangeListener;
import com.meg.listshop.lmt.service.tag.TagService;
import com.meg.listshop.lmt.service.tag.TagStructureService;
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
    private LayoutService layoutService;

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
        if (origParentTag == null || origParentTag.getTagTypeDefault() == null) {
            return;
        }

        if (!origParentTag.getTagTypeDefault() == true) {
            return;
        }

        // we know that this tag is being assigned out of the default
        // we'll look for siblings in the new parent, then assign the first category we find there
        // to this tag for every available list layout
        assignFromSibling(newParentTag, childTag);


    }


    @Override
    public void onTagUpdate(TagEntity beforeChange, TagEntity updatedTag) {
        // no implentation for this listener
    }

    @Override
    public void onTagAdd(TagEntity newTag, TagEntity parentTag) {
        if (newTag.getIsGroup()) {
            return;
        }
        assignFromSibling(parentTag, newTag);
    }

    @Override
    public void onTagDelete(TagEntity deletedTag) {
// not used in this implementation
    }

    @Override
    public void onTagCopy(TagEntity copiedTag, Long categoryId) {
        if (categoryId == null) {
            layoutService.assignDefaultCategoryToTag(new ArrayList<>(), copiedTag);
            return;
        }
        layoutService.addTagToCategory(categoryId, copiedTag);
    }


    private void assignFromSibling(TagEntity newParentTag, TagEntity childTag) {
        // get sibling
        List<TagEntity> siblings = tagStructureService.getDescendantTags(newParentTag);
        if (siblings != null) {
            // filter out child tag, because it could be part of siblings
            siblings = siblings.stream().filter(t -> (!t.getId().equals(childTag.getId())
                            && !t.getId().equals(newParentTag.getId())))
                    .collect(Collectors.toList());
        }

        layoutService.assignDefaultCategoryToTag(siblings, childTag);
        layoutService.assignUserDefaultCategoriesToTag(siblings, childTag);

    }
}
