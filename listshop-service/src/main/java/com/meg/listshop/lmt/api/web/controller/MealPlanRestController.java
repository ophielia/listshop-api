package com.meg.listshop.lmt.api.web.controller;

import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.lmt.api.controller.MealPlanRestControllerApi;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.api.exception.ObjectNotYoursException;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.data.entity.MealPlanEntity;
import com.meg.listshop.lmt.service.MealPlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
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

    private static final Logger  logger = LoggerFactory.getLogger(MealPlanRestController.class);


    private final MealPlanService mealPlanService;

    private final UserService userService;

    @Autowired
    public MealPlanRestController(MealPlanService mealPlanService, UserService userService) {
        this.mealPlanService = mealPlanService;
        this.userService = userService;
    }

    @Override
    public ResponseEntity<MealPlanListResource> retrieveMealPlans(HttpServletRequest request, Principal principal) {
        List<MealPlanResource> mealPlanList = mealPlanService
                .getMealPlansForUserName(principal.getName())
                .stream()
                .map(mealPlanEntity -> ModelMapper.toModel(mealPlanEntity, false))
                .map(MealPlanResource::new)
                .collect(Collectors.toList());

        MealPlanListResource resource = new MealPlanListResource(mealPlanList);
        resource.fillLinks(request, resource);
        return new ResponseEntity<>(resource, HttpStatus.OK);


    }

    @Override
    public ResponseEntity<Object> createMealPlan(HttpServletRequest request, Principal principal, @RequestBody MealPlan input) {
        MealPlanEntity mealPlanEntity = ModelMapper.toEntity(input);

        MealPlanEntity result = mealPlanService.createMealPlan(principal.getName(), mealPlanEntity);

        if (result != null) {
            MealPlanResource resource = new MealPlanResource(ModelMapper.toModel(result, false));

            HttpHeaders headers = new HttpHeaders();
            try {
                String link = resource.selfLink(request, resource).toString();
                headers.setLocation(new URI(link));
            } catch (URISyntaxException e) {
                logger.error("Can't parse meal plan link");
                return ResponseEntity.badRequest().build();
            }


            return new ResponseEntity<>(resource, headers, HttpStatus.CREATED);
        }
        return ResponseEntity.badRequest().build();

    }

    @Override
    public ResponseEntity<Object> createMealPlanFromTargetProposal(HttpServletRequest request, Principal principal, @PathVariable Long proposalId) {
        MealPlanEntity result = mealPlanService.createMealPlanFromProposal(principal.getName(), proposalId);

        if (result != null) {
            MealPlanResource resource = new MealPlanResource(ModelMapper.toModel(result, false));
            String link = resource.selfLink(request, resource).toString();

            return ResponseEntity.created(URI.create(link)).build();
        }
        return ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<MealPlanResource> readMealPlan(Principal principal, @PathVariable Long mealPlanId) {
        MealPlanEntity result = this.mealPlanService
                .getMealPlanById(principal.getName(), mealPlanId);

        MealPlanResource mealPlanResource = new MealPlanResource(ModelMapper.toModel(result, true));
        return new ResponseEntity<>(mealPlanResource, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> copyMealPlan(HttpServletRequest request, Principal principal, @PathVariable("mealPlanId") Long mealPlanId) throws ObjectNotYoursException, ObjectNotFoundException {
        MealPlanEntity mealPlan = this.mealPlanService.copyMealPlan(principal.getName(), mealPlanId);

        if (mealPlan != null) {
            MealPlanResource resource = new MealPlanResource(ModelMapper.toModel(mealPlan, false));
            String link = resource.selfLink(request, resource).toString();

            return ResponseEntity.created(URI.create(link)).build();
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @Override
    public ResponseEntity<MealPlan> deleteMealPlan(Principal principal, @PathVariable Long mealPlanId) {

        mealPlanService.deleteMealPlan(principal.getName(), mealPlanId);
        return ResponseEntity.noContent().build();
    }

    //@RequestMapping(method = RequestMethod.POST, value = "/{mealPlanId}/name", produces = "application/json")
    public ResponseEntity<Object> renameMealPlan(Principal principal, @PathVariable Long mealPlanId, @PathVariable String newName) throws ObjectNotYoursException, ObjectNotFoundException {
        this.mealPlanService.renameMealPlan(principal.getName(), mealPlanId, newName);
        return ResponseEntity.noContent().build();
    }


    @Override
    public ResponseEntity<Object> addDishToMealPlan(Principal principal, @PathVariable Long mealPlanId, @PathVariable Long dishId) {
        UserEntity user = userService.getUserByUserEmail(principal.getName());

        this.mealPlanService.addDishToMealPlan(user.getEmail(), mealPlanId, dishId);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> deleteDishFromMealPlan(Principal principal, @PathVariable Long mealPlanId, @PathVariable Long dishId) {
        UserEntity user = userService.getUserByUserEmail(principal.getName());

        this.mealPlanService.deleteDishFromMealPlan(user.getEmail(), mealPlanId, dishId);

        return ResponseEntity.noContent().build();
    }

    @Override
    //@RequestMapping(method=RequestMethod.GET, value = "/{mealPlanId}/ratings", produces = "application/json")
    public ResponseEntity<RatingUpdateInfoResource> getRatingUpdateInfo(Principal principal, @PathVariable Long mealPlanId) {
        UserEntity user = userService.getUserByUserEmail(principal.getName());

        RatingUpdateInfo ratingInfo = this.mealPlanService.getRatingsForMealPlan(user.getEmail(), mealPlanId);
        RatingUpdateInfoResource ratingResource = new RatingUpdateInfoResource(ratingInfo);

        return new ResponseEntity<>(ratingResource, HttpStatus.OK);
    }

}
