package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ListItemSource {


    @JsonProperty("linked_dish_id")
    private String linkedDishId;

    @JsonProperty("linked_list_id")
    private String linkedListId;

    @JsonProperty("amount_display")
    private String amountDisplay;

    @JsonProperty("list_amount_display")
    private String listAmountDisplay;

    @JsonIgnore
    private Integer wholeQuantity;
    @JsonIgnore
    private FractionType  fractionalQuantity;
    @JsonIgnore
    private Long unitId;
    @JsonIgnore
    private String marker;
    @JsonIgnore
    private String unitSize;


    public ListItemSource() {
    }




    public String getLinkedDishId() {
        return linkedDishId;
    }

    public ListItemSource linkedDishId(String linkedDishId) {
        this.linkedDishId = linkedDishId;
        return this;
    }

    public String getLinkedListId() {
        return linkedListId;
    }

    public ListItemSource linkedListId(String linkedListId) {
        this.linkedListId = linkedListId;
        return this;
    }

    public String getAmountDisplay() {
        return amountDisplay;
    }

    public ListItemSource amountDisplay(String amountDisplay) {
        this.amountDisplay = amountDisplay;
        return this;
    }

    public String getListAmountDisplay() {
        return listAmountDisplay;
    }

    public ListItemSource listAmountDisplay(String listAmountDisplay) {
        this.listAmountDisplay = listAmountDisplay;
        return this;
    }

    public Integer getWholeQuantity() {
        return wholeQuantity;
    }

    public ListItemSource wholeQuantity(Integer wholeQuantity) {
        this.wholeQuantity = wholeQuantity;
        return this;
    }

    public FractionType getFractionalQuantity() {
        return fractionalQuantity;
    }

    public ListItemSource fractionalQuantity(FractionType fractionalQuantity) {
        this.fractionalQuantity = fractionalQuantity;
        return this;
    }

    public Long getUnitId() {
        return unitId;
    }

    public ListItemSource unitId(Long unitId) {
        this.unitId = unitId;
        return this;
    }

    public String getMarker() {
        return marker;
    }

    public ListItemSource marker(String marker) {
        this.marker = marker;
        return this;
    }

    public String getUnitSize() {
        return unitSize;
    }

    public ListItemSource unitSize(String unitSize) {
        this.unitSize = unitSize;
        return this;
    }
}
