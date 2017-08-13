package com.meg.atable.service.impl;

import com.meg.atable.Application;
import com.meg.atable.model.Dish;
import com.meg.atable.model.Tag;
import com.meg.atable.model.TagInfo;
import com.meg.atable.service.DishService;
import com.meg.atable.service.TagService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertFalse;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class TagServiceImplTest {
    @Autowired
    private TagService tagService;

    @Autowired
    private DishService dishService;

    private Tag testTag;
    private Tag a;
    private Tag b;
    private Tag c;

    private Dish dish;

    @Before
    public void setUp() {
        // setting up for taginfo
        Tag parent = new Tag("parent","main1");

        parent = tagService.save(parent);

        testTag = tagService.createTag(parent,"testTag");
        Tag sub2 = tagService.createTag(parent,"testTagSibling");
        Tag sub3 = tagService.createTag(parent,"testTagSibling2");

        Tag sub4 = tagService.createTag(testTag,"testTagChild");
        Tag sub5 = tagService.createTag(testTag,"testTagAnotherChild");

        // setting up for error assign tag
        a = new Tag("a","a");
        a = tagService.createTag(null,a.getName());

        b = tagService.createTag(a,"b");
        c = tagService.createTag(b, "c");

        // setting up dish
        dish = new Dish();
        dish.setDishName("tagTest");
        dish.getTags().add(b);
        dish.getTags().add(c);

        dishService.save(dish);
    }

    @Test
    public void save() throws Exception {
        Tag testSave = new Tag();
        testSave.setName("testname");
        testSave.setDescription("testdescription");

        testSave = tagService.save(testSave);
        Long id = testSave.getId();

        Tag check = tagService.getTagById(id).get();
        Assert.assertNotNull(check);
        Assert.assertEquals(testSave.getName(),check.getName());
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
    public void getTagInfo() throws Exception {

        TagInfo tagInfo = tagService.getTagInfo(testTag.getId());

        // check that everything was retrieved correctly
        Assert.assertNotNull(tagInfo);
        Assert.assertEquals(testTag.getName(),tagInfo.getName());
        Assert.assertEquals(testTag.getDescription(),tagInfo.getDescription());

        Assert.assertNotNull(tagInfo.getParentId());
        Tag parent = tagService.getTagById(tagInfo.getParentId()).get();
        Assert.assertEquals(parent.getId().longValue(),tagInfo.getParentId().longValue());

        Assert.assertNotNull(tagInfo.getSiblingIds());
        List<Long> siblingids = tagInfo.getSiblingIds();
        List <Tag> siblingtags = getTagList(siblingids);
        Assert.assertNotNull(siblingtags);
        Assert.assertTrue(siblingtags.size()>1);
        Assert.assertTrue(siblingtags.get(0).getName().toLowerCase().contains("sibling"));

        Assert.assertNotNull(tagInfo.getChildrenIds());
        List<Long> childrenids = tagInfo.getChildrenIds();
        List <Tag> childrentags = getTagList(childrenids);
        Assert.assertNotNull(childrentags);
        Assert.assertTrue(childrentags.size()>1);
        Assert.assertTrue(childrentags.get(0).getName().toLowerCase().contains("child"));
    }

    @Test
    public void testGetTagsForDish() throws Exception {
        List<Tag> tags = tagService.getTagsForDish(dish.getId());

        Assert.assertNotNull(tags);
        Assert.assertTrue(tags.size() > 0);
        Assert.assertTrue(tags.size() == 2);

    }

    @Test
    public void testAddTagToDish() throws Exception {
        tagService.addTagToDish(dish.getId(),a.getId());

        List<Tag> tags = tagService.getTagsForDish(dish.getId());

        Assert.assertNotNull(tags);
        Assert.assertTrue(tags.size()==3);
        boolean containsTagA = false;
        for (Tag testTag:tags) {
            if (testTag.getId()==a.getId()) {
                containsTagA=true;
                break;
            }
        }
        Assert.assertTrue(containsTagA);
    }

    @Test
    public void testAssignTagToParent_error() {
        // tags a, b, c
        // assign a as child of c

        // service call
        boolean result = tagService.assignTagToParent(a.getId(),c.getId());

        Assert.assertFalse(result);

    }

    @Test
    public void testAssignTagToParent_noError() {
        // tags a, b, c
        // assign a as child of c

        // service call
        boolean result = tagService.assignTagToParent(c.getId(),a.getId());

        Assert.assertTrue(result);
        TagInfo resultInfo = tagService.getTagInfo(c.getId());
        Assert.assertNotNull(resultInfo);
        Assert.assertEquals(a.getId(),resultInfo.getParentId());
    }

    @Test
    public void testGetTagInfoFullList() {
        List<TagInfo> allTags = tagService.getTagInfoList(false);

        // test that each tag only exists once in list
        List<Long> idCheck = new ArrayList<>();
        for (TagInfo tag : allTags) {
            Long id = tag.getId();
            assertFalse(idCheck.contains(id));
            idCheck.add(id);
        }
    }

    private List<Tag> getTagList(List<Long> tagids) {
        return tagService.getTagList()
                .stream()
                .filter(t -> tagids.contains(t.getId()))
                .collect(Collectors.toList());

    }

}