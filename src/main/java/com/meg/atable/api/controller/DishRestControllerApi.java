package com.meg.atable.api.controller;

import com.meg.atable.api.model.Dish;
import com.meg.atable.api.model.DishResource;
import com.meg.atable.api.model.TagResource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@RestController
@RequestMapping("/dish")
@CrossOrigin
public interface DishRestControllerApi {


    @RequestMapping( method = RequestMethod.GET, produces = "application/json")
    ResponseEntity<Resources<DishResource>> retrieveDishes(Principal principal,
                                                           @RequestParam(value = "includedTags", required = false) String includedTags,
                                                           @RequestParam(value = "excludedTags", required = false) String excludedTags
                                                           );

    @RequestMapping(method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    ResponseEntity<Object> createDish(Principal principal, @RequestBody Dish input);

    @RequestMapping(value = "/{dishId}", method = RequestMethod.PUT, consumes = "application/json")
    ResponseEntity<Object> updateDish(Principal principal, @PathVariable Long dishId, @RequestBody Dish input);

    @RequestMapping(method = RequestMethod.GET, value = "/{dishId}", produces = "application/json")
     ResponseEntity<Dish> readDish(Principal principal, @PathVariable("dishId") Long dishId);

    @RequestMapping(method = RequestMethod.GET, value = "/{dishId}/tag", produces = "application/json")
     ResponseEntity<Resources<TagResource>> getTagsByDishId(Principal principal,@PathVariable("dishId") Long dishId);

    @RequestMapping(method = RequestMethod.POST, value = "/{dishId}/tag/{tagId}", produces = "application/json")
     ResponseEntity<Object> addTagToDish(Principal principal,@PathVariable Long dishId, @PathVariable Long tagId);

    @RequestMapping(method = RequestMethod.DELETE, value = "/{dishId}/tag/{tagId}", produces = "application/json")
     ResponseEntity<Object> deleteTagFromDish(Principal principal, @PathVariable Long dishId, @PathVariable Long tagId);

    @RequestMapping(method = RequestMethod.PUT, value = "/{dishId}/tag", produces = "application/json")
     ResponseEntity<Object> addAndRemoveTags(Principal principal, @PathVariable Long dishId,
                                                                    @RequestParam(value = "addTags", required = false) String addTags,
                                                                    @RequestParam(value = "removeTags", required = false) String removeTags);

}
