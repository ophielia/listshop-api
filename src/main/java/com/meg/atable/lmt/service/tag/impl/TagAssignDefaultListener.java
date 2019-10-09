package com.meg.atable.lmt.service.tag.impl;

import com.meg.atable.lmt.data.entity.TagEntity;
import com.meg.atable.lmt.service.ListLayoutService;
import com.meg.atable.lmt.service.tag.TagChangeListener;
import com.meg.atable.lmt.service.tag.TagService;
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


    @PostConstruct
    public void init() {
        tagService.addTagChangeListener(this);
    }

    @Autowired
    public void setTagService(TagService tagService) {
        this.tagService = tagService;
    }

    @Autowired
    public void setListLayoutService(ListLayoutService listLayoutService) {
        this.listLayoutService = listLayoutService;
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
    public void onTagAdd(TagEntity newTag) {
        listLayoutService.assignTagToDefaultCategories(newTag);
    }

    @Override
    public void onTagDelete(TagEntity deletedTag) {
        // not used in this implementation
    }

}
