package com.meg.listshop.lmt.service.tag.impl;

import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.lmt.api.exception.ActionInvalidException;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.data.entity.DishEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.entity.TagExtendedEntity;
import com.meg.listshop.lmt.data.repository.TagExtendedRepository;
import com.meg.listshop.lmt.data.repository.TagRepository;
import com.meg.listshop.lmt.service.DishSearchCriteria;
import com.meg.listshop.lmt.service.DishSearchService;
import com.meg.listshop.lmt.service.DishService;
import com.meg.listshop.lmt.service.ListTagStatisticService;
import com.meg.listshop.lmt.service.impl.ServiceTestUtils;
import com.meg.listshop.lmt.service.tag.TagReplaceService;
import com.meg.listshop.lmt.service.tag.TagService;
import com.meg.listshop.lmt.service.tag.TagStructureService;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.times;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class TagServiceImplMockTest {
    private TagService tagService;

    @MockBean
    private DishService dishService;
    @MockBean
    private DishSearchService dishSearchService;
    @MockBean
    private ListTagStatisticService tagStatisticService;
    @MockBean
    private TagExtendedRepository tagExtendedRepository;
    @MockBean
    private TagReplaceService tagReplaceService;
    @MockBean
    private TagRepository tagRepository;
    @MockBean
    private TagStructureService tagStructureService;
    @MockBean
    private UserService userService;


    @Before
    public void setUp() {
        this.tagService = new TagServiceImpl(
                tagStatisticService, dishService, tagStructureService, tagReplaceService, tagExtendedRepository, tagRepository, userService, dishSearchService
        );
    }

    @Test
    public void saveTagForDelete() {
        Long tagId = 99L;
        Long replaceTagId = 88L;
        TagEntity tag = new TagEntity();
        tag.setId(tagId);
        TagEntity replaceTag = new TagEntity();
        replaceTag.setId(replaceTagId);

        Mockito.when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));
        Mockito.when(tagRepository.findById(replaceTagId)).thenReturn(Optional.of(replaceTag));

        Mockito.when(tagStructureService.getChildren(tag)).thenReturn(new ArrayList<>());

        ArgumentCaptor<TagEntity> argument = ArgumentCaptor.forClass(TagEntity.class);
        tagReplaceService.replaceTag(tagId, replaceTagId);


        // call
        tagService.saveTagForDelete(tagId, replaceTagId);

        Mockito.verify(tagRepository).save(argument.capture());

        // assert in capture -

        // toDelete true
        Assert.assertEquals(true, argument.getValue().isToDelete());
        // replace tag id = replaceTagId
        Assert.assertEquals(replaceTagId, argument.getValue().getReplacementTagId());
        // removed on less than 1 minute
        Date date = argument.getValue().getRemovedOn();
        Assert.assertNotNull(date);
        Date compareDate = DateUtils.addMinutes(new Date(), -1);
        Assert.assertTrue(date.after(compareDate));

    }

    @Test
    public void testGetTagById_OK() {
        Long tagId = 99L;
        TagEntity tag = new TagEntity();
        tag.setId(tagId);

        Mockito.when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));

        TagEntity result = tagService.getTagById(tagId);

        Mockito.verify(tagRepository, times(1)).findById(tagId);

        Assert.assertNotNull(result);
    }

    @Test
    public void testGetTagById_Replacement() {
        Long tagId = 99L;
        Long replaceTagId = 88L;
        TagEntity tag = new TagEntity();
        tag.setId(tagId);
        tag.setToDelete(true);
        tag.setReplacementTagId(replaceTagId);
        TagEntity replacement = new TagEntity();
        replacement.setId(replaceTagId);


        Mockito.when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));
        Mockito.when(tagRepository.findById(replaceTagId)).thenReturn(Optional.of(replacement));

        TagEntity result = tagService.getTagById(tagId);

        Mockito.verify(tagRepository, times(1)).findById(tagId);
        Mockito.verify(tagRepository, times(1)).findById(replaceTagId);

        Assert.assertNotNull(result);
        Assert.assertEquals(replaceTagId, result.getId());

        Mockito.reset(tagRepository);
        // set replacement to null - expect null
        tag.setReplacementTagId(null);
        Mockito.when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));

        result = tagService.getTagById(tagId);

        Mockito.verify(tagRepository, times(1)).findById(tagId);

        Assert.assertNull(result);


    }

    @Test(expected = ActionInvalidException.class)
    public void saveTagForDelete_NullTag() {
        Long tagId = 99L;
        Long replaceTagId = 88L;
        TagEntity tag = new TagEntity();
        tag.setId(tagId);
        TagEntity replaceTag = new TagEntity();
        replaceTag.setId(replaceTagId);

        Mockito.when(tagRepository.findById(tagId)).thenReturn(Optional.empty());
        Mockito.when(tagRepository.findById(replaceTagId)).thenReturn(Optional.of(replaceTag));


        // call
        tagService.saveTagForDelete(tagId, replaceTagId);

    }

    @Test
    public void testUpdateTag() {
        Long tagId = 999L;
        TagEntity tag = ServiceTestUtils.buildTagEntity(tagId, "tagName", TagType.DishType);
        TagEntity copyfrom = ServiceTestUtils.buildTagEntity(tagId, "copied", TagType.Ingredient);
        copyfrom.setDescription("description");
        copyfrom.setAssignSelect(true);
        copyfrom.setSearchSelect(true);
        copyfrom.setPower(5.0);
        copyfrom.setToDelete(false);
        copyfrom.setRemovedOn(new Date());
        copyfrom.setCreatedOn(new Date());
        copyfrom.setCategoryUpdatedOn(new Date());
        copyfrom.setReplacementTagId(1000L);
        copyfrom.setUpdatedOn(new Date());

        ArgumentCaptor<TagEntity> argument = ArgumentCaptor.forClass(TagEntity.class);

        Mockito.when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));

        // call
        tagService.updateTag(tagId, copyfrom);

        Mockito.verify(tagRepository).save(argument.capture());
        Mockito.verify(tagRepository, times(1)).findById(tagId);

        TagEntity verifyCapture = argument.getValue();
        Assert.assertNotNull(verifyCapture);

        Assert.assertEquals(copyfrom.getDescription(), verifyCapture.getDescription());
        Assert.assertEquals(copyfrom.getAssignSelect(), verifyCapture.getAssignSelect());
        Assert.assertEquals(copyfrom.getSearchSelect(), verifyCapture.getSearchSelect());
        Assert.assertEquals(copyfrom.getPower(), verifyCapture.getPower());
        Assert.assertEquals(copyfrom.isToDelete(), verifyCapture.isToDelete());
        Assert.assertEquals(copyfrom.getRemovedOn(), verifyCapture.getRemovedOn());
        Assert.assertEquals(copyfrom.getCreatedOn(), verifyCapture.getCreatedOn());
        Assert.assertEquals(copyfrom.getCategoryUpdatedOn(), verifyCapture.getCategoryUpdatedOn());
        Assert.assertEquals(copyfrom.getReplacementTagId(), verifyCapture.getReplacementTagId());

        Assert.assertNotNull(verifyCapture.getUpdatedOn());

    }


    @Test
    public void testGetRatingUpdateInfo_OneEmptyRating() {


        List<TagExtendedEntity> ratingTagList = new ArrayList<>();
        TagExtendedEntity parentRating = buildTagExtendedEntity(100L, "Taste Rating", TagType.Rating, 0.0, 13L);
        TagExtendedEntity rating1 = buildTagExtendedEntity(10L, "Taste 1", TagType.Rating, 1.0, 100L);
        TagExtendedEntity rating2 = buildTagExtendedEntity(11L, "Taste 2", TagType.Rating, 2.0, 100L);
        TagExtendedEntity rating3 = buildTagExtendedEntity(12L, "Taste 3", TagType.Rating, 3.0, 100L);
        ratingTagList = Arrays.asList(parentRating, rating1, rating2, rating3);

        List<DishEntity> testDishes = new ArrayList<>();
        DishEntity dish1 = ServiceTestUtils.buildDish(300L, "great dish", Collections.singletonList(ServiceTestUtils.buildTagEntity(12L, "Taste 3", TagType.Rating, 3.0)));
        DishEntity dish2 = ServiceTestUtils.buildDish(300L, "great dish2", Collections.singletonList(ServiceTestUtils.buildTagEntity(12L, "Taste 3", TagType.Rating, 3.0)));
        DishEntity dish3 = ServiceTestUtils.buildDish(300L, "great dish3", Collections.singletonList(ServiceTestUtils.buildTagEntity(10000L, "Taste 1", TagType.Ingredient, 1.0)));
        testDishes = Arrays.asList(dish1, dish2, dish3);

        Mockito.when(tagExtendedRepository.findTagsByCriteria(Collections.singletonList(TagType.Rating), null))
                .thenReturn(ratingTagList);
        Mockito.when(dishService.getDishes("george", Arrays.asList(1L, 2L, 3L)))
                .thenReturn(testDishes);
        Mockito.when(tagRepository.findTagsByDishes(dish1)).thenReturn(dish1.getTags());
        Mockito.when(tagRepository.findTagsByDishes(dish2)).thenReturn(dish2.getTags());
        Mockito.when(tagRepository.findTagsByDishes(dish3)).thenReturn(dish3.getTags());

        RatingUpdateInfo updateInfo = tagService.getRatingUpdateInfoForDishIds("george", Arrays.asList(1L, 2L, 3L));


        Assert.assertNotNull(updateInfo);
        Assert.assertNotNull(updateInfo.getRatingHeaders());
        Assert.assertEquals(1, updateInfo.getRatingHeaders().size());
        Assert.assertNotNull(updateInfo.getDishRatingInfoSet());
        Assert.assertEquals(3, updateInfo.getDishRatingInfoSet().size());
        for (DishRatingInfo info : updateInfo.getDishRatingInfoSet()) {
            if (info.getDishName().equals("great dish3")) {
                Assert.assertNull(info.getRatings());
            } else {
                Assert.assertNotNull(info.getRatings());
                Assert.assertEquals(1, info.getRatings().size());
            }
        }

        Set<RatingInfo> testHeaders = updateInfo.getRatingHeaders();
        for (RatingInfo toTest : testHeaders) {
            Assert.assertNotNull(toTest.getMaxPower());
        }
    }

    @Test
    public void testGetRatingUpdateInfo_FilledRatings() {


        List<TagExtendedEntity> ratingTagList = new ArrayList<>();
        TagExtendedEntity parentRating = buildTagExtendedEntity(100L, "Taste Rating", TagType.Rating, 0.0, 13L);
        TagExtendedEntity rating1 = buildTagExtendedEntity(10L, "Taste 1", TagType.Rating, 1.0, 100L);
        TagExtendedEntity rating2 = buildTagExtendedEntity(11L, "Taste 2", TagType.Rating, 2.0, 100L);
        TagExtendedEntity rating3 = buildTagExtendedEntity(12L, "Taste 3", TagType.Rating, 3.0, 100L);
        ratingTagList = Arrays.asList(parentRating, rating1, rating2, rating3);

        List<DishEntity> testDishes = new ArrayList<>();
        DishEntity dish1 = ServiceTestUtils.buildDish(300L, "great dish", Collections.singletonList(ServiceTestUtils.buildTagEntity(12L, "Taste 3", TagType.Rating, 3.0)));
        DishEntity dish2 = ServiceTestUtils.buildDish(300L, "great dish2", Collections.singletonList(ServiceTestUtils.buildTagEntity(12L, "Taste 3", TagType.Rating, 3.0)));
        DishEntity dish3 = ServiceTestUtils.buildDish(300L, "great dish3", Collections.singletonList(ServiceTestUtils.buildTagEntity(10L, "Taste 1", TagType.Rating, 1.0)));
        testDishes = Arrays.asList(dish1, dish2, dish3);

        Mockito.when(tagExtendedRepository.findTagsByCriteria(Collections.singletonList(TagType.Rating), null))
                .thenReturn(ratingTagList);
        Mockito.when(dishService.getDishes("george", Arrays.asList(1L, 2L, 3L)))
                .thenReturn(testDishes);
        Mockito.when(tagRepository.findTagsByDishes(dish1)).thenReturn(dish1.getTags());
        Mockito.when(tagRepository.findTagsByDishes(dish2)).thenReturn(dish2.getTags());
        Mockito.when(tagRepository.findTagsByDishes(dish3)).thenReturn(dish3.getTags());

        RatingUpdateInfo updateInfo = tagService.getRatingUpdateInfoForDishIds("george", Arrays.asList(1L, 2L, 3L));


        Assert.assertNotNull(updateInfo);
        Assert.assertNotNull(updateInfo.getRatingHeaders());
        Assert.assertEquals(1, updateInfo.getRatingHeaders().size());
        Assert.assertNotNull(updateInfo.getDishRatingInfoSet());
        Assert.assertEquals(3, updateInfo.getDishRatingInfoSet().size());
        for (DishRatingInfo info : updateInfo.getDishRatingInfoSet()) {
            Assert.assertNotNull(info.getRatings());
            Assert.assertEquals(1, info.getRatings().size());
        }

        Set<RatingInfo> testHeaders = updateInfo.getRatingHeaders();
        for (RatingInfo toTest : testHeaders) {
            Assert.assertNotNull(toTest.getMaxPower());
        }
    }

    @Test
    public void getDictionaryForIds() {
        // make 5 dummy ingredients
        List<TagEntity> tagEntityArrayList = new ArrayList<>();
        tagEntityArrayList.add(ServiceTestUtils.buildTagEntity(1L, "one", TagType.TagType));
        tagEntityArrayList.add(ServiceTestUtils.buildTagEntity(2L, "two", TagType.Ingredient));
        tagEntityArrayList.add(ServiceTestUtils.buildTagEntity(3L, "three", TagType.Rating));
        tagEntityArrayList.add(ServiceTestUtils.buildTagEntity(4L, "four", TagType.NonEdible));
        tagEntityArrayList.add(ServiceTestUtils.buildTagEntity(5L, "one", TagType.DishType));

        Set<Long> tagIds = tagEntityArrayList.stream().map(TagEntity::getId).collect(Collectors.toSet());

        Mockito.when(tagRepository.findAllById(tagIds)).thenReturn(tagEntityArrayList);

        // method under test
        Map<Long, TagEntity> dictionary = tagService.getDictionaryForIds(tagIds);

        // check for dictionary with 5 elements
        Assert.assertNotNull(dictionary);
        Assert.assertEquals(5L, dictionary.size());
        dictionary.entrySet().stream().forEach(e -> Assert.assertEquals(e.getKey(), ((TagEntity) e.getValue()).getId()));
    }

    @Test
    public void testGetTagList() {
        List<TagType> tagTypes = new ArrayList<>();
        tagTypes.add(TagType.NonEdible);
        tagTypes.add(TagType.Rating);
        List<TagEntity> allTags = new ArrayList<>();
        allTags.add(ServiceTestUtils.buildTagEntity(99L, "tag99", TagType.NonEdible));
        allTags.add(ServiceTestUtils.buildTagEntity(199L, "tag199", TagType.Ingredient));
        allTags.add(ServiceTestUtils.buildTagEntity(299L, "tag299", TagType.DishType));

        Mockito.when(tagRepository.findTagsByCriteria(tagTypes, true, null)).thenReturn(allTags);

        tagService.getTagList(TagFilterType.ForSelectAssign, tagTypes);

        Mockito.verify(tagRepository, times(1)).findTagsByCriteria(tagTypes, true, null);

        Mockito.reset(tagRepository);

        // test for select search
        Mockito.when(tagRepository.findTagsByCriteria(tagTypes, null, true)).thenReturn(allTags);

        tagService.getTagList(TagFilterType.ForSelectSearch, tagTypes);

        Mockito.verify(tagRepository, times(1)).findTagsByCriteria(tagTypes, null, true);

        Mockito.reset(tagRepository);


        // test for empty criteria
        Mockito.when(tagRepository.findTagsByCriteria(tagTypes, null, null)).thenReturn(allTags);

        tagService.getTagList(TagFilterType.ParentTags
                , tagTypes);

        Mockito.verify(tagRepository, times(1)).findTagsByCriteria(tagTypes, null, null);

        Mockito.reset(tagRepository);

        // test for empty TagFilterType
        Mockito.when(tagRepository.findTagsByCriteria(tagTypes, null, null)).thenReturn(allTags);

        tagService.getTagList(null
                , tagTypes);

        Mockito.verify(tagRepository, times(1)).findTagsByCriteria(tagTypes, null, null);


    }

    @Test
    public void testCreateTag() {
        Assert.assertEquals(1, 1);

        //MM TODO - make this test
    }

    @Test
    public void testAddTagToDish_OK() {
        String userName = "userName@test.com";
        Long userId = 66L;
        Long dishId = 666L;
        Long tagId = 6666L;

        TagEntity newTag = ServiceTestUtils.buildTagEntity(tagId, "new tag", TagType.Ingredient);

        DishEntity dish = new DishEntity();
        dish.setId(dishId);
        dish.setUserId(userId);

        List<TagEntity> startDishTags = new ArrayList<>();
        startDishTags.add(ServiceTestUtils.buildTagEntity(1L, "tagone", TagType.Ingredient));
        startDishTags.add(ServiceTestUtils.buildTagEntity(2L, "tagtwo", TagType.Ingredient));
        startDishTags.add(ServiceTestUtils.buildTagEntity(3L, "tagthree", TagType.Ingredient));

        ArgumentCaptor<DishEntity> argumentCaptor = ArgumentCaptor.forClass(DishEntity.class);

        Mockito.when(dishService.getDishForUserById(userName, dishId)).thenReturn(dish);
        Mockito.when(tagRepository.findTagsByDishes(dish)).thenReturn(startDishTags);
        Mockito.when(tagRepository.findById(tagId)).thenReturn(Optional.of(newTag));

        // tested call
        tagService.addTagToDish(userName, dishId, tagId);

        Mockito.verify(dishService, times(1)).save(argumentCaptor.capture(), anyBoolean());
        Mockito.verify(tagStatisticService, times(1)).countTagAddedToDish(userId, tagId);

        DishEntity resultDish = argumentCaptor.getValue();
        Assert.assertNotNull(resultDish);
        List<TagEntity> resultTags = resultDish.getTags();
        Assert.assertNotNull(resultTags);
        Assert.assertEquals(4, resultTags.size());
        Assert.assertTrue(resultTags.stream().anyMatch(t -> t.getId().equals(tagId)));
    }


    @Test
    public void testIncrementRatingUp() {
        String userName = "userName@test.com";
        Long userId = 66L;
        Long dishId = 666L;
        Long tagId = 6666L;
        Long nextTagId = 7777L;
        Long ratingId = 9999999L;

        UserEntity user = new UserEntity();
        user.setUsername(userName);
        user.setId(userId);

        TagEntity currentTag = ServiceTestUtils.buildTagEntity(tagId, "parent tag", TagType.Rating);
        TagEntity nextTag = ServiceTestUtils.buildTagEntity(nextTagId, "next tag", TagType.Rating);

        DishEntity dish = new DishEntity();
        dish.setId(dishId);
        dish.setUserId(userId);

        List<TagEntity> startDishTags = new ArrayList<>();
        startDishTags.add(ServiceTestUtils.buildTagEntity(1L, "tagone", TagType.Ingredient));
        startDishTags.add(ServiceTestUtils.buildTagEntity(2L, "tagtwo", TagType.Ingredient));
        startDishTags.add(ServiceTestUtils.buildTagEntity(3L, "tagthree", TagType.Ingredient));

        ArgumentCaptor<DishEntity> argumentCaptor = ArgumentCaptor.forClass(DishEntity.class);

        Mockito.when(dishService.getDishForUserById(userName, dishId)).thenReturn(dish);
        Mockito.when(tagRepository.getAssignedTagForRating(dishId, ratingId)).thenReturn(currentTag);
        Mockito.when(tagRepository.getNextRatingUp(ratingId, currentTag.getId())).thenReturn(currentTag);
        Mockito.when(dishService.getDishForUserById(userName, dishId)).thenReturn(dish);


        // test call - increment
        tagService.incrementDishRating(userName, dishId, ratingId, SortOrMoveDirection.UP);

        Mockito.verify(dishService, times(1)).getDishForUserById(userName, dishId);
        Mockito.verify(tagRepository, times(1)).getAssignedTagForRating(dishId, ratingId);
        Mockito.verify(tagRepository, times(1)).getNextRatingUp(ratingId, currentTag.getId());
        Mockito.verify(dishService, times(1)).getDishForUserById(userName, dishId);

    }

    @Test
    public void testIncrementRatingDown() {
        String userName = "userName@test.com";
        Long userId = 66L;
        Long dishId = 666L;
        Long tagId = 6666L;
        Long nextTagId = 7777L;
        Long ratingId = 9999999L;

        UserEntity user = new UserEntity();
        user.setUsername(userName);
        user.setId(userId);

        TagEntity currentTag = ServiceTestUtils.buildTagEntity(tagId, "parent tag", TagType.Rating);
        TagEntity nextTag = ServiceTestUtils.buildTagEntity(nextTagId, "next tag", TagType.Rating);

        DishEntity dish = new DishEntity();
        dish.setId(dishId);
        dish.setUserId(userId);

        List<TagEntity> startDishTags = new ArrayList<>();
        startDishTags.add(ServiceTestUtils.buildTagEntity(1L, "tagone", TagType.Ingredient));
        startDishTags.add(ServiceTestUtils.buildTagEntity(2L, "tagtwo", TagType.Ingredient));
        startDishTags.add(ServiceTestUtils.buildTagEntity(3L, "tagthree", TagType.Ingredient));

        ArgumentCaptor<DishEntity> argumentCaptor = ArgumentCaptor.forClass(DishEntity.class);

        Mockito.when(dishService.getDishForUserById(userName, dishId)).thenReturn(dish);
        Mockito.when(tagRepository.getAssignedTagForRating(dishId, ratingId)).thenReturn(currentTag);
        Mockito.when(tagRepository.getNextRatingDown(ratingId, currentTag.getId())).thenReturn(currentTag);
        Mockito.when(dishService.getDishForUserById(userName, dishId)).thenReturn(dish);


        // test call - increment
        tagService.incrementDishRating(userName, dishId, ratingId, SortOrMoveDirection.DOWN);

    }

    @Test
    public void testGetTagExtendedList() {
        TagFilterType filter = TagFilterType.ForSelectAssign;
        List<TagType> tagTypes = new ArrayList<>();
        tagTypes.add(TagType.NonEdible);
        tagTypes.add(TagType.Rating);
        List<TagExtendedEntity> allTags = new ArrayList<>();
        allTags.add(buildTagExtendedEntity(99L, "tag99", TagType.NonEdible));
        allTags.add(buildTagExtendedEntity(199L, "tag199", TagType.Ingredient));
        allTags.add(buildTagExtendedEntity(299L, "tag299", TagType.DishType));

        Mockito.when(tagExtendedRepository.findTagsByCriteria(tagTypes, null)).thenReturn(allTags);

        tagService.getTagExtendedList(TagFilterType.ForSelectAssign, tagTypes);

        Mockito.verify(tagExtendedRepository, times(1)).findTagsByCriteria(tagTypes, null);

        Mockito.reset(tagExtendedRepository);

        Mockito.when(tagExtendedRepository.findTagsByCriteria(tagTypes, true)).thenReturn(allTags);

        tagService.getTagExtendedList(TagFilterType.ParentTags, tagTypes);

        Mockito.verify(tagExtendedRepository, times(1)).findTagsByCriteria(tagTypes, true);

    }

    @Test
    public void testGetTagsForDish() {
        Long dishId = 999L;
        List<TagType> tagTypes = new ArrayList<>();
        tagTypes.add(TagType.Ingredient);
        tagTypes.add(TagType.NonEdible);
        String username = "george";
        DishEntity dish = new DishEntity();
        dish.setId(dishId);
        List<TagEntity> allTags = new ArrayList<>();
        allTags.add(ServiceTestUtils.buildTagEntity(99L, "tag99", TagType.NonEdible));
        allTags.add(ServiceTestUtils.buildTagEntity(199L, "tag199", TagType.Ingredient));
        allTags.add(ServiceTestUtils.buildTagEntity(299L, "tag299", TagType.DishType));

        Mockito.when(dishService.getDishForUserById(username, dishId)).thenReturn(dish);
        Mockito.when(tagRepository.findTagsByDishes(dish)).thenReturn(allTags);

        List<TagEntity> resultList = tagService.getTagsForDish(username, dishId, tagTypes);

        Mockito.verify(tagRepository, times(1)).findTagsByDishes(dish);

        Assert.assertNotNull(resultList);
        Assert.assertEquals("Size should be 2.", 2, resultList.size());
    }


    @Test
    public void testGetTagById_NoneFound() {
        Long tagId = 99L;
        TagEntity tag = new TagEntity();
        tag.setId(tagId);


        Mockito.when(tagRepository.findById(tagId)).thenReturn(Optional.ofNullable(null));

        TagEntity result = tagService.getTagById(tagId);

        Mockito.verify(tagRepository, times(1)).findById(tagId);

        Assert.assertNull(result);
    }


    @Test(expected = ActionInvalidException.class)
    public void saveTagForDelete_HasChildren() {
        Long tagId = 99L;
        Long replaceTagId = 88L;
        TagEntity tag = new TagEntity();
        tag.setId(tagId);
        TagEntity replaceTag = new TagEntity();
        replaceTag.setId(replaceTagId);

        List<TagEntity> children = Collections.singletonList(new TagEntity());

        Mockito.when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));
        Mockito.when(tagRepository.findById(replaceTagId)).thenReturn(Optional.of(replaceTag));

        Mockito.when(tagStructureService.getChildren(tag)).thenReturn(children);


        // call
        tagService.saveTagForDelete(tagId, replaceTagId);

    }

    @Test
    public void assignTagToParent() {
    }


    @Test
    public void testGetReplacedTagsFromIds_NoneFound() {
        Set<Long> searchTagIds = new HashSet<>();

        List<TagEntity> foundTags = new ArrayList<>();

        Mockito.when(tagRepository.findTagsToBeReplaced(searchTagIds))
                .thenReturn(foundTags);

        // call to test
        List<TagEntity> result = tagService.getReplacedTagsFromIds(searchTagIds);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }


    @Test
    public void testAssignTagToParent() {
        // fixtures
        Long tagId = 1000L;
        Long parentId = 1001L;
        Long originalParentId = 1003L;
        TagEntity tag = new TagEntity();
        tag.setId(tagId);
        TagEntity parentTag = new TagEntity();
        parentTag.setId(parentId);
        TagEntity originalParentTag = new TagEntity();
        originalParentTag.setId(originalParentId);

        Mockito.when(tagRepository.findById(tagId))
                .thenReturn(Optional.of(tag));
        Mockito.when(tagRepository.findById(parentId))
                .thenReturn(Optional.of(parentTag));
        Mockito.when(tagStructureService.getParentTag(tag))
                .thenReturn(originalParentTag);
        Mockito.when(tagStructureService.assignTagToParent(tag, parentTag))
                .thenReturn(parentTag);

        // call to test
        tagService.assignTagToParent(1000L, 1001L);


    }

    @Test
    public void testAssignTagToParent_FromObjects() {
        // fixtures
        Long tagId = 1000L;
        Long parentId = 1001L;
        Long originalParentId = 1003L;
        TagEntity tag = new TagEntity();
        tag.setId(tagId);
        TagEntity parentTag = new TagEntity();
        parentTag.setId(parentId);
        TagEntity originalParentTag = new TagEntity();
        originalParentTag.setId(originalParentId);


        Mockito.when(tagRepository.findById(tag.getId())).thenReturn(Optional.of(tag));
        Mockito.when(tagRepository.findById(parentTag.getId())).thenReturn(Optional.of(parentTag));
        Mockito.when(tagStructureService.getParentTag(tag))
                .thenReturn(originalParentTag);
        Mockito.when(tagStructureService.assignTagToParent(tag, parentTag))
                .thenReturn(parentTag);

        // call to test
        tagService.assignTagToParent(tag.getId(), parentTag.getId());


    }

    @Test
    public void replaceTagInDishes() {
        String userName = "userName";
        Long dishId = 10000L;
        Set<Long> tagIds = new HashSet<>();
        tagIds.add(100L);
        tagIds.add(200L);
        tagIds.add(300L);

        DishEntity testDish = new DishEntity();
        List<TagEntity> dishTags = new ArrayList<>();
        List<TagEntity> dishTagsAfterDelete = new ArrayList<>();
        TagEntity tag1 = new TagEntity();
        tag1.setId(100L);
        TagEntity tag2 = new TagEntity();
        tag2.setId(200L);
        TagEntity tag3 = new TagEntity();
        tag3.setId(300L);
        TagEntity tag4 = new TagEntity();
        tag4.setId(400L);
        dishTags.add(tag1);
        dishTags.add(tag2);
        dishTags.add(tag3);
        dishTags.add(tag4);
        dishTagsAfterDelete.add(tag4);

        Mockito.when(dishService.getDishForUserById(userName, dishId))
                .thenReturn(testDish, testDish, testDish);
        Mockito.when(tagRepository.findTagsByDishes(testDish))
                .thenReturn(dishTags, dishTags, dishTags);

        // call under test
        tagService.removeTagsFromDish(userName, dishId, tagIds);

        Mockito.verify(dishService, times(3)).save(testDish, false);

    }


    @Test
    public void testAssignChildrenToParent() {
        Long parentId = 999L;
        List<Long> childrenIds = new ArrayList<>();
        childrenIds.add(1L);
        childrenIds.add(2L);
        childrenIds.add(3L);
        TagEntity tag = new TagEntity();
        tag.setId(parentId);
        TagEntity originalParentTag = new TagEntity();
        tag.setId(999L);
        TagEntity child1 = new TagEntity();
        tag.setId(1L);
        TagEntity child2 = new TagEntity();
        tag.setId(2L);
        TagEntity child3 = new TagEntity();
        tag.setId(3L);


        Mockito.when(tagRepository.findById(parentId))
                .thenReturn(Optional.of(tag));
        Mockito.when(tagRepository.findById(1L))
                .thenReturn(Optional.of(child1));
        Mockito.when(tagRepository.findById(2L))
                .thenReturn(Optional.of(child2));
        Mockito.when(tagRepository.findById(3L))
                .thenReturn(Optional.of(child3));
        Mockito.when(tagStructureService.getParentTag(child1))
                .thenReturn(originalParentTag);
        Mockito.when(tagStructureService.assignTagToParent(child1, tag))
                .thenReturn(tag);
        Mockito.when(tagStructureService.getParentTag(child2))
                .thenReturn(originalParentTag);
        Mockito.when(tagStructureService.assignTagToParent(child2, tag))
                .thenReturn(tag);
        Mockito.when(tagStructureService.getParentTag(child3))
                .thenReturn(originalParentTag);
        Mockito.when(tagStructureService.assignTagToParent(child3, tag))
                .thenReturn(tag);

        tagService.assignChildrenToParent(parentId, childrenIds);

    }

    @Test
    public void testAddTagsToDish() {
        String userName = "test@test.com";
        Long dishId = 50L;
        Set<Long> tagIdSet = new HashSet<>();
        tagIdSet.add(1L);
        tagIdSet.add(2L);
        tagIdSet.add(3L);

        List<TagEntity> tagList = new ArrayList<>();
        TagEntity tag1 = new TagEntity();
        tag1.setId(1L);
        tag1.setTagType(TagType.Ingredient);
        TagEntity tag2 = new TagEntity();
        tag2.setId(2L);
        tag2.setTagType(TagType.Ingredient);
        TagEntity tag3 = new TagEntity();
        tag3.setId(3L);
        tag3.setTagType(TagType.Ingredient);
        tagList.add(tag1);
        tagList.add(tag2);
        tagList.add(tag3);

        DishEntity dish = new DishEntity();
        dish.setId(dishId);

        Mockito.when(dishService.getDishForUserById(userName, dishId))
                .thenReturn(dish);
        Mockito.when(tagRepository.getTagsForIdList(tagIdSet))
                .thenReturn(tagList);
        Mockito.when(tagRepository.findTagsByDishes(dish))
                .thenReturn(new ArrayList<>());
        Mockito.when(dishService.getDishForUserById(userName, dishId))
                .thenReturn(dish);


        // call under test
        tagService.addTagsToDish(userName, dishId, tagIdSet);

        Mockito.verify(dishService, times(1)).save(dish, false);
    }

    @Test
    public void testReplaceTagInDishes() {
        String userName = "userName@test.com";
        Long userId = 66L;
        Long forTagId = 99L;
        Long toTagId = 88L;
        Long parentTagId = 77L;

        UserEntity user = new UserEntity();
        user.setUsername(userName);
        user.setId(userId);

        TagEntity fromTag = new TagEntity();
        fromTag.setId(forTagId);
        TagEntity toTag = new TagEntity();
        toTag.setId(toTagId);
        TagEntity parentTag = new TagEntity();
        parentTag.setId(parentTagId);

        List<TagEntity> childrenTags = dummyTagEntities(5);
        List<Long> childrenIds = childrenTags.stream().map(TagEntity::getId).collect(Collectors.toList());
        DishSearchCriteria criteria = new DishSearchCriteria(userId);
        criteria.setIncludedTagIds(Collections.singletonList(toTagId));

        List<DishEntity> dishList = new ArrayList<>();
        dishList.add(new DishEntity());

        List<TagEntity> dishTags = new ArrayList<>();
        dishTags.add(fromTag);
        dishTags.add(parentTag);

        List<TagEntity> afterDishTags = new ArrayList<>();
        afterDishTags.add(toTag);
        afterDishTags.add(parentTag);

        Mockito.when(userService.getUserByUserEmail(userName))
                .thenReturn(user);
        Mockito.when(tagRepository.findById(toTagId))
                .thenReturn(Optional.of(toTag));
        Mockito.when(dishSearchService.findDishes(any(DishSearchCriteria.class)))
                .thenReturn(dishList);
        Mockito.when(tagRepository.findTagsByDishes(dishList.get(0)))
                .thenReturn(dishTags);

        // call under test
        tagService.replaceTagInDishes(userName, forTagId, toTagId);

        DishEntity dish = dishList.get(0);
        dish.setTags(afterDishTags);

        Mockito.verify(dishService).save(dish, false);

    }

    @Test
    public void testGetByIdWithReplace() {
        Long tagId = 99L;
        Long replacementTagId = 100L;
        TagEntity tag = new TagEntity();
        tag.setId(tagId);
        tag.setToDelete(true);
        tag.setReplacementTagId(replacementTagId);
        TagEntity replaceTag = new TagEntity();
        replaceTag.setId(replacementTagId);

        Mockito.when(tagRepository.findById(tagId))
                .thenReturn(Optional.of(tag));

        Mockito.when(tagRepository.findById(replacementTagId))
                .thenReturn(Optional.of(replaceTag));

        // call to be tested
        TagEntity result = tagService.getTagById(tagId);

        Assert.assertNotNull(result);
        Assert.assertEquals(replacementTagId, result.getId());
    }

    private List<TagEntity> dummyTagEntities(int count) {
        List<TagEntity> returnList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            TagEntity tag = new TagEntity();
            tag.setId(Long.valueOf(i));
            returnList.add(tag);
        }
        return returnList;
    }


    private TagExtendedEntity buildTagExtendedEntity(Long tagId, String tagName,
                                                     TagType tagType) {
        return buildTagExtendedEntity(tagId, tagName, tagType, 0.0, 0L);
    }

    private TagExtendedEntity buildTagExtendedEntity(Long tagId, String tagName,
                                                     TagType tagType, Double power, Long parentId) {
        return new TagExtendedEntity(ServiceTestUtils.buildTagEntity(tagId, tagName, tagType, power), parentId);
    }

}