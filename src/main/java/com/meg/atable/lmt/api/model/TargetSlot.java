package com.meg.atable.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by margaretmartin on 10/06/2017.
 */
public class TargetSlot {
    private Long targetSlotId;

    private Long targetId;

    private Long slotDishTagId;

    private Tag slotDishTag;

    private List<Tag> slotTags;

    private Integer slotOrder;

    public TargetSlot() {
        // for JPA
    }

    public TargetSlot(Long targetSlotId) {
        this.targetSlotId = targetSlotId;
    }

    public Long getTargetSlotId() {
        return targetSlotId;
    }

    @JsonProperty("target_slot_id")
    public TargetSlot targetSlotId(Long targetSlotId) {
        this.targetSlotId = targetSlotId;
        return this;
    }

    public Long getTargetId() {
        return targetId;
    }

    @JsonProperty("target_id")
    public TargetSlot targetId(Long targetId) {
        this.targetId = targetId;
        return this;
    }

    @JsonProperty("slot_dish_tag_id")
    public Long getSlotDishTagId() {
        return slotDishTagId;
    }

    public TargetSlot slotDishTagId(Long slotDishTagId) {
        this.slotDishTagId = slotDishTagId;
        return this;
    }

    @JsonProperty("slot_tags")
    public List<Tag> getSlotTags() {
        return slotTags;
    }

    public TargetSlot slotTags(List<Tag> slotTags) {
        this.slotTags = slotTags;
        return this;
    }

    @JsonProperty("slot_order")
    public Integer getSlotOrder() {
        return slotOrder;
    }

    public TargetSlot slotOrder(Integer slotOrder) {
        this.slotOrder = slotOrder;
        return this;
    }

    @JsonProperty("slot_dish_tag")
    public Tag getSlotDishTag() {
        return slotDishTag;
    }

    public TargetSlot slotDishTag(Tag slotDishTag) {
        this.slotDishTag = slotDishTag;
        return this;
    }
}
