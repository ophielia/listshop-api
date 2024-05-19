package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class UnitDisplayListResource extends AbstractListShopResource implements ListShopModel {

    @JsonProperty("_embedded")
    private EmbeddedUnitListResource embeddedList;

    public UnitDisplayListResource(List<UnitDisplayResource> unitDisplayResources) {
        this.embeddedList = new EmbeddedUnitListResource(unitDisplayResources);
    }

    public UnitDisplayListResource() {
    }

    public EmbeddedUnitListResource getEmbeddedList() {
        return embeddedList;
    }

    public void setEmbeddedList(EmbeddedUnitListResource embeddedList) {
        this.embeddedList = embeddedList;
    }

    @Override
    @JsonIgnore()
    public String getRootPath() {
        return "unit";
    }

    @Override
    @JsonIgnore()
    public String getResourceId() {
        return null;
    }
}
