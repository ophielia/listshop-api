package com.meg.atable.lmt.service.impl;

import com.meg.atable.Application;
import com.meg.atable.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.atable.lmt.data.entity.TagEntity;
import com.meg.atable.lmt.data.repository.CategoryRelationRepository;
import com.meg.atable.lmt.data.repository.ListLayoutCategoryRepository;
import com.meg.atable.lmt.data.repository.ListLayoutRepository;
import com.meg.atable.lmt.data.repository.TagRepository;
import com.meg.atable.lmt.service.ListLayoutProperties;
import com.meg.atable.lmt.service.ShoppingListProperties;
import com.meg.atable.lmt.service.tag.TagService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
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

    @Before
    public void setUp() {
        listLayoutService = new ListLayoutServiceImpl(listLayoutCategoryRepository, listLayoutProperties, categoryRelationRepository, listLayoutRepository,
                tagRepository, tagService, shoppingListProperties);
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
}