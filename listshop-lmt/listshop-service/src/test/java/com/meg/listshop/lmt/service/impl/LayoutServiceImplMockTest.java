package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private UserService userService;

    @MockBean
    private TagRepository tagRepository;

    @BeforeEach
    void setUp() {
        listLayoutService = new LayoutServiceImpl(listLayoutRepository, categoryRepositoryRepository, tagRepository, userService);
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
        assertEquals(userId, savedLayout.getUserId(), "saved layout should have userId");
        Assertions.assertTrue(savedLayout.getDefault(), "saved layout should have default set to true");

        // verify captured category name and layout id
        ListLayoutCategoryEntity savedCategory = categoryCaptor.getValue();
        Assertions.assertNotNull(savedCategory);
        assertEquals(categoryTemplateName, savedCategory.getName(), "saved category should have template name");
        assertEquals(layoutId, savedCategory.getLayoutId(), "saved category should have layout id set");

        // Assert that new category has three mapped tags: 123,234, 345
        Set<Long> mappedIds = newCategory.getTags().stream().map(TagEntity::getId).collect(Collectors.toSet());
        assertEquals(3, mappedIds.size(), "should be three mapped tags");
        Assertions.assertTrue(mappedIds.containsAll(tagIdSet));
        // Assert that new category has layout id correctly filled in
        assertEquals(layoutId, newCategory.getLayoutId(), "layout id should be filled in");
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
        assertEquals(categoryTemplateName, savedCategory.getName(), "saved category should have template name");
        assertEquals(layoutId, savedCategory.getLayoutId(), "saved category should have layout id set");

        // Assert that new category has three mapped tags: 123,234, 345
        Set<Long> mappedIds = newCategory.getTags().stream().map(TagEntity::getId).collect(Collectors.toSet());
        assertEquals(3, mappedIds.size(), "should be three mapped tags");
        Assertions.assertTrue(mappedIds.containsAll(tagIdSet));
        // Assert that new category has layout id correctly filled in
        assertEquals(layoutId, newCategory.getLayoutId(), "layout id should be filled in");

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
        assertEquals(3, mappedIds.size(), "should be three mapped tags");
        Assertions.assertTrue(mappedIds.containsAll(tagIdSet));
        // Assert that new category has layout id correctly filled in
        assertEquals(layoutId, existingCategory.getLayoutId(), "layout id should be filled in");

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
        assertEquals(3, mappedIds.size(), "should be three mapped tags");
        Assertions.assertTrue(mappedIds.containsAll(tagIdSet));
        // Assert that new category has layout id correctly filled in
        assertEquals(layoutId, existingCategory.getLayoutId(), "layout id should be filled in");
        assertEquals(categoryTemplateName, existingCategory.getName(), "name is correct");
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
        assertEquals(categoryTemplateName, savedCategory.getName(), "saved category should have template name");
        assertEquals(layoutId, savedCategory.getLayoutId(), "saved category should have layout id set");
        // Assert that new category has three mapped tags: 123,234, 345
        Set<Long> mappedIds = newCategory.getTags().stream().map(TagEntity::getId).collect(Collectors.toSet());
        assertEquals(3, mappedIds.size(), "should be three mapped tags");
        Assertions.assertTrue(mappedIds.containsAll(tagIdSet));
        // Assert that new category has layout id correctly filled in
        assertEquals(layoutId, newCategory.getLayoutId(), "layout id should be filled in");
        assertEquals(categoryTemplateName, newCategory.getName(), "name is correct");

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
        List<Long> tagIds = Arrays.asList(123L, 234L, 345L, 123L, 234L, 345L, 123L, 234L, 345L, 123L, 234L, 345L, 123L, 234L, 345L);
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
        assertEquals(userId, savedLayout.getUserId(), "saved layout should have userId");
        Assertions.assertTrue(savedLayout.getDefault(), "saved layout should have default set to true");

        // verify captured category name and layout id
        ListLayoutCategoryEntity savedCategory = categoryCaptor.getValue();
        Assertions.assertNotNull(savedCategory);
        assertEquals(categoryTemplateName, savedCategory.getName(), "saved category should have template name");
        assertEquals(layoutId, savedCategory.getLayoutId(), "saved category should have layout id set");

        // Assert that new category has three mapped tags: 123,234, 345
        Set<Long> mappedIds = newCategory.getTags().stream().map(TagEntity::getId).collect(Collectors.toSet());
        assertEquals(3, mappedIds.size(), "should be three mapped tags");
        Assertions.assertTrue(mappedIds.containsAll(tagIdSet));
        // Assert that new category has layout id correctly filled in
        assertEquals(layoutId, newCategory.getLayoutId(), "layout id should be filled in");

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
        assertEquals(userId, savedLayout.getUserId(), "saved layout should have userId");
        Assertions.assertTrue(savedLayout.getDefault(), "saved layout should have default set to true");
    }

    @Test
    void testGetUserCategories() {
        // standard - user layout exists
        String userName = "testUserName";
        Long userId = 99L;
        Long userLayoutId = 109L;
        Long defaultLayoutId = 209L;

        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setUsername(userName);

        ListLayoutCategoryEntity userCategoryOne = buildCategory(1L, "aardvark");
        ListLayoutCategoryEntity userCategoryTwo = buildCategory(2L, "Badger");
        ListLayoutCategoryEntity userCategoryThree = buildCategory(3L, "capybera");
        ListLayoutEntity layout = new ListLayoutEntity(userLayoutId);
        Set<ListLayoutCategoryEntity> userCategories = new HashSet<>(Arrays.asList(userCategoryOne, userCategoryTwo, userCategoryThree));
        layout.setCategories(userCategories);

        ListLayoutCategoryEntity defaultCategoryOne = buildCategory(4L, "Ant");
        ListLayoutCategoryEntity defaultCategoryTwo = buildCategory(5L, "bee");
        ListLayoutCategoryEntity defaultCategoryThree = buildCategory(6L, "Caterpillar");
        ListLayoutEntity defaultLayout = new ListLayoutEntity(defaultLayoutId);
        Set<ListLayoutCategoryEntity> defaultCategories = new HashSet<>(Arrays.asList(defaultCategoryOne, defaultCategoryTwo, defaultCategoryThree));
        defaultLayout.setCategories(defaultCategories);

        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(user);
        Mockito.when(listLayoutRepository.getDefaultUserLayout(userId)).thenReturn(layout);
        Mockito.when(listLayoutRepository.getStandardLayout()).thenReturn(defaultLayout);

        // service call
        List<ListLayoutCategoryEntity> result = listLayoutService.getUserCategories(userName);

        Assertions.assertNotNull(result);
        assertEquals(6, result.size(), "6 results returned");
        // verify order
        assertEquals("aardvark", result.get(0).getName(), "name matches");
        assertEquals("Badger", result.get(1).getName(), "name matches");
        assertEquals("capybera", result.get(2).getName(), "name matches");
        assertEquals("Ant", result.get(3).getName(), "name matches");
        assertEquals("bee", result.get(4).getName(), "name matches");
        assertEquals("Caterpillar", result.get(5).getName(), "name matches");
    }

    @Test
    void testGetUserCategoriesNoUserDefault() {
        // standard - user layout exists
        String userName = "testUserName";
        Long userId = 99L;
        Long defaultLayoutId = 209L;

        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setUsername(userName);

        ListLayoutCategoryEntity defaultCategoryOne = buildCategory(4L, "Ant");
        ListLayoutCategoryEntity defaultCategoryTwo = buildCategory(5L, "bee");
        ListLayoutCategoryEntity defaultCategoryThree = buildCategory(6L, "Caterpillar");
        ListLayoutEntity defaultLayout = new ListLayoutEntity(defaultLayoutId);
        Set<ListLayoutCategoryEntity> defaultCategories = new HashSet<>(Arrays.asList(defaultCategoryOne, defaultCategoryTwo, defaultCategoryThree));
        defaultLayout.setCategories(defaultCategories);

        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(user);
        Mockito.when(listLayoutRepository.getDefaultUserLayout(userId)).thenReturn(null);
        Mockito.when(listLayoutRepository.getStandardLayout()).thenReturn(defaultLayout);

        // service call
        List<ListLayoutCategoryEntity> result = listLayoutService.getUserCategories(userName);

        Assertions.assertNotNull(result);
        assertEquals(3, result.size(), "3 results returned");
        // verify order
        assertEquals("Ant", result.get(0).getName(), "name matches");
        assertEquals("bee", result.get(1).getName(), "name matches");
        assertEquals("Caterpillar", result.get(2).getName(), "name matches");
    }

    @Test
    void testGetUserCategoriesOverlap() {
        // standard - user layout exists and duplicates categories in default
        String userName = "testUserName";
        Long userId = 99L;
        Long userLayoutId = 109L;
        Long defaultLayoutId = 209L;

        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setUsername(userName);

        ListLayoutCategoryEntity userCategoryOne = buildCategory(1L, "aardvark");
        ListLayoutCategoryEntity userCategoryTwo = buildCategory(2L, "Badger");
        ListLayoutCategoryEntity userCategoryThree = buildCategory(3L, "capybera");
        ListLayoutEntity layout = new ListLayoutEntity(userLayoutId);
        Set<ListLayoutCategoryEntity> userCategories = new HashSet<>(Arrays.asList(userCategoryOne, userCategoryTwo, userCategoryThree));
        layout.setCategories(userCategories);

        ListLayoutCategoryEntity defaultCategoryOne = buildCategory(4L, "Ant");
        ListLayoutCategoryEntity defaultCategoryTwo = buildCategory(5L, "Badger");
        ListLayoutCategoryEntity defaultCategoryThree = buildCategory(6L, "Capybera");
        ListLayoutEntity defaultLayout = new ListLayoutEntity(defaultLayoutId);
        Set<ListLayoutCategoryEntity> defaultCategories = new HashSet<>(Arrays.asList(defaultCategoryOne, defaultCategoryTwo, defaultCategoryThree));
        defaultLayout.setCategories(defaultCategories);

        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(user);
        Mockito.when(listLayoutRepository.getDefaultUserLayout(userId)).thenReturn(layout);
        Mockito.when(listLayoutRepository.getStandardLayout()).thenReturn(defaultLayout);

        // service call
        List<ListLayoutCategoryEntity> result = listLayoutService.getUserCategories(userName);

        Assertions.assertNotNull(result);
        assertEquals(4, result.size(), "4 results returned");
        // verify order
        assertEquals("aardvark", result.get(0).getName(), "name matches");
        assertEquals("Badger", result.get(1).getName(), "name matches");
        assertEquals("capybera", result.get(2).getName(), "name matches");
        assertEquals("Ant", result.get(3).getName(), "name matches");
        // verify ids
        Set<Long> categoryIds = result.stream().map(ListLayoutCategoryEntity::getId).collect(Collectors.toSet());
        Assertions.assertTrue(categoryIds.contains(1L));
        Assertions.assertTrue(categoryIds.contains(2L));
        Assertions.assertTrue(categoryIds.contains(3L));
        Assertions.assertTrue(categoryIds.contains(4L));
    }


    @Test
    void testAssignDefaultCategoryToTag_NoCategoryFound() {
        Long idToAssign = null;
        Long tagToAssignId = 99L;
        Long defaultId = 9999L;
        TagEntity tagToWhichToAssign = new TagEntity(tagToAssignId);
        TagEntity siblingTag1 = new TagEntity(1L);
        TagEntity siblingTag2 = new TagEntity(2L);
        TagEntity siblingTag3 = new TagEntity(3L);
        List<TagEntity> siblings = Arrays.asList(siblingTag1, siblingTag2, siblingTag3);
        Set<Long> siblingIds = siblings.stream().map(TagEntity::getId).collect(Collectors.toSet());
        ListLayoutCategoryEntity categoryEntity = new ListLayoutCategoryEntity(idToAssign);

        Mockito.when(listLayoutRepository.getDefaultCategoryForSiblings(siblingIds)).thenReturn(idToAssign);
        Mockito.when(categoryRepositoryRepository.getDefaultCategoryId()).thenReturn(defaultId);
        Mockito.when(categoryRepositoryRepository.findById(defaultId)).thenReturn(Optional.of(categoryEntity));
        // service call
        listLayoutService.assignDefaultCategoryToTag(siblings, tagToWhichToAssign);

        // assertions
        Assertions.assertNotNull(categoryEntity.getTags());
        Assertions.assertEquals(1,categoryEntity.getTags().size());
        Optional<TagEntity> assigned = categoryEntity.getTags().stream().findFirst();
        Assertions.assertTrue(assigned.isPresent());
        Assertions.assertEquals(assigned.get().getId(), tagToAssignId);
    }

    @Test
        //MM also needs controller test
    void testAssignUserDefaultCategoriesToTag() {
        Long userId = 1001L;
        Long category1Id = 10011L;
        Long category2Id = 10012L;
        Long category3Id = 10013L;
        Set idsToAssign = new HashSet<>(Arrays.asList(category3Id, category2Id, category1Id));

        Long tagToAssignId = 99L;
        TagEntity tagToWhichToAssign = new TagEntity(tagToAssignId);
        tagToWhichToAssign.setUserId(userId);
        TagEntity siblingTag1 = new TagEntity(1L);
        TagEntity siblingTag2 = new TagEntity(2L);
        TagEntity siblingTag3 = new TagEntity(3L);
        List<TagEntity> siblings = Arrays.asList(siblingTag1, siblingTag2, siblingTag3);
        Set<Long> siblingIds = siblings.stream().map(TagEntity::getId).collect(Collectors.toSet());
        ListLayoutCategoryEntity categoryEntity1 = new ListLayoutCategoryEntity(category1Id);
        ListLayoutCategoryEntity categoryEntity2 = new ListLayoutCategoryEntity(category2Id);
        ListLayoutCategoryEntity categoryEntity3 = new ListLayoutCategoryEntity(category3Id);

        Mockito.when(listLayoutRepository.getUserCategoriesForSiblings(userId, siblingIds)).thenReturn(idsToAssign);
        Mockito.when(categoryRepositoryRepository.getByIds(idsToAssign)).thenReturn(Arrays.asList(categoryEntity1, categoryEntity2, categoryEntity3));
        // service call
        listLayoutService.assignUserDefaultCategoriesToTag(siblings, tagToWhichToAssign);

        // assertions
        for (ListLayoutCategoryEntity categoryEntity : Arrays.asList(categoryEntity1, categoryEntity2, categoryEntity3)) {
            Assertions.assertNotNull(categoryEntity.getTags());
            Assertions.assertEquals(1,categoryEntity.getTags().size());
            Optional<TagEntity> assigned = categoryEntity.getTags().stream().findFirst();
            Assertions.assertTrue(assigned.isPresent());
            Assertions.assertEquals(assigned.get().getId(), tagToAssignId);

        }
    }

    @Test
    void testAssignUserDefaultCategoriesToTag_NoCategoryFound() {
        Long idToAssign = null;
        Long tagToAssignId = 99L;
        Long defaultId = 9999L;
        TagEntity tagToWhichToAssign = new TagEntity(tagToAssignId);
        TagEntity siblingTag1 = new TagEntity(1L);
        TagEntity siblingTag2 = new TagEntity(2L);
        TagEntity siblingTag3 = new TagEntity(3L);
        List<TagEntity> siblings = Arrays.asList(siblingTag1, siblingTag2, siblingTag3);
        Set<Long> siblingIds = siblings.stream().map(TagEntity::getId).collect(Collectors.toSet());
        ListLayoutCategoryEntity categoryEntity = new ListLayoutCategoryEntity(idToAssign);

        Mockito.when(listLayoutRepository.getDefaultCategoryForSiblings(siblingIds)).thenReturn(idToAssign);
        Mockito.when(categoryRepositoryRepository.getDefaultCategoryId()).thenReturn(defaultId);
        Mockito.when(categoryRepositoryRepository.findById(defaultId)).thenReturn(Optional.of(categoryEntity));
        // service call
        listLayoutService.assignDefaultCategoryToTag(siblings, tagToWhichToAssign);

        // assertions
        Assertions.assertNotNull(categoryEntity.getTags());
        Assertions.assertEquals(1, categoryEntity.getTags().size());
        Optional<TagEntity> assigned = categoryEntity.getTags().stream().findFirst();
        Assertions.assertTrue(assigned.isPresent());
        Assertions.assertEquals(assigned.get().getId(), tagToAssignId);
    }


    @Test
    void testAddTagToCategory() {
        // variants - no category found, tag already exists

        Long tagId = 99L;
        Long categoryId = 109L;
        TagEntity tag = new TagEntity(tagId);
        ListLayoutCategoryEntity category = new ListLayoutCategoryEntity(categoryId);


        Mockito.when(categoryRepositoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // service call
        listLayoutService.addTagToCategory(categoryId, tag);

        // assertions - tag has category, and category has tag
        Assertions.assertFalse(tag.getCategories().isEmpty());
        Assertions.assertEquals(tag.getCategories().get(0).getId(), categoryId);
        Assertions.assertFalse(category.getTags().isEmpty());
        Optional<TagEntity> result = category.getTags().stream().findFirst();
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(result.get().getId(), tagId);
    }

    @Test
    void testAddTagToCategory_CategoryNotFound_KO() {
        Long tagId = 99L;
        Long categoryId = 109L;
        TagEntity tag = new TagEntity(tagId);


        Mockito.when(categoryRepositoryRepository.findById(categoryId)).thenReturn(Optional.ofNullable(null));

        // service call
        listLayoutService.addTagToCategory(categoryId, tag);

        // assertions - tag has category, and category has tag
        Assertions.assertTrue(tag.getCategories().isEmpty());

    }

    @Test
    void testAddTagToCategory_TagAlreadyPresent_KO() {
        Long tagId = 99L;
        Long categoryId = 109L;
        TagEntity tag = new TagEntity(tagId);
        ListLayoutCategoryEntity category = new ListLayoutCategoryEntity(categoryId);
        category.addTag(tag);

        Mockito.when(categoryRepositoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // service call
        listLayoutService.addTagToCategory(categoryId, tag);

        // assertions - tag has category, and category has tag
        Optional<TagEntity> result = category.getTags().stream().findFirst();
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(result.get().getId(), tagId);
    }

    private ListLayoutCategoryEntity buildCategory(Long categoryId, String categoryName) {
        ListLayoutCategoryEntity category = new ListLayoutCategoryEntity(categoryId);
        category.setName(categoryName);
        return category;
    }


}