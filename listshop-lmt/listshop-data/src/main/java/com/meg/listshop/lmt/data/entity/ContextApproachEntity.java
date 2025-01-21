package com.meg.listshop.lmt.data.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "proposal_approach")
public class ContextApproachEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proposal_approach_sequence")
    @SequenceGenerator(name = "proposal_approach_sequence", sequenceName = "proposal_approach_sequence", allocationSize = 1)
    private Long proposalApproachId;

    @ManyToOne
    @JoinColumn(name = "proposal_context_id", nullable = false)
    private ProposalContextEntity proposalContext;

    private Integer approachNumber;

    private String instructions;

    public Long getId() {
        return proposalApproachId;
    }

    public void setId(Long proposalApproachId) {
        this.proposalApproachId = proposalApproachId;
    }

    public Integer getApproachNumber() {
        return approachNumber;
    }

    public void setApproachNumber(Integer approachNumber) {
        this.approachNumber = approachNumber;
    }

    public void setContext(ProposalContextEntity context) {
        this.proposalContext = context;
    }

    public ProposalContextEntity getContext() {
        return proposalContext;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

}
