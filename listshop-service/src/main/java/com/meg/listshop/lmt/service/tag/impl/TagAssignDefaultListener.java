package com.meg.listshop.lmt.service.tag.impl;

import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.service.ListLayoutService;
import com.meg.listshop.lmt.service.tag.TagChangeListener;
import com.meg.listshop.lmt.service.tag.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
public class TagAssignDefaultListener implements TagChangeListener {


    private TagService tagService;
    private ListLayoutService listLayoutService;

    @Autowired
    public TagAssignDefaultListener(TagService tagService, ListLayoutService listLayoutService) {
        this.tagService = tagService;
        this.listLayoutService = listLayoutService;
    }

    @PostConstruct
    public void init() {
        tagService.addTagChangeListener(this);
    }

    @Override
    public void onParentChange(TagEntity origParentTag, TagEntity newParentTag, TagEntity childTag) {
        // not used in this implementation

    }

    @Override
    public void onTagUpdate(TagEntity beforeChange, TagEntity afterChange) {
        // not used in this implementation

    }

    @Override
    public void onTagAdd(TagEntity newTag, TagEntity parentTag) {
        listLayoutService.assignTagToDefaultCategories(newTag);
    }

    @Override
    public void onTagDelete(TagEntity deletedTag) {
        // not used in this implementation
    }

}
