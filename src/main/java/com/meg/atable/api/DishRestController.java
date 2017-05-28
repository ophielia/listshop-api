package com.meg.atable.api;

import com.meg.atable.model.Dish;
import com.meg.atable.service.DishService;
import com.meg.atable.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@RestController
@RequestMapping("/{userId}/dish")
public class DishRestController {

    private final DishService dishService;
    private final UserService userService;

    @Autowired
    DishRestController(DishService dishService,
                       UserService userService) {
        this.dishService = dishService;
        this.userService = userService;
    }


    @RequestMapping(method = RequestMethod.GET,produces = "application/json")
    ResponseEntity<Resources<DishResource>> retrieveDishes(@PathVariable String userId) {
        this.validateUser(userId);

        List<DishResource> dishList = dishService.getDishesForUserName(userId)
                .stream().map(DishResource::new)
                .collect(Collectors.toList());

        Resources<DishResource> dishResourceList = new Resources<>(dishList);
        return new ResponseEntity(dishResourceList,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST,produces = "application/json",consumes = "application/json")
    ResponseEntity<Object> createDish(@PathVariable String userId, @RequestBody Dish input) {
        this.validateUser(userId);

        return this.userService
                .getUserByUserName(userId)
                .map(user -> {
                    Dish result = dishService.save(new Dish(user,
                            input.getDishName(),
                            input.getDescription()));

                    Link forOneDish= new DishResource(result).getLink("self");
                    return ResponseEntity.created(URI.create(forOneDish.getHref())).build();
                })
                .orElse(ResponseEntity.noContent().build());
    }


    @RequestMapping(value = "/{dishId}",method = RequestMethod.PUT,consumes = "application/json")
    ResponseEntity<Object> updateDish(@PathVariable String userId, @PathVariable Long dishId, @RequestBody Dish input) {
        this.validateUser(userId);

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




    @RequestMapping(method = RequestMethod.GET, value = "/{dishId}",produces = "application/json")
    public ResponseEntity<Dish> readDish(@PathVariable String userId, @PathVariable Long dishId) {
        this.validateUser(userId);

        // MM
        // invalid dishId - returns invalid id supplied - 400

        return this.dishService
                .getDishById(dishId)
                .map(dish -> {
                    DishResource dishResource = new DishResource(dish);

                    return new ResponseEntity(dishResource,HttpStatus.OK);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private void validateUser(String userId) {
        this.userService.getUserByUserName(userId).orElseThrow(
                () -> new UserNotFoundException(userId)
        );
    }

}
