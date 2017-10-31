package com.meg.atable.service.impl;

import com.meg.atable.api.model.ItemSourceType;
import com.meg.atable.api.model.ListLayoutType;
import com.meg.atable.api.model.ListType;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.data.entity.*;
import com.meg.atable.data.repository.ItemRepository;
import com.meg.atable.data.repository.ShoppingListRepository;
import com.meg.atable.service.ShoppingListService;
import jdk.nashorn.internal.runtime.regexp.joni.SearchAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
public class ShoppingListServiceImpl implements ShoppingListService {
@Autowired
    UserService userService;

    @Autowired
    ShoppingListRepository shoppingListRepository;

    @Autowired
    ItemRepository itemRepository;

    @Override
    public List<ShoppingListEntity> getListsByUsername(String userName) {
        UserAccountEntity user = userService.getUserByUserName(userName);

        return shoppingListRepository.findByUserId(user.getId());
    }

    @Override
    public ShoppingListEntity createList(String userName, ShoppingListEntity shoppingList) {
        UserAccountEntity user = userService.getUserByUserName(userName);
        shoppingList.setCreatedOn(new Date());
        shoppingList.setUserId(user.getId());
        return shoppingListRepository.save(shoppingList);
    }

    @Override
    public ShoppingListEntity getListByUsernameAndType(String userName, ListType listType) {
        // MM implement this
        return null;
    }

    @Override
    public ShoppingListEntity getListById(String userName, Long listId) {
        UserAccountEntity user = userService.getUserByUserName(userName);
        ShoppingListEntity shoppingListEntity = shoppingListRepository.findOne(listId);
        if (shoppingListEntity != null && shoppingListEntity.getUserId() == user.getId()) {
            return shoppingListEntity;
        }
        return null;
    }

    @Override
    public boolean deleteList(String userName, Long listId) {
        ShoppingListEntity toDelete = getListById(userName,listId);
        if (toDelete != null) {
            shoppingListRepository.delete(toDelete);
            return true;
        }
        return false;
    }

    @Override
    public void addItemToList(String name, Long listId, ItemEntity itemEntity) {
        ShoppingListEntity shoppingListEntity = getListById(name,listId);
        if (shoppingListEntity == null) {
            return;
        }
        // prepare item
        itemEntity.setAddedOn(new Date());
        itemEntity.setItemSource(ItemSourceType.Manual);
        itemEntity.setListId(listId);
        String listCategory = getCategoryForItem(itemEntity,shoppingListEntity.getListLayoutType());
        itemEntity.setListCategory(listCategory);
        itemEntity = itemRepository.save(itemEntity);
        // add to shoppingListEntity
        List<ItemEntity> items = shoppingListEntity.getItems();
        items.add(itemEntity);
        // save shoppingListEntity (also saving items)
        shoppingListRepository.save(shoppingListEntity);
    }



    @Override
    public void deleteItemFromList(String name, Long listId, Long itemId) {
        ShoppingListEntity shoppingListEntity = getListById(name,listId);
        if (shoppingListEntity == null) {
            return;
        }
        // get item to be deleted
        ItemEntity itemEntity = itemRepository.findOne(itemId);

        // get items for shopping list
        List<ItemEntity> listItems = itemRepository.findByListId(listId);

        // filter items removing item to be deleted
        List<ItemEntity> filteredItems = listItems.stream()
                .filter(i -> i.getId() != itemId)
                .collect(Collectors.toList());

        // delete item
        itemRepository.delete(itemId);

        // set filtered items in shopping list
        shoppingListEntity.setItems(filteredItems);

        // save shopping list
        shoppingListRepository.save(shoppingListEntity);
    }

    private String getCategoryForItem(ItemEntity itemEntity, ListLayoutType listLayoutType) {
        // MM needs real implementation with layouts
        return "ALL";
    }
}
