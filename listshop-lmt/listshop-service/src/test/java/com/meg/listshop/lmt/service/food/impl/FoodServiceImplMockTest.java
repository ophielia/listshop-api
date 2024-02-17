package com.meg.listshop.lmt.service.food.impl;

import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.data.entity.FoodCategoryEntity;
import com.meg.listshop.lmt.data.entity.FoodCategoryMappingEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.pojos.TagInfoDTO;
import com.meg.listshop.lmt.data.repository.FoodCategoryMappingRepository;
import com.meg.listshop.lmt.data.repository.FoodRepository;
import com.meg.listshop.lmt.service.food.FoodService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class FoodServiceImplMockTest {
    private FoodService foodService;

    @MockBean
    FoodCategoryMappingRepository foodCategoryMappingRepo;
    @MockBean
    FoodRepository foodRepository;

    List<FoodCategoryMappingEntity> allMappedCategories;

    List<TagInfoDTO> allSearchTags;

    @BeforeEach
    void setUp() {
        this.foodService = new FoodServiceImpl(
                foodCategoryMappingRepo, foodRepository
        );
        allSearchTags = new ArrayList<>();
        allSearchTags.add(buildTagInfoDTO(1L, "searchTag1", 2L));
        allSearchTags.add(buildTagInfoDTO(2L, "searchTag2", 3L));
        allSearchTags.add(buildTagInfoDTO(3L, "searchTag3", 4L));
        allSearchTags.add(buildTagInfoDTO(4L, "searchTag4", null));

        allMappedCategories = new ArrayList<>();
        allMappedCategories.add(buildMapping(2L, 22L, "category2"));
        allMappedCategories.add(buildMapping(3L, 33L, "category3"));
        allMappedCategories.add(buildMapping(4L, 44L, "category4"));
    }


    @Test
    void testCategoryMatchForTag() {
        Long tagId = 1L;
        List<Long> tagIds = allSearchTags.stream().map(TagInfoDTO::getTagId).collect(Collectors.toList());

        Mockito.when(foodCategoryMappingRepo.findMappingsByTagIds(tagIds)).thenReturn(allMappedCategories);
        FoodCategoryEntity result = foodService.getCategoryMatchForTag(tagId, allSearchTags);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(22L,result.getCategoryId(), "category should be 22" );

    }

    @Test
    void testCategoryMatchForTagNoMappings() {
        Long tagId = 1L;
        List<Long> tagIds = allSearchTags.stream().map(TagInfoDTO::getTagId).collect(Collectors.toList());

        List<FoodCategoryMappingEntity> onlyTop = allMappedCategories.stream().filter(m-> m.getTag().getId().equals(4L))
                        .collect(Collectors.toList());
        Mockito.when(foodCategoryMappingRepo.findMappingsByTagIds(tagIds)).thenReturn(onlyTop);
        FoodCategoryEntity result = foodService.getCategoryMatchForTag(tagId, allSearchTags);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(44L,result.getCategoryId(), "category should be 44" );
    }

    @Test
    void testCategoryMatchForSomeMappings() {
        Long tagId = 1L;
        List<Long> tagIds = allSearchTags.stream().map(TagInfoDTO::getTagId).collect(Collectors.toList());

        Mockito.when(foodCategoryMappingRepo.findMappingsByTagIds(tagIds)).thenReturn(new ArrayList<>());
        FoodCategoryEntity result = foodService.getCategoryMatchForTag(tagId, allSearchTags);
        Assertions.assertNull(result);

    }

    private TagInfoDTO buildTagInfoDTO(Long tagId, String tagName,
                                       Long parentId) {
        return new TagInfoDTO(tagId, tagName, "", 0.0, 0L, TagType.Rating, false, parentId, false);

    }

    private FoodCategoryMappingEntity buildMapping(Long tagId, Long categoryId, String categoryName) {
        TagEntity tag = new TagEntity(tagId);
        FoodCategoryEntity category = new FoodCategoryEntity();
        category.setCategoryId(categoryId);
        category.setName(categoryName);

        FoodCategoryMappingEntity entity = new FoodCategoryMappingEntity();
        entity.setTag(tag);
        entity.setCategory(category);
        return entity;
    }

}