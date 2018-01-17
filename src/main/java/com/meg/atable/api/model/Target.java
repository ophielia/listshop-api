package com.meg.atable.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by margaretmartin on 10/06/2017.
 */
public class Target {


    private Long targetId;

    private Long userId;


    private String targetName;

    private List<TargetSlot> slots = new ArrayList<>();

    private Date created;

    private Date lastUsed;

    private Long proposalId;

    private List<Tag> targetTags;

    public Target() {
        // empty for json constructor
    }

    public Target(Long targetId) {
        this.targetId = targetId;
    }

    @JsonProperty(value = "target_id")
    public Long getTargetId() {
        return targetId;
    }

    public Target targetId(Long targetId) {
        this.targetId = targetId;
        return this;
    }

    @JsonProperty(value = "user_id")
    public Long getUserId() {
        return userId;
    }

    public Target userId(Long userId) {
        this.userId = userId;
        return this;
    }

    @JsonProperty("target_name")
    public String getTargetName() {
        return targetName;
    }

    public Target targetName(String targetName) {
        this.targetName = targetName;
        return this;
    }

    @JsonProperty("target_slots")
    public List<TargetSlot> getSlots() {
        return slots;
    }

    public Target slots(List<TargetSlot> tags) {
        this.slots = tags;
        return this;
    }

    public Date getCreated() {
        return created;
    }

    public Target created(Date created) {
        this.created = created;
        return this;
    }

    @JsonProperty("last_used")
    public Date getLastUsed() {
        return lastUsed;
    }

    public Target lastUsed(Date lastUsed) {
        this.lastUsed = lastUsed;
        return this;
    }

    public List<Tag> getTargetTags() {
        return targetTags;
    }

    @JsonProperty("target_tags")
    public Target targetTags(List<Tag> targetTags) {
        this.targetTags = targetTags;
        return this;
    }

    public Long getProposalId() {
        return proposalId;
    }

    @JsonProperty("proposal_id")
    public Target proposalId(Long proposalId) {
        this.proposalId = proposalId;
        return this;
    }
}
