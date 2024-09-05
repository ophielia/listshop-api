package com.meg.listshop.lmt.api.web.controller;

import com.google.common.base.Enums;
import com.meg.listshop.auth.service.impl.JwtUser;
import com.meg.listshop.lmt.api.controller.DishRestControllerApi;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.data.entity.DishEntity;
import com.meg.listshop.lmt.data.entity.DishItemEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.service.DishSearchCriteria;
import com.meg.listshop.lmt.service.DishSearchService;
import com.meg.listshop.lmt.service.DishService;
import com.meg.listshop.lmt.service.tag.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@CrossOrigin
public class DishRestController implements DishRestControllerApi {

    private static final Logger logger = LoggerFactory.getLogger(DishRestController.class);

    private final DishService dishService;
    private final DishSearchService dishSearchService;
    private final TagService tagService;

    @Autowired
    DishRestController(DishService dishService,
                       DishSearchService dishSearchService,
                       TagService tagService) {
        this.dishService = dishService;
        this.tagService = tagService;
        this.dishSearchService = dishSearchService;
    }

    @Override
    public ResponseEntity<DishListResource> retrieveDishes(HttpServletRequest request,
                                                           Authentication authentication,
                                                           @RequestParam(value = "searchFragment", required = false) String searchFragment,
                                                           @RequestParam(value = "includedTags", required = false) String includedTags,
                                                           @RequestParam(value = "excludedTags", required = false) String excludedTags,
                                                           @RequestParam(value = "sortKey", required = false) String sortKey,
                                                           @RequestParam(value = "sortDirection", required = false) String sortDirection
    ) {
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        logger.info("Entered retrieveDishes includedTags: [{}], excludedTags: [{}], sortKey: [{}], sortDirection: [{}]", includedTags, excludedTags, sortKey, sortDirection);
        List<DishResource> dishList;
        if (ObjectUtils.isEmpty(includedTags) && ObjectUtils.isEmpty(excludedTags)
                && ObjectUtils.isEmpty(sortKey) && ObjectUtils.isEmpty(sortDirection)) {
            dishList = getAllDishes(userDetails.getId());
        } else {
            dishList = findDishes(userDetails.getId(), includedTags, excludedTags, searchFragment, sortKey, sortDirection);
        }

        DishListResource resource = new DishListResource(dishList);
        resource.fillLinks(request, resource);
        return new ResponseEntity<>(resource, HttpStatus.OK);

    }

    private List<DishResource> findDishes(Long userId, String includedTags, String excludedTags, String searchFragment, String sortKey, String sortDirection) {
        String message = String.format("find dishesfor user [%S] - search [%S]", userId, searchFragment);
        logger.info(message);

        var criteria = new DishSearchCriteria(userId);
        if (includedTags != null) {
            List<Long> tagIdList = commaDelimitedToList(includedTags);
            criteria.setIncludedTagIds(tagIdList);
        }
        if (excludedTags != null) {
            List<Long> tagIdList = commaDelimitedToList(excludedTags);
            criteria.setExcludedTagIds(tagIdList);
        }
        if (!ObjectUtils.isEmpty(sortKey)) {
            var dishSortKey = Enums.getIfPresent(DishSortKey.class, sortKey).orNull();
            criteria.setSortKey(dishSortKey);
        }
        if (!ObjectUtils.isEmpty(sortDirection)) {
            var dishSortDirection = Enums.getIfPresent(DishSortDirection.class, sortDirection).orNull();
            criteria.setSortDirection(dishSortDirection);
        }
        if (!ObjectUtils.isEmpty(searchFragment)) {
            criteria.setNameFragment(searchFragment);
        }
        logger.debug("Searching for dishes with criteria [{}]. ", criteria);
        return dishSearchService.findDishes(criteria).stream()
                .map(d -> ModelMapper.toModel(d, false))
                .map(DishResource::new)
                .collect(Collectors.toList());
    }

    private List<DishResource> getAllDishes(Long userId) {
        return dishService.getDishesForUser(userId).stream()
                .map(d -> ModelMapper.toModel(d, false))
                .map(DishResource::new)
                .collect(Collectors.toList());

    }

    public ResponseEntity<Object> createDish(HttpServletRequest request, Authentication authentication, @RequestBody Dish input) {
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        String message = String.format("create new dish for user [%S]", userDetails.getId());
        logger.info(message);
        DishEntity inputDish = ModelMapper.toEntity(input);
        inputDish.setUserId(userDetails.getId());
        DishEntity result = dishService.createDish(userDetails.getId(), inputDish);
        List<Tag> tagInputs = input.getTags();
        if (tagInputs != null && !tagInputs.isEmpty()) {
            Set<Long> tagIds = tagInputs.stream()
                    .filter(t -> t.getId() != null)
                    .map(t -> Long.valueOf(t.getId()))
                    .collect(Collectors.toSet());
            tagService.addTagsToDish(userDetails.getId(), result.getId(), tagIds);
            result = dishService.getDishForUserById(userDetails.getId(), result.getId());
        }
        DishResource resource = new DishResource(ModelMapper.toModel(result, false));
        String link = resource.selfLink(request, resource).toString();
        return ResponseEntity.created(URI.create(link)).build();
    }


    public ResponseEntity<Object> updateDish(Authentication authentication, @PathVariable Long dishId, @RequestBody Dish input) {
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        String message = String.format("updating dish [%S] for user [%S]", dishId, userDetails.getId());
        logger.info(message);


        DishEntity dish = this.dishService
                .getDishForUserById(userDetails.getId(), dishId);

        dish.setDescription(input.getDescription());
        dish.setDishName(input.getDishName());
        dish.setReference(input.getReference());
        dishService.save(dish, true);

        return ResponseEntity.noContent().build();
    }


    public ResponseEntity<DishResource> readDish(HttpServletRequest request, Authentication authentication, @PathVariable Long dishId) {
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        String message = String.format("retrieving dish [%S] for user [%S]", dishId, userDetails.getId());
        logger.info(message);
        DishEntity dish = this.dishService
                .getDishForUserById(userDetails.getId(), dishId);

        List<DishItemEntity> sortedDishItems = dish.getItems();
        Function<DishItemEntity, TagEntity> getTag = DishItemEntity::getTag;
        Function<DishItemEntity, TagType> tagType = getTag.andThen(TagEntity::getTagType);
        Function<DishItemEntity, String> tagName = getTag.andThen(TagEntity::getName);

        sortedDishItems.sort(Comparator.comparing(tagType).thenComparing(tagName));
        dish.setItems(sortedDishItems);

        DishResource resource = new DishResource(ModelMapper.toModel(dish, true));

        return new ResponseEntity(resource, HttpStatus.OK);
    }

    public ResponseEntity<CollectionModel<TagResource>> getTagsByDishId(HttpServletRequest request, Authentication authentication, @PathVariable Long dishId) {
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        String message = String.format("retrieving dish [%S] for user [%S]", dishId, userDetails.getId());
        logger.info(message);
        List<TagResource> tagList = tagService
                .getItemsForDish(userDetails.getId(), dishId)
                .stream().map(ModelMapper::itemToTagModel)
                .map(TagResource::new)
                .collect(Collectors.toList());
        tagList.forEach(tr -> tr.fillLinks(request, tr));
        return new ResponseEntity(tagList, HttpStatus.OK);
    }

    public ResponseEntity<Object> addTagToDish(Authentication authentication, @PathVariable Long dishId, @PathVariable Long tagId) {
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        String message = String.format("adding tag [%S] dish [%S] for user [%S]", tagId, dishId, userDetails.getId());
        logger.info(message);

        this.tagService.addTagToDish(userDetails.getId(), dishId, tagId);

        return ResponseEntity.noContent().build();

    }

    @Override
    public ResponseEntity<Object> deleteTagFromDish(Authentication authentication, @PathVariable Long dishId, @PathVariable Long tagId) {
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        String message = String.format("deleting tag [%S] from dish [%S] for user [%S]", tagId, dishId, userDetails.getId());
        logger.info(message);

        int updated = this.tagService.deleteTagFromDish(userDetails.getId(), dishId, tagId);

        if (updated == 1) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).build();
        }
    }

    @Override
    public ResponseEntity<Object> addAndRemoveTags(Authentication authentication, @PathVariable Long dishId,
                                                   @RequestParam(value = "addTags", required = false) String addTags,
                                                   @RequestParam(value = "removeTags", required = false) String removeTags) {

        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        String message = String.format("add and remove tags from dish [%S] for user [%S]", dishId, userDetails.getId());
        logger.info(message);
        if (addTags != null && !addTags.isEmpty()) {
            Set<Long> tagIds = commaDelimitedToSet(addTags);
            this.tagService.addTagsToDish(userDetails.getId(), dishId, tagIds);
        }

        boolean partialContent = false;
        if (removeTags != null && !removeTags.isEmpty()) {
            Set<Long> tagIds = commaDelimitedToSet(removeTags);
            int updated = this.tagService.removeTagsFromDish(userDetails.getId(), dishId, tagIds);
            partialContent = updated != tagIds.size();
        }


        if (partialContent) {
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).build();
        } else {
            return ResponseEntity.noContent().build();
        }

    }

    public ResponseEntity<RatingUpdateInfoResource> getRatingUpdateInfo(Authentication authentication, @PathVariable Long dishId) {
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        String message = String.format("get rating update info for dish [%S] for user [%S]", dishId, userDetails.getId());
        logger.info(message);
        var ratingUpdateInfo = tagService.getRatingUpdateInfoForDishIds(Collections.singletonList(dishId));
        var ratingResource = new RatingUpdateInfoResource(ratingUpdateInfo);

        return new ResponseEntity<>(ratingResource, HttpStatus.OK);

    }

    public ResponseEntity<Object> incrementRatingForDish(Authentication authentication, @PathVariable Long dishId,
                                                         @PathVariable Long ratingId,
                                                         @RequestParam(value = "direction") String direction) {
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        String message = String.format("increment rating for dish [%S],  user [%S] direction [%S]", dishId, userDetails.getId(), direction);
        logger.info(message);
        var moveDirection = SortOrMoveDirection.valueOf(direction);
        tagService.incrementDishRating(userDetails.getId(), dishId, ratingId, moveDirection);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<Object> setRatingForDish(Authentication authentication, @PathVariable Long dishId,
                                                   @PathVariable Long ratingId,
                                                   @PathVariable Integer step) {
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        String message = String.format("set rating for dish [%S] for user [%S]", dishId, userDetails.getId());
        logger.info(message);
        tagService.setDishRating(userDetails.getId(), dishId, ratingId, step);
        return ResponseEntity.noContent().build();
    }

    private List<Long> commaDelimitedToList(String commaSeparatedIds) {
// translate tags into list of Long ids
        if (commaSeparatedIds == null) {
            return new ArrayList<>();
        }
        String[] ids = commaSeparatedIds.split(",");
        if (ids.length == 0) {
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
        if (ids.length == 0) {
            return new HashSet<>();
        }
        return Arrays.stream(ids).map(Long::valueOf).collect(Collectors.toSet());

    }

}
