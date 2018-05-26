package com.meg.atable.service;

import com.meg.atable.data.entity.ContextApproachEntity;
import com.meg.atable.data.entity.ProposalContextApproachEntity;
import com.meg.atable.data.entity.ProposalSlotEntity;

import java.util.List;

/**
 * Created by margaretmartin on 23/05/2018.
 */
public class ProcessResult {
    private List<ProposalSlotEntity> resultSlots;
    private List<ContextApproachEntity> resultApproaches;

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
}
