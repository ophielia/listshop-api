package com.meg.atable.data.entity;

import com.meg.atable.service.TargetServiceConstants;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "target_proposal_slot")
@GenericGenerator(
        name = "target_proposal_slot_sequence",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@org.hibernate.annotations.Parameter(
                name = "sequence_name",
                value="target_proposal_slot_sequence"),
                @org.hibernate.annotations.Parameter(
                        name = "increment_size",
                        value="1")}
)
@Deprecated
public class TargetProposalSlotEntity extends AbstractInflateAndFlatten {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "targetProposalSlot")
    @LazyCollection(LazyCollectionOption.FALSE)
    List<TargetProposalDishEntity> dishSlotList;
    @Id
    @GeneratedValue( strategy=GenerationType.SEQUENCE, generator="target_proposal_slot_sequence")
    @Column(name = "slot_id")
    private Long slotId;
    private Long targetId;
    private Long slotDishTagId;
    private String targetTagIds;
    private Integer slotOrder;
    @ManyToOne
    private TargetProposalEntity targetProposal;
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

    public void setSlotDishTagId(Long slotDishTagId) {
        this.slotDishTagId = slotDishTagId;
    }

    public TagEntity getSlotDishTag() {
        return null; //MM remove this slotDishTag;
    }

    public void setSlotDishTag(TagEntity slotDishTag) {
        //MM remove thisthis.slotDishTag = slotDishTag;
    }

    public String getTargetTagIds() {
        return targetTagIds;
    }

    public void setTargetTagIds(String targetTagIds) {
        this.targetTagIds = targetTagIds;
    }

    public Integer getSlotOrder() {
        return slotOrder;
    }

    public void setSlotOrder(Integer slotOrder) {
        this.slotOrder = slotOrder;
    }

    public List<TagEntity> getTags() {
        return null;//MM remove this tags;
    }

    public void setTags(List<TagEntity> tags) {
        int i=1;//MM remove this this.tags = tags;
    }

    public TargetProposalEntity getTargetProposal() {
        return targetProposal;
    }

    public Set<String> getAllTagIds() {
        // make list of all tagList strings for target and contained slots
        // also include dish type tags
        Set<String> stringSet = new HashSet<>();
        stringSet.addAll(inflateStringToList(getTargetTagIds(), TargetServiceConstants.TARGET_TAG_DELIMITER));
        stringSet.add(String.valueOf(slotDishTagId));

        if (dishSlotList != null && !dishSlotList.isEmpty()) {
            Set<String> dishTagMatches = new HashSet<>();
            dishSlotList.stream()
                    .forEach(ds -> stringSet.addAll(ds.inflateStringToList(ds.getMatchedTagIds())));
        }
        return stringSet;
    }

    public void fillInTags(Map<Long, TagEntity> dictionary) {
        if (dictionary.isEmpty()) {
            return;
        }
        List<TagEntity> tags = inflateStringToList(getTargetTagIds()).stream()
                .filter(t -> dictionary.containsKey(new Long(t)))
                .map(t -> dictionary.get(new Long(t)))
                .collect(Collectors.toList());

        if (slotDishTagId != null) {
            if (dictionary.containsKey(slotDishTagId)) {
                TagEntity slotDishTag = dictionary.get(slotDishTagId);
            }
        }

        if (dishSlotList != null && !dishSlotList.isEmpty()) {
            for (TargetProposalDishEntity slot : dishSlotList) {
                slot.fillInTags(dictionary);
            }
        }

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

    public Long getSelectedDishId() {
        if (selectedDishIndex > -1) {
            if (dishSlotList != null && !dishSlotList.isEmpty() && dishSlotList.size() > selectedDishIndex) {
                return dishSlotList.get(selectedDishIndex).getDishId();
            }
            return null;
        }
        return null;
    }

}
