package com.meg.listshop.lmt.api.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class EmbeddedUnitListResource {

    @JsonProperty("unitDisplayResourceList")
    private List<UnitDisplayResource> unitDisplayResources;

    public EmbeddedUnitListResource(List<UnitDisplayResource> tagResourceList) {
        this.unitDisplayResources = tagResourceList;
    }

    public EmbeddedUnitListResource() {
    }

    public List<UnitDisplayResource> getUnitDisplayResourceList() {
        return unitDisplayResources;
    }

    public void setUnitDisplayResourceList(List<UnitDisplayResource> tagResourceList) {
        this.unitDisplayResources = tagResourceList;
    }
}