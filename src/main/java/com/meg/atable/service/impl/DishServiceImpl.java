package com.meg.atable.service.impl;

import com.meg.atable.api.DishNotFoundException;
import com.meg.atable.api.UnauthorizedAccessException;
import com.meg.atable.api.UserNotFoundException;
import com.meg.atable.api.model.TagType;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.data.repository.UserRepository;
import com.meg.atable.data.entity.DishEntity;
import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.data.repository.DishRepository;
import com.meg.atable.service.DishSearchCriteria;
import com.meg.atable.service.DishSearchService;
import com.meg.atable.service.DishService;
import com.meg.atable.service.tag.AutoTagService;
import com.meg.atable.service.tag.TagService;
import com.meg.atable.service.tag.TagStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
public class DishServiceImpl implements DishService {

    public static final Comparator<DishEntity> DISHNAME = (DishEntity o1, DishEntity o2) -> o1.getDishName().toLowerCase().compareTo(o2.getDishName().toLowerCase());
    @Autowired
    private DishRepository dishRepository;
    @Autowired
    private DishSearchService dishSearchService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AutoTagService autoTagService;
    @Autowired
    private TagService tagService;
    @Autowired
    private TagStructureService tagStructureService;

    @Override
    public Collection<DishEntity> getDishesForUserName(String userName) throws UserNotFoundException {
        UserAccountEntity user = userRepository.findByUsername(userName);
        if (user == null) {
            throw new UserNotFoundException(userName);
        }
        return dishRepository.findByUserId(user.getId());
    }

    @Override
    public Optional<DishEntity> getDishById(Long dishId) {
        return dishRepository.findById(dishId);
    }


    @Override
    public Optional<DishEntity> getDishForUserById(String username, Long dishId) {
        UserAccountEntity user = userRepository.findByUsername(username);
        Optional<DishEntity> dishOpt = dishRepository.findById(dishId);
        if (!dishOpt.isPresent()) {
            throw new DishNotFoundException(dishId);
        }
        DishEntity dish = dishOpt.get();
        if (!dish.getUserId().equals(user.getId())) {
            throw new UnauthorizedAccessException("Dish [" + dishId + "] doesn't belong to user [" + username + "].");
        }
        return Optional.of(dish);
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
    public List<DishEntity> save(List<DishEntity> dishes) {
        return dishRepository.saveAll(dishes);
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
        return new HashMap<Long, DishEntity>();
    }

    @Override
    public List<TagEntity> getDishesForTagChildren(Long tagId, String name) {
        UserAccountEntity user = userRepository.findByUsername(name);
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
        if (!dish.isPresent()) {
            return;
        }
        DishEntity dishEntity = dish.get();
        dishEntity.setLastAdded(new Date());
        dishRepository.save(dishEntity);
    }
}
