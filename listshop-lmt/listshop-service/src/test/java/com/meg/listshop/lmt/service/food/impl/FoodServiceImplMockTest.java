package com.meg.listshop.lmt.service.food.impl;

import com.meg.listshop.conversion.service.ConversionFactorService;
import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.data.entity.*;
import com.meg.listshop.lmt.data.pojos.TagInfoDTO;
import com.meg.listshop.lmt.data.pojos.TagSearchCriteria;
import com.meg.listshop.lmt.data.repository.FoodCategoryMappingRepository;
import com.meg.listshop.lmt.data.repository.FoodCategoryRepository;
import com.meg.listshop.lmt.data.repository.FoodConversionRepository;
import com.meg.listshop.lmt.data.repository.FoodRepository;
import com.meg.listshop.lmt.service.food.FoodService;
import com.meg.listshop.lmt.service.tag.TagService;
import com.meg.listshop.lmt.service.tag.TagStructureService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class FoodServiceImplMockTest {
    private FoodService foodService;

    @MockBean
    FoodCategoryMappingRepository foodCategoryMappingRepo;
    @MockBean
    FoodRepository foodRepository;

    @MockBean
    FoodCategoryRepository foodCategoryRepository;
    @MockBean
    TagService tagService;
    @MockBean
    TagStructureService tagStructureService;

    @MockBean
    FoodConversionRepository foodConversionRepository;

    @MockBean
    ConversionFactorService conversionFactorService ;

    List<FoodCategoryMappingEntity> allMappedCategories;
    List<FoodCategoryEntity> allCategories;

    List<TagInfoDTO> allSearchTags;

    @BeforeEach
    void setUp() {
        this.foodService = new FoodServiceImpl(
                foodCategoryMappingRepo, foodRepository, foodCategoryRepository,
                tagService, tagStructureService,
                 foodConversionRepository, conversionFactorService
        );
        allSearchTags = new ArrayList<>();
        allSearchTags.add(buildTagInfoDTO(1L, "searchTag1", 2L));
        allSearchTags.add(buildTagInfoDTO(2L, "searchTag2", 3L));
        allSearchTags.add(buildTagInfoDTO(3L, "searchTag3", 4L));
        allSearchTags.add(buildTagInfoDTO(4L, "searchTag4", null));

        allMappedCategories = new ArrayList<>();
        allMappedCategories.add(buildMapping(2L, 22L));
        allMappedCategories.add(buildMapping(3L, 33L));
        allMappedCategories.add(buildMapping(4L, 44L));

        allCategories = new ArrayList<>();
        allCategories.add(buildCategory(22L, "category2"));
        allCategories.add(buildCategory(33L, "category3"));
        allCategories.add(buildCategory(44L, "category4"));
    }


    @Test
    void testCategoryMatchForTag() {
        Long tagId = 1L;
        Long categoryId = 22L;
        FoodCategoryEntity expectedCategory = allCategories.stream().filter(c -> c.getId().equals(categoryId)).findFirst().orElse(null);
        List<Long> tagIds = allSearchTags.stream().map(TagInfoDTO::getTagId).collect(Collectors.toList());

        Mockito.when(foodCategoryMappingRepo.findFoodCategoryMappingEntityByTagIdIn(tagIds)).thenReturn(allMappedCategories);
        Mockito.when(foodCategoryRepository.findById(categoryId)).thenReturn(Optional.of(expectedCategory));

        FoodCategoryEntity result = foodService.getCategoryMatchForTag(tagId, allSearchTags);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(22L,result.getId(), "category should be 22" );

    }

    @Test
    void testCategoryMatchForTagSomeMappings() {
        Long tagId = 1L;
        Long categoryId = 44L;
        FoodCategoryEntity expectedCategory = allCategories.stream().filter(c -> c.getId().equals(categoryId)).findFirst().orElse(null);
        List<Long> tagIds = allSearchTags.stream().map(TagInfoDTO::getTagId).collect(Collectors.toList());

        List<FoodCategoryMappingEntity> onlyTop = allMappedCategories.stream().filter(m -> m.getTagId().equals(4L))
                        .collect(Collectors.toList());
        Mockito.when(foodCategoryMappingRepo.findFoodCategoryMappingEntityByTagIdIn(tagIds)).thenReturn(onlyTop);
        Mockito.when(foodCategoryRepository.findById(categoryId)).thenReturn(Optional.of(expectedCategory));
        FoodCategoryEntity result = foodService.getCategoryMatchForTag(tagId, allSearchTags);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(44L,result.getId(), "category should be 44" );
    }

    @Test
    void testCategoryMatchNoMappings() {
        Long tagId = 1L;
        List<Long> tagIds = allSearchTags.stream().map(TagInfoDTO::getTagId).collect(Collectors.toList());

        Mockito.when(foodCategoryMappingRepo.findFoodCategoryMappingEntityByTagIdIn(tagIds)).thenReturn(new ArrayList<>());
        FoodCategoryEntity result = foodService.getCategoryMatchForTag(tagId, allSearchTags);
        Assertions.assertNull(result);

    }

    @Test
    void testGetSuggestedFoods() {
        Long tagId = 1L;
        Long categoryId = 99L;
        Long otherCategoryId = 999L;
        Long replacementTagId = 100L;
        TagEntity tag = new TagEntity();
        tag.setId(tagId);
        tag.setName("tag1");
        tag.setReplacementTagId(replacementTagId);

        List<TagEntity> ascendantTags = new ArrayList<>();
        ascendantTags.add(buildTagEntity(2L, "tag2"));
        ascendantTags.add(buildTagEntity(3L, "tag3"));
        ascendantTags.add(buildTagEntity(4L, "tag4"));

        TagSearchCriteria searchCriteria = new TagSearchCriteria();
        searchCriteria.setTagIds(ascendantTags.stream().map(TagEntity::getId).collect(Collectors.toList()));

        List<TagInfoDTO> tagInfoList = new ArrayList<>();
        tagInfoList.add(buildTagInfoDTO(1L, "tag1",  2L));
        tagInfoList.add(buildTagInfoDTO(2L, "tag2",  3L));
        tagInfoList.add(buildTagInfoDTO(3L, "tag3",  4L));
        tagInfoList.add(buildTagInfoDTO(4L, "tag4",  null));

        FoodCategoryEntity foundCategory = new FoodCategoryEntity();
        foundCategory.setId(categoryId);


        List<FoodEntity> categoryFoods = new ArrayList<>();
        categoryFoods.add(buildFood(1L,"food1", otherCategoryId));
        categoryFoods.add(buildFood(2L,"food2", otherCategoryId));
        categoryFoods.add(buildFood(33L,"food33", otherCategoryId));
        categoryFoods.add(buildFood(44L,"food44", categoryId));
        categoryFoods.add(buildFood(55L,"food55", categoryId));

        TagEntity replaceTag = new TagEntity();
        replaceTag.setId(replacementTagId);

        Mockito.when(tagService.getTagById(tagId))
                .thenReturn(tag);

        Mockito.when(tagStructureService.getAscendantTags(tag))
                .thenReturn(ascendantTags);

        Mockito.when(tagService.getTagInfoList(any(TagSearchCriteria.class)))
                .thenReturn(tagInfoList);

        Mockito.when(foodCategoryMappingRepo.findFoodCategoryMappingEntityByTagIdIn(any(List.class)))
                .thenReturn(allMappedCategories);

        List<FoodCategoryMappingEntity> partialMappings = Collections.singletonList(buildMapping(4L,categoryId));
        Mockito.when(foodCategoryMappingRepo.findFoodCategoryMappingEntityByTagIdIn(any(List.class)))
                .thenReturn(partialMappings);


        Mockito.when(foodCategoryRepository.findById(categoryId))
                .thenReturn(Optional.of(foundCategory));
        Mockito.when(foodRepository.findFoodEntitiesByNameContainsIgnoreCaseAndHasFactorTrue(tag.getName().toLowerCase()))
                .thenReturn(categoryFoods);



        // call to be tested
        List<FoodEntity> result = foodService.getSuggestedFoods(tagId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(5, result.size(), "expect 5 results");
        for (int i = 0; i < 2; i++) {
            Assertions.assertEquals(categoryId,result.get(i).getCategoryId(),"first 2 should be the right category id");
        }
        for (int i = 2; i < 5; i++) {
            Assertions.assertEquals(otherCategoryId,result.get(i).getCategoryId(),"first 2 should be the right category id");
        }
    }

    @Test
    void testAddOrUpdateFoodForTag() {
        Long tagId = 99L;
        Long foodId = 999L;
        double conversionAmount = 3.0;
        Long conversionUnit = 3333L;
        double conversionGramWeight = 150.0;

        TagEntity tagNoFood = new TagEntity(tagId);
        tagNoFood.setFoodId(null);

        FoodConversionEntity conversionEntity = new FoodConversionEntity();
        conversionEntity.setAmount(conversionAmount);
        conversionEntity.setUnitId(conversionUnit);
        conversionEntity.setGramWeight(conversionGramWeight);

        Mockito.when(tagService.getTagById(tagId)).thenReturn(tagNoFood);
        Mockito.when(foodConversionRepository.findAllByFoodId(foodId))
                .thenReturn(Collections.singletonList(conversionEntity));
        Mockito.doNothing().when(conversionFactorService).addFactorForTag(tagId, conversionAmount, conversionUnit, conversionGramWeight);

        // call under test
        foodService.addOrUpdateFoodForTag(tagId, foodId, true);

        Mockito.verify(conversionFactorService).addFactorForTag(tagId,conversionAmount, conversionUnit, conversionGramWeight);
    }


    @Test
    void testAddOrUpdateFoodForTagExistingAssignment() {
        Long tagId = 99L;
        Long foodId = 999L;
        double conversionAmount = 3.0;
        Long conversionUnit = 3333L;
        double conversionGramWeight = 150.0;

        TagEntity tagNoFood = new TagEntity(tagId);
        tagNoFood.setFoodId(1234L);

        FoodConversionEntity conversionEntity = new FoodConversionEntity();
        conversionEntity.setAmount(conversionAmount);
        conversionEntity.setUnitId(conversionUnit);
        conversionEntity.setGramWeight(conversionGramWeight);

        Mockito.when(tagService.getTagById(tagId)).thenReturn(tagNoFood);
        Mockito.when(foodConversionRepository.findAllByFoodId(foodId))
                .thenReturn(Collections.singletonList(conversionEntity));
        Mockito.doNothing().when(conversionFactorService).deleteFactorsForTag(tagId);
        Mockito.doNothing().when(conversionFactorService).addFactorForTag(tagId, conversionAmount, conversionUnit, conversionGramWeight);

        // call under test
        foodService.addOrUpdateFoodForTag(tagId, foodId, true);

        Mockito.verify(conversionFactorService).addFactorForTag(tagId,conversionAmount, conversionUnit, conversionGramWeight);
        Mockito.verify(conversionFactorService).deleteFactorsForTag(tagId);
    }

    private FoodCategoryMappingEntity buildMapping(Long tagId, Long categoryId) {
        FoodCategoryMappingEntity entity = new FoodCategoryMappingEntity();
        entity.setTagId(tagId);
        entity.setCategoryId(categoryId);
        return entity;
    }

    private TagEntity buildTagEntity(Long tagId, String tagName) {
        TagEntity tag = new TagEntity();
        tag.setId(tagId);
        tag.setName(tagName);

        return tag;
    }


    private FoodCategoryEntity buildCategory(Long categoryId, String categoryName) {
        FoodCategoryEntity category = new FoodCategoryEntity();
        category.setId(categoryId);
        category.setName(categoryName);
        return category;
    }


    private TagInfoDTO buildTagInfoDTO(Long tagId, String tagName,
                                       Long parentId) {
        return new TagInfoDTO(tagId, tagName, "", 0.0, 0L, TagType.Rating, false, parentId, false);

    }

    private FoodEntity buildFood(Long foodId, String foodName, Long categoryId) {
        FoodEntity food = new FoodEntity();
        food.setFoodId(foodId);
        food.setName(foodName);
        food.setCategoryId(categoryId);
        return food;
    }

}