package com.meg.atable.service.impl;

import com.meg.atable.data.entity.DishEntity;
import com.meg.atable.data.entity.ShadowTags;
import com.meg.atable.data.repository.ShadowTagRepository;
import com.meg.atable.data.repository.TagRepository;
import com.meg.atable.service.AutoTagProcessor;
import com.meg.atable.service.AutoTagService;
import com.meg.atable.service.AutoTagSubject;
import com.meg.atable.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by margaretmartin on 07/12/2017.
 */
@Service
public class AutoTagServiceImpl implements AutoTagService {

    @Autowired
    TagRepository tagRepository;

    @Autowired
    ShadowTagRepository shadowTagRepository;

    @Autowired
    TagService tagService;

    @Autowired
    private List<AutoTagProcessor> processorList;

    @PostConstruct
    private void setup() {

    }

    @Override
    public void doAutoTag(DishEntity dishEntity, boolean overrideStatus) {
        if (dishEntity == null) {
            return;
        }
        // pull tagswithflags
        List<Integer> tagFlags = tagRepository.getAutoTagsForDish(dishEntity.getId());

        // pull autotagHistory
        List<ShadowTags> shadowTags = shadowTagRepository.findShadowTagsByDishId(dishEntity.getId());

        // create AutoTagSubject
        AutoTagSubject subject = new AutoTagSubject(dishEntity, overrideStatus);
        subject.setTagFlags(tagFlags);
        subject.setShadowTags(shadowTags);

        // run dishEntity through taggers
        for (AutoTagProcessor tagProcessor : processorList) {
            subject = tagProcessor.autoTagSubject(subject);
        }

        // save processed flags // MM TODO
        // check for results
        if (subject.getTagsToAssign()== null || subject.getTagsToAssign().isEmpty()) {
            return;
        }
        addTagsToDish(subject);
        createShadowTagsForDish(subject);
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
        shadowTagRepository.save(toInsert);
    }

    private void addTagsToDish(AutoTagSubject subject) {
        Long dishId = subject.getDish().getId();

        for (Long tagId : subject.getTagsToAssign()) {
            tagService.addTagToDish(dishId, tagId);
        }

    }
}
