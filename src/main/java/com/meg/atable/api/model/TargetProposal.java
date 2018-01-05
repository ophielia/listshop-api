package com.meg.atable.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meg.atable.data.entity.TagEntity;

import java.util.Date;
import java.util.List;

/**
 * Created by margaretmartin on 10/06/2017.
 */
public class TargetProposal {

    private Long proposalId;

    private Long userId;

    private String targetName;


    private Date created;

    private Date lastUsed;


    private List<Tag> targetTags;


    private List<TargetProposalSlot> proposalSlots;

    public TargetProposal(Long proposalId) {
        this.proposalId = proposalId;
    }

    @JsonProperty("proposal_id")
    public Long getProposalId() {
        return proposalId;
    }

    @JsonProperty("user_id")
    public Long getUserId() {
        return userId;
    }

    public TargetProposal userId(Long userId) {
        this.userId = userId;
        return this;
    }

    @JsonProperty("target_name")
    public String getTargetName() {
        return targetName;
    }

    public TargetProposal targetName(String targetName) {
        this.targetName = targetName;
        return this;
    }

    public Date getCreated() {
        return created;
    }

    public TargetProposal created(Date created) {
        this.created = created;
        return this;
    }

    public Date getLastUsed() {
        return lastUsed;
    }

    @JsonProperty("last_used")
    public TargetProposal lastUsed(Date lastUsed) {
        this.lastUsed = lastUsed;
        return this;
    }

    @JsonProperty("target_tags")
    public List<Tag> getTargetTags() {
        return targetTags;
    }

    public TargetProposal targetTags(List<Tag> targetTags) {
        this.targetTags = targetTags;
        return this;
    }

    @JsonProperty("proposal_slots")
    public List<TargetProposalSlot> getProposalSlots() {
        return proposalSlots;
    }

    public TargetProposal proposalSlots(List<TargetProposalSlot> proposalSlots) {
        this.proposalSlots = proposalSlots;
        return this;
    }
}
