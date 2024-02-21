/**
 * Created by margaretmartin on 13/05/2017.
 */
package com.meg.listshop.lmt.service.food.impl;

import com.meg.listshop.conversion.service.ConversionFactorService;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.data.entity.*;
import com.meg.listshop.lmt.data.pojos.FoodMappingDTO;
import com.meg.listshop.lmt.data.pojos.TagInfoDTO;
import com.meg.listshop.lmt.data.pojos.TagSearchCriteria;
import com.meg.listshop.lmt.data.repository.FoodCategoryMappingRepository;
import com.meg.listshop.lmt.data.repository.FoodCategoryRepository;
import com.meg.listshop.lmt.data.repository.FoodConversionRepository;
import com.meg.listshop.lmt.data.repository.FoodRepository;
import com.meg.listshop.lmt.service.food.FoodService;
import com.meg.listshop.lmt.service.tag.TagService;
import com.meg.listshop.lmt.service.tag.TagStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@Transactional
public class FoodServiceImpl implements FoodService {


    FoodCategoryMappingRepository foodCategoryMappingRepo;
    FoodRepository foodRepository;
    private final FoodCategoryRepository foodCategoryRepository;

    private final FoodConversionRepository foodConversionRepository;

    private final TagService tagService;

    private final TagStructureService tagStructureService;
    private final ConversionFactorService conversionFactorService;


    @Autowired
    public FoodServiceImpl(FoodCategoryMappingRepository foodCategoryMappingRepo, FoodRepository foodRepository,
                           FoodCategoryRepository foodCategoryRepository,
                           TagService tagService,
                           TagStructureService tagStructureService,
                           FoodConversionRepository foodConversionRepository,
                           ConversionFactorService conversionService) {
        this.foodCategoryMappingRepo = foodCategoryMappingRepo;
        this.foodRepository = foodRepository;
        this.foodCategoryRepository = foodCategoryRepository;
        this.tagService = tagService;
        this.tagStructureService = tagStructureService;
        this.foodConversionRepository = foodConversionRepository;
        this.conversionFactorService = conversionService;
    }

    @Override
    public FoodCategoryEntity getCategoryMatchForTag(Long tagId, List<TagInfoDTO> tagInfoDTOS) {
        // make reference hash of ascendant tags
        Map<Long, TagInfoDTO> tagsToParents = tagInfoDTOS.stream()
                .collect(Collectors.toMap( TagInfoDTO::getTagId, Function.identity()));

        // get mapping entities for tag ids
        List<Long> tagIds = new ArrayList<>(tagsToParents.keySet());
        List<FoodCategoryMappingEntity> mappingEntities =  foodCategoryMappingRepo.findFoodCategoryMappingEntityByTagIdIn(tagIds);
        Map<Long, FoodCategoryMappingEntity> mappingLookup = new HashMap<>();
        mappingEntities.forEach( v -> {
                    Long id = v.getTagId();
                    mappingLookup.put(id, v);
                });

        // find first tag in hierarchy with a category
        FoodCategoryMappingEntity foundMapping = findCategoryInHierarchy(tagId, tagsToParents, mappingLookup);
        if (foundMapping == null) {
            return null;
        }
        return foodCategoryRepository.findById(foundMapping.getCategoryId()).orElse(null);
    }


    private List<FoodEntity> foodMatches(String name) {
        if (name == null) {
            return new ArrayList<>();
        }
        // prepare searchTerm
        String searchTerm = name.trim().toLowerCase();
        return  foodRepository.findFoodEntitiesByNameContainsIgnoreCase(searchTerm);

    }




    public List<FoodEntity> getSuggestedFoods(Long tagId) {
        // get tag
        TagEntity tag = tagService.getTagById(tagId);
        if (tag == null) {
            return new ArrayList<>();
        }

        // first find applicable food category for tag
        List<FoodEntity> suggestions;
        List<FoodEntity> preferredList = new ArrayList<>();
        List<FoodEntity> otherList = new ArrayList<>();
        FoodCategoryEntity categoryMatch = findClosestFoodCategory(tag);
        suggestions = foodMatches(tag.getName());
        if (categoryMatch != null) {
            suggestions.stream().forEach( f -> {
                if (f.getCategoryId().equals(categoryMatch.getId())) {
                    preferredList.add(f);
                } else {
                    otherList.add(f);
                }
            });
            preferredList.addAll(otherList);
        } else {
            preferredList.addAll(suggestions);
        }

        return preferredList;
    }

    public void addOrUpdateFoodForTag(Long tagId, Long foodId) {
        // get tag
        TagEntity tag = tagService.getTagById(tagId);
        if (tag == null) {
            final String msg = String.format("No tag found by id tagId [%s]", tagId);
            throw new ObjectNotFoundException(msg);
        }
        // get food portion
        List<FoodConversionEntity> foodFactors = foodConversionRepository.findAllByFoodId(foodId);
        if (foodFactors == null || foodFactors.isEmpty()) {
            final String msg = String.format("No conversions found for foodId [%s]", foodId);
            throw new ObjectNotFoundException(msg);
        }
        // check if tag has food assigned
        if (tag.getFoodId() != null) {
        // update tag to add food
            conversionFactorService.deleteFactorsForTag(tagId);
        }
        tag.setFoodId(foodId);

        // create conversion factor
        for (FoodConversionEntity conversion : foodFactors) {
            conversionFactorService.addFactorForTag(tagId,conversion.getAmount(), conversion.getUnitId(), conversion.getGramWeight());
        }
    }

    public List<FoodMappingDTO> getFoodCategoryMappings() {
        return foodCategoryMappingRepo.retrieveAllFoodMappingDTOs();
    }
    private FoodCategoryMappingEntity findCategoryInHierarchy(Long tagId, Map<Long, TagInfoDTO> tagsToParents, Map<Long, FoodCategoryMappingEntity> mappingLookup) {
        // if mapping exists for tagId, return it
        if (mappingLookup.containsKey(tagId)) {
            return mappingLookup.get(tagId);
        }
        TagInfoDTO tag = tagsToParents.get(tagId);
        // if parent is null, return null
        if (tag.getParentId() == null) {
            return null;
        }
            // check for parent
            return findCategoryInHierarchy(tag.getParentId(),tagsToParents,mappingLookup);

    }

    private FoodCategoryEntity findClosestFoodCategory(TagEntity tag) {
        List<Long> tagSearchIds = new ArrayList<>();
        tagSearchIds.add(tag.getId());
        tagSearchIds.addAll(tagStructureService.getAscendantTags(tag).stream()
                .map(TagEntity::getId)
                .collect(Collectors.toList()));

        TagSearchCriteria searchCriteria = new TagSearchCriteria();
        searchCriteria.setTagIds(tagSearchIds);
        List<TagInfoDTO> allTagInfo = tagService.getTagInfoList(searchCriteria);
        return getCategoryMatchForTag(tag.getId() , allTagInfo);
    }
}
