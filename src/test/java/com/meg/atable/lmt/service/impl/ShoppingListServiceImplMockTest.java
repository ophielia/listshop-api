package com.meg.atable.lmt.service.impl;

import com.meg.atable.auth.data.entity.UserEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.lmt.api.model.ItemOperationType;
import com.meg.atable.lmt.api.model.ListGenerateProperties;
import com.meg.atable.lmt.api.model.ListLayoutType;
import com.meg.atable.lmt.api.model.TagType;
import com.meg.atable.lmt.data.entity.*;
import com.meg.atable.lmt.data.repository.ItemChangeRepository;
import com.meg.atable.lmt.data.repository.ItemRepository;
import com.meg.atable.lmt.data.repository.ShoppingListRepository;
import com.meg.atable.lmt.service.*;
import com.meg.atable.lmt.service.tag.TagService;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class ShoppingListServiceImplMockTest {


    private ShoppingListService shoppingListService;
    @MockBean
    private UserService userService;
    @MockBean
    private TagService tagService;
    @MockBean
    private DishService dishService;
    @MockBean
    private ShoppingListProperties shoppingListProperties;
    @MockBean
    private ShoppingListRepository shoppingListRepository;
    @MockBean
    private ListLayoutService listLayoutService;
    @MockBean
    private ListSearchService listSearchService;
    @MockBean
    private MealPlanService mealPlanService;
    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private ItemChangeRepository itemChangeRepository;

    @Before
    public void setUp() {

        shoppingListService = new ShoppingListServiceImpl(userService,
                tagService,
                dishService,
                shoppingListRepository,
                listLayoutService,
                listSearchService,
                mealPlanService,
                itemRepository,
                itemChangeRepository,
                shoppingListProperties);
    }


    @Test
    public void testCreateList_duplicateName() throws ShoppingListException, InvocationTargetException, IllegalAccessException {
        Long userId = 99L;
        String userName = "userName";
        String listName = "ShoppingList";
        // set up fixtures
        ListGenerateProperties properties = new ListGenerateProperties();
        properties.setListName(listName);

        UserEntity user = new UserEntity();
        user.setId(userId);

        ShoppingListEntity listDuplicate1 = new ShoppingListEntity();
        listDuplicate1.setName("ShoppingList");
        ShoppingListEntity listDuplicate2 = new ShoppingListEntity();
        listDuplicate2.setName("ShoppingList 2");
        ShoppingListEntity listDuplicate3 = new ShoppingListEntity();
        listDuplicate3.setName("ShoppingList 3");
        ShoppingListEntity createdList = new ShoppingListEntity();
        createdList.setName("ShoppingList 4");

        ListLayoutEntity listLayout = new ListLayoutEntity();
        BeanUtils.setProperty(listLayout, "id", 666L);

        ArgumentCaptor<ShoppingListEntity> listArgument = ArgumentCaptor.forClass(ShoppingListEntity.class);

        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(user);
        Mockito.when(shoppingListRepository.findByUserIdAndName(userId, listName)).thenReturn(Collections.singletonList(listDuplicate1));
        Mockito.when(shoppingListRepository.findByUserIdAndNameLike(userId, listName + "%"))
                .thenReturn(Arrays.asList(listDuplicate1, listDuplicate2, listDuplicate3));
        Mockito.when(listLayoutService.getListLayoutByType(ListLayoutType.All))
                .thenReturn(listLayout);
        Mockito.when(shoppingListRepository.save(listArgument.capture())).thenReturn(createdList);

        ShoppingListEntity result = shoppingListService.generateListForUser(userName, properties);

        Assert.assertNotNull(result);
        ShoppingListEntity captured = listArgument.getValue();
        Assert.assertNotNull(captured);
        Assert.assertEquals("ShoppingList 4", captured.getName());

    }

    @Test
    public void testGenerateListFromMealPlan() {
        String username = "Eustace";
        Long listId = 99L;
        Long mealPlanId = 999L;
        Long userId = 9L;
        String listName = "list name";

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setEmail(username);

        ShoppingListEntity createdShoppingList = emptyShoppingList(listId, userId, listName);
        List<TagType> tagTypeList = new ArrayList<>();
        tagTypeList.add(TagType.Ingredient);
        tagTypeList.add(TagType.NonEdible);

        // fixtures
        ListLayoutEntity listLayoutEntity = new ListLayoutEntity();
        listLayoutEntity.setLayoutType(ListLayoutType.All);
        MealPlanEntity mealPlan = new MealPlanEntity(mealPlanId);
        // make 6 tags
        TagEntity tag1 = ServiceTestUtils.buildTag(1L, "first tag", TagType.Ingredient);
        TagEntity tag2 = ServiceTestUtils.buildTag(2L, "second tag", TagType.Ingredient);
        TagEntity tag3 = ServiceTestUtils.buildTag(3L, "third tag", TagType.Ingredient);
        TagEntity tag4 = ServiceTestUtils.buildTag(4L, "fourth tag", TagType.Ingredient);
        TagEntity tag5 = ServiceTestUtils.buildTag(5L, "fifth tag", TagType.Ingredient);
        TagEntity tag6 = ServiceTestUtils.buildTag(6L, "outlier tag", TagType.NonEdible);
        // make 2 dishes
        DishEntity dish1 = ServiceTestUtils.buildDish(userId, "dish one", Arrays.asList(tag1, tag2));
        DishEntity dish2 = ServiceTestUtils.buildDish(userId, "dish two", Arrays.asList(tag1, tag3, tag4, tag5, tag6));
        dish1.setId(1212L);
        dish2.setId(2323L);
        // make 2 slots
        SlotEntity slot1 = ServiceTestUtils.buildDishSlot(mealPlan, dish1);
        SlotEntity slot2 = ServiceTestUtils.buildDishSlot(mealPlan, dish2);
        mealPlan.getSlots().add(slot1);
        mealPlan.getSlots().add(slot2);

        ArgumentCaptor<ShoppingListEntity> argument = ArgumentCaptor.forClass(ShoppingListEntity.class);

        // expectations
        Mockito.when(userService.getUserByUserEmail(username))
                .thenReturn(userEntity);
        Mockito.when(shoppingListProperties.getDefaultLayout())
                .thenReturn(ListLayoutType.All);
        Mockito.when(listLayoutService.getListLayouts())
                .thenReturn(Collections.singletonList(listLayoutEntity));
        Mockito.when(listLayoutService.getListLayoutByType(ListLayoutType.All))
                .thenReturn(listLayoutEntity);
        Mockito.when(mealPlanService.getMealPlanById(username, mealPlanId))
                .thenReturn(mealPlan);
        Mockito.when(shoppingListRepository.getWithItemsByListIdAndItemsRemovedOnIsNull(listId))
                .thenReturn(Optional.of(createdShoppingList));
        Mockito.when(tagService.getTagsForDish(username, dish1.getId(), tagTypeList))
                .thenReturn(Arrays.asList(tag1, tag2));
        Mockito.when(tagService.getTagsForDish(username, dish2.getId(), tagTypeList))
                .thenReturn(Arrays.asList(tag1, tag3, tag4, tag5));
        mealPlanService.updateLastAddedDateForDishes(mealPlan);

        itemChangeRepository.saveItemChanges(any(ShoppingListEntity.class), any(ItemCollector.class), eq(userId), any(CollectorContext.class));
        Mockito.when(shoppingListRepository.save(argument.capture()))
                .thenReturn(createdShoppingList);
        Mockito.when(shoppingListRepository.getWithItemsByListIdAndItemsRemovedOnIsNull(listId))
                .thenReturn(Optional.of(createdShoppingList));


        // call
        shoppingListService.generateListFromMealPlan(username, mealPlanId);

        // verification afterwards
        // note - checking captured list
        // list is not null
        ShoppingListEntity listResult = argument.getValue();
        Assert.assertNotNull(listResult);
        // list should contain 6 items
        //Assert.assertEquals(6, listResult.getItems().size());
        // verify items
        // put items into map
        Map<Long, ItemEntity> resultMap = listResult.getItems().stream()
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assert.assertNotNull(resultMap);
        // tag 1 should be there - twise
        Assert.assertNotNull(resultMap.get(1L));
        Assert.assertEquals(2, resultMap.get(1L).getUsedCount().longValue());
        // tags 3 and 4 should be there
        Assert.assertNotNull(resultMap.get(3L));
        Assert.assertEquals(1, resultMap.get(3L).getUsedCount().longValue());
        Assert.assertNotNull(resultMap.get(4L));
        Assert.assertEquals(1, resultMap.get(4L).getUsedCount().longValue());
        // tag 8 should not be there (once)
        Assert.assertNull(resultMap.get(8L));
        // tag 6 should NOT be there
        Assert.assertNull(resultMap.get(6L));

    }

    private ShoppingListEntity emptyShoppingList(Long listId, Long userId, String listName) {
        ShoppingListEntity newList = new ShoppingListEntity();
        // get list layout for user, list_type
        newList.setListLayoutId(33L);
        newList.setName(listName);
        newList.setIsStarterList(false);
        newList.setCreatedOn(new Date());
        newList.setUserId(userId);
        return newList;
    }

    @Test
    public void testAddToListFromMealPlan() {
        String username = "Eustace";
        Long listId = 99L;
        Long mealPlanId = 999L;
        Long userId = 9L;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setEmail(username);

        ShoppingListEntity shoppingList = dummyShoppingList(listId, userId);
        List<TagType> tagTypeList = new ArrayList<>();
        tagTypeList.add(TagType.Ingredient);
        tagTypeList.add(TagType.NonEdible);

        // fixtures
        MealPlanEntity mealPlan = new MealPlanEntity(mealPlanId);
        // make 6 tags
        TagEntity tag1 = ServiceTestUtils.buildTag(1L, "first tag", TagType.Ingredient);
        TagEntity tag2 = ServiceTestUtils.buildTag(2L, "second tag", TagType.Ingredient);
        TagEntity tag3 = ServiceTestUtils.buildTag(3L, "third tag", TagType.Ingredient);
        TagEntity tag4 = ServiceTestUtils.buildTag(4L, "fourth tag", TagType.Ingredient);
        TagEntity tag5 = ServiceTestUtils.buildTag(5L, "fifth tag", TagType.Ingredient);
        TagEntity tag6 = ServiceTestUtils.buildTag(6L, "outlier tag", TagType.NonEdible);
        // make 2 dishes
        DishEntity dish1 = ServiceTestUtils.buildDish(userId, "dish one", Arrays.asList(tag1, tag2));
        DishEntity dish2 = ServiceTestUtils.buildDish(userId, "dish two", Arrays.asList(tag1, tag3, tag4, tag5, tag6));
        dish1.setId(1212L);
        dish2.setId(2323L);
        // make 2 slots
        SlotEntity slot1 = ServiceTestUtils.buildDishSlot(mealPlan, dish1);
        SlotEntity slot2 = ServiceTestUtils.buildDishSlot(mealPlan, dish2);
        mealPlan.getSlots().add(slot1);
        mealPlan.getSlots().add(slot2);

        ArgumentCaptor<ShoppingListEntity> argument = ArgumentCaptor.forClass(ShoppingListEntity.class);

        // expectations
        Mockito.when(userService.getUserByUserEmail(username))
                .thenReturn(userEntity);
        Mockito.when(mealPlanService.getMealPlanById(username, mealPlanId))
                .thenReturn(mealPlan);
        Mockito.when(shoppingListRepository.getWithItemsByListId(listId))
                .thenReturn(Optional.of(shoppingList));
        Mockito.when(tagService.getTagsForDish(username, dish1.getId(), tagTypeList))
                .thenReturn(Arrays.asList(tag1, tag2));
        Mockito.when(tagService.getTagsForDish(username, dish2.getId(), tagTypeList))
                .thenReturn(Arrays.asList(tag1, tag3, tag4, tag5));
        mealPlanService.updateLastAddedDateForDishes(mealPlan);

        itemChangeRepository.saveItemChanges(any(ShoppingListEntity.class), any(ItemCollector.class), eq(userId), any(CollectorContext.class));
        Mockito.when(shoppingListRepository.save(argument.capture()))
                .thenReturn(shoppingList);
        Mockito.when(shoppingListRepository.getWithItemsByListIdAndItemsRemovedOnIsNull(listId))
                .thenReturn(Optional.of(shoppingList));

        // verifications before
        // list contain doesn't item from meal plan
        Optional<ItemEntity> mealPlanItem = shoppingList.getItems().stream()
                .filter(item -> item.getTag().getId().longValue() == 1L)
                .findFirst();
        Assert.assertFalse(mealPlanItem.isPresent());

        // call
        shoppingListService.addToListFromMealPlan(username, listId, mealPlanId);

        // verification afterwards
        // note - checking captured list
        // list is not null
        ShoppingListEntity listResult = argument.getValue();
        Assert.assertNotNull(listResult);
        // list should contain 6 items
        //Assert.assertEquals(6, listResult.getItems().size());
        // verify items
        // put items into map
        Map<Long, ItemEntity> resultMap = listResult.getItems().stream()
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assert.assertNotNull(resultMap);
        // tag 1 should be there - twise
        Assert.assertNotNull(resultMap.get(1L));
        Assert.assertEquals(2, resultMap.get(1L).getUsedCount().longValue());
        // tags 3 and 4 should be there twice
        Assert.assertNotNull(resultMap.get(3L));
        Assert.assertEquals(2, resultMap.get(3L).getUsedCount().longValue());
        Assert.assertNotNull(resultMap.get(4L));
        Assert.assertEquals(2, resultMap.get(4L).getUsedCount().longValue());
        // tag 8 should be there (once)
        Assert.assertNotNull(resultMap.get(8L));
        Assert.assertEquals(1, resultMap.get(8L).getUsedCount().longValue());
        // tag 6 should NOT be there
        Assert.assertNull(resultMap.get(6L));

    }

    @Test
    public void testPerformItemOperation_Remove() {
        String username = "Eustace";
        Long sourceListId = 99L;
        List<Long> operationTagIds = Arrays.asList(4L, 5L, 8L);

        Long userId = 9L;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setEmail(username);

        // 3 items, tagIds 3,4,8
        ShoppingListEntity sourceList = dummyShoppingList(sourceListId, userId);
        List<TagType> tagTypeList = new ArrayList<>();
        tagTypeList.add(TagType.Ingredient);
        tagTypeList.add(TagType.NonEdible);

        ArgumentCaptor<ShoppingListEntity> argument = ArgumentCaptor.forClass(ShoppingListEntity.class);

        // expectations
        Mockito.when(userService.getUserByUserEmail(username))
                .thenReturn(userEntity);
        Mockito.when(shoppingListRepository.findById(sourceListId))
                .thenReturn(Optional.of(sourceList));
        Mockito.when(itemRepository.findByListIdAAndRemovedOnIsNull(sourceListId))
                .thenReturn(sourceList.getItems());

        itemChangeRepository.saveItemChanges(any(ShoppingListEntity.class), any(ItemCollector.class), eq(userId), any(CollectorContext.class));
        Mockito.when(shoppingListRepository.save(argument.capture()))
                .thenReturn(sourceList);

        // call under test
        shoppingListService.performItemOperation(username, sourceListId, ItemOperationType.Remove, operationTagIds, null);

        // after remove, the list should contain only one item - id 3
        // list is not null
        ShoppingListEntity listResult = argument.getValue();
        Assert.assertNotNull(listResult);
        // list should contain 1 items - id 3
        List<ItemEntity> items = listResult.getItems();
        Assert.assertNotNull(items);
        Assert.assertFalse(items.isEmpty());
        Assert.assertTrue(items.size() == 3);
        // put items into map
        Map<Long, ItemEntity> resultMap = listResult.getItems().stream()
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assert.assertNotNull(resultMap);
        // tag 3 should be there - twise
        Assert.assertNotNull(resultMap.get(3L));
        Assert.assertEquals(1, resultMap.get(3L).getUsedCount().longValue());
        Assert.assertNull(resultMap.get(3L).getRemovedOn());
        // tags 4 and 8 should be there, but removed
        Assert.assertNotNull(resultMap.get(4L));
        Assert.assertEquals(0, resultMap.get(4L).getUsedCount().longValue());
        Assert.assertNotNull(resultMap.get(4L).getRemovedOn());
        Assert.assertNotNull(resultMap.get(8L));
        Assert.assertEquals(0, resultMap.get(8L).getUsedCount().longValue());
        Assert.assertNotNull(resultMap.get(8L).getRemovedOn());

        Long tagId = items.get(0).getTag().getId();
        Assert.assertEquals(Long.valueOf(3L), Long.valueOf(tagId));

    }

    @Test
    public void testPerformItemOperation_Move() {
        String username = "Eustace";
        Long sourceListId = 99L;
        Long destinationListId = 96L;
        List<Long> operationTagIds = Arrays.asList(4L, 5L, 8L);

        Long userId = 9L;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setEmail(username);

        // 3 items, tagIds 3,4,8
        ShoppingListEntity sourceList = dummyShoppingList(sourceListId, userId);
        List<TagType> tagTypeList = new ArrayList<>();
        tagTypeList.add(TagType.Ingredient);
        tagTypeList.add(TagType.NonEdible);

        // 3 items, tagIds 4,8,7
        ShoppingListEntity destinationList = dummyShoppingList(destinationListId, userId, Arrays.asList(4L, 8L, 7L));

        // tags for operation
        Map<Long, TagEntity> tagDictionary = dummyTagDictionary(operationTagIds);

        ArgumentCaptor<ShoppingListEntity> argument = ArgumentCaptor.forClass(ShoppingListEntity.class);

        // expectations
        Mockito.when(userService.getUserByUserEmail(username))
                .thenReturn(userEntity);
        Mockito.when(shoppingListRepository.findById(sourceListId))
                .thenReturn(Optional.of(sourceList));
        Mockito.when(shoppingListRepository.findById(destinationListId))
                .thenReturn(Optional.of(destinationList));
        Mockito.when(itemRepository.findByListIdAAndRemovedOnIsNull(sourceListId))
                .thenReturn(sourceList.getItems());
        Mockito.when(itemRepository.findByListIdAAndRemovedOnIsNull(destinationListId))
                .thenReturn(destinationList.getItems());
        Mockito.when(tagService.getDictionaryForIds(new HashSet(operationTagIds)))
                .thenReturn(tagDictionary);

        itemChangeRepository.saveItemChanges(any(ShoppingListEntity.class), any(ItemCollector.class), eq(userId), any(CollectorContext.class));
        Mockito.when(shoppingListRepository.save(argument.capture()))
                .thenReturn(sourceList);

        // call under test
        shoppingListService.performItemOperation(username, sourceListId, ItemOperationType.Move, operationTagIds, destinationListId);

        // after remove, the list should contain only one item - id 3
        // list is not null
        List<ShoppingListEntity> resultLists = argument.getAllValues();
        ShoppingListEntity firstCallResult = resultLists.get(0);
        Assert.assertNotNull(firstCallResult);
        ShoppingListEntity secondCallResult = resultLists.get(1);
        Assert.assertNotNull(secondCallResult);

        // first call result is the copy call -
        // result list should contain original 4,7,8 plus moved 4,5,8
        // so - 4 and 8 (twice) and 5 (once) and 7 (once)
        // list should contain 1 items - id 3
        List<ItemEntity> items = firstCallResult.getItems();
        Assert.assertNotNull(items);
        Assert.assertFalse(items.isEmpty());
        Assert.assertTrue(items.size() == 4);
        // put items into map
        Map<Long, ItemEntity> resultMap = firstCallResult.getItems().stream()
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assert.assertNotNull(resultMap);
        // tag 5 and 7 should be there - once
        Assert.assertNotNull(resultMap.get(5L));
        Assert.assertEquals(1, resultMap.get(5L).getUsedCount().longValue());
        Assert.assertNull(resultMap.get(5L).getRemovedOn());
        Assert.assertNotNull(resultMap.get(7L));
        Assert.assertEquals(1, resultMap.get(7L).getUsedCount().longValue());
        Assert.assertNull(resultMap.get(7L).getRemovedOn());
        // tags 4 and 8 should be there, twice
        Assert.assertNotNull(resultMap.get(4L));
        Assert.assertEquals(2, resultMap.get(4L).getUsedCount().longValue());
        Assert.assertNull(resultMap.get(4L).getRemovedOn());
        Assert.assertNotNull(resultMap.get(8L));
        Assert.assertEquals(2, resultMap.get(8L).getUsedCount().longValue());
        Assert.assertNull(resultMap.get(8L).getRemovedOn());

        // source list should not containttagids 4 and 8
        List<ItemEntity> sourceItems = secondCallResult.getItems();
        Assert.assertNotNull(sourceItems);
        Assert.assertFalse(sourceItems.isEmpty());
        Assert.assertTrue(sourceItems.size() == 3);
        // put sourceItems into map
        Map<Long, ItemEntity> sourceResultMap = sourceItems.stream()
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assert.assertNotNull(sourceResultMap);
        // tags 4 and 8 should not be there
        Assert.assertNotNull(sourceResultMap.get(4L));
        Assert.assertEquals(0, sourceResultMap.get(4L).getUsedCount().longValue());
        Assert.assertNotNull(sourceResultMap.get(4L).getRemovedOn());
        Assert.assertNotNull(sourceResultMap.get(8L));
        Assert.assertEquals(0, sourceResultMap.get(8L).getUsedCount().longValue());
        Assert.assertNotNull(sourceResultMap.get(8L).getRemovedOn());

    }

    @Test
    public void testPerformItemOperation_Copy() {
        String username = "Eustace";
        Long sourceListId = 99L;
        Long destinationListId = 96L;
        List<Long> operationTagIds = Arrays.asList(4L, 5L, 8L);

        Long userId = 9L;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setEmail(username);

        // 3 items, tagIds 3,4,8
        ShoppingListEntity sourceList = dummyShoppingList(sourceListId, userId);
        // 3 items, tagIds 4,8,7
        ShoppingListEntity destinationList = dummyShoppingList(destinationListId, userId, Arrays.asList(4L, 8L, 7L));

        List<TagType> tagTypeList = new ArrayList<>();
        tagTypeList.add(TagType.Ingredient);
        tagTypeList.add(TagType.NonEdible);


        // tags for operation
        Map<Long, TagEntity> tagDictionary = dummyTagDictionary(operationTagIds);

        ArgumentCaptor<ShoppingListEntity> argument = ArgumentCaptor.forClass(ShoppingListEntity.class);

        // expectations
        Mockito.when(userService.getUserByUserEmail(username))
                .thenReturn(userEntity);
        Mockito.when(shoppingListRepository.findById(destinationListId))
                .thenReturn(Optional.of(destinationList));
        Mockito.when(itemRepository.findByListIdAAndRemovedOnIsNull(destinationListId))
                .thenReturn(destinationList.getItems());
        Mockito.when(tagService.getDictionaryForIds(new HashSet(operationTagIds)))
                .thenReturn(tagDictionary);

        itemChangeRepository.saveItemChanges(any(ShoppingListEntity.class), any(ItemCollector.class), eq(userId), any(CollectorContext.class));
        Mockito.when(shoppingListRepository.save(argument.capture()))
                .thenReturn(sourceList);

        // call under test
        shoppingListService.performItemOperation(username, sourceListId, ItemOperationType.Copy, operationTagIds, destinationListId);

        // list is not null
        List<ShoppingListEntity> resultLists = argument.getAllValues();
        ShoppingListEntity firstCallResult = resultLists.get(0);
        Assert.assertNotNull(firstCallResult);

        // after remove, the destination list should contain three items
        // result list should contain original 4,7,8 plus moved 4,5,8
        // so - 4 and 8 (twice) and 5 (once) and 7 (once)
        // list should contain 1 items - id 3
        List<ItemEntity> items = firstCallResult.getItems();
        Assert.assertNotNull(items);
        Assert.assertFalse(items.isEmpty());
        Assert.assertTrue(items.size() == 4);
        // put items into map
        Map<Long, ItemEntity> resultMap = firstCallResult.getItems().stream()
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assert.assertNotNull(resultMap);
        // tag 5 and 7 should be there - once
        Assert.assertNotNull(resultMap.get(5L));
        Assert.assertEquals(1, resultMap.get(5L).getUsedCount().longValue());
        Assert.assertNull(resultMap.get(5L).getRemovedOn());
        Assert.assertNotNull(resultMap.get(7L));
        Assert.assertEquals(1, resultMap.get(7L).getUsedCount().longValue());
        Assert.assertNull(resultMap.get(7L).getRemovedOn());
        // tags 4 and 8 should be there, twice
        Assert.assertNotNull(resultMap.get(4L));
        Assert.assertEquals(2, resultMap.get(4L).getUsedCount().longValue());
        Assert.assertNull(resultMap.get(4L).getRemovedOn());
        Assert.assertNotNull(resultMap.get(8L));
        Assert.assertEquals(2, resultMap.get(8L).getUsedCount().longValue());
        Assert.assertNull(resultMap.get(8L).getRemovedOn());

    }

    private Map<Long, TagEntity> dummyTagDictionary(List<Long> tagIds) {
        Map<Long, TagEntity> resultMap = new HashMap<>();

        for (Long id : tagIds) {
            TagEntity tag = ServiceTestUtils.buildTag(id, String.valueOf(id), TagType.Ingredient);
            resultMap.put(id, tag);
        }

        return resultMap;
    }

    private ShoppingListEntity dummyShoppingList(Long shoppingListId, Long userId) {
        return dummyShoppingList(shoppingListId, userId, Arrays.asList(3L, 4L, 8L));

    }

    private ShoppingListEntity dummyShoppingList(Long shoppingListId, Long userId, List<Long> tagIds) {
        // make empty meal plan
        ShoppingListEntity listEntity = new ShoppingListEntity(shoppingListId);
        listEntity.setUserId(userId);
        listEntity.setCreatedOn(new Date());

        // make 3 tags
        List<ItemEntity> items = new ArrayList<>();
        for (Long tagbase : tagIds) {
            Long itemId = tagbase * 1111111;
            TagEntity tag = ServiceTestUtils.buildTag(tagbase, String.valueOf(tagbase), TagType.Ingredient);
            ItemEntity item = ServiceTestUtils.buildItem(itemId, tag, shoppingListId);
            item.setUsedCount(1);
            items.add(item);
        }
        // add items to list
        listEntity.getItems().addAll(items);

        return listEntity;

    }

    @Test
    public void testUpdateList() {
        String userName = "george";
        Long listId = 99L;
        ShoppingListEntity updateFrom = new ShoppingListEntity();
        updateFrom.setName("has been updated");
        updateFrom.setIsStarterList(false);

        ShoppingListEntity originalList = new ShoppingListEntity();
        originalList.setName("originalList");
        originalList.setIsStarterList(true);


        UserEntity user = new UserEntity();
        user.setId(999L);
        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(user);

        ArgumentCaptor<ShoppingListEntity> listArgument = ArgumentCaptor.forClass(ShoppingListEntity.class);

        Mockito.when(shoppingListRepository.findByListIdAndUserId(listId, 999L))
                .thenReturn(Collections.singletonList(originalList));
        Mockito.when(shoppingListRepository.save(listArgument.capture()))
                .thenReturn(updateFrom);

        shoppingListService.updateList(userName, listId, updateFrom);

        Assert.assertEquals("has been updated", listArgument.getValue().getName());
        Assert.assertEquals(false, listArgument.getValue().getIsStarterList());
    }
}