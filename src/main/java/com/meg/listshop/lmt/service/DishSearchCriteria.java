package com.meg.listshop.lmt.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by margaretmartin on 26/11/2017.
 */
public class DishSearchCriteria {
    private final Long userId;
    private List<Long> includedTagIds;
    private List<Long> excludedTagIds;

    public DishSearchCriteria(Long userId) {
        this.userId = userId;
        this.includedTagIds = new ArrayList<Long>();
        this.excludedTagIds = new ArrayList<Long>();
    }


    public Long getUserId() {
        return userId;
    }

    public List<Long> getIncludedTagIds() {
        return includedTagIds;
    }

    public void setIncludedTagIds(List<Long> includedTagIds) {
        this.includedTagIds = includedTagIds;
    }


    public List<Long> getExcludedTags() {
                return this.excludedTagIds;
    }

    public void setExcludedTagIds(List<Long> excludedTagIds) {
        this.excludedTagIds = excludedTagIds;
    }
}
