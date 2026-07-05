/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.data.pojos;

import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.lmt.api.model.FractionType;
import com.meg.listshop.lmt.api.model.v2.SpecificationType;
import com.meg.listshop.lmt.data.entity.ListItemEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class ListItemDTO {

    private Long itemId;
    private List<ListItemDetailDTO> details;
    private TagEntity tag;
    private String rawDishSources;
    private String rawListSources;
    private Long listId;
    private Integer usedCount;
    private Set<String> handles;
    private Date addedOn;
    private Date crossedOff;
    private Date removedOn;
    private Date updatedOn;
    private Double roundedQuantity;
    private Double rawQuantity;
    private Integer wholeQuantity;
    private FractionType fractionalQuantity;
    private UnitEntity unit;
    private String unitSize;
    private String amountText;
    private SpecificationType specificationType;
    private Long tagId;
    private Set<String> sources;


    public ListItemDTO(ListItemEntity entity) {
        if (entity == null) {
            return;
        }
        this.itemId = entity.getId();
        this.tag = entity.getTag();
        this.rawDishSources = entity.getRawDishSources();
        this.rawListSources = entity.getRawListSources();
        this.listId = entity.getListId();
        this.usedCount = entity.getUsedCount();
        this.handles = entity.getHandles();
        this.addedOn = entity.getAddedOn();
        this.crossedOff = entity.getCrossedOff();
        this.removedOn = entity.getRemovedOn();
        this.updatedOn = entity.getUpdatedOn();
        this.roundedQuantity = entity.getRoundedQuantity();
        this.rawQuantity = entity.getRawQuantity();
        this.wholeQuantity = entity.getWholeQuantity();
        this.fractionalQuantity = entity.getFractionalQuantity();
        this.unit = entity.getUnit();
        this.unitSize = entity.getUnitSize();
        this.amountText = entity.getAmountText();
        this.specificationType = entity.getSpecificationType();
        this.tagId = entity.getTagId();
        if (entity.getDetails() != null) {
            this.details = entity.getDetails().stream()
                    .map(ListItemDetailDTO::new)
                    .collect(Collectors.toList());
        }
    }

    public ListItemDTO(ListItemEntity entity, Set<String> sources) {
        this(entity);
        this.sources = sources;
    }

    public ListItemDTO() {
    }

    public Set<String> getSources() {
        return sources;
    }

    public void setSources(Set<String> sources) {
        this.sources = sources;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public List<ListItemDetailDTO> getDetails() {
        return details;
    }

    public void setDetails(List<ListItemDetailDTO> details) {
        this.details = details;
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

    public Integer getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(Integer usedCount) {
        this.usedCount = usedCount;
    }

    public Set<String> getHandles() {
        return handles;
    }

    public void setHandles(Set<String> handles) {
        this.handles = handles;
    }

    public Date getAddedOn() {
        return addedOn;
    }

    public void setAddedOn(Date addedOn) {
        this.addedOn = addedOn;
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

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    public Double getRoundedQuantity() {
        return roundedQuantity;
    }

    public void setRoundedQuantity(Double roundedQuantity) {
        this.roundedQuantity = roundedQuantity;
    }

    public Double getRawQuantity() {
        return rawQuantity;
    }

    public void setRawQuantity(Double rawQuantity) {
        this.rawQuantity = rawQuantity;
    }

    public Integer getWholeQuantity() {
        return wholeQuantity;
    }

    public void setWholeQuantity(Integer wholeQuantity) {
        this.wholeQuantity = wholeQuantity;
    }

    public FractionType getFractionalQuantity() {
        return fractionalQuantity;
    }

    public void setFractionalQuantity(FractionType fractionalQuantity) {
        this.fractionalQuantity = fractionalQuantity;
    }

    public UnitEntity getUnit() {
        return unit;
    }

    public void setUnit(UnitEntity unit) {
        this.unit = unit;
    }

    public String getUnitSize() {
        return unitSize;
    }

    public void setUnitSize(String unitSize) {
        this.unitSize = unitSize;
    }

    public String getAmountText() {
        return amountText;
    }

    public void setAmountText(String amountText) {
        this.amountText = amountText;
    }

    public SpecificationType getSpecificationType() {
        return specificationType;
    }

    public void setSpecificationType(SpecificationType specificationType) {
        this.specificationType = specificationType;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    @Override
    public String toString() {
        return "ListItemDTO{" +
                "itemId=" + itemId +
                ", sources=" + sources +
                '}';
    }
}
