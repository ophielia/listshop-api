/**
 * Created by margaretmartin on 13/05/2017.
 */
package com.meg.listshop.lmt.service.food.impl;

import com.meg.listshop.lmt.data.entity.*;
import com.meg.listshop.lmt.data.pojos.TagInfoDTO;
import com.meg.listshop.lmt.data.repository.FoodCategoryMappingRepository;
import com.meg.listshop.lmt.data.repository.FoodCategoryRepository;
import com.meg.listshop.lmt.data.repository.FoodRepository;
import com.meg.listshop.lmt.service.food.FoodService;
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


    @Autowired
    public FoodServiceImpl(FoodCategoryMappingRepository foodCategoryMappingRepo, FoodRepository foodRepository,
                           FoodCategoryRepository foodCategoryRepository) {
        this.foodCategoryMappingRepo = foodCategoryMappingRepo;
        this.foodRepository = foodRepository;
        this.foodCategoryRepository = foodCategoryRepository;
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

    @Override
    public List<FoodEntity> foodMatches(String name, FoodCategoryEntity categoryMatch) {
        if (name == null) {
            return new ArrayList<>();
        }
        // prepare searchTerm
        String searchTerm = name.trim().toLowerCase();
        if (categoryMatch != null) {
            Long categoryId = categoryMatch.getId();
            return foodRepository.findFoodEntitiesByNameContainsIgnoreCaseAndCategoryId(searchTerm,categoryId);
        }
        return  foodRepository.findFoodEntitiesByNameContainsIgnoreCase(searchTerm);
    }

    @Override
    public List<FoodEntity> foodMatches(String name) {
        return foodMatches(name, null);
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
}
