package com.meg.listshop.lmt.api.model.v2;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Ingredient {

    @JsonProperty("item_id")
    private String itemId;
    private NestedTag tag;
    private Amount amount;
    private String display;

    public Ingredient() {
    }

    public Ingredient withItemId(String itemId) {
        this.itemId = itemId;
        return this;
    }

    public Ingredient withTag(NestedTag tag) {
        this.tag = tag;
        return this;
    }

    public Ingredient withAmount(Amount amount) {
        this.amount = amount;
        return this;
    }

    public Ingredient withDisplay(String display) {
        this.display = display;
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

    public String getDisplay() {
        return display;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "itemId='" + itemId + '\'' +
                ", tag=" + tag +
                ", amount=" + amount +
                ", display='" + display + '\'' +
                '}';
    }
}
