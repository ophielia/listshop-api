package com.meg.atable.web.controller;

import com.meg.atable.api.controller.TargetProposalRestControllerApi;
import com.meg.atable.api.exception.ProposalProcessingException;
import com.meg.atable.api.model.ProposalResource;
import com.meg.atable.api.model.SortDirection;
import com.meg.atable.data.entity.ProposalEntity;
import com.meg.atable.service.ProposalGeneratorService;
import com.meg.atable.service.TargetProposalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;
import java.security.Principal;

/**
 * Created by margaretmartin on 20/10/2017.
 */
@Controller
public class TargetProposalRestController implements TargetProposalRestControllerApi {

    @Autowired
    private TargetProposalService targetProposalService;

    @Autowired
    private ProposalGeneratorService targetProposalGenerator;

    @Override
    public ResponseEntity<Object> generateProposal(Principal principal, @PathVariable Long targetId) {
        ProposalEntity proposalEntity = this.targetProposalGenerator.generateProposal(principal.getName(), targetId);
        if (proposalEntity != null) {
            Link forOneProposal = new ProposalResource(proposalEntity).getLink("self");
            return ResponseEntity.created(URI.create(forOneProposal.getHref())).build();
        }

        return null;
    }

    @Override
    public ResponseEntity<ProposalResource> getProposal(Principal principal, @PathVariable("proposalId") Long proposalId) {
        ProposalEntity proposalEntity = this.targetProposalService.getProposalById(principal.getName(), proposalId);
        if (proposalEntity != null) {

            // fill tag and dish info for proposal
            proposalEntity = this.targetProposalGenerator.fillInformationForProposal(principal.getName(), proposalEntity);
            ProposalResource proposalResource = new ProposalResource(proposalEntity);
            return new ResponseEntity<>(proposalResource, HttpStatus.OK);
        }

        return ResponseEntity.notFound().build();
    }


    @Override
    public ResponseEntity<Object> refreshProposal(Principal principal, @PathVariable("proposalId") Long proposalId,
                                                  @RequestParam(value = "direction", required = false) String direction) throws ProposalProcessingException {

        this.targetProposalGenerator.refreshProposal(principal.getName(), proposalId);

        return ResponseEntity.noContent().build();

    }

    @Override
    public ResponseEntity<Object> selectDishInSlot(Principal principal, @PathVariable Long proposalId, @PathVariable Long slotId, @PathVariable Long dishId) {
        this.targetProposalService.selectDishInSlot(principal, proposalId, slotId, dishId);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> clearDishFromSlot(Principal principal, @PathVariable Long proposalId, @PathVariable Long slotId, @PathVariable Long dishId) {
        this.targetProposalService.clearDishFromSlot(principal, proposalId, slotId, dishId);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> refreshProposalSlot(Principal principal, @PathVariable("proposalId") Long proposalId, @PathVariable("slotId") Long slotId) throws ProposalProcessingException {
        // MM need to swap out id with number
        this.targetProposalGenerator.addToProposalSlot(principal.getName(), proposalId, slotId.intValue());


        return ResponseEntity.noContent().build();
    }

}
