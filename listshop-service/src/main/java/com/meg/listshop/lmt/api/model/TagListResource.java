package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TagListResource {

    @JsonProperty("_embedded")
    private EmbeddedTagListResource embeddedList;

    public TagListResource(List<NewTagResource> tagList) {
        this.embeddedList = new EmbeddedTagListResource(tagList);
    }

    public EmbeddedTagListResource getEmbeddedList() {
        return embeddedList;
    }

    public void setEmbeddedList(EmbeddedTagListResource embeddedList) {
        this.embeddedList = embeddedList;
    }
}
