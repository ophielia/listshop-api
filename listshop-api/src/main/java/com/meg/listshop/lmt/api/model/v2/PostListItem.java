/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.api.model.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "tag_id", "name" })
public class PostListItem {

    @JsonProperty("tag_id")
    private String tagId;

    @JsonProperty("amount")
    private Amount amount;

    @JsonProperty("raw_entry")
            private String rawEntry;

    PostListItem() {
    }

    public PostListItem(String tagId, Amount amount) {
        this.tagId = tagId;
        this.amount = amount;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public String getRawEntry() {
        return rawEntry;
    }

    public void setRawEntry(String rawEntry) {
        this.rawEntry = rawEntry;
    }

    @Override
    public String toString() {
        return "PostListItem{" +
                "tagId='" + tagId + '\'' +
                ", amount=" + amount +
                ", rawEntry=" + rawEntry +
                '}';
    }
}

