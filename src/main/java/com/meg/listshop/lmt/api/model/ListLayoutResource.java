package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meg.listshop.lmt.api.web.controller.ListLayoutRestController;
import com.meg.listshop.lmt.data.entity.ListLayoutEntity;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by margaretmartin on 06/11/2017.
 */
public class ListLayoutResource extends ResourceSupport {

    @JsonProperty("list_layout")
    private final ListLayout listLayout;

    public ListLayoutResource(ListLayoutEntity listLayoutEntity, List<Category> categories) {
        listLayout = ModelMapper.toModel(listLayoutEntity, categories);

        this.add(linkTo(methodOn(ListLayoutRestController.class)
                .readListLayout(null, listLayout.getLayoutId())).withSelfRel());
    }
}
