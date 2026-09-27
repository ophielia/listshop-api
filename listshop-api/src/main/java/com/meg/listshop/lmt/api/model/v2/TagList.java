/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.api.model.v2;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TagList {

    @JsonProperty("tag_list")
    private List<Tag> tagList;

    public TagList(List<Tag> tagList) {
        this.tagList = tagList;
    }

    public TagList() {
    }

    public List<Tag> getTagList() {
        return tagList;
    }
}
