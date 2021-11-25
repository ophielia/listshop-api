package com.meg.listshop.lmt.api.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class EmbeddedListLayoutListResource {

    @JsonProperty("listLayoutResourceList")
    private List<ListLayoutResource> listLayoutResourceList;

    public EmbeddedListLayoutListResource(List<ListLayoutResource> listLayoutResourceList) {
        this.listLayoutResourceList = listLayoutResourceList;
    }

    public List<ListLayoutResource> getListLayoutResourceList() {
        return listLayoutResourceList;
    }

    public void setListLayoutResourceList(List<ListLayoutResource> listLayoutResourceList) {
        this.listLayoutResourceList = listLayoutResourceList;
    }
}