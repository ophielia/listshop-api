package com.meg.atable.data.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 04/01/2018.
 */
@Entity
@Table(name = "target_proposal_dish")
@GenericGenerator(
        name = "target_proposal_dish_sequence",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@org.hibernate.annotations.Parameter(
                name = "sequence_name",
                value="target_proposal_dish_sequence"),
                @org.hibernate.annotations.Parameter(
                        name = "increment_size",
                        value="1")}
)
public class TargetProposalDishEntity extends AbstractInflateAndFlatten {


    @Id
    @GeneratedValue( strategy=GenerationType.SEQUENCE, generator="target_proposal_dish_sequence")
    private Long proposalDishId;

    private Long dishId;

    private String matchedTagIds;

    @Transient
    private DishEntity dish;

    @Transient
    private List<TagEntity> matchedTags;

    @ManyToOne
    private TargetProposalSlotEntity targetProposalSlot;

    public TargetProposalDishEntity() {
    }

    public Long getProposalDishId() {
        return proposalDishId;
    }

    public void setProposalDishId(Long proposalDishId) {
        this.proposalDishId = proposalDishId;
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

    public void setTargetProposalSlot(TargetProposalSlotEntity targetProposalSlot) {
        this.targetProposalSlot = targetProposalSlot;
    }

    public TargetProposalSlotEntity getTargetProposalSlot() {
        return targetProposalSlot;
    }

    public void fillInTags(Map<Long, TagEntity> dictionary) {

        if (dictionary.isEmpty()) {
            return;
        }
        matchedTags =inflateStringToList(getMatchedTagIds()).stream()
                .filter(t -> dictionary.containsKey(new Long(t)))
                .map( t -> dictionary.get(new Long(t)))
                .collect(Collectors.toList());


    }



}
