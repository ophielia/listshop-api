package com.meg.listshop.lmt.api.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.meg.listshop.lmt.api.web.controller.TargetRestController;
import com.meg.listshop.lmt.data.entity.TargetEntity;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class TargetResource extends ResourceSupport {

    @JsonProperty("target")
    private final Target target;

    public TargetResource(TargetEntity targetEntity) {
        this.target = ModelMapper.toModel(targetEntity);

        Long targetId = targetEntity.getTargetId();
        this.add(linkTo(TargetRestController.class, targetId).withRel("target"));
        this.add(linkTo(methodOn(TargetRestController.class, targetId)
                .readTarget(null, target.getTargetId())).withSelfRel());
    }

    public Target getTarget() {
        return target;
    }
}