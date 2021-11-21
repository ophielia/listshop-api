package com.meg.listshop.lmt.service.categories;

import java.util.List;

/**
 * Created by margaretmartin on 29/10/2017.
 */

/**
 * Part of display of ShoppingList
 */
public interface ListShopCategory {


    String getName();

    ListShopCategory name(String name);

    Long getId();

    List<ListShopCategory> getSubCategories();

    ListShopCategory subCategories(List<ListShopCategory> subCategories);

    int getDisplayOrder();

    ListShopCategory displayOrder(Integer displayOrder);

    void addSubCategory(ListShopCategory subcategory);

    boolean isEmpty();


}
