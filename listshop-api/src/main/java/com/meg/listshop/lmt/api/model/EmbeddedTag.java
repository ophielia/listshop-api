package com.meg.listshop.lmt.api.model;


public class EmbeddedTag {

    private final Tag tag;

    public EmbeddedTag(Tag tag) {
        this.tag = tag;
    }

    public Tag getTag() {
        return tag;
    }
}