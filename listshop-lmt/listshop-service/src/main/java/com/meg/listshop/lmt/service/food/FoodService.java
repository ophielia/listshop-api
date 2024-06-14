package com.meg.listshop.lmt.service.food;

import com.meg.listshop.conversion.data.pojo.ConversionSampleDTO;
import com.meg.listshop.lmt.api.model.AdminTagFullInfo;
import com.meg.listshop.lmt.data.entity.FoodCategoryEntity;
import com.meg.listshop.lmt.data.entity.FoodConversionEntity;
import com.meg.listshop.lmt.data.entity.FoodEntity;
import com.meg.listshop.lmt.data.pojos.FoodMappingDTO;
import com.meg.listshop.lmt.data.pojos.TagInfoDTO;

import java.util.List;
import java.util.Map;

/**
 * Created by margaretmartin on 13/05/2017.
 */
public interface FoodService {

    FoodCategoryEntity getCategoryMatchForTag(Long tagId, List<TagInfoDTO> ascendantTags);


    List<FoodEntity> getSuggestedFoods(Long tagId, String alternateSearchTerm);

    List<FoodEntity> getSuggestedFoods(String alternateSearchTerm);

    void addOrUpdateFoodForTag(Long tagId, Long foodId, boolean fromAdmin);

    List<ConversionSampleDTO> samplesForConversionId(Long tagId, Boolean isLiquid);

    List<FoodMappingDTO> getFoodCategoryMappings();

    void addOrUpdateFoodCategory(Long tagId, Long categoryId);

    void fillFoodInformation(AdminTagFullInfo tagInfo);

    List<FoodCategoryEntity> getFoodCategories();

    void addOrUpdateFoodCategories(List<Long> tagIds, Long foodCategoryToAssign);

    Map<Long, List<FoodConversionEntity>> getFoodFactors(List<FoodEntity> foodEntities);

    void addOrUpdateFoodForTags(List<Long> tagIds, Long foodIdToAssign);

    void copyFoodFromTag(List<Long> tagIds, Long fromTagId);
}
