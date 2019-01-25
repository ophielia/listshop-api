package com.meg.atable.lmt.service.impl;

import com.meg.atable.Application;
import com.meg.atable.lmt.data.entity.DishEntity;
import com.meg.atable.lmt.data.entity.TagEntity;
import com.meg.atable.lmt.data.repository.TagRepository;
import com.meg.atable.lmt.service.DishService;
import com.meg.atable.lmt.service.tag.TagService;
import com.meg.atable.test.TestConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class TagServiceImplTest {
    @Autowired
    private TagService tagService;

    @Autowired
    private TagRepository tagRepository;

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

        TagEntity testTag = tagService.createTag(parent, "testTag");
        TagEntity sub2 = tagService.createTag(parent, "testTagSibling");
        TagEntity sub3 = tagService.createTag(parent, "testTagSibling2");

        TagEntity sub4 = tagService.createTag(testTag, "testTagChild");
        TagEntity sub5 = tagService.createTag(testTag, "testTagAnotherChild");

        // setting up for error assign tag
        a = new TagEntity("a", "a");
        a = tagService.createTag(null, a.getName());

        b = tagService.createTag(a, "b");
        c = tagService.createTag(b, "c");

        // setting up dish
        dish = new DishEntity();
        dish.setDishName("tagTest");
        dish.getTags().add(b);
        dish.getTags().add(c);

        dishService.save(dish, false);
    }

    @Test
    public void save() throws Exception {
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
    public void getTagById() throws Exception {
    }

    @Test
    public void getTagList() throws Exception {
    }

    @Test
    public void createTag() throws Exception {
    }

    @Test
    public void createTag1() throws Exception {
    }

    @Test
    public void testGetTagsForDish() throws Exception {
        List<TagEntity> tags = tagService.getTagsForDish(dish.getId());

        Assert.assertNotNull(tags);
        Assert.assertTrue(tags.size() > 0);
        Assert.assertTrue(tags.size() == 2);

    }


    @Test
    public void testAddTagToDish() throws Exception {
        tagService.addTagToDish(dish.getId(), TestConstants.TAG_MEAT);

        List<TagEntity> tags = tagService.getTagsForDish(dish.getId());

        Assert.assertNotNull(tags);
        Assert.assertTrue(tags.size() == 3);
        boolean containsTagA = false;
        for (TagEntity testTag : tags) {
            if (testTag.getId().equals(TestConstants.TAG_MEAT) ) {
                containsTagA = true;
                break;
            }
        }
        Assert.assertTrue(containsTagA);
    }


    @Test
    public void testDeleteTag() {
        // delete tag c from dish
        tagService.deleteTagFromDish(dish.getId(), c.getId());
        // get tags from dish
        List<TagEntity> tags = tagService.getTagsForDish(dish.getId());
        // check dish tags for c
        boolean found = tags.stream().anyMatch(t -> t.getId() == c.getId());
        // assert not found
        Assert.assertFalse(found);
    }


}