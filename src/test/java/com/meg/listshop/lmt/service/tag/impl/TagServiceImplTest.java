package com.meg.listshop.lmt.service.tag.impl;

import com.meg.listshop.Application;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.data.entity.DishEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.repository.ListTagStatisticRepository;
import com.meg.listshop.lmt.service.DishService;
import com.meg.listshop.lmt.service.tag.TagService;
import com.meg.listshop.test.TestConstants;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static com.meg.listshop.test.TestConstants.USER_3_NAME;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@Ignore
public class TagServiceImplTest {

    @ClassRule
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    @Autowired
    private TagService tagService;

    @Autowired
    private ListTagStatisticRepository statRepo;

    @Autowired
    private DishService dishService;

    private TagEntity a;
    private TagEntity b;
    private TagEntity c;

    private DishEntity dish;

    @Before
    public void setUp() {
        // setting up for taginfo
        TagEntity parent = new TagEntity("parent", "main1");

        parent = tagService.save(parent);


        // setting up for error assign tag
        a = new TagEntity("a", "a");

        // setting up dish
        dish = new DishEntity();
        dish.setDishName("tagTest");
        dish.getTags().add(b);
        dish.getTags().add(c);

        dishService.save(dish, false);
    }


    @Test
    public void save()  {
        TagEntity testSave = new TagEntity();
        testSave.setName("testname");
        testSave.setDescription("testdescription");

        testSave = tagService.save(testSave);
        Long id = testSave.getId();

        TagEntity check = tagService.getTagById(id);
        Assert.assertNotNull(check);
        Assert.assertEquals(testSave.getName(), check.getName());
        Assert.assertEquals(testSave.getDescription(), check.getDescription());
    }

    @Test
    public void testDeleteTagFromDish() {
        List<TagEntity> originaltags = tagService.getTagsForDish(USER_3_NAME, TestConstants.DISH_5_ID);
        TagEntity tagToDelete = originaltags.get(0);
        Long tagId = tagToDelete.getId();
        // delete tag c from dish
        tagService.deleteTagFromDish(USER_3_NAME, TestConstants.DISH_5_ID, tagId);
        // get tags from dish
        List<TagEntity> tags = tagService.getTagsForDish(USER_3_NAME, TestConstants.DISH_5_ID);
        // check dish tags for c
        boolean found = tags.stream().anyMatch(t -> t.getId() == tagId);
        // assert not found
        Assert.assertFalse(found);
    }

    @Test
    public void testDeleteTag() {
        // TODO this test is for a method which has a postgres specific query behind it, and which
        // doesn't work with h2

        // eventually we'll moved these to a testdb in postgres
        // until then, skipping these tests
        List<TagEntity> tags = tagService.getTagsForDish(USER_3_NAME, TestConstants.DISH_3_ID);
/*
        Assert.assertNotNull(tags);
        boolean containsTagA = false;
        boolean containsTagB = false;
        int tagCountBefore = tags.size();
        for (TagEntity testTag : tags) {
            if (testTag.getId().equals(TestConstants.TAG_TO_DELETE) ) {
                containsTagA = true;
                continue;
            } else
            if (testTag.getId().equals(TestConstants.TAG_TO_REPLACE) ) {
                containsTagB = true;
                continue;
            }
        }
        Assert.assertTrue(containsTagA);
        Assert.assertTrue(containsTagB);

        tagService.saveTagForDelete(TestConstants.TAG_TO_DELETE, TestConstants.TAG_TO_REPLACE);

        tags = tagService.getTagsForDish(TestConstants.DISH_3_ID);

        Assert.assertNotNull(tags);
        Assert.assertEquals(tagCountBefore - 1, tags.size());
  containsTagA = false;
        containsTagB = false;
        int tagCountBefore = tags.size();
        for (TagEntity testTag : tags) {
            if (testTag.getId().equals(TestConstants.TAG_TO_DELETE) ) {
                containsTagA = true;
                continue;
            } else
            if (testTag.getId().equals(TestConstants.TAG_TO_REPLACE) ) {
                containsTagB = true;
                continue;
            }
        }
        Assert.assertTrue(containsTagA);
        Assert.assertTrue(containsTagB);*/
    }



}