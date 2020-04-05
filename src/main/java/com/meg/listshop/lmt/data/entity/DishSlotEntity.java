package com.meg.listshop.lmt.data.entity;

import com.meg.listshop.common.FlatStringUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Table(name = "proposal_dish")
@GenericGenerator(
        name = "proposal_dish_sequence",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@org.hibernate.annotations.Parameter(
                name = "sequence_name",
                value = "proposal_dish_sequence"),
                @org.hibernate.annotations.Parameter(
                        name = "increment_size",
                        value = "1")}
)
public class DishSlotEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proposal_dish_sequence")
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
                .filter(t -> dictionary.containsKey(new Long(t)))
                .map(t -> dictionary.get(new Long(t)))
                .collect(Collectors.toList());


    }
}
