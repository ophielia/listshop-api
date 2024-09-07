package com.meg.listshop.lmt.data.entity;

import com.meg.listshop.common.FlatStringUtils;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Table(name = "proposal_dish")
public class DishSlotEntity {
    @Id
    @Tsid
    private Long dishSlotId;

    private Long dishId;

    private String matchedTagIds;

    @ManyToOne
    @JoinColumn(name = "slot_id", nullable = false)
    private ProposalSlotEntity slot;
    @Transient
    private DishEntity dish;
    @Transient
    private List<TagEntity> matchedTags;

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

    public ProposalSlotEntity getSlot() {
        return slot;
    }

    public void setSlot(ProposalSlotEntity slot) {
        this.slot = slot;
    }

    public void setProposalSlot(ProposalSlotEntity proposalSlot) {
        this.slot = proposalSlot;
    }

    public DishEntity getDish() {
        return dish;
    }

    public void setDish(DishEntity dish) {
        this.dish = dish;
    }

    public List<TagEntity> getMatchedTags() {
        return matchedTags;
    }

    public void setMatchedTags(List<TagEntity> matchedTags) {
        this.matchedTags = matchedTags;
    }

    public void fillInTags(Map<Long, TagEntity> dictionary) {

        if (dictionary.isEmpty()) {
            return;
        }
        matchedTags = FlatStringUtils.inflateStringToList(getMatchedTagIds(), ";").stream()
                .filter(t -> dictionary.containsKey(Long.valueOf(t)))
                .map(t -> dictionary.get(Long.valueOf(t)))
                .collect(Collectors.toList());


    }
}
