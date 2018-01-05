package com.meg.atable.controller;

import com.meg.atable.api.controller.TargetProposalRestControllerApi;
import com.meg.atable.api.model.*;
import com.meg.atable.data.entity.TargetProposalEntity;
import com.meg.atable.service.TargetProposalService;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

import java.net.URI;
import java.security.Principal;

/**
 * Created by margaretmartin on 20/10/2017.
 */
@Controller
public class TargetProposalRestController implements TargetProposalRestControllerApi {

    @Autowired
    private TargetProposalService targetProposalService;

    @Override
    public ResponseEntity<Object> generateProposal(Principal principal, @PathVariable Long targetId) {
TargetProposalEntity proposalEntity = this.targetProposalService.createTargetProposal(principal.getName(),targetId);
        if (proposalEntity != null) {
            Link forOneProposal = new TargetProposalResource(proposalEntity).getLink("self");
            return ResponseEntity.created(URI.create(forOneProposal.getHref())).build();
        }

        return null;
    }

    @Override
    public ResponseEntity<TargetProposalResource> getProposal(Principal principal, @PathVariable("proposalId") Long proposalId) {
        TargetProposalEntity proposalEntity = this.targetProposalService.getTargetProposalById(principal.getName(),proposalId);
        if (proposalEntity != null) {

            // fill tag and dish info for proposal
            proposalEntity = this.targetProposalService.fillInformationForProposal(proposalEntity);
            TargetProposalResource proposalResource = new TargetProposalResource(proposalEntity);
            return new ResponseEntity<TargetProposalResource>(proposalResource, HttpStatus.OK);
        }

        return ResponseEntity.notFound().build();
    }



    @Override
    public ResponseEntity<Object> refreshProposal(Principal principal, @PathVariable("proposalId") Long proposalId) {

        this.targetProposalService.refreshTargetProposal(principal.getName(),proposalId);


        return ResponseEntity.noContent().build();

    }

    @Override
    public ResponseEntity<Object> selectDishInSlot(Principal principal, @PathVariable Long proposalId, @PathVariable Long slotId, @PathVariable Long dishId) {
        this.targetProposalService.selectDishInSlot(principal,proposalId,slotId,dishId);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> clearDishFromSlot(Principal principal, @PathVariable Long proposalId, @PathVariable Long slotId, @PathVariable Long dishId) {
        this.targetProposalService.clearDishFromSlot(principal,proposalId,slotId,dishId);

        return ResponseEntity.noContent().build();    }

    @Override
    public ResponseEntity<Object> refreshProposalSlot(Principal principal, @PathVariable("proposalId") Long proposalId, @PathVariable("slotId") Long slotId) {
        this.targetProposalService.refreshTargetProposalSlot(principal.getName(),proposalId,slotId);


        return ResponseEntity.noContent().build();
    }

}
