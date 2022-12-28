package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CategoryListResource extends AbstractListShopResource implements ListShopModel {

    @JsonProperty("_embedded")
    private EmbeddedListCategoryListResource embeddedList;

    public CategoryListResource(List<CategoryResource> categoryResources) {
        this.embeddedList = new EmbeddedListCategoryListResource(categoryResources);
    }

    public CategoryListResource() {
        // empty constructor for jackson
    }

    public EmbeddedListCategoryListResource getEmbeddedList() {
        return embeddedList;
    }

    public void setEmbeddedList(EmbeddedListCategoryListResource embeddedList) {
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
