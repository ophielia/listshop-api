/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.lmt.data.CategoryTagMapping;
import com.meg.listshop.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.listshop.lmt.data.entity.ListLayoutEntity;
import com.meg.listshop.lmt.data.pojos.LayoutCategoryDTO;
import com.meg.listshop.lmt.data.pojos.LayoutDTO;
import com.meg.listshop.lmt.data.repository.ListLayoutCategoryRepository;
import com.meg.listshop.lmt.data.repository.ListLayoutRepository;
import com.meg.listshop.lmt.data.repository.TagRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class V2LayoutServiceImplMockTest {

    @Mock
    private V2LayoutServiceImpl listLayoutService;

    @Mock
    private ListLayoutRepository listLayoutRepository;

    @Mock
    private ListLayoutCategoryRepository categoryRepository;

    @Mock
    private UserService userService;

    @Mock
    private TagRepository tagRepository;

    @BeforeEach
    void setUp() {
        listLayoutService = new V2LayoutServiceImpl(listLayoutRepository, categoryRepository, tagRepository, userService);
    }


    @Test
    void testGetStandardLayoutNoUser() {
        Long userId = null;
        Long layoutId = 1L;
        ListLayoutEntity listLayout = testListLayout(layoutId);
        CategoryTagMapping carrotMapping = new CategoryTagMapping(1L, "produce", 2L, "carrot");
        CategoryTagMapping hairbrushMapping = new CategoryTagMapping(2L, "other", 3L, "hairbrush");
        CategoryTagMapping shampooMapping = new CategoryTagMapping(2L, "other", 4L, "shampoo");
        List<CategoryTagMapping> standardMappingList = List.of(carrotMapping, hairbrushMapping, shampooMapping);
        LayoutCategoryDTO produceCategory = new LayoutCategoryDTO("1", "produce", false, 100);
        LayoutCategoryDTO otherCategory = new LayoutCategoryDTO("2", "other", false, 100);
        LayoutCategoryDTO notAppearingInThisFilmCategory = new LayoutCategoryDTO("99", "not here", false, 100);
        List<LayoutCategoryDTO> standardCategoryList = List.of(produceCategory, otherCategory, notAppearingInThisFilmCategory);

        Mockito.when(listLayoutRepository.getStandardLayout()).thenReturn(listLayout);
        Mockito.when(categoryRepository.getStandardTagMappings(layoutId)).thenReturn(standardMappingList);
        Mockito.when(categoryRepository.getStandardCategories()).thenReturn(standardCategoryList);

        LayoutDTO testResult = listLayoutService.getStandardLayout(userId);

        Assertions.assertNotNull(testResult);
        Assertions.assertEquals(2, testResult.getCategories().size(), "There should be 2 categories");
        LayoutCategoryDTO otherCategoryResult = testResult.getCategories().stream()
                .filter(category -> category.getCategoryId().equals("2"))
                .findFirst()
                .orElse(null);
        Assertions.assertNotNull(otherCategoryResult, "Category 'other' not found");
        Assertions.assertEquals(2, otherCategoryResult.getTags().size(), "There should be 2 tags in category 'other'");

    }

    @Test
    void testGetStandardLayoutUser() {
        Long userId = 2L;
        Long layoutId = 1L;
        Long userLayoutId = 2L;
        ListLayoutEntity listLayout = testListLayout(layoutId);
        ListLayoutEntity userListLayout = testListLayout(userLayoutId);

        CategoryTagMapping userTagMapping = new CategoryTagMapping(2L, "other", 2222L, "weed wacker");
        CategoryTagMapping userCarrotMapping = new CategoryTagMapping(111L, "frozen", 2L, "carrot");
        CategoryTagMapping carrotMapping = new CategoryTagMapping(1L, "produce", 2L, "carrot");
        CategoryTagMapping hairbrushMapping = new CategoryTagMapping(2L, "other", 3L, "hairbrush");
        CategoryTagMapping shampooMapping = new CategoryTagMapping(2L, "other", 4L, "shampoo");
        List<CategoryTagMapping> standardMappingList = List.of(carrotMapping, hairbrushMapping, shampooMapping);
        LayoutCategoryDTO produceCategory = new LayoutCategoryDTO("1", "produce", false, 100);
        LayoutCategoryDTO otherCategory = new LayoutCategoryDTO("2", "other", false, 100);
        LayoutCategoryDTO frozenCategory = new LayoutCategoryDTO("3", "frozen", false, 100);
        LayoutCategoryDTO notAppearingInThisFilmCategory = new LayoutCategoryDTO("99", "not here", false, 100);
        List<LayoutCategoryDTO> standardCategoryList = List.of(produceCategory, otherCategory, notAppearingInThisFilmCategory, frozenCategory);

        Mockito.when(listLayoutRepository.getStandardLayout()).thenReturn(listLayout);
        Mockito.when(listLayoutRepository.getDefaultUserLayout(userId)).thenReturn(userListLayout);
        Mockito.when(categoryRepository.getTagCategoryMappings(userId, userLayoutId)).thenReturn(List.of(userCarrotMapping));
        Mockito.when(categoryRepository.getUserTagMappings(userId)).thenReturn(List.of(userTagMapping));
        Mockito.when(categoryRepository.getStandardTagMappings(layoutId)).thenReturn(standardMappingList);
        Mockito.when(categoryRepository.getStandardCategories()).thenReturn(standardCategoryList);

        LayoutDTO testResult = listLayoutService.getStandardLayout(userId);

        Assertions.assertNotNull(testResult);
        Assertions.assertEquals(2, testResult.getCategories().size(), "There should be 2 categories");
        LayoutCategoryDTO otherCategoryResult = testResult.getCategories().stream()
                .filter(category -> category.getCategoryId().equals("2"))
                .findFirst()
                .orElse(null);
        Assertions.assertNotNull(otherCategoryResult, "Category 'other' not found");
        Assertions.assertEquals(3, otherCategoryResult.getTags().size(), "There should be 2 tags in category 'other'");
        Assertions.assertTrue(otherCategoryResult.getTags().stream().anyMatch(tag -> tag.tagName().equals("weed wacker")));
        LayoutCategoryDTO frozenCategoryResult = testResult.getCategories().stream()
                .filter(category -> category.getCategoryId().equals("3"))
                .findFirst()
                .orElse(null);
        Assertions.assertNotNull(frozenCategoryResult, "Category 'frozen' not found");
        Assertions.assertEquals(1, frozenCategoryResult.getTags().size(), "There should be 1 tag in category 'frozen'");
    }

    @Test
    void testGetCategoryForTagSuccess() {
        Long userId = 1L;
        Long tagId = 2L;
        ListLayoutEntity layout = new ListLayoutEntity(10L);
        ListLayoutCategoryEntity category = new ListLayoutCategoryEntity(20L);
        category.setName("Test Category");
        layout.setCategories(Collections.singleton(category));

        Mockito.when(listLayoutRepository.getStandardLayout()).thenReturn(new ListLayoutEntity(5L));
        Mockito.when(listLayoutRepository.fillLayout(Mockito.eq(userId), Mockito.eq(tagId), Mockito.any())).thenReturn(layout);
        Mockito.when(listLayoutRepository.getUserLayoutsWithTag(userId, tagId)).thenReturn(Collections.emptyList());

        ListLayoutCategoryEntity result = listLayoutService.getCategoryForTag(userId, tagId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(20L, result.getId());
        Assertions.assertEquals("Test Category", result.getName());
    }

    @Test
    void testGetCategoryForTagNoLayouts() {
        Long userId = 1L;
        Long tagId = 2L;

        Mockito.when(listLayoutRepository.getStandardLayout()).thenReturn(new ListLayoutEntity(5L));
        Mockito.when(listLayoutRepository.fillLayout(Mockito.eq(userId), Mockito.eq(tagId), Mockito.any())).thenReturn(null);
        Mockito.when(listLayoutRepository.getUserLayoutsWithTag(userId, tagId)).thenReturn(Collections.emptyList());

        ListLayoutCategoryEntity result = listLayoutService.getCategoryForTag(userId, tagId);

        Assertions.assertNull(result);
    }

    @Test
    void testGetCategoryForTagEmptyCategories() {
        Long userId = 1L;
        Long tagId = 2L;
        ListLayoutEntity layout = new ListLayoutEntity(10L);
        layout.setCategories(Collections.emptySet());

        Mockito.when(listLayoutRepository.getStandardLayout()).thenReturn(new ListLayoutEntity(5L));
        Mockito.when(listLayoutRepository.fillLayout(Mockito.eq(userId), Mockito.eq(tagId), Mockito.any())).thenReturn(layout);
        Mockito.when(listLayoutRepository.getUserLayoutsWithTag(userId, tagId)).thenReturn(Collections.emptyList());

        ListLayoutCategoryEntity result = listLayoutService.getCategoryForTag(userId, tagId);

        Assertions.assertNull(result);
    }

    private ListLayoutEntity testListLayout(Long layoutId) {
        return new ListLayoutEntity(layoutId);
    }

}
