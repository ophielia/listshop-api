package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TagListResource extends AbstractListShopResource implements ListShopModel {

    @JsonProperty("_embedded")
    private EmbeddedTagListResource embeddedList;

    public TagListResource(List<Tag> tagList) {
        this.embeddedList = new EmbeddedTagListResource(tagList);
    }

    public EmbeddedTagListResource getEmbeddedList() {
        return embeddedList;
    }

    public void setEmbeddedList(EmbeddedTagListResource embeddedList) {
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
