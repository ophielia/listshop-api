package com.meg.listshop.lmt.api.web.controller;

import com.meg.listshop.common.StringTools;
import com.meg.listshop.lmt.api.controller.ProposalRestControllerApi;
import com.meg.listshop.lmt.api.exception.ProposalProcessingException;
import com.meg.listshop.lmt.api.model.ProposalResource;
import com.meg.listshop.lmt.data.entity.ProposalEntity;
import com.meg.listshop.lmt.service.proposal.ProposalGeneratorService;
import com.meg.listshop.lmt.service.proposal.ProposalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;
import java.security.Principal;
import java.util.Optional;

/**
 * Created by margaretmartin on 20/10/2017.
 */
@Controller
public class ProposalRestController implements ProposalRestControllerApi {

    private final ProposalService targetProposalService;

    private final ProposalGeneratorService targetProposalGenerator;

    @Autowired
    public ProposalRestController(ProposalService targetProposalService, ProposalGeneratorService targetProposalGenerator) {
        this.targetProposalService = targetProposalService;
        this.targetProposalGenerator = targetProposalGenerator;
    }

    @Override
    public ResponseEntity<Object> generateProposal(Principal principal, @PathVariable Long targetId) throws ProposalProcessingException {
        ProposalEntity proposalEntity = this.targetProposalGenerator.generateProposal(principal.getName(), targetId);
        if (proposalEntity != null) {
            Optional<Link> forOneProposal = new ProposalResource(proposalEntity).getLink("self");
            String link = StringTools.safeLink(forOneProposal);
            return ResponseEntity.created(URI.create(link)).build();
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
        this.targetProposalService.selectDishInSlot(principal.getName(), proposalId, slotId, dishId);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> clearDishFromSlot(Principal principal, @PathVariable Long proposalId, @PathVariable Long slotId, @PathVariable Long dishId) {
        this.targetProposalService.clearDishFromSlot(principal, proposalId, slotId, dishId);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> refreshProposalSlot(Principal principal, @PathVariable("proposalId") Long proposalId, @PathVariable("slotId") Long slotId) throws ProposalProcessingException {
        // TODO need to swap out id with number
        this.targetProposalGenerator.addToProposalSlot(principal.getName(), proposalId, slotId.intValue());


        return ResponseEntity.noContent().build();
    }

}
