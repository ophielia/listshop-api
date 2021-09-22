package com.meg.listshop.lmt.service.tag.impl;

import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.lmt.api.exception.ActionInvalidException;
import com.meg.listshop.lmt.api.model.TagFilterType;
import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.data.entity.DishEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.entity.TagExtendedEntity;
import com.meg.listshop.lmt.data.repository.TagExtendedRepository;
import com.meg.listshop.lmt.data.repository.TagRepository;
import com.meg.listshop.lmt.service.DishSearchCriteria;
import com.meg.listshop.lmt.service.DishSearchService;
import com.meg.listshop.lmt.service.DishService;
import com.meg.listshop.lmt.service.ListTagStatisticService;
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

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
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
        Long replaceTagId = 88L;
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

        Mockito.when(tagRepository.findById(tagId)).thenReturn(Optional.ofNullable(null));
        Mockito.when(tagRepository.findById(replaceTagId)).thenReturn(Optional.of(replaceTag));


        // call
        tagService.saveTagForDelete(tagId, replaceTagId);

    }

    @Test
    public void testUpdateTag() {
        Long tagId = 999L;
        TagEntity tag = buildTagEntity(tagId, "tagName", TagType.DishType);
        TagEntity copyfrom = buildTagEntity(tagId, "copied", TagType.Ingredient);
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

        Assert.assertNotEquals(copyfrom.getUpdatedOn(), verifyCapture.getUpdatedOn());
        Assert.assertNotNull(verifyCapture.getUpdatedOn());

    }

    @Test
    public void testGetTagList() {
        TagFilterType filter = TagFilterType.ForSelectAssign;
        List<TagType> tagTypes = new ArrayList<>();
        tagTypes.add(TagType.NonEdible);
        tagTypes.add(TagType.Rating);
        List<TagEntity> allTags = new ArrayList<>();
        allTags.add(buildTagEntity(99L, "tag99", TagType.NonEdible));
        allTags.add(buildTagEntity(199L, "tag199", TagType.Ingredient));
        allTags.add(buildTagEntity(299L, "tag299", TagType.DishType));

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
        LocalDateTime beforeCreate = LocalDateTime.now();
        TagEntity parent = buildTagEntity(9L, "ParanetTag", TagType.Ingredient);
        TagEntity createdTag = buildTagEntity(999L, "ParanetTag", TagType.Ingredient);
        String name = "new tag name";

        ArgumentCaptor<TagEntity> argument = ArgumentCaptor.forClass(TagEntity.class);

        Mockito.when(tagRepository.save(argument.capture())).thenReturn(createdTag);

        // call
        tagService.createTag(parent, name);

        Assert.assertNotNull(argument.getValue());
        TagEntity savedTag = argument.getValue();
        Assert.assertTrue(savedTag.getAssignSelect());
        Assert.assertFalse(savedTag.getSearchSelect());
        Assert.assertFalse(savedTag.isToDelete());
        Assert.assertNotNull(savedTag.getCreatedOn());


        // MM will need to test with null parent id
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
        allTags.add(buildTagEntity(99L, "tag99", TagType.NonEdible));
        allTags.add(buildTagEntity(199L, "tag199", TagType.Ingredient));
        allTags.add(buildTagEntity(299L, "tag299", TagType.DishType));

        Mockito.when(dishService.getDishForUserById(username, dishId)).thenReturn(dish);
        Mockito.when(tagRepository.findTagsByDishes(dish)).thenReturn(allTags);

        List<TagEntity> resultList = tagService.getTagsForDish(username, dishId, tagTypes);

        Mockito.verify(tagRepository, times(1)).findTagsByDishes(dish);

        Assert.assertNotNull(resultList);
        Assert.assertEquals("Size should be 2.", 2, resultList.size());
    }

    private TagEntity buildTagEntity(Long tagId, String tagName,
                                     TagType tagType) {
        TagEntity tag = new TagEntity();
        tag.setId(tagId);
        tag.setName(tagName);
        tag.setTagType(tagType);
        return tag;
    }

    private TagExtendedEntity buildTagExtendedEntity(Long tagId, String tagName,
                                                     TagType tagType) {
        TagExtendedEntity tag = new TagExtendedEntity(buildTagEntity(tagId, tagName, tagType));
        return tag;
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


        Mockito.when(tagStructureService.getParentTag(tag))
                .thenReturn(originalParentTag);
        Mockito.when(tagStructureService.assignTagToParent(tag, parentTag))
                .thenReturn(parentTag);

        // call to test
        tagService.assignTagToParent(tag, parentTag);


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

        TagEntity tag1 = new TagEntity();
        tag1.setId(1L);
        tag1.setTagType(TagType.Ingredient);
        TagEntity tag2 = new TagEntity();
        tag2.setId(2L);
        tag2.setTagType(TagType.Ingredient);
        TagEntity tag3 = new TagEntity();
        tag3.setId(3L);
        tag3.setTagType(TagType.Ingredient);

        DishEntity dish = new DishEntity();
        dish.setId(dishId);

        Mockito.when(dishService.getDishForUserById(userName, dishId))
                .thenReturn(dish);
        Mockito.when(tagRepository.findById(1L))
                .thenReturn(Optional.of(tag1));
        Mockito.when(tagRepository.findTagsByDishes(dish))
                .thenReturn(new ArrayList<>());
        Mockito.when(dishService.getDishForUserById(userName, dishId))
                .thenReturn(dish);
        Mockito.when(tagRepository.findById(2L))
                .thenReturn(Optional.of(tag2));
        Mockito.when(tagRepository.findTagsByDishes(dish))
                .thenReturn(new ArrayList<>());
        Mockito.when(dishService.getDishForUserById(userName, dishId))
                .thenReturn(dish);
        Mockito.when(tagRepository.findById(3L))
                .thenReturn(Optional.of(tag3));
        Mockito.when(tagRepository.findTagsByDishes(dish))
                .thenReturn(new ArrayList<>());


        // call under test
        tagService.addTagsToDish(userName, dishId, tagIdSet);

        Mockito.verify(dishService, times(3)).save(dish, false);
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
        TagEntity result = tagService.getTagById(tagId, true);

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


}