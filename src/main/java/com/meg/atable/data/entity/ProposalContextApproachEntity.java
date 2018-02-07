package com.meg.atable.data.entity;

import javax.persistence.*;

@Entity
@Table(name = "proposal_context_slot")
@SequenceGenerator(name="proposal_context_slot_sequence", sequenceName = "proposal_context_slot_sequence")
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