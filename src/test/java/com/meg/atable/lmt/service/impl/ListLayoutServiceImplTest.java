package com.meg.atable.lmt.service.impl;

import com.meg.atable.Application;
import com.meg.atable.lmt.api.model.Category;
import com.meg.atable.lmt.api.model.ListLayoutCategory;
import com.meg.atable.lmt.data.entity.CategoryRelationEntity;
import com.meg.atable.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.atable.lmt.data.entity.ListLayoutEntity;
import com.meg.atable.lmt.data.entity.TagEntity;
import com.meg.atable.lmt.data.repository.CategoryRelationRepository;
import com.meg.atable.lmt.data.repository.ListLayoutCategoryRepository;
import com.meg.atable.lmt.data.repository.ListLayoutRepository;
import com.meg.atable.lmt.data.repository.TagRepository;
import com.meg.atable.lmt.service.ListLayoutException;
import com.meg.atable.lmt.service.ListLayoutService;
import com.meg.atable.test.TestConstants;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class ListLayoutServiceImplTest {
    private static ListLayoutEntity retrieve;

    @Autowired
    private ListLayoutService listLayoutService;
    @Autowired
    private ListLayoutRepository listLayoutRepository;
    @Autowired
    private ListLayoutCategoryRepository layoutCategoryRepository;
    @Autowired
    private CategoryRelationRepository categoryRelationRepository;
    @Autowired
    private TagRepository tagRepository;


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
        Long id = TestConstants.LIST_LAYOUT_1_ID;

        ListLayoutEntity check = listLayoutService.getListLayoutById(id);

        Assert.assertNotNull(check);
        Assert.assertEquals(TestConstants.LIST_LAYOUT_1_ID, check.getId());
        Assert.assertEquals(TestConstants.LIST_LAYOUT_1_NAME, check.getName());
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
        Long id = TestConstants.LIST_LAYOUT_4_ID;
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
        Long id = TestConstants.LIST_LAYOUT_2_ID;

        // get count of categories
        Optional<ListLayoutEntity> retrieveOpt = listLayoutRepository.findById(id);
        retrieve = retrieveOpt.get();
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

        // check disp order
        ListLayoutCategoryEntity newCategory = resultcategories.stream()
                .filter(t -> t.getName() != null && t.getName().equals(testCategoryName)).findFirst().get();
        Assert.assertNotNull(newCategory.getDisplayOrder());
        Assert.assertTrue(newCategory.getDisplayOrder()>=3);

        // get category relation for new category
        List<CategoryRelationEntity> relation = categoryRelationRepository.findCategoryRelationsByChildId(newCategory.getId());
        Assert.assertNotNull(relation);
        Assert.assertEquals(1,relation.size());
        Assert.assertNull(relation.get(0).getParent());
    }

    @Test
    public void testDeleteCategoryFromListLayout() throws ListLayoutException {
        // get list layout
        Long id = TestConstants.LIST_LAYOUT_2_ID;

        // get count of categories
        Optional<ListLayoutEntity> retrieveOpt = listLayoutRepository.findById(id);
        ListLayoutEntity deleteACategory = retrieveOpt.get();
        Integer categoryCount = deleteACategory.getCategories().size();
        // get Categories - get first
        Optional<ListLayoutCategoryEntity> toDeleteCategoryOpt = layoutCategoryRepository.findById(TestConstants.LIST_LAYOUT_2_CATEGORY_ID4);
        ListLayoutCategoryEntity toDeleteCategory = toDeleteCategoryOpt.get();
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
        // get list layout 2 - and ensure that it has null as a parent
        List<CategoryRelationEntity> subcategories = categoryRelationRepository.findCategoryRelationsByChildId(TestConstants.LIST_LAYOUT_2_CATEGORY_ID5);
        Assert.assertNotNull(subcategories);
        Assert.assertEquals(1L,subcategories.size());
        Assert.assertNull(subcategories.get(0).getParent());
    }

    @Test
    public void testUpdateListLayoutCategory() {
        // get category from list
        ListLayoutEntity retrieve = listLayoutService.getListLayoutById(TestConstants.LIST_LAYOUT_1_ID);
        // update the name
        ListLayoutCategoryEntity layoutCategoryEntity = retrieve.getCategories().get(0);
        final Long catId = layoutCategoryEntity.getId();
        final String originalName = layoutCategoryEntity.getName();
        final String testNewName = "newName";
        layoutCategoryEntity.setName(testNewName);

        // service call
        listLayoutService.updateListLayoutCategory(retrieve.getId(), layoutCategoryEntity);

        // retrieve the category
        ListLayoutEntity result = listLayoutService.getListLayoutById(retrieve.getId());
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
    public void testGetTagsForLayoutCategory() {
        // retrieve category 1 has tags 1 and 2 assigned
        // this method should retrieve 1 and 2
        List<TagEntity> categoryTags = listLayoutService.getTagsForLayoutCategory(TestConstants.LIST_LAYOUT_2_CATEGORY_ID1);

        Assert.assertNotNull(categoryTags);
        Assert.assertTrue(categoryTags.size() == 2);

    }


    @Test
    public void testAddTagsToCategory() {
        List<TagEntity> beforeTags = tagRepository.getTagsForLayoutCategory(TestConstants.LIST_LAYOUT_1_CATEGORY_ID);
        int beforeCount = beforeTags.size();

        // add tags 3 4 and 5 to category
        List<Long> addTags = Arrays.asList(TestConstants.TAG_1_ID, TestConstants.TAG_4_ID);

        listLayoutService.addTagsToCategory(TestConstants.LIST_LAYOUT_1_ID, TestConstants.LIST_LAYOUT_1_CATEGORY_ID, addTags);

        // retrieve category
        Optional<ListLayoutCategoryEntity> categoryEntityOpt = layoutCategoryRepository.findById(TestConstants.LIST_LAYOUT_1_CATEGORY_ID);
        ListLayoutCategoryEntity categoryEntity = categoryEntityOpt.get();

        // assert has tags, and size is 5
        List<TagEntity> resultTags = tagRepository.getTagsForLayoutCategory(TestConstants.LIST_LAYOUT_1_CATEGORY_ID);

        Assert.assertNotNull(resultTags);
        Assert.assertEquals(beforeCount + 2, resultTags.size());
    }

    @Test
    public void testDeleteTagsFromCategory() {
        // ll LIST_LAYOUT_2_ID
        // category LIST_LAYOUT_2_CATEGORY_ID6
        // get tags from category
        Optional<ListLayoutCategoryEntity> categoryEntityOpt = layoutCategoryRepository.findById(TestConstants.LIST_LAYOUT_2_CATEGORY_ID6);
        ListLayoutCategoryEntity categoryEntity = categoryEntityOpt.get();
        List<TagEntity> tags = tagRepository.getTagsForLayoutCategory(categoryEntity.getId());
        List<Long> ids = tags.stream().map(TagEntity::getId).collect(Collectors.toList());
        // delete all the tags from category
        listLayoutService.deleteTagsFromCategory(TestConstants.LIST_LAYOUT_2_ID, TestConstants.LIST_LAYOUT_2_CATEGORY_ID6, ids);
        // assert that category has 0 categoriex
        Optional<ListLayoutCategoryEntity> resultOpt = layoutCategoryRepository.findById(TestConstants.LIST_LAYOUT_2_CATEGORY_ID6);
        ListLayoutCategoryEntity result = resultOpt.get();
        List<TagEntity> resultTags = tagRepository.getTagsForLayoutCategory(categoryEntity.getId());
        Assert.assertNotNull(resultTags);
        Assert.assertTrue(resultTags.size() == 0);
    }


    @Test
    public void testGetStructuredCategories() {

        ListLayoutEntity testentity = listLayoutService.getListLayoutById(TestConstants.LIST_LAYOUT_1_ID);
        List<Category> structured = listLayoutService.getStructuredCategories(testentity);
        Assert.assertNotNull(structured);
        Assert.assertEquals(42, structured.size());
        // get total category count
        int totalcategorycount = 0;
        for (Category categoryResult : structured) {
            ListLayoutCategory cr = (ListLayoutCategory) categoryResult;
            if (cr.getSubCategories() != null) {
                totalcategorycount += cr.getSubCategories().size();
            }
            totalcategorycount++;

        }
        Assert.assertEquals(44, totalcategorycount);
    }


    @Test
    public void testAddCategoryToParent() throws ListLayoutException {
        // get category one, two and three
        Optional<ListLayoutCategoryEntity> oneOpt = layoutCategoryRepository.findById(TestConstants.LIST_LAYOUT_2_CATEGORY_ID1);
        Optional<ListLayoutCategoryEntity> twoOpt = layoutCategoryRepository.findById(TestConstants.LIST_LAYOUT_2_CATEGORY_ID2);
        Optional<ListLayoutCategoryEntity> threeOpt = layoutCategoryRepository.findById(TestConstants.LIST_LAYOUT_2_CATEGORY_ID3);

        ListLayoutCategoryEntity one = oneOpt.get();
        ListLayoutCategoryEntity two = twoOpt.get();
        ListLayoutCategoryEntity three = threeOpt.get();

        // basic case - add category one to category two
        listLayoutService.addCategoryToParent(TestConstants.LIST_LAYOUT_2_CATEGORY_ID1, TestConstants.LIST_LAYOUT_2_CATEGORY_ID2);
        categoryRelationRepository.flush();

        // check relationship - category one
        List<CategoryRelationEntity> relationships = categoryRelationRepository.findCategoryRelationsByChildId(TestConstants.LIST_LAYOUT_2_CATEGORY_ID1);
        Assert.assertNotNull(relationships);
        Assert.assertEquals(1, relationships.size());
        CategoryRelationEntity relationship = relationships.get(0);
        Assert.assertEquals(TestConstants.LIST_LAYOUT_2_CATEGORY_ID2.longValue(), relationship.getParent().getId().longValue());

        // check order
        Optional<ListLayoutCategoryEntity> checkOpt = layoutCategoryRepository.findById(TestConstants.LIST_LAYOUT_2_CATEGORY_ID1);
        ListLayoutCategoryEntity check = checkOpt.get();
        Assert.assertNotNull(check);
        Assert.assertEquals(8,check.getDisplayOrder().intValue());

        // check order for second subcategory- add category three to category one
        listLayoutService.addCategoryToParent(TestConstants.LIST_LAYOUT_2_CATEGORY_ID3, TestConstants.LIST_LAYOUT_2_CATEGORY_ID2);
        categoryRelationRepository.flush();

        Optional<ListLayoutCategoryEntity> checkOptTwo = layoutCategoryRepository.findById(TestConstants.LIST_LAYOUT_2_CATEGORY_ID3);
        check = checkOptTwo.get();
        Assert.assertNotNull(check);
        Assert.assertEquals(13,check.getDisplayOrder().intValue());

    }

    @Test(expected = ListLayoutException.class)
    public void testAddCategoryToParent_errorDifferentLayouts() throws ListLayoutException {
        // get category one, two and three

        // basic case - add category two to category one
        listLayoutService.addCategoryToParent(1L, TestConstants.LIST_LAYOUT_2_CATEGORY_ID2);
    }

    @Test
    public void testMoveCategory() throws ListLayoutException {
        // using modification list layout
        // need one category with two subcategories
        // parent -LIST_LAYOUT_2_CATEGORY_ID7
        // children - LIST_LAYOUT_2_CATEGORY_ID8, LIST_LAYOUT_2_CATEGORY_ID9

        // get subcategories by order
        List<ListLayoutCategoryEntity> orderedSubcategories = layoutCategoryRepository.getSubcategoriesForOrder(TestConstants.LIST_LAYOUT_2_ID, TestConstants.LIST_LAYOUT_2_CATEGORY_ID7);
        ListLayoutCategoryEntity first = orderedSubcategories.get(0);
        ListLayoutCategoryEntity second = orderedSubcategories.get(1);

        // call move down
        listLayoutService.moveCategory(first.getId(),false);

        // get subcategories and verify that first is now last
        orderedSubcategories = layoutCategoryRepository.getSubcategoriesForOrder(TestConstants.LIST_LAYOUT_2_ID, TestConstants.LIST_LAYOUT_2_CATEGORY_ID7);
        ListLayoutCategoryEntity result = orderedSubcategories.get(1);
        Assert.assertEquals(first.getId(),result.getId());

        // call move up
        listLayoutService.moveCategory(result.getId(),true);
        // get subcategories and verify that last is now first
        orderedSubcategories = layoutCategoryRepository.getSubcategoriesForOrder(TestConstants.LIST_LAYOUT_2_ID, TestConstants.LIST_LAYOUT_2_CATEGORY_ID7);
        result = orderedSubcategories.get(0);
        Assert.assertEquals(first.getId(),result.getId());

    }


}