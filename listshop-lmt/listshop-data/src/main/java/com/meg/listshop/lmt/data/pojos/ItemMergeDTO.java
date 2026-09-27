/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.data.pojos;

import com.meg.listshop.lmt.api.model.v2.MergeItem;

import java.util.Date;
import java.util.Objects;


public class ItemMergeDTO {

    private Date addedOn;
    private Date crossedOff;
    private Date removed;
    private Date updated;
    private Date lastChanged;
    private Integer usedCount;
    private String itemId;
    private String listId;
    private String tagId;

    public ItemMergeDTO(MergeItem model) {
        this.addedOn = model.getAddedOn();
        this.crossedOff = model.getCrossedOff();
        this.removed = model.getRemoved();
        this.updated = model.getUpdated();
        this.lastChanged = model.getLastChanged();
        this.usedCount = model.getUsedCount();
        this.itemId = model.getId();
        this.listId = model.getListId();
        this.tagId = model.getTagId();
    }

    public Date getAddedOn() {
        return addedOn;
    }

    public Date getCrossedOff() {
        return crossedOff;
    }

    public Date getRemoved() {
        return removed;
    }

    public Date getUpdated() {
        return updated;
    }

    public Integer getUsedCount() {
        return usedCount;
    }

    public String getItemId() {
        return itemId;
    }

    public String getListId() {
        return listId;
    }

    public String getTagId() {
        return tagId;
    }

    public Date getLastChanged() {
        return lastChanged;
    }

    public void setLastChanged(Date lastChanged) {
        this.lastChanged = lastChanged;
    }

    @Override
    public String toString() {
        return "MergeItemDTO{" +
                "addedOn=" + addedOn +
                ", crossedOff=" + crossedOff +
                ", removed=" + removed +
                ", updated=" + updated +
                ", lastChanged=" + lastChanged +
                ", usedCount=" + usedCount +
                ", item_id=" + itemId +
                ", listId='" + listId + '\'' +
                ", tagId='" + tagId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ItemMergeDTO that = (ItemMergeDTO) o;
        return Objects.equals(addedOn, that.addedOn) && Objects.equals(listId, that.listId) && Objects.equals(tagId, that.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(addedOn, listId, tagId);
    }
}
