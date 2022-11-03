package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.lmt.data.entity.ListLayoutEntity;
import com.meg.listshop.lmt.data.repository.ListLayoutCategoryRepository;
import com.meg.listshop.lmt.data.repository.ListLayoutRepository;
import com.meg.listshop.lmt.data.repository.TagRepository;
import com.meg.listshop.lmt.service.ListSearchService;
import com.meg.listshop.lmt.service.tag.TagService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class ListLayoutServiceImplMockTest {

    private ListLayoutServiceImpl listLayoutService;

    @MockBean
    private ListLayoutCategoryRepository listLayoutCategoryRepository;

    @MockBean
    private ListLayoutRepository listLayoutRepository;

    @MockBean
    private TagRepository tagRepository;

    @MockBean
    private TagService tagService;

    @MockBean
    private ListSearchService listSearchService;

    @BeforeEach
    public void setUp() {
        listLayoutService = new ListLayoutServiceImpl(listLayoutCategoryRepository, listLayoutRepository,
                tagRepository, tagService, listSearchService);
    }


    @Test
    public void testGetStandardLayout() {
        Mockito.when(listLayoutRepository.getStandardLayout()).thenReturn(new ListLayoutEntity());

        ListLayoutEntity testResult = listLayoutService.getStandardLayout();

        Assertions.assertNotNull(testResult);

    }

    @Test
    public void testGetDefaultUserLayoutLayout() {
        Long userId = 12L;
        Mockito.when(listLayoutRepository.getDefaultUserLayout(userId)).thenReturn(new ListLayoutEntity());

        ListLayoutEntity testResult = listLayoutService.getDefaultUserLayout(userId);

        Assertions.assertNotNull(testResult);
    }

    @Test
    public void testGetDefaultUserLayoutLayout_DoesntExist() {
        Long userId = 12L;
        Mockito.when(listLayoutRepository.getDefaultUserLayout(userId)).thenReturn(null);

        ListLayoutEntity testResult = listLayoutService.getDefaultUserLayout(userId);

        Assertions.assertNull(testResult);
    }

    @Test
    public void testGetUserListLayout() {
        Long userId = 12L;
        Long listId = 13L;

        Mockito.when(listLayoutRepository.getUserListLayout(userId, listId)).thenReturn(new ListLayoutEntity());

        ListLayoutEntity testResult = listLayoutService.getUserListLayout(userId, listId);

        Assertions.assertNotNull(testResult);
    }

    @Test
    public void testGetUserListLayout_DoesntExist() {
        Long userId = 12L;
        Long listId = 13L;

        Mockito.when(listLayoutRepository.getUserListLayout(userId, listId)).thenReturn(null);

        ListLayoutEntity testResult = listLayoutService.getDefaultUserLayout(userId);

        Assertions.assertNull(testResult);
    }
    /*

        @Override
    public ListLayoutEntity getUserListLayout(Long userId, Long listLayoutId) {
        // get user layout
        return listLayoutRepository.getUserListLayout(userId, listLayoutId);
    }



     */

}