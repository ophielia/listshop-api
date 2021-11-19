package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.meg.listshop.lmt.data.entity.TagEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by margaretmartin on 29/10/2017.
 */
public class ListLayoutCategory extends AbstractCategory {

    @JsonProperty("layout_id")
    private Long layoutId;
    @JsonIgnore
    private List<TagEntity> tagEntities = new ArrayList<>();
    @JsonProperty("tags")
    private List<Tag> tags = new ArrayList<>();

    public ListLayoutCategory() {
        // empty constructor for jpa
    }

    public ListLayoutCategory(Long id) {
        super(id);
    }


    @Override
    public boolean isEmpty() {
        return getSubCategories().isEmpty() &&
                getTags().isEmpty() &&
                getTagEntities().isEmpty();
    }


    public List<Tag> getTags() {
        return tags;
    }

    public Category tags(List<Tag> tags) {
        this.tags = tags;
        return this;
    }

    public Long getLayoutId() {
        return layoutId;
    }

    public Category layoutId(Long layoutId) {
        this.layoutId = layoutId;
        return this;
    }

    @JsonIgnore
    public List<TagEntity> getTagEntities() {
        return tagEntities;
    }

    @JsonIgnore
    public Category tagEntities(List<TagEntity> tagEntities) {
        this.tagEntities = tagEntities;
        return this;
    }
}
