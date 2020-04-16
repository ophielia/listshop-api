package com.meg.listshop.lmt.api.model;


import com.meg.listshop.lmt.api.controller.DishRestControllerApi;
import com.meg.listshop.lmt.data.entity.DishEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import org.springframework.hateoas.ResourceSupport;

import java.security.Principal;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class DishResource extends ResourceSupport {

    private final Dish dish;

    public DishResource(Principal principal, DishEntity dishEntity) {
        this.dish = ModelMapper.toModel(dishEntity);

        Long userId = dishEntity.getUserId();
        this.add(linkTo(DishRestControllerApi.class, userId).withRel("dish"));
        this.add(linkTo(methodOn(DishRestControllerApi.class, userId)
                .readDish(principal, dish.getId())).withSelfRel());
        this.add(linkTo(methodOn(DishRestControllerApi.class, userId)
                .getTagsByDishId(principal, dish.getId())).withRel("tags"));
    }

    public DishResource(DishEntity dishEntity, List<TagEntity> tags) {
        this.dish = ModelMapper.toModel(dishEntity,tags);

        Long userId = dishEntity.getUserId();
        this.add(linkTo(DishRestControllerApi.class, userId).withRel("dish"));
        this.add(linkTo(methodOn(DishRestControllerApi.class, userId)
                .readDish( null,dish.getId())).withSelfRel());
        this.add(linkTo(methodOn(DishRestControllerApi.class, userId)
                .getTagsByDishId(null,dish.getId())).withRel("tags"));
    }
    public Dish getDish() {
        return dish;
    }
}