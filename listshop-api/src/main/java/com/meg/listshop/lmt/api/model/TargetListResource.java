package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TargetListResource extends AbstractListShopResource implements ListShopModel {

    @JsonProperty("_embedded")
    private EmbeddedTargetListResource embeddedList;

    public TargetListResource(List<TargetResource> newTargetResources) {
        this.embeddedList = new EmbeddedTargetListResource(newTargetResources);
    }

    public EmbeddedTargetListResource getEmbeddedList() {
        return embeddedList;
    }

    public void setEmbeddedList(EmbeddedTargetListResource embeddedList) {
        this.embeddedList = embeddedList;
    }

    @Override
    @JsonIgnore()
    public String getRootPath() {
        return "targetList";
    }

    @Override
    @JsonIgnore()
    public String getResourceId() {
        return null;
    }
}
