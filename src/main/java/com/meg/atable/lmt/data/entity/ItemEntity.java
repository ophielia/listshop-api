package com.meg.atable.lmt.data.entity;

import com.meg.atable.lmt.api.model.ItemSourceType;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by margaretmartin on 24/10/2017.
 */
@Entity
@Table(name = "list_item")
@GenericGenerator(
        name = "list_item_sequence",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@org.hibernate.annotations.Parameter(
                name = "sequence_name",
                value="list_item_sequence"),
                @org.hibernate.annotations.Parameter(
                        name = "increment_size",
                        value="1")}
)
public class ItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "list_item_sequence")
    @Column(name = "item_id")
    private Long item_id;

    @OneToOne
    @JoinColumn(name = "tagId")
    private TagEntity tag;

    @Column(name = "dish_sources")
    private String rawDishSources;

    @Column(name = "list_sources")
    private String rawListSources;

    @Column(name = "list_id")
    private Long listId;

    @Column(name = "used_count")
    private Integer usedCount;

    private Date addedOn;

    private Date crossedOff;

    private Date removedOn;

    private Date updatedOn;

    private String freeText;

    @Column(name = "frequent_cross_off")
    private Boolean isFrequent = false;

    @Transient
    private Long tagId;
    @Transient
    private int removedCount;
    @Transient
    private int addCount;
    @Transient
    private boolean isUpdated;
    @Transient
    private boolean isDeleted;
    @Transient
    private boolean isAdded;

    public ItemEntity(Long id) {
        item_id = id;
    }

    public ItemEntity() {
        // necessary for jpa construction
    }

    public Long getId() {
        return item_id;
    }

    public void setId(Long itemId) {
        // TODO - this is just for tests - make a DaoUtils so we can remove this method
        this.item_id = itemId;
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

    public Date getAddedOn() {
        return addedOn;
    }

    public void setAddedOn(Date addedOn) {
        this.addedOn = addedOn;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getFreeText() {
        return freeText;
    }

    public void setFreeText(String freeText) {
        this.freeText = freeText;
    }

    public Date getCrossedOff() {
        return crossedOff;
    }

    public void setCrossedOff(Date crossedOff) {
        this.crossedOff = crossedOff;
    }

    public Date getRemovedOn() {
        return removedOn;
    }

    public void setRemovedOn(Date removedOn) {
        this.removedOn = removedOn;
    }

    public Long getListId() {
        return listId;
    }

    public void setListId(Long listId) {
        this.listId = listId;
    }

    public Integer getUsedCount() {
        return usedCount != null ? usedCount : 0;
    }

    public void setUsedCount(Integer usedCount) {
        this.usedCount = usedCount;
    }

    public Long getTagId() {
        return tagId;

    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public boolean isFrequent() {
        if (isFrequent == null) {
            return false;
        }
        return isFrequent;
    }

    public void setFrequent(boolean frequent) {
        isFrequent = frequent;
    }

    public String getRawListSources() {
        return rawListSources != null ? rawListSources : "";
    }

    public void setRawListSources(String rawListSources) {
        this.rawListSources = rawListSources;
    }

    public void addRawDishSource(Long dishId) {
        if (dishId == null) {
            return;
        }
        if (rawDishSources == null) {
            rawDishSources = String.valueOf(dishId);
        } else {
            rawDishSources = rawDishSources + ";" + dishId;
        }
    }

    public void addRawListSource(String sourceType) {
        if (sourceType == null) {
            return;
        }
        if (rawListSources == null) {
            rawListSources = sourceType;
        } else {
            rawListSources = rawListSources + ";" + sourceType;
        }
    }


    @Override
    public String toString() {
        return "ItemEntity{" +
                "item_id=" + item_id +
                ", rawDishSources='" + rawDishSources + '\'' +
                ", rawListSources='" + rawListSources + '\'' +
                ", listId=" + listId +
                ", usedCount=" + usedCount +
                ", addedOn=" + addedOn +
                ", freeText='" + freeText + '\'' +
                ", crossedOff=" + crossedOff +
                ", tagId=" + tagId +
                ", isFrequent=" + isFrequent +
                '}';
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public void setUpdated(boolean updated) {
        this.isUpdated = updated;
        this.updatedOn = new Date();
    }

    public boolean isAdded() {
        return isAdded;
    }

    public void setIsAdded(boolean isAdded) {
        this.isAdded = isAdded;
        this.addedOn = new Date();
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        this.isDeleted = deleted;
        this.removedOn = new Date();
    }

    public int getAddCount() {
        return addCount;
    }

    public void incrementAddCount() {
        this.addCount++;
    }

    public void incrementAddCount(int addCount) {
        this.addCount = this.addCount + addCount;
    }

    public boolean isRemoved() {
        return removedCount > 0;
    }

    public int getRemovedCount() {
        return removedCount;
    }

    public void incrementRemovedCount() {
        this.removedCount++;
    }

    public void incrementRemovedCount(int removeCount) {
        this.removedCount = this.removedCount + removeCount;
    }



    public String getDisplay() {
        if (this.tag != null) {
            return this.tag.getName();
        }
        return this.freeText;
    }
}
