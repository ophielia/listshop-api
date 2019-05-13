package com.meg.atable.lmt.api.web.controller;

import com.meg.atable.lmt.api.controller.ListLayoutRestControllerApi;
import com.meg.atable.lmt.api.model.*;
import com.meg.atable.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.atable.lmt.data.entity.ListLayoutEntity;
import com.meg.atable.lmt.service.ListLayoutException;
import com.meg.atable.lmt.service.ListLayoutService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 20/10/2017.
 */
@Controller
public class ListLayoutRestController implements ListLayoutRestControllerApi {

    private static final Logger logger = LogManager.getLogger(ListLayoutRestController.class);

    @Autowired
    private ListLayoutService listLayoutService;

    public ResponseEntity<Resources<ListLayoutResource>> retrieveListLayouts(Principal principal) {
        List<ListLayoutResource> listLayoutList = listLayoutService
                .getListLayouts()
                .stream().map(ll -> new ListLayoutResource(ll, null))
                .collect(Collectors.toList());

        Resources<ListLayoutResource> listLayoutResourceList = new Resources<>(listLayoutList);
        return new ResponseEntity(listLayoutResourceList, HttpStatus.OK);

    }

    public ResponseEntity<Object> createListLayout(Principal principal, @RequestBody ListLayout input) {
        ListLayoutEntity listLayoutEntity = ModelMapper.toEntity(input);

        ListLayoutEntity result = listLayoutService.createListLayout(listLayoutEntity);

        if (result != null) {
            Link forOneListLayout = new ListLayoutResource(result, null).getLink("self");
            return ResponseEntity.created(URI.create(forOneListLayout.getHref())).build();
        }
        return ResponseEntity.badRequest().build();
    }

    public ResponseEntity<ListLayout> readListLayout(Principal principal, @PathVariable("listLayoutId") Long listLayoutId) {
        ListLayoutEntity listLayout = this.listLayoutService
                .getListLayoutById(listLayoutId);

        if (listLayout != null) {
            List<Category> structuredCategories = this.listLayoutService.getStructuredCategories(listLayout);
            ListLayoutResource listLayoutResource = new ListLayoutResource(listLayout, structuredCategories);

            return new ResponseEntity(listLayoutResource, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }


    public ResponseEntity<ListLayout> readDefaultListLayout() {
        ListLayoutEntity listLayout = this.listLayoutService
                .getDefaultListLayout();

        if (listLayout != null) {
            List<Category> structuredCategories = this.listLayoutService.getStructuredCategories(listLayout);
            ListLayoutResource listLayoutResource = new ListLayoutResource(listLayout, structuredCategories);

            return new ResponseEntity(listLayoutResource, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<ListLayout> deleteListLayout(Principal principal, @PathVariable("listLayoutId") Long listLayoutId) {

        listLayoutService.deleteListLayout(listLayoutId);
        return ResponseEntity.noContent().build();

    }

    public ResponseEntity<Object> addCategoryToListLayout(Principal principal, @PathVariable Long listLayoutId, @RequestBody ListLayoutCategory input) {
        ListLayoutCategoryEntity entity = ModelMapper.toEntity(input);
        this.listLayoutService.addCategoryToListLayout(listLayoutId, entity);


        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<Object> deleteCategoryFromListLayout(Principal principal, @PathVariable Long listLayoutId, @PathVariable Long layoutCategoryId) {
        try {
            this.listLayoutService.deleteCategoryFromListLayout(listLayoutId, layoutCategoryId);
        } catch (ListLayoutException e) {
            logger.error("Unable to delete Category [" + layoutCategoryId + "] from list layout [" + listLayoutId + "].",e);
        }

        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<Object> updateCategoryFromListLayout(Principal principal, @PathVariable Long listLayoutId, @RequestBody ListLayoutCategory layoutCategory) {
        ListLayoutCategoryEntity listLayoutCategory = ModelMapper.toEntity(layoutCategory);
        ListLayoutCategoryEntity result = this.listLayoutService.updateListLayoutCategory(listLayoutId, listLayoutCategory);

        if (result != null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().build();
    }

    public ResponseEntity<Object> getUncategorizedTags(Principal principal, @PathVariable Long listLayoutId) {
        List<TagResource> tagResourceList = this.listLayoutService.getUncategorizedTagsForList(listLayoutId)
                .stream()
                .map(TagResource::new)
                .collect(Collectors.toList());
        Resources<TagResource> resourceList = new Resources<>(tagResourceList);

        return new ResponseEntity(resourceList, HttpStatus.OK);
    }

    public ResponseEntity<Object> getTagsForCategory(Principal principal, @PathVariable Long listLayoutId, @PathVariable Long layoutCategoryId) {

        List<TagResource> tagResourceList = this.listLayoutService
                .getTagsForLayoutCategory(layoutCategoryId)
                .stream()
                .map(TagResource::new)
                .collect(Collectors.toList());
        Resources<TagResource> result = new Resources<>(tagResourceList);
        return new ResponseEntity(result, HttpStatus.OK);

    }

    public ResponseEntity<Object> addTagsToCategory(Principal principal, @PathVariable Long listLayoutId, @PathVariable Long layoutCategoryId, @RequestParam(value = "tags", required = true) String commaSeparatedIds) {
        // translate tags into list of Long ids
        List<Long> tagIdList = commaDelimitedToList(commaSeparatedIds);
        if (tagIdList == null) {
            return ResponseEntity.badRequest().build();
        }
        this.listLayoutService.addTagsToCategory(listLayoutId, layoutCategoryId, tagIdList);

        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<Object> deleteTagsFromCategory(Principal principal, @PathVariable Long listLayoutId, @PathVariable Long layoutCategoryId, @RequestParam(value = "tags", required = true) String commaSeparatedIds) {
        // translate tags into list of Long ids
        List<Long> tagIdList = commaDelimitedToList(commaSeparatedIds);
        if (tagIdList == null) {
            return ResponseEntity.badRequest().build();
        }
        this.listLayoutService.deleteTagsFromCategory(listLayoutId, layoutCategoryId, tagIdList);

        return ResponseEntity.noContent().build();

    }

    //@RequestMapping(method = RequestMethod.POST, value = "/category/{layoutCategoryId}/parent/{parentCategoryId}", produces = "application/json")
    public ResponseEntity<Object> addSubcategoryToCategory(Principal principal, @PathVariable Long layoutCategoryId,
                                                           @PathVariable Long parentCategoryId) {
        try {
            this.listLayoutService.addCategoryToParent(layoutCategoryId, parentCategoryId);
        } catch (ListLayoutException e) {
            logger.error("Unable to add Category [" + layoutCategoryId + "] to Parent [" + parentCategoryId + "].",e);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.noContent().build();
    }

    //@RequestMapping(method = RequestMethod.POST, value = "/category/{categoryId}", produces = "application/json")
    public ResponseEntity<Object> moveCategory(Principal principal, @PathVariable Long categoryId, @RequestParam(value = "move", required = true) String direction) {
        boolean moveUp = direction != null && "up".equalsIgnoreCase(direction);
        try {
            this.listLayoutService.moveCategory(categoryId, moveUp);
        } catch (ListLayoutException e) {
            logger.error("Unable to move Category [" + categoryId + "] direction up[" + moveUp + "].",e);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.noContent().build();
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
