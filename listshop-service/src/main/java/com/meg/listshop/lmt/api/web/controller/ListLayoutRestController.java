package com.meg.listshop.lmt.api.web.controller;

import com.meg.listshop.lmt.api.controller.ListLayoutRestControllerApi;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.listshop.lmt.data.entity.ListLayoutEntity;
import com.meg.listshop.lmt.service.ListLayoutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
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


    private static final Logger  logger = LoggerFactory.getLogger(ListLayoutRestController.class);

    private ListLayoutService listLayoutService;

    @Autowired
    public ListLayoutRestController(ListLayoutService listLayoutService) {
        this.listLayoutService = listLayoutService;
    }

    public ResponseEntity<ListLayout> readDefaultListLayout(HttpServletRequest request) {
        ListLayoutEntity listLayout = this.listLayoutService
                .getStandardLayout();

        if (listLayout != null) {
            ListLayout layoutModel = ModelMapper.toModel(listLayout, new ArrayList<>());
            ListLayoutResource listLayoutResource = new ListLayoutResource(layoutModel);
            listLayoutResource.fillLinks(request, listLayoutResource);

            return new ResponseEntity(listLayoutResource, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<Object> getCategoryForTag(Principal principal, @PathVariable Long listLayoutId, @PathVariable Long tagId) {
        ListLayoutCategoryEntity layoutCategory = this.listLayoutService
                .getLayoutCategoryForTag(listLayoutId, tagId);
        if (layoutCategory == null) {
            return ResponseEntity.notFound().build();
        }
        CategoryResource result = new CategoryResource(ModelMapper.toModel(layoutCategory, false));

        return new ResponseEntity<>(result, HttpStatus.OK);

    }


    public ResponseEntity<ListLayoutListResource> retrieveListLayouts(HttpServletRequest request, Principal principal) {
        return ResponseEntity.badRequest().build();

    }

    public ResponseEntity<Object> getTagsForCategory(HttpServletRequest request, Principal principal, @PathVariable Long listLayoutId, @PathVariable Long layoutCategoryId) {

        List<TagResource> taglist = this.listLayoutService
                .getTagsForLayoutCategory(layoutCategoryId)
                .stream()
                .map(ModelMapper::toModel)
                .map(TagResource::new)
                .collect(Collectors.toList());
        TagListResource tagListResource = new TagListResource(taglist);
        tagListResource.fillLinks(request, tagListResource);
        return new ResponseEntity<>(tagListResource, HttpStatus.OK);
    }

    public ResponseEntity<Object> createListLayout(HttpServletRequest request, Principal principal, @RequestBody ListLayout input) {
        return ResponseEntity.badRequest().build();
    }

    public ResponseEntity<ListLayoutResource> readListLayout(HttpServletRequest request, Principal principal, @PathVariable("listLayoutId") Long listLayoutId) {
        ListLayoutEntity listLayout = this.listLayoutService
                .getListLayoutById(listLayoutId);

        if (listLayout != null) {
            ListLayout layoutModel = ModelMapper.toModel(listLayout, null);
            ListLayoutResource rescource = new ListLayoutResource(layoutModel);
            rescource.fillLinks(request, rescource);

            return new ResponseEntity<>(rescource, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }


    public ResponseEntity<ListLayout> deleteListLayout(Principal principal, @PathVariable("listLayoutId") Long listLayoutId) {

        //listLayoutService.deleteListLayout(listLayoutId);
        return ResponseEntity.noContent().build();

    }

    public ResponseEntity<Object> addCategoryToListLayout(Principal principal, @PathVariable Long listLayoutId, @RequestBody ListLayoutCategory input) {
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<Object> deleteCategoryFromListLayout(Principal principal, @PathVariable Long listLayoutId, @PathVariable Long layoutCategoryId) {
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<Object> updateCategoryFromListLayout(Principal principal, @PathVariable Long listLayoutId, @RequestBody ListLayoutCategory layoutCategory) {
        return ResponseEntity.badRequest().build();
    }

    public ResponseEntity<Object> getUncategorizedTags(HttpServletRequest request, Principal principal, @PathVariable Long listLayoutId) {
        return ResponseEntity.badRequest().build();
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
        return ResponseEntity.noContent().build();

    }

    //    @RequestMapping(method = RequestMethod.GET, value = "/{listLayoutId}/tag/{tagId}/category", produces = "application/json")

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
