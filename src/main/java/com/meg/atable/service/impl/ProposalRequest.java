package com.meg.atable.service.impl;

import com.meg.atable.data.entity.MealPlanEntity;
import com.meg.atable.data.entity.ProposalContextEntity;
import com.meg.atable.data.entity.ProposalEntity;
import com.meg.atable.data.entity.TargetEntity;
import com.meg.atable.service.ProposalSearchType;

/**
 * Created by margaretmartin on 22/05/2018.
 */
public class ProposalRequest {

    private TargetEntity target;
    private ProposalEntity proposal ;
    private ProposalContextEntity context;
    private ProposalSearchType searchType;
    private MealPlanEntity mealPlan;

    public void setTarget(TargetEntity target) {
        this.target = target;
    }

    public void setProposal(ProposalEntity proposal) {
        this.proposal = proposal;
    }

    public void setContext(ProposalContextEntity context) {
        this.context = context;
    }

    public void setSearchType(ProposalSearchType searchType) {
        this.searchType = searchType;
    }


    public void setMealPlan(MealPlanEntity mealPlan) {
        this.mealPlan = mealPlan;
    }

    public MealPlanEntity getMealPlan() {
        return mealPlan;
    }

    public ProposalSearchType getSearchType() {
        return searchType;
    }


    public ProposalEntity getProposal() {
        return proposal;
    }

    public TargetEntity getTarget() {
        return target;
    }
}
