package com.meg.atable.lmt.service;

import com.meg.atable.lmt.data.entity.MealPlanEntity;
import com.meg.atable.lmt.data.entity.ProposalContextEntity;
import com.meg.atable.lmt.data.entity.ProposalEntity;
import com.meg.atable.lmt.data.entity.TargetEntity;
import com.meg.atable.lmt.service.impl.ProposalRequest;

/**
 * Created by margaretmartin on 22/05/2018.
 */
public class ProposalRequestBuilder {
    ProposalRequest request;


    public ProposalRequestBuilder() {

    }

    public ProposalRequestBuilder createNewSearch() {
        this.request = new ProposalRequest();
        this.request.setSearchType(ProposalSearchType.NewSearch);
        return this;
    }

    public ProposalRequestBuilder withTarget(TargetEntity target) {
        this.request.setTarget(target);
        return this;
    }
    public ProposalRequestBuilder withProposal(ProposalEntity proposal) {
        this.request.setProposal(proposal);
        return this;
    }

    public ProposalRequestBuilder withContext(ProposalContextEntity context) {
        this.request.setContext(context);
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
        this.request.setSearchType(searchType);
        return this;
    }

    public ProposalRequestBuilder withMealPlan(MealPlanEntity mealPlan) {
        this.request.setMealPlan(mealPlan);
        return this;
    }

    public ProposalRequestBuilder withSlotNumber(Integer slotNr) {
            this.request.setFillInSlotNumber(slotNr);
            return this;
    }
}
