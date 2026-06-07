/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.lmt.data.CategoryTagMapping;
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

import java.util.List;
import java.util.stream.Stream;

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

    private ListLayoutEntity testListLayout(Long layoutId) {
        return new ListLayoutEntity(layoutId);
    }

}
