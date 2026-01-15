package com.meg.listshop.lmt.api.controller;

import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.api.exception.ObjectNotYoursException;
import com.meg.listshop.lmt.api.model.MealPlan;
import com.meg.listshop.lmt.api.model.MealPlanListResource;
import com.meg.listshop.lmt.api.model.MealPlanResource;
import com.meg.listshop.lmt.api.model.RatingUpdateInfoResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@RestController
@RequestMapping("/mealplan")
@CrossOrigin
public interface MealPlanRestControllerApi {


    @GetMapping(produces = "application/json")
    ResponseEntity<MealPlanListResource> retrieveMealPlans(HttpServletRequest request, Principal principal) throws ObjectNotFoundException, ObjectNotYoursException;

    @PostMapping(produces = "application/json", consumes = "application/json")
    ResponseEntity<Object> createMealPlan(HttpServletRequest request, Principal principal, @RequestBody MealPlan input);

    @PostMapping(value = "/proposal/{proposalId}", produces = "application/json", consumes = "application/json")
    ResponseEntity<Object> createMealPlanFromTargetProposal(HttpServletRequest request, Principal principal, @PathVariable("proposalId") Long proposalId) throws ObjectNotFoundException, ObjectNotYoursException;

    @GetMapping(value = "/{mealPlanId}", produces = "application/json")
    ResponseEntity<MealPlanResource> readMealPlan(Principal principal, @PathVariable("mealPlanId") Long mealPlanId) throws ObjectNotYoursException, ObjectNotFoundException;

    @PostMapping(value = "/{mealPlanId}", produces = "application/json")
    ResponseEntity<Object> copyMealPlan(HttpServletRequest request, Principal principal, @PathVariable("mealPlanId") Long mealPlanId) throws ObjectNotYoursException, ObjectNotFoundException;

    @DeleteMapping(value = "/{mealPlanId}", produces = "application/json")
    ResponseEntity<MealPlan> deleteMealPlan(Principal principal, @PathVariable("mealPlanId") Long mealPlanId) throws ObjectNotFoundException, ObjectNotYoursException;

    @PostMapping(value = "/{mealPlanId}/dish/{dishId}", produces = "application/json")
    ResponseEntity<Object> addDishToMealPlan(Principal principal, @PathVariable("mealPlanId") Long mealPlanId, @PathVariable("dishId") Long dishId) throws ObjectNotFoundException, ObjectNotYoursException;

    @PostMapping(value = "/{mealPlanId}/name/{newName}", produces = "application/json")
    ResponseEntity<Object> renameMealPlan(Principal principal, @PathVariable("mealPlanId") Long mealPlanId, @PathVariable("newName") String newName) throws ObjectNotYoursException, ObjectNotFoundException;

    @DeleteMapping(value = "/{mealPlanId}/dish/{dishId}", produces = "application/json")
    ResponseEntity<Object> deleteDishFromMealPlan(Principal principal, @PathVariable("mealPlanId") Long mealPlanId, @PathVariable("dishId") Long dishId) throws ObjectNotFoundException, ObjectNotYoursException;

    @GetMapping(value = "/{mealPlanId}/ratings", produces = "application/json")
    ResponseEntity<RatingUpdateInfoResource> getRatingUpdateInfo(Principal principal, @PathVariable("mealPlanId") Long mealPlanId);


}
