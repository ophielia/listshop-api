package com.meg.listshop.lmt.api.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class EmbeddedListCategoryListResource {

    @JsonProperty("list_category_list")
    private List<CategoryResource> categoryResourceList;

    public EmbeddedListCategoryListResource(List<CategoryResource>  listLayoutResourceList) {
        this.categoryResourceList = listLayoutResourceList;
    }

    public EmbeddedListCategoryListResource() {
        // empty constructor for Jackson
    }

    public List<CategoryResource> getCategoryResourceList() {
        return categoryResourceList;
    }

    public void setCategoryResourceList(List<CategoryResource> categoryResourceList) {
        this.categoryResourceList = categoryResourceList;
    }
}