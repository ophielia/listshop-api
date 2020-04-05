package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by margaretmartin on 29/10/2017.
 */
public class Item implements Comparable {

    @JsonProperty("item_id")
    private Long item_id;

    private Tag tag;

    @JsonProperty("added")
    private Date addedOn;

    @JsonProperty("removed")
    private Date removed;

    @JsonProperty("updated")
    private Date updated;

    @JsonProperty("free_text")
    private String freeText;

    @JsonProperty("crossed_off")
    private Date crossedOff;

    @JsonProperty("list_id")
    private String listId;

    @JsonProperty("tag_id")
    private String tagId;

    @JsonProperty("used_count")
    private Integer usedCount;

    @JsonProperty("dish_sources")
    private List<ItemSource> dishSources;

    @JsonProperty("list_sources")
    private List<ItemSource> listSources;

    private Set<String> handles;

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

    public void setTagId(String tagId) {
        this.tagId = tagId;
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

    public Date getRemoved() {
        return removed;
    }

    public Item removed(Date created) {
        this.removed = created;
        return this;
    }

    public Date getUpdated() {
        return updated;
    }

    public Item updated(Date updated) {
        this.updated = updated;
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

    public void setDishSources(List<ItemSource> dishSources) {
        this.dishSources = dishSources;
    }

    public List<ItemSource> getDishSources() {
        return dishSources;
    }

    public void setListSources(List<ItemSource> listSources) {
        this.listSources = listSources;
    }

    public List<ItemSource> getListSources() {
        return listSources;
    }

    public Set<String> getHandles() {
        return handles;
    }

    public Item handles(Set<String> handles) {
        this.handles = handles;
        return this;
    }
}
