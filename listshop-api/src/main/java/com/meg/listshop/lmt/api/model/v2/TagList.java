package com.meg.listshop.lmt.api.model.v2;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meg.listshop.lmt.api.model.TagResource;

import java.util.List;

public class TagList {

    @JsonProperty("tag_list")
    private List<TagResource> tagList;

    public TagList(List<TagResource> tagList) {
        this.tagList = tagList;
    }

    public TagList() {
    }


}
