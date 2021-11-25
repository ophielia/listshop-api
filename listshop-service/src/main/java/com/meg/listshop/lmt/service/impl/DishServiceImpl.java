package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.data.repository.UserRepository;
import com.meg.listshop.common.StringTools;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.api.exception.ObjectNotYoursException;
import com.meg.listshop.lmt.api.exception.UserNotFoundException;
import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.data.entity.DishEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.repository.DishRepository;
import com.meg.listshop.lmt.service.DishSearchCriteria;
import com.meg.listshop.lmt.service.DishSearchService;
import com.meg.listshop.lmt.service.DishService;
import com.meg.listshop.lmt.service.tag.AutoTagService;
import com.meg.listshop.lmt.service.tag.TagService;
import com.meg.listshop.lmt.service.tag.TagStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
public class DishServiceImpl implements DishService {

    public static final Comparator<DishEntity> DISHNAME = (DishEntity o1, DishEntity o2) -> o1.getDishName().toLowerCase().compareTo(o2.getDishName().toLowerCase());

    private final DishRepository dishRepository;

    private final DishSearchService dishSearchService;

    private final UserRepository userRepository;

    private final AutoTagService autoTagService;

    private final TagService tagService;

    private final TagStructureService tagStructureService;

    @Autowired
    public DishServiceImpl(
            DishRepository dishRepository,
            DishSearchService dishSearchService,
            UserRepository userRepository,
            @Lazy AutoTagService autoTagService,
            TagService tagService,
            TagStructureService tagStructureService
    ) {
        this.dishRepository = dishRepository;
        this.dishSearchService = dishSearchService;
        this.userRepository = userRepository;
        this.autoTagService = autoTagService;
        this.tagService = tagService;
        this.tagStructureService = tagStructureService;
    }

    @Override
    public List<DishEntity> getDishesForUserName(String userName) {
        UserEntity user = userRepository.findByUsername(userName);
        if (user == null) {
            throw new UserNotFoundException(userName);
        }
        return dishRepository.findByUserId(user.getId());
    }

    private Optional<DishEntity> getDishById(Long dishId) {
        return dishRepository.findById(dishId);
    }


    @Override
    public DishEntity getDishForUserById(String username, Long dishId) {
        if (dishId == null) {
            final String msg = "Null dishId passed as argument [" + username + "].";
            throw new ObjectNotFoundException(msg, null, "Dish");
        }

        UserEntity user = userRepository.findByUsername(username);

        Optional<DishEntity> dishOpt = dishRepository.findById(dishId);
        if (dishOpt.isEmpty()) {
            final String msg = "No dish found by id for user [" + username + "] and dishId [" + dishId + "]";
            throw new ObjectNotFoundException(msg, dishId, "Dish");
        }
        DishEntity dish = dishOpt.get();
        if (!dish.getUserId().equals(user.getId())) {
            final String msg = "Dish found for dishId [" + dishId + "], but doesn't belong to user [" + username + "].";
            throw new ObjectNotYoursException(msg, "Dish", dishId, user.getEmail());
        }
        return dish;
    }

    @Override
    public DishEntity save(DishEntity dish, boolean doAutotag) {
        // autotag dish
        if (doAutotag) {
            autoTagService.doAutoTag(dish, true);
        }
        return dishRepository.save(dish);
    }

    @Override
    public DishEntity create(DishEntity createDish) {
        // check name before saving
        String name = ensureDishNameIsUnique(createDish.getUserId(), createDish.getDishName());
        createDish.setDishName(name);

        // copy creation fields into new DishEntity, since createDish came straight
        // from user input and may be tainted
        DishEntity newDish = new DishEntity(createDish.getUserId(),
                createDish.getDishName(),
                createDish.getDescription());
        newDish.setReference(createDish.getReference());

        return dishRepository.save(newDish);
    }

    private String ensureDishNameIsUnique(Long userId, String dishName) {
        // does this name already exist for the user?
        List<DishEntity> existing = dishRepository.findByUserIdAndDishName(userId, dishName.toLowerCase());

        if (existing.isEmpty()) {
            return dishName;
        }

        // if so, get all lists with names starting with the listName
        List<DishEntity> similar = dishRepository.findByUserIdAndDishNameLike(userId,
                dishName.toLowerCase() + "%");
        List<String> similarNames = similar.stream()
                .map(list -> list.getDishName().trim().toLowerCase()).collect(Collectors.toList());
        // use handy StringTools method to get first unique name

        return StringTools.makeUniqueName(dishName, similarNames);
    }

    @Override
    public List<DishEntity> save(List<DishEntity> dishes) {
        return dishRepository.saveAll(dishes);
    }

    @Override
    public List<DishEntity> getDishes(String username, List<Long> dishIds) {
        UserEntity user = userRepository.findByUsername(username);

        return dishRepository.findByDishIdsForUser(user.getId(), dishIds);
    }

    @Override
    public List<DishEntity> getDishes(List<Long> dishIds) {
        return dishRepository.findAllById(dishIds);
    }

    @Override
    public Map<Long, DishEntity> getDictionaryForIdList(List<Long> dishIds) {
        List<DishEntity> tags = dishRepository.findAllById(dishIds);
        if (!tags.isEmpty()) {
            return tags.stream().collect(Collectors.toMap(DishEntity::getId,
                    c -> c));

        }
        return new HashMap<>();
    }

    @Override
    public List<TagEntity> getDishesForTagChildren(Long tagId, String name) {
        UserEntity user = userRepository.findByUsername(name);
        TagEntity tag = tagService.getTagById(tagId);

        if (!TagType.Rating.equals(tag.getTagType())) {
            return new ArrayList<>();
        }

        List<TagEntity> childrenTags = tagStructureService.getChildren(tag);
        List<Long> allChildIds = new ArrayList<>();
        for (TagEntity childTag : childrenTags) {
            DishSearchCriteria criteria = new DishSearchCriteria(user.getId());
            criteria.setIncludedTagIds(Collections.singletonList(childTag.getId()));
            List<DishEntity> dishes = dishSearchService.findDishes(criteria);
            Collections.sort(dishes, DISHNAME);
            childTag.setDishes(dishes);
            allChildIds.add(childTag.getId());
        }
        // now, see if there are any unassigned dishes
        DishSearchCriteria criteria = new DishSearchCriteria(user.getId());
        criteria.setExcludedTagIds(allChildIds);
        List<DishEntity> unassigned = dishSearchService.findDishes(criteria);
        if (unassigned != null && !unassigned.isEmpty()) {
            // make a "dummy" non categorized tag
            TagEntity noncattag = new TagEntity();
            noncattag.setName("NonCategorized");
            noncattag.setPower(0D);
            noncattag.setTagType(TagType.Rating);
            noncattag.setDishes(unassigned);
            childrenTags.add(noncattag);
        }

        childrenTags.sort(Comparator.comparing(TagEntity::getPower));
        return childrenTags;
    }

    @Override
    public void updateLastAddedForDish(Long dishId) {
        Optional<DishEntity> dish = getDishById(dishId);

        dish.map(d -> {
            d.setLastAdded(new Date());
            dishRepository.save(d);
            return d;
        });
        return;
    }

    @Override
    public List<DishEntity> getDishesToAutotag(Long statusFlag, int dishLimit) {
        Pageable limit = PageRequest.of(0, dishLimit);
        return dishRepository.findDishesToAutotag(statusFlag, limit);
    }
}
