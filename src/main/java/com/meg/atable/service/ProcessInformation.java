package com.meg.atable.service;

import com.meg.atable.api.model.ApproachType;
import com.meg.atable.data.entity.TargetSlotEntity;

import java.util.List;

/**
 * Created by margaretmartin on 25/05/2018.
 */
public class ProcessInformation {
    private List<TargetSlotEntity> searchSlots;
    private List<TargetSlotEntity> fillSlots;
    private List<Long> sqlFilter;
    private int maximumEmpties;
    private int dishCountPerSlot;
    private ApproachType approachType;
    private int proposalCount;
    private int resultsPerSlot;

    public void setSearchSlots(List<TargetSlotEntity> searchSlots) {
        this.searchSlots = searchSlots;
    }

    public List<TargetSlotEntity> getSearchSlots() {
        return searchSlots;
    }

    public void setFillSlots(List<TargetSlotEntity> fillSlots) {
        this.fillSlots = fillSlots;
    }

    public List<TargetSlotEntity> getFillSlots() {
        return fillSlots;
    }

    public void setSqlFilter(List<Long> sqlFilter) {
        this.sqlFilter = sqlFilter;
    }

    public List<Long> getSqlFilter() {
        return sqlFilter;
    }

    public void setMaximumEmpties(int maximumEmpties) {
        this.maximumEmpties = maximumEmpties;
    }

    public int getMaximumEmpties() {
        return maximumEmpties;
    }

    public void setDishCountPerSlot(int dishCountPerSlot) {
        this.dishCountPerSlot = dishCountPerSlot;
    }

    public int getDishCountPerSlot() {
        return dishCountPerSlot;
    }

    public void setApproachType(ApproachType approachType) {
        this.approachType = approachType;
    }

    public ApproachType getApproachType() {
        return approachType;
    }

    public void setProposalCount(int proposalCount) {
        this.proposalCount = proposalCount;
    }

    public int getProposalCount() {
        return proposalCount;
    }

    public void setResultsPerSlot(int dishesPerSlot) {
        this.resultsPerSlot = dishesPerSlot;
    }

    public int getResultsPerSlot() {
        return resultsPerSlot;
    }
}
