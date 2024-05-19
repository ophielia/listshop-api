package com.meg.listshop.lmt.api.controller.v2;

import com.meg.listshop.lmt.api.exception.BadParameterException;
import com.meg.listshop.lmt.api.model.IngredientListResource;
import com.meg.listshop.lmt.api.model.v2.IngredientPut;
import com.meg.listshop.lmt.api.model.v2.IngredientResource;
import com.meg.listshop.lmt.api.model.v2.DishResource;
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
    ResponseEntity<IngredientListResource> getIngredientsByDishId(HttpServletRequest request, Authentication authentication, @PathVariable("dishId") Long dishId) throws BadParameterException;

    @PostMapping(value = "/{dishId}/ingredients", produces = "application/json")
    ResponseEntity<Object> addIngredientToDish(Authentication authentication, @PathVariable Long dishId, @RequestBody IngredientPut ingredient);

    @PutMapping(value = "/{dishId}/ingredients", produces = "application/json")
    ResponseEntity<Object> updateIngredientInDish(Authentication authentication, @PathVariable Long dishId, @RequestBody IngredientPut ingredient);

    @DeleteMapping(value = "/{dishId}/ingredients/{ingredientId}", produces = "application/json")
    ResponseEntity<Object> deleteIngredientFromDish(Authentication authentication, @PathVariable Long dishId, @PathVariable Long ingredientId) throws BadParameterException;

}
