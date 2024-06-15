package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Tag {

    private String tag_id;

    @JsonProperty("user_id")
    private String userId;
    private String name;

    private String description;

    @JsonProperty("tag_type")
    private String tagType;

    private String ratingFamily;

    @JsonProperty("is_group")
    private boolean isGroup;
    private Boolean assignSelect;

    private Boolean searchSelect;

    private List<Dish> dishes;

    private Double power;

    private String parentId;

    private Boolean toDelete;

    private Boolean isLiquid;

    Tag() {
    }

    public Tag(String name) {
        this.name = name;
    }

    public Tag(Long id) {
        this.tag_id = String.valueOf(id);
    }

    Tag(Long id, String name, String description, TagType tagType, String ratingFamily) {
        this.tag_id = String.valueOf(id);
        this.name = name;
        this.description = description;
        this.tagType = tagType != null ? tagType.name() : TagType.TagType.name();
        this.ratingFamily = ratingFamily;
    }

    @JsonProperty("tag_id")
    public String getId() {
        return tag_id;
    }

    public void setTag_id(String tag_id) {
        this.tag_id = tag_id;
    }

    public String getName() {
        return name;
    }

    public Tag name(String name) {
        this.name = name;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public Tag userId(String userId) {
        this.userId = userId;
        return this;
    }

    public boolean getIsGroup() {
        return isGroup;
    }


    public Tag isGroup(boolean isGroup) {
        this.isGroup = isGroup;
        return this;
    }

    @JsonProperty("is_liquid")
    public Boolean getIsLiquid() {
        return isLiquid;
    }


    public Tag isLiquid(Boolean isLiquid) {
        this.isLiquid = isLiquid;
        return this;
    }



    public String getDescription() {
        return description;
    }

    public Tag description(String description) {
        this.description = description;
        return this;
    }

    @JsonProperty("tag_type")
    public String getTagType() {
        return tagType;
    }

    public Tag tagType(String tagType) {
        this.tagType = tagType;
        return this;
    }

    @JsonProperty("rating_family")
    public String getRatingFamily() {
        return ratingFamily;
    }

    public Tag ratingFamily(String ratingFamily) {
        this.ratingFamily = ratingFamily;
        return this;
    }

    @JsonProperty("assign_select")
    public Boolean getAssignSelect() {
        return assignSelect;
    }

    public Tag assignSelect(Boolean assignSelect) {
        this.assignSelect = assignSelect;
        return this;
    }

    @JsonProperty("parent_id")
    public String getParentId() {
        return parentId;
    }

    public Tag parentId(String parent_id) {
        if (parent_id != null && !parent_id.equalsIgnoreCase("null")) {
            this.parentId = parent_id;
        }
        return this;
    }

    @JsonProperty("search_select")
    public Boolean getSearchSelect() {
        return searchSelect;
    }

    public Tag searchSelect(Boolean searchSelect) {
        this.searchSelect = searchSelect;
        return this;
    }

    public Double getPower() {
        return power;
    }

    public Tag power(Double power) {
        this.power = power;
        return this;
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public Tag dishes(List<Dish> dishes) {
        this.dishes = dishes;
        return this;
    }

    @JsonProperty("to_delete")
    public Boolean getToDelete() {
        return toDelete;
    }

    public Tag toDelete(Boolean toDelete) {
        this.toDelete = toDelete;
        return this;
    }


}