package com.meg.listshop.lmt.api.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

public class TagResource extends AbstractListShopResource implements ListShopModel {

    private Tag tag;

    public TagResource(Tag tag) {
        this.tag = tag;
    }

    public TagResource() {
    }

    public Tag getTag() {
        return tag;
    }

    @Override
    @JsonIgnore()
    public String getRootPath() {
        return "tag";
    }

    @Override
    @JsonIgnore()
    public String getResourceId() {
        return tag.getId();
    }
}