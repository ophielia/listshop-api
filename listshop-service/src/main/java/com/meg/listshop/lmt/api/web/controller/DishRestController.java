package com.meg.listshop.lmt.api.web.controller;

import com.google.common.base.Enums;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.lmt.api.controller.DishRestControllerApi;
import com.meg.listshop.lmt.api.exception.UserNotFoundException;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.data.entity.DishEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.service.DishSearchCriteria;
import com.meg.listshop.lmt.service.DishSearchService;
import com.meg.listshop.lmt.service.DishService;
import com.meg.listshop.lmt.service.tag.TagService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@CrossOrigin
public class DishRestController implements DishRestControllerApi {

    private static final Logger logger = LogManager.getLogger(DishRestController.class);

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
                                                                  @RequestParam(value = "searchFragment", required = false) String searchFragment,
                                                                  @RequestParam(value = "includedTags", required = false) String includedTags,
                                                                  @RequestParam(value = "excludedTags", required = false) String excludedTags,
                                                                  @RequestParam(value = "sortKey", required = false) String sortKey,
                                                                  @RequestParam(value = "sortDirection", required = false) String sortDirection
    ) {
        logger.info("Entered retrieveDishes includedTags: [%s], excludedTags: [%s], sortKey: [%s], sortDirection: [%s]", includedTags, excludedTags, sortKey, sortDirection);
        List<DishResource> dishList;
        if (StringUtils.isEmpty(includedTags) && StringUtils.isEmpty(excludedTags)
                && StringUtils.isEmpty(sortKey) && StringUtils.isEmpty(sortDirection)) {
            dishList = getAllDishes(principal);
        } else {
            dishList = findDishes(principal, includedTags, excludedTags, searchFragment, sortKey, sortDirection);
        }

        Resources<DishResource> dishResourceList = new Resources<>(dishList);
        return ResponseEntity.ok(dishResourceList);
    }

    private List<DishResource> findDishes(Principal principal, String includedTags, String excludedTags, String searchFragment, String sortKey, String sortDirection) {

        UserEntity user = userService.getUserByUserEmail(principal.getName());
        var criteria = new DishSearchCriteria(user.getId());
        if (includedTags != null) {
            List<Long> tagIdList = commaDelimitedToList(includedTags);
            criteria.setIncludedTagIds(tagIdList);
        }
        if (excludedTags != null) {
            List<Long> tagIdList = commaDelimitedToList(excludedTags);
            criteria.setExcludedTagIds(tagIdList);
        }
        if (!StringUtils.isEmpty(sortKey)) {
            var dishSortKey = Enums.getIfPresent(DishSortKey.class, sortKey).orNull();
            criteria.setSortKey(dishSortKey);
        }
        if (!StringUtils.isEmpty(sortDirection)) {
            var dishSortDirection = Enums.getIfPresent(DishSortDirection.class, sortDirection).orNull();
            criteria.setSortDirection(dishSortDirection);
        }
        if (!StringUtils.isEmpty(sortDirection)) {
            var dishSortDirection = Enums.getIfPresent(DishSortDirection.class, sortDirection).orNull();
            criteria.setSortDirection(dishSortDirection);
        }
        if (!StringUtils.isEmpty(searchFragment)) {
            criteria.setNameFragment(searchFragment);
        }
        logger.debug(String.format("Searching for dishes with criteria [%s]. ", criteria));
        return dishSearchService.findDishes(criteria)
                .stream().map(d -> new DishResource(principal, d))
                .collect(Collectors.toList());
    }

    private List<DishResource> getAllDishes(Principal principal) {
        return dishService.getDishesForUserName(principal.getName())
                .stream().map(d -> new DishResource(principal, d))
                .collect(Collectors.toList());

    }

    public ResponseEntity<Object> createDish(Principal principal, @RequestBody Dish input) {
        //MM Validation to be done here
        UserEntity user = userService.getUserByUserEmail(principal.getName());
        DishEntity inputDish = ModelMapper.toEntity(input);
        inputDish.setUserId(user.getId());
        DishEntity result = dishService.create(inputDish);
        List<Tag> tagInputs = input.getTags();
        if (tagInputs != null && !tagInputs.isEmpty()) {
            Set<Long> tagIds = tagInputs.stream()
                    .filter(t -> t.getId() != null)
                    .map(t -> Long.valueOf(t.getId()))
                    .collect(Collectors.toSet());
            tagService.addTagsToDish(principal.getName(), result.getId(), tagIds);
            result = dishService.getDishForUserById(principal.getName(), result.getId());
        }
        var forOneDish = new DishResource(principal, result).getLink("self");
        return ResponseEntity.created(URI.create(forOneDish.getHref())).build();
    }


    public ResponseEntity<Object> updateDish(Principal principal, @PathVariable Long dishId, @RequestBody Dish input) {
        UserEntity user = this.getUserForPrincipal(principal);

        DishEntity dish =  this.dishService
                .getDishForUserById(user.getEmail(), dishId);

        dish.setDescription(input.getDescription());
        dish.setDishName(input.getDishName());
        dish.setReference(input.getReference());
        dishService.save(dish, true);

        return ResponseEntity.noContent().build();
    }


    public ResponseEntity<Dish> readDish(Principal principal, @PathVariable Long dishId) {
        DishEntity dish =  this.dishService
                .getDishForUserById(principal.getName(),dishId);
        List<TagEntity> sortedDishTags = dish.getTags();
        sortedDishTags.sort(Comparator.comparing(TagEntity::getTagType)
                .thenComparing(TagEntity::getName));
        var dishResource = new DishResource(dish, sortedDishTags);

        return new ResponseEntity(dishResource, HttpStatus.OK);
    }

    public ResponseEntity<Resources<TagResource>> getTagsByDishId(HttpServletRequest request, Principal principal, @PathVariable Long dishId) {

        List<TagResource> tagList = tagService
                .getTagsForDish(principal.getName(), dishId)
                .stream().map(te -> ModelMapper.toModel(te))
                .map(tm -> new TagResource(tm))
                .collect(Collectors.toList());
        tagList.forEach(tr -> tr.fillLinks(request, tr));
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

        var ratingUpdateInfo = tagService.getRatingUpdateInfoForDishIds(principal.getName(), Collections.singletonList(dishId));
        var ratingResource = new RatingUpdateInfoResource(ratingUpdateInfo);
        return new ResponseEntity(ratingResource, HttpStatus.OK);

    }

    public ResponseEntity<Object> incrmentRatingForDish(Principal principal, @PathVariable Long dishId,
                                                        @PathVariable Long ratingId,
                                                        @RequestParam(value = "direction", required = true) String direction) {
        var moveDirection = SortOrMoveDirection.valueOf(direction);
        tagService.incrementDishRating(principal.getName(), dishId, ratingId, moveDirection);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<Object> setRatingForDish(Principal principal, @PathVariable Long dishId,
                                                   @PathVariable Long ratingId,
                                                   @PathVariable Integer step) {
        tagService.setDishRating(principal.getName(), dishId, ratingId, step);
        return ResponseEntity.noContent().build();
    }

    private UserEntity getUserForPrincipal(Principal principal) {

        String username = principal.getName();
        UserEntity user = this.userService
                .getUserByUserEmail(username);
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
