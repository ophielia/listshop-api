package com.meg.atable.controller;

import com.meg.atable.api.controller.ListLayoutRestControllerApi;
import com.meg.atable.api.model.*;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.data.entity.ListLayoutCategoryEntity;
import com.meg.atable.data.entity.ListLayoutEntity;
import com.meg.atable.service.ListLayoutService;
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


    @Autowired
    private ListLayoutService listLayoutService;

    @Autowired
    private UserService userService;

    public ResponseEntity<Resources<ListLayoutResource>> retrieveListLayouts(Principal principal) {
        List<ListLayoutResource> listLayoutList = listLayoutService
                .getListLayouts()
                .stream().map(ListLayoutResource::new)
                .collect(Collectors.toList());

        Resources<ListLayoutResource> listLayoutResourceList = new Resources<>(listLayoutList);
        return new ResponseEntity(listLayoutResourceList, HttpStatus.OK);

    }

    //  @RequestMapping(method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<Object> createListLayout(Principal principal, @RequestBody ListLayout input) {
        ListLayoutEntity listLayoutEntity = ModelMapper.toEntity(input);

        ListLayoutEntity result = listLayoutService.createListLayout(listLayoutEntity);

        if (result != null) {
            Link forOneListLayout = new ListLayoutResource(result).getLink("self");
            return ResponseEntity.created(URI.create(forOneListLayout.getHref())).build();
        }
        return ResponseEntity.badRequest().build();
    }

    //    @RequestMapping(method = RequestMethod.GET, value = "/{listLayoutId}", produces = "application/json")
    public ResponseEntity<ListLayout> readListLayout(Principal principal, @PathVariable("listLayoutId") Long listLayoutId) {
        ListLayoutEntity listLayout = this.listLayoutService
                .getListLayoutById(listLayoutId);

        if (listLayout != null) {
            ListLayoutResource listLayoutResource = new ListLayoutResource(listLayout);

            return new ResponseEntity(listLayoutResource, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    // @RequestMapping(method = RequestMethod.DELETE, value = "/{listLayoutId}", produces = "application/json")
    public ResponseEntity<ListLayout> deleteListLayout(Principal principal, @PathVariable("listLayoutId") Long listLayoutId) {

        listLayoutService.deleteListLayout(listLayoutId);
        return ResponseEntity.noContent().build();

    }

    //@RequestMapping(method = RequestMethod.POST, value = "/{listLayoutId}/category", produces = "application/json")
    public ResponseEntity<Object> addCategoryToListLayout(Principal principal, @PathVariable Long listLayoutId, @RequestBody ListLayoutCategory input) {
        ListLayoutCategoryEntity entity = ModelMapper.toEntity(input);
        this.listLayoutService.addCategoryToListLayout(listLayoutId, entity);


        return ResponseEntity.noContent().build();
    }

    // @RequestMapping(method = RequestMethod.DELETE, value = "/{listLayoutId}/dish/{dishId}", produces = "application/json")
    public ResponseEntity<Object> deleteCategoryFromListLayout(Principal principal, @PathVariable Long listLayoutId, @PathVariable Long layoutCategoryId) {
        this.listLayoutService.deleteCategoryFromListLayout(listLayoutId, layoutCategoryId);

        return ResponseEntity.noContent().build();
    }

    //@RequestMapping(method = RequestMethod.PUT, value = "/{listLayoutId}/category/{layoutCategoryId}", produces = "application/json")
    public ResponseEntity<Object> updateCategoryFromListLayout(Principal principal, @PathVariable Long listLayoutId, @RequestBody ListLayoutCategory layoutCategory) {
        ListLayoutCategoryEntity listLayoutCategory = ModelMapper.toEntity(layoutCategory);
        ListLayoutCategoryEntity result = this.listLayoutService.updateListLayoutCategory(listLayoutId, listLayoutCategory);

        if (result != null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().build();
    }

    //@RequestMapping(method = RequestMethod.GET, value = "/{listLayoutId}", produces = "application/json")
    public ResponseEntity<Object> getUncategorizedTags(Principal principal, @PathVariable Long listLayoutId) {
        List<TagResource> tagResourceList = this.listLayoutService.getUncategorizedTagsForList(listLayoutId)
                .stream()
                .map(TagResource::new)
                .collect(Collectors.toList());
        Resources<TagResource> resourceList = new Resources<>(tagResourceList);

        return new ResponseEntity(resourceList, HttpStatus.OK);
    }

    //@RequestMapping(method = RequestMethod.GET, value = "/{listLayoutId}/category/{layoutCategoryId}/tags", produces = "application/json")
    public ResponseEntity<Object> getTagsForCategory(Principal principal, @PathVariable Long listLayoutId, @PathVariable Long layoutCategoryId) {

        List<TagResource> tagResourceList = this.listLayoutService
                .getTagsForLayoutCategory(layoutCategoryId)
                .stream()
                .map(TagResource::new)
                .collect(Collectors.toList());
        Resources<TagResource> result = new Resources<>(tagResourceList);
        return new ResponseEntity(result, HttpStatus.OK);

    }

    // @RequestMapping(method = RequestMethod.POST, value = "/{listLayoutId}/category/{layoutCategoryId}", produces = "application/json")
    public ResponseEntity<Object> addTagsToCategory(Principal principal, @PathVariable Long listLayoutId, @PathVariable Long layoutCategoryId, @RequestParam(value = "tags", required = true) String commaSeparatedIds) {
        // translate tags into list of Long ids
        List<Long> tagIdList = commaDelimitedToList(commaSeparatedIds);
        if (tagIdList == null) {
            return ResponseEntity.badRequest().build();
        }
        this.listLayoutService.addTagsToCategory(listLayoutId, layoutCategoryId, tagIdList);

        return ResponseEntity.noContent().build();
    }

    //@RequestMapping(method = RequestMethod.DELETE, value = "/{listLayoutId}/category/{layoutCategoryId}", produces = "application/json")
    public ResponseEntity<Object> deleteTagsFromCategory(Principal principal, @PathVariable Long listLayoutId, @PathVariable Long layoutCategoryId, @RequestParam(value = "tags", required = true) String commaSeparatedIds) {
        // translate tags into list of Long ids
        List<Long> tagIdList = commaDelimitedToList(commaSeparatedIds);
        if (tagIdList == null) {
            return ResponseEntity.badRequest().build();
        }
        this.listLayoutService.deleteTagsFromCategory(listLayoutId, layoutCategoryId, tagIdList);

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
