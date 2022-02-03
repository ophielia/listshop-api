/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.auth.api.model;


import com.meg.listshop.lmt.api.model.AbstractListShopResource;
import com.meg.listshop.lmt.api.model.ListShopModel;

public class ProposalResource extends AbstractListShopResource implements ListShopModel {

    private final TargetProposal proposal;

    public ProposalResource(TargetProposal proposal) {
        this.proposal = proposal;
    }

    public TargetProposal getProposal() {
        return proposal;
    }

    @Override
    public String getRootPath() {
        return "proposal";
    }

    @Override
    public String getResourceId() {
        return null;
    }
}