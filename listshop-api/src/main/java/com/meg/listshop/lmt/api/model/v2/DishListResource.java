package com.meg.listshop.lmt.api.model.v2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.meg.listshop.lmt.api.model.AbstractListShopResource;
import com.meg.listshop.lmt.api.model.ListShopModel;

import java.util.List;

public class DishListResource extends AbstractListShopResource implements ListShopModel {

    @JsonProperty("_embedded")
    private EmbeddedDishListResource embeddedList;

    public DishListResource(List<DishResource> dishRS) {
        this.embeddedList = new EmbeddedDishListResource(dishRS);
    }

    public DishListResource() {
        // empty constructor - because jackson needs it
    }

    public EmbeddedDishListResource getEmbeddedList() {
        return embeddedList;
    }

    public void setEmbeddedList(EmbeddedDishListResource embeddedList) {
        this.embeddedList = embeddedList;
    }

    @Override
    @JsonIgnore()
    public String getRootPath() {
        return "listlayout";
    }

    @Override
    @JsonIgnore()
    public String getResourceId() {
        return null;
    }
}
