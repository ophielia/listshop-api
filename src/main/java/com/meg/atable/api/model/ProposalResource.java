package com.meg.atable.api.model;


import com.meg.atable.api.controller.TargetProposalRestControllerApi;
import com.meg.atable.data.entity.ProposalEntity;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class ProposalResource extends ResourceSupport {

    private final TargetProposal proposal;

    public ProposalResource(ProposalEntity proposalEntity) {
        this.proposal = ModelMapper.toModel(proposalEntity);
        this.add(linkTo(methodOn(TargetProposalRestControllerApi.class)
                .getProposal(null,proposalEntity.getId())).withSelfRel());
    }

    public TargetProposal getProposal() {
        return proposal;
    }
}