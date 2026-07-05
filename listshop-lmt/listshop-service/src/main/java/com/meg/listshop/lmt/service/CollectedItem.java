/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.service;

import com.meg.listshop.common.FlatStringUtils;
import com.meg.listshop.lmt.data.entity.ListItemEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.pojos.ListItemDTO;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by margaretmartin on 02/11/2017.
 */
public class CollectedItem {

    private Long id;

    private TagEntity tag;
    private String rawDishSources;
    private String rawListSources;
    private Long listId;
    private Integer usedCount;
    private LocalDateTime addedOn;
    private LocalDateTime crossedOff;
    private LocalDateTime removedOn;
    private LocalDateTime updatedOn;
    private ListItemEntity itemEntity;

    private boolean isUpdated;

    private boolean isRemoved;

    private boolean isAdded;

    private boolean isCountAdded;

    private boolean isCountDecreased;

    private boolean isChanged;

    private final static int SECOND_COMPARISON_WINDOW = 2;


    public CollectedItem(ListItemEntity entity) {
        if (entity == null) {
            return;
        }
        this.itemEntity = entity;
        this.id = entity.getId();
        this.tag = entity.getTag();
        this.rawDishSources = entity.getRawDishSources();
        this.rawListSources = entity.getRawListSources();
        this.listId = entity.getListId();
        this.usedCount = entity.getUsedCount();
        this.addedOn = toLocalDateTime(entity.getAddedOn());
        this.crossedOff = toLocalDateTime(entity.getCrossedOff());
        this.removedOn = toLocalDateTime(entity.getRemovedOn());
        this.updatedOn = toLocalDateTime(entity.getUpdatedOn());
        this.isRemoved = this.removedOn != null;
    }

    public CollectedItem(ListItemDTO dto) {
        if (dto == null) {
            return;
        }
        this.id = dto.getItemId();
        this.tag = dto.getTag();
        this.rawDishSources = dto.getRawDishSources();
        this.rawListSources = dto.getRawListSources();
        this.listId = dto.getListId();
        this.usedCount = dto.getUsedCount();
        this.addedOn = toLocalDateTime(dto.getAddedOn());
        this.crossedOff = toLocalDateTime(dto.getCrossedOff());
        this.removedOn = toLocalDateTime(dto.getRemovedOn());
        this.updatedOn = toLocalDateTime(dto.getUpdatedOn());
        this.isRemoved = this.removedOn != null;
    }

    
    //** Item Accessors **/

    public Long getId() {
        return id;
    }

    public void setId(Long itemId) {
        this.id = itemId;
    }

    public TagEntity getTag() {
        return tag;
    }

    public void setTag(TagEntity tag) {
        this.tag = tag;
    }

    public String getRawDishSources() {
        return rawDishSources;
    }

    public void setRawDishSources(String rawDishSources) {
        this.rawDishSources = rawDishSources;
    }

    public String getRawListSources() {
        return rawListSources;
    }

    public void setRawListSources(String rawListSources) {
        this.rawListSources = rawListSources;
    }

    public Long getListId() {
        return listId;
    }

    public void setListId(Long listId) {
        this.listId = listId;
    }

    public boolean isCountAdded() {
        return isCountAdded;
    }

    public boolean isCountDecreased() {
        return isCountDecreased;
    }

    public Integer getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(Integer usedCount) {
        boolean countIncrease = this.usedCount != null && this.usedCount < usedCount;
        boolean countDecrease = this.usedCount != null && this.usedCount > usedCount;
        this.usedCount = usedCount;
        this.isCountAdded = countIncrease;
        this.isCountDecreased = countDecrease;
    }

    public LocalDateTime getAddedOn() {
        return addedOn;
    }

    public void setAddedOn(LocalDateTime addedOn) {
        this.addedOn = addedOn;
    }

    public LocalDateTime getCrossedOff() {
        return crossedOff;
    }

    public void setCrossedOff(LocalDateTime crossedOff) {
        this.crossedOff = crossedOff;
    }

    public LocalDateTime getRemovedOn() {
        return removedOn;
    }

    public void setRemovedOn(LocalDateTime removedOn) {
        this.removedOn = removedOn;
    }

    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }


    public void setUpdatedOn(LocalDateTime updatedOn) {
        this.updatedOn = updatedOn;
    }


    // Collector Item methods
    public void remove(CollectorContext context) {
        int count = getUsedCount() != null ? getUsedCount() : 0;
        if (count <= 1 || context.isRemoveEntireItem()) {
            // delete item outright
            setRemoved(true);
            setUsedCount(0);
            setRawDishSources(null);
            setRawListSources(null);
            return;
        }
        // (possibly) remove list sources
        boolean updateListSources = context.hasListId() && context.eligibleForListSourceChange();
        if (updateListSources) {
            Set<String> inflatedListSources = FlatStringUtils.inflateStringToSet(getRawListSources(), ";");
            if (inflatedListSources.contains(String.valueOf(context.getListId()))) {
                inflatedListSources.remove(String.valueOf(context.getListId()));
                String newSources = FlatStringUtils.flattenSetToString(inflatedListSources, ";");
                setRawListSources(newSources);
            }
        }
        // (possibly) remove dish sources
        boolean updateDishSources = context.hasDishId() && context.eligibleForDishSourceChange();
        if (updateDishSources) {
            Set<String> inflatedDishSources = FlatStringUtils.inflateStringToSet(getRawDishSources(), ";");
            if (inflatedDishSources.contains(String.valueOf(context.getDishId()))) {
                inflatedDishSources.remove(String.valueOf(context.getDishId()));
                String newSources = FlatStringUtils.flattenSetToString(inflatedDishSources, ";");
                setRawDishSources(newSources);
            }
        }
        setUpdated(true, context.isKeepExistingCrossedOffStatus());
        setUsedCount(getUsedCount() - 1);

    }


    public void add(CollectorContext context) {
        add(context, false);
    }

    public void add(CollectorContext context, Boolean isNew) {
        add(1, context, null, isNew);

    }

    private void add(int newCount, CollectorContext context, LocalDateTime crossedOffDate, boolean isNew) {
        int count = getUsedCount() != null ? getUsedCount() : 0;
        boolean updateListSources = context.hasListId() && context.eligibleForListSourceChange();
        if (updateListSources) {
            Set<String> inflatedListSources = FlatStringUtils.inflateStringToSet(getRawListSources(), ";");
            inflatedListSources.add(String.valueOf(context.getListId()));
            String newSources = FlatStringUtils.flattenSetToString(inflatedListSources, ";");
            setRawListSources(newSources);
        }
        boolean updateDishSources = context.hasDishId() && context.eligibleForDishSourceChange();
        if (updateDishSources) {
            Set<String> inflatedDishSources = FlatStringUtils.inflateStringToSet(getRawDishSources(), ";");
            inflatedDishSources.add(String.valueOf(context.getDishId()));
            String newSources = FlatStringUtils.flattenSetToString(inflatedDishSources, ";");
            setRawDishSources(newSources);
        }
        setUsedCount(count + newCount);
        if ((isNew || isRemoved) || !context.isKeepExistingCrossedOffStatus()) {
            // copy crossed off
            setCrossedOff(crossedOffDate);
        }
        if (!isNew) {
            setUpdated(true, context.isKeepExistingCrossedOffStatus());
        }

    }

    public void add(ListItemEntity item, LocalDateTime crossedOffDate, CollectorContext context, boolean isNew) {

        int newCount = item.getUsedCount() != null && item.getUsedCount() > 0 ? item.getUsedCount() : 1;
        add(newCount, context, crossedOffDate, isNew);

    }
    // Collector Item values

    public Long getTagId() {
        return tag != null ? tag.getId() : null;
    }

    public boolean isChanged() {
        return isChanged;
    }

    public void setChanged(boolean changed) {
        isChanged = changed;
    }


    public ListItemEntity getItem() {
        ListItemEntity item = this.itemEntity;
        if (item == null) {
            item = new ListItemEntity();
        }
        item.setId(this.id);
        item.setTag(this.tag);
        item.setRawDishSources(this.rawDishSources);
        item.setRawListSources(this.rawListSources);
        item.setListId(this.listId);
        item.setUsedCount(this.usedCount);
        item.setAddedOn(toDate(this.addedOn));
        item.setCrossedOff(toDate(this.crossedOff));
        item.setRemovedOn(toDate(this.removedOn));
        item.setUpdatedOn(toDate(this.updatedOn));

        return item;
    }

    public void setItem(ListItemEntity entity) {
        if (entity == null) {
            return;
        }
        this.id = entity.getId();
        this.tag = entity.getTag();
        this.rawDishSources = entity.getRawDishSources();
        this.rawListSources = entity.getRawListSources();
        this.listId = entity.getListId();
        this.usedCount = entity.getUsedCount();
        this.addedOn = toLocalDateTime(entity.getAddedOn());
        this.crossedOff = toLocalDateTime(entity.getCrossedOff());
        this.removedOn = toLocalDateTime(entity.getRemovedOn());
        this.updatedOn = toLocalDateTime(entity.getUpdatedOn());
    }

    // Date change methods

    public boolean isAdded() {
        return isAdded;
    }

    public void setIsAdded(boolean isAdded) {
        this.isAdded = isAdded;
        if (isAdded) {
            this.addedOn = LocalDateTime.now();
            // reset dates besides added on
            this.updatedOn = null;
            this.crossedOff = null;
            this.removedOn = null;
            this.isChanged = true;

        }
    }

    public boolean isRemoved() {
        return isRemoved;
    }

    public void setRemoved(boolean removed) {
        if (removed) {
            this.setRemovedOn(LocalDateTime.now());
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

    public void setUpdated(boolean updated, boolean keepCrossedOffStatus) {
        isUpdated = updated;
        if (isUpdated) {
            this.updatedOn = LocalDateTime.now();
            // reset dates besides added on
            if (!keepCrossedOffStatus) {
                this.crossedOff = null;
            }
            this.removedOn = null;
            this.isChanged = true;

        }

    }
    // convenience methods
    // get status
    private CollectedItemStatus getStatus() {
        if (this.removedOn != null) {
            return CollectedItemStatus.REMOVED;
        }
        if (this.addedOn != null &&
                this.removedOn == null &&
                this.crossedOff == null &&
                this.updatedOn == null) {
            return CollectedItemStatus.NEW;
        }
        if (this.crossedOff != null) {
            return CollectedItemStatus.CROSSED_OFF;
        }
        return CollectedItemStatus.UPDATED;
    }

    @Override
    public boolean equals(Object o) {
        // this is just a basic comparison
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollectedItem that = (CollectedItem) o;


        return isUpdated == that.isUpdated &&
                isRemoved == that.isRemoved &&
                Objects.equals(this.getTagId(), that.getTagId());
    }

    public boolean equalsWithWindow(int secondCount, Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollectedItem that = (CollectedItem) o;

        if (dateEquals(SECOND_COMPARISON_WINDOW, getStatusDate(), that.getStatusDate())) {
            return true;
        }

        return dateEquals(secondCount, getAddedOn(), that.getAddedOn()) &&
                dateEquals(secondCount, getUpdatedOn(), that.getUpdatedOn()) &&
                dateEquals(secondCount, getRemovedOn(), that.getRemovedOn()) &&
                dateEquals(secondCount, getCrossedOff(), that.getCrossedOff()) &&
                Objects.equals(getUsedCount(), that.getUsedCount()) &&
                Objects.equals(this.getTagId(), that.getTagId());
    }

    private boolean dateEquals(int secondCount, LocalDateTime date1, LocalDateTime date2) {
        if (date1 == null && date2 == null) {
            return true;
        } else if (date1 != null && date2 == null) {
            return false;
        } else if (date1 == null) {
            return false;
        }

        Duration period = Duration.between(date1, date2);
        long milliSeconds = Math.abs(period.toMillis());
        return secondCount * 1000 > milliSeconds;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getTagId(), isUpdated, isRemoved);
    }


    public LocalDateTime getStatusDate() {
        switch (getStatus()) {
            case NEW:
                return getAddedOn();
            case REMOVED:
                return getRemovedOn();
            case UPDATED:
                return getUpdatedOn();
            case CROSSED_OFF:
                return getCrossedOff();
        }
        return getAddedOn();
    }

    public void resetRemoved() {
        // if this tag has been previously removed, we need to clear that information - because
        // it's now being added.
        if (isRemoved()) {
            setRemoved(false);
            setUpdated(true, false);
            this.rawDishSources = null;
            this.rawListSources = null;
            setUsedCount(0);
        }

    }


    public void mergeFrom(CollectedItem clientItem) {
        this.addedOn = clientItem.getAddedOn();
        this.crossedOff = clientItem.getCrossedOff();
        this.removedOn = clientItem.getRemovedOn();
        this.updatedOn = clientItem.getUpdatedOn();
        this.usedCount = clientItem.getUsedCount();
        // copy current state
        CollectedItemStatus clientStatus = clientItem.getStatus();
        LocalDateTime statusDate = clientItem.getStatusDate();
        switch (clientStatus) {
            case NEW:
                setAddedOn(statusDate);
                setIsAdded(true);
                break;
            case REMOVED:
                setRemovedOn(statusDate);
                setRemoved(true);
                break;
            case UPDATED:
                setUpdatedOn(statusDate);
                setUpdated(true, false);
                break;
            case CROSSED_OFF:
                setUpdated(true, false);
                setCrossedOff(statusDate);
                break;
        }
    }

    public boolean createdBefore(Date listLastUpdate) {
        if (listLastUpdate == null) {
            return false;
        }

        LocalDateTime date1 = getAddedOn();
        LocalDateTime date2 = new java.sql.Timestamp(
                listLastUpdate.getTime()).toLocalDateTime();

        if (date1 == null || date2 == null) {
            return false;
        }
        Duration period = Duration.between(date1, date2);
        long days = period.toDays();
        return days > 7; // removed items are purged after 7 days.
    }

    private LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return new java.sql.Timestamp(date.getTime()).toLocalDateTime();
    }

    private Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return java.sql.Timestamp.valueOf(localDateTime);
    }
}
