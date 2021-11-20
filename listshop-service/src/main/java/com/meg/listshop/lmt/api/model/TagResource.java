package com.meg.listshop.lmt.api.model;


import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.entity.TagExtendedEntity;
import org.springframework.hateoas.ResourceSupport;

public class TagResource extends ResourceSupport {

    private final Tag tag;

    public TagResource(TagEntity tag) {
        this.tag = ModelMapper.toModel(tag);
        //this.add(ControllerLinkBuilder.linkTo(methodOn(TagRestControllerApi.class)
        //      .readTag(tag.getId())).withSelfRel());
    }

    public TagResource(TagExtendedEntity tag) {
        this.tag = ModelMapper.toModel(tag);
        //this.add(linkTo(methodOn(TagRestControllerApi.class)
        //      .readTag(tag.getId())).withSelfRel());
    }

    public Tag getTag() {
        return tag;
    }
}