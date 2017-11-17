package com.meg.atable.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.List;

/**
 * Created by margaretmartin on 29/10/2017.
 */
public class ListLayoutCategory {

    @JsonProperty("category_id")
    private Long categoryId;

    private String name;

    private List<Tag> tags;

    @JsonProperty("layout_id")
    private Long layoutId;

    public ListLayoutCategory() {
        // empty constructor for jpa
    }

    public ListLayoutCategory(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public ListLayoutCategory name(String name) {
        this.name = name;
        return this;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public ListLayoutCategory tags(List<Tag> tags) {
        this.tags = tags;
        return this;
    }

    public Long getLayoutId() {
        return layoutId;
    }

    public ListLayoutCategory layoutId(Long layoutId) {
        this.layoutId = layoutId;
        return this;
    }
}
