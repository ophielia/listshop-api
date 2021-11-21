package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meg.listshop.lmt.service.categories.ListShopCategory;

import java.util.Date;
import java.util.List;

public class ShoppingList {

    private Long list_id;

    @JsonProperty("created")
    private Date createdOn;

    @JsonProperty("updated")
    private Date updated;

    @JsonProperty("layout_type")
    private String layoutType;

    @JsonProperty("layout_id")
    private String layoutId;

    @JsonProperty("item_count")
    private Integer itemCount;

    @JsonProperty("legend")
    private List<LegendSource> legendSources;

    private java.util.List<ListShopCategory>
            categories;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("is_starter_list")
    private Boolean isStarterList;

    @JsonProperty("name")
    private String name;

    public ShoppingList() {
        // empty constructor
    }


    public ShoppingList(Long id) {
        this.list_id = id;
    }

    public Long getList_id() {
        return list_id;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public ShoppingList createdOn(Date createdOn) {
        this.createdOn = createdOn;
        return this;
    }


    public java.util.List<ListShopCategory> getCategories() {
        return categories;
    }

    public ShoppingList categories(java.util.List<ListShopCategory> categories) {
        this.categories = categories;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public ShoppingList userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getLayoutType() {
        return layoutType;
    }

    public ShoppingList layoutType(String layoutType) {
        this.layoutType = layoutType;
        return this;
    }

    public Integer getItemCount() {
        return itemCount;
    }

    public ShoppingList itemCount(Integer itemCount) {
        this.itemCount = itemCount;
        return this;
    }


    public ShoppingList legendSources(List<LegendSource> legendSources) {
        this.legendSources = legendSources;
        return this;
    }

    public List<LegendSource> getLegendSources() {
        return legendSources;
    }

    public String getLayoutId() {
        return layoutId;
    }

    public ShoppingList layoutId(String layoutId) {
        this.layoutId = layoutId;
        return this;
    }

    public Date getUpdated() {
        return updated;
    }

    public ShoppingList updated(Date updated) {
        this.updated = updated;
        return this;
    }

    @JsonProperty("is_starter_list")
    public Boolean getStarterList() {
        return isStarterList;
    }

    public ShoppingList isStarterList(Boolean starterList) {
        isStarterList = starterList;
        return this;
    }

    public String getName() {
        return name;
    }

    public ShoppingList name(String name) {
        this.name = name;
        return this;
    }
}