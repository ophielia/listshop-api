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

    @GetMapping(value = "/default", produces = "application/json")
    ResponseEntity<ListLayout> readDefaultListLayout(HttpServletRequest request);

    @GetMapping(value = "/{listLayoutId}/tag/{tagId}/category", produces = "application/json")
    ResponseEntity<Object> getCategoryForTag(Principal principal, @PathVariable Long listLayoutId, @PathVariable Long tagId);

    @GetMapping(value = "/{listLayoutId}/category/{layoutCategoryId}/tag", produces = "application/json")
    @Deprecated
    ResponseEntity<Object> getTagsForCategory(HttpServletRequest request, Principal principal, @PathVariable Long listLayoutId, @PathVariable Long layoutCategoryId);


    @GetMapping(produces = "application/json")
    @Deprecated
    ResponseEntity<ListLayoutListResource> retrieveListLayouts(HttpServletRequest request, Principal principal);

    @PostMapping(produces = "application/json", consumes = "application/json")
    @Deprecated
    ResponseEntity<Object> createListLayout(HttpServletRequest request, Principal principal, @RequestBody ListLayout input);

    @GetMapping(value = "/{listLayoutId}", produces = "application/json")
    @Deprecated
    ResponseEntity<ListLayoutResource> readListLayout(HttpServletRequest request, Principal principal, @PathVariable("listLayoutId") Long listLayoutId);


    @DeleteMapping(value = "/{listLayoutId}", produces = "application/json")
    @Deprecated
    ResponseEntity<ListLayout> deleteListLayout(Principal principal, @PathVariable("listLayoutId") Long listLayoutId);

    @PostMapping(value = "/{listLayoutId}/category", produces = "application/json")
    @Deprecated
    ResponseEntity<Object> addCategoryToListLayout(Principal principal, @PathVariable Long listLayoutId, @RequestBody ListLayoutCategory input);

    @DeleteMapping(value = "/{listLayoutId}/category/{layoutCategoryId}", produces = "application/json")
    @Deprecated
    ResponseEntity<Object> deleteCategoryFromListLayout(Principal principal, @PathVariable Long listLayoutId, @PathVariable Long layoutCategoryId) throws ListLayoutException;

    @PutMapping(value = "/{listLayoutId}/category/{layoutCategoryId}", produces = "application/json")
    @Deprecated
    ResponseEntity<Object> updateCategoryFromListLayout(Principal principal, @PathVariable Long layoutCategoryId, @RequestBody ListLayoutCategory layoutCategory);

    @GetMapping(value = "/{listLayoutId}/tag", produces = "application/json")
    @Deprecated
    ResponseEntity<Object> getUncategorizedTags(HttpServletRequest request, Principal principal, @PathVariable Long listLayoutId);

    @PostMapping(value = "/{listLayoutId}/category/{layoutCategoryId}/tag", produces = "application/json")
    @Deprecated
    ResponseEntity<Object> addTagsToCategory(Principal principal, @PathVariable Long listLayoutId, @PathVariable Long layoutCategoryId, @RequestParam(value = "tags", required = true) String commaSeparatedIds);

    @DeleteMapping(value = "/{listLayoutId}/category/{layoutCategoryId}/tag", produces = "application/json")
    @Deprecated
    ResponseEntity<Object> deleteTagsFromCategory(Principal principal, @PathVariable Long listLayoutId, @PathVariable Long layoutCategoryId, @RequestParam(value = "tags", required = true) String commaSeparatedIds);


}
