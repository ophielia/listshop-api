package com.meg.atable.lmt.service;

import com.meg.atable.lmt.data.entity.ItemEntity;
import com.meg.atable.lmt.data.entity.TagEntity;

import java.util.*;

/**
 * Created by margaretmartin on 02/11/2017.
 */
public class CollectedItem {

    private ItemEntity item;

    private Long tagId;

    private int removedCount;

    private int addCount;

    private boolean isUpdated;

    private boolean isRemoved;

    private boolean isAdded;

    private boolean isChanged;

    public CollectedItem(ItemEntity itemEntity) {
        item = itemEntity;
    }
    
    //** Item Accessors **/

    public Long getId() {
        return item.getId();
    }

    public void setId(Long item_id) {
        this.item.setId(item_id);
    }

    public TagEntity getTag() {
        return item.getTag();
    }

    public void setTag(TagEntity tag) {
        this.item.setTag(tag);
    }

    public String getRawDishSources() {
        return item.getRawDishSources();
    }

    public void setRawDishSources(String rawDishSources) {
        this.item.setRawDishSources( rawDishSources);
    }

    public String getRawListSources() {
        return item.getRawListSources();
    }

    public void setRawListSources(String rawListSources) {
        this.item.setRawListSources(rawListSources);
    }

    public Long getListId() {
        return item.getListId();
    }

    public void setListId(Long listId) {
        this.item.setListId(listId);
    }

    public Integer getUsedCount() {
        return item.getUsedCount();
    }

    public void setUsedCount(Integer usedCount) {
        this.item.setUsedCount(usedCount);
    }

    public Date getAddedOn() {
        return item.getAddedOn();
    }

    public void setAddedOn(Date addedOn) {
        this.item.setAddedOn(addedOn);
    }

    public Date getCrossedOff() {
        return item.getCrossedOff();
    }

    public void setCrossedOff(Date crossedOff) {
        this.item.setCrossedOff(crossedOff);
    }

    public Date getRemovedOn() {
        return item.getRemovedOn();
    }

    public void setRemovedOn(Date removedOn) {
        this.item.setRemovedOn(removedOn);
    }

    public Date getUpdatedOn() {
        return item.getUpdatedOn();
    }

    public void setUpdatedOn(Date updatedOn) {
        this.item.setUpdatedOn(updatedOn);
    }

    public String getFreeText() {
        return item.getFreeText();
    }

    public void setFreeText(String freeText) {
        this.item.setFreeText(freeText);
    }

    public Boolean getFrequent() {
        return item.isFrequent();
    }

    public void setFrequent(Boolean frequent) {
        this.item.setFrequent(frequent);
    }


    // Collector Item values

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public int getRemovedCount() {
        return removedCount;
    }

    public void setRemovedCount(int removedCount) {
        this.removedCount = removedCount;
    }

    public int getAddCount() {
        return addCount;
    }

    public void setAddCount(int addCount) {
        this.addCount = addCount;
    }




    public boolean isChanged() {
        return isChanged;
    }

    public void setChanged(boolean changed) {
        isChanged = changed;
    }


    public ItemEntity getItem() {
        return item;
    }

    public void setItem(ItemEntity item) {
        this.item = item;
    }

    // Date change methods

    public boolean isAdded() {
        return isAdded;
    }

    public void setIsAdded(boolean isAdded) {
        this.isAdded = isAdded;
        if (isAdded) {
            this.item.setAddedOn(new Date());
            // reset dates besides added on
            this.item.setUpdatedOn(null);
            this.item.setCrossedOff(null);
            this.item.setRemovedOn(null);
            this.isChanged = true;

        }
    }

    public boolean isRemoved() {
        return isRemoved;
    }

    public void setRemoved(boolean removed) {
        if (removed) {
            this.setRemovedOn(new Date());
            this.isRemoved = true;
        } else {
            this.setRemovedOn(null);
            this.setCrossedOff(null);
            this.setUpdatedOn(null);
            this.setUsedCount(0);
            this.isRemoved = false;
        }
        this.isChanged = true;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public void setUpdated(boolean updated) {
        isUpdated = updated;
        if (isUpdated) {
            this.item.setUpdatedOn(new Date());
            // reset dates besides added on
            this.item.setCrossedOff(null);
            this.item.setRemovedOn(null);
            this.isChanged = true;

        }

    }
    // convenience methods
    public void addRawDishSource(Long dishId) {
        if (dishId == null) {
            return;
        }
        if (item.getRawDishSources() == null) {
            item.setRawDishSources(String.valueOf(dishId));
        } else {
            item.setRawDishSources(item.getRawDishSources() + ";" + dishId);
        }
    }

    public void addRawListSource(String sourceType) {
        if (sourceType == null) {
            return;
        }
        if (item.getRawListSources() == null) {
            item.setRawListSources(String.valueOf(sourceType));
        } else {
            item.setRawListSources(item.getRawListSources() + ";" + sourceType);
        }
    }

    public void incrementAddCount() {
        this.addCount++;
    }

    public void incrementAddCount(int addCount) {
        this.addCount = this.removedCount + addCount;
    }


    public void incrementRemovedCount() {
        this.removedCount++;
    }

    public void incrementRemovedCount(int removeCount) {
        this.removedCount = this.removedCount + removeCount;
    }

}
