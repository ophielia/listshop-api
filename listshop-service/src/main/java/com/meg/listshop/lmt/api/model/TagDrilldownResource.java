package com.meg.listshop.lmt.api.model;

import com.meg.listshop.lmt.api.controller.TagRestControllerApi;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by margaretmartin on 10/06/2017.
 */
public class TagDrilldownResource extends ResourceSupport {

    private final TagDrilldown tagDrilldown;

    public TagDrilldownResource(FatTag fatTag) {
        this.tagDrilldown = ModelMapper.toModel(fatTag);

        this.add(ControllerLinkBuilder.linkTo(methodOn(TagRestControllerApi.class)
                .readTag(fatTag.getId())).withSelfRel());
    }
    public TagDrilldown getTagDrilldown() {
        return tagDrilldown;
    }
}
