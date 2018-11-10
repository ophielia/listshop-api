package com.meg.atable.lmt.data.entity;

import com.meg.atable.lmt.api.model.ApproachType;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "proposal_context")
@GenericGenerator(
        name = "proposal_context_sequence",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@org.hibernate.annotations.Parameter(
                name = "sequence_name",
                value = "proposal_context_sequence"),
                @org.hibernate.annotations.Parameter(
                        name = "increment_size",
                        value = "1")}
)
public class ProposalContextEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proposal_context_sequence")
    @Column(name = "proposal_context_id")
    private Long proposalContextId;

    private Long proposalId;

    @Enumerated(EnumType.STRING)
    private ApproachType currentApproachType;


    private Integer currentApproachIndex;

    private Long targetId;

    @OneToMany(mappedBy = "proposalContext", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<ContextApproachEntity> approaches;

    private String targetHashCode;

    private String proposalHashCode;

    private Long mealPlanId;


    public ProposalContextEntity() {
        // jpa empty constructor
    }

    public Long getProposalId() {
        return proposalId;
    }

    public void setProposalId(Long proposalId) {
        this.proposalId = proposalId;
    }

    public int getCurrentApproachIndex() {
        return currentApproachIndex;
    }

    public void setCurrentApproachIndex(int currentApproachIndex) {
        this.currentApproachIndex = currentApproachIndex;
    }

    public Long getId() {
        return this.proposalContextId;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public String getProposalHashCode() {
        // the proposalHashCode is used in the context of deciding if the picked dishes for a proposal have changed
        return this.proposalHashCode;
    }

    public void setProposalHashCode(String proposalHashCode) {
        this.proposalHashCode = proposalHashCode;
    }

    public String getTargetHashCode() {
        return this.targetHashCode;
    }

    public void setTargetHashCode(String targetHashCode) {
        this.targetHashCode = targetHashCode;
    }

    public List<ContextApproachEntity> getApproaches() {
        return approaches;
    }

    public void setApproaches(List<ContextApproachEntity> approaches) {
        this.approaches = approaches;
    }

    public Long getMealPlanId() {
        return mealPlanId;
    }

    public void setMealPlanId(Long mealPlanId) {
        this.mealPlanId = mealPlanId;
    }


    public void setCurrentApproachType(ApproachType currentApproachType) {
        this.currentApproachType = currentApproachType;
    }

    public ApproachType getCurrentApproachType() {
        return currentApproachType;
    }
}