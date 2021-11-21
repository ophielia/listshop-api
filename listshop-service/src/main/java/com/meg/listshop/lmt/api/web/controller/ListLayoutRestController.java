package com.meg.listshop.lmt.api.web.controller;

import com.meg.listshop.lmt.api.controller.ListLayoutRestControllerApi;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.listshop.lmt.data.entity.ListLayoutEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.service.ListLayoutException;
import com.meg.listshop.lmt.service.ListLayoutService;
import com.meg.listshop.lmt.service.categories.ListLayoutCategoryPojo;
import com.meg.listshop.lmt.service.categories.ListShopCategory;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.Resources;
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
import java.util.Date;
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

    public ResponseEntity<Resources<ListLayoutListResource>> retrieveListLayouts(HttpServletRequest request, Principal principal) {
        List<ListLayoutResource> listLayoutList = listLayoutService
                .getListLayouts()
                .stream()
                .map(ll -> ModelMapper.toModel(ll, null))
                .map(ListLayoutResource::new)
                .collect(Collectors.toList());
        ListLayoutListResource resource = new ListLayoutListResource(listLayoutList);
        resource.fillLinks(request, resource);
        return new ResponseEntity(resource, HttpStatus.OK);

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
            List<ListShopCategory> structuredCategories = this.listLayoutService.getStructuredCategories(listLayout);
            ListLayout layoutModel = ModelMapper.toModel(listLayout, structuredCategories);
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
            List<ListShopCategory> structuredCategories = this.listLayoutService.getStructuredCategories(listLayout);
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

    public ResponseEntity<Object> addCategoryToListLayout(Principal principal, @PathVariable Long listLayoutId, @RequestBody ListLayoutCategoryPojo input) {
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

    public ResponseEntity<Object> updateCategoryFromListLayout(Principal principal, @PathVariable Long listLayoutId, @RequestBody ListLayoutCategoryPojo layoutCategory) {
        ListLayoutCategoryEntity listLayoutCategory = ModelMapper.toEntity(layoutCategory);
        ListLayoutCategoryEntity result = this.listLayoutService.updateListLayoutCategory(listLayoutId, listLayoutCategory);

        if (result != null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().build();
    }

    public ResponseEntity<Object> getUncategorizedTags(HttpServletRequest request, Principal principal, @PathVariable Long listLayoutId) {
        List<TagResource> tagResourceList = this.listLayoutService.getUncategorizedTagsForList(listLayoutId)
                .stream()
                .map(te -> ModelMapper.toModel(te))
                .map(tm -> new TagResource(tm))
                .collect(Collectors.toList());

        tagResourceList.forEach(tr -> tr.fillLinks(request, tr));

        return new ResponseEntity(tagResourceList, HttpStatus.OK);
    }

    public ResponseEntity<Object> getTagsForCategory(HttpServletRequest request, Principal principal, @PathVariable Long listLayoutId, @PathVariable Long layoutCategoryId) {

        List<Tag> taglist = this.listLayoutService
                .getTagsForLayoutCategory(layoutCategoryId)
                .stream()
                .map(ModelMapper::toModel)
                .collect(Collectors.toList());
        TagListResource tagListResource = new TagListResource(taglist);
        tagListResource.fillLinks(request, tagListResource);
        return new ResponseEntity(tagListResource, HttpStatus.OK);
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
        CategoryResource result = new CategoryResource(layoutCategory);
        return new ResponseEntity(result, HttpStatus.OK);

    }


    //@RequestMapping(method = RequestMethod.POST, value = "/category/{layoutCategoryId}/parent/{parentCategoryId}", produces = "application/json")
    public ResponseEntity<Object> addSubcategoryToCategory(Principal principal, @PathVariable Long layoutCategoryId,
                                                           @PathVariable Long parentCategoryId) {
        try {
            this.listLayoutService.addCategoryToParent(layoutCategoryId, parentCategoryId);
        } catch (ListLayoutException e) {
            logger.error("Unable to add Category [" + layoutCategoryId + "] to Parent [" + parentCategoryId + "].", e);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.noContent().build();
    }

    //@RequestMapping(method = RequestMethod.POST, value = "/category/{categoryId}", produces = "application/json")
    public ResponseEntity<Object> moveCategory(Principal principal, @PathVariable Long categoryId, @RequestParam(value = "move", required = true) String direction) {
        boolean moveUp = "up".equalsIgnoreCase(direction);
        try {
            this.listLayoutService.moveCategory(categoryId, moveUp);
        } catch (ListLayoutException e) {
            logger.error("Unable to move Category [" + categoryId + "] direction up[" + moveUp + "].",e);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<CategoryItemRefresh>> retrieveRefreshedTagToCategoryList(Principal principal, @PathVariable Long listLayoutId,
                                                                                        @RequestParam(value = "after") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date after) {
        List<Pair<TagEntity, ListLayoutCategoryEntity>> categoryChanged = this.listLayoutService.getTagCategoryChanges(listLayoutId, after);


        List<CategoryItemRefresh> refreshed = new ArrayList<>();

        for (Pair<TagEntity, ListLayoutCategoryEntity> change : categoryChanged) {
            CategoryItemRefresh refresh = new CategoryItemRefresh(change.getKey(), change.getValue());
                refreshed.add(refresh);
        }

        return new ResponseEntity(refreshed, HttpStatus.OK);
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
