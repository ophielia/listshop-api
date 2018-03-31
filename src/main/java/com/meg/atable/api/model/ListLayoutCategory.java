package com.meg.atable.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.meg.atable.data.entity.TagEntity;

import java.util.List;

/**
 * Created by margaretmartin on 29/10/2017.
 */
public class ListLayoutCategory extends AbstractCategory {

    @JsonProperty("layout_id")
    private Long layoutId;
    @JsonIgnore
    private List<TagEntity> tagEntities;
    @JsonProperty("tags")
    private List<Tag> tags;

    public ListLayoutCategory() {
        // empty constructor for jpa
    }

    public ListLayoutCategory(Long id) {
        super(id);
    }


    @Override
    public boolean isEmpty() {
        if (getSubCategories().isEmpty() &&
                getTags().isEmpty() &&
                getTagEntities().isEmpty()) {
            return true;
        }
        return false;
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

    public List<TagEntity> getTagEntities() {
        return tagEntities;
    }

    public Category tagEntities(List<TagEntity> tagEntities) {
        this.tagEntities = tagEntities;
        return this;
    }
}
