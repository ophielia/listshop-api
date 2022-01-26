package com.meg.listshop.lmt.api.model;


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