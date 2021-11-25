package com.meg.listshop.lmt.api.model;


import com.meg.listshop.lmt.data.entity.ProposalEntity;
import org.springframework.hateoas.RepresentationModel;


public class ProposalResource extends RepresentationModel {

    private final TargetProposal proposal;

    public ProposalResource(ProposalEntity proposalEntity) {
        this.proposal = ModelMapper.toModel(proposalEntity);
//        this.add(ControllerLinkBuilder.linkTo(methodOn(ProposalRestControllerApi.class)
        //              .getProposal(null, proposalEntity.getId())).withSelfRel());
    }

    public TargetProposal getProposal() {
        return proposal;
    }
}