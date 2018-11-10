package com.meg.atable.lmt.service.tag;

import com.meg.atable.lmt.data.entity.DishEntity;
import com.meg.atable.lmt.data.entity.ShadowTags;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by margaretmartin on 08/12/2017.
 */
public class AutoTagSubject {
    private final boolean overrideFlag;
    private List<Long> tagsToAssign;
    private DishEntity dish;
    private List<ShadowTags> shadowTags;
    private List<Long> processedByList = new ArrayList<>();
    private Set<Long> tagIdsForDish;

    public AutoTagSubject(DishEntity dishEntity, boolean overrideStatus) {
        this.dish = dishEntity;
        this.overrideFlag = overrideStatus;
        this.tagsToAssign = new ArrayList<>();
    }

    public boolean isOverrideFlag() {
        return overrideFlag;
    }


    public void setShadowTags(List<ShadowTags> shadowTags) {
        this.shadowTags = shadowTags;
    }

    public List<ShadowTags> getShadowTags() {
        return shadowTags;
    }


    public List<Long> getTagsToAssign() {
        return tagsToAssign;
    }

    public DishEntity getDish() {
        return dish;
    }

    public void addToTagIdsToAssign(Long tagId) {
        // before adding, ensure that this tag doesn't exist as shadow
        Optional<ShadowTags> sTag = shadowTags.stream().filter(s -> s.getTagId().equals(tagId)).findFirst();
        if (sTag.isPresent()) {
            return;
        }
        this.tagsToAssign.add(tagId);
    }

    public void addProcessedBy(Long processIdentifier) {
        processedByList.add(processIdentifier);
    }

    public boolean hasBeenProcessedBy(Long processIdentifier) {
        Long dishAutotagStatus = dish.getAutoTagStatus();
        return dishAutotagStatus % processIdentifier == 0;
    }

    public Set<Long> getTagIdsForDish() {
        return tagIdsForDish;
    }

    public void setTagIdsForDish(Set<Long> tagIdsForDish) {
        this.tagIdsForDish = tagIdsForDish;
    }
}
