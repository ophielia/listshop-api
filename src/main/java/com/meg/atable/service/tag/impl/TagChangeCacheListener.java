package com.meg.atable.service.tag.impl;

import com.meg.atable.api.model.TagType;
import com.meg.atable.data.entity.ListLayoutCategoryEntity;
import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.service.ListLayoutService;
import com.meg.atable.service.TagCache;
import com.meg.atable.service.tag.TagChangeListener;
import com.meg.atable.service.tag.TagService;
import com.meg.atable.service.tag.TagStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
public class TagChangeCacheListener implements TagChangeListener {


    @Autowired
    private TagService tagService;

    @Autowired
    private TagStructureService tagStructureService;

    @Autowired
    private TagCache tagCache;

    
    @PostConstruct
    public void init() {
        tagService.addTagChangeListener(this);
    }


    @Override
    public void onParentChange(TagEntity origParentTag, TagEntity newParentTag, TagEntity childTag) {

        // MM TODO remove all from cache here
        List<TagEntity> currentAscendants = tagStructureService.getAscendantTags(childTag,false);
        List<TagEntity> origAscendants = tagStructureService.getAscendantTags(childTag,false);

        Set<Long> idsToClear = currentAscendants.stream().map(TagEntity::getId).collect(Collectors.toSet());
        idsToClear.addAll(origAscendants.stream().map(TagEntity::getId).collect(Collectors.toSet()));
        idsToClear.add(childTag.getId());

        for (Long id: idsToClear) {
            tagCache.clearEntry(id);
        }


    }

    @Override
    public void onTagUpdate(TagEntity beforeChange, TagEntity updatedTag) {
        List<TagEntity> currentAscendants = tagStructureService.getAscendantTags(updatedTag,false);

        Set<Long> idsToClear = currentAscendants.stream().map(TagEntity::getId).collect(Collectors.toSet());

        for (Long id: idsToClear) {
            tagCache.clearEntry(id);
        }    }
}
