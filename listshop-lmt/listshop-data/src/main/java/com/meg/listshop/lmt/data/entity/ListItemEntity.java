package com.meg.listshop.lmt.data.entity;

import jakarta.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by margaretmartin on 24/10/2017.
 */
@Entity
@Table(name = "list_item")
public class ListItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "list_item_sequence")
    @SequenceGenerator(name = "list_item_sequence", sequenceName = "list_item_sequence", allocationSize = 1)
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

    @Transient
    private Set<String> handles;


    private Date addedOn;

    private Date crossedOff;

    private Date removedOn;

    private Date updatedOn;

    private String freeText;


    @Transient
    private Long tagId;

    public ListItemEntity(Long id) {
        item_id = id;
    }

    public ListItemEntity() {
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


    public String getRawListSources() {
        return rawListSources != null ? rawListSources : "";
    }

    public void setRawListSources(String rawListSources) {
        this.rawListSources = rawListSources;
    }

    public Set<String> getHandles() {
        return handles != null ? handles : new HashSet<>();
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
                '}';
    }

    public ListItemEntity createCopy() {
        ListItemEntity cloned = new ListItemEntity();
        cloned.setAddedOn(this.getAddedOn());
        cloned.setCrossedOff(this.getCrossedOff());
        cloned.setRemovedOn(this.getRemovedOn());
        cloned.setUpdatedOn(this.getUpdatedOn());
        cloned.setUsedCount(this.getUsedCount());
        cloned.setListId(this.getListId());
        cloned.setFreeText(this.getFreeText());
        cloned.setRawDishSources(this.getRawDishSources());
        cloned.setRawListSources(this.getRawListSources());
        cloned.setTag(this.getTag());
        return cloned;
    }

    public String getDisplay() {
        if (this.tag != null) {
            return this.tag.getName();
        }
        return this.freeText;
    }
}
