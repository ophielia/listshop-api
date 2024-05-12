package com.meg.listshop.lmt.api.controller.v2;

import com.meg.listshop.lmt.api.model.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@RestController
@RequestMapping("/v2/dish")
@CrossOrigin
public interface V2DishRestControllerApi {

    @GetMapping(value = "/{dishId}", produces = "application/json")
    ResponseEntity<DishResource> retrieveDish(HttpServletRequest request, Authentication authentication, @PathVariable("dishId") Long dishId);

    @GetMapping(value = "/{dishId}/ingredients", produces = "application/json")
    ResponseEntity<CollectionModel<IngredientResource>> getIngredientssByDishId(HttpServletRequest request, Authentication authentication, @PathVariable("dishId") Long dishId);

    @PostMapping(value = "/{dishId}/ingredient", produces = "application/json")
    ResponseEntity<Object> addIngredientToDish(Authentication authentication, @PathVariable Long dishId, @RequestParam(value = "ingredient") Ingredient ingredient);

    @PutMapping(value = "/{dishId}/ingredient", produces = "application/json")
    ResponseEntity<Object> updateIngredientToDish(Authentication authentication, @PathVariable Long dishId, @RequestParam(value = "ingredient") Ingredient ingredient);

    @DeleteMapping(value = "/{dishId}/ingredient/{ingredientId}", produces = "application/json")
    ResponseEntity<Object> deleteIngredientFromDish(Authentication authentication, @PathVariable Long dishId, @PathVariable Long ingredientId);

}
