package com.meg.atable.data.entity;

import com.sun.javafx.scene.control.behavior.OptionalBoolean;

import java.beans.beancontext.BeanContextMembershipEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by margaretmartin on 23/05/2018.
 */
public class ProposalSlotEntity {
    private Integer slotNumber;
    private ProposalEntity proposal;
    private List<DishSlotEntity> dishSlots;
    private Long pickedDishId;

    public Integer getSlotNumber() {
        return slotNumber;
    }

    public void setProposal(ProposalEntity proposal) {
        this.proposal = proposal;
    }

    public ProposalEntity getProposal() {
        return proposal;
    }

    public List<DishSlotEntity> getDishSlots() {
        return dishSlots!=null?dishSlots:new ArrayList<>();
    }

    public void setDishSlots(List<DishSlotEntity> dishSlots) {
        this.dishSlots = dishSlots;
    }

    public Long getPickedDishId() {
        return pickedDishId;
    }

    public void setPickedDishId(Long pickedDishId) {
        this.pickedDishId = pickedDishId;
    }
}
