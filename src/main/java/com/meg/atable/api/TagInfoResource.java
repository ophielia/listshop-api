package com.meg.atable.api;

import com.meg.atable.model.Tag;
import com.meg.atable.model.TagInfo;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by margaretmartin on 10/06/2017.
 */
public class TagInfoResource extends ResourceSupport
{

    private final TagInfo tagInfo;

    public TagInfoResource(TagInfo tag) {
        this.tagInfo = tag;
        this.add(linkTo(methodOn(TagInfoRestController.class)
                .readTag(tag.getId())).withSelfRel());
        this.add(linkTo(methodOn(TagRestController.class)
                .readTag(tag.getId())).withRel("SimpleTag"));
    }

    public TagInfo getTagInfo() {
        return tagInfo;
    }
}
