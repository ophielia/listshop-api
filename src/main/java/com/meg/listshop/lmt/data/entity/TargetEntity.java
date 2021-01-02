package com.meg.listshop.lmt.data.entity;

import com.meg.listshop.lmt.api.model.TargetType;
import com.meg.listshop.lmt.service.TargetServiceConstants;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "target")
@GenericGenerator(
        name = "target_sequence",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@org.hibernate.annotations.Parameter(
                name = "sequence_name",
                value = "target_sequence"),
                @org.hibernate.annotations.Parameter(
                        name = "increment_size",
                        value = "1")}
)
public class TargetEntity extends AbstractInflateAndFlatten {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "target_sequence")
    private Long targetId;

    private Long userId;

    private String targetName;

    @Enumerated(EnumType.STRING)
    private TargetType targetType;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "target_id")
    private List<TargetSlotEntity> slots = new ArrayList<>();

    private Date created;

    private Date lastUsed;

    private Date lastUpdated;

    private LocalDateTime expires;

    private String targetTagIds;

    @Transient
    private List<TagEntity> targetTags;

    private Long proposalId;

    public TargetEntity() {
        // for jpa
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
            slots = new ArrayList<>();
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
                .filter(t -> dictionary.containsKey(Long.valueOf(t)))
                .map(t -> dictionary.get(Long.valueOf(t)))
                .collect(Collectors.toList());

        if (slots != null && !slots.isEmpty()) {
            for (TargetSlotEntity slot : slots) {
                slot.fillInTags(dictionary);
            }
        }
    }

    public Long getProposalId() {
        return proposalId;
    }

    public void setProposalId(Long proposalId) {
        this.proposalId = proposalId;
    }

    public LocalDateTime getExpires() {
        return expires;
    }

    public void setExpires(LocalDateTime expires) {
        this.expires = expires;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(TargetType targetType) {
        this.targetType = targetType;
    }

    public String getContentHashCode() {
// this is used to decide if the tags for this target have changed since the last proposal was run
        // this is used to decide if the slots have changed since the last proposal has been run
        // slots changed == different picked dishes
        StringBuilder hashCode = new StringBuilder(getTargetId().hashCode());
        // make list of all tagList strings for target and contained slots
        // also include dish type tags
        List<String> targetTags = inflateStringToList(getTargetTagIds(), TargetServiceConstants.TARGET_TAG_DELIMITER);
        if (targetTags != null) {
            targetTags.forEach(t -> hashCode.append(t.hashCode()));
        }

        if (slots != null && !slots.isEmpty()) {
            for (TargetSlotEntity slot : slots) {
                List<String> slotTags = inflateStringToList(slot.getTargetTagIds(), TargetServiceConstants.TARGET_TAG_DELIMITER);
                if (slotTags != null) {
                    int slotHashCode = (slot.getSlotOrder() * slotTags.hashCode());
                    hashCode.append(slotHashCode);
                }
                if (slot.getSlotDishTagId() != null) {
                    hashCode.append(slot.getSlotDishTagId().hashCode());
                }
            }
        }
        int finalCode = hashCode.toString().hashCode();
        return String.valueOf(finalCode);
    }


}