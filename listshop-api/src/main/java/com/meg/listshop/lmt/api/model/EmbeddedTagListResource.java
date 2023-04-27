package com.meg.listshop.lmt.api.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class EmbeddedTagListResource {

    @JsonProperty("tagResourceList")
    private List<TagResource> tagResourceList;

    public EmbeddedTagListResource(List<TagResource> tagResourceList) {
        this.tagResourceList = tagResourceList;
    }

    public EmbeddedTagListResource() {
    }

    public List<TagResource> getTagResourceList() {
        return tagResourceList;
    }

    public void setTagResourceList(List<TagResource> tagResourceList) {
        this.tagResourceList = tagResourceList;
    }
}