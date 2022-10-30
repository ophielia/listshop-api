package com.meg.listshop.lmt.api.web.controller;

import com.meg.listshop.lmt.api.controller.ListLayoutRestControllerApi;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.listshop.lmt.data.entity.ListLayoutEntity;
import com.meg.listshop.lmt.service.ListLayoutException;
import com.meg.listshop.lmt.service.ListLayoutService;
import com.meg.listshop.lmt.service.categories.ListLayoutCategoryPojo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger logger = LogManager.getLogger(ListLayoutRestController.class);

    private ListLayoutService listLayoutService;

    @Autowired
    public ListLayoutRestController(ListLayoutService listLayoutService) {
        this.listLayoutService = listLayoutService;
    }

    public ResponseEntity<ListLayoutListResource> retrieveListLayouts(HttpServletRequest request, Principal principal) {
        List<ListLayoutResource> listLayoutList = listLayoutService
                .getListLayouts()
                .stream()
                .map(ll -> ModelMapper.toModel(ll, null))
                .map(ListLayoutResource::new)
                .collect(Collectors.toList());
        ListLayoutListResource resource = new ListLayoutListResource(listLayoutList);
        resource.fillLinks(request, resource);
        return new ResponseEntity<>(resource, HttpStatus.OK);

    }

    public ResponseEntity<Object> createListLayout(HttpServletRequest request, Principal principal, @RequestBody ListLayout input) {
        ListLayoutEntity listLayoutEntity = ModelMapper.toEntity(input);

        ListLayoutEntity result = listLayoutService.createListLayout(listLayoutEntity);

        if (result != null) {
            ListLayout model = ModelMapper.toModel(listLayoutEntity, null);
            ListLayoutResource resource = new ListLayoutResource(model);

            return ResponseEntity.created(resource.selfLink(request, resource)).build();
        }
        return ResponseEntity.badRequest().build();
    }

    public ResponseEntity<ListLayout> readListLayout(HttpServletRequest request, Principal principal, @PathVariable("listLayoutId") Long listLayoutId) {
        ListLayoutEntity listLayout = this.listLayoutService
                .getListLayoutById(listLayoutId);

        if (listLayout != null) {
            List<ListLayoutCategoryPojo> structuredCategories = null;//this.listLayoutService.getStructuredCategories(listLayout);
            ListLayout layoutModel = ModelMapper.toModel(listLayout, null);
            ListLayoutResource rescource = new ListLayoutResource(layoutModel);
            rescource.fillLinks(request, rescource);

            return new ResponseEntity(rescource, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }


    public ResponseEntity<ListLayout> readDefaultListLayout(HttpServletRequest request) {
        ListLayoutEntity listLayout = this.listLayoutService
                .getDefaultListLayout();

        if (listLayout != null) {
            List<ListLayoutCategoryPojo> structuredCategories = this.listLayoutService.getStructuredCategories(listLayout);
            ListLayout layoutModel = ModelMapper.toModel(listLayout, structuredCategories);
            ListLayoutResource listLayoutResource = new ListLayoutResource(layoutModel);
            listLayoutResource.fillLinks(request, listLayoutResource);

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
            logger.error("Unable to delete Category [%d] from list layout [%d]. Exception: %s", layoutCategoryId, listLayoutId, e);
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

    public ResponseEntity<Object> getUncategorizedTags(HttpServletRequest request, Principal principal, @PathVariable Long listLayoutId) {
        List<TagResource> tagList = this.listLayoutService.getUncategorizedTagsForList(listLayoutId)
                .stream()
                .map(ModelMapper::toModel)
                .map(TagResource::new)
                .collect(Collectors.toList());

        TagListResource resource = new TagListResource(tagList);
        resource.setReflectRequest(true);
        resource.fillLinks(request, resource);

        return new ResponseEntity<>(resource, HttpStatus.OK);
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

    //    @RequestMapping(method = RequestMethod.GET, value = "/{listLayoutId}/tag/{tagId}/category", produces = "application/json")
    public ResponseEntity<Object> getCategoryForTag(Principal principal, @PathVariable Long listLayoutId, @PathVariable Long tagId) {
        ListLayoutCategoryEntity layoutCategory = this.listLayoutService
                .getLayoutCategoryForTag(listLayoutId, tagId);
        if (layoutCategory == null) {
            return ResponseEntity.notFound().build();
        }
        CategoryResource result = new CategoryResource(ModelMapper.toModel(layoutCategory, false));

        return new ResponseEntity<>(result, HttpStatus.OK);

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
