package com.meg.atable.data.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    private List<ProposalSlotEntity> slots;
    private boolean isRefreshable;
    private Long id;






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

}
