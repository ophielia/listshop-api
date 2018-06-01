package com.meg.atable.data.entity;

import com.sun.javafx.scene.control.behavior.OptionalBoolean;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "proposal_dish")
@GenericGenerator(
        name = "proposal_dish_sequence",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@org.hibernate.annotations.Parameter(
                name = "sequence_name",
                value="proposal_dish_sequence"),
                @org.hibernate.annotations.Parameter(
                        name = "increment_size",
                        value="1")}
)
public class DishSlotEntity {
    @Id
    private Long dishSlotId;

    private Long dishId;

    private String matchedTagIds;

    @ManyToOne
    @JoinColumn(name = "slot_id", nullable = false)
    private ProposalSlotEntity slot;
    @Transient
    private DishEntity dish;

    public Long getId() {
        return dishSlotId;
    }

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

    public void setProposalSlot(ProposalSlotEntity proposalSlot) {
        this.slot = proposalSlot;
    }

    public void setDish(DishEntity dish) {
        this.dish = dish;
    }

    public DishEntity getDish() {
        return dish;
    }
}
