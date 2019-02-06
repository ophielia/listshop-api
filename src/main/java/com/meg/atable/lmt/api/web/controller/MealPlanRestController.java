package com.meg.atable.lmt.api.web.controller;

import com.meg.atable.lmt.api.controller.MealPlanRestControllerApi;
import com.meg.atable.lmt.api.exception.ObjectNotFoundException;
import com.meg.atable.lmt.api.exception.ObjectNotYoursException;
import com.meg.atable.lmt.api.model.MealPlan;
import com.meg.atable.lmt.api.model.MealPlanResource;
import com.meg.atable.lmt.api.model.ModelMapper;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.lmt.data.entity.MealPlanEntity;
import com.meg.atable.lmt.service.MealPlanService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 20/10/2017.
 */
@Controller
public class MealPlanRestController implements MealPlanRestControllerApi {

    private static final Logger logger = LogManager.getLogger(MealPlanRestController.class);


    @Autowired
    private MealPlanService mealPlanService;

    @Autowired
    private UserService userService;


    @Override
    public ResponseEntity<Resources<MealPlanResource>> retrieveMealPlans(Principal principal) throws ObjectNotYoursException,  ObjectNotFoundException {
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
            MealPlanResource mealPlanResource = new MealPlanResource(result);
            Link forOneMealPlan = mealPlanResource.getLink("self");
            HttpHeaders headers = new HttpHeaders();
            try {
                headers.setLocation(new URI(forOneMealPlan.getHref()));
            } catch (URISyntaxException e) {
                logger.error("Can't parse meal plan link");
                return ResponseEntity.badRequest().build();
            }


            ResponseEntity<Object> r = new ResponseEntity(mealPlanResource, headers, HttpStatus.CREATED);
            return r;

        }
        return ResponseEntity.badRequest().build();

    }

    @Override
    public ResponseEntity<Object> createMealPlanFromTargetProposal(Principal principal, @PathVariable Long proposalId) throws ObjectNotFoundException, ObjectNotYoursException {
        MealPlanEntity result = mealPlanService.createMealPlanFromProposal(principal.getName(), proposalId);

        if (result != null) {
            Link forOneMealPlan = new MealPlanResource(result).getLink("self");
            return ResponseEntity.created(URI.create(forOneMealPlan.getHref())).build();
        }
        return ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<MealPlan> readMealPlan(Principal principal, @PathVariable Long mealPlanId) throws ObjectNotYoursException, ObjectNotFoundException {
        MealPlanEntity mealPlan = this.mealPlanService
                .getMealPlanById(principal.getName(), mealPlanId);

        MealPlanResource mealPlanResource = new MealPlanResource(mealPlan);

        return new ResponseEntity(mealPlanResource, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MealPlan> deleteMealPlan(Principal principal, @PathVariable Long mealPlanId) throws ObjectNotFoundException, ObjectNotYoursException {

        boolean success = mealPlanService.deleteMealPlan(principal.getName(), mealPlanId);
        if (success) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().build();
    }

    //@RequestMapping(method = RequestMethod.POST, value = "/{mealPlanId}/name", produces = "application/json")
    public ResponseEntity<Object> renameMealPlan(Principal principal, @PathVariable Long mealPlanId, @PathVariable String newName) throws ObjectNotYoursException, ObjectNotFoundException {
        this.mealPlanService.renameMealPlan(principal.getName(), mealPlanId, newName);
        return ResponseEntity.noContent().build();
    }


    @Override
    public ResponseEntity<Object> addDishToMealPlan(Principal principal, @PathVariable Long mealPlanId, @PathVariable Long dishId) throws ObjectNotFoundException, ObjectNotYoursException {
        UserAccountEntity user = userService.getUserByUserName(principal.getName());

        this.mealPlanService.addDishToMealPlan(user.getUsername(), mealPlanId, dishId);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> deleteDishFromMealPlan(Principal principal, @PathVariable Long mealPlanId, @PathVariable Long dishId) throws ObjectNotFoundException, ObjectNotYoursException {
        UserAccountEntity user = userService.getUserByUserName(principal.getName());

        this.mealPlanService.deleteDishFromMealPlan(user.getUsername(), mealPlanId, dishId);

        return ResponseEntity.noContent().build();
    }
}