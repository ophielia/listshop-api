package com.meg.atable.controller;

import com.meg.atable.api.UserNotFoundException;
import com.meg.atable.api.controller.DishRestControllerApi;
import com.meg.atable.api.model.Dish;
import com.meg.atable.api.model.DishResource;
import com.meg.atable.api.model.TagResource;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.data.entity.DishEntity;
import com.meg.atable.service.DishService;
import com.meg.atable.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Controller
public class DishRestController implements DishRestControllerApi {

    private final DishService dishService;
    private final TagService tagService;
    private final UserService userService;

    @Autowired
    DishRestController(DishService dishService,
                       UserService userService,
                       TagService tagService) {
        this.dishService = dishService;
        this.userService = userService;
        this.tagService = tagService;
    }

    @Override
    public ResponseEntity<Resources<DishResource>> retrieveDishes(Principal principal) {
        this.validateUser(principal);

        List<DishResource> dishList = dishService.getDishesForUserName(principal.getName())
                .stream().map(DishResource::new)
                .collect(Collectors.toList());

        Resources<DishResource> dishResourceList = new Resources<>(dishList);
        return new ResponseEntity(dishResourceList, HttpStatus.OK);
    }

    public ResponseEntity<Object> createDish(Principal principal, @RequestBody Dish input) {
        this.validateUser(principal);

        UserAccountEntity user = userService.getUserByUserName(principal.getName());
        DishEntity result = dishService.save(new DishEntity(user,
                input.getDishName(),
                input.getDescription()));

        Link forOneDish = new DishResource(result).getLink("self");
        return ResponseEntity.created(URI.create(forOneDish.getHref())).build();
    }


    public ResponseEntity<Object> updateDish(Principal principal, @PathVariable Long dishId, @RequestBody Dish input) {
        this.validateUser(principal);

        // MM
        // invalid dishId - returns invalid id supplied - 400

        // MM
        // invalid contents of input - returns 405 validation exception

        return this.dishService
                .getDishById(dishId)
                .map(dish -> {
                    dish.setDescription(input.getDescription());
                    dish.setDishName(input.getDishName());

                    dishService.save(dish);

                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }


    public ResponseEntity<Dish> readDish(Principal principal, @PathVariable Long dishId) {
        this.validateUser(principal);

        // MM
        // invalid dishId - returns invalid id supplied - 400

        return this.dishService
                .getDishById(dishId)
                .map(dish -> {
                    DishResource dishResource = new DishResource(dish);

                    return new ResponseEntity(dishResource, HttpStatus.OK);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private void validateUser(UserAccountEntity principal) {
        if (principal == null) {
            throw new UserNotFoundException("principal is null");
        }
        String username = principal.getUsername();
        UserAccountEntity user = this.userService
                .getUserByUserName(username);
        if (user == null) {
            throw new UserNotFoundException("username");
        }

    }

    private void validateUser(Principal principal) {

        String username = principal.getName();
        UserAccountEntity user = this.userService
                .getUserByUserName(username);
        if (user == null) {
            throw new UserNotFoundException("username");
        }

    }

    public ResponseEntity<Resources<TagResource>> getTagsByDishId(Principal principal, @PathVariable  Long dishId) {
        validateUser(principal);

        List<TagResource> tagList = tagService
                .getTagsForDish(dishId)
                .stream().map(TagResource::new)
                .collect(Collectors.toList());
        return new ResponseEntity(tagList, HttpStatus.OK);
    }

    public ResponseEntity<Object> addTagToDish(Principal principal,@PathVariable Long dishId, @PathVariable Long tagId) {
        validateUser(principal);

        this.tagService.addTagToDish(dishId, tagId);

        return ResponseEntity.noContent().build();

    }


    private void validateUser(String userId) {
        if (this.userService.getUserByUserName(userId) == null) {
            throw new UserNotFoundException(userId);
        }
    }

}
