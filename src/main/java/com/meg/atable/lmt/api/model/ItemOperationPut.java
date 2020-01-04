package com.meg.atable.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ItemOperationPut {

    @JsonProperty("operation")
    private String operation;

    @JsonProperty("tag_ids")
    private List<Long> tagIds;


    @JsonProperty("destination_list_id")
    private Long destinationListId;

    public ItemOperationPut() {
    }

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }

    public Long getDestinationListId() {
        return destinationListId;
    }

    public void setDestinationListId(Long destinationListId) {
        this.destinationListId = destinationListId;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    @Override
    public String toString() {
        return "ItemOperationPut{" +
                "operation='" + operation + '\'' +
                ", tagIds=" + tagIds +
                ", destinationListId=" + destinationListId +
                '}';
    }
}