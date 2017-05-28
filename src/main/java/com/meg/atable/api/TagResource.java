package com.meg.atable.api;


import com.meg.atable.model.Tag;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class TagResource extends ResourceSupport {

    private final Tag tag;

    public TagResource(Tag tag) {
        this.tag = tag;
        this.add(linkTo(methodOn(TagRestController.class)
                .readTag(tag.getId())).withSelfRel());
    }

    public Tag getTag() {
        return tag;
    }
}