package com.meg.listshop.lmt.api.model;


import com.fasterxml.jackson.annotation.JsonProperty;


public class TargetResource extends AbstractListShopResource implements ListShopModel {

    @JsonProperty("target")
    private final Target target;

    public TargetResource(Target target) {
        this.target = target;
    }

    public Target getTarget() {
        return target;
    }

    @Override
    public String getRootPath() {
        return "target";
    }

    @Override
    public String getResourceId() {
        return null;
    }
}