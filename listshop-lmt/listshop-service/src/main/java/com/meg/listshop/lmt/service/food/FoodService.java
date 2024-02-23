package com.meg.listshop.lmt.service.food;

import com.meg.listshop.lmt.api.model.AdminTagFullInfo;
import com.meg.listshop.lmt.data.entity.FoodCategoryEntity;
import com.meg.listshop.lmt.data.entity.FoodEntity;
import com.meg.listshop.lmt.data.pojos.FoodMappingDTO;
import com.meg.listshop.lmt.data.pojos.TagInfoDTO;

import java.util.List;

/**
 * Created by margaretmartin on 13/05/2017.
 */
public interface FoodService {

    FoodCategoryEntity getCategoryMatchForTag(Long tagId, List<TagInfoDTO> ascendantTags);


    List<FoodEntity> getSuggestedFoods(Long tagId);

    void addOrUpdateFoodForTag(Long tagId, Long foodId, boolean fromAdmin);

    List<FoodMappingDTO> getFoodCategoryMappings();

    void addOrCategoryToTag(Long tagId, Long categoryId);

    void fillFoodInformation(AdminTagFullInfo tagInfo);
}
