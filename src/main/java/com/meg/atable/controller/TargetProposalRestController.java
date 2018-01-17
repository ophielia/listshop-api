package com.meg.atable.controller;

import com.meg.atable.api.controller.TargetProposalRestControllerApi;
import com.meg.atable.api.model.SortDirection;
import com.meg.atable.api.model.TargetProposalResource;
import com.meg.atable.data.entity.TargetProposalEntity;
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

    @Override
    public ResponseEntity<Object> generateProposal(Principal principal, @PathVariable Long targetId) {
        TargetProposalEntity proposalEntity = this.targetProposalService.createTargetProposal(principal.getName(), targetId);
        if (proposalEntity != null) {
            Link forOneProposal = new TargetProposalResource(proposalEntity).getLink("self");
            return ResponseEntity.created(URI.create(forOneProposal.getHref())).build();
        }

        return null;
    }

    @Override
    public ResponseEntity<TargetProposalResource> getProposal(Principal principal, @PathVariable("proposalId") Long proposalId) {
        TargetProposalEntity proposalEntity = this.targetProposalService.getTargetProposalById(principal.getName(), proposalId);
        if (proposalEntity != null) {

            // fill tag and dish info for proposal
            proposalEntity = this.targetProposalService.fillInformationForProposal(proposalEntity);
            TargetProposalResource proposalResource = new TargetProposalResource(proposalEntity);
            return new ResponseEntity<TargetProposalResource>(proposalResource, HttpStatus.OK);
        }

        return ResponseEntity.notFound().build();
    }


    @Override
    public ResponseEntity<Object> refreshProposal(Principal principal, @PathVariable("proposalId") Long proposalId,
                                                  @RequestParam(value = "direction", required = false) String direction) {
        SortDirection sortDirection = SortDirection.UP;
        if (direction != null) {
            sortDirection = SortDirection.valueOf(direction);
        }
        this.targetProposalService.refreshTargetProposal(principal.getName(), proposalId, sortDirection);


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
    public ResponseEntity<Object> refreshProposalSlot(Principal principal, @PathVariable("proposalId") Long proposalId, @PathVariable("slotId") Long slotId) {
        this.targetProposalService.showMoreProposalSlotOptions(principal.getName(), proposalId, slotId);


        return ResponseEntity.noContent().build();
    }

}
