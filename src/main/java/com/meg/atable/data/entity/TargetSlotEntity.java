package com.meg.atable.data.entity;

import com.meg.atable.service.TargetService;
import com.meg.atable.service.TargetServiceConstants;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="Target")
@Table(name = "target_slot")
public class TargetSlotEntity {

    @Id
    @GeneratedValue
    private Long targetSlotId;

    private Long targetId;

    private Long slotDishTagId;

    @Transient
    private TagEntity slotDishTag;

    private String targetTagIds;

    private Integer slotOrder;

    @Transient
    private List<TagEntity> tags;

    public TargetSlotEntity() {
        // for JPA
    }

    public TargetSlotEntity(Long targetSlotId) {
        this.targetSlotId = targetSlotId;
    }

    public Long getId() {
        return targetSlotId;
    }

    public void setId(Long targetSlotId) {
        this.targetSlotId = targetSlotId;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public Long getSlotDishTagId() {
        return slotDishTagId;
    }

    public void setSlotDishTagId(Long slotDishTagId) {
        this.slotDishTagId = slotDishTagId;
    }

    public String getTargetTagIds() {
        return targetTagIds;
    }

    public void setTargetTagIds(String targetTagIds) {
        this.targetTagIds = targetTagIds;
    }

    public Integer getSlotOrder() {
        return slotOrder;
    }

    public void setSlotOrder(Integer slotOrder) {
        this.slotOrder = slotOrder;
    }

    public List<TagEntity> getTags() {
        return tags;
    }

    public void setTags(List<TagEntity> tags) {
        this.tags = tags;
    }

    public TagEntity getSlotDishTag() {
        return slotDishTag;
    }

    public void setSlotDishTag(TagEntity slotDishTag) {
        this.slotDishTag = slotDishTag;
    }

    public void addTagId(Long tagId) {
        List<String> tagids = getTagIdsAsList();
        tagids.add(tagId.toString());
        targetTagIds = flattenListToString(tagids);
    }

    public void removeTagId(Long tagId) {
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
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(targetTagIds.split(TargetServiceConstants.TARGET_TAG_DELIMITER)));
    }

    private String flattenListToString(List<String> list) {
        return String.join(TargetServiceConstants.TARGET_TAG_DELIMITER,list);
    }

    public void fillInTags(Map<Long, TagEntity> dictionary) {
        if (dictionary.isEmpty()) {
            return;
        }
        tags =getTagIdsAsList().stream()
                .filter(t -> dictionary.containsKey(new Long(t)))
                .map( t -> dictionary.get(new Long(t)))
                .collect(Collectors.toList());

        if (slotDishTagId!=null) {
            if (dictionary.containsKey(slotDishTagId)) {
                slotDishTag = dictionary.get(slotDishTagId);
            }
        }
        return;

    }
}