package com.meg.atable.data.entity;

import com.meg.atable.service.TargetServiceConstants;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 01/01/2018.
 */
@Entity
@Table(name="target_proposal")
public class TargetProposalEntity extends AbstractInflateAndFlatten {
    @Id
    @GeneratedValue
    private Long proposalId;

    private Long userId;

    private String targetName;


    private Date created;

    private Date lastUsed;

    private Date lastUpdated;

    private String targetTagIds;

    @Transient
    private List<TagEntity> targetTags;

    private Long forTargetId;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<TargetProposalSlotEntity> proposalSlots;

    private Boolean regenerateOnRefresh;

    private Integer currentProposalIndex;

    private String slotSortOrder;

    private String proposalIndexList;

    public TargetProposalEntity(TargetEntity target) {
        this.forTargetId = target.getTargetId();
        this.setCreated(new Date());
        this.setUserId(target.getUserId());
        this.setTargetName(target.getTargetName());
        this.setTargetTagIds(target.getTargetTagIds());
        this.proposalSlots = new ArrayList<>();
    }

    public TargetProposalEntity() {
    }

    public Long getProposalId() {
        return proposalId;
    }

    public void setProposalId(Long proposalId) {
        this.proposalId = proposalId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }


    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(Date lastUsed) {
        this.lastUsed = lastUsed;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getTargetTagIds() {
        return targetTagIds;
    }

    public void setTargetTagIds(String targetTagIds) {
        this.targetTagIds = targetTagIds;
    }

    public Long getForTargetId() {
        return forTargetId;
    }

    public void setForTargetId(Long forTargetId) {
        this.forTargetId = forTargetId;
    }

    public List<TargetProposalSlotEntity> getProposalSlots() {
        return proposalSlots;
    }

    public void setProposalSlots(List<TargetProposalSlotEntity> proposalSlots) {
        this.proposalSlots = proposalSlots;
    }

    public Boolean getRegenerateOnRefresh() {
        return regenerateOnRefresh;
    }

    public void setRegenerateOnRefresh(Boolean regenerateOnRefresh) {
        this.regenerateOnRefresh = regenerateOnRefresh;
    }

    public Integer getCurrentProposalIndex() {
        return currentProposalIndex;
    }

    public void setCurrentProposalIndex(Integer currentProposalIndex) {
        this.currentProposalIndex = currentProposalIndex;
    }

    public String getSlotSortOrder() {
        return slotSortOrder;
    }

    public void setSlotSortOrder(String slotSortOrder) {
        this.slotSortOrder = slotSortOrder;
    }

    public String getProposalIndexList() {
        return proposalIndexList;
    }

    public void setProposalIndexList(String proposalIndexList) {
        this.proposalIndexList = proposalIndexList;
    }

    public List<TagEntity> getTargetTags() {
        return targetTags;
    }

    public void setTargetTags(List<TagEntity> targetTags) {
        this.targetTags = targetTags;
    }

    public void addSlot(TargetProposalSlotEntity proposalSlotEntity) {
        proposalSlots.add(proposalSlotEntity);
    }

    public List<Long> getAllTagIds() {
        // make list of all tagList strings for target and contained slots
        // also include dish type tags
        List<String> stringList = new ArrayList<>();
        stringList.addAll(inflateStringToList(getTargetTagIds(), TargetServiceConstants.TARGET_TAG_DELIMITER));
        if (proposalSlots != null && !proposalSlots.isEmpty()) {
            for (TargetProposalSlotEntity slot : proposalSlots) {
                stringList.addAll(inflateStringToList(slot.getTargetTagIds()));
                if (slot.getSlotDishTagId() != null) {
                    stringList.add(slot.getSlotDishTagId().toString());
                }
            }
        }

        // convert list of strings to list of longs and return
        if (!stringList.isEmpty()) {
            return stringList.stream()
                    .map(Long::new)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public void fillInAllTags(Map<Long, TagEntity> dictionary) {
        if (dictionary.isEmpty()) {
            return;
        }
        targetTags =inflateStringToList(getTargetTagIds()).stream()
                .filter(t -> dictionary.containsKey(new Long(t)))
                .map( t -> dictionary.get(new Long(t)))
                .collect(Collectors.toList());

        if (proposalSlots != null && !proposalSlots.isEmpty()) {
            for (TargetProposalSlotEntity slot : proposalSlots) {
                slot.fillInTags(dictionary);
            }
        }
        return;
    }

    public List<Long> getAllDishIds() {
        // make list of all dish strings for target and contained slots
        // also include dish type tags
        List<Long> dishIdList = new ArrayList<>();
        if (proposalSlots != null && !proposalSlots.isEmpty()) {
            for (TargetProposalSlotEntity dishslot : proposalSlots) {
                dishIdList.addAll(dishslot.getAllDishIds());
            }
        }

        return dishIdList;
    }

    public void fillInAllDishes(Map<Long, DishEntity> dictionary) {
        if (dictionary.isEmpty()) {
            return;
        }
        if (proposalSlots != null && !proposalSlots.isEmpty()) {
            for (TargetProposalSlotEntity slot : proposalSlots) {
                slot.fillInDishes(dictionary);
            }
        }
        return;
    }

}
