package com.meg.listshop.lmt.api.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ListLayoutResource extends AbstractListShopResource implements ListShopModel {

    @JsonProperty("list_layout")
    private  ListLayout listLayout;

    public ListLayoutResource(ListLayout listLayout) {
        this.listLayout = listLayout;
    }

    public ListLayoutResource() {
        // empty constructor for jackson
    }

    public ListLayout getListLayout() {
        return listLayout;
    }

    @Override
    @JsonIgnore()
    public String getRootPath() {
        return "listlayout";
    }

    @Override
    @JsonIgnore()
    public String getResourceId() {
        return listLayout.getLayoutId().toString();
    }
}