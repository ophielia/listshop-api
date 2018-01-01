package com.meg.atable.data.entity;

import com.meg.atable.service.TargetServiceConstants;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "target")
public class TargetEntity {

    @Id
    @GeneratedValue
    private Long targetId;

    private Long userId;

    private String targetName;


    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<TargetSlotEntity> slots = new ArrayList<>();

    private Date created;

    private Date lastUsed;

    private String targetTagIds;

    @Transient
    private List<TagEntity> targetTags;

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
        List<String> tagids = getTagIdsAsList();
        tagids.add(tagId.toString());
        targetTagIds = flattenListToString(tagids);
    }

    public void removeTargetTagId(Long tagId) {
        if (targetTagIds == null || targetTagIds.isEmpty()) {
            return;
        }

        List<String> tagids = getTagIdsAsList();
        tagids = tagids.stream()
                .filter(t -> !t.equals(tagId.toString()))
                .collect(Collectors.toList());
        targetTagIds = flattenListToString(tagids);
    }

    public List<String> getTagIdsAsList() {
        if (targetTagIds == null || targetTagIds.isEmpty()) {
            return new ArrayList<String>();
        }

        List<String> idList = new ArrayList<>();
idList.addAll(Arrays.asList(targetTagIds.split(TargetServiceConstants.TARGET_TAG_DELIMITER)));
        return idList;
    }

    private String flattenListToString(List<String> list) {
        return String.join(TargetServiceConstants.TARGET_TAG_DELIMITER, list);
    }

    public List<TagEntity> getTargetTags() {
        return targetTags;
    }

    public void setTargetTags(List<TagEntity> tags) {
        targetTags = tags;
    }

    public List<Long> getAllTagIds() {
        // make list of all tagList strings for target and contained slots
        // also include dish type tags
        List<String> stringList = new ArrayList<>();
        stringList.addAll(getTagIdsAsList());
        if (slots != null && !slots.isEmpty()) {
            for (TargetSlotEntity slot : slots) {
                stringList.addAll(slot.getTagIdsAsList());
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
        targetTags =getTagIdsAsList().stream()
                .filter(t -> dictionary.containsKey(new Long(t)))
                .map( t -> dictionary.get(new Long(t)))
                .collect(Collectors.toList());

        if (slots != null && !slots.isEmpty()) {
            for (TargetSlotEntity slot : slots) {
                slot.fillInTags(dictionary);
            }
        }
        return;
    }


}