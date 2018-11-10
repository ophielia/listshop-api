package com.meg.atable.lmt.service.tag.impl;

import com.meg.atable.lmt.data.entity.TagEntity;
import com.meg.atable.lmt.service.TagCache;
import com.meg.atable.lmt.service.tag.TagChangeListener;
import com.meg.atable.lmt.service.tag.TagService;
import com.meg.atable.lmt.service.tag.TagStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
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
    public void onTagAdd(TagEntity newTag) {
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
