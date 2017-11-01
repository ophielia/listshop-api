package com.meg.atable.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by margaretmartin on 29/10/2017.
 */
public class Item implements Comparable {

    @JsonProperty("item_id")
    private Long item_id;

    private Tag tag;

    @JsonProperty("item_source")
    private String itemSource;

    @JsonProperty("added")
    private Date addedOn;

    @JsonProperty("free_text")
    private String freeText;

    @JsonProperty("crossed_off")
    private Date crossedOff;

    @JsonProperty("list_category")
    private String listCategory;

    @JsonProperty("list_id")
    private String listId;

    @JsonProperty("tag_id")
    private String tagId;

    @JsonProperty("used_count")
    private Integer usedCount;

    public Item(Long id) {
        this.item_id = id;
    }

    public Item() {
        // necessary for json construction
    }

    @JsonIgnore
    public Long getId() {
        return item_id;
    }


    public Tag getTag() {
        return tag;
    }

    public Item tag(Tag tag) {
        this.tag = tag;
        return this;
    }

    public String getItemSource() {
        return itemSource;
    }

    public Item itemSource(String itemSource) {
        this.itemSource = itemSource;
        return this;
    }

    public Date getAddedOn() {
        return addedOn;
    }

    public Item addedOn(Date addedOn) {
        this.addedOn = addedOn;
        return this;
    }


    public String getFreeText() {
        return freeText;
    }

    public Item freeText(String freeText) {
        this.freeText = freeText;
        return this;
    }

    public Date getCrossedOff() {
        return crossedOff;
    }

    public Item crossedOff(Date crossedOff) {
        this.crossedOff = crossedOff;
        return this;
    }

    public String getListCategory() {
        return listCategory;
    }

    public Item listCategory(String listCategory) {
        this.listCategory = listCategory;
        return this;
    }

    public String getListId() {
        return listId;
    }


    public Item listId(String listId) {
        this.listId = listId;
        return this;
    }

    public String getTagId() {
        return tagId;
    }

    public Item tagId(String tagId) {
        this.tagId = tagId;
        return this;
    }

    public Integer getUsedCount() {
        return usedCount;
    }

    public Item usedCount(Integer usedCount) {
        this.usedCount = usedCount;
        return this;
    }

    @Override
    public int compareTo(Object o) {
        String name = this.tag != null ? this.tag.getName() : freeText;
        String comparename = ((Item) o).tag != null ? ((Item) o).getTag().getName() : ((Item) o).getFreeText();
        if (name == null) {
            name = "";
        }
        if (comparename == null) {
            comparename = "";
        }
        return name.toLowerCase().compareTo(comparename.toLowerCase());
    }
}
