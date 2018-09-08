package com.meg.atable.service.impl;

import com.meg.atable.api.model.FatTag;
import com.meg.atable.service.TagCache;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by margaretmartin on 15/04/2018.
 */
@Service
public class TagCacheImpl implements TagCache {

    Map<Long, TagCacheEntry> cacheMap = new HashMap<>();

    private final static long EXPIRATION_TIME = 5L * 60000L;

    private class TagCacheEntry {
        long createstamp;
        FatTag fatTag;

        public TagCacheEntry(FatTag fatTag) {
            this.fatTag = fatTag;
            this.createstamp = new Date().getTime();
        }
    }

    @Override
    public FatTag get(Long id) {
        if (!cacheMap.containsKey(id)) {
            return null;
        }

        TagCacheEntry cacheEntry = cacheMap.get(id);
        FatTag fatTag = cacheEntry.fatTag;
        removeIfExpired(id, cacheEntry);
        return fatTag;
    }

    private void removeIfExpired(Long id, TagCacheEntry cacheEntry) {
        long nowstamp = new Date().getTime();
        if (nowstamp > (cacheEntry.createstamp + EXPIRATION_TIME)) {
            cacheMap.remove(id);
        }
    }


    @Override
    public void set(FatTag fatTag) {
        Long id = fatTag.getId();
        cacheMap.put(id,new TagCacheEntry(fatTag));
    }

    @Override
    public void clearEntry(Long id) {
        if (cacheMap.containsKey(id)) {
            cacheMap.remove(id);
        }
    }

}
