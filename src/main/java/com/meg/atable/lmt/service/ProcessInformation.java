package com.meg.atable.lmt.service;

import com.meg.atable.lmt.api.model.ApproachType;
import com.meg.atable.lmt.data.entity.ContextApproachEntity;
import com.meg.atable.lmt.data.entity.ProposalEntity;
import com.meg.atable.lmt.data.entity.TargetSlotEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by margaretmartin on 25/05/2018.
 */
public class ProcessInformation {
    Map<Integer, List<String>> searchTagKeyBySlot = new HashMap<>();
    private List<TargetSlotEntity> searchSlots;
    private List<TargetSlotEntity> fillSlots;
    private List<Long> sqlFilter;
    private int maximumEmpties;
    private Map<Integer, Integer> dishCountPerSlot = new HashMap<>();
    private ApproachType approachType;
    private int proposalCount;
    private int resultsPerSlot;
    private Map<Integer, Long> dishTagBySlot = new HashMap<>();
    private ProposalEntity proposal;
    private int defaultDishCountPerSlot;
    private ContextApproachEntity currentApproach;
    private int currentApproachIndex;
    private List<Long> codeFilter;
    private List<Long> currentSlotResults;

    public List<TargetSlotEntity> getSearchSlots() {
        return searchSlots;
    }

    public void setSearchSlots(List<TargetSlotEntity> searchSlots) {
        this.searchSlots = searchSlots;
    }

    public List<TargetSlotEntity> getFillSlots() {
        return fillSlots;
    }

    public void setFillSlots(List<TargetSlotEntity> fillSlots) {
        this.fillSlots = fillSlots;
    }

    public List<Long> getSqlFilter() {
        return sqlFilter;
    }

    public void setSqlFilter(List<Long> sqlFilter) {
        this.sqlFilter = sqlFilter;
    }

    public int getDefaultDishCountPerSlot() {
        return defaultDishCountPerSlot;
    }

    public void setDefaultDishCountPerSlot(int defaultDishCountPerSlot) {
        this.defaultDishCountPerSlot = defaultDishCountPerSlot;
    }

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

    public int getResultsPerSlot() {
        return resultsPerSlot;
    }

    public void setResultsPerSlot(int dishesPerSlot) {
        this.resultsPerSlot = dishesPerSlot;
    }

    public void addTagKeyBySlot(Integer slotOrder, List<String> tagListForSlot) {
        searchTagKeyBySlot.put(slotOrder, tagListForSlot);
    }

    public List<String> getTagKeyBySlotNumber(Integer slotNumber) {
        return searchTagKeyBySlot.get(slotNumber);
    }

    public Long getDishTagBySlotNumber(Integer slotNumber) {
        return dishTagBySlot.get(slotNumber);
    }

    public void setDishTagBySlotNumber(Integer slotNumber, Long tagId) {
        dishTagBySlot.put(slotNumber, tagId);
    }

    public ProposalEntity getProposal() {
        return proposal;
    }

    public void setProposal(ProposalEntity proposal) {
        this.proposal = proposal;
    }

    public Integer getDishCountBySlotNumber(Integer slotNumber) {
        if (dishCountPerSlot.containsKey(slotNumber)) {

        return dishCountPerSlot.get(slotNumber);
        }
        return defaultDishCountPerSlot;
    }

    public void setDishCountPerSlotNumber(Map<Integer, Integer> dishCountPerSlot) {
        this.dishCountPerSlot = dishCountPerSlot;
    }


    public void setCurrentApproach(ContextApproachEntity currentApproach) {
        this.currentApproach = currentApproach;
    }

    public ContextApproachEntity getCurrentApproach() {
        return currentApproach;
    }

    public void setCurrentApproachIndex(int currentApproachIndex) {
        this.currentApproachIndex = currentApproachIndex;
    }

    public int getCurrentApproachIndex() {
        return currentApproachIndex;
    }

    public void setCodeFilter(List<Long> codeFilter) {
        this.codeFilter = codeFilter;
    }

    public List<Long> getCodeFilter() {
        return codeFilter;
    }

    public void setCurrentSlotResults(List<Long> currentSlotResults) {
        this.currentSlotResults = currentSlotResults;
    }

    public List<Long> getCurrentDishIdsForSlot() {
        return currentSlotResults;
    }
}
