package com.meg.atable.api;


import com.meg.atable.model.Dish;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class DishResource extends ResourceSupport {

    private Dish dish;

    public DishResource(Dish dish) {
        String username = dish.getUserAccount().getUserName();
        this.dish = dish;
        this.add(linkTo(DishRestController.class, username).withRel("dish"));
        this.add(linkTo(methodOn(DishRestController.class, username)
                .readDish(username, dish.getId())).withSelfRel());
        this.add(linkTo(methodOn(DishRestController.class, username)
                .getTagsByDishId(dish.getId())).withRel("tags"));
    }

    public Dish getDish() {
        return dish;
    }
}