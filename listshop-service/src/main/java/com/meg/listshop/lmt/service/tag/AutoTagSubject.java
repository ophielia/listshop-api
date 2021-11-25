package com.meg.listshop.lmt.service.tag;

import com.meg.listshop.lmt.data.entity.DishEntity;
import com.meg.listshop.lmt.data.entity.ShadowTags;

import java.util.*;

/**
 * Created by margaretmartin on 08/12/2017.
 */
public class AutoTagSubject {
    private final boolean overrideFlag;
    private final List<Long> tagsToAssign;
    private final DishEntity dish;
    private List<ShadowTags> shadowTags;
    private Set<Long> processedBySet = new HashSet<>();
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
        processedBySet.add(processIdentifier);
    }

    public boolean hasBeenProcessedBy(Long processIdentifier) {
        Long dishAutotagStatus = dish.getAutoTagStatus() == null ? 1 : dish.getAutoTagStatus();
        return dishAutotagStatus % processIdentifier == 0;
    }

    public Set<Long> getTagIdsForDish() {
        return tagIdsForDish;
    }

    public void setTagIdsForDish(Set<Long> tagIdsForDish) {
        this.tagIdsForDish = tagIdsForDish;
    }

    public Set<Long> getProcessedBySet() {
        return processedBySet;
    }

    public void setProcessedBySet(Set<Long> processedBySet) {
        this.processedBySet = processedBySet;
    }
}
