package com.meg.atable.api.model;

import com.meg.atable.api.controller.TagInfoRestControllerApi;
import com.meg.atable.api.controller.TagRestControllerApi;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by margaretmartin on 10/06/2017.
 */
public class TagInfoResource extends ResourceSupport {

    private final TagInfo tagInfo;

    public TagInfoResource(TagInfo tag) {
        this.tagInfo = tag;
        this.add(linkTo(methodOn(TagInfoRestControllerApi.class)
                .readTag(tag.getId())).withSelfRel());
        this.add(linkTo(methodOn(TagRestControllerApi.class)
                .readTag(tag.getId())).withRel("SimpleTag"));
    }

    public TagInfo getTagInfo() {
        return tagInfo;
    }
}
