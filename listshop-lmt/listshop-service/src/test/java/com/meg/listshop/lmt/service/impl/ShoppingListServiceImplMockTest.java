package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.lmt.api.exception.ActionInvalidException;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.data.ItemChangeRepository;
import com.meg.listshop.lmt.data.entity.*;
import com.meg.listshop.lmt.data.pojos.LongTagIdPairDTO;
import com.meg.listshop.lmt.data.repository.ItemRepository;
import com.meg.listshop.lmt.data.repository.ShoppingListRepository;
import com.meg.listshop.lmt.dish.DishService;
import com.meg.listshop.lmt.service.*;
import com.meg.listshop.lmt.service.tag.TagService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

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
    private ShoppingListRepository shoppingListRepository;
    @MockBean
    private LayoutService layoutService;
    @MockBean
    private ListSearchService listSearchService;
    @MockBean
    private MealPlanService mealPlanService;
    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private ItemChangeRepository itemChangeRepository;
    @MockBean
    private ListTagStatisticService listTagStatisticService;

    @Before
    public void setUp() {

        shoppingListService = new ShoppingListServiceImpl(tagService,
                dishService,
                shoppingListRepository,
                layoutService,
                mealPlanService,
                itemRepository,
                itemChangeRepository,
                listTagStatisticService);
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


        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(user);
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


        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(user);
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


        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(user);

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


        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(user);
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
        Mockito.when(shoppingListRepository.findByListIdAndUserId(listId,userId)).thenReturn(Optional.of(shoppingListForSave));
        Mockito.when(shoppingListRepository.save(listArgument.capture())).thenReturn(shoppingListForSave);


        // test call
        shoppingListService.deleteList(userId, listId);

        Mockito.verify(shoppingListRepository, times(1)).findByListIdAndUserId(listId,userId);
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

        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(user);
        Mockito.when(shoppingListRepository.findByUserIdOrderByLastUpdateDesc(listId)).thenReturn(new ArrayList<>());

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

        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(user);
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

        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(user);
        Mockito.when(shoppingListRepository.findByUserIdOrderByLastUpdateDesc(userId)).thenReturn(listOfLists);

        // test call
        shoppingListService.deleteList(userId, listId);


    }
    // test list - last list

    @Test
    public void testAddItemToListByTag() {
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

        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(user);
        Mockito.when(shoppingListRepository.getWithItemsByListId(listId)).thenReturn(Optional.of(shoppingList));
        Mockito.when(tagService.getTagById(tagId)).thenReturn(tagToAdd);

        // test call
        shoppingListService.addItemToListByTag(userId, listId, tagId);

        Mockito.verify(itemChangeRepository, times(1)).saveItemChanges(any(ShoppingListEntity.class),
                any(ListItemCollector.class), any(Long.class), any(CollectorContext.class));
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

        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(user);
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
    public void testDeleteAllItemsFromList() {
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

        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(user);
        Mockito.when(shoppingListRepository.findByListIdAndUserId(listId,userId)).thenReturn(Optional.of(shoppingList));
        Mockito.when(itemRepository.findByListId(listId)).thenReturn(items);

        // test call
        shoppingListService.deleteAllItemsFromList(userId, listId);


        Mockito.verify(shoppingListRepository, times(1)).findByListIdAndUserId(listId,userId);
        Mockito.verify(itemRepository, times(1)).findByListId(listId);
        Mockito.verify(itemChangeRepository, times(1)).saveItemChanges(any(ShoppingListEntity.class),
                any(ListItemCollector.class), any(Long.class), any(CollectorContext.class));
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
    public void testDeleteAllItemsFromList_ListNotFound() {
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

        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(user);
        Mockito.when(shoppingListRepository.findByListIdAndUserId(listId, userId)).thenReturn(Optional.empty());

        // test call
        shoppingListService.deleteAllItemsFromList(userId, listId);


        Mockito.verify(shoppingListRepository, times(1)).findByListIdAndUserId(listId,userId);

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


        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(user);
        Mockito.when(shoppingListRepository.findByUserIdOrderByLastUpdateDesc(userId)).thenReturn(new ArrayList<>());

        // test call
        shoppingListService.getListsByUserId(userId);


        Mockito.verify(shoppingListRepository, times(1)).findByUserIdOrderByLastUpdateDesc(userId);

    }

    @Test
    public void testCrossOffAllItems() {
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

        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(user);
        Mockito.when(shoppingListRepository.getWithItemsByListId(listId)).thenReturn(Optional.of(shoppingList));


        // test call
        shoppingListService.crossOffAllItems(userId, listId, true);


        Mockito.verify(shoppingListRepository, times(1)).getWithItemsByListId(listId);
        Mockito.verify(itemRepository, times(1)).saveAll(crossedOffItems.capture());

        List<ListItemEntity> updatedItems = crossedOffItems.getValue();
        Assert.assertNotNull(updatedItems);
        Assert.assertEquals("should be three items", 3, updatedItems.size());
        Assert.assertTrue("nothing uncrossed off should be found",
                updatedItems.stream().noneMatch(i -> i.getCrossedOff() == null));
    }

    @Test
    public void testUpdateItemCrossedOff() {
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
        items.add(ServiceTestUtils.buildItem(1L, new TagEntity(11L), listId));
        items.add(ServiceTestUtils.buildItem(2L, new TagEntity(22L), listId));
        items.add(ServiceTestUtils.buildItem(3L, new TagEntity(33L), listId));
        shoppingList.setItems(items);

        ArgumentCaptor<ListItemEntity> itemCapture = ArgumentCaptor.forClass(ListItemEntity.class);


        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(user);
        Mockito.when(shoppingListRepository.getWithItemsByListId(listId)).thenReturn(Optional.of(shoppingList));
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        // test call
        shoppingListService.updateItemCrossedOff(userId, listId, itemId, true);


        Mockito.verify(shoppingListRepository, times(1)).getWithItemsByListId(listId);
        Mockito.verify(itemRepository, times(1)).save(itemCapture.capture());
        Mockito.verify(shoppingListRepository, times(1)).save(any(ShoppingListEntity.class));

        // Assertions
        ListItemEntity resultItem = itemCapture.getValue();
        Assert.assertNotNull(resultItem);
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


        Mockito.when(listSearchService.getTagToCategoryMap(layoutId, items
                        .stream().map(ListItemEntity::getTag)
                        .collect(Collectors.toList())))
                .thenReturn(tagIdToCategoryId);
        //Mockito.when(layoutService.getListCategoriesForLayout(layoutId)).thenReturn(categories);
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


        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(user);
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


        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(user);
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
    public void testCreateList_duplicateName() throws ShoppingListException {
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

        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(user);
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
    public void testGenerateListFromMealPlan() {
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
        Mockito.when(userService.getUserByUserEmail(username))
                .thenReturn(userEntity);
        Mockito.when(mealPlanService.getMealPlanForUserById(userId, mealPlanId))
                .thenReturn(mealPlan);
        Mockito.when(layoutService.getDefaultUserLayout(userId))
                .thenReturn(listLayoutEntity);
        Mockito.when(shoppingListRepository.getWithItemsByListIdAndItemsRemovedOnIsNull(listId))
                .thenReturn(Optional.of(createdShoppingList));
        Mockito.when(tagService.getTagsForDish(userId, dish1.getId(), tagTypeList))
                .thenReturn(Arrays.asList(tag1, tag2));
        Mockito.when(tagService.getTagsForDish(userId, dish2.getId(), tagTypeList))
                .thenReturn(Arrays.asList(tag1, tag3, tag4, tag5));
        mealPlanService.updateLastAddedDateForDishes(mealPlan);

        itemChangeRepository.saveItemChanges(any(ShoppingListEntity.class), any(ItemCollector.class), eq(userId), any(CollectorContext.class));
        Mockito.when(shoppingListRepository.save(argument.capture()))
                .thenReturn(createdShoppingList);
        Mockito.when(shoppingListRepository.getWithItemsByListIdAndItemsRemovedOnIsNull(listId))
                .thenReturn(Optional.of(createdShoppingList));


        // call
        shoppingListService.generateListFromMealPlan(userId, mealPlanId);

        // verification afterwards
        // note - checking captured list
        // list is not null
        ShoppingListEntity listResult = argument.getValue();
        Assert.assertNotNull(listResult);
        // list should contain 6 items
        //Assert.assertEquals(6, listResult.getItems().size());
        // verify items
        // put items into map
        Map<Long, ListItemEntity> resultMap = listResult.getItems().stream()
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
        Mockito.when(shoppingListRepository.getWithItemsByListId(listId))
                .thenReturn(Optional.of(shoppingList));
        Mockito.when(tagService.getTagsForDish(userId, dish1.getId(), tagTypeList))
                .thenReturn(Arrays.asList(tag1, tag2));
        Mockito.when(tagService.getTagsForDish(userId, dish2.getId(), tagTypeList))
                .thenReturn(Arrays.asList(tag1, tag3, tag4, tag5));
        mealPlanService.updateLastAddedDateForDishes(mealPlan);

        itemChangeRepository.saveItemChanges(any(ShoppingListEntity.class), any(ItemCollector.class), eq(userId), any(CollectorContext.class));
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
        // note - checking captured list
        // list is not null
        ShoppingListEntity listResult = argument.getValue();
        Assert.assertNotNull(listResult);
        // list should contain 6 items
        //Assert.assertEquals(6, listResult.getItems().size());
        // verify items
        // put items into map
        Map<Long, ListItemEntity> resultMap = listResult.getItems().stream()
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
    public void testAddDishesToList() throws ShoppingListException {
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
        // put tags into items
        DishItemEntity item1 = ServiceTestUtils.buildDishItemFromTag(11L, tag1);
        DishItemEntity item2 = ServiceTestUtils.buildDishItemFromTag(22L, tag2);
        DishItemEntity item3 = ServiceTestUtils.buildDishItemFromTag(33L, tag3);
        DishItemEntity item4 = ServiceTestUtils.buildDishItemFromTag(44L, tag4);
        DishItemEntity item5 = ServiceTestUtils.buildDishItemFromTag(55L, tag5);
        DishItemEntity item6 = ServiceTestUtils.buildDishItemFromTag(66L, tag6);

        Long dishId1 = 1212L;
        Long dishId2 = 2323L;
        DishEntity dish1 = ServiceTestUtils.buildDishWithTags(userId, "dish one", Arrays.asList(tag1, tag2));
        DishEntity dish2 = ServiceTestUtils.buildDishWithTags(userId, "dish two", Arrays.asList(tag1, tag3, tag4, tag5, tag6));
        dish1.setId(dishId1);
        dish2.setId(dishId2);

        ArgumentCaptor<ShoppingListEntity> argument = ArgumentCaptor.forClass(ShoppingListEntity.class);

        ListAddProperties addProperties = new ListAddProperties();
        addProperties.setDishSources(Arrays.asList(dishId1.toString(), dishId2.toString()));

        // expectations
        Mockito.when(userService.getUserByUserEmail(username))
                .thenReturn(userEntity);
        Mockito.when(shoppingListRepository.getWithItemsByListId(listId))
                .thenReturn(Optional.of(shoppingList));
        Mockito.when(tagService.getReplacedTagsFromIds(any(Set.class)))
                .thenReturn(new ArrayList<Long>());
        Mockito.when(tagService.getItemsForDish(userId, dish1.getId()))
                .thenReturn(Arrays.asList(item1, item2, item3, item4));
        Mockito.when(tagService.getItemsForDish(userId, dish2.getId()))
                .thenReturn(Arrays.asList(item6, item5, item4, item3));
        Mockito.doNothing().when(dishService).updateLastAddedForDish(dishId1);
        Mockito.doNothing().when(dishService).updateLastAddedForDish(dishId2);

        itemChangeRepository.saveItemChanges(any(ShoppingListEntity.class), any(ItemCollector.class), eq(userId), any(CollectorContext.class));
        Mockito.when(shoppingListRepository.save(argument.capture()))
                .thenReturn(shoppingList);


        // verifications before
        // list contain doesn't item from meal plan
        Optional<ListItemEntity> mealPlanItem = shoppingList.getItems().stream()
                .filter(item -> item.getTag().getId() == 1L)
                .findFirst();
        Assert.assertFalse(mealPlanItem.isPresent());

        // call
        shoppingListService.addDishesToList(userId, listId, addProperties);

        // verification afterwards
        // note - checking captured list
        // list is not null
        ShoppingListEntity listResult = argument.getValue();
        Assert.assertNotNull(listResult);
        // list should contain 6 items

        Assert.assertEquals(6, listResult.getItems().size());
        // verify items
        // put items into map
        Map<Long, ListItemEntity> resultMap = listResult.getItems().stream()
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assert.assertNotNull(resultMap);
        // tag 1, 2, 5, 6 should be there - once
        Assert.assertNotNull(resultMap.get(1L));
        Assert.assertEquals(1, resultMap.get(1L).getUsedCount().longValue());
        Assert.assertNotNull(resultMap.get(2L));
        Assert.assertEquals(1, resultMap.get(2L).getUsedCount().longValue());
        Assert.assertNotNull(resultMap.get(5L));
        Assert.assertEquals(1, resultMap.get(5L).getUsedCount().longValue());
        Assert.assertNotNull(resultMap.get(6L));
        Assert.assertEquals(1, resultMap.get(6L).getUsedCount().longValue());
        // tags 3 and 4 should be there twice
        Assert.assertNotNull(resultMap.get(3L));
        Assert.assertEquals(2, resultMap.get(3L).getUsedCount().longValue());
        Assert.assertNotNull(resultMap.get(4L));
        Assert.assertEquals(2, resultMap.get(4L).getUsedCount().longValue());


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
        shoppingListService.performItemOperation(userId, sourceListId, ItemOperationType.Remove, operationTagIds, null);

        // after remove, the list should contain only one item - id 3
        // list is not null
        ShoppingListEntity listResult = argument.getValue();
        Assert.assertNotNull(listResult);
        // list should contain 1 items - id 3
        List<ListItemEntity> items = listResult.getItems();
        Assert.assertNotNull(items);
        Assert.assertFalse(items.isEmpty());
        Assert.assertEquals(3, items.size());
        // put items into map
        Map<Long, ListItemEntity> resultMap = listResult.getItems().stream()
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
        Assert.assertEquals(Long.valueOf(3L), tagId);

    }

    @Test
    public void testPerformItemOperation_RemoveMultipleCount() {
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
        shoppingListService.performItemOperation(userId, sourceListId, ItemOperationType.Remove, operationTagIds, null);

        // after remove, the list should contain only one item - id 3
        // list is not null
        ShoppingListEntity listResult = argument.getValue();
        Assert.assertNotNull(listResult);
        // list should contain 1 non-removed item - id 3
        List<ListItemEntity> items = listResult.getItems();
        Assert.assertNotNull(items);
        Assert.assertFalse(items.isEmpty());
        Assert.assertEquals(3, items.size());
        // put items into map
        Map<Long, ListItemEntity> resultMap = listResult.getItems().stream()
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assert.assertNotNull(resultMap);
        // tag 3 should be there - with used count of 5
        Assert.assertNotNull(resultMap.get(3L));
        Assert.assertEquals(5, resultMap.get(3L).getUsedCount().longValue());
        Assert.assertNull(resultMap.get(3L).getRemovedOn());
        // tags 4 and 8 should be there, but removed
        Assert.assertNotNull(resultMap.get(4L));
        Assert.assertEquals(0, resultMap.get(4L).getUsedCount().longValue());
        Assert.assertNotNull(resultMap.get(4L).getRemovedOn());
        Assert.assertNotNull(resultMap.get(8L));
        Assert.assertEquals(0, resultMap.get(8L).getUsedCount().longValue());
        Assert.assertNotNull(resultMap.get(8L).getRemovedOn());

        Long tagId = items.get(0).getTag().getId();
        Assert.assertEquals(Long.valueOf(3L), tagId);

    }

    @Test
    public void testPerformItemOperation_RemoveCrossedOff() {
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
        shoppingListService.performItemOperation(userId, sourceListId, ItemOperationType.RemoveCrossedOff, operationTagIds, null);

        // after remove, the list should contain only one item - id 4
        // list is not null
        ShoppingListEntity listResult = argument.getValue();
        Assert.assertNotNull(listResult);
        // list should contain 1 items - id 3
        List<ListItemEntity> items = listResult.getItems();
        Assert.assertNotNull(items);
        Assert.assertFalse(items.isEmpty());
        Assert.assertEquals(3, items.size());
        // put items into map
        Map<Long, ListItemEntity> resultMap = listResult.getItems().stream()
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assert.assertNotNull(resultMap);
        // tag 3 should be removed
        Assert.assertNotNull(resultMap.get(3L));
        Assert.assertNotNull(resultMap.get(3L).getRemovedOn());
        // tag 8 should be removed
        Assert.assertNotNull(resultMap.get(8L));
        Assert.assertNotNull(resultMap.get(8L).getRemovedOn());
        // tags 4 should be there, not removed
        Assert.assertNotNull(resultMap.get(4L));
        Assert.assertNull(resultMap.get(4L).getRemovedOn());

    }

    @Test
    public void testPerformItemOperation_RemoveAll() {
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
        shoppingListService.performItemOperation(userId, sourceListId, ItemOperationType.RemoveAll, new ArrayList<>(), null);

        // after remove, the list should contain only one item - id 4
        // list is not null
        ShoppingListEntity listResult = argument.getValue();
        Assert.assertNotNull(listResult);
        // list should contain 1 items - id 3
        List<ListItemEntity> items = listResult.getItems();
        Assert.assertNotNull(items);
        Assert.assertFalse(items.isEmpty());
        Assert.assertEquals(3, items.size());
        // put items into map
        Map<Long, ListItemEntity> resultMap = listResult.getItems().stream()
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assert.assertNotNull(resultMap);
        // tag 3 should be removed
        Assert.assertNotNull(resultMap.get(3L));
        Assert.assertNotNull(resultMap.get(3L).getRemovedOn());
        // tag 8 should be removed
        Assert.assertNotNull(resultMap.get(8L));
        Assert.assertNotNull(resultMap.get(8L).getRemovedOn());
        // tags 4 should be there, not removed
        Assert.assertNotNull(resultMap.get(4L));
        Assert.assertNotNull(resultMap.get(4L).getRemovedOn());

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
        Mockito.when(userService.getUserByUserEmail(username))
                .thenReturn(userEntity);
        Mockito.when(shoppingListRepository.findById(sourceListId))
                .thenReturn(Optional.of(sourceList));
        Mockito.when(shoppingListRepository.findById(destinationListId))
                .thenReturn(Optional.of(destinationList));
        Mockito.when(itemRepository.findByListId(sourceListId))
                .thenReturn(sourceList.getItems());
        Mockito.when(itemRepository.findByListIdAAndRemovedOnIsNull(destinationListId))
                .thenReturn(destinationList.getItems());
        Mockito.when(tagService.getDictionaryForIds(new HashSet<>(operationTagIds)))
                .thenReturn(tagDictionary);

        itemChangeRepository.saveItemChanges(any(ShoppingListEntity.class), any(ItemCollector.class), eq(userId), any(CollectorContext.class));
        Mockito.when(shoppingListRepository.save(argument.capture()))
                .thenReturn(sourceList);

        // call under test
        shoppingListService.performItemOperation(userId, sourceListId, ItemOperationType.Move, operationTagIds, destinationListId);

        // after remove, the list should contain only one item - id 3
        // list is not null
        List<ShoppingListEntity> resultLists = argument.getAllValues();
        ShoppingListEntity firstCallResult = resultLists.get(0);
        ShoppingListEntity secondCallResult = resultLists.get(1);

        // first call result is the copy call -
        // result list should contain original 4,7,8 plus moved 4,5,8
        // so - 4 and 8 (twice) and 5 (once) and 7 (once)
        // list should contain 1 items - id 3
        List<ListItemEntity> items = firstCallResult.getItems();
        Assert.assertNotNull(items);
        Assert.assertFalse(items.isEmpty());
        Assert.assertEquals(4, items.size());
        // put items into map
        Map<Long, ListItemEntity> resultMap = firstCallResult.getItems().stream()
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
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
        List<ListItemEntity> sourceItems = secondCallResult.getItems();
        Assert.assertNotNull(sourceItems);
        Assert.assertFalse(sourceItems.isEmpty());
        Assert.assertEquals(3, sourceItems.size());
        // put sourceItems into map
        Map<Long, ListItemEntity> sourceResultMap = sourceItems.stream()
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
        Mockito.when(userService.getUserByUserEmail(username))
                .thenReturn(userEntity);
        Mockito.when(shoppingListRepository.findById(sourceListId))
                .thenReturn(Optional.of(sourceList));
        Mockito.when(shoppingListRepository.findById(destinationListId))
                .thenReturn(Optional.of(destinationList));
        Mockito.when(itemRepository.findByListIdAAndRemovedOnIsNull(destinationListId))
                .thenReturn(destinationList.getItems());
        Mockito.when(itemRepository.findByListId(sourceListId))
                .thenReturn(sourceList.getItems());

        Mockito.when(tagService.getDictionaryForIds(new HashSet<>(operationTagIds)))
                .thenReturn(tagDictionary);

        itemChangeRepository.saveItemChanges(any(ShoppingListEntity.class), any(ItemCollector.class), eq(userId), any(CollectorContext.class));
        Mockito.when(shoppingListRepository.save(argument.capture()))
                .thenReturn(destinationList);

        // call under test
        shoppingListService.performItemOperation(userId, sourceListId, ItemOperationType.Copy, operationTagIds, destinationListId);

        // list is not null
        List<ShoppingListEntity> resultLists = argument.getAllValues();
        ShoppingListEntity firstCallResult = resultLists.get(0);
        Assert.assertNotNull(firstCallResult);

        // after remove, the destination list should contain three items
        // result list should contain original 4,7,8 plus moved 4,5,8
        // so - 4 and 8 (twice) and 5 (once) and 7 (once)
        // list should contain 1 items - id 3
        List<ListItemEntity> items = firstCallResult.getItems();
        Assert.assertNotNull(items);
        Assert.assertFalse(items.isEmpty());
        Assert.assertEquals(4, items.size());
        // put items into map
        Map<Long, ListItemEntity> resultMap = firstCallResult.getItems().stream()
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

    private ShoppingListEntity expectGetShoppingListById(Long shoppingListId, Long userId, List<Long> tagIds, String userEmail) {
        ShoppingListEntity shoppingList = dummyShoppingList(shoppingListId, userId, tagIds);

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setEmail(userEmail);

        // expectations
        Mockito.when(userService.getUserByUserEmail(userEmail))
                .thenReturn(userEntity);
        Mockito.when(shoppingListRepository.getOne(shoppingListId))
                .thenReturn(shoppingList);
        Mockito.when(shoppingListRepository.getWithItemsByListId(shoppingListId))
                .thenReturn(Optional.of(shoppingList));

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
        Mockito.verify(itemChangeRepository).saveItemChanges(any(ShoppingListEntity.class),
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
        Mockito.verify(itemChangeRepository).saveItemChanges(any(ShoppingListEntity.class),
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
        Mockito.verify(itemChangeRepository).saveItemChanges(any(ShoppingListEntity.class),
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
    public void testDeleteItemFromList() {
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

        shoppingListService.deleteItemFromList(userId, deleteFromListId, itemIdToRemove, false, null);


        // for testing after call
        Mockito.verify(itemChangeRepository).saveItemChanges(any(ShoppingListEntity.class),
                collectorCapture.capture(),
                any(Long.class),
                any(CollectorContext.class));

        Assert.assertNotNull(collectorCapture);
        Assert.assertNotNull(collectorCapture.getValue());
        ListItemCollector capturedToVerify = (ListItemCollector) collectorCapture.getValue();
        Map<Long, CollectedItem> itemMap = capturedToVerify.getTagCollectedMap();

        Assert.assertEquals(3, itemMap.keySet().size());
        // assert 501 is there, with removed
        CollectedItem testItem = itemMap.get(501L);
        Assert.assertNotNull(testItem);
        Assert.assertTrue(testItem.isChanged());
        Assert.assertTrue(testItem.isRemoved());


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
        Mockito.when(userService.getUserByUserEmail(userName)).thenReturn(user);

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
