package com.meg.atable.data.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "proposal_approach")
@GenericGenerator(
        name = "proposal_approach_sequence",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@org.hibernate.annotations.Parameter(
                name = "sequence_name",
                value = "proposal_approach_sequence"),
                @org.hibernate.annotations.Parameter(
                        name = "increment_size",
                        value = "1")}
)
public class ContextApproachEntity {

    @Id
    private Long proposalApproachId;

    @ManyToOne
    @JoinColumn(name = "proposal_context_id", nullable = false)
    private ProposalContextEntity proposalContext;

    private Integer approachNumber;

    private String instructions;

    private ProposalContextEntity context;

    public Long getId() {
        return proposalApproachId;
    }

    public void setId(Long proposalApproachId) {
        this.proposalApproachId = proposalApproachId;
    }

    public ProposalContextEntity getProposalContext() {
        return proposalContext;
    }

    public void setProposalContext(ProposalContextEntity proposalContext) {
        this.proposalContext = proposalContext;
    }

    public Integer getApproachNumber() {
        return approachNumber;
    }

    public void setApproachNumber(Integer approachNumber) {
        this.approachNumber = approachNumber;
    }

    public void setContext(ProposalContextEntity context) {
        this.context = context;
    }

    public ProposalContextEntity getContext() {
        return context;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

}
