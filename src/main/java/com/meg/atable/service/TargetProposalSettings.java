package com.meg.atable.service;

import com.meg.atable.api.model.ApproachType;

/**
 * Created by margaretmartin on 01/01/2018.
 */
public class TargetProposalSettings {

    ApproachType approachType;

    int proposalCount;

    int maximumEmpties;
    private int dishCount;

    public int getMaximumEmpties() {
        return maximumEmpties;
    }

    public void setMaximumEmpties(int maximumEmpties) {
        this.maximumEmpties = maximumEmpties;
    }

    public ApproachType getApproachType() {
        return approachType;
    }

    public void setApproachType(ApproachType approachType) {
        this.approachType = approachType;
    }

    public int getProposalCount() {
        return proposalCount;
    }

    public void setProposalCount(int proposalCount) {
        this.proposalCount = proposalCount;
    }

    public void setDishCountPerSlot(int dishCount) {
        this.dishCount = dishCount;
    }

    public int getDishCountPerSlot() {
        return dishCount;
    }
}
