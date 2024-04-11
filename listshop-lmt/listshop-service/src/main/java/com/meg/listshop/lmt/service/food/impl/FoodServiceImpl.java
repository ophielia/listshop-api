/**
 * Created by margaretmartin on 13/05/2017.
 */
package com.meg.listshop.lmt.service.food.impl;

import com.meg.listshop.conversion.service.ConversionService;
import com.meg.listshop.conversion.service.FoodFactor;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.api.model.AdminTagFullInfo;
import com.meg.listshop.lmt.data.entity.*;
import com.meg.listshop.lmt.data.pojos.FoodMappingDTO;
import com.meg.listshop.lmt.data.pojos.TagInfoDTO;
import com.meg.listshop.lmt.data.pojos.TagInternalStatus;
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
    private final ConversionService conversionFactorService;


    @Autowired
    public FoodServiceImpl(FoodCategoryMappingRepository foodCategoryMappingRepo, FoodRepository foodRepository,
                           FoodCategoryRepository foodCategoryRepository,
                           TagService tagService,
                           TagStructureService tagStructureService,
                           FoodConversionRepository foodConversionRepository,
                           ConversionService conversionService) {
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
                .collect(Collectors.toMap(TagInfoDTO::getTagId, Function.identity()));

        // get mapping entities for tag ids
        List<Long> tagIds = new ArrayList<>(tagsToParents.keySet());
        List<FoodCategoryMappingEntity> mappingEntities = foodCategoryMappingRepo.findFoodCategoryMappingEntityByTagIdIn(tagIds);
        Map<Long, FoodCategoryMappingEntity> mappingLookup = new HashMap<>();
        mappingEntities.forEach(v -> {
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
        String searchTerm = "%" + name.trim().toLowerCase() + "%";
        return foodRepository.findFoodMatches(searchTerm);

    }


    public List<FoodEntity> getSuggestedFoods(Long tagId, String alternateSearchTerm) {
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
        String foodSearchTerm = tag.getName();
        if (alternateSearchTerm != null) {
            foodSearchTerm = alternateSearchTerm;
        }
        suggestions = foodMatches(foodSearchTerm);
        if (categoryMatch != null) {
            suggestions.forEach(f -> {
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

    public List<FoodEntity> getSuggestedFoods(String searchTerm) {
        if (searchTerm == null) {
            return new ArrayList<>();
        }

        // first find applicable food category for tag
        return foodMatches(searchTerm);
    }




    public List<FoodMappingDTO> getFoodCategoryMappings() {
        return foodCategoryMappingRepo.retrieveAllFoodMappingDTOs();
    }

    @Override
    public void fillFoodInformation(AdminTagFullInfo tagInfo) {
        if (tagInfo == null || tagInfo.getFoodId() == null) {
            return;
        }
        String stringFoodId = tagInfo.getFoodId();
        Long foodId = Long.valueOf(stringFoodId);
        FoodEntity food = getFoodById(foodId);
        if (food != null) {
            tagInfo.setFoodName(food.getName());
        }
    }

    @Override
    public List<FoodCategoryEntity> getFoodCategories() {
        return foodCategoryRepository.findAll();
    }

    @Override
    public void addOrUpdateFoodCategories(List<Long> tagIds, Long foodCategoryToAssign) {
        List<TagEntity> tags = tagService.getTagsForIdList(tagIds);
        for (TagEntity tag : tags) {
            addOrUpdateFoodCategory(tag, foodCategoryToAssign);
        }
    }
    @Override
    public void addOrUpdateFoodForTags(List<Long> tagIds, Long foodId) {
        List<TagEntity> tags = tagService.getTagsForIdList(tagIds);
        for (TagEntity tag : tags) {
            addOrUpdateFoodForTag(tag, foodId, true);
        }
    }

    public void addOrUpdateFoodForTag(Long tagId, Long foodId, boolean fromAdmin) {
        // get tag
        TagEntity tag = tagService.getTagById(tagId);
        if (tag == null) {
            final String msg = String.format("No tag found by id tagId [%s]", tagId);
            throw new ObjectNotFoundException(msg);
        }
        addOrUpdateFoodForTag(tag, foodId, fromAdmin);
    }

    private void addOrUpdateFoodForTag(TagEntity tag, Long foodId, boolean fromAdmin) {
        // get food entity - for conversion_id and marker
        FoodEntity food = foodRepository.findById(foodId).orElse(null);
        if (food == null) {
            throw new ObjectNotFoundException("No food found for food Id [" + foodId + "]");
        }

        // get food conversions for conversion_id
        List<FoodFactor> foodFactors = foodConversionRepository.findAllByConversionId(food.getConversionId()).stream()
                .map(f->(FoodFactor)f)
                .collect(Collectors.toList());
        if (foodFactors == null || foodFactors.isEmpty()) {
            final String msg = String.format("No conversions found for foodId [%s]", foodId);
            throw new ObjectNotFoundException(msg);
        }

        // create  factors
        conversionFactorService.saveConversionFactors(food.getConversionId(), foodFactors);

        // update tag
        tag.setConversionId(food.getConversionId());
        tag.setMarker(food.getMarker());
        tag.setInternalStatus(TagInternalStatus.FOOD_ASSIGNED);
        if (fromAdmin) {
            tag.setInternalStatus(TagInternalStatus.FOOD_VERIFIED);
        }
        tagService.updateTag(tag.getId(), tag);
    }

    @Override
    public Map<Long, List<FoodConversionEntity>> getFoodFactors(List<FoodEntity> foodEntities) {
        List<Long> tagIds =   foodEntities.stream().map(FoodEntity::getFoodId).collect(Collectors.toList());
        List<FoodConversionEntity> conversionEntities = foodConversionRepository.findAllByFoodIdIn(tagIds);

        Map<Long, List<FoodConversionEntity>> mappedFactors = new HashMap<>();
        for (FoodConversionEntity factor : conversionEntities) {
            Long foodId = factor.getFoodId();
            mappedFactors.putIfAbsent(foodId, new ArrayList<>());
            mappedFactors.get(foodId).add(factor);
        }

        return mappedFactors;
    }

    @Override
    public void addOrUpdateFoodCategory(Long tagId, Long categoryId) {
        // get tag
        TagEntity tag = tagService.getTagById(tagId);
        addOrUpdateFoodCategory(tag, categoryId);
    }

    private void addOrUpdateFoodCategory(TagEntity tag, Long categoryId) {
        if (tag == null) {
            final String msg = "Null tag in addOrUpdateFoodCategory";
            throw new ObjectNotFoundException(msg);
        }
        if (!tag.getIsGroup()) {
            return;
        }
        // get category mapping for tag or create a new one
        FoodCategoryMappingEntity mappingEntity = foodCategoryMappingRepo.findFoodCategoryMappingEntityByTagId(tag.getId());
        if (mappingEntity == null) {
            mappingEntity = new FoodCategoryMappingEntity();
        }
        // save the category id in the mapping
        mappingEntity.setCategoryId(categoryId);
        mappingEntity.setTagId(tag.getId());
        foodCategoryMappingRepo.save(mappingEntity);
        // update the internal status of the tag
        tag.setInternalStatus(TagInternalStatus.CATEGORY_ASSIGNED);
    }


    private FoodEntity getFoodById(Long foodId) {
        if (foodId == null) {
            return null;
        }
        return foodRepository.findById(foodId).orElse(null);
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
        return findCategoryInHierarchy(tag.getParentId(), tagsToParents, mappingLookup);

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
        return getCategoryMatchForTag(tag.getId(), allTagInfo);
    }
}
