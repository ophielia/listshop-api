package com.meg.listshop.lmt.api.controller;


import com.meg.listshop.lmt.api.model.ListLayout;
import com.meg.listshop.lmt.api.model.ListLayoutCategory;
import com.meg.listshop.lmt.api.model.ListLayoutListResource;
import com.meg.listshop.lmt.api.model.ListLayoutResource;
import com.meg.listshop.lmt.service.ListLayoutException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@RestController
@RequestMapping("/listlayout")
@CrossOrigin
public interface ListLayoutRestControllerApi {


    @GetMapping(produces = "application/json")
    ResponseEntity<ListLayoutListResource> retrieveListLayouts(HttpServletRequest request, Principal principal);

    @PostMapping(produces = "application/json", consumes = "application/json")
    ResponseEntity<Object> createListLayout(HttpServletRequest request, Principal principal, @RequestBody ListLayout input);

    @GetMapping(value = "/{listLayoutId}", produces = "application/json")
    ResponseEntity<ListLayoutResource> readListLayout(HttpServletRequest request, Principal principal, @PathVariable("listLayoutId") Long listLayoutId);

    @GetMapping(value = "/default", produces = "application/json")
    ResponseEntity<ListLayout> readDefaultListLayout(HttpServletRequest request);

    @DeleteMapping(value = "/{listLayoutId}", produces = "application/json")
    ResponseEntity<ListLayout> deleteListLayout(Principal principal, @PathVariable("listLayoutId") Long listLayoutId);

    @PostMapping(value = "/{listLayoutId}/category", produces = "application/json")
    ResponseEntity<Object> addCategoryToListLayout(Principal principal, @PathVariable Long listLayoutId, @RequestBody ListLayoutCategory input);

    @DeleteMapping(value = "/{listLayoutId}/category/{layoutCategoryId}", produces = "application/json")
    ResponseEntity<Object> deleteCategoryFromListLayout(Principal principal, @PathVariable Long listLayoutId, @PathVariable Long layoutCategoryId) throws ListLayoutException;

    @PutMapping(value = "/{listLayoutId}/category/{layoutCategoryId}", produces = "application/json")
    ResponseEntity<Object> updateCategoryFromListLayout(Principal principal, @PathVariable Long layoutCategoryId, @RequestBody ListLayoutCategory layoutCategory);

    @GetMapping(value = "/{listLayoutId}/tag", produces = "application/json")
    ResponseEntity<Object> getUncategorizedTags(HttpServletRequest request, Principal principal, @PathVariable Long listLayoutId);

    @GetMapping(value = "/{listLayoutId}/category/{layoutCategoryId}/tag", produces = "application/json")
    ResponseEntity<Object> getTagsForCategory(HttpServletRequest request, Principal principal, @PathVariable Long listLayoutId, @PathVariable Long layoutCategoryId);

    @PostMapping(value = "/{listLayoutId}/category/{layoutCategoryId}/tag", produces = "application/json")
    ResponseEntity<Object> addTagsToCategory(Principal principal, @PathVariable Long listLayoutId, @PathVariable Long layoutCategoryId, @RequestParam(value = "tags", required = true) String commaSeparatedIds);

    @DeleteMapping(value = "/{listLayoutId}/category/{layoutCategoryId}/tag", produces = "application/json")
    ResponseEntity<Object> deleteTagsFromCategory(Principal principal, @PathVariable Long listLayoutId, @PathVariable Long layoutCategoryId, @RequestParam(value = "tags", required = true) String commaSeparatedIds);

    @GetMapping(value = "/{listLayoutId}/tag/{tagId}/category", produces = "application/json")
    ResponseEntity<Object> getCategoryForTag(Principal principal, @PathVariable Long listLayoutId, @PathVariable Long tagId);

}
