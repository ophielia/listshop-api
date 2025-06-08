/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.lmt.dish.impl;

import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.data.repository.UserRepository;
import com.meg.listshop.common.FlatStringUtils;
import com.meg.listshop.common.StringTools;
import com.meg.listshop.conversion.service.ConversionService;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.api.exception.UserNotFoundException;
import com.meg.listshop.lmt.api.model.FractionType;
import com.meg.listshop.lmt.api.model.RatingUpdateInfo;
import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.data.entity.DishEntity;
import com.meg.listshop.lmt.data.entity.DishItemEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.pojos.DishDTO;
import com.meg.listshop.lmt.data.pojos.DishItemDTO;
import com.meg.listshop.lmt.data.repository.DishItemRepository;
import com.meg.listshop.lmt.data.repository.DishRepository;
import com.meg.listshop.lmt.dish.DishService;
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
import java.util.function.Function;
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

    private final ConversionService conversionService;


    private static final Logger logger = LoggerFactory.getLogger(DishServiceImpl.class);
    private static final String DEFAULT_UNIT_SIZE = "medium";

    Function<DishItemEntity, TagEntity> functionGetTag = DishItemEntity::getTag;
    Function<DishItemEntity, String> functionGetTagName = functionGetTag.andThen(TagEntity::getName);

    Set<TagType> includedInStandard;

    @Autowired
    public DishServiceImpl(
            DishRepository dishRepository,
            UserRepository userRepository,
            @Lazy AutoTagService autoTagService,
            TagService tagService,
            DishItemRepository dishItemRepository,
            AmountService amountService,
            ConversionService conversionService) {
        this.dishRepository = dishRepository;
        this.userRepository = userRepository;
        this.autoTagService = autoTagService;
        this.tagService = tagService;
        this.dishItemRepository = dishItemRepository;
        this.amountService = amountService;

        this.includedInStandard = Set.of(TagType.DishType, TagType.TagType);
        this.conversionService = conversionService;
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
        DishItemEntity dishItemEntity = dishItems.stream()
                .filter(t -> t.getTag().getTagType().equals(TagType.Ingredient))
                .filter(i -> i.getTag().getId().equals(dishItemDTO.getTagId()))
                //.filter(i -> i.getUnitId() != null && i.getUnitId().equals(dishItemDTO.getUnitId()))
                .filter(t -> t.getTag().getId().equals(dishItemDTO.getTagId()))
                .findFirst()
                .orElse(null);

        if (dishItemEntity == null) {
            // this is a new ingredient
            dishItemEntity = new DishItemEntity();
        }
        doAddOrUpdateIngredient(dish, dishItemEntity, dishItemDTO, true);
    }

    @Override
    public void updateIngredientInDish(Long userId, Long dishId, DishItemDTO dishItemDTO) {
        // get dish
        DishEntity dish = getDishForUserById(userId, dishId);
        List<DishItemEntity> dishItems = dish.getItems();
        // pull this ingredient
        Long dishItemId = dishItemDTO.getDishItemId();
        DishItemEntity dishItemEntity = dishItems.stream()
                .filter(i -> i.getDishItemId() != null && i.getDishItemId().equals(dishItemId))
                .findFirst()
                .orElse(null);

        if (dishItemEntity == null) {
            throw new ObjectNotFoundException("DishItem not found", dishItemId, "DishItem");
        }

        doAddOrUpdateIngredient(dish, dishItemEntity, dishItemDTO, false);
    }

    @Override
    public void deleteIngredientFromDish(Long userId, Long dishId, Long ingredientId) {

        // get dish
        DishEntity dish = getDishForUserById(userId, dishId);
        List<DishItemEntity> dishItems = dish.getItems();
        // pull this ingredient
        DishItemEntity dishItemEntity = dishItems.stream()
                .filter(i -> i.getDishItemId() != null && i.getDishItemId().equals(ingredientId))
                .findFirst()
                .orElse(null);

        if (dishItemEntity == null) {
            throw new ObjectNotFoundException("Ingredient not found", ingredientId, "DishItem");
        }

        dishItems.remove(dishItemEntity);
        dishItemRepository.delete(dishItemEntity);
        dish.setItems(dishItems);
        save(dish, true);
    }

    @Override
    public DishDTO getDishForV2Display(Long userId, Long dishId) {
        // get dish, with tags, ingredients and ratings
        // sub objects sorted, but in "raw" form
        // will be "translated" into ingredients in ModelMapper
        if (dishId == null) {
            final String msg = String.format("Null dishId passed as argument userId [%s].", userId);
            throw new ObjectNotFoundException(msg, null, "Dish");
        }

        Optional<DishEntity> dishOpt = dishRepository.findByDishIdForUser(userId, dishId);
        if (dishOpt.isEmpty()) {
            final String msg = String.format("No dish found by id for user [%s] and dishId [%s]", userId, dishId);
            throw new ObjectNotFoundException(msg, dishId, "Dish");
        }
        DishEntity dish = dishOpt.get();

        // get ingredients
        List<DishItemDTO> ingredients = dishItemRepository.getIngredientsForDish(dishId);
        ingredients.forEach(in -> {
            // insert fraction display
            if (in.getFractionalQuantity() != null) {
                String fractionDisplay = in.getFractionalQuantity().getDisplayName();
                in.setFractionDisplay(fractionDisplay);
            }
        });
        ingredients.sort(Comparator.comparing(DishItemDTO::getTagDisplay));

        // tags
        List<DishItemEntity> tags = dish.getItems().stream()
                .filter(di -> includedInStandard.contains(di.getTag().getTagType()))
                .collect(Collectors.toList());
        tags.sort(Comparator.comparing(functionGetTagName));

        // ratings
        RatingUpdateInfo ratings = tagService.getRatingUpdateInfoForDishIds(Collections.singletonList(dishId));

        return new DishDTO(dish, ingredients, tags, ratings);
    }

    @Override
    public List<DishItemDTO> getDishIngredients(Long userId, Long dishId) {
        // get dish, with tags, ingredients and ratings
        // sub objects sorted, but in "raw" form
        // will be "translated" into ingredients in ModelMapper
        if (dishId == null) {
            final String msg = String.format("Null dishId passed as argument userId [%s].", userId);
            throw new ObjectNotFoundException(msg, null, "Dish");
        }

        Optional<DishEntity> dishOpt = dishRepository.findByDishIdForUser(userId, dishId);
        if (dishOpt.isEmpty()) {
            final String msg = String.format("No dish found by id for user [%s] and dishId [%s]", userId, dishId);
            throw new ObjectNotFoundException(msg, dishId, "Dish");
        }

        // get ingredients
        List<DishItemDTO> ingredients = dishItemRepository.getIngredientsForDish(dishId);
        ingredients.forEach(in -> {
            // insert fraction display
            if (in.getFractionalQuantity() != null) {
                String fractionDisplay = in.getFractionalQuantity().getDisplayName();
                in.setFractionDisplay(fractionDisplay);
            }
        });
        ingredients.sort(Comparator.comparing(DishItemDTO::getTagDisplay));
        return ingredients;
    }

    public void doAddOrUpdateIngredient(DishEntity dish, DishItemEntity dishItemEntity, DishItemDTO dishItemDTO, boolean updateStatistics) {
        List<DishItemEntity> dishItems = dish.getItems();
        dishItemEntity.setDish(dish);
        // get tag
        TagEntity tag = tagService.getTagById(dishItemDTO.getTagId());
        if (tag == null) {
            throw new ObjectNotFoundException("Tag not found", dishItemDTO.getTagId(), "Tag");
        }
        dishItemEntity.setTag(tag);

        // clear quantity fields
        clearIngredientQuantities(dishItemEntity);

        if (dishItemDTO.hasAmount()) {
            // calculate quantity from fraction and whole
            doubleCheckAndFillQuantity(dishItemEntity, dishItemDTO);

            // unit
            dishItemEntity.setUnitId(dishItemDTO.getUnitId());

            // fill unitSize and marker from raw_modifiers
            setModifiersFromRawModifiers(dishItemDTO, dishItemEntity, tag);

            dishItemEntity.setRawEntry(dishItemDTO.getRawEntry());
        }

        // save ingredient
        boolean isNewIngredient = dishItemEntity.getDishItemId() == null;
        dishItemRepository.save(dishItemEntity);
        if (isNewIngredient) {
            dishItemRepository.save(dishItemEntity);
            dishItems.add(dishItemEntity);
        }
        // add ingredient to dish
        dish.setItems(dishItems);
        save(dish, false);
        // update statistic
        if (updateStatistics) {
            tagService.countTagAddedToDish(dish.getUserId(), tag.getId());
        }
    }

    private void doubleCheckAndFillQuantity(DishItemEntity dishItemEntity, DishItemDTO dishItemDTO) {
        Double dtoQuantity = dishItemDTO.getQuantity();
        Integer dtoWhole = dishItemDTO.getWholeQuantity();
        FractionType dtoFraction = dishItemDTO.getFractionalQuantity();

        Double calculatedQuantity = 0.0;
        if (dtoWhole != null) {
            calculatedQuantity += Double.valueOf(dtoWhole);
        }
        if (dtoFraction != null) {
            double fractionDouble = FractionType.doubleValueOf(dtoFraction);
            calculatedQuantity += fractionDouble;
        }

        dishItemEntity.setWholeQuantity(dishItemDTO.getWholeQuantity());
        dishItemEntity.setFractionalQuantity(dishItemDTO.getFractionalQuantity());
        dishItemEntity.setQuantity(dishItemDTO.getQuantity());

        if (!calculatedQuantity.equals(dtoQuantity)) {
            dishItemEntity.setQuantity(calculatedQuantity);
        }

    }

    private void clearIngredientQuantities(DishItemEntity dishItemEntity) {
        dishItemEntity.setRawModifiers(null);
        dishItemEntity.setRawEntry(null);
        dishItemEntity.setQuantity(null);
        dishItemEntity.setFractionalQuantity(null);
        dishItemEntity.setQuantity(null);
        dishItemEntity.setUnitSize(null);
        dishItemEntity.setUnitId(null);
    }

    private void setModifiersFromRawModifiers(DishItemDTO dishItemDTO, DishItemEntity dishItemEntity, TagEntity tag) {
        List<String> rawModifiers = dishItemDTO.getRawModifiers();
        if (rawModifiers == null || rawModifiers.isEmpty()) {


            dishItemEntity.setRawModifiers(null);
            dishItemEntity.setMarker(null);
            dishItemEntity.setUnitSize(DEFAULT_UNIT_SIZE);
            dishItemEntity.setModifiersProcessed(true);
            return;
        }
        dishItemEntity.setRawModifiers(FlatStringUtils.flattenListToString(rawModifiers, "|"));
        if (tag.getConversionId() != null) {
            fillIngredientModifiers(tag.getConversionId(), rawModifiers, dishItemEntity);
            dishItemEntity.setModifiersProcessed(true);
        } else {
            dishItemEntity.setModifiersProcessed(false);
        }
        if (dishItemEntity.getUnitSize() != null) {
            dishItemEntity.setUserSize(true);
        } else {
            // unitSize
            if (dishItemDTO.getUnitSize() == null) {
                String  defaultUnitSize = conversionService.getDefaultUnitSizeForConversionId(tag.getConversionId(),dishItemDTO.getUnitId());
                dishItemEntity.setUnitSize(defaultUnitSize);
                dishItemEntity.setUserSize(false);
            }
        }



}

private void fillIngredientModifiers(Long conversionId, List<String> rawModifiers, DishItemEntity dishItemEntity) {
    if (rawModifiers == null) {
        dishItemEntity.setMarker(null);
        dishItemEntity.setUnitSize(null);
        return;
    }
    List<String> markers = amountService.pullMarkersForModifers(rawModifiers, conversionId);
    if (markers != null && !markers.isEmpty()) {
        String firstMarker = markers.get(0);
        dishItemEntity.setMarker(firstMarker);
    }
    List<String> unitSizes = amountService.pullUnitSizesForModifiers(rawModifiers, conversionId);
    if (unitSizes != null && !unitSizes.isEmpty()) {
        String firstUnit = unitSizes.get(0);
        dishItemEntity.setUnitSize(firstUnit);
    }
}

}
