package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.lmt.api.exception.ActionIgnoredException;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.api.exception.ObjectNotYoursException;
import com.meg.listshop.lmt.api.model.MealPlanType;
import com.meg.listshop.lmt.api.model.RatingUpdateInfo;
import com.meg.listshop.lmt.data.entity.*;
import com.meg.listshop.lmt.data.repository.MealPlanRepository;
import com.meg.listshop.lmt.data.repository.SlotRepository;
import com.meg.listshop.lmt.dish.DishService;
import com.meg.listshop.lmt.service.MealPlanService;
import com.meg.listshop.lmt.service.proposal.ProposalService;
import com.meg.listshop.lmt.service.tag.TagService;
import io.jsonwebtoken.lang.Collections;
import me.atrox.haikunator.Haikunator;
import me.atrox.haikunator.HaikunatorBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 20/10/2017.
 */
@Service
public class MealPlanServiceImpl implements MealPlanService {

    private static final Logger logger = LoggerFactory.getLogger(MealPlanServiceImpl.class);

    private static final String MEAL_PLAN = "Meal Plan";

    private UserService userService;

    private MealPlanRepository mealPlanRepository;

    private SlotRepository slotRepository;

    private DishService dishService;

    private ProposalService targetProposalService;


    private TagService tagService;

    @Autowired
    public MealPlanServiceImpl(UserService userService,
                               MealPlanRepository mealPlanRepository,
                               SlotRepository slotRepository,
                               DishService dishService,
                               ProposalService targetProposalService,
                               TagService tagService) {
        this.userService = userService;
        this.mealPlanRepository = mealPlanRepository;
        this.slotRepository = slotRepository;
        this.dishService = dishService;
        this.targetProposalService = targetProposalService;
        this.tagService = tagService;
    }

    public List<MealPlanEntity> getMealPlansForUserName(String username) {
        // get user
        UserEntity user = userService.getUserByUserEmail(username);

        return mealPlanRepository.findByUserIdOrderByCreated(user.getId());
    }

    public MealPlanEntity createMealPlan(String username, MealPlanEntity mealPlanEntity) {
        // get username
        UserEntity user = userService.getUserByUserEmail(username);

        // check name - if null or empty, autoname
        if (mealPlanEntity.getName() == null || mealPlanEntity.getName().isEmpty()) {
            Haikunator haikunator = new HaikunatorBuilder().setTokenLength(0).setDelimiter(" ").build();
            String mealPlanName = haikunator.haikunate();
            mealPlanEntity.setName(mealPlanName);
        }
        // createMealPlan with repository and return
        mealPlanEntity.setUserId(user.getId());
        mealPlanEntity.setCreated(new Date());
        return mealPlanRepository.save(mealPlanEntity);
    }

    public MealPlanEntity createMealPlan(Long userId, MealPlanEntity mealPlanEntity) {
        // check name - if null or empty, autoname
        if (mealPlanEntity.getName() == null || mealPlanEntity.getName().isEmpty()) {
            Haikunator haikunator = new HaikunatorBuilder().setTokenLength(0).setDelimiter(" ").build();
            String mealPlanName = haikunator.haikunate();
            mealPlanEntity.setName(mealPlanName);
        }
        // createMealPlan with repository and return
        mealPlanEntity.setUserId(userId);
        mealPlanEntity.setCreated(new Date());
        return mealPlanRepository.save(mealPlanEntity);
    }

    @Override
    public MealPlanEntity createMealPlanFromProposal(String username, Long proposalId) {
        // get username
        UserEntity user = userService.getUserByUserEmail(username);
        // get proposal
        ProposalEntity proposalEntity = targetProposalService.getTargetProposalById(username, proposalId);

        if (proposalEntity == null) {
            logger.debug(String.format("No proposal found with id [%s] for user [%s]", proposalId, user.getId()));
            return null;
        }
        logger.debug(String.format("Found proposal [%s] for user [%s]", proposalEntity.getId(), user.getId()));

        // create the Meal Plan Entity
        MealPlanEntity mealPlan = new MealPlanEntity();
        mealPlan.setUserId(user.getId());
        mealPlan.setCreated(new Date());
        mealPlan.setMealPlanType(MealPlanType.Targeted);
        mealPlan.setName("generated from " + proposalEntity.getTargetName());
        mealPlan = mealPlanRepository.save(mealPlan);

        // get targets for proposal
        List<ProposalSlotEntity> proposalSlots = proposalEntity.getSlots();
        if (proposalSlots == null) {
            return mealPlan;
        }
        for (ProposalSlotEntity proposalSlot : proposalSlots) {
            Long dishId = proposalSlot.getPickedDishId();
            if (dishId == null) {
                logger.warn(String.format("Null dish id found in proposal slot [%s]", proposalSlot.getId()));
                continue;
            }
            DishEntity dish = dishService.getDishForUserById(username, dishId);

            // add new meal plan slot
            SlotEntity slot = new SlotEntity();
            slot.setMealPlan(mealPlan);
            slot.setDish(dish);
            slotRepository.save(slot);
        }
        return mealPlan;
    }

    public MealPlanEntity getMealPlanById(String userName, Long mealPlanId)  {
        UserEntity user = userService.getUserByUserEmail(userName);

        Optional<MealPlanEntity> mealPlanEntityOpt = mealPlanRepository.findById(mealPlanId);
        if (!mealPlanEntityOpt.isPresent()) {
            final String msg = "No meal plan found by id for user [" + userName + "] and mealPlanId [" + mealPlanId + "]";
            throw new ObjectNotFoundException(msg, mealPlanId, MEAL_PLAN);
        }
        MealPlanEntity mealPlanEntity = mealPlanEntityOpt.get();
        // ensure that this meal plan belongs to the user
        if (!mealPlanEntity.getUserId().equals(user.getId())) {
            final String msg = "MealPlan found, but doesn't belong to user [" + userName + "] and mealPlanId [" + mealPlanId + "]";
            throw new ObjectNotYoursException(msg, MEAL_PLAN, mealPlanId, user.getEmail());
        }
        return mealPlanEntity;
    }

    public MealPlanEntity getMealPlanForUserById(Long userId, Long mealPlanId) throws ObjectNotFoundException, ObjectNotYoursException {

        Optional<MealPlanEntity> mealPlanEntityOpt = mealPlanRepository.findById(mealPlanId);
        if (!mealPlanEntityOpt.isPresent()) {
            final String msg = String.format("No meal plan found by id for user [%s] and mealPlanId [%s]", userId, mealPlanId);
            throw new ObjectNotFoundException(msg, mealPlanId, MEAL_PLAN);
        }
        MealPlanEntity mealPlanEntity = mealPlanEntityOpt.get();
        // ensure that this meal plan belongs to the user
        if (!mealPlanEntity.getUserId().equals(userId)) {
            final String msg = String.format("MealPlan found, but doesn't belong to user [%s] and mealPlanId [%s]", userId, mealPlanId);
            throw new ObjectNotYoursException(msg, MEAL_PLAN, mealPlanId, userId);
        }
        return mealPlanEntity;
    }


    public void addDishToMealPlan(String username, Long mealPlanId, Long dishId) {
        // get meal plan
        MealPlanEntity mealPlan = getMealPlanById(username, mealPlanId);

        // get dish
        DishEntity dish = dishService.getDishForUserById(username, dishId);

        // check if dish already exists in meal plan
        if (dishExistsInMealPlan(mealPlan, dish)) {
            throw new ActionIgnoredException(String.format("Dish (%S) can't be added to mealplan(%s), because it already exists", dish.getId(), mealPlan.getId()));
        }
        // add slot to dish
        List<SlotEntity> slotList = slotRepository.findByMealPlan(mealPlan);

        // add new slot
        SlotEntity slot = new SlotEntity();
        slot.setMealPlan(mealPlan);
        slot.setDish(dish);
        slotRepository.save(slot);

        slotList.add(slot);
        mealPlan.setSlots(slotList);
        mealPlanRepository.save(mealPlan);
    }

    public void addDishToMealPlan(Long userId, Long mealPlanId, Long dishId) throws ObjectNotYoursException, ObjectNotFoundException {
        // get meal plan
        MealPlanEntity mealPlan = getMealPlanForUserById(userId, mealPlanId);

        // get dish
        DishEntity dish = dishService.getDishForUserById(userId, dishId);

        // check if dish already exists in meal plan
        if (dishExistsInMealPlan(mealPlan, dish)) {
            throw new ActionIgnoredException(String.format("Dish (%S) can't be added to mealplan(%s), because it already exists", dish.getId(), mealPlan.getId()));
        }
        // add slot to dish
        List<SlotEntity> slotList = slotRepository.findByMealPlan(mealPlan);

        // add new slot
        SlotEntity slot = new SlotEntity();
        slot.setMealPlan(mealPlan);
        slot.setDish(dish);
        slotRepository.save(slot);

        slotList.add(slot);
        mealPlan.setSlots(slotList);
        mealPlanRepository.save(mealPlan);
    }

    private boolean dishExistsInMealPlan(MealPlanEntity mealPlan, DishEntity dish) {
        List<SlotEntity> existingSlots = slotRepository.findByMealPlanAndDish(mealPlan, dish);
        return !Collections.isEmpty(existingSlots);
    }

    public void deleteDishFromMealPlan(String username, Long mealPlanId, Long dishId) throws ObjectNotYoursException, ObjectNotFoundException {
        // get meal plan
        MealPlanEntity mealPlan = getMealPlanById(username, mealPlanId);


        // get slots
        List<SlotEntity> slotList = slotRepository.findByMealPlan(mealPlan);

        SlotEntity toDelete = null;
        List<SlotEntity> toSave = new ArrayList<>();
        for (SlotEntity slot : slotList) {
            if (slot.getDish().getId().longValue() == dishId.longValue()) {
                toDelete = slot;
            } else {
                toSave.add(slot);
            }
        }
        // filter slot to be deleted from plan
        mealPlan.setSlots(toSave);
        mealPlanRepository.save(mealPlan);
        if (toDelete != null) {
            slotRepository.delete(toDelete);
        }


    }


    public void deleteMealPlan(String name, Long mealPlanId) throws ObjectNotYoursException, ObjectNotFoundException {
        MealPlanEntity toDelete = getMealPlanById(name, mealPlanId);

        if (toDelete == null) {
            return;
        }
        if (!toDelete.getSlots().isEmpty()) {
            slotRepository.deleteAll(toDelete.getSlots());
            toDelete.setSlots(null);
        }
        mealPlanRepository.delete(toDelete);
    }

    public void renameMealPlan(String userName, Long mealPlanId, String newName)  {
        MealPlanEntity mealPlan = getMealPlanById(userName, mealPlanId);
        mealPlan.setName(newName);
        mealPlanRepository.save(mealPlan);
    }

    public List<TagEntity> fillInDishTags(MealPlanEntity mealPlan) {
        List<Long> dishIds = mealPlan.getSlots().stream()
                .map(s -> s.getDish().getId())
                .collect(Collectors.toList());

        return tagService.getIngredientTagsForDishes(dishIds);
    }

    public void updateLastAddedDateForDishes(MealPlanEntity mealPlan) {
        if (mealPlan == null || mealPlan.getSlots() == null) {
            return;
        }
        // get ids for dishes
        List<Long> dishIds = mealPlan.getSlots().stream()
                .map(s -> s.getDish().getId())
                .collect(Collectors.toList());
        // update lastAdded date
        List<DishEntity> dishes = dishService.getDishes(dishIds);
        for (DishEntity dish : dishes) {
            dish.setLastAdded(new Date());
        }
        // save dishes
        dishService.save(dishes);
    }

    public RatingUpdateInfo getRatingsForMealPlan(String username, Long mealPlanId) {
        // get mealplan
        MealPlanEntity mealPlan = getMealPlanById(username, mealPlanId);

        // gather dish ids
        List<Long> dishIds = new ArrayList<>();
        for (SlotEntity slot : mealPlan.getSlots()) {
            dishIds.add(slot.getDish().getId());
        }

        // call and return tag service method
        return tagService.getRatingUpdateInfoForDishIds(dishIds);
    }

    public MealPlanEntity copyMealPlan(String name, Long mealPlanId) {
        logger.debug("Copying mealPlan: {} for user {}", mealPlanId, name);
        // retrieve meal plan, ensuring meal plan belongs to user
        MealPlanEntity copyFrom = getMealPlanById(name, mealPlanId);

        // create meal plan
        MealPlanEntity copyTo = createMealPlan(name, new MealPlanEntity());

        // add all dishes from newly created dish to meal plan
        // get slotsToCopy
        List<SlotEntity> slotsToCopy = copyFrom.getSlots();

        if (!Collections.isEmpty(slotsToCopy)) {
            List<SlotEntity> slotsToCreate = new ArrayList<>();
            for (SlotEntity slot : slotsToCopy) {
                DishEntity dish = slot.getDish();
                // add new slot
                SlotEntity newSlot = new SlotEntity();
                newSlot.setMealPlan(copyTo);
                newSlot.setDish(dish);
                slotRepository.save(newSlot);

                slotsToCreate.add(newSlot);
            }
            copyTo.setSlots(slotsToCreate);
        }

        // return newly created meal plan
        return mealPlanRepository.save(copyTo);
    }

}
