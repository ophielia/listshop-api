package com.meg.atable.api.controller;

import com.meg.atable.api.model.ListLayout;
import com.meg.atable.api.model.ListLayoutCategory;
import com.meg.atable.api.model.ListLayoutResource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@RestController
@RequestMapping("/listlayout")
@CrossOrigin
public interface ListLayoutRestControllerApi {


    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    ResponseEntity<Resources<ListLayoutResource>> retrieveListLayouts(Principal principal);

    @RequestMapping(method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    ResponseEntity<Object> createListLayout(Principal principal, @RequestBody ListLayout input);

    @RequestMapping(method = RequestMethod.GET, value = "/{listLayoutId}", produces = "application/json")
    ResponseEntity<ListLayout> readListLayout(Principal principal, @PathVariable("listLayoutId") Long listLayoutId);

    @RequestMapping(method = RequestMethod.DELETE, value = "/{listLayoutId}", produces = "application/json")
    ResponseEntity<ListLayout> deleteListLayout(Principal principal, @PathVariable("listLayoutId") Long listLayoutId);

    @RequestMapping(method = RequestMethod.POST, value = "/{listLayoutId}/category", produces = "application/json")
    ResponseEntity<Object> addCategoryToListLayout(Principal principal, @PathVariable Long listLayoutId, @RequestBody ListLayoutCategory input);

    @RequestMapping(method = RequestMethod.DELETE, value = "/{listLayoutId}/category/{layoutCategoryId}", produces = "application/json")
    ResponseEntity<Object> deleteCategoryFromListLayout(Principal principal, @PathVariable Long listLayoutId, @PathVariable Long layoutCategoryId);

    @RequestMapping(method = RequestMethod.PUT, value = "/{listLayoutId}/category/{layoutCategoryId}", produces = "application/json")
    ResponseEntity<Object> updateCategoryFromListLayout(Principal principal, @PathVariable Long listLayoutId, @RequestBody ListLayoutCategory layoutCategory);

    @RequestMapping(method = RequestMethod.GET, value = "/{listLayoutId}/tag", produces = "application/json")
    ResponseEntity<Object> getUncategorizedTags(Principal principal, @PathVariable Long listLayoutId);

    @RequestMapping(method = RequestMethod.GET, value = "/{listLayoutId}/category/{layoutCategoryId}/tag", produces = "application/json")
    ResponseEntity<Object> getTagsForCategory(Principal principal, @PathVariable Long listLayoutId, @PathVariable Long layoutCategoryId);

    @RequestMapping(method = RequestMethod.POST, value = "/{listLayoutId}/category/{layoutCategoryId}/tag", produces = "application/json")
    ResponseEntity<Object> addTagsToCategory(Principal principal, @PathVariable Long listLayoutId, @PathVariable Long layoutCategoryId, @RequestParam(value = "tags", required = true) String commaSeparatedIds);

    @RequestMapping(method = RequestMethod.DELETE, value = "/{listLayoutId}/category/{layoutCategoryId}/tag", produces = "application/json")
    ResponseEntity<Object> deleteTagsFromCategory(Principal principal, @PathVariable Long listLayoutId, @PathVariable Long layoutCategoryId, @RequestParam(value = "tags", required = true) String commaSeparatedIds);

}