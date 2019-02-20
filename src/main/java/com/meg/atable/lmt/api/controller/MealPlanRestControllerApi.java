package com.meg.atable.lmt.api.controller;

import com.meg.atable.lmt.api.exception.ObjectNotFoundException;
import com.meg.atable.lmt.api.exception.ObjectNotYoursException;
import com.meg.atable.lmt.api.model.MealPlan;
import com.meg.atable.lmt.api.model.MealPlanResource;
import com.meg.atable.lmt.api.model.RatingUpdateInfoResource;
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
    ResponseEntity<Resources<MealPlanResource>> retrieveMealPlans(Principal principal) throws ObjectNotFoundException, ObjectNotYoursException;

    @RequestMapping(method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    ResponseEntity<Object> createMealPlan(Principal principal, @RequestBody MealPlan input);

    @RequestMapping(method = RequestMethod.POST, value = "/proposal/{proposalId}", produces = "application/json", consumes = "application/json")
    ResponseEntity<Object> createMealPlanFromTargetProposal(Principal principal, @PathVariable Long proposalId) throws ObjectNotFoundException, ObjectNotYoursException;

    @RequestMapping(method = RequestMethod.GET, value = "/{mealPlanId}", produces = "application/json")
    ResponseEntity<MealPlan> readMealPlan(Principal principal, @PathVariable("mealPlanId") Long mealPlanId) throws ObjectNotYoursException, ObjectNotFoundException;

    @RequestMapping(method = RequestMethod.DELETE, value = "/{mealPlanId}", produces = "application/json")
    ResponseEntity<MealPlan> deleteMealPlan(Principal principal, @PathVariable("mealPlanId") Long mealPlanId) throws ObjectNotFoundException, ObjectNotYoursException;

    @RequestMapping(method = RequestMethod.POST, value = "/{mealPlanId}/dish/{dishId}", produces = "application/json")
    ResponseEntity<Object> addDishToMealPlan(Principal principal, @PathVariable Long mealPlanId, @PathVariable Long dishId) throws ObjectNotFoundException, ObjectNotYoursException;

    @RequestMapping(method = RequestMethod.POST, value = "/{mealPlanId}/name/{newName}", produces = "application/json")
    ResponseEntity<Object> renameMealPlan(Principal principal, @PathVariable Long mealPlanId, @PathVariable String newName) throws ObjectNotYoursException, ObjectNotFoundException;

    @RequestMapping(method = RequestMethod.DELETE, value = "/{mealPlanId}/dish/{dishId}", produces = "application/json")
    ResponseEntity<Object> deleteDishFromMealPlan(Principal principal, @PathVariable Long mealPlanId, @PathVariable Long dishId) throws ObjectNotFoundException, ObjectNotYoursException;

    @RequestMapping(method = RequestMethod.GET, value = "/{mealPlanId}/ratings", produces = "application/json")
    ResponseEntity<RatingUpdateInfoResource> getRatingUpdateInfo(Principal principal, @PathVariable Long mealPlanId);


}
