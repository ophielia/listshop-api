package com.meg.listshop.lmt.api.model.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShoppingListItemDetails {

    @JsonProperty("detail_id")
    private String itemDetailId;

    @JsonProperty("dish_id")
    private String dishId;

    @JsonProperty("list_id")
    private String listId;

    private Amount amount;

    public ShoppingListItemDetails() {
        // necessary for json construction
    }

    public ShoppingListItemDetails withItemDetailId(String itemDetailId) {
        this.itemDetailId = itemDetailId;
        return this;
    }

    public ShoppingListItemDetails withDishId(String dishId) {
        this.dishId = dishId;
        return this;
    }

    public ShoppingListItemDetails withListId(String listId) {
        this.listId = listId;
        return this;
    }

    public ShoppingListItemDetails withAmount(Amount amount) {
        this.amount = amount;
        return this;
    }

    public String getItemDetailId() {
        return itemDetailId;
    }

    public String getDishId() {
        return dishId;
    }

    public String getListId() {
        return listId;
    }

    public Amount getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "ShoppingListItemDetails{" +
                "itemDetailId='" + itemDetailId + '\'' +
                ", dishId='" + dishId + '\'' +
                ", listId='" + listId + '\'' +
                ", amount=" + amount +
                '}';
    }
}
