package com.meg.atable.service;

import com.meg.atable.data.entity.MealPlanEntity;
import com.meg.atable.data.entity.ProposalContextEntity;
import com.meg.atable.data.entity.ProposalEntity;
import com.meg.atable.data.entity.TargetEntity;
import com.meg.atable.service.impl.ProposalRequest;

/**
 * Created by margaretmartin on 22/05/2018.
 */
public class ProposalRequestBuilder {
    ProposalRequest request;
    ProposalEntity proposal;
    TargetEntity target;
    ProposalContextEntity context;
    ProposalSearchType searchType;

    public ProposalRequestBuilder() {

    }

    public ProposalRequestBuilder createNewSearch() {
        this.request = new ProposalRequest();
        this.request.setSearchType(ProposalSearchType.NewSearch);
        return this;
    }

    public ProposalRequestBuilder withTarget(TargetEntity target) {
        this.request.setTarget(this.target);
        return this;
    }
    public ProposalRequestBuilder withProposal(ProposalEntity proposal) {
        this.request.setProposal(this.proposal);
        return this;
    }

    public ProposalRequestBuilder withContext(ProposalContextEntity context) {
        this.request.setContext(this.context);
        return this;
    }


    public ProposalRequest build() {
        return request;
    }

    public ProposalRequestBuilder create() {
        this.request = new ProposalRequest();
        return this;
    }

    public ProposalRequestBuilder withSearchType(ProposalSearchType searchType) {
        this.request.setSearchType(this.searchType);
        return this;
    }

    public ProposalRequestBuilder withMealPlan(MealPlanEntity mealPlan) {
        this.request.setMealPlan(mealPlan);
        return this;
    }
}
