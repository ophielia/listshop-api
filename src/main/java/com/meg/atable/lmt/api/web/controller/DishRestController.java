package com.meg.atable.lmt.api.web.controller;

import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.lmt.api.controller.DishRestControllerApi;
import com.meg.atable.lmt.api.exception.UserNotFoundException;
import com.meg.atable.lmt.api.model.*;
import com.meg.atable.lmt.data.entity.DishEntity;
import com.meg.atable.lmt.data.entity.TagEntity;
import com.meg.atable.lmt.service.DishSearchCriteria;
import com.meg.atable.lmt.service.DishSearchService;
import com.meg.atable.lmt.service.DishService;
import com.meg.atable.lmt.service.tag.TagService;
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
import java.util.*;
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

        UserAccountEntity user = userService.getUserByUserName(principal.getName());
        DishEntity result = dishService.save(new DishEntity(user.getId(),
                input.getDishName(),
                input.getDescription()), false);
        List<Tag> tagInputs = input.getTags();
        if (tagInputs !=null && !tagInputs.isEmpty()) {
            Set<Long> tagIds = tagInputs.stream()
                    .filter(t -> t.getId() != null)
                    .map(t -> new Long(t.getId()))
                    .collect(Collectors.toSet());
            tagService.addTagsToDish(principal.getName(),result.getId(),tagIds);
            result = dishService.getDishForUserById(principal.getName(),result.getId());
        }
        Link forOneDish = new DishResource(result).getLink("self");
        return ResponseEntity.created(URI.create(forOneDish.getHref())).build();
    }


    public ResponseEntity<Object> updateDish(Principal principal, @PathVariable Long dishId, @RequestBody Dish input) {
        UserAccountEntity user = this.getUserForPrincipal(principal);

        DishEntity dish =  this.dishService
                .getDishForUserById(user.getUsername(), dishId);

        dish.setDescription(input.getDescription());
        dish.setDishName(input.getDishName());

        dishService.save(dish, true);

        return ResponseEntity.noContent().build();
    }


    public ResponseEntity<Dish> readDish(Principal principal, @PathVariable Long dishId) {
        DishEntity dish =  this.dishService
                .getDishForUserById(principal.getName(),dishId);
        List<TagEntity> sortedDishTags = dish.getTags();
        sortedDishTags.sort(Comparator.comparing(TagEntity::getTagType)
                .thenComparing(TagEntity::getName));
        DishResource dishResource = new DishResource(dish, sortedDishTags);

        return new ResponseEntity(dishResource, HttpStatus.OK);
    }

    public ResponseEntity<Resources<TagResource>> getTagsByDishId(Principal principal, @PathVariable Long dishId) {

        List<TagResource> tagList = tagService
                .getTagsForDish(principal.getName(), dishId)
                .stream().map(TagResource::new)
                .collect(Collectors.toList());
        return new ResponseEntity(tagList, HttpStatus.OK);
    }

    public ResponseEntity<Object> addTagToDish(Principal principal, @PathVariable Long dishId, @PathVariable Long tagId) {
        getUserForPrincipal(principal);

        this.tagService.addTagToDish(principal.getName(),dishId, tagId);

        return ResponseEntity.noContent().build();

    }

    @Override
    public ResponseEntity<Object> deleteTagFromDish(Principal principal, @PathVariable Long dishId, @PathVariable Long tagId) {
        getUserForPrincipal(principal);

        this.tagService.deleteTagFromDish(principal.getName(), dishId, tagId);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> addAndRemoveTags(Principal principal, @PathVariable Long dishId,
                                                                  @RequestParam(value = "addTags", required = false) String addTags,
                                                                  @RequestParam(value = "removeTags", required = false) String removeTags)
    {

        if (addTags != null && !addTags.isEmpty()) {
            Set<Long> tagIds = commaDelimitedToSet(addTags);
            this.tagService.addTagsToDish(principal.getName(), dishId, tagIds);
        }

        if (removeTags != null && !removeTags.isEmpty()) {
            Set<Long> tagIds = commaDelimitedToSet(removeTags);
            this.tagService.removeTagsFromDish(principal.getName(), dishId, tagIds);
        }

        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<RatingUpdateInfoResource> getRatingUpdateInfo(Principal principal, @PathVariable Long dishId) {

        RatingUpdateInfo ratingUpdateInfo = tagService.getRatingUpdateInfoForDishIds(principal.getName(), Collections.singletonList(dishId));
        RatingUpdateInfoResource ratingResource = new RatingUpdateInfoResource(ratingUpdateInfo);
        return new ResponseEntity(ratingResource, HttpStatus.OK);

    }

    public ResponseEntity<Object> updateRatingForDish(Principal principal, @PathVariable Long dishId,
                                               @PathVariable Long ratingId,
                                               @RequestParam(value = "direction", required = true) String direction) {
        SortOrMoveDirection moveDirection = SortOrMoveDirection.valueOf(direction);
        tagService.incrementDishRating(principal.getName(),dishId, ratingId, moveDirection);
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

    private Set<Long> commaDelimitedToSet(String commaSeparatedIds) {
// translate tags into list of Long ids
        if (commaSeparatedIds == null) {
            return new HashSet<>();
        }
        String[] ids = commaSeparatedIds.split(",");
        if (ids == null || ids.length == 0) {
            return new HashSet<>();
        }
        return Arrays.stream(ids).map(Long::valueOf).collect(Collectors.toSet());

    }

}
