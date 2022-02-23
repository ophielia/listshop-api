package com.meg.listshop.lmt.api.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class EmbeddedTargetListResource {

    @JsonProperty("target_resource_list")
    private List<TargetResource> targetResourceList;

    public EmbeddedTargetListResource(List<TargetResource> listLayoutResourceList) {
        this.targetResourceList = listLayoutResourceList;
    }

    public List<TargetResource> getTargetResourceList() {
        return targetResourceList;
    }

    public void setTargetResourceList(List<TargetResource> listLayoutResourceList) {
        this.targetResourceList = listLayoutResourceList;
    }
}