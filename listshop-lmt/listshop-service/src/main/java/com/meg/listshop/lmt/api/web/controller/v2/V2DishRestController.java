package com.meg.listshop.lmt.api.web.controller.v2;

import com.github.dockerjava.api.exception.BadRequestException;
import com.meg.listshop.auth.service.impl.JwtUser;
import com.meg.listshop.lmt.api.controller.v2.V2DishRestControllerApi;
import com.meg.listshop.lmt.api.model.FractionType;
import com.meg.listshop.lmt.api.model.ModelMapper;
import com.meg.listshop.lmt.api.model.v2.DishResource;
import com.meg.listshop.lmt.api.model.v2.IngredientPut;
import com.meg.listshop.lmt.api.model.v2.IngredientResource;
import com.meg.listshop.lmt.data.pojos.DishDTO;
import com.meg.listshop.lmt.data.pojos.DishItemDTO;
import com.meg.listshop.lmt.service.DishService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.servlet.http.HttpServletRequest;

@Controller
@CrossOrigin
public class V2DishRestController implements V2DishRestControllerApi {

    private static final Logger logger = LoggerFactory.getLogger(V2DishRestController.class);

    private final DishService dishService;

    @Autowired
    V2DishRestController(DishService dishService) {
        this.dishService = dishService;
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
    public ResponseEntity<CollectionModel<IngredientResource>> getIngredientsByDishId(HttpServletRequest request, Authentication authentication, Long dishId) {
        //@GetMapping(value = "/{dishId}/ingredients", produces = "application/json")
        return null;
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
    public ResponseEntity<Object> deleteIngredientFromDish(Authentication authentication, Long dishId, Long ingredientId) {
        //@DeleteMapping(value = "/{dishId}/ingredient/{ingredientId}", produces = "application/json")
        return null;
    }


    private DishItemDTO validateIngredient(IngredientPut ingredient, Boolean verifyItemId) {
        if (ingredient == null) {
            throw new BadRequestException("Ingredient is null");
        }
        if (verifyItemId && ingredient.getId() == null) {
                throw new BadRequestException("Ingredient id is null");
        }
        if (ingredient.getTagId() == null) {
            throw new BadRequestException("Ingredient tag id is null");
        }
        if (ingredient.getUnitId() == null) {
            throw new BadRequestException("Ingredient unit id is null");
        }
        DishItemDTO dishItemDTO = new DishItemDTO();
        dishItemDTO.setDishItemId(stringToLongOrException(ingredient.getId()));
        dishItemDTO.setTagId(stringToLongOrException(ingredient.getTagId()));
        dishItemDTO.setUnitId(stringToLongOrException(ingredient.getUnitId()));

        if (ingredient.getWholeQuantity() != null) {
            dishItemDTO.setWholeQuantity(ingredient.getWholeQuantity());
        }
        if (ingredient.getFractionalQuantity() != null) {
            FractionType fraction = FractionType.valueOf(ingredient.getFractionalQuantity());
            dishItemDTO.setFractionalQuantity(fraction);
        }
        if (ingredient.getUnitId() != null) {
            dishItemDTO.setUnitId(longValueOf(ingredient.getUnitId()));
        }
        if (ingredient.getId() != null) {
            dishItemDTO.setDishItemId(longValueOf(ingredient.getId()));
        }
        dishItemDTO.setRawModifiers(ingredient.getRawModifiers());
        return dishItemDTO;
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

    private Long longValueOf(String longValueAsString) {
        Long longValue = null;
        try {
            longValue = Long.valueOf(longValueAsString);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid unit id");
        }
        return longValue;
    }
}
