package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ListLayoutListResource extends AbstractListShopResource implements ListShopModel {

    @JsonProperty("_embedded")
    private EmbeddedListLayoutListResource embeddedList;

    public ListLayoutListResource(List<ListLayoutResource> listLayoutResourceList) {
        this.embeddedList = new EmbeddedListLayoutListResource(listLayoutResourceList);
    }

    public EmbeddedListLayoutListResource getEmbeddedList() {
        return embeddedList;
    }

    public void setEmbeddedList(EmbeddedListLayoutListResource embeddedList) {
        this.embeddedList = embeddedList;
    }

    @Override
    @JsonIgnore()
    public String getRootPath() {
        return "tag";
    }

    @Override
    @JsonIgnore()
    public String getResourceId() {
        return null;
    }
}
