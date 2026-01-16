package com.meg.listshop.lmt.api.controller;

import com.meg.listshop.lmt.api.model.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@RestController
@RequestMapping("/dish")
@CrossOrigin
public interface DishRestControllerApi {


    @GetMapping(produces = "application/json")
    ResponseEntity<DishListResource> retrieveDishes(HttpServletRequest request,
                                                    Authentication authentication,
                                                    @RequestParam(value = "searchFragment", required = false) String searchFragment,
                                                    @RequestParam(value = "includedTags", required = false) String includedTags,
                                                    @RequestParam(value = "excludedTags", required = false) String excludedTags,
                                                    @RequestParam(value = "sortKey", required = false) String sortKey,
                                                    @RequestParam(value = "sortDirection", required = false) String sortDirection
    );

    @PostMapping(produces = "application/json", consumes = "application/json")
    ResponseEntity<Object> createDish(HttpServletRequest request, Authentication authentication, @RequestBody Dish input);

    @PutMapping(value = "/{dishId}", consumes = "application/json")
    ResponseEntity<Object> updateDish(Authentication authentication, @PathVariable("dishId") Long dishId, @RequestBody Dish input);

    @GetMapping(value = "/{dishId}", produces = "application/json")
    ResponseEntity<DishResource> readDish(HttpServletRequest request, Authentication authentication, @PathVariable("dishId") Long dishId);

    @GetMapping(value = "/{dishId}/tag", produces = "application/json")
    ResponseEntity<CollectionModel<TagResource>> getTagsByDishId(HttpServletRequest request, Authentication authentication, @PathVariable("dishId") Long dishId);

    @PostMapping(value = "/{dishId}/tag/{tagId}", produces = "application/json")
    ResponseEntity<Object> addTagToDish(Authentication authentication, @PathVariable("dishId") Long dishId,
                                        @PathVariable("tagId") Long tagId);

    @DeleteMapping(value = "/{dishId}/tag/{tagId}", produces = "application/json")
    ResponseEntity<Object> deleteTagFromDish(Authentication authentication, @PathVariable("dishId") Long dishId,
                                             @PathVariable("tagId") Long tagId);

    @PutMapping(value = "/{dishId}/tag", produces = "application/json")
    ResponseEntity<Object> addAndRemoveTags(Authentication authentication, @PathVariable("dishId") Long dishId,
                                            @RequestParam(value = "addTags", required = false) String addTags,
                                             @RequestParam(value = "removeTags", required = false) String removeTags);

    @GetMapping(value = "/{dishId}/ratings", produces = "application/json")
    ResponseEntity<RatingUpdateInfoResource> getRatingUpdateInfo(Authentication authentication, @PathVariable("dishId") Long dishId);

    @PostMapping(value = "/{dishId}/rating/{ratingId}", produces = "application/json")
    ResponseEntity<Object> incrementRatingForDish(Authentication authentication, @PathVariable("dishId") Long dishId,
                                                  @PathVariable("ratingId") Long ratingId,
                                                  @RequestParam(value = "action", required = true) String direction);

    @PutMapping(value = "/{dishId}/rating/{ratingId}/{step}", produces = "application/json")
    ResponseEntity<Object> setRatingForDish(Authentication authentication, @PathVariable("dishId") Long dishId,
                                            @PathVariable("ratingId") Long ratingId,
                                            @PathVariable("step") Integer step);
}
