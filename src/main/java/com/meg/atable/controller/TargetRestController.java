package com.meg.atable.controller;

import com.meg.atable.api.controller.TargetRestControllerApi;
import com.meg.atable.api.model.ModelMapper;
import com.meg.atable.api.model.Target;
import com.meg.atable.api.model.TargetResource;
import com.meg.atable.api.model.TargetSlot;
import com.meg.atable.data.entity.TargetEntity;
import com.meg.atable.data.entity.TargetSlotEntity;
import com.meg.atable.service.TargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 20/10/2017.
 */
@Controller
public class TargetRestController implements TargetRestControllerApi {
    @Autowired
    TargetService targetService;

    @Override
    public ResponseEntity<Resources<TargetResource>> retrieveTargets(Principal principal) {
        List<TargetResource> targetList = targetService
                .getTargetsForUserName(principal.getName())
                .stream().map(TargetResource::new)
                .collect(Collectors.toList());

        Resources<TargetResource> targetResourceList = new Resources<>(targetList);
        return new ResponseEntity(targetResourceList, HttpStatus.OK);

    }

    @Override
    public ResponseEntity<Object> createTarget(Principal principal, @RequestBody Target input) {
        //this.getUserForPrincipal(principal);
        TargetEntity targetEntity = ModelMapper.toEntity(input);

        TargetEntity result = targetService.createTarget(principal.getName(), targetEntity);

        if (result != null) {
            Link forOneTarget = new TargetResource(result).getLink("self");
            return ResponseEntity.created(URI.create(forOneTarget.getHref())).build();
        }
        return ResponseEntity.badRequest().build();

    }

    @Override
    public ResponseEntity<Target> readTarget(Principal principal,@PathVariable Long targetId) {
        TargetEntity target = this.targetService
                .getTargetById(principal.getName(), targetId);

        // fill tag info for target
        target = this.targetService.fillTagsForTarget(target);

        if (target != null) {
            TargetResource targetResource = new TargetResource(target);

            return new ResponseEntity(targetResource, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();

    }

    @Override
    public ResponseEntity<Target> deleteTarget(Principal principal,@PathVariable Long targetId) {
        boolean success = targetService.deleteTarget(principal.getName(), targetId);
        if (success) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().build();

    }

    @Override
    public ResponseEntity<Object> updateTarget(Principal principal, @PathVariable Long targetId, @RequestBody Target input) {
        //this.getUserForPrincipal(principal);
        TargetEntity targetEntity = ModelMapper.toEntity(input);

        TargetEntity result = targetService.updateTarget(principal.getName(), targetEntity);

        if (result != null) {
            Link forOneTarget = new TargetResource(result).getLink("self");
            return ResponseEntity.created(URI.create(forOneTarget.getHref())).build();
        }
        return ResponseEntity.badRequest().build();

    }

    @Override
    public ResponseEntity<Object> addSlotToTarget(Principal principal, @PathVariable Long targetId, @RequestBody TargetSlot input) {
        TargetSlotEntity targetSlotEntity = ModelMapper.toEntity(input);
        this.targetService.addSlotToTarget(principal.getName(), targetId, targetSlotEntity);

        return ResponseEntity.noContent().build();

    }

    @Override
    public ResponseEntity<Object> deleteSlotFromTarget(Principal principal, @PathVariable Long targetId, @PathVariable Long slotId) {
        this.targetService.deleteSlotFromTarget(principal.getName(), targetId, slotId);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> addTagToSlot(Principal principal, @PathVariable Long targetId, @PathVariable Long slotId, @PathVariable Long tagId) {
        this.targetService.addTagToTargetSlot(principal.getName(), targetId, slotId, tagId);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> deleteTagFromSlot(Principal principal, @PathVariable Long targetId,@PathVariable Long slotId,@PathVariable Long tagId) {
        this.targetService.deleteTagFromTargetSlot(principal.getName(), targetId, slotId, tagId);

        return ResponseEntity.noContent().build();

    }

    @Override
    public ResponseEntity<Object> addTagToTarget(Principal principal,@PathVariable Long targetId,@PathVariable Long tagId) {
        this.targetService.addTagToTarget(principal.getName(), targetId, tagId);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> deleteTagFromTarget(Principal principal,@PathVariable Long targetId,@PathVariable Long tagId) {
        this.targetService.deleteTagFromTarget(principal.getName(), targetId, tagId);

        return ResponseEntity.noContent().build();
    }


}
