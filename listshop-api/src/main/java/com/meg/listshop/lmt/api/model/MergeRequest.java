package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MergeRequest {

    @JsonProperty("list_id")
    private Long listId;

    @JsonProperty("last_changed")
    private Date lastChanged;

    @JsonProperty("last_offline_change")
    private Date lastOfflineChange;

    @JsonProperty("last_synced")
    private Date lastSynced;
    @JsonProperty("layout_id")
    private Long layoutId;

    @JsonProperty("merge_items")
    private List<Item> mergeItems = new ArrayList<>();

    @JsonProperty("check_tag_conflict")
    private boolean checkTagConflict;

    public MergeRequest() {
        // empty constructor for jackson
    }

    public Long getListId() {
        return listId;
    }

    public void setListId(Long listId) {
        this.listId = listId;
    }

    public Date getLastChanged() {
        return lastChanged;
    }

    public void setLastChanged(Date lastChanged) {
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

    public boolean isCheckTagConflict() {
        return checkTagConflict;
    }

    public void setCheckTagConflict(boolean checkTagConflict) {
        this.checkTagConflict = checkTagConflict;
    }

    public Date getLastOfflineChange() {
        return lastOfflineChange;
    }

    public void setLastOfflineChange(Date lastOfflineChange) {
        this.lastOfflineChange = lastOfflineChange;
    }

    public Date getLastSynced() {
        return lastSynced;
    }

    public void setLastSynced(Date lastSynced) {
        this.lastSynced = lastSynced;
    }
}
