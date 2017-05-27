package com.meg.atable.api;


import com.meg.atable.model.Dish;
import org.springframework.hateoas.ResourceSupport;

import javax.xml.bind.annotation.XmlRootElement;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class DishResource extends ResourceSupport {

    private Dish dish;

    public DishResource(Dish dish) {
        String username = dish.getUser().getUserName();
        this.dish = dish;
        this.add(linkTo(DishRestController.class, username).withRel("dish"));
        this.add(linkTo(methodOn(DishRestController.class, username)
                .readDish(username, dish.getId())).withSelfRel());
    }

    public Dish getDish() {
        return dish;
    }
}