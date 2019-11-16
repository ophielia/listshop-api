package com.meg.atable.lmt.service;

import com.meg.atable.common.FlatStringUtils;
import com.meg.atable.lmt.data.entity.ItemEntity;
import com.meg.atable.lmt.data.entity.TagEntity;

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

    private Long tagId;

    private int removedCount;

    private int addCount;

    private boolean isUpdated;

    private boolean isRemoved;

    private boolean isAdded;

    private boolean isChanged;
    private boolean fromClient = false;

    private int secondComparisonWindow = 2;

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

    public Boolean getFrequent() {
        return item.isFrequent();
    }

    public void setFrequent(Boolean frequent) {
        this.item.setFrequent(frequent);
    }


    // Collector Item methods
    public void remove(CollectorContext context) {
        int count = getUsedCount() != null ? getUsedCount() : 0;
        if (count <= 1 || context.isRemoveEntireItem()) {
            // delete item outright
            setRemoved(true);
            incrementRemovedCount(getUsedCount());
            setUsedCount(0);
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
        // (possibly) remove list sources
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
        incrementRemovedCount();

    }


    public void add(CollectorContext context) {
        add(context, false);
    }

    public void add(CollectorContext context, Boolean isNew) {
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
            inflatedDishSources.add(String.valueOf(context.getListId()));
            String newSources = FlatStringUtils.flattenSetToString(inflatedDishSources, ";");
            setRawDishSources(newSources);
        }
        setUsedCount(count + 1);
        incrementAddCount();
        if (!isNew) {
            setUpdated(true);
        }

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
            item.setRawListSources(sourceType);
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


    public void setFromClient(boolean fromClient) {
        this.fromClient = fromClient;
    }

    public boolean getFromClient() {
        return fromClient;
    }

    @Override
    public boolean equals(Object o) {
        // this is just a basic comparison
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollectedItem that = (CollectedItem) o;


        return isUpdated == that.isUpdated &&
                isRemoved == that.isRemoved &&
                tagId.equals(that.tagId);
    }

    public boolean equalsWithWindow(int secondCount, Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollectedItem that = (CollectedItem) o;

        if (dateEquals(secondComparisonWindow, getStatusDate(), that.getStatusDate())) {
            return true;
        }

        return  dateEquals(secondCount,getAddedOn() ,that.getAddedOn()) &&
                dateEquals(secondCount,getUpdatedOn() ,that.getUpdatedOn()) &&
                dateEquals(secondCount,getRemovedOn() ,that.getRemovedOn()) &&
                dateEquals(secondCount,getCrossedOff() ,that.getCrossedOff()) &&
                getUsedCount() != null && getUsedCount().equals(that.getUsedCount()) &&
                getFreeText() != null && getFreeText().equals(that.getFreeText()) &&
             //           getRawDishSources() != null && getRawDishSources().equals(that.getRawDishSources()) &&
               //         getRawListSources() != null && getRawListSources().equals(that.getRawListSources()) &&
        tagId.equals(that.tagId) ;
    }

    private boolean dateEquals(int secondCount, LocalDateTime date1, LocalDateTime date2) {
        if (date1 == null && date2 == null) {
            return true;
        } else if (date1 != null && date2 == null) {
            return false;
        } else if (date2 != null && date1 == null) {
            return false;
        }

        Duration period = Duration.between(date1, date2);
        long milliSeconds = Math.abs(period.toMillis());
        return secondCount * 1000 > milliSeconds;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tagId, isUpdated, isRemoved);
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
        }

    }

}
