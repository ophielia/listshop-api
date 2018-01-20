package com.meg.atable.controller;

import com.meg.atable.api.UserNotFoundException;
import com.meg.atable.api.controller.DishRestControllerApi;
import com.meg.atable.api.model.Dish;
import com.meg.atable.api.model.DishResource;
import com.meg.atable.api.model.TagResource;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.data.entity.DishEntity;
import com.meg.atable.service.DishSearchCriteria;
import com.meg.atable.service.DishSearchService;
import com.meg.atable.service.DishService;
import com.meg.atable.service.tag.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@CrossOrigin
public class DishRestController implements DishRestControllerApi {

    private final DishService dishService;
    private final DishSearchService dishSearchService;
    private final TagService tagService;
    private final UserService userService;

    @Autowired
    DishRestController(DishService dishService,
                       UserService userService,
                       DishSearchService dishSearchService,
                       TagService tagService) {
        this.dishService = dishService;
        this.userService = userService;
        this.tagService = tagService;
        this.dishSearchService = dishSearchService;
    }

    @Override
    public ResponseEntity<Resources<DishResource>> retrieveDishes(Principal principal,
                                                                  @RequestParam(value = "includedTags", required = false) String includedTags,
                                                                  @RequestParam(value = "excludedTags", required = false) String excludedTags
    ) {
        List<DishResource> dishList;
        if (includedTags == null && excludedTags == null) {
            dishList = getAllDishes(principal);
        } else {
            dishList = findDishes(principal, includedTags, excludedTags);
        }

        Resources<DishResource> dishResourceList = new Resources<>(dishList);
        return new ResponseEntity(dishResourceList, HttpStatus.OK);
    }

    private List<DishResource> findDishes(Principal principal, String includedTags, String excludedTags) {

        UserAccountEntity user = userService.getUserByUserName(principal.getName());
        DishSearchCriteria criteria = new DishSearchCriteria(user.getId());
        if (includedTags != null) {
            List<Long> tagIdList = commaDelimitedToList(includedTags);
            criteria.setIncludedTagIds(tagIdList);
        }
        if (excludedTags != null) {
            List<Long> tagIdList = commaDelimitedToList(excludedTags);
            criteria.setExcludedTagIds(tagIdList);
        }
        return dishSearchService.findDishes(criteria)
                .stream().map(DishResource::new)
                .collect(Collectors.toList());
    }

    private List<DishResource> getAllDishes(Principal principal) {
        return dishService.getDishesForUserName(principal.getName())
                .stream().map(DishResource::new)
                .collect(Collectors.toList());

    }

    public ResponseEntity<Object> createDish(Principal principal, @RequestBody Dish input) {
        //this.getUserForPrincipal(principal);

        UserAccountEntity user = userService.getUserByUserName(principal.getName());
        DishEntity result = dishService.save(new DishEntity(user.getId(),
                input.getDishName(),
                input.getDescription()), false);

        Link forOneDish = new DishResource(result).getLink("self");
        return ResponseEntity.created(URI.create(forOneDish.getHref())).build();
    }


    public ResponseEntity<Object> updateDish(Principal principal, @PathVariable Long dishId, @RequestBody Dish input) {
        UserAccountEntity user = this.getUserForPrincipal(principal);

        return this.dishService
                .getDishForUserById(user.getUsername(), dishId)
                .map(dish -> {
                    dish.setDescription(input.getDescription());
                    dish.setDishName(input.getDishName());

                    dishService.save(dish, true);

                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }


    public ResponseEntity<Dish> readDish(Principal principal, @PathVariable Long dishId) {
        return this.dishService
                .getDishById(dishId)
                .map(dish -> {
                    DishResource dishResource = new DishResource(dish, dish.getTags());

                    return new ResponseEntity(dishResource, HttpStatus.OK);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<Resources<TagResource>> getTagsByDishId(Principal principal, @PathVariable Long dishId) {

        List<TagResource> tagList = tagService
                .getTagsForDish(dishId)
                .stream().map(TagResource::new)
                .collect(Collectors.toList());
        return new ResponseEntity(tagList, HttpStatus.OK);
    }

    public ResponseEntity<Object> addTagToDish(Principal principal, @PathVariable Long dishId, @PathVariable Long tagId) {
        getUserForPrincipal(principal);

        this.tagService.addTagToDish(dishId, tagId);

        return ResponseEntity.noContent().build();

    }

    @Override
    public ResponseEntity<Object> deleteTagFromDish(Principal principal, @PathVariable Long dishId, @PathVariable Long tagId) {
        getUserForPrincipal(principal);

        this.tagService.deleteTagFromDish(dishId, tagId);

        return ResponseEntity.noContent().build();
    }


    private UserAccountEntity getUserForPrincipal(Principal principal) {

        String username = principal.getName();
        UserAccountEntity user = this.userService
                .getUserByUserName(username);
        if (user == null) {
            throw new UserNotFoundException("username");
        }
        return user;
    }

    private List<Long> commaDelimitedToList(String commaSeparatedIds) {
// translate tags into list of Long ids
        if (commaSeparatedIds == null) {
            return new ArrayList<>();
        }
        String[] ids = commaSeparatedIds.split(",");
        if (ids == null || ids.length == 0) {
            return new ArrayList<>();
        }
        return Arrays.stream(ids).map(Long::valueOf).collect(Collectors.toList());

    }

}
