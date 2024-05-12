package com.meg.listshop.lmt.api.web.controller.v2;

import com.meg.listshop.lmt.api.controller.v2.V2DishRestControllerApi;
import com.meg.listshop.lmt.api.model.DishResource;
import com.meg.listshop.lmt.api.model.Ingredient;
import com.meg.listshop.lmt.api.model.IngredientResource;
import com.meg.listshop.lmt.api.model.TagResource;
import com.meg.listshop.lmt.service.DishSearchService;
import com.meg.listshop.lmt.service.DishService;
import com.meg.listshop.lmt.service.tag.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@CrossOrigin
public class V2DishRestController implements V2DishRestControllerApi {

    private static final Logger logger = LoggerFactory.getLogger(V2DishRestController.class);

    private final DishService dishService;
    private final DishSearchService dishSearchService;
    private final TagService tagService;

    @Autowired
    V2DishRestController(DishService dishService,
                         DishSearchService dishSearchService,
                         TagService tagService) {
        this.dishService = dishService;
        this.tagService = tagService;
        this.dishSearchService = dishSearchService;
    }

    @Override
    public ResponseEntity<DishResource> retrieveDish(HttpServletRequest request, Authentication authentication, Long dishId) {
        //@GetMapping(value = "/{dishId}", produces = "application/json")
        return null;
    }

    @Override
    public ResponseEntity<CollectionModel<IngredientResource>> getIngredientssByDishId(HttpServletRequest request, Authentication authentication, Long dishId) {
        //@GetMapping(value = "/{dishId}/ingredients", produces = "application/json")
        return null;
    }

    @Override
    public ResponseEntity<Object> addIngredientToDish(Authentication authentication, Long dishId, Ingredient ingredient) {
        //@PostMapping(value = "/{dishId}/ingredient", produces = "application/json")
        // validate / translate ingredient to dishItem => strings to long, resolving fraction
        // pull raw modifiers
        // sent to service with dishId, dishItemEntity, and rawModifiers
        // service in charge of processing modifiers into markers and unit size, and resolving quantity to decimal, and saving
        return null;
    }

    @Override
    public ResponseEntity<Object> updateIngredientToDish(Authentication authentication, Long dishId, Ingredient ingredient) {
        //@PutMapping(value = "/{dishId}/ingredient", produces = "application/json")
        return null;
    }

    @Override
    public ResponseEntity<Object> deleteIngredientFromDish(Authentication authentication, Long dishId, Long ingredientId) {
        //@DeleteMapping(value = "/{dishId}/ingredient/{ingredientId}", produces = "application/json")
        return null;
    }


}
