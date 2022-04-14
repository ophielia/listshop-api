package com.meg.listshop.lmt.service.tag.impl;

import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.service.tag.TagCache;
import com.meg.listshop.lmt.service.tag.TagChangeListener;
import com.meg.listshop.lmt.service.tag.TagService;
import com.meg.listshop.lmt.service.tag.TagStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Deprecated
@Service
public class TagChangeCacheListener implements TagChangeListener {

    private TagService tagService;

    private TagStructureService tagStructureService;

    private TagCache tagCache;

    @Autowired
    public TagChangeCacheListener(TagService tagService, TagStructureService tagStructureService, TagCache tagCache) {
        this.tagService = tagService;
        this.tagStructureService = tagStructureService;
        this.tagCache = tagCache;
    }

    @PostConstruct
    public void init() {

        //tagService.addTagChangeListener(this);
    }


    @Override
    public void onParentChange(TagEntity origParentTag, TagEntity newParentTag, TagEntity childTag) {
        removeCacheForTag(childTag);
        if (origParentTag != null) {
            removeCacheForTag(origParentTag);
        }
    }

    @Override
    public void onTagUpdate(TagEntity beforeChange, TagEntity updatedTag) {
        removeCacheForTag(updatedTag);
    }

    @Override
    public void onTagAdd(TagEntity newTag, TagEntity parentTag) {
        removeCacheForTag(newTag);
    }

    @Override
    public void onTagDelete(TagEntity deletedTag) {
        removeCacheForTag(deletedTag);
    }

    private void removeCacheForTag(TagEntity tag) {
        List<TagEntity> currentAscendants = tagStructureService.getAscendantTags(tag, false);

        Set<Long> idsToClear = currentAscendants.stream().map(TagEntity::getId).collect(Collectors.toSet());
        idsToClear.add(tag.getId());
        for (Long id : idsToClear) {
            tagCache.clearEntry(id);
        }
    }
}
