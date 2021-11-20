package com.meg.listshop.lmt.api.web.controller;

import com.meg.listshop.lmt.api.controller.TagRestControllerApi;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.entity.TagExtendedEntity;
import com.meg.listshop.lmt.service.DishService;
import com.meg.listshop.lmt.service.tag.TagService;
import com.meg.listshop.lmt.service.tag.TagStructureService;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@Controller
public class TagRestController implements TagRestControllerApi {

    private final TagService tagService;
    private final DishService dishService;

    private final TagStructureService tagStructureService;

    private static final Logger logger = LogManager.getLogger(TagRestController.class);

    @Autowired
    TagRestController(TagService tagService, TagStructureService tagStructureService, DishService dishService) {
        this.tagStructureService = tagStructureService;
        this.tagService = tagService;
        this.dishService = dishService;
    }


    public ResponseEntity<TagListResource> retrieveTagList(HttpServletRequest request,
                                                           @RequestParam(value = "filter", required = false) String filter,
                                                           @RequestParam(value = "tag_type", required = false) String tagType,
                                                           @RequestParam(value = "extended", required = false) Boolean extended) {
        List<TagType> tagTypeFilter = processTagTypeInput(tagType);
        TagFilterType tagFilterTypeFilter = filter != null ? TagFilterType.valueOf(filter) : TagFilterType.All;
        if (extended == null) {
            extended = false;
        }
        if (extended || tagFilterTypeFilter == TagFilterType.ParentTags) {
            return retrieveTagExtendedList(request, tagFilterTypeFilter, tagTypeFilter);
        }

        List<TagEntity> tagList = tagService.getTagList(tagFilterTypeFilter, tagTypeFilter);

        List<Tag> resourceList = tagList.stream().map(t -> ModelMapper.toModel(t))
                .collect(Collectors.toList());
        resourceList.forEach(r -> ((ListShopResource) r).fillLinks(request, r));
        TagListResource returnValue = new TagListResource(resourceList);
        return new ResponseEntity(returnValue, HttpStatus.OK);
    }

    private ResponseEntity<TagListResource> retrieveTagExtendedList(HttpServletRequest request, TagFilterType tagFilterTypeFilter, List<TagType> tagTypeFilter) {
        List<TagExtendedEntity> tagList = tagService.getTagExtendedList(tagFilterTypeFilter, tagTypeFilter);

        List<Tag> resourceList = tagList.stream().map(t -> ModelMapper.toModel(t))
                .collect(Collectors.toList());
        resourceList.forEach(r -> ((ListShopResource) r).fillLinks(request, r));
        TagListResource returnValue = new TagListResource(resourceList);
        return new ResponseEntity(returnValue, HttpStatus.OK);
    }


    public ResponseEntity<Tag> add(HttpServletRequest request, @RequestBody Tag input) {
        TagEntity tagEntity = ModelMapper.toEntity(input);
        TagEntity result = this.tagService.createTag(null, tagEntity);

        if (result != null) {

            Tag tagModel = ModelMapper.toModel(tagEntity);
            return ResponseEntity.created(tagModel.selfLink(request, tagModel)).build();

        }
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<Tag> addAsChild(HttpServletRequest request, @PathVariable Long tagId, @RequestBody Tag input) {
        TagEntity parent = this.tagService.getTagById(tagId);

        if (parent != null) {
            TagEntity tagEntity = ModelMapper.toEntity(input);
            TagEntity result = this.tagService.createTag(parent, tagEntity);
            if (result != null) {
                Tag tagModel = ModelMapper.toModel(tagEntity);
                return ResponseEntity.created(tagModel.selfLink(request, tagModel)).build();
            } else {
                return ResponseEntity.noContent().build();
            }
        }
        return ResponseEntity.badRequest().build();

    }

    public ResponseEntity addChildren(@PathVariable Long tagId, @RequestParam(value = "tagIds") String filter) {
        if (filter == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Long> tagIdList = commaDelimitedToList(filter);
        this.tagService.assignChildrenToParent(tagId, tagIdList);
        return ResponseEntity.noContent().build();

    }


    @Override
    public ResponseEntity assignChildToParent(@PathVariable Long parentId, @PathVariable Long childId) {
        tagService.assignTagToParent(childId, parentId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity assignChildToBaseTag(@PathVariable("tagId") Long tagId) {
        TagEntity tag = this.tagService.getTagById(tagId);

        this.tagStructureService.assignTagToTopLevel(tag);
        return ResponseEntity.ok().build();

    }

    public ResponseEntity<Tag> readTag(HttpServletRequest request, @PathVariable Long tagId) {
        // invalid dishId - returns invalid id supplied - 400
        TagEntity tagEntity = this.tagService
                .getTagById(tagId);

        if (tagEntity == null) {
            return ResponseEntity.notFound().build();
        }
        Tag tagModel = ModelMapper.toModel(tagEntity);
        tagModel.fillLinks(request, tagModel);

        return new ResponseEntity(new EmbeddedTag(tagModel), HttpStatus.OK);

    }

    public ResponseEntity<Object> updateTag(@PathVariable Long tagId, @RequestBody Tag input) {
        // invalid tagId - returns invalid id supplied - 400

        // invalid contents of input - returns 405 validation exception
        TagEntity toUpdate = ModelMapper.toEntity(input);
        TagEntity updatedTag = this.tagService.updateTag(tagId, toUpdate);
        if (updatedTag != null) {
            return ResponseEntity.noContent().build();

        }
        return ResponseEntity.notFound().build();

    }


    public ResponseEntity<Tag> getChildrenTagDishAssignments(HttpServletRequest request, Principal principal, @PathVariable("tagId") Long tagId) {
        logger.warn("DEPRACATED! - Depracated method getChildrenTagDishAssignments called.");

        Optional<Tag> tagModel = this.dishService.getDishesForTagChildren(tagId, principal.getName())
                .stream()
                .findFirst()
                .map(t -> ModelMapper.toModel(t));

        if (tagModel.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity(tagModel, HttpStatus.OK);
    }

    public ResponseEntity<Object> replaceTagsInDishes(HttpServletRequest request, Principal principal, @PathVariable("fromTagId") Long tagId, @PathVariable("toTagId") Long toTagId) {
        this.tagService.replaceTagInDishes(principal.getName(), tagId, toTagId);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<Object> saveTagForDelete(@PathVariable Long tagId, @RequestParam Long replacementTagId) {

        this.tagService.saveTagForDelete(tagId, replacementTagId);


        return ResponseEntity.noContent().build();
    }

    private List<TagType> processTagTypeInput(String tag_type) {
        if (tag_type == null) {
            return new ArrayList<>();
        } else if (tag_type.contains(",")) {
            return Arrays.asList(tag_type.split(",")).stream()
                    .map(t -> TagType.valueOf(t.trim()))
                    .collect(Collectors.toList());
        } else {
            return Collections.singletonList(TagType.valueOf(tag_type));
        }
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
