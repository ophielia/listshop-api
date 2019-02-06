package com.meg.atable.lmt.service;

import com.meg.atable.lmt.api.model.FatTag;

/**
 * Created by margaretmartin on 15/04/2018.
 */
public interface TagCache {


    FatTag get(Long id);

    void set(FatTag fatTag);

    void clearEntry(Long id);

    void clearCache();
}
