package com.meg.atable.api.model;

import java.util.ArrayList;
import java.util.List;

public class Tag {

    private Long tag_id;

    private String name;

    private String description;

    private List<Dish> dishes = new ArrayList<>();

    public Tag() {
    }

    public Tag(String name) {
        this.name = name;
    }

    public Tag(Long id) {
        this.tag_id = id;
    }

    public Tag(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Long getId() {
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