package com.meg.atable.service.impl;

import com.meg.atable.Application;
import com.meg.atable.api.model.ListLayoutType;
import com.meg.atable.api.model.TagType;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.data.entity.ListLayoutCategoryEntity;
import com.meg.atable.data.entity.ListLayoutEntity;
import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.data.repository.ListLayoutCategoryRepository;
import com.meg.atable.data.repository.ListLayoutRepository;
import com.meg.atable.data.repository.TagRepository;
import com.meg.atable.service.ListLayoutService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class ListLayoutServiceImplTest {
    @Autowired
    private ListLayoutService listLayoutService;

    @Autowired
    private ListLayoutRepository listLayoutRepository;

    @Autowired
    private ListLayoutCategoryRepository layoutCategoryRepository;

    private static boolean setUpComplete = false;
    private static UserAccountEntity userAccount;
    private static ListLayoutEntity retrieve;
    private static ListLayoutEntity toDelete;
    private static TagEntity tag1;
    private static TagEntity tag2;
    private static TagEntity tag3;
    private static TagEntity tag4;
    private static TagEntity tag5;
    private static Long addTagsCategoryId;
    private static Long retrieveCategoryId;
    private static Long deleteTagsCategoryId;

    private static String noseyUserName;

    @Autowired
    private TagRepository tagRepository;
    private static Long entireCategoryDeleteId;
    private static ListLayoutEntity uncategorizedCount;
    private static ListLayoutEntity deleteACategory;

    @Before
    public void setUp() {
        if (setUpComplete) {
            return;
        }
        retrieve = new ListLayoutEntity();
        retrieve.setLayoutType(ListLayoutType.All);
        retrieve = listLayoutRepository.save(retrieve);

        uncategorizedCount = new ListLayoutEntity();
        uncategorizedCount.setLayoutType(ListLayoutType.All);
        uncategorizedCount = listLayoutRepository.save(uncategorizedCount);

        deleteACategory = new ListLayoutEntity();
        deleteACategory.setLayoutType(ListLayoutType.All);
        deleteACategory = listLayoutRepository.save(deleteACategory);


        toDelete = new ListLayoutEntity();
        toDelete.setLayoutType(ListLayoutType.All);
        listLayoutRepository.save(toDelete);

        tag1 = ServiceTestUtils.buildTag("tag1", TagType.Ingredient);
        tag2 = ServiceTestUtils.buildTag("tag2", TagType.Ingredient);
        tag3 = ServiceTestUtils.buildTag("tag3", TagType.Ingredient);
        tag4 = ServiceTestUtils.buildTag("tag4", TagType.Ingredient);
        tag5 = ServiceTestUtils.buildTag("tag5", TagType.Ingredient);
        tag1 = tagRepository.save(tag1);
        tag2 = tagRepository.save(tag2);
        tag3 = tagRepository.save(tag3);
        tag4 = tagRepository.save(tag4);
        tag5 = tagRepository.save(tag5);

        ListLayoutCategoryEntity layoutCategoryEntity = new ListLayoutCategoryEntity();
        layoutCategoryEntity.setName("addTagsCategoryId");
        List<TagEntity> assignedTags = Arrays.asList(tag1, tag2);
        layoutCategoryEntity.setTags(assignedTags);
        layoutCategoryEntity.setLayoutId(retrieve.getId());
        ListLayoutCategoryEntity addCategories = layoutCategoryRepository.save(layoutCategoryEntity);
        addTagsCategoryId = addCategories.getId();

        layoutCategoryEntity = new ListLayoutCategoryEntity();
        layoutCategoryEntity.setName("deleteTagsCategory");
        assignedTags = Arrays.asList(tag1, tag2);
        layoutCategoryEntity.setTags(assignedTags);
        layoutCategoryEntity.setLayoutId(retrieve.getId());
        ListLayoutCategoryEntity deleteCategory = layoutCategoryRepository.save(layoutCategoryEntity);
        deleteTagsCategoryId = deleteCategory.getId();

        ListLayoutCategoryEntity retrieveCategory = new ListLayoutCategoryEntity();
        layoutCategoryEntity.setName("retrieveCategory");
        assignedTags = Arrays.asList(tag3, tag4);
        retrieveCategory.setTags(assignedTags);
        retrieveCategory.setLayoutId(retrieve.getId());
        ListLayoutCategoryEntity retrieveCat = layoutCategoryRepository.save(retrieveCategory);
        retrieveCategoryId = retrieveCat.getId();

        ListLayoutCategoryEntity entireCategoryToDelete = new ListLayoutCategoryEntity();
        entireCategoryToDelete.setName("entireCategoryToDelete");
        assignedTags = Arrays.asList(tag3, tag4);
        entireCategoryToDelete.setTags(assignedTags);
        entireCategoryToDelete.setLayoutId(deleteACategory.getId());
        ListLayoutCategoryEntity deleteEntireCat = layoutCategoryRepository.save(retrieveCategory);
        entireCategoryDeleteId = deleteEntireCat.getId();

        List<ListLayoutCategoryEntity> newCategories = Arrays.asList(retrieveCat, deleteCategory, addCategories);
        retrieve.setCategories(newCategories);
        listLayoutRepository.save(retrieve);

        ListLayoutCategoryEntity uncatCat = new ListLayoutCategoryEntity();
        uncatCat.setName("uncatCat");
        assignedTags = Arrays.asList(tag3, tag4);
        uncatCat.setTags(assignedTags);
        uncatCat.setLayoutId(uncategorizedCount.getId());
        ListLayoutCategoryEntity uncatCategory = layoutCategoryRepository.save(uncatCat);


        uncategorizedCount.setCategories(Arrays.asList(uncatCategory));
        listLayoutRepository.save(uncategorizedCount);

        List<ListLayoutCategoryEntity> willBeDeleted = Arrays.asList(deleteEntireCat);
        deleteACategory.setCategories(willBeDeleted);
        listLayoutRepository.save(deleteACategory);

        setUpComplete = true;

    }

    @Test
    public void createListLayout() throws Exception {
        ListLayoutEntity testSave = new ListLayoutEntity();
        testSave.setName("testname");

        testSave = listLayoutService.createListLayout(testSave);
        Assert.assertNotNull(testSave);
        Long id = testSave.getId();

        ListLayoutEntity check = listLayoutService.getListLayoutById(id);
        Assert.assertNotNull(check);
        Assert.assertEquals(testSave.getName(), check.getName());
    }


    @Test
    public void getListLayoutById() throws Exception {
        // get id for retrieve - already set up
        Long id = retrieve.getId();

        ListLayoutEntity check = listLayoutService.getListLayoutById(id);

        Assert.assertNotNull(check);
        Assert.assertEquals(retrieve.getId(), check.getId());
        Assert.assertEquals(retrieve.getName(), check.getName());
    }

    @Test
    public void getListLayoutList() throws Exception {
        List<ListLayoutEntity> list = listLayoutService.getListLayouts();

        Assert.assertNotNull(list);
        Assert.assertEquals(4L, list.size());
    }

    @Test
    public void testDeleteListLayout() {
        // get id for delete - already set up
        Long id = toDelete.getId();
        // service call
        listLayoutService.deleteListLayout(id);
        // retrieve by id
        ListLayoutEntity result = listLayoutService.getListLayoutById(id);
        // test that it's null
        Assert.assertNull(result);
    }

    @Test
    public void testAddCategoryToListLayout() {
        // get list layout
        Long id = retrieve.getId();

        // get count of categories
        retrieve = listLayoutRepository.findOne(id);
        Integer categoryCount = retrieve.getCategories().size();
        // build category
        final String testCategoryName = "testAddNewCategoryNamekkk";
        ListLayoutCategoryEntity layoutCategoryEntity = new ListLayoutCategoryEntity();
        layoutCategoryEntity.setName(testCategoryName);

        // add category to list layout - service call
        listLayoutService.addCategoryToListLayout(id, layoutCategoryEntity);
        // retrieve listlayout
        ListLayoutEntity result = listLayoutService.getListLayoutById(id);

        // assert that size is 1 bigger, and that categories contain new category
        Assert.assertNotNull(result);
        List<ListLayoutCategoryEntity> resultcategories = result.getCategories();
        Assert.assertNotNull(resultcategories);
        Assert.assertEquals(categoryCount + 1, resultcategories.size());
        Long count = resultcategories.stream().filter(t -> t.getName() != null && t.getName().equals(testCategoryName)).count();
        Assert.assertTrue(count == 1);
    }

    @Test
    public void testDeleteCategoryFromListLayout() {
        // get list layout
        Long id = deleteACategory.getId();

        // get count of categories
        deleteACategory = listLayoutRepository.findOne(id);
        Integer categoryCount = deleteACategory.getCategories().size();
        // get Categories - get first
        ListLayoutCategoryEntity toDeleteCategory = layoutCategoryRepository.findOne(entireCategoryDeleteId);
        final String testCategoryName = toDeleteCategory.getName();

        // delete category from list layout - service call
        listLayoutService.deleteCategoryFromListLayout(id, toDeleteCategory.getId());
        // retrieve listlayout
        ListLayoutEntity result = listLayoutService.getListLayoutById(id);

        // assert that size is 1 smaller, and that categories don't contain category
        Assert.assertNotNull(result);
        List<ListLayoutCategoryEntity> resultcategories = result.getCategories();
        Assert.assertNotNull(resultcategories);
        Assert.assertEquals(categoryCount - 1, resultcategories.size());
        Long count = resultcategories.stream().filter(t -> t.getName().equals(testCategoryName)).count();
        Assert.assertTrue(count == 0);

    }

    @Test
    public void testUpdateListLayoutCategory() {
        // get category from list
        Long id = retrieve.getId();

        // update the name
        ListLayoutCategoryEntity layoutCategoryEntity = retrieve.getCategories().get(0);
        final Long catId = layoutCategoryEntity.getId();
        final String originalName = layoutCategoryEntity.getName();
        final String testNewName = "newName";
        layoutCategoryEntity.setName(testNewName);

        // service call
        listLayoutService.updateListLayoutCategory(id, layoutCategoryEntity);

        // retrieve the category
        ListLayoutEntity result = listLayoutService.getListLayoutById(id);
        List<ListLayoutCategoryEntity> resultCategories = result.getCategories()
                .stream()
                .filter(t -> t.getId() == catId)
                .collect(Collectors.toList());

        // assert that the name is changed
        Assert.assertNotNull(resultCategories);
        Assert.assertNotNull(resultCategories.get(0));
        Assert.assertEquals(testNewName, resultCategories.get(0).getName());
    }

    @Test
    public void testGetUncategorizedTagsForList() {
        // retrieve category 1 has tags 1 and 2 assigned
        // this method should retrieve 3 - 5
        List<TagEntity> uncategorizedTags = listLayoutService.getUncategorizedTagsForList(uncategorizedCount.getId());

        Assert.assertNotNull(uncategorizedTags);
        Assert.assertTrue(uncategorizedTags.size() == 3);
    }

    @Test
    public void testGetTagsForLayoutCategory() {
        // retrieve category 1 has tags 1 and 2 assigned
        // this method should retrieve 1 and 2
        List<TagEntity> categoryTags = listLayoutService.getTagsForLayoutCategory(retrieveCategoryId);

        Assert.assertNotNull(categoryTags);
        Assert.assertTrue(categoryTags.size() == 2);

    }

    @Test
    public void testAddTagsToCategory() {
        // add tags 3 4 and 5 to category
        List<Long> addTags = Arrays.asList(tag2.getId(), tag3.getId(), tag4.getId(), tag5.getId());

        listLayoutService.addTagsToCategory(retrieve.getId(), addTagsCategoryId, addTags);

        // retrieve category
        ListLayoutCategoryEntity categoryEntity = layoutCategoryRepository.findOne(addTagsCategoryId);
        List<TagEntity> resultTags = tagRepository.getTagsForLayoutCategory(categoryEntity.getId());

        // assert has tags, and size is 5
        Assert.assertNotNull(resultTags);
        Assert.assertTrue(resultTags.size() == 5);
    }

    @Test
    public void testDeleteTagsFromCategory() {
        // get tags from category
        ListLayoutCategoryEntity categoryEntity = layoutCategoryRepository.findOne(deleteTagsCategoryId);
        List<TagEntity> tags = tagRepository.getTagsForLayoutCategory(categoryEntity.getId());
        List<Long> ids = tags.stream().map(TagEntity::getId).collect(Collectors.toList());
        // delete all the tags from category
        listLayoutService.deleteTagsFromCategory(retrieve.getId(), deleteTagsCategoryId, ids);
        // assert that category has 0 categoriex
        ListLayoutCategoryEntity result = layoutCategoryRepository.findOne(deleteTagsCategoryId);
        List<TagEntity> resultTags = tagRepository.getTagsForLayoutCategory(categoryEntity.getId());
        Assert.assertNotNull(resultTags);
        Assert.assertTrue(resultTags.size() == 0);
    }

}