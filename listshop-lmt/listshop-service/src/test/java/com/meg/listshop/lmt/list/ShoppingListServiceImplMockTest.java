package com.meg.listshop.lmt.list;

import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.lmt.api.exception.ActionInvalidException;
import com.meg.listshop.lmt.api.exception.ItemProcessingException;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.data.ItemChangeRepository;
import com.meg.listshop.lmt.data.entity.*;
import com.meg.listshop.lmt.data.pojos.LongTagIdPairDTO;
import com.meg.listshop.lmt.data.repository.ItemRepository;
import com.meg.listshop.lmt.data.repository.ShoppingListRepository;
import com.meg.listshop.lmt.dish.DishService;
import com.meg.listshop.lmt.list.impl.ShoppingListServiceImpl;
import com.meg.listshop.lmt.list.state.ItemStateContext;
import com.meg.listshop.lmt.list.state.ListItemEvent;
import com.meg.listshop.lmt.list.state.ListItemStateMachine;
import com.meg.listshop.lmt.service.*;
import com.meg.listshop.lmt.service.tag.TagService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
@ActiveProfiles("test")
public class ShoppingListServiceImplMockTest {


    private ShoppingListService shoppingListService;

    private UserService userService = Mockito.mock(UserService.class);

    private TagService tagService = Mockito.mock(TagService.class);

    private DishService dishService = Mockito.mock(DishService.class);

    private ShoppingListRepository shoppingListRepository = Mockito.mock(ShoppingListRepository.class);

    private LayoutService layoutService = Mockito.mock(LayoutService.class);

    private ListSearchService listSearchService = Mockito.mock(ListSearchService.class);

    private MealPlanService mealPlanService = Mockito.mock(MealPlanService.class);

    private ItemRepository itemRepository = Mockito.mock(ItemRepository.class);

    private ItemChangeRepository itemChangeRepository = Mockito.mock(ItemChangeRepository.class);

    private ListTagStatisticService listTagStatisticService = Mockito.mock(ListTagStatisticService.class);

    private ListItemStateMachine listItemStateMachine = Mockito.mock(ListItemStateMachine.class);

    @Before
    public void setUp() {

        shoppingListService = new ShoppingListServiceImpl(tagService,
                dishService,
                shoppingListRepository,
                layoutService,
                mealPlanService,
                itemRepository,
                itemChangeRepository,
                listTagStatisticService,
                listItemStateMachine);
    }

    @Test
    public void testGetListById() {
        Long userId = 99L;
        String userName = "userName";
        UserEntity user = new UserEntity();
        user.setId(userId);

        Long listId = 199L;
        ShoppingListEntity shoppingList = new ShoppingListEntity(listId);
        shoppingList.setUserId(userId);


        Mockito.when(shoppingListRepository.getWithItemsByListId(listId)).thenReturn(Optional.of(shoppingList));

        // test call
        ShoppingListEntity result = shoppingListService.getListForUserById(userId, listId);


        Mockito.verify(shoppingListRepository, times(1)).getWithItemsByListId(listId);

        // Assertions
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetListById_NoItems() {
        Long userId = 99L;
        String userName = "userName";
        UserEntity user = new UserEntity();
        user.setId(userId);

        Long listId = 199L;
        ShoppingListEntity shoppingList = new ShoppingListEntity(listId);
        shoppingList.setUserId(userId);


        Mockito.when(shoppingListRepository.getWithItemsByListId(listId)).thenReturn(Optional.empty());
        Mockito.when(shoppingListRepository.findById(listId)).thenReturn(Optional.of(shoppingList));

        // test call
        ShoppingListEntity result = shoppingListService.getListForUserById(userId, listId);


        Mockito.verify(shoppingListRepository, times(1)).getWithItemsByListId(listId);
        Mockito.verify(shoppingListRepository, times(1)).findById(listId);

        // Assertions
        Assert.assertNotNull(result);

        // test returning null - user
        // test returning null - shoppinglistwithitems


    }

    @Test
    public void testGetListById_NoUser() {
        Long userId = 99L;
        String userName = "userName";
        UserEntity user = new UserEntity();
        user.setId(userId);

        Long listId = 199L;
        ShoppingListEntity shoppingList = new ShoppingListEntity(listId);
        shoppingList.setUserId(userId);


        // test call
        ShoppingListEntity result = shoppingListService.getListForUserById(userId, listId);

        // Assertions
        Assert.assertNull(result);

        // test returning null - user
        // test returning null - shoppinglistwithitems


    }

    @Test
    public void testGetListById_BadUser() {
        Long userId = 99L;
        String userName = "userName";
        UserEntity user = new UserEntity();
        user.setId(userId);

        Long listId = 199L;
        ShoppingListEntity shoppingList = new ShoppingListEntity(listId);
        shoppingList.setUserId(1L); // list doesn't belong to this user


        Mockito.when(shoppingListRepository.getWithItemsByListId(listId)).thenReturn(Optional.of(shoppingList));

        // test call
        ShoppingListEntity result = shoppingListService.getListForUserById(userId, listId);

        Mockito.verify(shoppingListRepository, times(1)).getWithItemsByListId(listId);

        // Assertions
        Assert.assertNull(result);


    }

    @Test
    public void testDeleteList() {
        Long userId = 99L;
        UserEntity user = new UserEntity();
        user.setId(userId);

        Long listId = 199L;
        ShoppingListEntity shoppingList = ServiceTestUtils.buildShoppingList(userId, listId);
        ShoppingListEntity shoppingListForSave = ServiceTestUtils.buildShoppingList(userId, listId);
        List<ShoppingListEntity> listOfLists = Arrays.asList(
                ServiceTestUtils.buildShoppingList(userId, 1L),
                shoppingList,
                ServiceTestUtils.buildShoppingList(userId, 1011L),
                ServiceTestUtils.buildShoppingList(userId, 1021L)
        );


        List<ListItemEntity> items = new ArrayList<>();
        items.add(ServiceTestUtils.buildItem(1L, new TagEntity(11L), listId));
        items.add(ServiceTestUtils.buildItem(2L, new TagEntity(22L), listId));
        items.add(ServiceTestUtils.buildItem(3L, new TagEntity(33L), listId));
        shoppingList.setItems(items);
        shoppingListForSave.setItems(new ArrayList<>());

        ArgumentCaptor<ShoppingListEntity> listArgument = ArgumentCaptor.forClass(ShoppingListEntity.class);

        Mockito.when(shoppingListRepository.findByUserIdOrderByLastUpdateDesc(userId)).thenReturn(listOfLists);
        Mockito.when(itemRepository.findByListId(listId)).thenReturn(items);
        Mockito.when(shoppingListRepository.findByListIdAndUserId(listId, userId)).thenReturn(Optional.of(shoppingListForSave));
        Mockito.when(shoppingListRepository.save(listArgument.capture())).thenReturn(shoppingListForSave);


        // test call
        shoppingListService.deleteList(userId, listId);

        Mockito.verify(shoppingListRepository, times(1)).findByListIdAndUserId(listId, userId);
        Mockito.verify(shoppingListRepository, times(1)).findByUserIdOrderByLastUpdateDesc(userId);
        Mockito.verify(itemRepository, times(1)).findByListId(listId);
        Mockito.verify(itemRepository, times(1)).deleteAll(items);
        Mockito.verify(shoppingListRepository, times(1)).delete(listId);
        Mockito.verify(shoppingListRepository, times(1)).flush();

        // Assertions
        Assert.assertNotNull(listArgument.getValue());
        ShoppingListEntity resultList = listArgument.getValue();
        Assert.assertTrue(resultList.getItems().isEmpty());
    }

    @Test(expected = ActionInvalidException.class)
    public void testDeleteList_NoListsFound() {
        Long userId = 99L;
        String userName = "userName";
        UserEntity user = new UserEntity();
        user.setId(userId);

        Long listId = 199L;

        // test call
        shoppingListService.deleteList(userId, listId);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testDeleteList_BadList() {
        Long userId = 99L;
        String userName = "userName";
        UserEntity user = new UserEntity();
        user.setId(userId);

        Long listId = 199L;
        List<ShoppingListEntity> listOfLists = Arrays.asList(
                ServiceTestUtils.buildShoppingList(userId, 1L),
                ServiceTestUtils.buildShoppingList(userId, 101L),
                ServiceTestUtils.buildShoppingList(userId, 1011L),
                ServiceTestUtils.buildShoppingList(userId, 1021L)
        );

        Mockito.when(shoppingListRepository.findByUserIdOrderByLastUpdateDesc(userId)).thenReturn(listOfLists);

        // test call
        shoppingListService.deleteList(userId, listId);


    }

    @Test(expected = ActionInvalidException.class)
    public void testDeleteList_LastList() {
        Long userId = 99L;
        String userName = "userName";
        UserEntity user = new UserEntity();
        user.setId(userId);

        Long listId = 199L;
        List<ShoppingListEntity> listOfLists = Collections.singletonList(ServiceTestUtils.buildShoppingList(userId, listId));

        Mockito.when(shoppingListRepository.findByUserIdOrderByLastUpdateDesc(userId)).thenReturn(listOfLists);

        // test call
        shoppingListService.deleteList(userId, listId);


    }
    // test list - last list

    @Test
    public void testAddItemToListByTag() throws ItemProcessingException {
        Long userId = 99L;
        String userName = "userName";
        UserEntity user = new UserEntity();
        user.setId(userId);

        Long listId = 199L;
        ShoppingListEntity shoppingList = new ShoppingListEntity(listId);
        shoppingList.setUserId(userId);

        List<ListItemEntity> items = new ArrayList<>();
        items.add(ServiceTestUtils.buildItem(1L, new TagEntity(11L), listId));
        items.add(ServiceTestUtils.buildItem(2L, new TagEntity(22L), listId));
        items.add(ServiceTestUtils.buildItem(3L, new TagEntity(33L), listId));
        shoppingList.setItems(items);


        Long tagId = 1199L;
        TagEntity tagToAdd = ServiceTestUtils.buildTagEntity(tagId, "tagName", TagType.Ingredient);
        ListItemEntity newItem = new ListItemEntity();
        newItem.setTag(tagToAdd);

        ArgumentCaptor<ShoppingListEntity> listCapture = ArgumentCaptor.forClass(ShoppingListEntity.class);

        Mockito.when(shoppingListRepository.getWithItemsByListId(listId)).thenReturn(Optional.of(shoppingList));
        Mockito.when(tagService.getTagById(tagId)).thenReturn(tagToAdd);

        // test call
        shoppingListService.addItemToListByTag(userId, listId, tagId);

        Mockito.verify(itemChangeRepository, times(1)).saveItemChangeStatistics(any(ShoppingListEntity.class),
                any(List.class),any(List.class), any(Long.class), any(ListOperationType.class));
        Mockito.verify(shoppingListRepository, times(1)).save(listCapture.capture());

        Mockito.verify(shoppingListRepository, times(1)).getWithItemsByListId(listId);
        Mockito.verify(tagService, times(1)).getTagById(tagId);

        // Assertions
        ShoppingListEntity listResult = listCapture.getValue();
        Assert.assertNotNull(listResult);
        Assert.assertNotNull(listResult.getLastUpdate());
    }

    @Test
    public void testUpdateItemCount() {
        Long userId = 99L;
        String userName = "userName";
        UserEntity user = new UserEntity();
        user.setId(userId);

        Long listId = 199L;
        ShoppingListEntity shoppingList = new ShoppingListEntity(listId);
        shoppingList.setUserId(userId);

        Long itemId = 1199L;
        Long tagId = 11199L;
        ListItemEntity item = ServiceTestUtils.buildItem(itemId, ServiceTestUtils.buildTagEntity(tagId, "tagName", TagType.Ingredient), listId);

        Integer usedCount = 5;

        ArgumentCaptor<ListItemEntity> itemCapture = ArgumentCaptor.forClass(ListItemEntity.class);
        ArgumentCaptor<ShoppingListEntity> listCapture = ArgumentCaptor.forClass(ShoppingListEntity.class);

        Mockito.when(shoppingListRepository.getWithItemsByListId(listId)).thenReturn(Optional.of(shoppingList));
        Mockito.when(itemRepository.getItemByListAndTag(listId, tagId)).thenReturn(item);

        // test call
        shoppingListService.updateItemCount(userId, listId, tagId, usedCount);


        Mockito.verify(shoppingListRepository, times(1)).getWithItemsByListId(listId);
        Mockito.verify(itemRepository, times(1)).save(itemCapture.capture());
        Mockito.verify(shoppingListRepository, times(1)).save(listCapture.capture());

        // Assertions
        ListItemEntity itemResult = itemCapture.getValue();
        ShoppingListEntity listResult = listCapture.getValue();
        Assert.assertNotNull(itemResult);
        Assert.assertNotNull(listResult);

        // item checks
        Assert.assertEquals("used count should equal that in call", usedCount, itemResult.getUsedCount());
        Assert.assertNotNull("update date should be set", itemResult.getUpdatedOn());
        Assert.assertNull("removed on should be null", itemResult.getRemovedOn());
        Assert.assertNull("crossed off should be null", itemResult.getCrossedOff());

        // list chanes
        Assert.assertNotNull("update date should be set", listResult.getLastUpdate());

    }

    @Test
    public void testDeleteAllItemsFromList() throws ItemProcessingException {
        Long userId = 99L;
        String userName = "userName";
        UserEntity user = new UserEntity();
        user.setId(userId);

        Long listId = 199L;
        ShoppingListEntity shoppingList = new ShoppingListEntity(listId);
        shoppingList.setUserId(userId);

        List<ListItemEntity> items = new ArrayList<>();
        items.add(ServiceTestUtils.buildItem(1L, new TagEntity(11L), listId));
        items.add(ServiceTestUtils.buildItem(2L, new TagEntity(22L), listId));
        items.add(ServiceTestUtils.buildItem(3L, new TagEntity(33L), listId));

        ArgumentCaptor<ShoppingListEntity> savedList = ArgumentCaptor.forClass(ShoppingListEntity.class);

        Mockito.when(shoppingListRepository.findByListIdAndUserId(listId, userId)).thenReturn(Optional.of(shoppingList));
        Mockito.when(itemRepository.findByListId(listId)).thenReturn(items);

        // test call
        shoppingListService.deleteAllItemsFromList(userId, listId);


        Mockito.verify(shoppingListRepository, times(1)).findByListIdAndUserId(listId, userId);
        Mockito.verify(itemRepository, times(1)).findByListId(listId);
        //Mockito.verify(itemChangeRepository, times(1)).saveItemChanges(any(ShoppingListEntity.class),
        //      any(ListItemCollector.class), any(Long.class), any(CollectorContext.class));
        Mockito.verify(shoppingListRepository, times(1)).save(savedList.capture());

        // Assertions
        ShoppingListEntity testResult = savedList.getValue();
        Assert.assertNotNull(testResult);
        Optional<ListItemEntity> notCrossedOff = testResult.getItems().stream()
                .filter(i -> i.getRemovedOn() == null)
                .findFirst();
        Assert.assertFalse("All items should be shown as removed", notCrossedOff.isPresent());
    }

    @Test
    public void testDeleteAllItemsFromList_ListNotFound() throws ItemProcessingException {
        Long userId = 99L;
        String userName = "userName";
        UserEntity user = new UserEntity();
        user.setId(userId);

        Long listId = 199L;
        ShoppingListEntity shoppingList = new ShoppingListEntity(listId);
        shoppingList.setUserId(userId);

        List<ListItemEntity> items = new ArrayList<>();
        items.add(ServiceTestUtils.buildItem(1L, new TagEntity(11L), listId));
        items.add(ServiceTestUtils.buildItem(2L, new TagEntity(22L), listId));
        items.add(ServiceTestUtils.buildItem(3L, new TagEntity(33L), listId));

        Mockito.when(shoppingListRepository.findByListIdAndUserId(listId, userId)).thenReturn(Optional.empty());

        // test call
        shoppingListService.deleteAllItemsFromList(userId, listId);


        Mockito.verify(shoppingListRepository, times(1)).findByListIdAndUserId(listId, userId);

    }

    @Test
    public void testGetListsByUsername() {
        Long userId = 99L;
        String userName = "userName";
        UserEntity user = new UserEntity();
        user.setId(userId);

        Long listId = 199L;
        ShoppingListEntity shoppingList = new ShoppingListEntity(listId);
        shoppingList.setUserId(userId);


        Mockito.when(shoppingListRepository.findByUserIdOrderByLastUpdateDesc(userId)).thenReturn(new ArrayList<>());

        // test call
        shoppingListService.getListsByUserId(userId);


        Mockito.verify(shoppingListRepository, times(1)).findByUserIdOrderByLastUpdateDesc(userId);

    }

    @Test
    public void testCrossOffAllItems() throws ItemProcessingException {
        Long userId = 99L;
        String userName = "userName";
        UserEntity user = new UserEntity();
        user.setId(userId);

        Long listId = 199L;
        ShoppingListEntity shoppingList = new ShoppingListEntity(listId);
        shoppingList.setUserId(userId);

        List<ListItemEntity> items = new ArrayList<>();
        items.add(ServiceTestUtils.buildItem(1L, new TagEntity(11L), listId));
        items.add(ServiceTestUtils.buildItem(2L, new TagEntity(22L), listId));
        items.add(ServiceTestUtils.buildItem(3L, new TagEntity(33L), listId));
        shoppingList.setItems(items);

        ArgumentCaptor<List<ListItemEntity>> crossedOffItems = ArgumentCaptor.forClass(List.class);

        Mockito.when(shoppingListRepository.getWithItemsByListId(listId)).thenReturn(Optional.of(shoppingList));


        // test call
        shoppingListService.crossOffAllItems(userId, listId, true);


        Mockito.verify(shoppingListRepository, times(1)).getWithItemsByListId(listId);
        Mockito.verify(itemRepository, times(1)).saveAll(crossedOffItems.capture());

        List<ListItemEntity> updatedItems = crossedOffItems.getValue();
        Assert.assertNotNull(updatedItems);
        Assert.assertEquals("should be three items", 3, updatedItems.size());
    }

    @Test
    public void testUpdateItemCrossedOff() throws ItemProcessingException {
        Long userId = 99L;
        String userName = "userName";
        UserEntity user = new UserEntity();
        user.setId(userId);

        Long listId = 199L;
        ShoppingListEntity shoppingList = new ShoppingListEntity(listId);
        shoppingList.setUserId(userId);

        Long itemId = 1199L;
        ListItemEntity item = new ListItemEntity(itemId);
        item.setListId(listId);

        List<ListItemEntity> items = new ArrayList<>();
        items.add(item);
        items.add(ServiceTestUtils.buildItem(1L, new TagEntity(11L), listId));
        items.add(ServiceTestUtils.buildItem(2L, new TagEntity(22L), listId));
        items.add(ServiceTestUtils.buildItem(3L, new TagEntity(33L), listId));
        shoppingList.setItems(items);
        ItemStateContext context = new ItemStateContext(item, listId);
        context.setCrossedOff(true);

        ArgumentCaptor<ListItemEntity> itemCapture = ArgumentCaptor.forClass(ListItemEntity.class);


        Mockito.when(shoppingListRepository.getWithItemsByListId(listId)).thenReturn(Optional.of(shoppingList));
        Mockito.when(listItemStateMachine.handleEvent(eq(ListItemEvent.CROSS_OFF_ITEM), any(ItemStateContext.class)))
                .thenReturn(item);
        // test call
        shoppingListService.updateItemCrossedOff(userId, listId, itemId, true);

        Mockito.verify(shoppingListRepository, times(1)).getWithItemsByListId(listId);
        Mockito.verify(shoppingListRepository, times(1)).save(any(ShoppingListEntity.class));

        // Assertions
        Assert.assertEquals(item.getCrossedOff(), item.getUpdatedOn());

    }

    @Test
    public void testCategorizeList() {
        Long userId = 99L;
        UserEntity user = new UserEntity();
        user.setId(userId);

        Long listId = 199L;
        Long layoutId = 1199L;
        ShoppingListEntity shoppingList = new ShoppingListEntity(listId);
        shoppingList.setUserId(userId);
        shoppingList.setListLayoutId(layoutId);

        List<ListItemEntity> items = new ArrayList<>();
        items.add(ServiceTestUtils.buildItem(1L, new TagEntity(11L), listId));
        items.add(ServiceTestUtils.buildItem(2L, new TagEntity(22L), listId));
        items.add(ServiceTestUtils.buildItem(3L, new TagEntity(33L), listId));
        shoppingList.setItems(items);

        Map<Long, Long> tagIdToCategoryId = items.stream()
                .map(ListItemEntity::getTag)
                .collect(Collectors.toMap(TagEntity::getId, tt -> tt.getId() + 300));

        Long listLayoutId = 1001L;
        ListLayoutEntity testLayout = ServiceTestUtils.buildListLayout(listLayoutId, "listLayoutName");
        ListLayoutCategoryEntity llCategory1 = ServiceTestUtils.buildListCategory(333L, "one oh two", testLayout);
        ListLayoutCategoryEntity llCategory2 = ServiceTestUtils.buildListCategory(322L, "one oh two", testLayout);
        ListLayoutCategoryEntity llCategory3 = ServiceTestUtils.buildListCategory(333L, "one oh two", testLayout);
        List<ListLayoutCategoryEntity> categories = Arrays.asList(llCategory1, llCategory2, llCategory3);
        testLayout.setCategories(new HashSet<>(categories));


        Mockito.when(listTagStatisticService.findFrequentIdsForList(listId, userId)).thenReturn(new ArrayList<>());

        // test call
        List<ShoppingListCategory> resultList = shoppingListService.categorizeList(shoppingList);

        // Assertions
        Assert.assertNotNull(resultList);
    }

    @Test
    public void testGetStarterList() {
        Long userId = 99L;
        String userName = "userName";
        UserEntity user = new UserEntity();
        user.setId(userId);

        Long listId = 199L;
        ShoppingListEntity shoppingList = new ShoppingListEntity(listId);
        shoppingList.setUserId(userId);


        Mockito.when(shoppingListRepository.findByUserIdAndIsStarterListTrue(userId)).thenReturn(List.of(shoppingList));


        // test call
        ShoppingListEntity result = shoppingListService.getStarterList(userId);


        Mockito.verify(shoppingListRepository, times(1)).findByUserIdAndIsStarterListTrue(userId);

        // Assertions
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetMostRecentList() {
        Long userId = 99L;
        String userName = "userName";
        UserEntity user = new UserEntity();
        user.setId(userId);

        Long listId = 199L;
        ShoppingListEntity shoppingList = new ShoppingListEntity(listId);
        shoppingList.setUserId(userId);
        ShoppingListEntity shoppingList2 = new ShoppingListEntity(999L);
        shoppingList.setUserId(userId);
        List<ShoppingListEntity> listOfLists = Arrays.asList(shoppingList, shoppingList2);


        Mockito.when(shoppingListRepository.findByUserIdOrderByLastUpdateDesc(userId))
                .thenReturn(listOfLists);

        // test call
        ShoppingListEntity result = shoppingListService.getMostRecentList(userId);


        Mockito.verify(shoppingListRepository, times(1)).findByUserIdOrderByLastUpdateDesc(userId);

        // Assertions
        Assert.assertNotNull(result);
        Assert.assertEquals(listId, result.getId());
    }

    // getChangedItemsForMostRecentList
    // addListToList
    // fillSources
    // changeListLayout
    // addDishToList
    // removeDishFromList
    // removeListItemsFromList

    @Test
    public void testCreateList_duplicateName() throws ShoppingListException, ItemProcessingException {
        Long userId = 99L;
        String userName = "userName";
        String listName = "ShoppingList";
        Long listLayoutId = 666L;

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

        ListLayoutEntity listLayout = new ListLayoutEntity(listLayoutId);

        ArgumentCaptor<ShoppingListEntity> listArgument = ArgumentCaptor.forClass(ShoppingListEntity.class);

        Mockito.when(shoppingListRepository.findByUserIdAndName(userId, listName)).thenReturn(Collections.singletonList(listDuplicate1));
        Mockito.when(shoppingListRepository.findByUserIdAndNameLike(userId, listName + "%"))
                .thenReturn(Arrays.asList(listDuplicate1, listDuplicate2, listDuplicate3));
        Mockito.when(shoppingListRepository.save(listArgument.capture())).thenReturn(createdList);
        Mockito.when(layoutService.getDefaultUserLayout(userId)).thenReturn(listLayout);

        ShoppingListEntity result = shoppingListService.generateListForUser(userId, properties);

        Assert.assertNotNull(result);
        ShoppingListEntity captured = listArgument.getValue();
        Assert.assertNotNull(captured);
        Assert.assertEquals("ShoppingList 4", captured.getName());

    }

    @Test
    public void testGenerateListFromMealPlan() throws ShoppingListException, ItemProcessingException {
        String username = "Eustace";
        Long listId = 99L;
        Long mealPlanId = 999L;
        Long userId = 9L;
        String listName = "list name";
        Long listLayoutId = 9999L;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setEmail(username);

        ShoppingListEntity createdShoppingList = emptyShoppingList(listId, userId, listName);
        List<TagType> tagTypeList = new ArrayList<>();
        tagTypeList.add(TagType.Ingredient);
        tagTypeList.add(TagType.NonEdible);

        // fixtures
        ListLayoutEntity listLayoutEntity = new ListLayoutEntity(listLayoutId);
        MealPlanEntity mealPlan = new MealPlanEntity(mealPlanId);
        // make 6 tags
        TagEntity tag1 = ServiceTestUtils.buildTag(1L, "first tag", TagType.Ingredient);
        TagEntity tag2 = ServiceTestUtils.buildTag(2L, "second tag", TagType.Ingredient);
        TagEntity tag3 = ServiceTestUtils.buildTag(3L, "third tag", TagType.Ingredient);
        TagEntity tag4 = ServiceTestUtils.buildTag(4L, "fourth tag", TagType.Ingredient);
        TagEntity tag5 = ServiceTestUtils.buildTag(5L, "fifth tag", TagType.Ingredient);
        TagEntity tag6 = ServiceTestUtils.buildTag(6L, "outlier tag", TagType.NonEdible);
        // make 2 dishes
        DishEntity dish1 = ServiceTestUtils.buildDishWithTags(userId, "dish one", Arrays.asList(tag1, tag2));
        DishEntity dish2 = ServiceTestUtils.buildDishWithTags(userId, "dish two", Arrays.asList(tag1, tag3, tag4, tag5, tag6));
        dish1.setId(1212L);
        dish2.setId(2323L);
        // make 2 slots
        SlotEntity slot1 = ServiceTestUtils.buildDishSlot(mealPlan, dish1);
        SlotEntity slot2 = ServiceTestUtils.buildDishSlot(mealPlan, dish2);
        mealPlan.getSlots().add(slot1);
        mealPlan.getSlots().add(slot2);

        ArgumentCaptor<ShoppingListEntity> argument = ArgumentCaptor.forClass(ShoppingListEntity.class);

        // expectations
        Mockito.when(mealPlanService.getMealPlanForUserById(userId, mealPlanId))
                .thenReturn(mealPlan);
        Mockito.when(layoutService.getDefaultUserLayout(userId))
                .thenReturn(listLayoutEntity);
        mealPlanService.updateLastAddedDateForDishes(mealPlan);

        //itemChangeRepository.saveItemChanges(any(ShoppingListEntity.class), any(ItemCollector.class), eq(userId), any(CollectorContext.class));
        Mockito.when(shoppingListRepository.save(argument.capture()))
                .thenReturn(createdShoppingList);


        // call
        shoppingListService.generateListFromMealPlan(userId, mealPlanId);

        // verification afterwards
        // note - checking captured list
        // list is not null
        ShoppingListEntity listResult = argument.getValue();
        Assert.assertNotNull(listResult);
    }

    @Test
    public void testAddToListFromMealPlan() throws ShoppingListException, ItemProcessingException {
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
        DishEntity dish1 = ServiceTestUtils.buildDishWithTags(userId, "dish one", Arrays.asList(tag1, tag2));
        DishEntity dish2 = ServiceTestUtils.buildDishWithTags(userId, "dish two", Arrays.asList(tag1, tag3, tag4, tag5, tag6));
        dish1.setId(1212L);
        dish2.setId(2323L);
        // make dish items
        DishItemEntity dishItem1 = ServiceTestUtils.buildDishItemFromTag(1011L, dish1, tag1);
        DishItemEntity dishItem2 = ServiceTestUtils.buildDishItemFromTag(1012L, dish1, tag2);
        DishItemEntity dishItem3 = ServiceTestUtils.buildDishItemFromTag(1013L, dish1, tag3);
        DishItemEntity dishItem4 = ServiceTestUtils.buildDishItemFromTag(1014L, dish2, tag4);
        DishItemEntity dishItem5 = ServiceTestUtils.buildDishItemFromTag(1015L, dish2, tag5);
        DishItemEntity dishItem6 = ServiceTestUtils.buildDishItemFromTag(1016L, dish2, tag6);

        // make 2 slots
        SlotEntity slot1 = ServiceTestUtils.buildDishSlot(mealPlan, dish1);
        SlotEntity slot2 = ServiceTestUtils.buildDishSlot(mealPlan, dish2);
        mealPlan.getSlots().add(slot1);
        mealPlan.getSlots().add(slot2);

        ArgumentCaptor<ShoppingListEntity> argument = ArgumentCaptor.forClass(ShoppingListEntity.class);

        // expectations
        Mockito.when(mealPlanService.getMealPlanForUserById(userId, mealPlanId))
                .thenReturn(mealPlan);
        Mockito.when(shoppingListRepository.getWithItemsByListId(listId))
                .thenReturn(Optional.of(shoppingList));
        Mockito.when(dishService.getDishItems(userId, Arrays.asList(dish1.getId(), dish2.getId())))
                .thenReturn(Arrays.asList(dishItem1, dishItem2, dishItem3, dishItem4, dishItem5, dishItem6));
        mealPlanService.updateLastAddedDateForDishes(mealPlan);

        Mockito.when(shoppingListRepository.save(argument.capture()))
                .thenReturn(shoppingList);
        Mockito.when(shoppingListRepository.getWithItemsByListIdAndItemsRemovedOnIsNull(listId))
                .thenReturn(Optional.of(shoppingList));

        // verifications before
        // list contain doesn't item from meal plan
        Optional<ListItemEntity> mealPlanItem = shoppingList.getItems().stream()
                .filter(item -> item.getTag().getId() == 1L)
                .findFirst();
        Assert.assertFalse(mealPlanItem.isPresent());

        // call
        shoppingListService.addToListFromMealPlan(userId, listId, mealPlanId);

        // verification afterwards

        // list is not null
        ShoppingListEntity listResult = argument.getValue();
        Assert.assertNotNull(listResult);

        String invocations = Mockito.mockingDetails(tagService).printInvocations();
        Assertions.assertFalse(invocations.toLowerCase().contains("unused"));
    }

    @Test
    public void testAddDishesToList() throws ShoppingListException, ItemProcessingException {
        Long listId = 99L;
        Long userId = 9L;

        // fixtures
        String username = "Eustace";

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setEmail(username);

        ShoppingListEntity shoppingList = dummyShoppingList(listId, userId);

        // make 6 tags
        TagEntity tag1 = ServiceTestUtils.buildTag(1L, "first tag", TagType.Ingredient);
        TagEntity tag2 = ServiceTestUtils.buildTag(2L, "second tag", TagType.Ingredient);
        TagEntity tag3 = ServiceTestUtils.buildTag(3L, "third tag", TagType.Ingredient);
        TagEntity tag4 = ServiceTestUtils.buildTag(4L, "fourth tag", TagType.Ingredient);
        TagEntity tag5 = ServiceTestUtils.buildTag(5L, "fifth tag", TagType.Ingredient);
        TagEntity tag6 = ServiceTestUtils.buildTag(6L, "outlier tag", TagType.NonEdible);


        Long dishId1 = 1212L;
        Long dishId2 = 2323L;
        DishEntity dish1 = ServiceTestUtils.buildDishWithTags(userId, "dish one", Arrays.asList(tag1, tag2));
        DishEntity dish2 = ServiceTestUtils.buildDishWithTags(userId, "dish two", Arrays.asList(tag1, tag3, tag4, tag5, tag6));
        dish1.setId(dishId1);
        dish2.setId(dishId2);
        // put tags into items
        DishItemEntity d1Item1 = ServiceTestUtils.buildDishItemFromTag(11L, dish1, tag1);
        DishItemEntity d1Item2 = ServiceTestUtils.buildDishItemFromTag(22L, dish1, tag2);
        DishItemEntity d2Item1 = ServiceTestUtils.buildDishItemFromTag(11L, dish2, tag1);
        DishItemEntity d2Item3 = ServiceTestUtils.buildDishItemFromTag(33L, dish2, tag3);
        DishItemEntity d2Item4 = ServiceTestUtils.buildDishItemFromTag(44L, dish2, tag4);
        DishItemEntity d2Item5 = ServiceTestUtils.buildDishItemFromTag(55L, dish2, tag5);
        DishItemEntity d2Item6 = ServiceTestUtils.buildDishItemFromTag(66L, dish2, tag6);
        List<DishItemEntity> dishItems = Arrays.asList(d1Item1, d1Item2, d2Item1,
                d2Item3, d2Item4, d2Item5, d2Item6);
        ArgumentCaptor<ShoppingListEntity> argument = ArgumentCaptor.forClass(ShoppingListEntity.class);

        ListAddProperties addProperties = new ListAddProperties();
        addProperties.setDishSources(Arrays.asList(dishId1.toString(), dishId2.toString()));

        // expectations
        Mockito.when(shoppingListRepository.getWithItemsByListId(listId))
                .thenReturn(Optional.of(shoppingList));
        Mockito.when(dishService.getDishItems(eq(userId), any(List.class)))
                .thenReturn(dishItems);

        Mockito.when(listItemStateMachine.handleEvent(any(ListItemEvent.class),
                        any(ItemStateContext.class)))
                .thenReturn(new ListItemEntity());
        Mockito.doNothing().when(dishService).updateLastAddedForDishes(any(List.class));
        Mockito.when(shoppingListRepository.save(argument.capture()))
                .thenReturn(new ShoppingListEntity());

        // call
        shoppingListService.addDishesToList(userId, listId, addProperties);

        // verification afterwards
        // note - checking captured list
        // list is not null
        ShoppingListEntity listResult = argument.getValue();
        Assert.assertNotNull(listResult);

        // list should contain 7 items
        Assert.assertEquals(7, listResult.getItems().size());
    }

    @Test
    public void testAddDishToList() throws ItemProcessingException, ShoppingListException {
        Long userId = 99L;
        Long listId = 88L;
        Long dishId = 77L;

        ShoppingListEntity shoppingList = dummyShoppingList(listId, userId, new ArrayList<>());
        TagEntity tag1 = ServiceTestUtils.buildTag(1L, "first tag", TagType.Ingredient);
        TagEntity tag2 = ServiceTestUtils.buildTag(2L, "second tag", TagType.Ingredient);
        DishEntity dish1 = ServiceTestUtils.buildDishWithTags(userId, dishId, "dish one", Arrays.asList(tag1, tag2));


        // mock calls
        ArgumentCaptor<List> listArgument = ArgumentCaptor.forClass(List.class);
        Mockito.when(shoppingListRepository.getWithItemsByListId(listId))
                .thenReturn(Optional.of(shoppingList));
        Mockito.when(tagService.getItemsForDish(userId, dishId))
                .thenReturn(dish1.getItems());
        Mockito.when(listItemStateMachine.handleEvent(any(ListItemEvent.class), any(ItemStateContext.class)))
                .thenReturn(new ListItemEntity());
        Mockito.doNothing().when(itemChangeRepository).saveItemChangeStatistics(
                any(ShoppingListEntity.class),
                listArgument.capture(),
                any(List.class),
                any(Long.class),
                eq(ListOperationType.DISH_ADD));


        // call under test
        shoppingListService.addDishToList(userId, listId, dishId);

        Mockito.verify(itemChangeRepository, times(1)).saveItemChangeStatistics(
                any(ShoppingListEntity.class),
                any(List.class),
                any(List.class),
                any(Long.class),
                eq(ListOperationType.DISH_ADD));
        // changed items - count of 2
        Assertions.assertNotNull(listArgument.getValue());
        Assertions.assertEquals(2, listArgument.getValue().size());
    }

    @Test
    public void testPerformItemOperation_Remove() throws ItemProcessingException {
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
        Mockito.when(shoppingListRepository.findById(sourceListId))
                .thenReturn(Optional.of(sourceList));

        Mockito.when(shoppingListRepository.save(argument.capture()))
                .thenReturn(sourceList);

        // call under test
        shoppingListService.performItemOperation(userId, sourceListId, ItemOperationType.Remove, operationTagIds, null);

        // after remove, the list should contain only one item - id 3
        // list is not null
        ShoppingListEntity listResult = argument.getValue();
        Assert.assertNotNull(listResult);
        // can't test real removal of items, since this is done in
        // a mocked service.  Will depend on other tests (basically ITs) for that
    }

    @Test
    public void testPerformItemOperation_RemoveMultipleCount() throws ItemProcessingException {
        String username = "Eustace";
        Long sourceListId = 99L;
        List<Long> operationTagIds = Arrays.asList(4L, 5L, 8L);

        Long userId = 9L;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setEmail(username);

        // 3 items, tagIds 3,4,8
        ShoppingListEntity sourceList = dummyShoppingList(sourceListId, userId);
        // make all items multiple counts
        sourceList.getItems().forEach(item -> item.setUsedCount(5));

        List<TagType> tagTypeList = new ArrayList<>();
        tagTypeList.add(TagType.Ingredient);
        tagTypeList.add(TagType.NonEdible);

        ArgumentCaptor<ShoppingListEntity> argument = ArgumentCaptor.forClass(ShoppingListEntity.class);

        // expectations
        Mockito.when(shoppingListRepository.findById(sourceListId))
                .thenReturn(Optional.of(sourceList));

        //itemChangeRepository.saveItemChanges(any(ShoppingListEntity.class), any(ItemCollector.class), eq(userId), any(CollectorContext.class));
        Mockito.when(shoppingListRepository.save(argument.capture()))
                .thenReturn(sourceList);

        // call under test
        shoppingListService.performItemOperation(userId, sourceListId, ItemOperationType.Remove, operationTagIds, null);

        // after remove, the list should contain only one item - id 3
        // list is not null
        ShoppingListEntity listResult = argument.getValue();
        Assert.assertNotNull(listResult);
    }

    @Test
    public void testPerformItemOperation_RemoveCrossedOff() throws ItemProcessingException {
        String username = "Eustace";
        Long sourceListId = 99L;
        List<Long> operationTagIds = Arrays.asList(4L, 5L, 8L);

        Long userId = 9L;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setEmail(username);

        // 3 items, tagIds 3,4,8 - 3 and 8 are crossed off
        ShoppingListEntity sourceList = dummyShoppingList(sourceListId, userId);
        Map<Long, ListItemEntity> sourceItems = sourceList.getItems().stream()
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        sourceItems.get(3L).setCrossedOff(new Date());
        sourceItems.get(8L).setCrossedOff(new Date());
        List<TagType> tagTypeList = new ArrayList<>();
        tagTypeList.add(TagType.Ingredient);
        tagTypeList.add(TagType.NonEdible);

        ArgumentCaptor<ShoppingListEntity> argument = ArgumentCaptor.forClass(ShoppingListEntity.class);

        // expectations
        Mockito.when(shoppingListRepository.findById(sourceListId))
                .thenReturn(Optional.of(sourceList));

        //itemChangeRepository.saveItemChanges(any(ShoppingListEntity.class), any(ItemCollector.class), eq(userId), any(CollectorContext.class));
        Mockito.when(shoppingListRepository.save(argument.capture()))
                .thenReturn(sourceList);

        // call under test
        shoppingListService.performItemOperation(userId, sourceListId, ItemOperationType.RemoveCrossedOff, operationTagIds, null);

        // after remove, the list should contain only one item - id 4
        // list is not null
        ShoppingListEntity listResult = argument.getValue();
        Assert.assertNotNull(listResult);
    }

    @Test
    public void testPerformItemOperation_RemoveAll() throws ItemProcessingException {
        String username = "Eustace";
        Long sourceListId = 99L;

        Long userId = 9L;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setEmail(username);

        // 3 items, tagIds 3,4,8 - 3, 4 and 8 are crossed off
        ShoppingListEntity sourceList = dummyShoppingList(sourceListId, userId);
        List<TagType> tagTypeList = new ArrayList<>();
        tagTypeList.add(TagType.Ingredient);
        tagTypeList.add(TagType.NonEdible);

        ArgumentCaptor<ShoppingListEntity> argument = ArgumentCaptor.forClass(ShoppingListEntity.class);

        // expectations
        Mockito.when(shoppingListRepository.findById(sourceListId))
                .thenReturn(Optional.of(sourceList));

        //itemChangeRepository.saveItemChanges(any(ShoppingListEntity.class), any(ItemCollector.class), eq(userId), any(CollectorContext.class));
        Mockito.when(shoppingListRepository.save(argument.capture()))
                .thenReturn(sourceList);

        // call under test
        shoppingListService.performItemOperation(userId, sourceListId, ItemOperationType.RemoveAll, new ArrayList<>(), null);

        // after remove, the list should contain only one item - id 4
        // list is not null
        ShoppingListEntity listResult = argument.getValue();
        Assert.assertNotNull(listResult);
    }

    @Test
    public void testPerformItemOperation_Move() throws ItemProcessingException {
        String username = "Eustace";
        Long sourceListId = 99L;
        Long destinationListId = 96L;
        List<Long> operationTagIds = Arrays.asList(4L, 5L, 8L);

        Long userId = 9L;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setEmail(username);

        // 3 items, tagIds 3,4,8
        ShoppingListEntity sourceList = dummyShoppingList(sourceListId, userId, operationTagIds);
        List<TagType> tagTypeList = new ArrayList<>();
        tagTypeList.add(TagType.Ingredient);
        tagTypeList.add(TagType.NonEdible);

        // 3 items, tagIds 4,8,7
        ShoppingListEntity destinationList = dummyShoppingList(destinationListId, userId, Arrays.asList(4L, 8L, 7L));

        // tags for operation
        Map<Long, TagEntity> tagDictionary = dummyTagDictionary(operationTagIds);

        ArgumentCaptor<ShoppingListEntity> argument = ArgumentCaptor.forClass(ShoppingListEntity.class);

        // expectations
        Mockito.when(shoppingListRepository.findById(sourceListId))
                .thenReturn(Optional.of(sourceList));
        Mockito.when(shoppingListRepository.findById(destinationListId))
                .thenReturn(Optional.of(destinationList));

        //itemChangeRepository.saveItemChanges(any(ShoppingListEntity.class), any(ItemCollector.class), eq(userId), any(CollectorContext.class));
        Mockito.when(shoppingListRepository.save(argument.capture()))
                .thenReturn(sourceList);

        // call under test
        shoppingListService.performItemOperation(userId, sourceListId, ItemOperationType.Move, operationTagIds, destinationListId);

        // after remove, the list should contain only one item - id 3
        // list is not null
        List<ShoppingListEntity> resultLists = argument.getAllValues();
        ShoppingListEntity firstCallResult = resultLists.get(0);
        ShoppingListEntity secondCallResult = resultLists.get(1);

    }

    @Test
    public void testPerformItemOperation_Copy() throws ItemProcessingException {
        String username = "Eustace";
        Long sourceListId = 99L;
        Long destinationListId = 96L;
        List<Long> operationTagIds = Arrays.asList(4L, 5L, 8L);

        Long userId = 9L;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setEmail(username);

        // 3 items, tagIds 4,5,8
        ShoppingListEntity sourceList = dummyShoppingList(sourceListId, userId, operationTagIds);
        // 3 items, tagIds 4,8,7
        ShoppingListEntity destinationList = dummyShoppingList(destinationListId, userId, Arrays.asList(4L, 8L, 7L));

        List<TagType> tagTypeList = new ArrayList<>();
        tagTypeList.add(TagType.Ingredient);
        tagTypeList.add(TagType.NonEdible);


        // tags for operation
        Map<Long, TagEntity> tagDictionary = dummyTagDictionary(operationTagIds);

        ArgumentCaptor<ShoppingListEntity> argument = ArgumentCaptor.forClass(ShoppingListEntity.class);

        // expectations
        Mockito.when(shoppingListRepository.findById(sourceListId))
                .thenReturn(Optional.of(sourceList));
        Mockito.when(shoppingListRepository.findById(destinationListId))
                .thenReturn(Optional.of(destinationList));


        Mockito.when(shoppingListRepository.save(argument.capture()))
                .thenReturn(destinationList);

        // call under test
        shoppingListService.performItemOperation(userId, sourceListId, ItemOperationType.Copy, operationTagIds, destinationListId);

        // list is not null
        List<ShoppingListEntity> resultLists = argument.getAllValues();
        ShoppingListEntity firstCallResult = resultLists.get(0);
        Assert.assertNotNull(firstCallResult);

    }

    private ShoppingListEntity expectGetShoppingListById(Long shoppingListId, Long userId, List<Long> tagIds, String userEmail) {
        ShoppingListEntity shoppingList = dummyShoppingList(shoppingListId, userId, tagIds);

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setEmail(userEmail);

        // expectations
        Mockito.when(shoppingListRepository.getWithItemsByListId(shoppingListId))
                .thenReturn(Optional.ofNullable(shoppingList));

        return shoppingList;
    }

    @Test
    public void testMerge_AllChangedOneNewFromClient() {
        Long mergeListId = 9999L;
        final Long serverListId = 9999L;
        final Long[] serverTagIds = {8887L, 8888L, 8889L};
        final Long userId = 2L;
        final String userEmail = "me@mine.ours";

        LocalDate date = LocalDate.of(2020, 01, 01);
        Date addedDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        ShoppingListEntity shoppingList = expectGetShoppingListById(serverListId, userId, Arrays.asList(serverTagIds), userEmail);
        shoppingList.getItems().stream().forEach(item -> item.setAddedOn(addedDate));


        // add several new items to the list
        MergeRequest clientMergeRequest = new MergeRequest();
        clientMergeRequest.setListId(mergeListId);
        Map<Long, TagEntity> tagDictionary = new HashMap<>();
        for (long i = 500; i < 504; i++) {
            clientMergeRequest.getMergeItems().add(createMergeTestItem(String.valueOf(i), 4, 0, 0, 0));
            TagEntity tag = new TagEntity(i);
            tag.setName(String.valueOf(i));
            tagDictionary.put(i, tag);
        }

        ArgumentCaptor<ItemCollector> collectorCapture = ArgumentCaptor.forClass(ItemCollector.class);

        Mockito.when(tagService.getReplacedTagsFromIds(any(Set.class)))
                .thenReturn(new ArrayList<Long>());
        Mockito.when(tagService.getDictionaryForIds(any(Set.class)))
                .thenReturn(tagDictionary);

        Mockito.when(shoppingListRepository.save(any(ShoppingListEntity.class)))
                .thenReturn(shoppingList);

        shoppingListService.mergeFromClient(userId, clientMergeRequest);


        // for testing after call
        Mockito.verify(itemChangeRepository).legacySaveItemChanges(any(ShoppingListEntity.class),
                collectorCapture.capture(),
                any(Long.class),
                any(CollectorContext.class));

        Assert.assertNotNull(collectorCapture);
        Assert.assertNotNull(collectorCapture.getValue());
        MergeItemCollector capturedToVerify = (MergeItemCollector) collectorCapture.getValue();
        Assert.assertEquals(Long.valueOf(9999L), capturedToVerify.getListId());
        Map<Long, CollectedItem> itemMap = capturedToVerify.getTagCollectedMap();

        Assert.assertEquals(7, itemMap.keySet().size());
        // assert 8888 is there, and unchanged
        CollectedItem testItem = itemMap.get(8888L);
        Assert.assertNotNull(testItem);
        Assert.assertFalse(testItem.isChanged());

        // assert 501 is there, with added = true
        CollectedItem testItem2 = itemMap.get(501L);
        Assert.assertNotNull(testItem2);
        Assert.assertTrue(testItem2.isChanged());
        Assert.assertTrue(testItem2.isAdded());

    }

    @Test
    public void testMerge_StandardUserTagConflicts() {
        Long mergeListId = 9999L;
        final Long serverListId = 9999L;
        final Long userId = 2L;
        final String userEmail = "me@mine.ours";

        LocalDate date = LocalDate.of(2020, 01, 01);
        ShoppingListEntity emptyServerShoppingList = expectGetShoppingListById(serverListId, userId, Collections.emptyList(), userEmail);

        // standard user mapping
        List<LongTagIdPairDTO> conflicts = new ArrayList<>();
        conflicts.add(new LongTagIdPairDTO(500L, 1500L));
        conflicts.add(new LongTagIdPairDTO(501L, 1501L));
        // add several new items to the list
        MergeRequest clientMergeRequest = new MergeRequest();
        clientMergeRequest.setCheckTagConflict(true);
        clientMergeRequest.setListId(mergeListId);
        Map<Long, TagEntity> tagDictionary = new HashMap<>();
        Set<Long> mergeTagKeys = new HashSet<>();
        for (long i = 500; i < 504; i++) {
            clientMergeRequest.getMergeItems().add(createMergeTestItem(String.valueOf(i),
                    4,
                    0, 0, 0));
            TagEntity tag = new TagEntity(i);
            mergeTagKeys.add(i);
            tag.setName(String.valueOf(i));
            tagDictionary.put(i, tag);
            TagEntity userDefinedTag = new TagEntity(i + 1000);
            tagDictionary.put(i + 1000, userDefinedTag);
        }

        ArgumentCaptor<ItemCollector> collectorCapture = ArgumentCaptor.forClass(ItemCollector.class);

        Mockito.when(tagService.getStandardUserDuplicates(userId, mergeTagKeys))
                .thenReturn(conflicts);
        Mockito.when(tagService.getReplacedTagsFromIds(any(Set.class)))
                .thenReturn(new ArrayList<Long>());
        Mockito.when(tagService.getDictionaryForIds(any(Set.class)))
                .thenReturn(tagDictionary);

        Mockito.when(shoppingListRepository.save(any(ShoppingListEntity.class)))
                .thenReturn(emptyServerShoppingList);

        shoppingListService.mergeFromClient(userId, clientMergeRequest);


        // for testing after call
        Mockito.verify(itemChangeRepository).legacySaveItemChanges(any(ShoppingListEntity.class),
                collectorCapture.capture(),
                any(Long.class),
                any(CollectorContext.class));

        Assert.assertNotNull(collectorCapture);
        Assert.assertNotNull(collectorCapture.getValue());
        MergeItemCollector capturedToVerify = (MergeItemCollector) collectorCapture.getValue();
        Assert.assertEquals(Long.valueOf(9999L), capturedToVerify.getListId());
        Map<Long, CollectedItem> itemMap = capturedToVerify.getTagCollectedMap();

        Assert.assertEquals(4, itemMap.keySet().size());
        // assert 1500, 1501 are there
        Assert.assertNotNull(itemMap.get(1500L));
        Assert.assertNotNull(itemMap.get(1501L));

        // assert 500, 501 are not there
        Assert.assertNull(itemMap.get(500L));
        Assert.assertNull(itemMap.get(501L));

        // assert 503, 503 are there
        Assert.assertNotNull(itemMap.get(502L));
        Assert.assertNotNull(itemMap.get(503L));
    }

    @Test
    public void testMerge_ChangesServerSide() {
        Long mergeListId = 9999L;
        final Long serverListId = 9999L;
        final Long[] serverTagIds = {501L, 502L, 503L};
        final Long userId = 2L;
        final String userEmail = "me@mine.ours";

        Date removedDate = getDateForInterval(1);
        ShoppingListEntity shoppingList = expectGetShoppingListById(serverListId, userId, Arrays.asList(serverTagIds), userEmail);
        shoppingList.getItems().stream().forEach(item -> item.setRemovedOn(removedDate));


        // add several new items to the list
        MergeRequest clientMergeRequest = new MergeRequest();
        clientMergeRequest.setListId(mergeListId);
        Map<Long, TagEntity> tagDictionary = new HashMap<>();
        for (long i = 500; i < 504; i++) {
            clientMergeRequest.getMergeItems().add(createMergeTestItem(String.valueOf(i), 4, 0, 0, 0));
            TagEntity tag = new TagEntity(i);
            tag.setName(String.valueOf(i));
            tagDictionary.put(i, tag);
        }

        ArgumentCaptor<ItemCollector> collectorCapture = ArgumentCaptor.forClass(ItemCollector.class);

        Mockito.when(tagService.getReplacedTagsFromIds(any(Set.class)))
                .thenReturn(new ArrayList<Long>());
        Mockito.when(tagService.getDictionaryForIds(any(Set.class)))
                .thenReturn(tagDictionary);

        Mockito.when(shoppingListRepository.save(any(ShoppingListEntity.class)))
                .thenReturn(shoppingList);

        shoppingListService.mergeFromClient(userId, clientMergeRequest);


        // for testing after call
        Mockito.verify(itemChangeRepository).legacySaveItemChanges(any(ShoppingListEntity.class),
                collectorCapture.capture(),
                any(Long.class),
                any(CollectorContext.class));

        Assert.assertNotNull(collectorCapture);
        Assert.assertNotNull(collectorCapture.getValue());
        MergeItemCollector capturedToVerify = (MergeItemCollector) collectorCapture.getValue();
        Assert.assertEquals(Long.valueOf(9999L), capturedToVerify.getListId());
        Map<Long, CollectedItem> itemMap = capturedToVerify.getTagCollectedMap();

        Assert.assertEquals(4, itemMap.keySet().size());
        // assert 500 is there, with added
        CollectedItem testItem = itemMap.get(500L);
        Assert.assertNotNull(testItem);
        Assert.assertTrue(testItem.isChanged());
        Assert.assertTrue(testItem.isAdded());

        // assert 501 is there, with changed false
        CollectedItem testItem2 = itemMap.get(501L);
        Assert.assertNotNull(testItem2);
        Assert.assertFalse(testItem2.isChanged());
        Assert.assertNotNull(testItem2.getRemovedOn());

    }

    @Test
    public void testDeleteItemFromList() throws ItemProcessingException {
        Long deleteFromListId = 9999L;
        final String userEmail = "me@mine.ours";
        final Long userId = 2L;
        final Long[] listTagIds = {501L, 502L, 503L};
        final Long itemIdToRemove = 501L * 1111111;

        Date addedDate = getDateForInterval(1);
        ShoppingListEntity shoppingList = expectGetShoppingListById(deleteFromListId, userId, Arrays.asList(listTagIds), userEmail);
        shoppingList.getItems().stream().forEach(item -> item.setAddedOn(addedDate));

        final Optional<ListItemEntity> itemToRemoveOpt = shoppingList.getItems()
                .stream()
                .filter(item -> item.getId().equals(itemIdToRemove))
                .findFirst();


        ArgumentCaptor<ItemCollector> collectorCapture = ArgumentCaptor.forClass(ItemCollector.class);

        Mockito.when(itemRepository.findById(itemIdToRemove))
                .thenReturn(itemToRemoveOpt);

        Mockito.when(shoppingListRepository.save(any(ShoppingListEntity.class)))
                .thenReturn(shoppingList);

        shoppingListService.deleteItemFromList(userId, deleteFromListId, itemIdToRemove);


    }

    @Test
    public void testUpdateList() {
        String userName = "george";
        Long userId = 999L;
        Long listId = 99L;
        ShoppingListEntity updateFrom = new ShoppingListEntity();
        updateFrom.setName("has been updated");
        updateFrom.setIsStarterList(false);

        ShoppingListEntity originalList = new ShoppingListEntity();
        originalList.setName("originalList");
        originalList.setIsStarterList(true);


        UserEntity user = new UserEntity();
        user.setId(999L);

        ArgumentCaptor<ShoppingListEntity> listArgument = ArgumentCaptor.forClass(ShoppingListEntity.class);

        Mockito.when(shoppingListRepository.findByListIdAndUserId(listId, 999L))
                .thenReturn(Optional.of(originalList));
        Mockito.when(shoppingListRepository.save(listArgument.capture()))
                .thenReturn(updateFrom);

        shoppingListService.updateList(userId, listId, updateFrom);

        Assert.assertEquals("has been updated", listArgument.getValue().getName());
        Assert.assertEquals(false, listArgument.getValue().getIsStarterList());
    }

    private Item createMergeTestItem(String tagId, int addedDateInterval, int updatedDateInterval, int crossedOffDateInterval, int removedDateInterval) {
        Item item = new Item();
        item.setTagId(tagId);
        item.usedCount(1);
        item.addedOn(getDateForInterval(addedDateInterval));
        item.updated(getDateForInterval(updatedDateInterval));
        item.crossedOff(getDateForInterval(crossedOffDateInterval));
        item.removed(getDateForInterval(removedDateInterval));
        return item;
    }

    private Date getDateForInterval(int interval) {
        LocalDateTime now = LocalDateTime.now();
        if (interval == 0) {
            return null;
        }
        if (interval == 99) {
            // cheating for test - 5 seconds in the future
            LocalDateTime newDate = now.plusSeconds(5);
            return java.sql.Timestamp.valueOf(newDate);
        }
        LocalDateTime newDate = now.minusDays(interval);
        return java.sql.Timestamp.valueOf(newDate);
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
        List<ListItemEntity> items = new ArrayList<>();
        for (Long tagbase : tagIds) {
            Long itemId = tagbase * 1111111;
            TagEntity tag = ServiceTestUtils.buildTag(tagbase, String.valueOf(tagbase), TagType.Ingredient);
            ListItemEntity item = ServiceTestUtils.buildItem(itemId, tag, shoppingListId);
            item.setUsedCount(1);
            items.add(item);
        }
        // add items to list
        listEntity.getItems().addAll(items);

        return listEntity;

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


}
