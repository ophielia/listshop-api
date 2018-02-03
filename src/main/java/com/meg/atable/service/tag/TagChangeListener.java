package com.meg.atable.service.tag;

import com.meg.atable.api.model.TagFilterType;
import com.meg.atable.api.model.TagType;
import com.meg.atable.data.entity.TagEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by margaretmartin on 13/05/2017.
 */
public interface TagChangeListener {

    void onParentChange(TagEntity oldParent, TagEntity newParent, TagEntity changedTag);

    void onTagUpdate(TagEntity beforeChange, TagEntity changed);
}
