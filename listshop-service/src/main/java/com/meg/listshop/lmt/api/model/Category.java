package com.meg.listshop.lmt.api.model;

import java.util.List;

/**
 * Created by margaretmartin on 29/10/2017.
 */

/**
 * Part of display of ShoppingList
 */
public interface Category {


    String getName();

    Category name(String name);

    Long getId();

    List<Category> getSubCategories();

    Category subCategories(List<Category> subCategories);

    int getDisplayOrder();

    Category displayOrder(Integer displayOrder);

    void addSubCategory(Category subcategory);

    boolean isEmpty();


}
