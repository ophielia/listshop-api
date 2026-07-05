/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.api.model.v2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.meg.listshop.lmt.api.model.Tag;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class MergeItem implements Comparable {

    @JsonProperty("item_id")
    private Long item_id;

    @JsonProperty("added")
    private Date addedOn;

    @JsonProperty("removed")
    private Date removed;

    @JsonProperty("updated")
    private Date updated;

    @JsonProperty("crossed_off")
    private Date crossedOff;

    @JsonProperty("list_id")
    private String listId;

    @JsonProperty("tag_id")
    private String tagId;

    @JsonProperty("used_count")
    private Integer usedCount;
    public MergeItem(Long id) {
        this.item_id = id;
    }

    public MergeItem() {
        // necessary for json construction
    }

    @JsonIgnore
    public Long getId() {
        return item_id;
    }

    public Date getAddedOn() {
        return addedOn;
    }

    public MergeItem addedOn(Date addedOn) {
        this.addedOn = addedOn;
        return this;
    }

    public Date getCrossedOff() {
        return crossedOff;
    }

    public MergeItem crossedOff(Date crossedOff) {
        this.crossedOff = crossedOff;
        return this;
    }

    public String getListId() {
        return listId;
    }

    public MergeItem listId(String listId) {
        this.listId = listId;
        return this;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public MergeItem tagId(String tagId) {
        this.tagId = tagId;
        return this;
    }

    public Integer getUsedCount() {
        return usedCount;
    }

    public MergeItem usedCount(Integer usedCount) {
        this.usedCount = usedCount;
        return this;
    }

    public Date getRemoved() {
        return removed;
    }

    public MergeItem removed(Date created) {
        this.removed = created;
        return this;
    }

    public Date getUpdated() {
        return updated;
    }

    public MergeItem updated(Date updated) {
        this.updated = updated;
        return this;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
