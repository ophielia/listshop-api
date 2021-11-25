package com.meg.listshop.lmt.service.tag;

import com.meg.listshop.lmt.data.entity.DishEntity;

import java.util.List;

/**
 * Created by margaretmartin on 07/12/2017.
 */
public interface AutoTagService {

    void doAutoTag(DishEntity dishEntity, boolean overrideStatus);

    List<DishEntity> getDishesToAutotag(int dishToAutotagCount);
}
