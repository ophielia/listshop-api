package com.meg.atable.lmt.service;

import com.meg.atable.lmt.api.model.ApproachType;
import com.meg.atable.lmt.data.entity.ContextApproachEntity;
import com.meg.atable.lmt.data.entity.ProposalSlotEntity;

import java.util.List;

/**
 * Created by margaretmartin on 23/05/2018.
 */
public class ProcessResult {
    private List<ProposalSlotEntity> resultSlots;
    private List<ContextApproachEntity> resultApproaches;
    private int currentApproach;
    private ApproachType currentApproachType;

    public ProcessResult(List<ContextApproachEntity> contextApproaches) {
        this.resultApproaches = contextApproaches;
    }

    public List<ProposalSlotEntity> getResultSlots() {
        return resultSlots;
    }

    public void setResultSlots(List<ProposalSlotEntity> resultSlots) {
        this.resultSlots = resultSlots;
    }

    public List<ContextApproachEntity> getResultApproaches() {
        return resultApproaches;
    }

    public void setResultApproaches(List<ContextApproachEntity> resultApproaches) {
        this.resultApproaches = resultApproaches;
    }


    public void addResults(List<ProposalSlotEntity> proposalSlots) {
        this.resultSlots=proposalSlots;
    }

    public int getCurrentApproach() {
        return currentApproach;
    }

    public void setCurrentApproach(int currentApproach) {
        this.currentApproach = currentApproach;
    }

    public ApproachType getCurrentApproachType() {
        return currentApproachType;
    }

    public void setCurrentApproachType(ApproachType currentApproachType) {
        this.currentApproachType = currentApproachType;
    }
}
