package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.listshop.lmt.data.entity.ListLayoutEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.repository.ListLayoutCategoryRepository;
import com.meg.listshop.lmt.data.repository.ListLayoutRepository;
import com.meg.listshop.lmt.data.repository.TagRepository;
import com.meg.listshop.lmt.service.TagTestBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class LayoutServiceImplMockTest {

    @MockBean
    private LayoutServiceImpl listLayoutService;

    @MockBean
    private ListLayoutRepository listLayoutRepository;

    @MockBean
    private ListLayoutCategoryRepository categoryRepositoryRepository;

    @MockBean
    private TagRepository tagRepository;

    @BeforeEach
    void setUp() {
        listLayoutService = new LayoutServiceImpl(listLayoutRepository, categoryRepositoryRepository, tagRepository);
    }


    @Test
    void testGetStandardLayout() {
        Mockito.when(listLayoutRepository.getStandardLayout()).thenReturn(new ListLayoutEntity());

        ListLayoutEntity testResult = listLayoutService.getStandardLayout();

        Assertions.assertNotNull(testResult);

    }

    @Test
    void testGetDefaultUserLayout() {
        Long userId = 12L;
        Mockito.when(listLayoutRepository.getDefaultUserLayout(userId)).thenReturn(new ListLayoutEntity());

        ListLayoutEntity testResult = listLayoutService.getDefaultUserLayout(userId);

        Assertions.assertNotNull(testResult);
    }

    @Test
    void testGetDefaultUserLayoutLayout_DoesntExist() {
        Long userId = 12L;
        Mockito.when(listLayoutRepository.getDefaultUserLayout(userId)).thenReturn(null);

        ListLayoutEntity testResult = listLayoutService.getDefaultUserLayout(userId);

        Assertions.assertNull(testResult);

    }

    @Test
    void testGetUserListLayout() {
        Long userId = 12L;
        Long listId = 13L;

        Mockito.when(listLayoutRepository.getUserListLayout(userId, listId)).thenReturn(new ListLayoutEntity());

        ListLayoutEntity testResult = listLayoutService.getUserListLayout(userId, listId);

        Assertions.assertNotNull(testResult);
    }

    @Test
    void testGetUserListLayout_DoesntExist() {
        Long userId = 12L;
        Long listId = 13L;

        Mockito.when(listLayoutRepository.getUserListLayout(userId, listId)).thenReturn(null);

        ListLayoutEntity testResult = listLayoutService.getDefaultUserLayout(userId);

        Assertions.assertNull(testResult);
    }

    @Test
    void testAddDefaultUserMappingsAllNew() {
        //new layout - three new mappings
        Long userId = 999L;
        Long categoryTemplateId = 888L;
        String categoryTemplateName = "To be copied everywhere";
        Long layoutId = 777L;
        Long layoutIdForTemplate = 666L;
        Long newCategoryId = 555L;
        List<Long> tagIds = Arrays.asList(123L, 234L, 345L);
        Set<Long> tagIdSet = new HashSet<>(tagIds);

        List<TagEntity> tagEntities = new ArrayList<>();
        tagEntities.add(new TagTestBuilder().withTagId(123L).build());
        tagEntities.add(new TagTestBuilder().withTagId(234L).build());
        tagEntities.add(new TagTestBuilder().withTagId(345L).build());

        ListLayoutEntity newDefault = new ListLayoutEntity(layoutId);
        newDefault.setDefault(true);
        newDefault.setUserId(userId);

        ListLayoutCategoryEntity categoryTemplate = new ListLayoutCategoryEntity(categoryTemplateId);
        categoryTemplate.setLayoutId(layoutIdForTemplate);
        categoryTemplate.setName(categoryTemplateName);

        ListLayoutCategoryEntity newCategory = new ListLayoutCategoryEntity(newCategoryId);
        newCategory.setLayoutId(layoutId);
        newCategory.setName(categoryTemplateName);

        ArgumentCaptor<ListLayoutEntity> layoutCaptor = ArgumentCaptor.forClass(ListLayoutEntity.class);
        ArgumentCaptor<ListLayoutCategoryEntity> categoryCaptor = ArgumentCaptor.forClass(ListLayoutCategoryEntity.class);
        Mockito.when(listLayoutRepository.getDefaultUserLayout(userId)).thenReturn(null);
        Mockito.when(listLayoutRepository.save(layoutCaptor.capture())).thenReturn(newDefault);
        Mockito.when(listLayoutRepository.getTagsToDeleteFromLayout(anyLong(), anySet())).thenReturn(new ArrayList<>());
        Mockito.when(categoryRepositoryRepository.getById(categoryTemplateId)).thenReturn(categoryTemplate);
        Mockito.when(categoryRepositoryRepository.findByNameInLayout(categoryTemplateName, layoutId)).thenReturn(null);
        Mockito.when(categoryRepositoryRepository.save(categoryCaptor.capture())).thenReturn(newCategory);
        Mockito.when(tagRepository.getTagsForIdList(tagIdSet)).thenReturn(tagEntities);

        // call under test
        listLayoutService.addDefaultUserMappings(userId, categoryTemplateId, tagIds);


        // verify captured layout userId and default
        ListLayoutEntity savedLayout = layoutCaptor.getValue();
        Assertions.assertNotNull(savedLayout);
        Assertions.assertEquals(userId, savedLayout.getUserId(), "saved layout should have userId");
        Assertions.assertTrue(savedLayout.getDefault(), "saved layout should have default set to true");

        // verify captured category name and layout id
        ListLayoutCategoryEntity savedCategory = categoryCaptor.getValue();
        Assertions.assertNotNull(savedCategory);
        Assertions.assertEquals(categoryTemplateName, savedCategory.getName(), "saved category should have template name");
        Assertions.assertEquals(layoutId, savedCategory.getLayoutId(), "saved category should have layout id set");

        // Assert that new category has three mapped tags: 123,234, 345
        Set<Long> mappedIds = newCategory.getTags().stream().map(TagEntity::getId).collect(Collectors.toSet());
        Assertions.assertEquals(3, mappedIds.size(), "should be three mapped tags");
        Assertions.assertTrue(mappedIds.containsAll(tagIdSet));
        // Assert that new category has layout id correctly filled in
        Assertions.assertEquals(layoutId, newCategory.getLayoutId(), "layout id should be filled in");
    }

    @Test
    void testAddDefaultUserMappingsNewCategory() {
        // existing layout, new category - three new mappings
        Long userId = 999L;
        Long categoryTemplateId = 888L;
        String categoryTemplateName = "To be copied everywhere";
        Long layoutId = 777L;
        Long layoutIdForTemplate = 666L;
        Long newCategoryId = 555L;
        List<Long> tagIds = Arrays.asList(123L, 234L, 345L);
        Set<Long> tagIdSet = new HashSet<>(tagIds);

        List<TagEntity> tagEntities = new ArrayList<>();
        tagEntities.add(new TagTestBuilder().withTagId(123L).build());
        tagEntities.add(new TagTestBuilder().withTagId(234L).build());
        tagEntities.add(new TagTestBuilder().withTagId(345L).build());

        ListLayoutEntity existingDefault = new ListLayoutEntity(layoutId);
        existingDefault.setDefault(true);
        existingDefault.setUserId(userId);

        ListLayoutCategoryEntity categoryTemplate = new ListLayoutCategoryEntity(categoryTemplateId);
        categoryTemplate.setLayoutId(layoutIdForTemplate);
        categoryTemplate.setName(categoryTemplateName);

        ListLayoutCategoryEntity newCategory = new ListLayoutCategoryEntity(newCategoryId);
        newCategory.setLayoutId(layoutId);
        newCategory.setName(categoryTemplateName);

        ArgumentCaptor<ListLayoutCategoryEntity> categoryCaptor = ArgumentCaptor.forClass(ListLayoutCategoryEntity.class);
        Mockito.when(listLayoutRepository.getDefaultUserLayout(userId)).thenReturn(existingDefault);
        Mockito.when(listLayoutRepository.getTagsToDeleteFromLayout(anyLong(), anySet())).thenReturn(new ArrayList<>());
        Mockito.when(categoryRepositoryRepository.getById(categoryTemplateId)).thenReturn(categoryTemplate);
        Mockito.when(categoryRepositoryRepository.findByNameInLayout(categoryTemplateName, layoutId)).thenReturn(null);
        Mockito.when(categoryRepositoryRepository.save(categoryCaptor.capture())).thenReturn(newCategory);
        Mockito.when(tagRepository.getTagsForIdList(tagIdSet)).thenReturn(tagEntities);

        // call under test
        listLayoutService.addDefaultUserMappings(userId, categoryTemplateId, tagIds);

        // verify captured category name and layout id
        ListLayoutCategoryEntity savedCategory = categoryCaptor.getValue();
        Assertions.assertNotNull(savedCategory);
        Assertions.assertEquals(categoryTemplateName, savedCategory.getName(), "saved category should have template name");
        Assertions.assertEquals(layoutId, savedCategory.getLayoutId(), "saved category should have layout id set");

        // Assert that new category has three mapped tags: 123,234, 345
        Set<Long> mappedIds = newCategory.getTags().stream().map(TagEntity::getId).collect(Collectors.toSet());
        Assertions.assertEquals(3, mappedIds.size(), "should be three mapped tags");
        Assertions.assertTrue(mappedIds.containsAll(tagIdSet));
        // Assert that new category has layout id correctly filled in
        Assertions.assertEquals(layoutId, newCategory.getLayoutId(), "layout id should be filled in");

    }

    @Test
    void testAddDefaultUserMappingsNewMappings() {
        // existing layout, existing category - three new mappings
        Long userId = 999L;
        Long categoryTemplateId = 888L;
        String categoryTemplateName = "To be copied everywhere";
        Long layoutId = 777L;
        Long layoutIdForTemplate = 666L;
        Long newCategoryId = 555L;
        List<Long> tagIds = Arrays.asList(123L, 234L, 345L);
        Set<Long> tagIdSet = new HashSet<>(tagIds);

        List<TagEntity> tagEntities = new ArrayList<>();
        tagEntities.add(new TagTestBuilder().withTagId(123L).build());
        tagEntities.add(new TagTestBuilder().withTagId(234L).build());
        tagEntities.add(new TagTestBuilder().withTagId(345L).build());

        ListLayoutEntity existingDefault = new ListLayoutEntity(layoutId);
        existingDefault.setDefault(true);
        existingDefault.setUserId(userId);

        ListLayoutCategoryEntity categoryTemplate = new ListLayoutCategoryEntity(categoryTemplateId);
        categoryTemplate.setLayoutId(layoutIdForTemplate);
        categoryTemplate.setName(categoryTemplateName);

        ListLayoutCategoryEntity existingCategory = new ListLayoutCategoryEntity(newCategoryId);
        existingCategory.setLayoutId(layoutId);
        existingCategory.setName(categoryTemplateName);

        Mockito.when(listLayoutRepository.getDefaultUserLayout(userId)).thenReturn(existingDefault);
        Mockito.when(listLayoutRepository.getTagsToDeleteFromLayout(anyLong(), anySet())).thenReturn(new ArrayList<>());
        Mockito.when(categoryRepositoryRepository.getById(categoryTemplateId)).thenReturn(categoryTemplate);
        Mockito.when(categoryRepositoryRepository.findByNameInLayout(categoryTemplateName, layoutId)).thenReturn(existingCategory);
        Mockito.when(tagRepository.getTagsForIdList(tagIdSet)).thenReturn(tagEntities);

        // call under test
        listLayoutService.addDefaultUserMappings(userId, categoryTemplateId, tagIds);

        // Assert that new category has three mapped tags: 123,234, 345
        Set<Long> mappedIds = existingCategory.getTags().stream().map(TagEntity::getId).collect(Collectors.toSet());
        Assertions.assertEquals(3, mappedIds.size(), "should be three mapped tags");
        Assertions.assertTrue(mappedIds.containsAll(tagIdSet));
        // Assert that new category has layout id correctly filled in
        Assertions.assertEquals(layoutId, existingCategory.getLayoutId(), "layout id should be filled in");

    }

    @Test
    void testAddDefaultUserMappingsExistingMappings() {
        // existing layout, existing category, mappings in other category
        //new layout - three new mappings
        Long userId = 999L;
        Long categoryTemplateId = 888L;
        String categoryTemplateName = "To be copied everywhere";
        Long layoutId = 777L;
        Long layoutIdForTemplate = 666L;
        Long newCategoryId = 555L;
        List<Long> tagIds = Arrays.asList(123L, 234L, 345L);
        Set<Long> tagIdSet = new HashSet<>(tagIds);

        List<TagEntity> tagEntities = new ArrayList<>();
        tagEntities.add(new TagTestBuilder().withTagId(123L).build());
        tagEntities.add(new TagTestBuilder().withTagId(234L).build());
        tagEntities.add(new TagTestBuilder().withTagId(345L).build());

        ListLayoutEntity existingLayout = new ListLayoutEntity(layoutId);
        existingLayout.setDefault(true);
        existingLayout.setUserId(userId);

        ListLayoutCategoryEntity originalMappedCategory = new ListLayoutCategoryEntity(12L);
        originalMappedCategory.setLayoutId(layoutIdForTemplate);
        originalMappedCategory.setName("original");
        List<TagEntity> mappedToOtherCategory = new ArrayList<>(tagEntities);
        mappedToOtherCategory.forEach(t -> t.addCategory(originalMappedCategory));

        ListLayoutCategoryEntity categoryTemplate = new ListLayoutCategoryEntity(categoryTemplateId);
        categoryTemplate.setLayoutId(layoutIdForTemplate);
        categoryTemplate.setName(categoryTemplateName);

        ListLayoutCategoryEntity existingCategory = new ListLayoutCategoryEntity(newCategoryId);
        existingCategory.setLayoutId(layoutId);
        existingCategory.setName(categoryTemplateName);

        Mockito.when(listLayoutRepository.getDefaultUserLayout(userId)).thenReturn(existingLayout);
        Mockito.when(listLayoutRepository.getTagsToDeleteFromLayout(anyLong(), anySet())).thenReturn(mappedToOtherCategory);
        Mockito.when(categoryRepositoryRepository.getById(categoryTemplateId)).thenReturn(categoryTemplate);
        Mockito.when(categoryRepositoryRepository.findByNameInLayout(categoryTemplateName, layoutId)).thenReturn(existingCategory);
        Mockito.when(tagRepository.getTagsForIdList(tagIdSet)).thenReturn(tagEntities);

        // call under test
        listLayoutService.addDefaultUserMappings(userId, categoryTemplateId, tagIds);

        // Assert that new category has three mapped tags: 123,234, 345
        Set<Long> mappedIds = existingCategory.getTags().stream().map(TagEntity::getId).collect(Collectors.toSet());
        Assertions.assertEquals(3, mappedIds.size(), "should be three mapped tags");
        Assertions.assertTrue(mappedIds.containsAll(tagIdSet));
        // Assert that new category has layout id correctly filled in
        Assertions.assertEquals(layoutId, existingCategory.getLayoutId(), "layout id should be filled in");
        Assertions.assertEquals(categoryTemplateName, existingCategory.getName(), "name is correct");
    }

    @Test
    void testAddDefaultUserMappingsExistingMappingsNewCategory() {
        // existing layout, new category, mappings in other category
        // existing layout, existing category, mappings in other category
        //new layout - three new mappings
        Long userId = 999L;
        Long categoryTemplateId = 888L;
        String categoryTemplateName = "To be copied everywhere";
        Long layoutId = 777L;
        Long layoutIdForTemplate = 666L;
        Long newCategoryId = 555L;
        List<Long> tagIds = Arrays.asList(123L, 234L, 345L);
        Set<Long> tagIdSet = new HashSet<>(tagIds);

        List<TagEntity> tagEntities = new ArrayList<>();
        tagEntities.add(new TagTestBuilder().withTagId(123L).build());
        tagEntities.add(new TagTestBuilder().withTagId(234L).build());
        tagEntities.add(new TagTestBuilder().withTagId(345L).build());

        ListLayoutEntity existingLayout = new ListLayoutEntity(layoutId);
        existingLayout.setDefault(true);
        existingLayout.setUserId(userId);

        ListLayoutCategoryEntity originalMappedCategory = new ListLayoutCategoryEntity(12L);
        originalMappedCategory.setLayoutId(layoutIdForTemplate);
        originalMappedCategory.setName("original");
        List<TagEntity> mappedToOtherCategory = new ArrayList<>(tagEntities);
        mappedToOtherCategory.forEach(t -> t.addCategory(originalMappedCategory));

        ListLayoutCategoryEntity categoryTemplate = new ListLayoutCategoryEntity(categoryTemplateId);
        categoryTemplate.setLayoutId(layoutIdForTemplate);
        categoryTemplate.setName(categoryTemplateName);

        ListLayoutCategoryEntity newCategory = new ListLayoutCategoryEntity(newCategoryId);
        newCategory.setLayoutId(layoutId);
        newCategory.setName(categoryTemplateName);

        ArgumentCaptor<ListLayoutCategoryEntity> categoryCaptor = ArgumentCaptor.forClass(ListLayoutCategoryEntity.class);
        Mockito.when(listLayoutRepository.getDefaultUserLayout(userId)).thenReturn(existingLayout);
        Mockito.when(listLayoutRepository.getTagsToDeleteFromLayout(anyLong(), anySet())).thenReturn(mappedToOtherCategory);
        Mockito.when(categoryRepositoryRepository.getById(categoryTemplateId)).thenReturn(categoryTemplate);
        Mockito.when(categoryRepositoryRepository.findByNameInLayout(categoryTemplateName, layoutId)).thenReturn(null);
        Mockito.when(categoryRepositoryRepository.save(categoryCaptor.capture())).thenReturn(newCategory);
        Mockito.when(tagRepository.getTagsForIdList(tagIdSet)).thenReturn(tagEntities);

        // call under test
        listLayoutService.addDefaultUserMappings(userId, categoryTemplateId, tagIds);


        // verify captured category name and layout id
        ListLayoutCategoryEntity savedCategory = categoryCaptor.getValue();
        Assertions.assertNotNull(savedCategory);
        Assertions.assertEquals(categoryTemplateName, savedCategory.getName(), "saved category should have template name");
        Assertions.assertEquals(layoutId, savedCategory.getLayoutId(), "saved category should have layout id set");
        // Assert that new category has three mapped tags: 123,234, 345
        Set<Long> mappedIds = newCategory.getTags().stream().map(TagEntity::getId).collect(Collectors.toSet());
        Assertions.assertEquals(3, mappedIds.size(), "should be three mapped tags");
        Assertions.assertTrue(mappedIds.containsAll(tagIdSet));
        // Assert that new category has layout id correctly filled in
        Assertions.assertEquals(layoutId, newCategory.getLayoutId(), "layout id should be filled in");
        Assertions.assertEquals(categoryTemplateName, newCategory.getName(), "name is correct");

    }

    @Test
    void testAddDefaultUserMappingsBadTagId() {
        // non existant tag ids
        Long userId = 999L;
        Long categoryTemplateId = 888L;
        String categoryTemplateName = "To be copied everywhere";
        Long layoutId = 777L;
        Long layoutIdForTemplate = 666L;
        Long newCategoryId = 555L;
        List<Long> tagIds = Arrays.asList(123L, 234L, 345L);
        Set<Long> tagIdSet = new HashSet<>(tagIds);

        ListLayoutEntity existingLayout = new ListLayoutEntity(layoutId);
        existingLayout.setDefault(true);
        existingLayout.setUserId(userId);

        ListLayoutCategoryEntity categoryTemplate = new ListLayoutCategoryEntity(categoryTemplateId);
        categoryTemplate.setLayoutId(layoutIdForTemplate);
        categoryTemplate.setName(categoryTemplateName);

        ListLayoutCategoryEntity existingCategory = new ListLayoutCategoryEntity(newCategoryId);
        existingCategory.setLayoutId(layoutId);
        existingCategory.setName(categoryTemplateName);

        Mockito.when(listLayoutRepository.getDefaultUserLayout(userId)).thenReturn(existingLayout);
        Mockito.when(listLayoutRepository.getTagsToDeleteFromLayout(anyLong(), anySet())).thenReturn(new ArrayList<>());
        Mockito.when(categoryRepositoryRepository.getById(categoryTemplateId)).thenReturn(categoryTemplate);
        Mockito.when(categoryRepositoryRepository.findByNameInLayout(categoryTemplateName, layoutId)).thenReturn(existingCategory);
        Mockito.when(tagRepository.getTagsForIdList(tagIdSet)).thenReturn(new ArrayList<>());

        // call under test
        listLayoutService.addDefaultUserMappings(userId, categoryTemplateId, tagIds);

        // no error or exception thrown
    }

    @Test
    void testAddDefaultUserMappingsTagIdsRepeated() {
        // duplicate tag ids
        Long userId = 999L;
        Long categoryTemplateId = 888L;
        String categoryTemplateName = "To be copied everywhere";
        Long layoutId = 777L;
        Long layoutIdForTemplate = 666L;
        Long newCategoryId = 555L;
        List<Long> tagIds = Arrays.asList(123L, 234L, 345L,123L, 234L, 345L,123L, 234L, 345L,123L, 234L, 345L,123L, 234L, 345L);
        Set<Long> tagIdSet = new HashSet<>(tagIds);

        List<TagEntity> tagEntities = new ArrayList<>();
        tagEntities.add(new TagTestBuilder().withTagId(123L).build());
        tagEntities.add(new TagTestBuilder().withTagId(234L).build());
        tagEntities.add(new TagTestBuilder().withTagId(345L).build());

        ListLayoutEntity newDefault = new ListLayoutEntity(layoutId);
        newDefault.setDefault(true);
        newDefault.setUserId(userId);

        ListLayoutCategoryEntity categoryTemplate = new ListLayoutCategoryEntity(categoryTemplateId);
        categoryTemplate.setLayoutId(layoutIdForTemplate);
        categoryTemplate.setName(categoryTemplateName);

        ListLayoutCategoryEntity newCategory = new ListLayoutCategoryEntity(newCategoryId);
        newCategory.setLayoutId(layoutId);
        newCategory.setName(categoryTemplateName);

        ArgumentCaptor<ListLayoutEntity> layoutCaptor = ArgumentCaptor.forClass(ListLayoutEntity.class);
        ArgumentCaptor<ListLayoutCategoryEntity> categoryCaptor = ArgumentCaptor.forClass(ListLayoutCategoryEntity.class);
        Mockito.when(listLayoutRepository.getDefaultUserLayout(userId)).thenReturn(null);
        Mockito.when(listLayoutRepository.save(layoutCaptor.capture())).thenReturn(newDefault);
        Mockito.when(listLayoutRepository.getTagsToDeleteFromLayout(anyLong(), anySet())).thenReturn(new ArrayList<>());
        Mockito.when(categoryRepositoryRepository.getById(categoryTemplateId)).thenReturn(categoryTemplate);
        Mockito.when(categoryRepositoryRepository.findByNameInLayout(categoryTemplateName, layoutId)).thenReturn(null);
        Mockito.when(categoryRepositoryRepository.save(categoryCaptor.capture())).thenReturn(newCategory);
        Mockito.when(tagRepository.getTagsForIdList(tagIdSet)).thenReturn(tagEntities);

        // call under test
        listLayoutService.addDefaultUserMappings(userId, categoryTemplateId, tagIds);


        // verify captured layout userId and default
        ListLayoutEntity savedLayout = layoutCaptor.getValue();
        Assertions.assertNotNull(savedLayout);
        Assertions.assertEquals(userId, savedLayout.getUserId(), "saved layout should have userId");
        Assertions.assertTrue(savedLayout.getDefault(), "saved layout should have default set to true");

        // verify captured category name and layout id
        ListLayoutCategoryEntity savedCategory = categoryCaptor.getValue();
        Assertions.assertNotNull(savedCategory);
        Assertions.assertEquals(categoryTemplateName, savedCategory.getName(), "saved category should have template name");
        Assertions.assertEquals(layoutId, savedCategory.getLayoutId(), "saved category should have layout id set");

        // Assert that new category has three mapped tags: 123,234, 345
        Set<Long> mappedIds = newCategory.getTags().stream().map(TagEntity::getId).collect(Collectors.toSet());
        Assertions.assertEquals(3, mappedIds.size(), "should be three mapped tags");
        Assertions.assertTrue(mappedIds.containsAll(tagIdSet));
        // Assert that new category has layout id correctly filled in
        Assertions.assertEquals(layoutId, newCategory.getLayoutId(), "layout id should be filled in");

    }

    @Test
    void testAddDefaultUserMappingsBadCategoryId() {
        // non existant template category
            Long userId = 999L;
            Long categoryTemplateId = 888L;
            String categoryTemplateName = "To be copied everywhere";
            Long layoutId = 777L;
            Long layoutIdForTemplate = 666L;
            Long newCategoryId = 555L;
            List<Long> tagIds = Arrays.asList(123L, 234L, 345L);
            Set<Long> tagIdSet = new HashSet<>(tagIds);

            List<TagEntity> tagEntities = new ArrayList<>();
            tagEntities.add(new TagTestBuilder().withTagId(123L).build());
            tagEntities.add(new TagTestBuilder().withTagId(234L).build());
            tagEntities.add(new TagTestBuilder().withTagId(345L).build());

            ListLayoutEntity newDefault = new ListLayoutEntity(layoutId);
            newDefault.setDefault(true);
            newDefault.setUserId(userId);

            ListLayoutCategoryEntity categoryTemplate = new ListLayoutCategoryEntity(categoryTemplateId);
            categoryTemplate.setLayoutId(layoutIdForTemplate);
            categoryTemplate.setName(categoryTemplateName);

            ListLayoutCategoryEntity newCategory = new ListLayoutCategoryEntity(newCategoryId);
            newCategory.setLayoutId(layoutId);
            newCategory.setName(categoryTemplateName);

            ArgumentCaptor<ListLayoutEntity> layoutCaptor = ArgumentCaptor.forClass(ListLayoutEntity.class);
            ArgumentCaptor<ListLayoutCategoryEntity> categoryCaptor = ArgumentCaptor.forClass(ListLayoutCategoryEntity.class);
            Mockito.when(listLayoutRepository.getDefaultUserLayout(userId)).thenReturn(null);
            Mockito.when(listLayoutRepository.save(layoutCaptor.capture())).thenReturn(newDefault);
            Mockito.when(listLayoutRepository.getTagsToDeleteFromLayout(anyLong(), anySet())).thenReturn(new ArrayList<>());
            Mockito.when(categoryRepositoryRepository.getById(categoryTemplateId)).thenReturn(null);
            Mockito.when(categoryRepositoryRepository.findByNameInLayout(categoryTemplateName, layoutId)).thenReturn(null);
            Mockito.when(categoryRepositoryRepository.save(categoryCaptor.capture())).thenReturn(newCategory);
            Mockito.when(tagRepository.getTagsForIdList(tagIdSet)).thenReturn(tagEntities);

            // call under test
            boolean exceptionThrown = false;
            try {
                listLayoutService.addDefaultUserMappings(userId, categoryTemplateId, tagIds);
            } catch (ObjectNotFoundException e) {
                exceptionThrown = true;
            }

            Assertions.assertTrue(exceptionThrown);

            // verify captured layout userId and default - default layout created even though mappings werent completed.
            ListLayoutEntity savedLayout = layoutCaptor.getValue();
            Assertions.assertNotNull(savedLayout);
            Assertions.assertEquals(userId, savedLayout.getUserId(), "saved layout should have userId");
            Assertions.assertTrue(savedLayout.getDefault(), "saved layout should have default set to true");


    }
}