package com.meg.atable.data.entity;

import com.meg.atable.service.TargetServiceConstants;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "Target")
@Table(name = "target")
public class TargetEntity extends AbstractInflateAndFlatten {

    @Id
    @GeneratedValue
    private Long targetId;

    private Long userId;

    private String targetName;


    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<TargetSlotEntity> slots = new ArrayList<>();

    private Date created;

    private Date lastUsed;

    private Date lastUpdated;

    private String targetTagIds;

    @Transient
    private List<TagEntity> targetTags;

    private Long proposalId;

    public TargetEntity() {
    }

    public TargetEntity(Long targetId) {
        this.targetId = targetId;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
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

    public List<TargetSlotEntity> getSlots() {
        return slots;
    }

    public void setSlots(List<TargetSlotEntity> slots) {
        this.slots = slots;
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

    public String getTargetTagIds() {
        return targetTagIds;
    }

    public void setTargetTagIds(String targetTagIds) {
        this.targetTagIds = targetTagIds;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void addSlot(TargetSlotEntity slot) {
        if (slots == null) {
            slots = new ArrayList<TargetSlotEntity>();
        }
        slots.add(slot);

    }

    public void removeSlot(TargetSlotEntity targetSlotEntity) {
        if (slots == null) {
            return;
        }
        slots = slots.stream()
                .filter(t -> t.getId().longValue() != targetSlotEntity.getId().longValue())
                .collect(Collectors.toList());
    }

    public void addTargetTagId(Long tagId) {
        List<String> tagids = inflateStringToList(getTargetTagIds(), TargetServiceConstants.TARGET_TAG_DELIMITER);
        tagids.add(tagId.toString());
        targetTagIds = flattenListToString(tagids);
    }

    public void removeTargetTagId(Long tagId) {
        if (targetTagIds == null || targetTagIds.isEmpty()) {
            return;
        }

        List<String> tagids = inflateStringToList(getTargetTagIds());
        tagids = tagids.stream()
                .filter(t -> !t.equals(tagId.toString()))
                .collect(Collectors.toList());
        targetTagIds = flattenListToString(tagids);
    }

    public Set<String> getTagIdsAsSet() {
        return inflateStringToSet(getTargetTagIds());
    }

    public List<TagEntity> getTargetTags() {
        return targetTags;
    }

    public void setTargetTags(List<TagEntity> tags) {
        targetTags = tags;
    }

    public Set<Long> getAllTagIds() {
        // make list of all tagList strings for target and contained slots
        // also include dish type tags
        List<String> stringList = new ArrayList<>();
        stringList.addAll(inflateStringToList(getTargetTagIds(), TargetServiceConstants.TARGET_TAG_DELIMITER));
        if (slots != null && !slots.isEmpty()) {
            for (TargetSlotEntity slot : slots) {
                stringList.addAll(inflateStringToList(slot.getTargetTagIds(), TargetServiceConstants.TARGET_TAG_DELIMITER));
                if (slot.getSlotDishTagId() != null) {
                    stringList.add(slot.getSlotDishTagId().toString());
                }
            }
        }

        // convert list of strings to list of longs and return
        if (!stringList.isEmpty()) {
            return stringList.stream()
                    .map(Long::new)
                    .collect(Collectors.toSet());
        }
        return new HashSet<>();
    }

    public void fillInAllTags(Map<Long, TagEntity> dictionary) {
        if (dictionary.isEmpty()) {
            return;
        }
        targetTags = inflateStringToList(getTargetTagIds()).stream()
                .filter(t -> dictionary.containsKey(new Long(t)))
                .map(t -> dictionary.get(new Long(t)))
                .collect(Collectors.toList());

        if (slots != null && !slots.isEmpty()) {
            for (TargetSlotEntity slot : slots) {
                slot.fillInTags(dictionary);
            }
        }
        return;
    }

    public Long getProposalId() {
        return proposalId;
    }

    public void setProposalId(Long proposalId) {
        this.proposalId = proposalId;
    }
}