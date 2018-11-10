package com.meg.atable.lmt.service.tag;

import com.meg.atable.lmt.data.entity.TagEntity;

/**
 * Created by margaretmartin on 13/05/2017.
 */
public interface TagChangeListener {

    void onParentChange(TagEntity oldParent, TagEntity newParent, TagEntity changedTag);

    void onTagUpdate(TagEntity beforeChange, TagEntity changed);

    void onTagAdd(TagEntity newTag);

    void onTagDelete(TagEntity deletedTag);
}
