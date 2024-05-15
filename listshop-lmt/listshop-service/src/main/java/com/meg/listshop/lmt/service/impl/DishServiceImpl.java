/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.data.repository.UserRepository;
import com.meg.listshop.common.StringTools;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.api.exception.UserNotFoundException;
import com.meg.listshop.lmt.api.model.FractionType;
import com.meg.listshop.lmt.data.entity.DishEntity;
import com.meg.listshop.lmt.data.entity.DishItemEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.pojos.DishItemDTO;
import com.meg.listshop.lmt.data.repository.DishItemRepository;
import com.meg.listshop.lmt.data.repository.DishRepository;
import com.meg.listshop.lmt.service.DishService;
import com.meg.listshop.lmt.service.food.AmountService;
import com.meg.listshop.lmt.service.tag.AutoTagService;
import com.meg.listshop.lmt.service.tag.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
public class DishServiceImpl implements DishService {

    private final DishRepository dishRepository;

    private final UserRepository userRepository;

    private final AutoTagService autoTagService;

    private final TagService tagService;

    private final DishItemRepository dishItemRepository;

    private final AmountService amountService;


    private static final Logger logger = LoggerFactory.getLogger(DishServiceImpl.class);

    @Autowired
    public DishServiceImpl(
            DishRepository dishRepository,
            UserRepository userRepository,
            @Lazy AutoTagService autoTagService,
            TagService tagService,
            DishItemRepository dishItemRepository,
            AmountService amountService
    ) {
        this.dishRepository = dishRepository;
        this.userRepository = userRepository;
        this.autoTagService = autoTagService;
        this.tagService = tagService;
        this.dishItemRepository = dishItemRepository;
        this.amountService = amountService;
    }

    @Override
    public List<DishEntity> getDishesForUserName(String userName) {
        UserEntity user = userRepository.findByEmail(userName);
        if (user == null) {
            throw new UserNotFoundException(userName);
        }
        return getDishesForUser(user.getId());
    }

    @Override
    public List<DishEntity> getDishesForUser(Long userId) {

        return dishRepository.findByUserId(userId);
    }

    private Optional<DishEntity> getDishById(Long dishId) {
        return dishRepository.findById(dishId);
    }


    @Override
    public DishEntity getDishForUserById(String username, Long dishId) {
        if (dishId == null) {
            final String msg = "Null dishId passed as argument [" + username + "].";
            throw new ObjectNotFoundException(msg, null, "Dish");
        }

        UserEntity user = userRepository.findByEmail(username);

        return getDishForUserById(user.getId(), dishId);
    }

    @Override
    public DishEntity getDishForUserById(Long userId, Long dishId) {

        if (dishId == null) {
            final String msg = String.format("Null dishId passed as argument userId [%s].", userId);
            throw new ObjectNotFoundException(msg, null, "Dish");
        }

        Optional<DishEntity> dishOpt = dishRepository.findByDishIdForUser(userId, dishId);
        if (dishOpt.isEmpty()) {
            final String msg = String.format("No dish found by id for user [%s] and dishId [%s]", userId, dishId);
            throw new ObjectNotFoundException(msg, dishId, "Dish");
        }
        return dishOpt.get();
    }

    @Override
    public DishEntity save(DishEntity dish, boolean doAutotag) {
        String message = String.format("service - saving dish [%S], autotag [%S] ", dish.getId(), doAutotag);
        logger.info(message);
        // autotag dish
        if (doAutotag) {
            autoTagService.doAutoTag(dish, true);
        }
        return dishRepository.save(dish);
    }

    @Override
    public DishEntity createDish(Long userId, DishEntity createDish) {
        // check name before saving
        String name = ensureDishNameIsUnique(createDish.getUserId(), createDish.getDishName());
        createDish.setDishName(name);

        // copy creation fields into new DishEntity, since createDish came straight
        // from user input and may be tainted
        DishEntity newDish = new DishEntity(createDish.getUserId(),
                createDish.getDishName(),
                createDish.getDescription());
        newDish.setReference(createDish.getReference());

        DishEntity createdDish = dishRepository.save(newDish);
        tagService.assignDefaultRatingsToDish(userId, createdDish.getId());
        return createdDish;
    }

    private String ensureDishNameIsUnique(Long userId, String dishName) {
        // does this name already exist for the user?
        List<DishEntity> existing = dishRepository.findByUserIdAndDishName(userId, dishName.toLowerCase());

        if (existing.isEmpty()) {
            return dishName;
        }

        // if so, get all lists with names starting with the listName
        List<DishEntity> similar = dishRepository.findByUserIdAndDishNameLike(userId,
                dishName.toLowerCase() + "%");
        List<String> similarNames = similar.stream()
                .map(list -> list.getDishName().trim().toLowerCase()).collect(Collectors.toList());
        // use handy StringTools method to get first unique name

        return StringTools.makeUniqueName(dishName, similarNames);
    }

    @Override
    public List<DishEntity> save(List<DishEntity> dishes) {
        return dishRepository.saveAll(dishes);
    }

    @Override
    public List<DishEntity> getDishes(String username, List<Long> dishIds) {
        UserEntity user = userRepository.findByEmail(username);

        return dishRepository.findByDishIdsForUser(user.getId(), dishIds);
    }

    @Override
    public List<DishEntity> getDishes(List<Long> dishIds) {
        return dishRepository.findAllById(dishIds);
    }

    @Override
    public Map<Long, DishEntity> getDictionaryForIdList(List<Long> dishIds) {
        List<DishEntity> tags = dishRepository.findAllById(dishIds);
        if (!tags.isEmpty()) {
            return tags.stream().collect(Collectors.toMap(DishEntity::getId,
                    c -> c));

        }
        return new HashMap<>();
    }

    @Override
    public void updateLastAddedForDish(Long dishId) {
        Optional<DishEntity> dish = getDishById(dishId);

        dish.ifPresent(d -> {
            d.setLastAdded(new Date());
            dishRepository.save(d);
        });
    }

    @Override
    public List<DishEntity> getDishesToAutotag(Long statusFlag, int dishLimit) {
        Pageable limit = PageRequest.of(0, dishLimit);
        return dishRepository.findDishesToAutotag(statusFlag, limit);
    }

    @Override
    public void addIngredientToDish(Long userId, Long dishId, DishItemDTO dishItemDTO) {
        // get dish
        DishEntity dish = getDishForUserById(userId, dishId);
        List<DishItemEntity> dishItems = dish.getItems();
        // check if ingredient already exists for this tagId and unit
        TagEntity existingTag = dishItems.stream()
                .filter(i -> i.getUnitId() != null && i.getUnitId().equals(dishItemDTO.getUnitId()))
                .map(DishItemEntity::getTag)
                .filter(t -> t.getId().equals(dishItemDTO.getTagId()))
                .findFirst()
                .orElse(null);

        if (existingTag != null) {
            // ingredient already exists for this tag and unit
            return;
        }
        DishItemEntity dishItemEntity = new DishItemEntity();
        dishItemEntity.setDish(dish);
        // get tag
        TagEntity tag = tagService.getTagById(dishItemDTO.getTagId());
        if (tag == null) {
            throw new ObjectNotFoundException("Tag not found", dishItemDTO.getTagId(), "Tag");
        }
        dishItemEntity.setTag(tag);

        // calculate quantity from fraction and whole
        Double quantity = calculateQuantity(dishItemDTO);
        dishItemEntity.setQuantity(quantity);
        dishItemEntity.setWholeQuantity(dishItemDTO.getWholeQuantity());
        dishItemEntity.setFractionalQuantity(dishItemDTO.getFractionalQuantity());

        // unit
        dishItemEntity.setUnitId(dishItemDTO.getUnitId());

        // fill unitSize and marker from raw_modifiers
        dishItemEntity.setRawModifiers(dishItemDTO.getRawModifiers());
        if (tag.getConversionId() != null) {
            fillIngredientModifiers(tag.getConversionId(), dishItemDTO.getRawModifiers(), dishItemEntity);
            dishItemEntity.setModifiersProcessed(true);
        } else  {
            dishItemEntity.setModifiersProcessed(false);
        }

        // save ingredient
        dishItemRepository.save(dishItemEntity);
        dishItems.add(dishItemEntity);
        // add ingredient to dish
        dish.setItems(dishItems);
        save(dish, false);
        // update statistic
        tagService.countTagAddedToDish(dish.getUserId(), tag.getId());
    }

    private Double calculateQuantity(DishItemDTO dishItemDTO) {
        Double quantity = 0.0;
        if (dishItemDTO.getWholeQuantity() != null) {
            quantity = quantity + dishItemDTO.getWholeQuantity();
        }
        if (dishItemDTO.getFractionalQuantity() != null) {
            Double fractionalValue = FractionType.doubleValueOf(dishItemDTO.getFractionalQuantity());
            quantity = quantity + fractionalValue;
        }

        return quantity;
    }

    private void fillIngredientModifiers(Long conversionId, String rawModifiers, DishItemEntity dishItemEntity) {
        if (rawModifiers == null) {
            return;
        }
        List<String> modifierTokens = amountService.pullModifierTokens(rawModifiers);
        List<String> markers = amountService.pullMarkersForModifers(modifierTokens, conversionId);
        if (markers != null && !markers.isEmpty()) {
            String firstMarker = markers.get(0);
            dishItemEntity.setMarker(firstMarker);
        }
        List<String> unitSizes = amountService.pullUnitSizesForModifiers(modifierTokens, conversionId);
        if (unitSizes != null && !unitSizes.isEmpty()) {
            String firstUnit = unitSizes.get(0);
            dishItemEntity.setUnitSize(firstUnit);
        }
    }

}
