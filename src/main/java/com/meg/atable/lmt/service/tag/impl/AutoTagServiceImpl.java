package com.meg.atable.lmt.service.tag.impl;

import com.meg.atable.lmt.data.entity.DishEntity;
import com.meg.atable.lmt.data.entity.ShadowTags;
import com.meg.atable.lmt.data.repository.ShadowTagRepository;
import com.meg.atable.lmt.data.repository.TagRepository;
import com.meg.atable.lmt.service.tag.AutoTagProcessor;
import com.meg.atable.lmt.service.tag.AutoTagService;
import com.meg.atable.lmt.service.tag.AutoTagSubject;
import com.meg.atable.lmt.service.tag.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        Set<Long> tagIdsForDish = tagRepository.getTagIdsForDish(dishEntity.getId());

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
        shadowTagRepository.saveAll(toInsert);
    }

    private void addTagsToDish(AutoTagSubject subject) {
        Long dishId = subject.getDish().getId();

        for (Long tagId : subject.getTagsToAssign()) {
            tagService.addTagToDish(dishId, tagId);
        }

    }
}
