package com.meg.atable.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Tag {

    private String tag_id;

    private String name;

    private String description;

    Tag() {
    }

    public Tag(String name) {
        this.name = name;
    }

    public Tag(Long id) {
        this.tag_id = String.valueOf(id);
    }

    Tag(Long id, String name, String description) {
        this.tag_id = String.valueOf(id);
        this.name = name;
        this.description = description;
    }

    @JsonProperty("tag_id")
    public String getId() {
        return tag_id;
    }

    public String getName() {
        return name;
    }

    public Tag name(String name) {
        this.name = name;
        return this;
    }


    public String getDescription() {
        return description;
    }

    public Tag description(String description) {
        this.description = description;
        return this;
    }

}