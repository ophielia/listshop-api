package com.meg.atable.api.model;

import com.meg.atable.api.controller.TagInfoRestControllerApi;
import com.meg.atable.api.controller.TagRestControllerApi;
import com.meg.atable.data.entity.TagEntity;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by margaretmartin on 10/06/2017.
 */
public class TagInfoResource extends ResourceSupport {

    private final TagInfo tagInfo;


    public TagInfoResource(List<TagEntity> entities) {
        // create new TagInfo
        // make into resource
        this.tagInfo = new TagInfo(entities);

        this.add(linkTo(methodOn(TagInfoRestControllerApi.class)
                .retrieveTagList("none")).withSelfRel());
    }

    public TagInfo getTagInfo() {
        return tagInfo;
    }
}
