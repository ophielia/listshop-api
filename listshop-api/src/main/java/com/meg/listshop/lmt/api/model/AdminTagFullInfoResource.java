package com.meg.listshop.lmt.api.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

public class AdminTagFullInfoResource extends AbstractListShopResource implements ListShopModel {

    private AdminTagFullInfo tag;

    public AdminTagFullInfoResource(AdminTagFullInfo tag) {
        this.tag = tag;
    }

    public AdminTagFullInfoResource() {
    }

    public AdminTagFullInfo getTag() {
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
        return tag.getTag_id();
    }
}