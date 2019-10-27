package com.meg.atable.lmt.service.impl;

import com.meg.atable.auth.data.entity.UserEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.lmt.api.model.ListGenerateProperties;
import com.meg.atable.lmt.api.model.ListLayoutType;
import com.meg.atable.lmt.data.entity.ListLayoutEntity;
import com.meg.atable.lmt.data.entity.ShoppingListEntity;
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
import java.util.Arrays;
import java.util.Collections;

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
        /*

        ShoppingListEntity result = shoppingListService.generateListFromMealPlan(userAccount.getEmail(), TestConstants.MEAL_PLAN_1_ID);
        Assert.assertNotNull(result);
         */
    }
}