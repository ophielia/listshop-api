package com.meg.listshop.lmt.api.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class EmbeddedFoodCategoryListResource {

    @JsonProperty("category_resource_list")
    private List<FoodCategoryResource> mappingResourceList;

    public EmbeddedFoodCategoryListResource(List<FoodCategoryResource> mappingResourceList) {
        this.mappingResourceList = mappingResourceList;
    }

    public EmbeddedFoodCategoryListResource() {
    }

    public List<FoodCategoryResource> getMappingResourceList() {
        return mappingResourceList;
    }

    public void setMappingResourceList(List<FoodCategoryResource> mappingResourceList) {
        this.mappingResourceList = mappingResourceList;
    }
}