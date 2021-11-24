package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ShoppingListListResource extends AbstractListShopResource implements ListShopModel {

    @JsonProperty("_embedded")
    private EmbeddedShoppingListListResource embeddedList;

    public ShoppingListListResource(List<ShoppingList> listLayoutResourceList) {
        this.embeddedList = new EmbeddedShoppingListListResource(listLayoutResourceList);
    }

    public EmbeddedShoppingListListResource getEmbeddedList() {
        return embeddedList;
    }

    public void setEmbeddedList(EmbeddedShoppingListListResource embeddedList) {
        this.embeddedList = embeddedList;
    }

    @Override
    @JsonIgnore()
    public String getRootPath() {
        return "shoppinglist";
    }

    @Override
    @JsonIgnore()
    public String getResourceId() {
        return null;
    }
}
