package com.meg.listshop.lmt.api.model;


import java.util.List;

public class EmbeddedListLayoutListResource {

    private List<ListLayoutResource> listLayoutResourceList;

    public EmbeddedListLayoutListResource(List<ListLayoutResource> listLayoutResourceList) {
        this.listLayoutResourceList = listLayoutResourceList;
    }


    public List<ListLayoutResource> getTagResourceList() {
        return listLayoutResourceList;
    }

    public void setTagResourceList(List<ListLayoutResource> listLayoutResourceList) {
        this.listLayoutResourceList = listLayoutResourceList;
    }


}