package com.meg.listshop.admin.controller;

import com.meg.listshop.admin.model.PostSearchTags;
import com.meg.listshop.lmt.api.model.FoodSuggestion;
import com.meg.listshop.lmt.api.model.Tag;
import com.meg.listshop.lmt.api.model.TagListResource;
import com.meg.listshop.lmt.api.model.TagOperationPut;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@RestController
@RequestMapping("/admin/tag")
public interface AdminTagRestControllerApi {


    @GetMapping(value = "/category/list")
     ResponseEntity<TagListResource> getCategoryTags();

    @GetMapping(value = "/{tag_id}/food/suggestions")
    ResponseEntity<List<FoodSuggestion>> getFoodSuggestionsForTag(@PathVariable("tagId") Long tagId);

    @PostMapping(value = "/search")
     ResponseEntity<TagListResource> findTags(@RequestBody PostSearchTags searchTags);

    @GetMapping(value = "/standard/list")
     ResponseEntity<TagListResource> getStandardTagList(@RequestParam(value = "filter", required = false) String filter);

    @GetMapping(value = "/user/{userId}/list")
    ResponseEntity<TagListResource> getUserTagList(@PathVariable("userId") Long userId,
                                                   @RequestParam(value = "filter", required = false) String filter);

    @GetMapping(value = "/standard/grid")
    ResponseEntity<TagListResource> getStandardTagListForGrid(HttpServletRequest request);

    @GetMapping(value = "/user/{userId}/grid")
    ResponseEntity<TagListResource> getUserTagListForGrid(@PathVariable("userId") Long userId);

    @DeleteMapping(value = "/delete/{tagId}")
    ResponseEntity<Object> saveTagForDelete(@PathVariable("tagId") Long tagId, @RequestParam(value = "replacementTagId", required = true) Long replacementTagId);

    @PostMapping(value = "{tagId}/children", produces = "application/json", consumes = "application/json")
    ResponseEntity<Object> addChildren(@PathVariable Long tagId, @RequestParam(value = "tagIds", required = false) String filter);

    @PutMapping(value = "{parentId}/child/{childId}", produces = "application/json")
    ResponseEntity assignChildToParent(@PathVariable("parentId") Long parentId, @PathVariable("childId") Long childId);

    @PutMapping(value = "/base/{tagId}", produces = "application/json")
    ResponseEntity assignChildToBaseTag(@PathVariable("tagId") Long tagId);

    @PutMapping(value = "/{tagId}", consumes = "application/json")
    ResponseEntity<Object> updateTag(@PathVariable Long tagId, @RequestBody Tag input);

    @PutMapping(consumes = "application/json")
    ResponseEntity<Object> performOperation(@RequestBody TagOperationPut input);

    @PutMapping(value = "/{fromTagId}/dish/{toTagId}", produces = "application/json")
    ResponseEntity replaceTagsInDishes(HttpServletRequest request, Authentication authentication, @PathVariable("fromTagId") Long tagId, @PathVariable("toTagId") Long toTagId);


}
