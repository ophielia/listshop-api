package com.meg.listshop.lmt.api.web.controller.v2;

import com.github.dockerjava.api.exception.BadRequestException;
import com.google.common.base.Enums;
import com.meg.listshop.auth.service.impl.JwtUser;
import com.meg.listshop.common.FlatStringUtils;
import com.meg.listshop.common.FractionUtils;
import com.meg.listshop.common.RoundingUtils;
import com.meg.listshop.lmt.api.controller.v2.V2DishRestControllerApi;
import com.meg.listshop.lmt.api.exception.BadParameterException;
import com.meg.listshop.lmt.api.model.DishSortDirection;
import com.meg.listshop.lmt.api.model.DishSortKey;
import com.meg.listshop.lmt.api.model.FractionType;
import com.meg.listshop.lmt.api.model.ModelMapper;
import com.meg.listshop.lmt.api.model.v2.*;
import com.meg.listshop.lmt.api.model.v2.DishListResource;
import com.meg.listshop.lmt.data.pojos.DishDTO;
import com.meg.listshop.lmt.data.pojos.DishItemDTO;
import com.meg.listshop.lmt.service.DishSearchCriteria;
import com.meg.listshop.lmt.service.DishSearchService;
import com.meg.listshop.lmt.service.DishService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@CrossOrigin
public class V2DishRestController implements V2DishRestControllerApi {

    private static final Logger logger = LoggerFactory.getLogger(V2DishRestController.class);

    private final DishService dishService;
    private final DishSearchService dishSearchService;

    @Value("${conversionservice.single.unit.id:1011}")
    private Long defaultUnitId;

    @Autowired
    V2DishRestController(DishService dishService,
                         DishSearchService dishSearchService) {
        this.dishService = dishService;
        this.dishSearchService = dishSearchService;
    }

    public ResponseEntity<DishListResource> retrieveDishes(HttpServletRequest request,
                                                           Authentication authentication,
                                                           @RequestParam(value = "searchFragment", required = false) String searchFragment,
                                                           @RequestParam(value = "includedTags", required = false) String includedTags,
                                                           @RequestParam(value = "excludedTags", required = false) String excludedTags,
                                                           @RequestParam(value = "sortKey", required = false) String sortKey,
                                                           @RequestParam(value = "sortDirection", required = false) String sortDirection
    ) {
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        logger.info("Entered retrieveDishes includedTags: [{}], excludedTags: [{}], sortKey: [{}], sortDirection: [{}]", includedTags, excludedTags, sortKey, sortDirection);
        List<DishResource> dishList;
        if (ObjectUtils.isEmpty(includedTags) && ObjectUtils.isEmpty(excludedTags)
                && ObjectUtils.isEmpty(sortKey) && ObjectUtils.isEmpty(sortDirection)) {
            dishList = getAllDishes(userDetails.getId());
        } else {
            dishList = findDishes(userDetails.getId(), includedTags, excludedTags, searchFragment, sortKey, sortDirection);
        }

        DishListResource resource = new DishListResource(dishList);
        resource.fillLinks(request, resource);
        return new ResponseEntity<>(resource, HttpStatus.OK);

    }

    @Override
    public ResponseEntity<DishResource> retrieveDish(HttpServletRequest request, Authentication authentication, Long dishId) {
        //@GetMapping(value = "/{dishId}", produces = "application/json")
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        String message = String.format("retrieving dish [%S] for user [%S]", dishId, userDetails.getId());
        logger.info(message);

        // get dish, with tags, ingredients and ratings
        // sub objects sorted, but in "raw" form
        // will be "translated" in ModelMapper
        DishDTO dish = this.dishService
                .getDishForV2Display(userDetails.getId(), dishId);

        DishResource resource = new DishResource(ModelMapper.toModel(dish, true));

        return new ResponseEntity(resource, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<IngredientListResource> getIngredientsByDishId(HttpServletRequest request, Authentication authentication, Long dishId) throws BadParameterException {
        //@GetMapping(value = "/{dishId}/ingredients", produces = "application/json")
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();

        if (dishId == null) {
            throw new BadParameterException("Dish id cannot be null in getIngredientsByDishId");
        }
        List<DishItemDTO> rawIngredients = dishService.getDishIngredients(userDetails.getId(), dishId);
        List<IngredientResource> ingredients = rawIngredients.stream()
                .map(ModelMapper::toModel)
                .map(IngredientResource::new)
                .collect(Collectors.toList());
        var returnValue = new IngredientListResource(ingredients);
        return new ResponseEntity<>(returnValue, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> addIngredientToDish(Authentication authentication, Long dishId, IngredientPut ingredient) {
        //@PostMapping(value = "/{dishId}/ingredient", produces = "application/json")
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        // validate / translate ingredient to dishItem => strings to long, resolving fraction
        DishItemDTO validatedEntry = validateIngredient(ingredient, false);
        // sent to service with dishId, dishItemDTO
        dishService.addIngredientToDish(userDetails.getId(), dishId, validatedEntry);
        return ResponseEntity.noContent().build();
    }


    @Override
    public ResponseEntity<Object> updateIngredientInDish(Authentication authentication, Long dishId, IngredientPut ingredient) {
        //@PutMapping(value = "/{dishId}/ingredient", produces = "application/json")
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();
        // validate / translate ingredient to dishItem => strings to long, resolving fraction
        DishItemDTO validatedEntry = validateIngredient(ingredient, true);
        // sent to service with dishId, dishItemDTO
        dishService.updateIngredientInDish(userDetails.getId(), dishId, validatedEntry);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> deleteIngredientFromDish(Authentication authentication, Long dishId, Long ingredientId) throws BadParameterException {
        //@DeleteMapping(value = "/{dishId}/ingredients/{ingredientId}", produces = "application/json")
        JwtUser userDetails = (JwtUser) authentication.getPrincipal();

        if (ingredientId == null || dishId == null) {
            String message = String.format("can't delete ingredient without ingredientId [%s] or dishId [%s]", ingredientId, dishId);
            throw new BadParameterException(message);
        }
        // sent to service with dishId, dishItemDTO
        dishService.deleteIngredientFromDish(userDetails.getId(), dishId, ingredientId);
        return ResponseEntity.noContent().build();
    }


    private DishItemDTO validateIngredient(IngredientPut ingredient, Boolean verifyItemId) {
        if (ingredient == null) {
            throw new BadRequestException("Ingredient is null");
        }
        if (Boolean.TRUE.equals(verifyItemId) && ingredient.getId() == null) {
            throw new BadRequestException("Ingredient id is null");
        }
        if (ingredient.getTagId() == null) {
            throw new BadRequestException("Ingredient tag id is null");
        }

        DishItemDTO dishItemDTO = new DishItemDTO();
        dishItemDTO.setDishItemId(stringToLongOrException(ingredient.getId()));
        dishItemDTO.setTagId(stringToLongOrException(ingredient.getTagId()));

        if (!ingredientHasAmount(ingredient)) {
            return dishItemDTO;
        }
        Long unitId = stringToLongOrDefault(ingredient.getUnitId(), defaultUnitId);
        dishItemDTO.setUnitId(unitId);

        // check quantity and round if necessary

        // check fraction, and round if necessary
        if (ingredient.getQuantity() != null ) {
            // fill from quantity
            validateAndFillFromQuantity(dishItemDTO, ingredient);
        } else {
            // fill from whole number and fraction
            validateAndFillFromParts(dishItemDTO, ingredient);
        }

        if (ingredient.getId() != null) {
            dishItemDTO.setDishItemId(longValueOf(ingredient.getId()));
        }
        dishItemDTO.setRawModifiers(ingredient.getRawModifiers());

        return dishItemDTO;
    }

    private void validateAndFillFromParts(DishItemDTO dishItemDTO, IngredientPut ingredient) {
        String rawEntry = ingredient.getRawEntry();
        Integer wholeQuantity = 0;
        Double fractionQuantity = 0.0;
        if (ingredient.getFractionalQuantity() != null && !ingredient.getFractionalQuantity().isEmpty()) {
            FractionType fraction = FractionType.fromDisplayName(ingredient.getFractionalQuantity());
            if (fraction == null) {
                double fractionValue = RoundingUtils.doubleFromStringFraction(ingredient.getFractionalQuantity());
                fraction = FractionUtils.getFractionTypeForDecimal(new BigDecimal(fractionValue));

            }
            fractionQuantity = FractionType.doubleValueOf(fraction);
            // handle entry changes -- also fraction types of 0 and 1
            rawEntry = rawEntry.replace(ingredient.getFractionalQuantity(), fraction.getDisplayName());
            dishItemDTO.setFractionalQuantity(fraction);
        }

        if (ingredient.getWholeQuantity() != null) {
            wholeQuantity = ingredient.getWholeQuantity();
        }

        Double quantity = wholeQuantity.doubleValue();
        quantity += fractionQuantity;

        dishItemDTO.setWholeQuantity(wholeQuantity);
        dishItemDTO.setQuantity(quantity);
        dishItemDTO.setRawEntry(rawEntry);

    }

    private void validateAndFillFromQuantity(DishItemDTO dishItemDTO, IngredientPut ingredient) {
        Double quantity = 0.0;
        Double originalQuantity = ingredient.getQuantity();
        BigDecimal bigDecimal = new BigDecimal(String.valueOf(originalQuantity));
        int wholeNumber = bigDecimal.intValue();
        BigDecimal decimalPart = bigDecimal.subtract(new BigDecimal(wholeNumber));
        FractionType fraction = FractionUtils.getFractionTypeForDecimal(decimalPart);
        quantity += wholeNumber;
        quantity += FractionType.doubleValueOf(fraction);

        String rawEntry = ingredient.getRawEntry();
        if (!Objects.equals(quantity, originalQuantity)) {
            // a change was made - replace in raw entry
            rawEntry = rawEntry.replace(String.valueOf(originalQuantity), String.valueOf(quantity) );
        }

        dishItemDTO.setWholeQuantity(wholeNumber);
        dishItemDTO.setFractionalQuantity(fraction);
        dishItemDTO.setQuantity(quantity);
        dishItemDTO.setRawEntry(rawEntry);
    }

    private boolean ingredientHasAmount(IngredientPut ingredient) {
        return ingredient.getQuantity() != null ||
                ingredient.getFractionalQuantity() != null ||
                ingredient.getWholeQuantity() != null;
    }

    private Long stringToLongOrException(String toConvert) {
        if (toConvert == null) {
            return null;
        }
        try {
            return Long.parseLong(toConvert);
        } catch (NumberFormatException e) {
            throw new BadRequestException(String.format("Id [%s] cannot be converted to Long.", toConvert));
        }
    }

    private Long stringToLongOrDefault(String toConvert, Long defaultValue) {
        if (toConvert == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(toConvert);
        } catch (NumberFormatException e) {
            String message = String.format("Id [%s] cannot be converted to Long.", toConvert);
            logger.info(message);
        }
        return defaultValue;
    }

    private Long longValueOf(String longValueAsString) {
        Long longValue = null;
        try {
            longValue = Long.valueOf(longValueAsString);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid unit id");
        }
        return longValue;
    }


    private List<DishResource> findDishes(Long userId, String includedTags, String excludedTags,
                                          String searchFragment, String sortKey, String sortDirection) {
        String message = String.format("find dishesfor user [%S] - search [%S]", userId, searchFragment);
        logger.info(message);

        var criteria = new DishSearchCriteria(userId);
        if (includedTags != null) {
            List<Long> tagIdList = FlatStringUtils.inflateStringToLongList(includedTags,",");
            criteria.setIncludedTagIds(tagIdList);
        }
        if (excludedTags != null) {
            List<Long> tagIdList = FlatStringUtils.inflateStringToLongList(excludedTags,",");
            criteria.setExcludedTagIds(tagIdList);
        }
        if (!ObjectUtils.isEmpty(sortKey)) {
            var dishSortKey = Enums.getIfPresent(DishSortKey.class, sortKey).orNull();
            criteria.setSortKey(dishSortKey);
        }
        if (!ObjectUtils.isEmpty(sortDirection)) {
            var dishSortDirection = Enums.getIfPresent(DishSortDirection.class, sortDirection).orNull();
            criteria.setSortDirection(dishSortDirection);
        }
        if (!ObjectUtils.isEmpty(searchFragment)) {
            criteria.setNameFragment(searchFragment);
        }
        logger.debug("Searching for dishes with criteria [{}]. ", criteria);
        return dishSearchService.findDishes(criteria).stream()
                .map(d -> ModelMapper.toV2DishModel(d, false))
                .map(DishResource::new)
                .collect(Collectors.toList());
    }

    private List<DishResource> getAllDishes(Long userId) {
        return dishService.getDishesForUser(userId).stream()
                .map(d -> ModelMapper.toV2DishModel(d, false))
                .map(DishResource::new)
                .collect(Collectors.toList());

    }
}
