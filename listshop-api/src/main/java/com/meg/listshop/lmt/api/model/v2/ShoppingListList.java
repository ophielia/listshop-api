package com.meg.listshop.lmt.api.model.v2;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

public class ShoppingListList {

    @JsonProperty("list_of_lists")
    private List<NestedShoppingList> lists;

    public ShoppingListList() {
    }

    public ShoppingListList(List<NestedShoppingList> lists) {
        this.lists = lists;
    }

    public List<NestedShoppingList> getLists() {
        return lists;
    }

    @Override
    public String toString() {
        return "ShoppingListList{" +
                "lists=" + lists +
                '}';
    }
}
