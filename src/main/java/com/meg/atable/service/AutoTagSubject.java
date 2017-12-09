package com.meg.atable.service;

import com.meg.atable.data.entity.DishEntity;
import com.meg.atable.data.entity.ShadowTags;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 08/12/2017.
 */
public class AutoTagSubject {
    private final boolean overrideFlag;
    private List<Long> tagsToAssign;
    private DishEntity dish;
    private List<ShadowTags> shadowTags;
    private List<Integer> tagFlags;
    private List<Long> processedByList = new ArrayList<>();

    public AutoTagSubject(DishEntity dishEntity, boolean overrideStatus) {
        this.dish = dishEntity;
        this.overrideFlag = overrideStatus;
        this.tagsToAssign = new ArrayList<Long>();
    }

    public boolean isOverrideFlag() {
        return overrideFlag;
    }

    public List<Integer> getTagFlags() {
        return tagFlags;
    }

    public void setTagFlags(List<Integer> tagFlags) {
        this.tagFlags = tagFlags;
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
        // check if tagid exists in shadow tag
        List<ShadowTags> match = shadowTags.stream()
                .filter(t -> tagId.longValue() == t.getTagId().longValue())
                .collect(Collectors.toList());
        if (match.isEmpty()) {
            this.tagsToAssign.add(tagId);
        }
    }

    public void addProcessedBy(Long processIdentifier) {
        processedByList.add(processIdentifier);
    }

    public boolean hasBeenProcessedBy(Long processIdentifier) {
        Long dishAutotagStatus = dish.getAutoTagStatus();
        return dishAutotagStatus % processIdentifier ==0;
    }
}
