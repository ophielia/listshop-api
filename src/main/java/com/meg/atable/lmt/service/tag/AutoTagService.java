package com.meg.atable.lmt.service.tag;

import com.meg.atable.lmt.data.entity.DishEntity;

/**
 * Created by margaretmartin on 07/12/2017.
 */
public interface AutoTagService {

    void doAutoTag(DishEntity dishEntity, boolean overrideStatus);
}
