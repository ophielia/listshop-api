package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CategoryMappingListResource extends AbstractListShopResource implements ListShopModel {

    @JsonProperty("_embedded")
    private EmbeddedCategoryMappingListResource embeddedList;

    public CategoryMappingListResource(List<FoodCategoryMappingResource> foodResourceList) {
        this.embeddedList = new EmbeddedCategoryMappingListResource(foodResourceList);
    }

    public CategoryMappingListResource() {
    }

    public EmbeddedCategoryMappingListResource getEmbeddedList() {
        return embeddedList;
    }

    public void setEmbeddedList(EmbeddedCategoryMappingListResource embeddedList) {
        this.embeddedList = embeddedList;
    }

    @Override
    @JsonIgnore()
    public String getRootPath() {
        return "food";
    }

    @Override
    @JsonIgnore()
    public String getResourceId() {
        return null;
    }
}
