package com.meg.atable.data.entity;

import com.meg.atable.api.model.ItemSourceType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by margaretmartin on 24/10/2017.
 */
@Entity
@Table(name = "list_item")
@SequenceGenerator(name = "list_item_sequence", sequenceName = "list_item_sequence")
public class ItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "list_item_sequence")
    @Column(name = "item_id")
    private Long item_id;

    @OneToOne
    @JoinColumn(name = "tag_id")
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

    private String freeText;

    private Date crossedOff;

    private String listCategory;

    @Column(name = "frequent_cross_off")
    private Boolean isFrequent = false;


    @Transient
    private Long tag_id;


    @Transient
    private List<DishEntity> dishSources = new ArrayList<>();

    @Transient
    private List<ShoppingListEntity> listSources = new ArrayList<>();

    private Long categoryId;

    @Transient
    private int removedCount;
    @Transient
    private int addCount;
    @Transient
    private boolean isUpdated;
    @Transient
    private boolean deleted;

    public ItemEntity(Long id) {
        item_id = id;
    }

    public ItemEntity() {
        // necessary for jpa construction
    }

    public Long getId() {
        return item_id;
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

    public String getListCategory() {
        return listCategory;
    }

    public void setListCategory(String listCategory) {
        this.listCategory = listCategory;
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
        return tag_id;

    }

    public void setTagId(Long tag_id) {
        this.tag_id = tag_id;
    }


    public void addItemSource(ItemSourceType sourceType) {
        if (this.rawDishSources == null) {
            this.rawDishSources = sourceType.name();
            return;
        }
        if (this.rawDishSources.contains(sourceType.name())) {
            return;
        }
        this.rawDishSources = this.rawDishSources + ";" + sourceType.name();
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isFrequent() {

        return isFrequent == null? false : isFrequent;
    }

    public void setFrequent(boolean frequent) {
        isFrequent = frequent;
    }

    public String getRawListSources() {
        return rawListSources;
    }

    public void setRawListSources(String rawListSources) {
        this.rawListSources = rawListSources;
    }

    public List<DishEntity> getDishSources() {
        return dishSources;
    }

    public void setDishSources(List<DishEntity> dishSources) {
        this.dishSources = dishSources;
    }

    public List<ShoppingListEntity> getListSources() {
        return listSources;
    }

    public void setListSources(List<ShoppingListEntity> listSources) {
        this.listSources = listSources;
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
                ", tag_id=" + tag_id +
                ", isFrequent=" + isFrequent +
                '}';
    }

    public boolean isUpdated() {
        return !isDeleted() && (isUpdated || addCount > 0 || removedCount > 0);
    }

    public void setUpdated(boolean updated) {
        this.isUpdated = updated;
    }

    public boolean isAdded() {
        return addCount > 0;
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
