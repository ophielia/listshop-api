package com.meg.listshop.lmt.service.tag.impl;

import com.meg.listshop.Application;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.data.entity.DishEntity;
import com.meg.listshop.lmt.data.entity.ListTagStatistic;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.entity.TagExtendedEntity;
import com.meg.listshop.lmt.data.repository.ListTagStatisticRepository;
import com.meg.listshop.lmt.service.DishService;
import com.meg.listshop.lmt.service.tag.TagService;
import com.meg.listshop.test.TestConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static com.meg.listshop.test.TestConstants.*;
import static java.lang.Thread.sleep;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class TagServiceImplTest {
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
    public void getTagList_AssignTagsFilter() {
        TagFilterType filter = TagFilterType.ForSelectAssign;
        List<TagType> tagTypes = new ArrayList<>();
        tagTypes.add(TagType.NonEdible);
        tagTypes.add(TagType.Rating);

        // test call
        List<TagEntity> result = tagService.getTagList(filter, tagTypes);

        // confirm results
        // should not include Produce and Personal Hygiene (388 and 389)
        Assert.assertFalse(resultsInclude(388L, result));
        Assert.assertFalse(resultsInclude(389L, result));
        // should not include Quick To Table and Occasions (290 and 35)
        Assert.assertFalse(resultsInclude(290L, result));
        Assert.assertFalse(resultsInclude(35L, result));
        // should not include finger food and pantry dish (7 and 471)
        Assert.assertFalse(resultsInclude(7L, result));
        Assert.assertFalse(resultsInclude(471L, result));
        // should  include ease of prep 4 and soft cat food (399 and 470)
        Assert.assertTrue(resultsInclude(399L, result));
        Assert.assertTrue(resultsInclude(470L, result));

        // test call without call types
        List<TagEntity> resultsNoFilter = tagService.getTagList(filter, null);

        // confirm results
        // should not include Produce and Personal Hygiene (388 and 389)
        Assert.assertFalse(resultsInclude(388L, resultsNoFilter));
        Assert.assertFalse(resultsInclude(389L, resultsNoFilter));
        // should not include Quick To Table and Occasions (290 and 35)
        Assert.assertFalse(resultsInclude(290L, resultsNoFilter));
        Assert.assertFalse(resultsInclude(35L, resultsNoFilter));
        // should  include finger food and pantry dish (7 and 471)
        Assert.assertTrue(resultsInclude(7L, resultsNoFilter));
        Assert.assertTrue(resultsInclude(471L, resultsNoFilter));
        // should  include heavy cream and red lentils (78 and 126)
        Assert.assertTrue(resultsInclude(78L, resultsNoFilter));
        Assert.assertTrue(resultsInclude(126L, resultsNoFilter));

    }

    @Test
    public void getTagList_SearchTagsFilter() {
        TagFilterType filter = TagFilterType.ForSelectSearch;
        List<TagType> tagTypes = new ArrayList<>();
        tagTypes.add(TagType.Ingredient);
        tagTypes.add(TagType.TagType);

        // test call
        List<TagEntity> result = tagService.getTagList(filter, tagTypes);

        // confirm results
        // should  include light cream and lardons (163 and 74)
        Assert.assertTrue(resultsInclude(163L, result));
        Assert.assertTrue(resultsInclude(74L, result));
        // should not include coffee filters and ease of prep 2 (401 and 148)
        Assert.assertFalse(resultsInclude(401L, result));
        Assert.assertFalse(resultsInclude(148L, result));
        // should not include cultural roots and frozen (403 and 35)
        Assert.assertFalse(resultsInclude(403L, result));
        Assert.assertFalse(resultsInclude(35L, result));
        // should not  include ease of prep 4 and soft cat food (3 and 63)
        Assert.assertFalse(resultsInclude(3L, result));
        Assert.assertFalse(resultsInclude(63L, result));

        // test call without call types
        List<TagEntity> resultsNoFilter = tagService.getTagList(filter, null);

        // confirm results
        // should  include light cream and lardons (163 and 74)
        Assert.assertTrue(resultsInclude(163L, resultsNoFilter));
        Assert.assertTrue(resultsInclude(74L, resultsNoFilter));
        // should  include coffee filters and ease of prep 2 (401 and 148)
        Assert.assertTrue(resultsInclude(401L, resultsNoFilter));
        Assert.assertTrue(resultsInclude(148L, resultsNoFilter));
        // should not include cultural roots and frozen (403 and 35)
        Assert.assertFalse(resultsInclude(403L, resultsNoFilter));
        Assert.assertFalse(resultsInclude(35L, resultsNoFilter));
        // should not  include ease of prep 4 and soft cat food (3 and 63)
        Assert.assertFalse(resultsInclude(3L, resultsNoFilter));
        Assert.assertFalse(resultsInclude(63L, resultsNoFilter));

    }

    @Test
    public void getTagExtendedList_ParentTagsFilter() {
        TagFilterType filter = TagFilterType.ParentTags;
        List<TagType> tagTypes = new ArrayList<>();
        tagTypes.add(TagType.Ingredient);
        tagTypes.add(TagType.NonEdible);

        // test call
        List<TagExtendedEntity> result = tagService.getTagExtendedList(filter, tagTypes);

        // confirm results
        // should include rice and prepared meat (14 and 88)
        Assert.assertTrue(tagExtendedResultsInclude(14L, result));
        Assert.assertTrue(tagExtendedResultsInclude(88L, result));
        // should not include preparation type and special diet (157 and 246)
        Assert.assertFalse(tagExtendedResultsInclude(157L, result));
        Assert.assertFalse(tagExtendedResultsInclude(246L, result));
        // should  include cultural roots but not frozen (403 and 35)
        Assert.assertTrue(tagExtendedResultsInclude(403L, result));
        Assert.assertFalse(tagExtendedResultsInclude(35L, result));
        // should not  include ease of prep 4 and soft cat food (3 and 63)
        Assert.assertFalse(tagExtendedResultsInclude(3L, result));
        Assert.assertFalse(tagExtendedResultsInclude(63L, result));

        // test call without call types
        List<TagExtendedEntity> resultsNoFilter = tagService.getTagExtendedList(filter, null);

        // confirm results
        // should  include rice and prepared meat (14 and 88)
        Assert.assertTrue(tagExtendedResultsInclude(14L, resultsNoFilter));
        Assert.assertTrue(tagExtendedResultsInclude(88L, resultsNoFilter));
        // should not include preparation type and special diet (157 and 246)
        Assert.assertTrue(tagExtendedResultsInclude(157L, resultsNoFilter));
        Assert.assertTrue(tagExtendedResultsInclude(246L, resultsNoFilter));
        // should not include cultural roots and frozen (403 and 35)
        Assert.assertTrue(tagExtendedResultsInclude(403L, resultsNoFilter));
        Assert.assertTrue(tagExtendedResultsInclude(35L, resultsNoFilter));
        // should not  include ease of prep 4 and soft cat food (3 and 63)
        Assert.assertFalse(tagExtendedResultsInclude(3L, resultsNoFilter));
        Assert.assertFalse(tagExtendedResultsInclude(63L, resultsNoFilter));

    }

    @Test
    public void getTagList_All() {
        TagFilterType filter = TagFilterType.All;
        List<TagType> tagTypes = new ArrayList<>();
        tagTypes.add(TagType.Ingredient);
        tagTypes.add(TagType.NonEdible);

        // test call
        List<TagEntity> result = tagService.getTagList(filter, tagTypes);

        // confirm results
        // should  include rice and prepared meat (14 and 88)
        Assert.assertTrue(resultsInclude(163L, result));
        Assert.assertTrue(resultsInclude(74L, result));
        // should not include preparation type and special diet (157 and 246)
        Assert.assertFalse(resultsInclude(157L, result));
        Assert.assertFalse(resultsInclude(246L, result));
        // should not include cultural roots and frozen (403 and 35)
        Assert.assertTrue(resultsInclude(403L, result));
        Assert.assertFalse(resultsInclude(35L, result));
        // should include sponge and salt and pepper shaker (3 and 63)
        Assert.assertTrue(resultsInclude(3L, result));
        Assert.assertTrue(resultsInclude(63L, result));

        // should not included deleted
        // test call without call types
        List<TagEntity> resultsNoFilter = tagService.getTagList(filter, null);

        // confirm results
        // should  include rice and prepared meat (14 and 88)
        Assert.assertTrue(resultsInclude(163L, resultsNoFilter));
        Assert.assertTrue(resultsInclude(74L, resultsNoFilter));
        // should  include preparation type and special diet (157 and 246)
        Assert.assertTrue(resultsInclude(157L, resultsNoFilter));
        Assert.assertTrue(resultsInclude(246L, resultsNoFilter));
        // should  include cultural roots and frozen (403 and 35)
        Assert.assertTrue(resultsInclude(403L, resultsNoFilter));
        Assert.assertTrue(resultsInclude(35L, resultsNoFilter));
        // should include sponge and salt and pepper shaker (3 and 63)
        Assert.assertTrue(resultsInclude(3L, result));
        Assert.assertTrue(resultsInclude(63L, result));


    }


    private boolean resultsInclude(Long tagId, List<TagEntity> tagList) {
        Optional<TagEntity> foundTag = tagList.stream().filter(tag -> tag.getId().equals(tagId)).findFirst();
        return foundTag.isPresent();
    }

    private boolean tagExtendedResultsInclude(Long tagId, List<TagExtendedEntity> tagList) {
        Optional<TagExtendedEntity> foundTag = tagList.stream().filter(tag -> tag.getId().equals(tagId)).findFirst();
        return foundTag.isPresent();
    }

    @Test
    public void createTag()  {
        LocalDateTime beforeCreate = LocalDateTime.now();
        TagEntity parent = tagService.getTagById(TestConstants.TAG_TO_BE_PARENT);
        TagEntity newTag = new TagEntity();
        newTag.setTagType(TagType.Ingredient);
        newTag.setName("baby rutabegas");
        newTag.setDescription("are cute");
        TagEntity result = tagService.createTag(parent, newTag);
        Long resultId = result.getId();

        TagEntity resultTest = tagService.getTagById(resultId);
        Assert.assertNotNull(resultTest);
        Assert.assertNotNull(resultTest.getId());
        Assert.assertNotNull(resultTest.getCreatedOn());
        LocalDateTime time = LocalDateTime.ofInstant(result.getCreatedOn().toInstant(), ZoneId.systemDefault());
        int nanos = Duration.between(beforeCreate, time).getNano();
        Assert.assertTrue(nanos > 0);
    }

    @Test
    public void updateTag() throws InterruptedException {
        LocalDateTime beforeCreate = LocalDateTime.now();
        sleep(500);
        TagEntity tagToUpdate = tagService.getTagById(TestConstants.TAG_TO_BE_PARENT);
        String newname = "rutaruta";
        String newdescription = "be be be be begas";
        tagToUpdate.setName(newname);
        tagToUpdate.setDescription(newdescription);

        tagService.updateTag(TestConstants.TAG_TO_BE_PARENT, tagToUpdate);

        TagEntity result = tagService.getTagById(TestConstants.TAG_TO_BE_PARENT);

        Assert.assertNotNull(result);
        Assert.assertEquals(newname, result.getName());
        Assert.assertEquals(newdescription, result.getDescription());
        Assert.assertNotNull(result.getUpdatedOn());
        LocalDateTime time = LocalDateTime.ofInstant(result.getUpdatedOn().toInstant(), ZoneId.systemDefault());
        int nanos = Duration.between(beforeCreate, time).getNano();
        Assert.assertTrue(nanos > 0);

    }

    @Test
    public void testGetTagsForDish()  {
        List<TagEntity> tags = tagService.getTagsForDish("testuser", 500L);

        Assert.assertNotNull(tags);
        Assert.assertTrue(tags.size() > 0);
        Assert.assertTrue(tags.size() == 2);

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
        DishEntity dish = dishService.getDishForUserById(TestConstants.USER_1_NAME, TestConstants.DISH_4_ID);

        // get tags for dish
        List<TagEntity> tags = tagService.getTagsForDish(TestConstants.USER_1_NAME, dish.getId());

        // assert includes 400
        Optional<TagEntity> testTag = tags.stream().filter(t -> t.getId().equals(400L)).findFirst();
        Assert.assertTrue(testTag.isPresent());

        // increment
        tagService.incrementDishRating(TestConstants.USER_1_NAME, TestConstants.DISH_4_ID, 291L, SortOrMoveDirection.DOWN);

        // get tags for dish
        tags = tagService.getTagsForDish(TestConstants.USER_1_NAME, dish.getId());

        // assert doesn't include 400
        testTag = tags.stream().filter(t -> t.getId().equals(400L)).findFirst();
        Assert.assertFalse(testTag.isPresent());

        // assert includes 399
        testTag = tags.stream().filter(t -> t.getId().equals(401L)).findFirst();
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


    @Test
    public void getTagById() {
        // get tag carrots
        TagEntity result = tagService.getTagById(TAG_CARROTS);
        Assert.assertNotNull(result);
        Assert.assertEquals("carrots", result.getName());
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


    @Test
    public void saveTagForDelete() {
    }


    @Test
    public void assignTagToParent() {
    }


    @Test
    public void replaceTagInDishes() {
    }




}