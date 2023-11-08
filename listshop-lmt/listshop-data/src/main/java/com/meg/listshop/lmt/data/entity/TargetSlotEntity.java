package com.meg.listshop.lmt.data.entity;

import com.meg.listshop.lmt.data.pojos.TargetServiceConstants;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Table(name = "target_slot")
@GenericGenerator(
        name = "target_slot_sequence",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@org.hibernate.annotations.Parameter(
                name = "sequence_name",
                value = "target_slot_sequence"),
                @org.hibernate.annotations.Parameter(
                        name = "increment_size",
                        value = "2")}
)
public class TargetSlotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "target_slot_sequence")
    private Long targetSlotId;

    @Column(name = "target_id")
    private Long targetId;

    private Long slotDishTagId;

    @Transient
    private TagEntity slotDishTag;

    private String targetTagIds;

    private Integer slotOrder;

    @Transient
    private List<TagEntity> tags;

    public final static String IDENTIFIER = "TargetSlotEntity";

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
                .filter(t -> !t.equals(String.valueOf(tagId)))
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
        return String.join(TargetServiceConstants.TARGET_TAG_DELIMITER, list);
    }

    void fillInTags(Map<Long, TagEntity> dictionary) {
        if (dictionary.isEmpty()) {
            return;
        }
        tags = getTagIdsAsList().stream()
                .filter(t -> dictionary.containsKey(Long.valueOf(t)))
                .map(t -> dictionary.get(Long.valueOf(t)))
                .collect(Collectors.toList());

        if (slotDishTagId != null && dictionary.containsKey(slotDishTagId)) {
            slotDishTag = dictionary.get(slotDishTagId);

        }

    }
}