package com.meg.atable.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MergeRequest {

    @JsonProperty("list_id")
    private Long listId;

    @JsonProperty("last_changed")
    private LocalDate lastChanged;

    @JsonProperty("layout_id")
    private Long layoutId;

    @JsonProperty("merge_items")
    private List<Item> mergeItems = new ArrayList<>();

    public Long getListId() {
        return listId;
    }

    public void setListId(Long listId) {
        this.listId = listId;
    }

    public LocalDate getLastChanged() {
        return lastChanged;
    }

    public void setLastChanged(LocalDate lastChanged) {
        this.lastChanged = lastChanged;
    }

    public Long getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(Long layoutId) {
        this.layoutId = layoutId;
    }

    public List<Item> getMergeItems() {
        return mergeItems;
    }

    public void setMergeItems(List<Item> mergeItems) {
        this.mergeItems = mergeItems;
    }
}
