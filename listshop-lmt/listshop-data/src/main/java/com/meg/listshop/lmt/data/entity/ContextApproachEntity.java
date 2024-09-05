package com.meg.listshop.lmt.data.entity;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;

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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proposal_approach_sequence")
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
