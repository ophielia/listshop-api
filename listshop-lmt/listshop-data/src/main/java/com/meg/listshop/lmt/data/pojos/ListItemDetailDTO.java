/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.data.pojos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meg.listshop.common.FlatStringUtils;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.lmt.api.model.FractionType;
import com.meg.listshop.lmt.data.entity.ListItemDetailEntity;
import com.meg.listshop.lmt.data.entity.ListItemEntity;
import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;


public class ListItemDetailDTO {

    private ListItemDetailEntity entity;
    private UnitEntity unit;

    public ListItemDetailDTO(ListItemDetailEntity entity, UnitEntity unit) {
        this.entity = entity;
        this.unit = unit;
    }

    public String getUnitDisplay() {
        return unit.getName();
    }

    public Long getItemDetailId() {
        return entity.getItemDetailId();
    }

    public ListItemEntity getItem() {
        return entity.getItem();
    }

    public int getCount() {
        return entity.getCount();
    }

    public Long getLinkedListId() {
        return entity.getLinkedListId();
    }

    public Long getLinkedDishId() {
        return entity.getLinkedDishId();
    }

    public Integer getWholeQuantity() {
        return entity.getWholeQuantity();
    }

    public FractionType getFractionalQuantity() {
        return entity.getFractionalQuantity();
    }

    public Double getQuantity() {
        return entity.getQuantity();
    }

    public Long getUnitId() {
        return entity.getUnitId();
    }

    public String getMarker() {
        return entity.getMarker();
    }

    public String getUnitSize() {
        return entity.getUnitSize();
    }

    public String getRawEntry() {
        return entity.getRawEntry();
    }

    public boolean isUnspecified() {
        return entity.isUnspecified();
    }

    public boolean isContainsUnspecified() {
        return entity.isContainsUnspecified();
    }

    public boolean isUserSize() {
        return entity.isUserSize();
    }
}
