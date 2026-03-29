/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.api.model.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShoppingListItem {

    @JsonProperty("item_id")
    private String itemId;

    private NestedTag tag;

    private Amount amount;

    @JsonProperty("added")
    private Date addedOn;

    @JsonProperty("removed")
    private Date removed;

    @JsonProperty("updated")
    private Date updated;

    @JsonProperty("crossed_off")
    private Date crossedOff;

    @JsonProperty("list_id")
    private String listId;

    @JsonProperty("used_count")
    private Integer usedCount;

    @JsonProperty("sources")
    private Set<String> sources;

    @JsonProperty("amount_type")
    private String amountType;

    private List<ShoppingListItemDetails> details;

    public ShoppingListItem(Long id) {
        this.itemId = String.valueOf(id);
    }

    public ShoppingListItem() {
        // necessary for json construction
    }

    public ShoppingListItem withItemId(String itemId) {
        this.itemId = itemId;
        return this;
    }

    public ShoppingListItem withTag(NestedTag tag) {
        this.tag = tag;
        return this;
    }

    public ShoppingListItem withAmount(Amount amount) {
        this.amount = amount;
        return this;
    }

    public ShoppingListItem withAddedOn(Date addedOn) {
        this.addedOn = addedOn;
        return this;
    }

    public ShoppingListItem withRemoved(Date removed) {
        this.removed = removed;
        return this;
    }

    public ShoppingListItem withUpdated(Date updated) {
        this.updated = updated;
        return this;
    }

    public ShoppingListItem withCrossedOff(Date crossedOff) {
        this.crossedOff = crossedOff;
        return this;
    }

    public ShoppingListItem withListId(String listId) {
        this.listId = listId;
        return this;
    }

    public ShoppingListItem withUsedCount(Integer usedCount) {
        this.usedCount = usedCount;
        return this;
    }

    public ShoppingListItem withDetails(List<ShoppingListItemDetails> details) {
        this.details = details;
        return this;
    }

    public ShoppingListItem withSources(Set<String> sources) {
        this.sources = sources;
        return this;
    }

    public ShoppingListItem withAmountType(String amountType) {
        this.amountType = amountType;
        return this;
    }
    public String getItemId() {
        return itemId;
    }

    public NestedTag getTag() {
        return tag;
    }

    public Amount getAmount() {
        return amount;
    }

    public Date getAddedOn() {
        return addedOn;
    }

    public Date getRemoved() {
        return removed;
    }

    public Date getUpdated() {
        return updated;
    }

    public Date getCrossedOff() {
        return crossedOff;
    }

    public String getListId() {
        return listId;
    }

    public Integer getUsedCount() {
        return usedCount;
    }

    public Set<String> getSources() {
        return sources;
    }

    public String getAmountType() {
        return amountType;
    }

    public List<ShoppingListItemDetails> getDetails() {
        return details;
    }

    @Override
    public String toString() {
        return "ShoppingListItem{" +
                "itemId='" + itemId + '\'' +
                ", tag=" + tag +
                ", amount=" + amount +
                ", addedOn=" + addedOn +
                ", removed=" + removed +
                ", updated=" + updated +
                ", crossedOff=" + crossedOff +
                ", listId='" + listId + '\'' +
                ", usedCount=" + usedCount +
                ", details=" + details +
                ", amountType=" + amountType +
                '}';
    }
}
