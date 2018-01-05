package com.meg.atable.data.entity;

import com.meg.atable.service.TargetServiceConstants;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Table(name = "target_proposal_slot")
public class TargetProposalSlotEntity extends AbstractInflateAndFlatten {

    @Id
    @GeneratedValue
    @Column(name="slot_id")
    private Long slotId;

    private Long targetId;

    private Long slotDishTagId;

    @Transient
    private TagEntity slotDishTag;

    private String targetTagIds;

    private Integer slotOrder;

    @Transient
    private List<TagEntity> tags;


    @ManyToOne
    private  TargetProposalEntity targetProposal;

    @OneToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    List<TargetProposalDishEntity> dishSlotList;

    private Long targetSlotId;

    private Integer selectedDishIndex;

    public TargetProposalSlotEntity() {
    }

    public TargetProposalSlotEntity(TargetProposalEntity proposalEntity, TargetSlotEntity targetSlot) {
        this.targetProposal = proposalEntity;
        this.targetSlotId = targetSlot.getId();
        this.dishSlotList = new ArrayList<>();
        this.selectedDishIndex = -1;
        setSlotDishTagId(targetSlot.getSlotDishTagId());
        setSlotOrder(targetSlot.getSlotOrder());
        setTargetTagIds(targetSlot.getTargetTagIds());
    }

    public List<TargetProposalDishEntity> getDishSlotList() {
        return dishSlotList;
    }

    public void setDishSlotList(List<TargetProposalDishEntity> dishSlotList) {
        this.dishSlotList = dishSlotList;
    }

    public Long getTargetSlotId() {
        return targetSlotId;
    }

    public void setTargetSlotId(Long targetSlotId) {
        this.targetSlotId = targetSlotId;
    }

    public Integer getSelectedDishIndex() {
        return selectedDishIndex;
    }

    public void setSelectedDishIndex(Integer selectedDishIndex) {
        this.selectedDishIndex = selectedDishIndex;
    }

    public void addDish(TargetProposalDishEntity dish) {
        dishSlotList.add(dish);
    }

    public void setSlotDishTagId(Long slotDishTagId) {
        this.slotDishTagId = slotDishTagId;
    }

    public void setSlotOrder(Integer slotOrder) {
        this.slotOrder = slotOrder;
    }

    public void setTargetTagIds(String targetTagIds) {
        this.targetTagIds = targetTagIds;
    }

    public Long getSlotId() {
        return slotId;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public Long getSlotDishTagId() {
        return slotDishTagId;
    }

    public TagEntity getSlotDishTag() {
        return slotDishTag;
    }

    public void setSlotDishTag(TagEntity slotDishTag) {
        this.slotDishTag = slotDishTag;
    }

    public String getTargetTagIds() {
        return targetTagIds;
    }

    public Integer getSlotOrder() {
        return slotOrder;
    }

    public List<TagEntity> getTags() {
        return tags;
    }

    public void setTags(List<TagEntity> tags) {
        this.tags = tags;
    }

    public TargetProposalEntity getTargetProposal() {
        return targetProposal;
    }

    public List<Long> getAllTagIds() {
        // make list of all tagList strings for target and contained slots
        // also include dish type tags
        List<String> stringList = new ArrayList<>();
        stringList.addAll(inflateStringToList(getTargetTagIds(), TargetServiceConstants.TARGET_TAG_DELIMITER));
        stringList.add(String.valueOf(slotDishTagId));

        // convert list of strings to list of longs and return
        if (!stringList.isEmpty()) {
            return stringList.stream()
                    .map(Long::new)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public void fillInTags(Map<Long, TagEntity> dictionary) {
        if (dictionary.isEmpty()) {
            return;
        }
        tags =inflateStringToList(getTargetTagIds()).stream()
                .filter(t -> dictionary.containsKey(new Long(t)))
                .map( t -> dictionary.get(new Long(t)))
                .collect(Collectors.toList());

        if (slotDishTagId!=null) {
            if (dictionary.containsKey(slotDishTagId)) {
                slotDishTag = dictionary.get(slotDishTagId);
            }
        }

        if (dishSlotList != null && !dishSlotList.isEmpty()) {
            for (TargetProposalDishEntity slot : dishSlotList) {
                slot.fillInTags(dictionary);
            }
        }
        return;

    }

    public List<Long> getAllDishIds() {
        // make list of all dish strings for target and contained slots
        // also include dish type tags
        List<Long> dishIdList = new ArrayList<>();
        if (dishSlotList != null && !dishSlotList.isEmpty()) {
            for (TargetProposalDishEntity dishslot : dishSlotList) {
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
        if (dishSlotList != null && !dishSlotList.isEmpty()) {
            for (TargetProposalDishEntity dishslot : dishSlotList) {
             if (dictionary.containsKey(dishslot.getDishId()))
                dishslot.setDish(dictionary.get(dishslot.getDishId()));
            }
        }


    }
}
