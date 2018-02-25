package com.meg.atable.service.tag;

import com.meg.atable.data.entity.DishEntity;
import com.meg.atable.data.entity.ShadowTags;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        this.tagsToAssign = new ArrayList<Long>();
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
        tagsToAssign = tagsToAssign.stream()
                .filter(t -> !shadowTags.contains(t))
                .collect(Collectors.toList());

        return tagsToAssign;
    }
 public DishEntity getDish() {
        return dish;
    }

    public void addToTagIdsToAssign(Long tagId) {
        this.tagsToAssign.add(tagId);
    }

    public void addProcessedBy(Long processIdentifier) {
        processedByList.add(processIdentifier);
    }

    public boolean hasBeenProcessedBy(Long processIdentifier) {
        Long dishAutotagStatus = dish.getAutoTagStatus();
        return dishAutotagStatus % processIdentifier ==0;
    }

    public Set<Long> getTagIdsForDish() {
        return tagIdsForDish;
    }

    public void setTagIdsForDish(Set<Long> tagIdsForDish) {
        this.tagIdsForDish = tagIdsForDish;
    }
}
