package com.meg.listshop.lmt.api.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class EmbeddedStringList {

    @JsonIgnore
    private List<String> strings;

    public EmbeddedStringList(List<String> stringList) {
        this.strings = stringList;
    }

    public EmbeddedStringList() {
    }

    public List<String> getList() {
        return strings;
    }

    public void setStringList(List<String> stringList) {
        this.strings = stringList;
    }
}