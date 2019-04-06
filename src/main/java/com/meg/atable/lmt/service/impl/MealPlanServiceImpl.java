package com.meg.atable.lmt.service.impl;

import com.meg.atable.auth.data.entity.UserEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.lmt.api.exception.ObjectNotFoundException;
import com.meg.atable.lmt.api.exception.ObjectNotYoursException;
import com.meg.atable.lmt.api.model.MealPlanType;
import com.meg.atable.lmt.api.model.RatingUpdateInfo;
import com.meg.atable.lmt.data.entity.*;
import com.meg.atable.lmt.data.repository.MealPlanRepository;
import com.meg.atable.lmt.data.repository.SlotRepository;
import com.meg.atable.lmt.data.repository.TagRepository;
import com.meg.atable.lmt.service.DishService;
import com.meg.atable.lmt.service.MealPlanService;
import com.meg.atable.lmt.service.ProposalService;
import com.meg.atable.lmt.service.tag.TagService;
import me.atrox.haikunator.Haikunator;
import me.atrox.haikunator.HaikunatorBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger logger = LogManager.getLogger(ShoppingListServiceImpl.class);


    @Autowired
    private UserService userService;

    @Autowired
    private MealPlanRepository mealPlanRepository;

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private DishService dishService;

    @Autowired
    private ProposalService targetProposalService;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagService tagService;

    public List<MealPlanEntity> getMealPlansForUserName(String username) {
        // get user
        UserEntity user = userService.getUserByUserEmail(username);

        return mealPlanRepository.findByUserId(user.getId());
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

    @Override
    public MealPlanEntity createMealPlanFromProposal(String username, Long proposalId) {
        // get username
        UserEntity user = userService.getUserByUserEmail(username);
        // get proposal
        ProposalEntity proposalEntity = targetProposalService.getTargetProposalById(username, proposalId);

        if (proposalEntity == null) {
            return null;
        }

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
            throw new ObjectNotFoundException(msg, mealPlanId, "MealPlan");
        }
        MealPlanEntity mealPlanEntity = mealPlanEntityOpt.get();
        // ensure that this meal plan belongs to the user
        if (!mealPlanEntity.getUserId().equals(user.getId())) {
            final String msg = "MealPlan found, but doesn't belong to user [" + userName + "] and mealPlanId [" + mealPlanId + "]";
            throw new ObjectNotYoursException(msg, "MealPlan",mealPlanId, user.getEmail());
        }
        return mealPlanEntity;
    }


    public void addDishToMealPlan(String username, Long mealPlanId, Long dishId) {
        // get meal plan
        MealPlanEntity mealPlan = getMealPlanById(username, mealPlanId);

        // get dish
        DishEntity dish =  dishService.getDishForUserById(username, dishId);

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


    public boolean deleteMealPlan(String name, Long mealPlanId) throws ObjectNotYoursException, ObjectNotFoundException {
        MealPlanEntity toDelete = getMealPlanById(name, mealPlanId);

        if (!toDelete.getSlots().isEmpty()) {
            slotRepository.deleteAll(toDelete.getSlots());
            toDelete.setSlots(null);
        }
        mealPlanRepository.delete(toDelete);
        return true;

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

        return tagRepository.getIngredientTagsForDishes(dishIds);
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
        return tagService.getRatingUpdateInfoForDishIds(username, dishIds);
    }

    public List<TagEntity> getTagsForSlot(SlotEntity slot) {
        // MM implement this
        return null;
    }
}
