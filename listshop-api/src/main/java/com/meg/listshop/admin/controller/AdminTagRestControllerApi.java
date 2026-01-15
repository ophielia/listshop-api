package com.meg.listshop.admin.controller;

import com.meg.listshop.admin.model.PostSearchTags;
import com.meg.listshop.admin.model.PostUpdateTags;
import com.meg.listshop.lmt.api.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@RestController
@RequestMapping("/admin/tag")
public interface AdminTagRestControllerApi {

    @GetMapping(value = "/{tagId}/food/suggestions")
    ResponseEntity<FoodListResource> getFoodSuggestionsForTag(@PathVariable("tagId") Long tagId,
                                                              @RequestParam(value = "searchTerm", required = false) String searchTerm);

    @GetMapping(value = "/food/suggestions")
    ResponseEntity<FoodListResource> getFoodSuggestionsForTerm(@RequestParam(value = "searchTerm", required = true) String searchTerm);

    @PostMapping(value = "/{tagId}/food/{foodId}")
    ResponseEntity<Object> assignFoodToTag(@PathVariable("tagId") Long tagId, @PathVariable("foodId") Long foodId);

    @PostMapping(value = "/{tagId}/liquid/{isLiquid}")
    ResponseEntity<Object> assignLiquidProperty(@PathVariable("tagId") Long tagId, @PathVariable("isLiquid") Boolean foodId);

    @GetMapping(value = "/food/category/mappings")
    ResponseEntity<CategoryMappingListResource> getFoodCategoryMappings();

    @GetMapping(value = "/food/category")
    ResponseEntity<FoodCategoryListResource> getFoodCategories();


    @PostMapping(value = "/{tagId}/food/category/{categoryId}")
    ResponseEntity<Object> assignFoodCategory(@PathVariable("tagId") Long tagId, @PathVariable("categoryId") Long categoryId);

    @GetMapping(value = "/{tagId}/fullinfo")
    ResponseEntity<AdminTagFullInfoResource> getFullTagInfo(@PathVariable("tagId") Long tagId);

    @PostMapping(value = "/search")
    ResponseEntity<TagListResource> findTags(@RequestBody PostSearchTags searchTags);


    @DeleteMapping(value = "/delete/{tagId}")
    ResponseEntity<Object> saveTagForDelete(@PathVariable Long tagId, @RequestParam(value = "replacementTagId") Long replacementTagId);

    @PostMapping(value = "/{tagId}/children")
    ResponseEntity<Object> addChildren(@PathVariable("tagId") Long tagId, @RequestBody PostUpdateTags tagIdPost);

    @PutMapping(value = "/{parentId}/child/{childId}", produces = "application/json")
    ResponseEntity<Object> assignChildToParent(@PathVariable("parentId") Long parentId, @PathVariable("childId") Long childId);

    @PutMapping(value = "/base/{tagId}", produces = "application/json")
    ResponseEntity<Object> assignChildToBaseTag(@PathVariable("tagId") Long tagId);

    @PutMapping(value = "/{tagId}", consumes = "application/json")
    ResponseEntity<Object> updateTag(@PathVariable("tagId") Long tagId, @RequestBody Tag input);

    @PutMapping(consumes = "application/json")
    ResponseEntity<Object> performOperation(@RequestBody TagOperationPut input);

    @PutMapping(value = "/{fromTagId}/dish/{toTagId}", produces = "application/json")
    ResponseEntity<Object> replaceTagsInDishes(HttpServletRequest request, Authentication authentication, @PathVariable("fromTagId") Long tagId, @PathVariable("toTagId") Long toTagId);


}
