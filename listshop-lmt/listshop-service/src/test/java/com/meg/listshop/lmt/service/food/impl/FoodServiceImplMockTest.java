package com.meg.listshop.lmt.service.food.impl;

import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.data.entity.FoodCategoryEntity;
import com.meg.listshop.lmt.data.entity.FoodCategoryMappingEntity;
import com.meg.listshop.lmt.data.pojos.TagInfoDTO;
import com.meg.listshop.lmt.data.repository.FoodCategoryMappingRepository;
import com.meg.listshop.lmt.data.repository.FoodCategoryRepository;
import com.meg.listshop.lmt.data.repository.FoodRepository;
import com.meg.listshop.lmt.service.food.FoodService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    List<FoodCategoryMappingEntity> allMappedCategories;
    List<FoodCategoryEntity> allCategories;

    List<TagInfoDTO> allSearchTags;

    @BeforeEach
    void setUp() {
        this.foodService = new FoodServiceImpl(
                foodCategoryMappingRepo, foodRepository, foodCategoryRepository
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

    private FoodCategoryEntity buildCategory(Long categoryId, String categoryName) {
        FoodCategoryEntity category = new FoodCategoryEntity();
        category.setId(categoryId);
        category.setName(categoryName);
        return category;
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

    private TagInfoDTO buildTagInfoDTO(Long tagId, String tagName,
                                       Long parentId) {
        return new TagInfoDTO(tagId, tagName, "", 0.0, 0L, TagType.Rating, false, parentId, false);

    }

    private FoodCategoryMappingEntity buildMapping(Long tagId, Long categoryId) {
        FoodCategoryMappingEntity entity = new FoodCategoryMappingEntity();
        entity.setTagId(tagId);
        entity.setCategoryId(categoryId);
        return entity;
    }

}