package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class ShoppingListItem {

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

    @JsonProperty("source_keys")
    private List<String> sourceKeys;

    private Set<String> handles;

    public ShoppingListItem(Long id) {
        this.item_id = id;
    }

    public ShoppingListItem() {
        // necessary for json construction
    }

    @JsonIgnore
    public Long getId() {
        return item_id;
    }


    public Tag getTag() {
        return tag;
    }

    public ShoppingListItem tag(Tag tag) {
        this.tag = tag;
        return this;
    }


    public Date getAddedOn() {
        return addedOn;
    }

    public ShoppingListItem addedOn(Date addedOn) {
        this.addedOn = addedOn;
        return this;
    }


    public String getFreeText() {
        return freeText;
    }

    public ShoppingListItem freeText(String freeText) {
        this.freeText = freeText;
        return this;
    }

    public Date getCrossedOff() {
        return crossedOff;
    }

    public ShoppingListItem crossedOff(Date crossedOff) {
        this.crossedOff = crossedOff;
        return this;
    }


    public String getListId() {
        return listId;
    }


    public ShoppingListItem listId(String listId) {
        this.listId = listId;
        return this;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public ShoppingListItem tagId(String tagId) {
        this.tagId = tagId;
        return this;
    }

    public Integer getUsedCount() {
        return usedCount;
    }

    public ShoppingListItem usedCount(Integer usedCount) {
        this.usedCount = usedCount;
        return this;
    }

    public Date getRemoved() {
        return removed;
    }

    public ShoppingListItem removed(Date created) {
        this.removed = created;
        return this;
    }

    public Date getUpdated() {
        return updated;
    }

    public ShoppingListItem updated(Date updated) {
        this.updated = updated;
        return this;
    }


    public ShoppingListItem sourceKeys(List<String> sourceKeys) {
        this.sourceKeys = sourceKeys;
        return this;
    }

    public List<String> getSourceKeys() {
        return sourceKeys;
    }

    public Set<String> getHandles() {
        return handles;
    }

    public ShoppingListItem handles(Set<String> handles) {
        this.handles = handles;
        return this;
    }
}