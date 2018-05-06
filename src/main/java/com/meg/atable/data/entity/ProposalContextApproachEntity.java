package com.meg.atable.data.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "proposal_context_slot")
@GenericGenerator(
        name = "proposal_context_slot_sequence",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@org.hibernate.annotations.Parameter(
                name = "sequence_name",
                value="proposal_context_slot_sequence"),
                @org.hibernate.annotations.Parameter(
                        name = "increment_size",
                        value="1")}
)
public class ProposalContextApproachEntity {

    @Id
    @GeneratedValue( strategy=GenerationType.SEQUENCE, generator="proposal_context_slot_sequence")
    @Column(name = "proposal_context_slot_id")
    private Long proposalContextSlotId;

    @ManyToOne
    @JoinColumn(name = "proposal_context_id", nullable = false)
    private ProposalContextEntity proposalContext;
    private String approachOrder;

    private Integer sortKey;

    public ProposalContextApproachEntity() {
        // jpa empty constructor
    }

    public Long getProposalContextSlotId() {
        return proposalContextSlotId;
    }

    public ProposalContextEntity getProposalContext() {
        return proposalContext;
    }

    public void setProposalContext(ProposalContextEntity proposalContext) {
        this.proposalContext = proposalContext;
    }

    public String getApproachOrder() {
        return approachOrder;
    }

    public void setApproachOrder(String approachOrder) {
        this.approachOrder = approachOrder;
    }

    public Integer getSortKey() {
        return sortKey;
    }

    public void setSortKey(Integer sortKey) {
        this.sortKey = sortKey;
    }
}