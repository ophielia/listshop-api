package com.meg.listshop.lmt.data.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by margaretmartin on 22/05/2018.
 */
@Entity
@Table(name = "proposal")
@GenericGenerator(
        name = "proposal_sequence",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@org.hibernate.annotations.Parameter(
                name = "sequence_name",
                value="proposal_sequence"),
                @org.hibernate.annotations.Parameter(
                        name = "increment_size",
                        value="1")}
)
public class ProposalEntity {


    @Id
    @GeneratedValue( strategy= GenerationType.SEQUENCE, generator="proposal_sequence")
    private Long proposalId;

    private Long userId;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "proposal")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<ProposalSlotEntity> slots;

    private Date created;

    private boolean isRefreshable;

    @Transient
    private List<TagEntity> targetTags;
    @Transient
    private String targetName;


    public Long getId() {
        return proposalId;
    }

    public void setId(Long proposalId) {
        this.proposalId = proposalId;
    }

    public boolean isRefreshable() {
        return isRefreshable;
    }
    public void setIsRefreshable(boolean isRefreshable) {
        this.isRefreshable = isRefreshable;
    }
    public List<ProposalSlotEntity> getSlots() {
        return slots!=null?slots:new ArrayList<>();
    }

    public void setSlots(List<ProposalSlotEntity> slots) {
        this.slots = slots;
    }
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public String getPickedHashCode() {
        // this is used to decide if the slots have changed since the last proposal has been run
        // slots changed == different picked dishes
        StringBuilder hashCode = new StringBuilder(getId().hashCode());
        for (ProposalSlotEntity slot : getSlots()) {
            if (slot.getPickedDishId() != null) {
                hashCode.append(slot.getPickedDishId().hashCode());
            }
        }

        int finalCode = hashCode.toString().hashCode();
        return String.valueOf(finalCode);
    }


    public void setTargetTags(List<TagEntity> targetTags) {
        this.targetTags = targetTags;
    }

    public List<TagEntity> getTargetTags() {
        return targetTags;
    }

    public void fillSlotTags(Integer slotOrder, List<String> tagIdsAsList, Map<Long, TagEntity> tagDictionary) {
        ProposalSlotEntity slotToFill = null;
        for (ProposalSlotEntity slot: getSlots()) {
            if (slot.getSlotNumber().equals(slotOrder)) {
                slotToFill = slot;
                break;
            }
        }
        if (slotToFill == null) {
            return;
        }
        slotToFill.fillInTags(tagIdsAsList,tagDictionary);
    }

    public List<Long> getAllDishIds() {
        // make list of all dish strings for target and contained slots
        // also include dish type tags
        List<Long> dishIdList = new ArrayList<>();
        if (slots != null && !slots.isEmpty()) {
            for (ProposalSlotEntity dishslot : slots) {
                dishIdList.addAll(dishslot.getAllDishIds());
            }
        }

        return dishIdList;
    }

    public void fillInAllDishes(Map<Long, DishEntity> dictionary) {
        if (dictionary.isEmpty()) {
            return;
        }
        if (slots != null && !slots.isEmpty()) {
            for (ProposalSlotEntity slot : slots) {
                slot.fillInDishes(dictionary);
            }
        }
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getCreated() {
        return created;
    }
}
