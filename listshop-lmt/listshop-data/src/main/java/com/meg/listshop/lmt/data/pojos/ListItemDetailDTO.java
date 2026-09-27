/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.data.pojos;

import com.meg.listshop.lmt.api.model.FractionType;
import com.meg.listshop.lmt.data.entity.ListItemDetailEntity;

public class ListItemDetailDTO {

    private Long itemDetailId;
    private Long itemId;
    private int count;
    private Long linkedListId;
    private Long linkedDishId;
    private Integer wholeQuantity;
    private FractionType fractionalQuantity;
    private Double quantity;
    private Long unitId;
    private String marker;
    private String unitSize;
    private String rawEntry;
    private boolean unspecified;
    private boolean containsUnspecified;
    private boolean isUserSize;

    public ListItemDetailDTO(ListItemDetailEntity entity) {
        if (entity == null) {
            return;
        }
        this.itemDetailId = entity.getItemDetailId();
        this.itemId = entity.getItem() != null ? entity.getItem().getId() : null;
        this.count = entity.getCount() != null ? entity.getCount() : 0;
        this.linkedListId = entity.getLinkedListId();
        this.linkedDishId = entity.getLinkedDishId();
        this.wholeQuantity = entity.getWholeQuantity();
        this.fractionalQuantity = entity.getFractionalQuantity();
        this.quantity = entity.getQuantity();
        this.unitId = entity.getUnitId();
        this.marker = entity.getMarker();
        this.unitSize = entity.getUnitSize();
        this.rawEntry = entity.getRawEntry();
        this.unspecified = entity.isUnspecified();
        this.containsUnspecified = entity.isContainsUnspecified();
        this.isUserSize = entity.isUserSize();
    }

    public ListItemDetailDTO() {
    }

    public Long getItemDetailId() {
        return itemDetailId;
    }

    public void setItemDetailId(Long itemDetailId) {
        this.itemDetailId = itemDetailId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Long getLinkedListId() {
        return linkedListId;
    }

    public void setLinkedListId(Long linkedListId) {
        this.linkedListId = linkedListId;
    }

    public Long getLinkedDishId() {
        return linkedDishId;
    }

    public void setLinkedDishId(Long linkedDishId) {
        this.linkedDishId = linkedDishId;
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

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public String getUnitSize() {
        return unitSize;
    }

    public void setUnitSize(String unitSize) {
        this.unitSize = unitSize;
    }

    public String getRawEntry() {
        return rawEntry;
    }

    public void setRawEntry(String rawEntry) {
        this.rawEntry = rawEntry;
    }

    public boolean isUnspecified() {
        return unspecified;
    }

    public void setUnspecified(boolean unspecified) {
        this.unspecified = unspecified;
    }

    public boolean isContainsUnspecified() {
        return containsUnspecified;
    }

    public void setContainsUnspecified(boolean containsUnspecified) {
        this.containsUnspecified = containsUnspecified;
    }

    public boolean isUserSize() {
        return isUserSize;
    }

    public void setUserSize(boolean userSize) {
        isUserSize = userSize;
    }

    @Override
    public String toString() {
        return "ListItemDetailDTO{" +
                "itemDetailId=" + itemDetailId +
                ", itemId=" + itemId +
                ", count=" + count +
                ", linkedListId=" + linkedListId +
                ", linkedDishId=" + linkedDishId +
                ", quantity=" + quantity +
                '}';
    }
}
