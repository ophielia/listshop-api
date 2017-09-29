package com.meg.atable.api.model;

import com.meg.atable.data.entity.TagEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by margaretmartin on 10/06/2017.
 */
public class TagInfo {

    private List<Long> baseIds;
    private List<Tag> tagList;


    public TagInfo(List<TagEntity> tags) {
        if (tags == null) {
            // shouldn't happen
            return;
        }
        baseIds = new ArrayList<>();
        tagList = new ArrayList<>();
        for (TagEntity tag : tags) {
            if (tag.getParentId() == null || tag.getParentId() == 0L) {
                baseIds.add(tag.getId());
            }
            tagList.add(ModelMapper.toExtendedModel(tag));
        }
    }

    public List<Long> getBaseIds() {
        return baseIds;
    }


    public List<Tag> getTagList() {
        return tagList;
    }


}
