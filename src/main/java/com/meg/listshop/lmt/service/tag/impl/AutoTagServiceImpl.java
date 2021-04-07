package com.meg.listshop.lmt.service.tag.impl;

import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.lmt.data.entity.DishEntity;
import com.meg.listshop.lmt.data.entity.ShadowTags;
import com.meg.listshop.lmt.data.repository.ShadowTagRepository;
import com.meg.listshop.lmt.data.repository.TagRepository;
import com.meg.listshop.lmt.service.DishService;
import com.meg.listshop.lmt.service.tag.AutoTagProcessor;
import com.meg.listshop.lmt.service.tag.AutoTagService;
import com.meg.listshop.lmt.service.tag.AutoTagSubject;
import com.meg.listshop.lmt.service.tag.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by margaretmartin on 07/12/2017.
 */
@Service
public class AutoTagServiceImpl implements AutoTagService {

    TagRepository tagRepository;

    ShadowTagRepository shadowTagRepository;

    UserService userService;

    DishService dishService;

    TagService tagService;

    private final List<AutoTagProcessor> processorList;

    @Autowired
    public AutoTagServiceImpl(TagRepository tagRepository, ShadowTagRepository shadowTagRepository, UserService userService, DishService dishService, TagService tagService, List<AutoTagProcessor> processorList) {
        this.tagRepository = tagRepository;
        this.shadowTagRepository = shadowTagRepository;
        this.userService = userService;
        this.dishService = dishService;
        this.tagService = tagService;
        this.processorList = processorList;
    }

    @Override
    public void doAutoTag(DishEntity dishEntity, boolean overrideStatus) {
        if (dishEntity == null) {
            return;
        }
        // get user
        UserEntity user = userService.getUserById(dishEntity.getUserId());

        // pull items
        Set<Long> tagIdsForDish = tagRepository.getTagIdsForDish(dishEntity.getId());

        if (tagIdsForDish == null || tagIdsForDish.isEmpty()) {
            return;
        }

        // pull autotagHistory
        List<ShadowTags> shadowTags = shadowTagRepository.findShadowTagsByDishId(dishEntity.getId());

        // create AutoTagSubject
        AutoTagSubject subject = new AutoTagSubject(dishEntity, overrideStatus);
        subject.setShadowTags(shadowTags);
        subject.setTagIdsForDish(tagIdsForDish);

        // run dishEntity through taggers
        for (AutoTagProcessor tagProcessor : processorList) {
            subject = tagProcessor.autoTagSubject(subject);
        }

        // save processed flags - just setting this in the entity,
        // because the doAutoTag() method is (often) called from a save context
        Long processedStatusFlag = subject.getProcessedBySet().stream()
                .reduce(1L, (a, b) -> a * b);
        dishEntity.setAutoTagStatus(processedStatusFlag);
        dishService.save(dishEntity, false);
        // check for results
        if (subject.getTagsToAssign()== null || subject.getTagsToAssign().isEmpty()) {
            return;
        }

        addTagsToDish(user.getEmail(), subject);
        createShadowTagsForDish(subject);
    }

    @Override
    public List<DishEntity> getDishesToAutotag(int dishToAutotagCount) {

        Long maxProcessedStatusFlag = processorList.stream().map(processor -> processor.getProcessIdentifier())
                .reduce(1L, (a, b) -> a * b);

        return dishService.getDishesToAutotag(maxProcessedStatusFlag, dishToAutotagCount);
    }


    private void createShadowTagsForDish(AutoTagSubject subject) {
        Long dishId = subject.getDish().getId();
        List<ShadowTags> toInsert = new ArrayList<>();

        for (Long tagId : subject.getTagsToAssign()) {
            ShadowTags newShadow = new ShadowTags();
            newShadow.setDishId(dishId);
            newShadow.setTagId(tagId);
            toInsert.add(newShadow);
        }
        shadowTagRepository.saveAll(toInsert);
    }

    private void addTagsToDish(String userName, AutoTagSubject subject) {
        Long dishId = subject.getDish().getId();

        for (Long tagId : subject.getTagsToAssign()) {
            tagService.addTagToDish(userName, dishId, tagId);
        }


    }
}
