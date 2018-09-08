package com.meg.atable.data.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Table(name = "proposal_slot")
@GenericGenerator(
        name = "proposal_slot_sequence",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@org.hibernate.annotations.Parameter(
                name = "sequence_name",
                value = "proposal_slot_sequence"),
                @org.hibernate.annotations.Parameter(
                        name = "increment_size",
                        value = "1")}
)
public class ProposalSlotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proposal_slot_sequence")
    private Long slotId;

    private Integer slotNumber;

    @ManyToOne
    @JoinColumn(name = "proposal_id", nullable = false)
    private ProposalEntity proposal;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "slot")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<DishSlotEntity> dishSlots;

    private Long pickedDishId;

    private Long slotDishTagId;

    private String flatMatchedTagIds;

    @Transient
    private List<String> matchedTagIds;

    @Transient
    private TagEntity slotDishTag;

    @Transient
    private List<TagEntity> tags;

    public Long getId() {
        return slotId;
    }

    public void setId(Long id) {
        this.slotId = id;
    }

    public Integer getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(Integer slotNumber) {
        this.slotNumber = slotNumber;
    }

    public void setProposal(ProposalEntity proposal) {
        this.proposal = proposal;
    }

    public ProposalEntity getProposal() {
        return proposal;
    }

    public List<DishSlotEntity> getDishSlots() {
        return dishSlots != null ? dishSlots : new ArrayList<>();
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

    public void setSlotDishTagId(Long slotDishTagId) {
        this.slotDishTagId = slotDishTagId;
    }

    public Long getSlotDishTagId() {
        return slotDishTagId;
    }

    public String getFlatMatchedTagIds() {
        return flatMatchedTagIds;
    }

    public void setFlatMatchedTagIds(String flatMatchedTagIds) {
        this.flatMatchedTagIds = flatMatchedTagIds;
    }

    public void setMatchedTagIds(List<String> matchedTagIds) {
        this.matchedTagIds = matchedTagIds;
    }

    public List<String> getMatchedTagIds() {
        return matchedTagIds;
    }

    public TagEntity getSlotDishTag() {
        return slotDishTag;
    }

    public void setSlotDishTag(TagEntity slotDishTag) {
        this.slotDishTag = slotDishTag;
    }

    public List<TagEntity> getTags() {
        return tags;
    }

    public void setTags(List<TagEntity> tags) {
        this.tags = tags;
    }

    public void fillInTags(List<String> tagIdsAsList, Map<Long, TagEntity> dictionary) {
        if (dictionary.isEmpty()) {
            return;
        }
        tags = tagIdsAsList.stream()
                .filter(t -> dictionary.containsKey(new Long(t)))
                .map(t -> dictionary.get(new Long(t)))
                .collect(Collectors.toList());

        if (slotDishTagId != null && dictionary.containsKey(slotDishTagId)) {
            slotDishTag = dictionary.get(slotDishTagId);
        }

        if (dishSlots != null && !dishSlots.isEmpty()) {
            for (DishSlotEntity dishslot : dishSlots) {
                dishslot.fillInTags(dictionary);
            }
        }

    }

    public Collection<Long> getAllDishIds() {
        // make list of all dish strings for target and contained slots
        // also include dish type tags
        List<Long> dishIdList = new ArrayList<>();
        if (dishSlots != null && !dishSlots.isEmpty()) {
            for (DishSlotEntity dishslot : dishSlots) {
                dishIdList.add(dishslot.getDishId());
            }
        }

        return dishIdList;
    }

    public void fillInDishes(Map<Long, DishEntity> dictionary) {
        if (dictionary.isEmpty()) {
            return;
        }
        // make list of all dish strings for target and contained slots
        // also include dish type tags
        if (dishSlots != null && !dishSlots.isEmpty()) {
            for (DishSlotEntity dishslot : dishSlots) {
                if (dictionary.containsKey(dishslot.getDishId()))
                    dishslot.setDish(dictionary.get(dishslot.getDishId()));
            }
        }

    }
}
