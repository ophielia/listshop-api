package com.meg.listshop.lmt.api.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class EmbeddedLayoutCategoryListResource {

    @JsonProperty("layout_category_resource_list")
    private List<LayoutCategoryResource> mappingResourceList;

    public EmbeddedLayoutCategoryListResource(List<LayoutCategoryResource> mappingResourceList) {
        this.mappingResourceList = mappingResourceList;
    }

    public EmbeddedLayoutCategoryListResource() {
    }

    public List<LayoutCategoryResource> getMappingResourceList() {
        return mappingResourceList;
    }

    public void setMappingResourceList(List<LayoutCategoryResource> mappingResourceList) {
        this.mappingResourceList = mappingResourceList;
    }
}
