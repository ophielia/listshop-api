package com.meg.listshop.lmt.api.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

public class ListLayoutResource extends AbstractListShopResource implements ListShopModel {

    private final ListLayout listLayout;

    public ListLayoutResource(ListLayout listLayout) {
        this.listLayout = listLayout;
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