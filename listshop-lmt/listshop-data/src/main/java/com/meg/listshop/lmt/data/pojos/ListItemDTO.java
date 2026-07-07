/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.data.pojos;

import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.lmt.api.model.FractionType;
import com.meg.listshop.lmt.api.model.v2.SpecificationType;
import com.meg.listshop.lmt.data.entity.ListItemDetailEntity;
import com.meg.listshop.lmt.data.entity.ListItemEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class ListItemDTO {

    private ListItemEntity entity;
    private Set<String> sources;


    public ListItemDTO(ListItemEntity entity) {
        this.entity = entity;
        }

    public ListItemDTO(ListItemEntity entity, Set<String> sources) {
        this.entity = entity;
        this.sources = sources;
    }


    public Set<String> getSources() {
        return sources;
    }

    public void setSources(Set<String> sources) {
        this.sources = sources;
    }

    public Long getItemId() {
        return entity.getId();
    }

    public List<ListItemDetailEntity> getDetails() {
        return entity.getDetails();
    }

    public TagEntity getTag() {
        return entity.getTag();
    }

    public Long getListId() {
        return entity.getListId();
    }

    public Integer getUsedCount() {
        return entity.getUsedCount();
    }

    public Date getAddedOn() {
        return entity.getAddedOn();
    }

    public Date getCrossedOff() {
        return entity.getCrossedOff();
    }

    public Date getRemovedOn() {
        return entity.getRemovedOn();
    }

    public Date getUpdatedOn() {
        return entity.getUpdatedOn();
    }

    public Double getRoundedQuantity() {
        return entity.getRoundedQuantity();
    }

    public Double getRawQuantity() {
        return entity.getRawQuantity();
    }

    public Integer getWholeQuantity() {
        return entity.getWholeQuantity();
    }

    public FractionType getFractionalQuantity() {
        return entity.getFractionalQuantity();
    }

    public UnitEntity getUnit() {
        return entity.getUnit();
    }

    public String getUnitSize() {
        return entity.getUnitSize();
    }

    public String getAmountText() {
        return entity.getAmountText();
    }

    public SpecificationType getSpecificationType() {
        return entity.getSpecificationType();
    }

    @Override
    public String toString() {
        return "ListItemDTO{" +
                "entityId=" + (entity != null ? entity.getId() : null) +
                ", sources=" + sources +
                '}';
    }
}
