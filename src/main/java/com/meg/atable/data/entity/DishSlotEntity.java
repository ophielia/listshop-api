package com.meg.atable.data.entity;

import com.sun.javafx.scene.control.behavior.OptionalBoolean;

/**
 * Created by margaretmartin on 23/05/2018.
 */
public class DishSlotEntity {
    private Long dishId;
    private String matchedTagIds;
    private ProposalSlotEntity slot;

    public Long getDishId() {
        return dishId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    public String getMatchedTagIds() {
        return matchedTagIds;
    }

    public void setMatchedTagIds(String matchedTagIds) {
        this.matchedTagIds = matchedTagIds;
    }

    public void setSlot(ProposalSlotEntity slot) {
        this.slot = slot;
    }

    public ProposalSlotEntity getSlot() {
        return slot;
    }
}
