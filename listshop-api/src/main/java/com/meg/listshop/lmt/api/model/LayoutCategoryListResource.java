package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class LayoutCategoryListResource extends AbstractListShopResource implements ListShopModel {

    @JsonProperty("_embedded")
    private EmbeddedLayoutCategoryListResource embeddedList;

    public LayoutCategoryListResource(List<LayoutCategoryResource> layoutCategoryResourceList) {
        this.embeddedList = new EmbeddedLayoutCategoryListResource(layoutCategoryResourceList);
    }

    public LayoutCategoryListResource() {
    }

    public EmbeddedLayoutCategoryListResource getEmbeddedList() {
        return embeddedList;
    }

    public void setEmbeddedList(EmbeddedLayoutCategoryListResource embeddedList) {
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
