package com.meg.atable.lmt.api.model;


import com.meg.atable.lmt.api.controller.TagRestControllerApi;
import com.meg.atable.lmt.data.entity.TagEntity;
import com.meg.atable.lmt.data.entity.TagExtendedEntity;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class TagResource extends ResourceSupport {

    private final Tag tag;

    public TagResource(TagEntity tag) {
        this.tag = ModelMapper.toModel(tag);
        this.add(linkTo(methodOn(TagRestControllerApi.class)
                .readTag(tag.getId())).withSelfRel());
    }

    public TagResource(TagExtendedEntity tag) {
        this.tag = ModelMapper.toModel(tag);
        this.add(linkTo(methodOn(TagRestControllerApi.class)
                .readTag(tag.getId())).withSelfRel());
    }

    public Tag getTag() {
        return tag;
    }
}