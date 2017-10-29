package com.meg.atable.api.controller;

import com.meg.atable.api.model.MealPlan;
import com.meg.atable.api.model.MealPlanResource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@RestController
@RequestMapping("/mealplan")
@CrossOrigin
public interface MealPlanRestControllerApi {


    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    ResponseEntity<Resources<MealPlanResource>> retrieveMealPlans(Principal principal);

    @RequestMapping(method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    ResponseEntity<Object> createMealPlan(Principal principal, @RequestBody MealPlan input);

    @RequestMapping(method = RequestMethod.GET, value = "/{mealPlanId}", produces = "application/json")
    public ResponseEntity<MealPlan> readMealPlan(Principal principal, @PathVariable("mealPlanId") Long mealPlanId);

    @RequestMapping(method = RequestMethod.DELETE, value = "/{mealPlanId}", produces = "application/json")
    public ResponseEntity<MealPlan> deleteMealPlan(Principal principal, @PathVariable("mealPlanId") Long mealPlanId);

    @RequestMapping(method = RequestMethod.POST, value = "/{mealPlanId}/dish/{dishId}", produces = "application/json")
    public ResponseEntity<Object> addDishToMealPlan(Principal principal, @PathVariable Long mealPlanId, @PathVariable Long dishId);

    @RequestMapping(method = RequestMethod.DELETE, value = "/{mealPlanId}/dish/{dishId}", produces = "application/json")
    public ResponseEntity<Object> deleteDishFromMealPlan(Principal principal, @PathVariable Long mealPlanId, @PathVariable Long dishId);

}
