package com.meg.listshop.lmt.service.tag.impl;

import com.meg.listshop.Application;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.api.model.DishRatingInfo;
import com.meg.listshop.lmt.api.model.RatingInfo;
import com.meg.listshop.lmt.api.model.RatingUpdateInfo;
import com.meg.listshop.lmt.api.model.SortOrMoveDirection;
import com.meg.listshop.lmt.data.entity.DishEntity;
import com.meg.listshop.lmt.data.entity.ListTagStatistic;
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

import java.util.*;

import static com.meg.listshop.test.TestConstants.*;

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
    public void testAddTagToDish()  {
        ListTagStatistic stat = statRepo.findByUserIdAndTagId(USER_3_ID, TestConstants.TAG_MEAT);
        int beforeCount = stat != null && stat.getAddedToDishCount() != null ? stat.getAddedToDishCount() : 0;
        List<TagEntity> originalTags = tagService.getTagsForDish(USER_3_NAME, TestConstants.DISH_1_ID);


        tagService.addTagToDish(USER_3_NAME, TestConstants.DISH_1_ID, TestConstants.TAG_MEAT);

        List<TagEntity> tags = tagService.getTagsForDish(USER_3_NAME, TestConstants.DISH_1_ID);

        Assert.assertNotNull(tags);
        Assert.assertEquals(originalTags.size() + 1, tags.size());
        boolean containsTagA = false;
        for (TagEntity testTag : tags) {
            if (testTag.getId().equals(TestConstants.TAG_MEAT)) {
                containsTagA = true;
                break;
            }
        }
        Assert.assertTrue(containsTagA);

        // check stats updated
        stat = statRepo.findByUserIdAndTagId(USER_3_ID, TestConstants.TAG_MEAT);
        Assert.assertNotNull(stat);
        Assert.assertNotNull(stat.getAddedToDishCount());
        Assert.assertEquals(beforeCount + 1, (long) stat.getAddedToDishCount());
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
    public void testGetRatingUpdateInfoForOneDish_EmptyRatings() {
        // get rating info for dish 25 (ham and potato soup) and user3
        // dish 25 doesn't have any ratings
        RatingUpdateInfo updateInfo = tagService.getRatingUpdateInfoForDishIds(TestConstants.USER_3_NAME, Collections.singletonList(25L));
        Assert.assertNotNull(updateInfo);
        Assert.assertNotNull(updateInfo.getRatingHeaders());
        Assert.assertEquals(8, updateInfo.getRatingHeaders().size());
        Assert.assertNotNull(updateInfo.getDishRatingInfoSet());
        Assert.assertEquals(1, updateInfo.getDishRatingInfoSet().size());
        DishRatingInfo info = updateInfo.getDishRatingInfoSet().iterator().next();
        Assert.assertNotNull(info.getRatings());
        Assert.assertEquals(8, info.getRatings().size());

        Set<RatingInfo> testHeaders = updateInfo.getRatingHeaders();
        Iterator it = testHeaders.iterator();
        while (it.hasNext()) {
            RatingInfo toTest = (RatingInfo) it.next();
            Assert.assertNotNull(toTest.getMaxPower());
        }
    }

    @Test
    public void testGetRatingUpdateInfoForOneDish_FilledRatings() {

        // one dish, all ratings filled
        // dish 503 belonging to user 500 (user_1)
        // it has all ratings filled in already
        RatingUpdateInfo updateInfo = tagService.getRatingUpdateInfoForDishIds(TestConstants.USER_1_NAME, Collections.singletonList(TestConstants.DISH_4_ID));
        Assert.assertNotNull(updateInfo);
        Assert.assertNotNull(updateInfo.getRatingHeaders());
        Assert.assertEquals(8, updateInfo.getRatingHeaders().size());
        Assert.assertNotNull(updateInfo.getDishRatingInfoSet());
        Assert.assertEquals(1, updateInfo.getDishRatingInfoSet().size());
        DishRatingInfo info = updateInfo.getDishRatingInfoSet().iterator().next();
        Assert.assertNotNull(info.getRatings());
        Assert.assertEquals(8, info.getRatings().size());

        Set<RatingInfo> testHeaders = updateInfo.getRatingHeaders();
        Iterator it = testHeaders.iterator();
        while (it.hasNext()) {
            RatingInfo toTest = (RatingInfo) it.next();
            Assert.assertNotNull(toTest.getMaxPower());
        }
    }


    @Test
    public void testGetRatingUpdateInfoForDishIds() {

        // two dishes, one filled, one not
        // dish 4 - filled, dish 5 - not filled
        List<Long> dishIds = new ArrayList<>();
        dishIds.add(TestConstants.DISH_3_ID);
        dishIds.add(TestConstants.DISH_5_ID);
        RatingUpdateInfo updateInfo = tagService.getRatingUpdateInfoForDishIds(TestConstants.USER_3_NAME, dishIds);
        Assert.assertNotNull(updateInfo);
        Assert.assertNotNull(updateInfo.getRatingHeaders());
        Assert.assertEquals(8, updateInfo.getRatingHeaders().size());
        Assert.assertNotNull(updateInfo.getDishRatingInfoSet());
        Assert.assertEquals(2, updateInfo.getDishRatingInfoSet().size());
        for (Iterator<DishRatingInfo> iter = updateInfo.getDishRatingInfoSet().iterator(); iter.hasNext(); ) {
            DishRatingInfo info = iter.next();
            Assert.assertNotNull(info.getRatings());
            Assert.assertEquals(8, info.getRatings().size());
        }

        Set<RatingInfo> testHeaders = updateInfo.getRatingHeaders();
        Iterator it = testHeaders.iterator();
        while (it.hasNext()) {
            RatingInfo toTest = (RatingInfo) it.next();
            Assert.assertNotNull(toTest.getMaxPower());
        }
    }

    @Test
    @Ignore
    public void testIncrementRatingUp() {
        // for dish 4, user 1 - increment rating 291
        // should move from 400 to 399
        // get dish
        DishEntity dish = dishService.getDishForUserById(TestConstants.USER_1_NAME, TestConstants.DISH_6_ID);

        // get tags for dish
        List<TagEntity> tags = tagService.getTagsForDish(USER_1_NAME, TestConstants.DISH_6_ID);

        // assert includes 400
        Optional<TagEntity> testTag = tags.stream().filter(t -> t.getId().equals(400L)).findFirst();
        Assert.assertTrue(testTag.isPresent());

        // increment
        tagService.incrementDishRating(TestConstants.USER_1_NAME, TestConstants.DISH_6_ID, 291L, SortOrMoveDirection.UP);

        // get tags for dish
        tags = tagService.getTagsForDish(USER_1_NAME, TestConstants.DISH_6_ID);

        // assert doesn't include 400
        testTag = tags.stream().filter(t -> t.getId().equals(400L)).findFirst();
        Assert.assertFalse(testTag.isPresent());

        // assert includes 399
        testTag = tags.stream().filter(t -> t.getId().equals(399L)).findFirst();
        Assert.assertTrue(testTag.isPresent());
    }

    @Test
    public void testIncrementRatingDown() {
        // for dish 62, user 3 - increment rating 291
        // should move from 400 to 401
        // get dish
        // incrementing elegance - elegance 3 396
        Long eleganceTagIdLevel3 = 396L;
        Long eleganceTagIdLevel2 = 397L;
        DishEntity dish = dishService.getDishForUserById(TestConstants.USER_1_NAME, TestConstants.DISH_4_ID);

        // get tags for dish
        List<TagEntity> tags = tagService.getTagsForDish(TestConstants.USER_1_NAME, dish.getId());

        // assert includes 396
        Optional<TagEntity> testTag = tags.stream().filter(t -> t.getId().equals(eleganceTagIdLevel3)).findFirst();
        Assert.assertTrue(testTag.isPresent());

        // increment
        tagService.incrementDishRating(TestConstants.USER_1_NAME, TestConstants.DISH_4_ID, 292L, SortOrMoveDirection.DOWN);

        // get tags for dish
        tags = tagService.getTagsForDish(TestConstants.USER_1_NAME, dish.getId());

        // assert doesn't include 396
        testTag = tags.stream().filter(t -> t.getId().equals(eleganceTagIdLevel3)).findFirst();
        Assert.assertFalse(testTag.isPresent());

        // assert includes 397
        testTag = tags.stream().filter(t -> t.getId().equals(eleganceTagIdLevel2)).findFirst();
        Assert.assertTrue(testTag.isPresent());
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

    //MM to implement
    @Test
    public void getDictionaryForIds() {
        // make list of 5 ids
        Set<Long> testTagIds = new HashSet();
        testTagIds.add(TAG_CARROTS);
        testTagIds.add(TAG_CROCKPOT);
        testTagIds.add(TAG_SOUP);

        // method under test
        Map<Long, TagEntity> dictionary = tagService.getDictionaryForIds(testTagIds);

        // check for dictionary with 5 elements
        Assert.assertNotNull(dictionary);
        Assert.assertEquals(3L, dictionary.size());
    }


}