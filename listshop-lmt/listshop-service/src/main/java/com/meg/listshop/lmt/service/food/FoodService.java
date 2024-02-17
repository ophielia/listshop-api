package com.meg.listshop.lmt.service.food;

import com.meg.listshop.lmt.data.entity.FoodCategoryEntity;
import com.meg.listshop.lmt.data.entity.FoodEntity;
import com.meg.listshop.lmt.data.pojos.TagInfoDTO;

import java.util.List;

/**
 * Created by margaretmartin on 13/05/2017.
 */
public interface FoodService {

    FoodCategoryEntity getCategoryMatchForTag(Long tagId, List<TagInfoDTO> ascendantTags);

    List<FoodEntity> foodMatches(String name, FoodCategoryEntity categoryMatch);
    List<FoodEntity> foodMatches(String name);
}
