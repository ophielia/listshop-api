package com.meg.atable.web.controller;

import com.meg.atable.api.controller.MealPlanRestControllerApi;
import com.meg.atable.api.model.MealPlan;
import com.meg.atable.api.model.MealPlanResource;
import com.meg.atable.api.model.ModelMapper;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.data.entity.MealPlanEntity;
import com.meg.atable.service.MealPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 20/10/2017.
 */
@Controller
public class MealPlanRestController implements MealPlanRestControllerApi {

    @Autowired
    private MealPlanService mealPlanService;

    @Autowired
    private UserService userService;


    @Override
    public ResponseEntity<Resources<MealPlanResource>> retrieveMealPlans(Principal principal) {
        List<MealPlanResource> mealPlanList = mealPlanService
                .getMealPlansForUserName(principal.getName())
                .stream().map(MealPlanResource::new)
                .collect(Collectors.toList());

        Resources<MealPlanResource> mealPlanResourceList = new Resources<>(mealPlanList);
        return new ResponseEntity(mealPlanResourceList, HttpStatus.OK);

    }

    @Override
    public ResponseEntity<Object> createMealPlan(Principal principal, @RequestBody MealPlan input) {
        //this.getUserForPrincipal(principal);
        MealPlanEntity mealPlanEntity = ModelMapper.toEntity(input);

        MealPlanEntity result = mealPlanService.createMealPlan(principal.getName(), mealPlanEntity);

        if (result != null) {
            Link forOneMealPlan = new MealPlanResource(result).getLink("self");
            return ResponseEntity.created(URI.create(forOneMealPlan.getHref())).build();
        }
        return ResponseEntity.badRequest().build();

    }

    @Override
    public ResponseEntity<Object> createMealPlanFromTargetProposal(Principal principal, @PathVariable Long proposalId) {
        MealPlanEntity result = mealPlanService.createMealPlanFromProposal(principal.getName(), proposalId);

        if (result != null) {
            Link forOneMealPlan = new MealPlanResource(result).getLink("self");
            return ResponseEntity.created(URI.create(forOneMealPlan.getHref())).build();
        }
        return ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<MealPlan> readMealPlan(Principal principal, @PathVariable Long mealPlanId) {
        MealPlanEntity mealPlan = this.mealPlanService
                .getMealPlanById(principal.getName(), mealPlanId);

        MealPlanResource mealPlanResource = new MealPlanResource(mealPlan);

        return new ResponseEntity(mealPlanResource, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MealPlan> deleteMealPlan(Principal principal, @PathVariable Long mealPlanId) {

        boolean success = mealPlanService.deleteMealPlan(principal.getName(), mealPlanId);
        if (success) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().build();
    }

    //@RequestMapping(method = RequestMethod.POST, value = "/{mealPlanId}/name", produces = "application/json")
    public ResponseEntity<Object> renameMealPlan(Principal principal, @PathVariable Long mealPlanId, @PathVariable String newName) {
        this.mealPlanService.renameMealPlan(principal.getName(),mealPlanId,newName );
        return ResponseEntity.noContent().build();
    }


    @Override
    public ResponseEntity<Object> addDishToMealPlan(Principal principal, @PathVariable Long mealPlanId, @PathVariable Long dishId) {
        UserAccountEntity user = userService.getUserByUserName(principal.getName());

        this.mealPlanService.addDishToMealPlan(user.getUsername(), mealPlanId, dishId);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> deleteDishFromMealPlan(Principal principal, @PathVariable Long mealPlanId, @PathVariable Long dishId) {
        UserAccountEntity user = userService.getUserByUserName(principal.getName());

        this.mealPlanService.deleteDishFromMealPlan(user.getUsername(), mealPlanId, dishId);

        return ResponseEntity.noContent().build();
    }
}
