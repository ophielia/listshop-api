package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.lmt.api.model.ListLayoutType;
import com.meg.listshop.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.listshop.lmt.data.entity.ListLayoutEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.repository.CategoryRelationRepository;
import com.meg.listshop.lmt.data.repository.ListLayoutCategoryRepository;
import com.meg.listshop.lmt.data.repository.ListLayoutRepository;
import com.meg.listshop.lmt.data.repository.TagRepository;
import com.meg.listshop.lmt.service.ListLayoutProperties;
import com.meg.listshop.lmt.service.ListSearchService;
import com.meg.listshop.lmt.service.ShoppingListProperties;
import com.meg.listshop.lmt.service.tag.TagService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class ListLayoutServiceImplMockTest {

    private ListLayoutServiceImpl listLayoutService;

    @MockBean
    private ListLayoutCategoryRepository listLayoutCategoryRepository;

    @MockBean
    private ListLayoutProperties listLayoutProperties;

    @MockBean
    private CategoryRelationRepository categoryRelationRepository;

    @MockBean
    private ListLayoutRepository listLayoutRepository;

    @MockBean
    private TagRepository tagRepository;

    @MockBean
    private TagService tagService;

    @MockBean
    private ShoppingListProperties shoppingListProperties;

    @MockBean
    private ListSearchService listSearchService;

    @Before
    public void setUp() {
        listLayoutService = new ListLayoutServiceImpl(listLayoutCategoryRepository, listLayoutProperties, categoryRelationRepository, listLayoutRepository,
                tagRepository, tagService, shoppingListProperties, listSearchService);
    }


    @Test
    public void testGetAllDefaultCategories() {
        Long tagId = 66L;
        TagEntity mockTag = new TagEntity();
        mockTag.setId(66L);
        List<ListLayoutCategoryEntity> mockCategories = new ArrayList<>();
        mockCategories.add(new ListLayoutCategoryEntity());
        mockCategories.add(new ListLayoutCategoryEntity());
        mockCategories.add(new ListLayoutCategoryEntity());

        Mockito.when(tagService.getTagById(tagId)).thenReturn(mockTag);
        Mockito.when(listLayoutCategoryRepository.findByIsDefaultTrue())
                .thenReturn(mockCategories);

        ArgumentCaptor<TagEntity> argument = ArgumentCaptor.forClass(TagEntity.class);

        ArgumentCaptor<List<ListLayoutCategoryEntity>> listCaptor
                = ArgumentCaptor.forClass((Class) List.class);

        listLayoutService.assignTagToDefaultCategories(mockTag);

        Mockito.verify(tagService).save(argument.capture());
        Mockito.verify(listLayoutCategoryRepository).saveAll(listCaptor.capture());

    }

    @Test
    public void testGetDefaultListLayout() {
        Mockito.when(shoppingListProperties.getDefaultListLayoutType()).thenReturn(ListLayoutType.RoughGrained);
        Mockito.when(listLayoutRepository.findByLayoutType(ListLayoutType.RoughGrained)).thenReturn(Collections.singletonList(new ListLayoutEntity()));

        ListLayoutEntity testResult = listLayoutService.getDefaultListLayout();

        Assert.assertNotNull(testResult);

    }


}