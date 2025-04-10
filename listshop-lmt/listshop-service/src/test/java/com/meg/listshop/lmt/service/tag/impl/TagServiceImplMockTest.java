package com.meg.listshop.lmt.service.tag.impl;

import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.lmt.api.exception.ActionInvalidException;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.data.entity.DishEntity;
import com.meg.listshop.lmt.data.entity.DishItemEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.pojos.ICountResult;
import com.meg.listshop.lmt.data.pojos.TagInfoDTO;
import com.meg.listshop.lmt.data.repository.DishItemRepository;
import com.meg.listshop.lmt.data.repository.CustomTagInfoRepository;
import com.meg.listshop.lmt.data.repository.ListLayoutCategoryRepository;
import com.meg.listshop.lmt.data.repository.TagRepository;
import com.meg.listshop.lmt.service.*;
import com.meg.listshop.lmt.service.food.FoodService;
import com.meg.listshop.lmt.service.tag.TagReplaceService;
import com.meg.listshop.lmt.service.tag.TagService;
import com.meg.listshop.lmt.service.tag.TagStructureService;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class TagServiceImplMockTest {
    private TagService tagService;

    @MockBean
    private DishService dishService;
    @MockBean
    private DishSearchService dishSearchService;
    @MockBean
    private ListTagStatisticService tagStatisticService;
    @MockBean
    private TagReplaceService tagReplaceService;
    @MockBean
    private TagRepository tagRepository;
    @MockBean
    private TagStructureService tagStructureService;
    @MockBean
    private UserService userService;
    @MockBean
    private DishItemRepository dishItemRepository;

    @MockBean
    CustomTagInfoRepository tagInfoCustomRepository;
    @MockBean
    FoodService foodService;
    @MockBean
    ListLayoutCategoryRepository listLayoutCategoryRepository;
    @MockBean
    LayoutService listLayoutService;


    private static class CountResult implements ICountResult {
        int countResult;

        CountResult(int countResult) {
            this.countResult = countResult;
        }

        @Override
        public Integer getCountResult() {
            return countResult;
        }

    }

    @BeforeEach
    void setUp() {
        this.tagService = new TagServiceImpl(
                tagStatisticService, dishService, tagStructureService, tagReplaceService,
                tagRepository, tagInfoCustomRepository, dishSearchService, dishItemRepository,
                listLayoutCategoryRepository
        );
    }

    @Test
    void saveTagForDelete() {
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
        Assertions.assertEquals(true, argument.getValue().isToDelete());
        // replace tag id = replaceTagId
        Assertions.assertEquals(replaceTagId, argument.getValue().getReplacementTagId());
        // removed on less than 1 minute
        Date date = argument.getValue().getRemovedOn();
        Assertions.assertNotNull(date);
        Date compareDate = DateUtils.addMinutes(new Date(), -1);
        Assertions.assertTrue(date.after(compareDate));

    }

    @Test
    void testGetTagById_OK() {
        Long tagId = 99L;
        TagEntity tag = new TagEntity();
        tag.setId(tagId);

        Mockito.when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));

        TagEntity result = tagService.getTagById(tagId);

        Mockito.verify(tagRepository, times(1)).findById(tagId);

        Assertions.assertNotNull(result);
    }

    @Test
    void testGetTagById_Replacement() {
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

        Assertions.assertNotNull(result);
        Assertions.assertEquals(replaceTagId, result.getId());

        Mockito.reset(tagRepository);
        // set replacement to null - expect null
        tag.setReplacementTagId(null);
        Mockito.when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));

        result = tagService.getTagById(tagId);

        Mockito.verify(tagRepository, times(1)).findById(tagId);

        Assertions.assertNull(result);


    }

    @Test
    void saveTagForDelete_NullTag() {
        Long tagId = 99L;
        Long replaceTagId = 88L;
        TagEntity tag = new TagEntity();
        tag.setId(tagId);
        TagEntity replaceTag = new TagEntity();
        replaceTag.setId(replaceTagId);

        Mockito.when(tagRepository.findById(tagId)).thenReturn(Optional.empty());
        Mockito.when(tagRepository.findById(replaceTagId)).thenReturn(Optional.of(replaceTag));


        // call
        Assertions.assertThrows(ActionInvalidException.class,
                () -> tagService.saveTagForDelete(tagId, replaceTagId));
    }

    @Test
    void testUpdateTag() {
        Long tagId = 999L;
        TagEntity tag = ServiceTestUtils.buildTagEntity(tagId, "tagName", TagType.DishType);
        TagEntity copyfrom = ServiceTestUtils.buildTagEntity(tagId, "copied", TagType.Ingredient);
        copyfrom.setDescription("description");
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
        Assertions.assertNotNull(verifyCapture);

        Assertions.assertEquals(copyfrom.getDescription(), verifyCapture.getDescription());
        Assertions.assertEquals(copyfrom.getPower(), verifyCapture.getPower());
        Assertions.assertEquals(copyfrom.isToDelete(), verifyCapture.isToDelete());
        Assertions.assertEquals(copyfrom.getRemovedOn(), verifyCapture.getRemovedOn());
        Assertions.assertEquals(copyfrom.getCreatedOn(), verifyCapture.getCreatedOn());
        Assertions.assertEquals(copyfrom.getCategoryUpdatedOn(), verifyCapture.getCategoryUpdatedOn());
        Assertions.assertEquals(copyfrom.getReplacementTagId(), verifyCapture.getReplacementTagId());

        Assertions.assertNotNull(verifyCapture.getUpdatedOn());

    }


    @Test
    void testGetRatingUpdateInfo_OneEmptyRating() {
        String userName = "george";
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(userName);
        userEntity.setEmail(userName);
        userEntity.setId(2000L);

        TagInfoDTO parentRating = buildTagInfoDTO(100L, "Taste Rating", 0.0, 13L);
        TagInfoDTO rating1 = buildTagInfoDTO(10L, "Taste 1", 1.0, 100L);
        TagInfoDTO rating2 = buildTagInfoDTO(11L, "Taste 2", 2.0, 100L);
        TagInfoDTO rating3 = buildTagInfoDTO(12L, "Taste 3", 3.0, 100L);
        List<TagInfoDTO> ratingTagList = Arrays.asList(parentRating, rating1, rating2, rating3);

        DishEntity dish1 = ServiceTestUtils.buildDish(300L, "great dish", Collections.singletonList(ServiceTestUtils.buildDishItemFromTag(112L, 12L, "Taste 3", TagType.Rating, 3.0)));
        DishEntity dish2 = ServiceTestUtils.buildDish(300L, "great dish2", Collections.singletonList(ServiceTestUtils.buildDishItemFromTag(113L, 12L, "Taste 3", TagType.Rating, 3.0)));
        DishEntity dish3 = ServiceTestUtils.buildDish(300L, "great dish3", Collections.singletonList(ServiceTestUtils.buildDishItemFromTag(114L, 10000L, "Taste 1", TagType.Ingredient, 1.0)));
        List<DishEntity> testDishes = Arrays.asList(dish1, dish2, dish3);


        Mockito.when(dishService.getDishes(userName, Arrays.asList(1L, 2L, 3L)))
                .thenReturn(testDishes);
        Mockito.when(tagInfoCustomRepository.retrieveTagInfoByUser(null, Collections.singletonList(TagType.Rating))).thenReturn(ratingTagList);
        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(userEntity);

        RatingUpdateInfo updateInfo = tagService.getRatingUpdateInfoForDishIds(Arrays.asList(1L, 2L, 3L));

        // defaults are returned for dishes, even if not assigned.
        // contract is that ratings are always returned

        Assertions.assertNotNull(updateInfo);
        Assertions.assertNotNull(updateInfo.getRatingHeaders());
        Assertions.assertNotNull(updateInfo.getDishRatingInfoSet());
        Assertions.assertEquals(3, updateInfo.getDishRatingInfoSet().size());
        for (DishRatingInfo info : updateInfo.getDishRatingInfoSet()) {

            Assertions.assertNotNull(info.getRatings());
            Assertions.assertEquals(1, info.getRatings().size());
        }

        Set<RatingInfo> testHeaders = updateInfo.getRatingHeaders();
        for (RatingInfo toTest : testHeaders) {
            Assertions.assertNotNull(toTest.getMaxPower());
        }
    }

    @Test
    void testGetRatingUpdateInfo_FilledRatings() {
        String userName = "UserName";
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(userName);
        userEntity.setEmail(userName);
        userEntity.setId(2000L);

        TagInfoDTO parentRating = buildTagInfoDTO(100L, "Taste Rating", 0.0, 13L);
        TagInfoDTO rating1 = buildTagInfoDTO(10L, "Taste 1", 1.0, 100L);
        TagInfoDTO rating2 = buildTagInfoDTO(11L, "Taste 2", 2.0, 100L);
        TagInfoDTO rating3 = buildTagInfoDTO(12L, "Taste 3", 3.0, 100L);

        Long dish1Id = 1L;
        Long dish2Id = 2L;
        Long dish3Id = 3L;

        Mockito.when(tagInfoCustomRepository.retrieveTagInfoByUser(null, Collections.singletonList(TagType.Rating)))
                .thenReturn(Arrays.asList(parentRating, rating1, rating2, rating3));
        Mockito.when(tagInfoCustomRepository.retrieveRatingInfoForDish(dish1Id)).thenReturn(new ArrayList<>());
        Mockito.when(tagInfoCustomRepository.retrieveRatingInfoForDish(dish2Id)).thenReturn(Collections.singletonList(rating3));
        Mockito.when(tagInfoCustomRepository.retrieveRatingInfoForDish(dish3Id)).thenReturn(Collections.singletonList(rating3));

        // call under test
        RatingUpdateInfo updateInfo = tagService.getRatingUpdateInfoForDishIds(Arrays.asList(1L, 2L, 3L));


        Assertions.assertNotNull(updateInfo);
        Assertions.assertNotNull(updateInfo.getDishRatingInfoSet());
        Assertions.assertEquals(3, updateInfo.getDishRatingInfoSet().size());
        for (DishRatingInfo info : updateInfo.getDishRatingInfoSet()) {
            Assertions.assertNotNull(info.getRatings());
            Assertions.assertEquals(1, info.getRatings().size());
        }
    }

    @Test
    void getDictionaryForIds() {
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
        Assertions.assertNotNull(dictionary);
        Assertions.assertEquals(5L, dictionary.size());
        dictionary.forEach((key, value) -> Assertions.assertEquals(key, value.getId()));
    }

    @Test
    void testAddTagToDish_OK() {
        String userName = "userName@test.com";
        Long userId = 66L;
        Long dishId = 666L;
        Long tagId = 6666L;

        TagEntity newTag = ServiceTestUtils.buildTagEntity(tagId, "new tag", TagType.Ingredient);

        DishTestBuilder dishTestBuilder = new DishTestBuilder()
                .withDishId(dishId)
                .withUserId(userId)
                .withTag(ServiceTestUtils.buildTagEntity(1L, "tagone", TagType.Ingredient))
                .withTag(ServiceTestUtils.buildTagEntity(2L, "tagtwo", TagType.Ingredient))
                .withTag(ServiceTestUtils.buildTagEntity(3L, "tagthree", TagType.Ingredient));
        DishEntity dish = dishTestBuilder.build();


        ArgumentCaptor<DishEntity> argumentCaptor = ArgumentCaptor.forClass(DishEntity.class);

        Mockito.when(dishService.getDishForUserById(userId, dishId)).thenReturn(dish);
        Mockito.when(tagRepository.findById(tagId)).thenReturn(Optional.of(newTag));

        // tested call
        tagService.addTagToDish(userId, dishId, tagId);

        Mockito.verify(dishService, times(1)).save(argumentCaptor.capture(), anyBoolean());
        Mockito.verify(tagStatisticService, times(1)).countTagAddedToDish(userId, tagId);

        DishEntity resultDish = argumentCaptor.getValue();
        Assertions.assertNotNull(resultDish);
        List<TagEntity> resultTags = resultDish.getItems().stream().map(DishItemEntity::getTag).collect(Collectors.toList());
        Assertions.assertNotNull(resultTags);
        Assertions.assertEquals(4, resultTags.size());
        Assertions.assertTrue(resultTags.stream().anyMatch(t -> t.getId().equals(tagId)));
    }


    @Test
    void testIncrementRatingUp() {
        String userName = "userName@test.com";
        Long userId = 66L;
        Long dishId = 666L;
        Long tagId = 6666L;
        Long ratingId = 9999999L;


        TagEntity currentTag = ServiceTestUtils.buildTagEntity(tagId, "parent tag", TagType.Rating);

        DishEntity dish = new DishEntity();
        dish.setId(dishId);
        dish.setUserId(userId);

        Mockito.when(dishService.getDishForUserById(userId, dishId)).thenReturn(dish);
        Mockito.when(tagRepository.getAssignedTagForRating(dishId, ratingId)).thenReturn(currentTag);
        Mockito.when(tagRepository.getNextRatingUp(ratingId, currentTag.getId())).thenReturn(currentTag);
        Mockito.when(dishService.getDishForUserById(userId, dishId)).thenReturn(dish);


        // test call - increment
        tagService.incrementDishRating(userId, dishId, ratingId, SortOrMoveDirection.UP);

        Mockito.verify(dishService, times(1)).getDishForUserById(userId, dishId);
        Mockito.verify(tagRepository, times(1)).getAssignedTagForRating(dishId, ratingId);
        Mockito.verify(tagRepository, times(1)).getNextRatingUp(ratingId, currentTag.getId());
        Mockito.verify(dishService, times(1)).getDishForUserById(userId, dishId);

    }

    @Test
    void testIncrementRatingDown() {
        String userName = "userName@test.com";
        Long userId = 66L;
        Long dishId = 666L;
        Long tagId = 6666L;
        Long ratingId = 9999999L;

        TagEntity currentTag = ServiceTestUtils.buildTagEntity(tagId, "parent tag", TagType.Rating);

        DishEntity dish = new DishEntity();
        dish.setId(dishId);
        dish.setUserId(userId);

        Mockito.when(dishService.getDishForUserById(userId, dishId)).thenReturn(dish);
        Mockito.when(tagRepository.getAssignedTagForRating(dishId, ratingId)).thenReturn(currentTag);
        Mockito.when(tagRepository.getNextRatingDown(ratingId, currentTag.getId())).thenReturn(currentTag);
        Mockito.when(dishService.getDishForUserById(userId, dishId)).thenReturn(dish);


        // test call - increment
        tagService.incrementDishRating(userId, dishId, ratingId, SortOrMoveDirection.DOWN);

        Mockito.verify(dishService, times(1)).getDishForUserById(userId, dishId);
        Mockito.verify(tagRepository, times(1)).getAssignedTagForRating(dishId, ratingId);
        Mockito.verify(tagRepository, times(1)).getNextRatingDown(ratingId, currentTag.getId());
        Mockito.verify(dishService, times(1)).getDishForUserById(userId, dishId);
    }

    @Test
    void testSetDishRating() {
        String userName = "userName@test.com";
        Long userId = 66L;
        Long dishId = 666L;
        Long tagId = 6666L;
        Long newTagId = 7777L;
        Long ratingId = 10L;
        Integer step = 5;

        TagEntity currentRatingTag = ServiceTestUtils.buildTagEntity(tagId, "current tag - power 1", TagType.Rating);
        TagEntity newRatingTag = ServiceTestUtils.buildTagEntity(newTagId, "next tag - power 5", TagType.Rating);

        DishTestBuilder dishBuilder = new DishTestBuilder()
                .withDishId(dishId)
                .withUserId(userId)
                .withTag(ServiceTestUtils.buildTagEntity(1L, "tagone", TagType.Ingredient))
                .withTag(currentRatingTag);

        List<TagEntity> siblingTags = new ArrayList<>();
        siblingTags.add(currentRatingTag);
        DishEntity dish = dishBuilder.build();

        ArgumentCaptor<DishEntity> argumentCaptor = ArgumentCaptor.forClass(DishEntity.class);

        Mockito.when(dishService.getDishForUserById(userId, dishId)).thenReturn(dish);
        Mockito.when(tagRepository.findRatingTagIdForStep(ratingId, step)).thenReturn(newTagId);
        Mockito.when(tagRepository.findById(newTagId)).thenReturn(Optional.of(newRatingTag));
        Mockito.when(tagStructureService.getSiblingTags(newRatingTag)).thenReturn(siblingTags);

        // test call - increment
        tagService.setDishRating(userId, dishId, ratingId, 5);

        Mockito.verify(dishService, times(1)).getDishForUserById(userId, dishId);
        Mockito.verify(tagRepository, times(1)).findRatingTagIdForStep(ratingId, step);
        Mockito.verify(tagRepository, times(1)).findById(newTagId);
        Mockito.verify(tagStructureService, times(1)).getSiblingTags(newRatingTag);
        Mockito.verify(dishService).save(argumentCaptor.capture(), anyBoolean());

        DishEntity result = argumentCaptor.getValue();
        Assertions.assertNotNull(result);
        List<TagEntity> resultDishTags = result.getItems().stream().map(DishItemEntity::getTag).collect(Collectors.toList());
        Assertions.assertEquals(2, resultDishTags.size());
        Assertions.assertTrue(resultDishTags.stream().anyMatch(t -> t.getId().equals(newTagId)));
        Assertions.assertFalse(resultDishTags.stream().anyMatch(t -> t.getId().equals(tagId)));
    }


    @Test
    void testGetTagsForDish() {
        Long userId = 99L;
        Long dishId = 999L;
        List<TagType> tagTypes = new ArrayList<>();
        tagTypes.add(TagType.Ingredient);
        tagTypes.add(TagType.NonEdible);
        String username = "george";
        DishEntity dish = new DishEntity();
        dish.setId(dishId);
        List<DishItemEntity> allItems = new ArrayList<>();
        allItems.add(ServiceTestUtils.buildDishItemFromTag(199L, 99L, "tag99", TagType.NonEdible));
        allItems.add(ServiceTestUtils.buildDishItemFromTag(1199L, 199L, "tag199", TagType.Ingredient));
        allItems.add(ServiceTestUtils.buildDishItemFromTag(1299L, 299L, "tag299", TagType.DishType));
        dish.setItems(allItems);

        Mockito.when(dishService.getDishForUserById(userId, dishId)).thenReturn(dish);

        List<DishItemEntity> resultList = tagService.getItemsForDish(userId, dishId, tagTypes);

        Assertions.assertNotNull(resultList);
        Assertions.assertEquals(2, resultList.size());
    }


    @Test
    void testGetTagById_NoneFound() {
        Long tagId = 99L;
        TagEntity tag = new TagEntity();
        tag.setId(tagId);


        Mockito.when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        TagEntity result = tagService.getTagById(tagId);

        Mockito.verify(tagRepository, times(1)).findById(tagId);

        Assertions.assertNull(result);
    }


    @Test
    void saveTagForDelete_HasChildren() {
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
        Assertions.assertThrows(ActionInvalidException.class,
                () -> tagService.saveTagForDelete(tagId, replaceTagId));
    }

    @Test
    void testGetReplacedTagsFromIds_NoneFound() {
        Set<Long> searchTagIds = new HashSet<>();

        List<TagEntity> foundTags = new ArrayList<>();

        Mockito.when(tagRepository.findTagsToBeReplaced(searchTagIds))
                .thenReturn(foundTags);

        // call to test
        List<TagEntity> result = tagService.getReplacedTagsFromIds(searchTagIds);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }


    @Test
    void replaceTagInDishes() {
        String userName = "userName";
        Long userId = 99L;
        Long dishId = 10000L;
        Set<Long> tagIds = new HashSet<>();
        tagIds.add(100L);
        tagIds.add(200L);
        tagIds.add(300L);

        DishEntity testDish = new DishEntity();
        List<TagEntity> dishTags = new ArrayList<>();
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
        testDish.setItems(dishTags.stream().map(t -> {
            DishItemEntity item = new DishItemEntity();
            item.setTag(t);
            return item;
        }).collect(Collectors.toList()));

        Mockito.when(dishService.getDishForUserById(userId, dishId))
                .thenReturn(testDish);

        // call under test
        tagService.removeTagsFromDish(userId, dishId, tagIds);

        Mockito.verify(dishService, times(1)).save(testDish, false);

    }

    @Test
    void removeLastDishTypeTagInDish_Valid() {

        String userName = "userName";
        Long userId = 99L;
        Long dishId = 10000L;

        Set<Long> tagIdsToDelete = new HashSet<>();
        tagIdsToDelete.add(100L);
        tagIdsToDelete.add(200L);
        tagIdsToDelete.add(300L);

        Set<Long> existingTagIds = new HashSet<>();
        existingTagIds.add(200L);
        existingTagIds.add(300L);
        existingTagIds.add(400L);
        existingTagIds.add(500L);

        DishTestBuilder testBuilder = new DishTestBuilder()
                .withDishId(dishId);

        existingTagIds.forEach(id -> testBuilder.withTag(id, TagType.DishType));

        DishEntity testDish = testBuilder.build();
        ArgumentCaptor<DishEntity> savedDish = ArgumentCaptor.forClass(DishEntity.class);

        Mockito.when(dishService.getDishForUserById(userId, dishId))
                .thenReturn(testDish);
        Mockito.when(tagRepository.countRemainingDishTypeTags(dishId, existingTagIds))
                .thenReturn(Collections.singletonList(new CountResult(1)));
        Mockito.when(dishService.save(savedDish.capture(), ArgumentMatchers.anyBoolean()))
                .thenReturn(new DishEntity());

        // call under test
        tagService.removeTagsFromDish(userId, dishId, tagIdsToDelete);

        // all tags should be saved, since last dish type tag for dish was not removed
        Assertions.assertNotNull(savedDish.getValue());
        DishEntity saved = savedDish.getValue();
        // should have 2 tags after delete
        Assertions.assertEquals(2, saved.getItems().size());
        Set<Long> tagIdsAfterDelete = saved.getItems().stream()
                .map(DishItemEntity::getTag)
                .map(TagEntity::getId)
                .collect(Collectors.toSet());
        Assertions.assertTrue(tagIdsAfterDelete.contains(400L));
        Assertions.assertTrue(tagIdsAfterDelete.contains(500L));

    }

    @Test
    void removeLastDishTypeTagInDish_Invalid() {

        String userName = "userName";
        Long userId = 99L;
        Long dishId = 10000L;

        Set<Long> tagIdsToDelete = new HashSet<>();
        tagIdsToDelete.add(100L);
        tagIdsToDelete.add(200L);
        tagIdsToDelete.add(300L);

        Set<Long> existingTagIds = new HashSet<>();
        existingTagIds.add(200L);
        existingTagIds.add(300L);
        existingTagIds.add(400L);
        existingTagIds.add(500L);

        DishTestBuilder testBuilder = new DishTestBuilder()
                .withDishId(dishId);

        existingTagIds.forEach(id -> {
            if (id.equals(200L)) {
                testBuilder.withTag(id, TagType.DishType);
            } else {
                testBuilder.withTag(id, TagType.Ingredient);
            }
        });

        DishEntity testDish = testBuilder.build();

        DishTestBuilder dummyBuilder = new DishTestBuilder();
        tagIdsToDelete.forEach(id -> {
            if (id.equals(200L)) {
                dummyBuilder.withTag(id, TagType.DishType);
            } else {
                dummyBuilder.withTag(id, TagType.Ingredient);
            }
        });


        ArgumentCaptor<DishEntity> savedDish = ArgumentCaptor.forClass(DishEntity.class);

        Mockito.when(dishService.getDishForUserById(userId, dishId))
                .thenReturn(testDish);
        Mockito.when(tagRepository.countRemainingDishTypeTags(dishId, tagIdsToDelete))
                .thenReturn(Collections.singletonList(new CountResult(0)));
        Mockito.when(tagRepository.findAllById(tagIdsToDelete)).thenReturn(dummyBuilder.build().getItems().stream().map(DishItemEntity::getTag).collect(Collectors.toList()));
        Mockito.when(dishService.save(savedDish.capture(), ArgumentMatchers.anyBoolean()))
                .thenReturn(new DishEntity());

        // call under test
        tagService.removeTagsFromDish(userId, dishId, tagIdsToDelete);

        // all tags should be saved, since last dish type tag for dish was not removed
        Assertions.assertNotNull(savedDish.getValue());
        DishEntity saved = savedDish.getValue();
        // should have 2 tags after delete
        Assertions.assertEquals(3, saved.getItems().size());
        Set<Long> tagIdsAfterDelete = saved.getItems().stream()
                .map(DishItemEntity::getTag)
                .map(TagEntity::getId).collect(Collectors.toSet());
        Assertions.assertTrue(tagIdsAfterDelete.contains(200L));
        Assertions.assertTrue(tagIdsAfterDelete.contains(400L));
        Assertions.assertTrue(tagIdsAfterDelete.contains(500L));

    }

    @Test
    void testAddTagsToDish() {
        String userName = "test@test.com";
        Long userId = 99L;
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

        Mockito.when(dishService.getDishForUserById(userId, dishId))
                .thenReturn(dish);
        Mockito.when(tagRepository.getTagsForIdList(tagIdSet))
                .thenReturn(tagList);
        Mockito.when(dishService.getDishForUserById(userId, dishId))
                .thenReturn(dish);


        // call under test
        tagService.addTagsToDish(userId, dishId, tagIdSet);

        Mockito.verify(dishService, times(1)).save(dish, false);
    }

    @Test
    void testReplaceTagInDishes() {
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

        // call under test
        tagService.replaceTagInDishes(userId, forTagId, toTagId);
    }

    @Test
    void testGetByIdWithReplace() {
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

        Assertions.assertNotNull(result);
        Assertions.assertEquals(replacementTagId, result.getId());
    }





    private TagInfoDTO buildTagInfoDTO(Long tagId, String tagName,
                                       Double power, Long parentId) {
        return new TagInfoDTO(tagId, tagName, "", power, 0L, TagType.Rating, false, parentId, false);

    }



}