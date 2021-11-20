package com.meg.listshop.lmt.api.model;


import java.util.List;

public class EmbeddedTagListResource {

    private List<Tag> tagResourceList;

    public EmbeddedTagListResource(List<Tag> tagResourceList) {
        this.tagResourceList = tagResourceList;
    }


    public List<Tag> getTagResourceList() {
        return tagResourceList;
    }

    public void setTagResourceList(List<Tag> tagResourceList) {
        this.tagResourceList = tagResourceList;
    }


}