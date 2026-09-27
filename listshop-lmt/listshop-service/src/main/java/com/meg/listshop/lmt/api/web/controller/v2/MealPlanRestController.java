/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.api.web.controller.v2;

import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.CustomUserDetails;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.lmt.api.controller.v2.MealPlanRestControllerApi;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.api.exception.ObjectNotYoursException;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.data.entity.MealPlanEntity;
import com.meg.listshop.lmt.service.MealPlanService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 20/10/2017.
 */
@Controller
public class MealPlanRestController implements MealPlanRestControllerApi {

    private final MealPlanService mealPlanService;

    private final UserService userService;

    @Autowired
    public MealPlanRestController(MealPlanService mealPlanService, UserService userService) {
        this.mealPlanService = mealPlanService;
        this.userService = userService;
    }

    @Override
    public ResponseEntity<MealPlanList> retrieveMealPlans(HttpServletRequest request, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        List<MealPlan> mealPlanList = mealPlanService
                .getMealPlansForUserName(userDetails.getUsername())
                .stream()
                .map(mealPlanEntity -> ModelMapper.toModel(mealPlanEntity, false))
                .collect(Collectors.toList());

        MealPlanList list = new MealPlanList(mealPlanList);
        return new ResponseEntity<>(list, HttpStatus.OK);


    }

    @Override
    public ResponseEntity<Object> createMealPlan(HttpServletRequest request, Authentication authentication, @RequestBody MealPlan input) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        MealPlanEntity mealPlanEntity = ModelMapper.toEntity(input);

        MealPlanEntity result = mealPlanService.createMealPlan(userDetails.getUsername(), mealPlanEntity);

        if (result != null) {
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{mealPlanId}")
                    .buildAndExpand(result.getId())
                    .toUri();

            return ResponseEntity.created(location).body(ModelMapper.toModel(result, false));
        }
        return ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<Object> createMealPlanFromTargetProposal(HttpServletRequest request, Authentication authentication, @PathVariable("proposalId") Long proposalId) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        MealPlanEntity result = mealPlanService.createMealPlanFromProposal(userDetails.getUsername(), proposalId);

        if (result != null) {
            URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/v2/mealplan/{mealPlanId}")
                    .buildAndExpand(result.getId())
                    .toUri();

            return ResponseEntity.created(location).build();
        }
        return ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<MealPlan> readMealPlan(Principal principal, @PathVariable("mealPlanId") Long mealPlanId) {
        MealPlanEntity result = this.mealPlanService
                .getMealPlanById(principal.getName(), mealPlanId);

        MealPlan mealPlan = ModelMapper.toModel(result, true);
        return new ResponseEntity<>(mealPlan, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> copyMealPlan(HttpServletRequest request, Authentication authentication, @PathVariable("mealPlanId") Long mealPlanId) throws ObjectNotYoursException, ObjectNotFoundException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        MealPlanEntity mealPlan = this.mealPlanService.copyMealPlan(userDetails.getUsername(), mealPlanId);

        if (mealPlan != null) {
            URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/v2/mealplan/{mealPlanId}")
                    .buildAndExpand(mealPlan.getId())
                    .toUri();

            return ResponseEntity.created(location).build();
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @Override
    public ResponseEntity<MealPlan> deleteMealPlan(Authentication authentication, @PathVariable("mealPlanId") Long mealPlanId) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        mealPlanService.deleteMealPlan(userDetails.getUsername(), mealPlanId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> renameMealPlan(Authentication authentication, @PathVariable("mealPlanId") Long mealPlanId,
                                                 @PathVariable("newName") String newName) throws ObjectNotYoursException, ObjectNotFoundException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        this.mealPlanService.renameMealPlan(userDetails.getUsername(), mealPlanId, newName);
        return ResponseEntity.noContent().build();
    }


    @Override
    public ResponseEntity<Object> addDishToMealPlan(Authentication authentication, @PathVariable("mealPlanId") Long mealPlanId,
                                                    @PathVariable("dishId") Long dishId) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserEntity user = userService.getUserById(userDetails.getId());

        this.mealPlanService.addDishToMealPlan(user.getEmail(), mealPlanId, dishId);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> deleteDishFromMealPlan(Authentication authentication, @PathVariable("mealPlanId") Long mealPlanId, @PathVariable("dishId") Long dishId) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserEntity user = userService.getUserById(userDetails.getId());

        this.mealPlanService.deleteDishFromMealPlan(user.getEmail(), mealPlanId, dishId);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<RatingUpdateInfo> getRatingUpdateInfo(Authentication authentication, @PathVariable("mealPlanId") Long mealPlanId) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserEntity user = userService.getUserById(userDetails.getId());

        RatingUpdateInfo ratingInfo = this.mealPlanService.getRatingsForMealPlan(user.getEmail(), mealPlanId);

        return new ResponseEntity<>(ratingInfo, HttpStatus.OK);
    }

}
