package com.meg.listshop.lmt.api.model;


import java.util.List;

public class EmbeddedTagListResource {

    private List<NewTagResource> tagResourceList;

    public EmbeddedTagListResource(List<NewTagResource> tagResourceList) {
        this.tagResourceList = tagResourceList;
    }


    public List<NewTagResource> getTagResourceList() {
        return tagResourceList;
    }

    public void setTagResourceList(List<NewTagResource> tagResourceList) {
        this.tagResourceList = tagResourceList;
    }


}