package com.meg.atable.data.entity;

import com.meg.atable.api.model.ApproachType;
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

    @OneToMany(mappedBy = "proposalContext", fetch = FetchType.EAGER)
    private List<ProposalContextApproachEntity> contextApproaches;

    private Integer maximumEmpties;

    private Integer dishCountPerSlot;

    @Enumerated(EnumType.STRING)
    private ApproachType approachType;

    private Integer proposalCount;


    private String refreshFlag;
    private Integer currentAttemptIndex;
    private Long id;
    private Long targetId;
    private List<ContextApproachEntity> approaches;
    private Long mealPlanId;
    private String targetHashCode;
    private String proposalHashCode;

    public ProposalContextEntity() {
        // jpa empty constructor
    }

    public Long getProposalId() {
        return proposalId;
    }

    public void setProposalId(Long proposalId) {
        this.proposalId = proposalId;
    }

    public String getRefreshFlag() {
        return refreshFlag;
    }

    public void setRefreshFlag(String refreshFlag) {
        this.refreshFlag = refreshFlag;
    }

    public List<ProposalContextApproachEntity> getSlots() {
        return contextApproaches;
    }

    public Integer getMaximumEmpties() {
        return maximumEmpties;
    }

    public void setMaximumEmpties(int maximumEmpties) {
        this.maximumEmpties = maximumEmpties;
    }

    public Integer getDishCountPerSlot() {
        return dishCountPerSlot;
    }

    public void setDishCountPerSlot(int dishCountPerSlot) {
        this.dishCountPerSlot = dishCountPerSlot;
    }

    public ApproachType getApproachType() {
        return approachType;
    }

    public void setApproachType(ApproachType approachType) {
        this.approachType = approachType;
    }

    public Integer getProposalCount() {
        return proposalCount;
    }

    public void setProposalCount(int proposalCount) {
        this.proposalCount = proposalCount;
    }

    public void setContextApproaches(List<ProposalContextApproachEntity> contextApproaches) {
        this.contextApproaches = contextApproaches;
    }

    public int getCurrentAttemptIndex() {
        return currentAttemptIndex;
    }

    public void setCurrentAttemptIndex(int currentAttemptIndex) {
        this.currentAttemptIndex = currentAttemptIndex;
    }


    public Long getId() {
        // MM finish implementing id
        return id;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public String proposalHashCode() {
        // the proposalHashCode is used in the context of deciding if the picked dishes for a proposal have changed
        return this.proposalHashCode;
    }

    public String targetHashCode() {
        return null;
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

    public String getTargetHashCode() {
        return targetHashCode;
    }

    public void setTargetHashCode(String targetHashCode) {
        this.targetHashCode = targetHashCode;
    }

    public String getProposalHashCode() {
        return proposalHashCode;
    }

    public void setProposalHashCode(String proposalHashCode) {
        this.proposalHashCode = proposalHashCode;
    }
}