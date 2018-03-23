package com.meg.atable.data.entity;

import com.meg.atable.api.model.ItemSourceType;

import javax.persistence.*;
import java.util.Date;

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
    private String dishSources;

    @Column(name = "item_sources")
    private String itemSources;

    @Column(name = "list_id")
    private Long listId;

    @Column(name = "used_count")
    private Integer usedCount;

    private Date addedOn;

    private String freeText;

    private Date crossedOff;

    private String listCategory;

    @Transient
    private Long tag_id;

    @Transient
    private boolean isFrequent = false;

    private Long categoryId;

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

    public String getDishSources() {
        return dishSources;
    }

    public void setDishSources(String dishSources) {
        this.dishSources = dishSources;
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
        return usedCount;
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
        if (this.dishSources == null) {
            this.dishSources = sourceType.name();
            return;
        }
        if (this.dishSources.contains(sourceType.name())) {
            return;
        }
        this.dishSources = this.dishSources + ";" + sourceType.name();
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isFrequent() {
        return isFrequent;
    }

    public void setFrequent(boolean frequent) {
        isFrequent = frequent;
    }

    public String getItemSources() {
        return itemSources;
    }

    public void setItemSources(String itemSources) {
        this.itemSources = itemSources;
    }
}
