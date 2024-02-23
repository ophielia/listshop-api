package com.meg.listshop.admin.controller;

import com.meg.listshop.admin.model.PostSearchTags;
import com.meg.listshop.auth.service.impl.JwtUser;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.pojos.IncludeType;
import com.meg.listshop.lmt.data.pojos.TagInfoDTO;
import com.meg.listshop.lmt.data.pojos.TagInternalStatus;
import com.meg.listshop.lmt.data.pojos.TagSearchCriteria;
import com.meg.listshop.lmt.service.food.FoodService;
import com.meg.listshop.lmt.service.tag.TagService;
import com.meg.listshop.lmt.service.tag.TagStructureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@Controller
public class AdminTagRestController implements AdminTagRestControllerApi {

    private final TagService tagService;
    private final TagStructureService tagStructureService;
    private final FoodService foodService;

    private static final Logger logger = LoggerFactory.getLogger(AdminTagRestController.class);

    @Autowired
    AdminTagRestController(TagService tagService, TagStructureService tagStructureService,
                           FoodService foodService) {
        this.tagStructureService = tagStructureService;
        this.tagService = tagService;
        this.foodService = foodService;
    }


    public ResponseEntity<Object> performOperation(@RequestBody TagOperationPut input) {
        //@RequestMapping(method = RequestMethod.PUT, consumes = "application/json")
        if (input == null || input.getTagOperationType() == null) {
            return ResponseEntity.badRequest().build();
        }
        List<Long> tagIds = input.getTagIds();
        if (tagIds == null || tagIds.isEmpty()) {
            return ResponseEntity.ok().build();
        }
        TagOperationType operationType = TagOperationType.valueOf(input.getTagOperationType());
        switch (operationType) {
            case AssignToUser:
                String userIdString = input.getUserId();
                if (userIdString == null) {
                    return ResponseEntity.badRequest().build();
                }
                Long userId = Long.valueOf(userIdString);
                tagService.assignTagsToUser(userId, tagIds);

                break;
            case MarkAsReviewed:
                tagService.setTagsAsVerified(tagIds);
                break;
            case CopyToStandard:
                tagService.createStandardTagsFromUserTags(tagIds);

        }
        return ResponseEntity.ok().build();
    }


    public ResponseEntity<FoodListResource> getFoodSuggestionsForTag(@PathVariable("tagId") Long tagId) {
        //@GetMapping(value = "/{tag_id}/food/suggestions")

        List<FoodResource> resourceList = foodService.getSuggestedFoods(tagId).stream()
                .map(ModelMapper::toModel)
                .map(FoodResource::new)
                .collect(Collectors.toList());

        var returnValue = new FoodListResource(resourceList);
        return new ResponseEntity<>(returnValue, HttpStatus.OK);
    }


    public ResponseEntity<Object> assignFoodToTag(@PathVariable("tagId") Long tagId,
                                                  @PathVariable("foodId") Long foodId) {

        foodService.addOrUpdateFoodForTag(tagId, foodId, true);

        return ResponseEntity.noContent().build();
    }


    public ResponseEntity<CategoryMappingListResource> getFoodCategoryMappings() {

        List<FoodCategoryMappingResource> resourceList = foodService.getFoodCategoryMappings().stream()
                .map(ModelMapper::toModel)
                .map(FoodCategoryMappingResource::new)
                .collect(Collectors.toList());

        var returnValue = new CategoryMappingListResource(resourceList);
        return new ResponseEntity<>(returnValue, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> assignFoodCategory(Long tagId, Long categoryId) {
        //@PostMapping(value = "/{tagId}/food/category/{categoryId}")
        foodService.addOrCategoryToTag(tagId, categoryId);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<AdminTagFullInfoResource> getFullTagInfo(@PathVariable("tagId") Long tagId) {
        AdminTagFullInfo tagInfo = tagService.getFullTagInfo(tagId);
        foodService.fillFoodInformation(tagInfo);
        AdminTagFullInfoResource resource = new AdminTagFullInfoResource(tagInfo);
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }


    public ResponseEntity<TagListResource> findTags(@RequestBody PostSearchTags searchTags) {
        //@PostMapping(value = "/search")
        TagSearchCriteria criteria = translateUserRequest(searchTags);
        List<TagInfoDTO> infoTags = tagService.getTagInfoList(criteria);
        List<TagResource> resourceList = infoTags.stream()
                .map(ModelMapper::toModel)
                .map(TagResource::new)
                .collect(Collectors.toList());
        var returnValue = new TagListResource(resourceList);
        return new ResponseEntity<>(returnValue, HttpStatus.OK);
    }

    private TagSearchCriteria translateUserRequest(PostSearchTags searchTags) {
        List<TagInternalStatus> included = stringsToTagInternalStatus(searchTags.getIncludeStatuses());
        List<TagInternalStatus> excluded = stringsToTagInternalStatus(searchTags.getExcludeStatuses());
        Long userId = searchTags.getUserId() != null ? Long.valueOf(searchTags.getUserId()) : null;
        IncludeType groupIncludeType = stringToIncludeType(searchTags.getGroupIncludeType());
        List<TagType> tagTypes = toTagTypes(searchTags.getTagTypes());
        String textFragment = searchTags.getTextFragment() == null || searchTags.getTextFragment().isEmpty() ?
                null : searchTags.getTextFragment();
        return  new TagSearchCriteria(userId,
                textFragment,
                tagTypes,
                excluded,
                included,
                groupIncludeType);
    }

    private List<TagType> toTagTypes(List<TagType> tagTypes) {
        if (tagTypes == null) {
            return new ArrayList<>();
        }
       return tagTypes;
    }

    private IncludeType stringToIncludeType(String groupIncludeType) {
        if (groupIncludeType == null) {
            return IncludeType.IGNORE;
        }
        return IncludeType.valueOf(groupIncludeType);
    }

    private List<TagInternalStatus> stringsToTagInternalStatus(List<String> includeStatuses) {
        if (includeStatuses == null) {
            return new ArrayList<>();
        }
        return includeStatuses.stream()
                .map(this::stringToInternalStatus)
                .collect(Collectors.toList());
    }

    private TagInternalStatus stringToInternalStatus(String raw) {
        if (raw == null) {
            return null;
        }
        switch (raw) {
            case "CHECKED":
                return TagInternalStatus.CHECKED;
            case "LIQUID_ASSIGNED":
                return TagInternalStatus.LIQUID_ASSIGNED;
            case "FOOD_ASSIGNED":
                return TagInternalStatus.FOOD_ASSIGNED;
            case "FOOD_VERIFIED":
                return TagInternalStatus.FOOD_VERIFIED;
            case "CATEGORY_ASSIGNED":
                return TagInternalStatus.CATEGORY_ASSIGNED;
            default:
                return TagInternalStatus.EMPTY;
        }
    }


    public ResponseEntity<Object> addChildren(@PathVariable Long tagId, @RequestParam(value = "tagIds") String filter) {
        if (filter == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Long> tagIdList = commaDelimitedToList(filter);
        this.tagService.assignChildrenToParent(tagId, tagIdList);
        return ResponseEntity.noContent().build();

    }

    /* havent gone over things starting hers - looking for what isn't used anymore */
    @Override
    public ResponseEntity<Object> assignChildToParent(@PathVariable Long parentId, @PathVariable Long childId) {
        tagService.assignTagToParent(childId, parentId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Object> assignChildToBaseTag(@PathVariable("tagId") Long tagId) {
        TagEntity tag = this.tagService.getTagById(tagId);

        this.tagStructureService.assignTagToTopLevel(tag);
        return ResponseEntity.ok().build();

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


    public ResponseEntity<Object> replaceTagsInDishes(HttpServletRequest request, Authentication authentication, @PathVariable("fromTagId") Long tagId, @PathVariable("toTagId") Long toTagId) {
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        String message = String.format("Admin - replace tag in dishes, admin user [%S]", userDetails.getId());
        logger.info(message);

        this.tagService.replaceTagInDishes(userDetails.getId(), tagId, toTagId);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<Object> saveTagForDelete(@PathVariable Long tagId, @RequestParam Long replacementTagId) {

        this.tagService.saveTagForDelete(tagId, replacementTagId);


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
