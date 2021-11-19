package com.meg.listshop.lmt.service.tag;

import com.meg.listshop.lmt.api.model.FatTag;

/**
 * Created by margaretmartin on 15/04/2018.
 */
public interface TagCache {


    FatTag get(Long id);

    void set(FatTag fatTag);

    void clearEntry(Long id);

    void clearCache();
}
