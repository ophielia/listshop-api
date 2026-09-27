/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.data.pojos;

import com.meg.listshop.common.FlatStringUtils;
import com.meg.listshop.lmt.api.model.FractionType;

import java.util.List;
import java.util.Objects;


public class SimpleListItemDTO {

    private Long listId;
    private Long tagId;
    private Long unitId;
    private Double quantity;
    private Integer wholeQuantity;
    private FractionType fractionalQuantity;
    private List<String> rawModifiers;
    private String unitName;
    private String marker;
    private String unitSize;
    private String rawEntry;



    public static SimpleListItemDTO from(Long tagId, Long listId) {
        SimpleListItemDTO newDto = new SimpleListItemDTO();
        newDto.listId = listId;
        newDto.tagId = tagId;
        return newDto;
    }

    public Long getListId() {
        return listId;
    }

    public void setListId(Long listId) {
        this.listId = listId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
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

    public List<String> getRawModifiers() {
        return rawModifiers;
    }

    public void setRawModifiers(List<String> rawModifiers) {
        this.rawModifiers = rawModifiers;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
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

    @Override
    public String toString() {
        return "SimpleListItemDTO{" +
                "listId=" + listId +
                ", tagId=" + tagId +
                ", unitId=" + unitId +
                ", quantity=" + quantity +
                ", wholeQuantity=" + wholeQuantity +
                ", fractionalQuantity=" + fractionalQuantity +
                ", rawModifiers=" + rawModifiers +
                ", unitName='" + unitName + '\'' +
                ", marker='" + marker + '\'' +
                ", unitSize='" + unitSize + '\'' +
                ", rawEntry='" + rawEntry + '\'' +
                '}';
    }
}
