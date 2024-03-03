package com.meg.listshop.lmt.api.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class EmbeddedCategoryMappingListResource {

    @JsonProperty("category_mapping_resource_list")
    private List<FoodCategoryMappingResource> mappingResourceList;

    public EmbeddedCategoryMappingListResource(List<FoodCategoryMappingResource> mappingResourceList) {
        this.mappingResourceList = mappingResourceList;
    }

    public EmbeddedCategoryMappingListResource() {
    }

    public List<FoodCategoryMappingResource> getMappingResourceList() {
        return mappingResourceList;
    }

    public void setMappingResourceList(List<FoodCategoryMappingResource> mappingResourceList) {
        this.mappingResourceList = mappingResourceList;
    }
}