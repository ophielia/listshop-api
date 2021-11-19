package com.meg.listshop.lmt.service;

import com.meg.listshop.common.FlatStringUtils;
import com.meg.listshop.lmt.data.entity.ItemEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

/**
 * Created by margaretmartin on 02/11/2017.
 */
public class CollectedItem {

    private ItemEntity item;

    private boolean isUpdated;

    private boolean isRemoved;

    private boolean isAdded;

    private boolean isCountAdded;

    private boolean isCountDecreased;

    private boolean isChanged;
    private boolean fromClient = false;

    private final int secondComparisonWindow = 2;


    public CollectedItem(ItemEntity itemEntity) {
        item = itemEntity;
        isRemoved = item.getRemovedOn() != null;
    }
    
    //** Item Accessors **/

    public Long getId() {
        return item.getId();
    }

    public void setId(Long itemId) {
        this.item.setId(itemId);
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

    public boolean isCountAdded() {
        return isCountAdded;
    }

    public boolean isCountDecreased() {
        return isCountDecreased;
    }

    public Integer getUsedCount() {
        return item.getUsedCount();
    }

    public void setUsedCount(Integer usedCount) {
        boolean countIncrease = this.item.getUsedCount() != null && this.item.getUsedCount() < usedCount;
        boolean countDecrease = this.item.getUsedCount() != null && this.item.getUsedCount() > usedCount;
        this.item.setUsedCount(usedCount);
        this.isCountAdded = countIncrease;
        this.isCountDecreased = countDecrease;
    }

    public LocalDateTime getAddedOn() {
        if (item.getAddedOn() == null) {
            return null;
        }
        return new java.sql.Timestamp(
                item.getAddedOn().getTime()).toLocalDateTime();
    }

    public void setAddedOn(LocalDateTime addedOn) {
        if (addedOn == null) {
            this.item.setAddedOn(null);
            return;
        }
        this.item.setAddedOn(java.sql.Timestamp.valueOf(addedOn));
    }

    public LocalDateTime getCrossedOff() {
        if (item.getCrossedOff() == null) {
            return null;
        }
        return new java.sql.Timestamp(
                item.getCrossedOff().getTime()).toLocalDateTime();

    }

    public void setCrossedOff(LocalDateTime crossedOff) {
        if (crossedOff == null) {
            this.item.setCrossedOff(null);
            return;
        }
        this.item.setCrossedOff(java.sql.Timestamp.valueOf(crossedOff));
    }

    public LocalDateTime getRemovedOn() {

            if (item.getRemovedOn() == null) {
                return null;
            }
            return new java.sql.Timestamp(
                    item.getRemovedOn().getTime()).toLocalDateTime();


    }

    public void setRemovedOn(LocalDateTime removedOn) {
        if (removedOn == null) {
            this.item.setRemovedOn(null);
            return;
        }
        this.item.setRemovedOn(java.sql.Timestamp.valueOf(removedOn));
    }

    public LocalDateTime getUpdatedOn()
        {

            if ( item.getUpdatedOn() == null) {
                return null;
            }
            return new java.sql.Timestamp(item.getUpdatedOn().getTime()).toLocalDateTime();


        }


    public void setUpdatedOn(LocalDateTime updatedOn)
    {
        if (updatedOn == null) {
            this.item.setUpdatedOn(null);
            return;
        }
        this.item.setUpdatedOn(java.sql.Timestamp.valueOf(updatedOn));
    }

    public String getFreeText() {
        return item.getFreeText();
    }

    public void setFreeText(String freeText) {
        this.item.setFreeText(freeText);
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
        setUpdated(true);
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
            setUpdated(true);
        }

    }

    public void add(ItemEntity item, LocalDateTime crossedOffDate, CollectorContext context, boolean isNew) {

        int newCount = item.getUsedCount() != null && item.getUsedCount() > 0 ? item.getUsedCount() : 1;
        add(newCount, context, crossedOffDate, isNew);

    }
    // Collector Item values

    public Long getTagId() {
        return getTag().getId();
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
    // get status
    private CollectedItemStatus getStatus() {
        if (item.getRemovedOn() != null) {
            return CollectedItemStatus.REMOVED;
        }
        if (item.getAddedOn() != null &&
                item.getRemovedOn() == null &&
                item.getCrossedOff() == null &&
                item.getUpdatedOn() == null) {
            return CollectedItemStatus.NEW;
        }
        if (item.getCrossedOff() != null) {
            return CollectedItemStatus.CROSSED_OFF;
        }
        return CollectedItemStatus.UPDATED;
    }



    public void setFromClient(boolean fromClient) {
        this.fromClient = fromClient;
    }

    @Override
    public boolean equals(Object o) {
        // this is just a basic comparison
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollectedItem that = (CollectedItem) o;


        return isUpdated == that.isUpdated &&
                isRemoved == that.isRemoved &&
                getTag().getId().equals(that.getTag().getId());
    }

    public boolean equalsWithWindow(int secondCount, Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollectedItem that = (CollectedItem) o;

        if (dateEquals(secondComparisonWindow, getStatusDate(), that.getStatusDate())) {
            return true;
        }

        return dateEquals(secondCount, getAddedOn(), that.getAddedOn()) &&
                dateEquals(secondCount, getUpdatedOn(), that.getUpdatedOn()) &&
                dateEquals(secondCount, getRemovedOn(), that.getRemovedOn()) &&
                dateEquals(secondCount, getCrossedOff(), that.getCrossedOff()) &&
                getUsedCount() != null && getUsedCount().equals(that.getUsedCount()) &&
                getFreeText() != null && getFreeText().equals(that.getFreeText()) &&
                getTag().getId().equals(that.getTag().getId());
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
        return Objects.hash(getTag().getId(), isUpdated, isRemoved);
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
            setUpdated(true);
            item.setRawDishSources(null);
            item.setRawListSources(null);
            setUsedCount(0);
        }

    }


    public void mergeFrom(CollectedItem clientItem) {
        ItemEntity nakedServer = getItem();
        ItemEntity nakedClient = clientItem.getItem();
        nakedServer.setAddedOn(nakedClient.getAddedOn());
        nakedServer.setCrossedOff(nakedClient.getCrossedOff());
        nakedServer.setFreeText(nakedClient.getFreeText());
        nakedServer.setRemovedOn(nakedClient.getRemovedOn());
        nakedServer.setUpdatedOn(nakedClient.getUpdatedOn());
        nakedServer.setUsedCount(nakedClient.getUsedCount());
        nakedServer.setUsedCount(nakedClient.getUsedCount());
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
                setUpdated(true);
                break;
            case CROSSED_OFF:
                setUpdated(true);
                setCrossedOff(statusDate);
                break;
        }
    }
}
