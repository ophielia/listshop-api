package com.meg.atable.service.impl;

import com.meg.atable.Application;
import com.meg.atable.api.model.ListLayoutType;
import com.meg.atable.api.model.ListType;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.data.entity.ItemEntity;
import com.meg.atable.data.entity.ShoppingListEntity;
import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.data.repository.ItemRepository;
import com.meg.atable.data.repository.ShoppingListRepository;
import com.meg.atable.service.ShoppingListService;
import com.meg.atable.service.TagService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class ShoppingListServiceImplTest {
    @Autowired
    private ShoppingListService shoppingListService;

    @Autowired
    private ShoppingListRepository shoppingListRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserService userService;


    @Autowired
    private TagService tagService;

    private static boolean setUpComplete = false;
    private static UserAccountEntity userAccount;
    private static UserAccountEntity noseyUser;
    private static TagEntity tag1;
    private static TagEntity tag2;
    private static TagEntity tag3;
    private static ItemEntity itemEntity;
    private static ShoppingListEntity baseList;
    private static ShoppingListEntity activeList;
    private static ShoppingListEntity toDelete;
    private static String noseyUserName;

    @Before
    public void setUp() {
        if (setUpComplete) {
            return;
        }
// make user
        String userName = "shoppingListTest";
        this.userAccount = userService.save(new UserAccountEntity(userName, "password"));
        this.noseyUserName = "noseyUser";
        this.noseyUser = userService.save(new UserAccountEntity(this.noseyUserName, "password"));

        // make tags
        tag1 = new TagEntity("tag1", "main1");
        tag2 = new TagEntity("tag1", "main1");
        tag3 = new TagEntity("tag1", "main1");

        tag1 = tagService.save(tag1);
        tag2 = tagService.save(tag2);
        tag3 = tagService.save(tag3);

        // make base list
        baseList = new ShoppingListEntity();
        baseList.setListType(ListType.BaseList);
        baseList.setCreatedOn(new Date());
        baseList.setUserId(userAccount.getId());
        baseList = shoppingListRepository.save(baseList);

        // make active list
        activeList = new ShoppingListEntity();
        activeList.setListType(ListType.ActiveList);
        activeList.setCreatedOn(new Date());
        activeList.setUserId(userAccount.getId());
        activeList = shoppingListRepository.save(activeList);
        itemEntity = new ItemEntity();
        itemEntity.setListCategory("All");
        itemEntity.setTag(tag1);
        itemEntity.setListId(activeList.getId());
        itemEntity = itemRepository.save(itemEntity);

        // make list to be deleted
        toDelete = new ShoppingListEntity();
        toDelete.setListType(ListType.ActiveList);
        toDelete.setCreatedOn(new Date());
        toDelete.setUserId(userAccount.getId());
        toDelete = shoppingListRepository.save(toDelete);

        setUpComplete = true;
    }

    @Test
    public void testGetListsByUsername() {
        List<ShoppingListEntity> results = shoppingListService.getListsByUsername(this.userAccount.getUsername());

        Assert.assertNotNull(results);
        Assert.assertTrue(results.size() == 3);
    }

    @Test
    public void testGetListByUsername() {
        ShoppingListEntity result = shoppingListService.getListById(this.userAccount.getUsername(),
                baseList.getId());

        Assert.assertNotNull(result);
        Assert.assertEquals(baseList.getCreatedOn(), result.getCreatedOn());
    }


    @Test
    public void testGetListByUsername_BadUser() {
        ShoppingListEntity result = shoppingListService.getListById(noseyUserName,
                baseList.getId());

        Assert.assertNull(result);
    }

    @Test
    public void testCreateList() {
        ShoppingListEntity shoppingListEntity = new ShoppingListEntity();
        shoppingListEntity.setListType(ListType.BaseList);
        shoppingListEntity.setListLayoutType(ListLayoutType.All);

        ShoppingListEntity result = shoppingListService.createList(userAccount.getUsername(), shoppingListEntity);

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getCreatedOn());
        Assert.assertNotNull(result.getListLayoutType());
        Assert.assertEquals(shoppingListEntity.getListType(), result.getListType());
        Assert.assertNotNull(result.getId());
    }

    @Test
    public void testDeleteList() {
        boolean result = shoppingListService.deleteList(userAccount.getUsername(), toDelete.getId());

        Assert.assertTrue(result);
    }

    @Test
    public void testAddItemToList() {
        // make item (unsaved)
ItemEntity itemEntity = new ItemEntity();
itemEntity.setListCategory("All");
itemEntity.setTag(tag1);

        // add to baseList
        shoppingListService.addItemToList(userAccount.getUsername(),baseList.getId(),itemEntity);

        // retrieve baselist
ShoppingListEntity result = shoppingListService.getListById(userAccount.getUsername(),baseList.getId());

        // ensure item is there
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getItems());
        Assert.assertTrue(result.getItems().size()>0);
        Assert.assertNotNull(result.getItems().get(0).getId());
    }

    @Test
    public void testDeleteItemFromList() {

        // delete from active list
        shoppingListService.deleteItemFromList(userAccount.getUsername(),activeList.getId(),itemEntity.getId());

        // retrieve active list
        ShoppingListEntity result = shoppingListService.getListById(userAccount.getUsername(),activeList.getId());

        // ensure item is NOT there
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getItems());
        Assert.assertTrue(result.getItems().size()==0);
    }
}