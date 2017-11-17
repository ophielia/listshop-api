package com.meg.atable.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meg.atable.controller.ListLayoutRestController;
import com.meg.atable.data.entity.ListLayoutEntity;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by margaretmartin on 06/11/2017.
 */
public class ListLayoutResource extends ResourceSupport {

    @JsonProperty("list_layout")
    private final ListLayout listLayout;

    public ListLayoutResource(ListLayoutEntity listLayoutEntity) {
        this.listLayout = ModelMapper.toModel(listLayoutEntity);

        this.add(linkTo(methodOn(ListLayoutRestController.class)
                .readListLayout(null, listLayout.getLayoutId())).withSelfRel());
    }
}
