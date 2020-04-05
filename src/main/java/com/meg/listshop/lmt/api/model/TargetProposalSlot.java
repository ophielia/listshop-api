package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by margaretmartin on 05/01/2018.
 */
public class TargetProposalSlot {

    List<TargetProposalDish> dishSlotList;
    private Long slotId;
    private Tag slotDishTag;
    private Integer slotOrder;
    private List<Tag> tags;

    private Integer selectedDishIndex;
    private Long selectedDishId;


    public TargetProposalSlot(Long slotId) {
        this.slotId = slotId;
    }


    public TargetProposalSlot slotDishTag(Tag slotDishTag) {
        this.slotDishTag = slotDishTag;
        return this;
    }

    public TargetProposalSlot slotOrder(Integer slotOrder) {
        this.slotOrder = slotOrder;
        return this;
    }

    public TargetProposalSlot tags(List<Tag> tags) {
        this.tags = tags;
        return this;
    }

    public TargetProposalSlot dishSlotList(List<TargetProposalDish> dishSlotList) {
        this.dishSlotList = dishSlotList;
        return this;
    }

    public TargetProposalSlot selectedDishIndex(Integer selectedDishIndex) {
        this.selectedDishIndex = selectedDishIndex;
        return this;
    }

    @JsonProperty("slot_id")
    public Long getSlotId() {
        return slotId;
    }


    @JsonProperty("slot_dish_tag")
    public Tag getSlotDishTag() {
        return slotDishTag;
    }

    @JsonProperty("slot_order")
    public Integer getSlotOrder() {
        return slotOrder;
    }

    public List<Tag> getTags() {
        return tags;
    }

    @JsonProperty("dish_slot_list")
    public List<TargetProposalDish> getDishSlotList() {
        return dishSlotList;
    }

    @JsonProperty("selected_dish_index")
    public Integer getSelectedDishIndex() {
        return selectedDishIndex;
    }

    public TargetProposalSlot selectedDishId(Long selectedDishId) {
        this.selectedDishId = selectedDishId;
        return this;
    }
}
