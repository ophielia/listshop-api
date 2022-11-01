package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.lmt.data.entity.ListLayoutEntity;
import com.meg.listshop.lmt.data.repository.ListLayoutCategoryRepository;
import com.meg.listshop.lmt.data.repository.ListLayoutRepository;
import com.meg.listshop.lmt.data.repository.TagRepository;
import com.meg.listshop.lmt.service.ListLayoutProperties;
import com.meg.listshop.lmt.service.ListSearchService;
import com.meg.listshop.lmt.service.ShoppingListProperties;
import com.meg.listshop.lmt.service.tag.TagService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class ListLayoutServiceImplMockTest {

    private ListLayoutServiceImpl listLayoutService;

    @MockBean
    private ListLayoutCategoryRepository listLayoutCategoryRepository;

    @MockBean
    private ListLayoutProperties listLayoutProperties;


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
        listLayoutService = new ListLayoutServiceImpl(listLayoutCategoryRepository, listLayoutRepository,
                tagRepository, tagService, listSearchService);
    }


    @Test
    @Ignore
    public void testGetDefaultListLayout() {
        //MM layout
        //Mockito.when(shoppingListProperties.getDefaultListLayoutType()).thenReturn(ListLayoutType.RoughGrained);
        //Mockito.when(listLayoutRepository.findByLayoutType(ListLayoutType.RoughGrained)).thenReturn(Collections.singletonList(new ListLayoutEntity()));

        ListLayoutEntity testResult = listLayoutService.getDefaultListLayout();

        Assert.assertNotNull(testResult);

    }


}