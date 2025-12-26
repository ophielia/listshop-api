package com.meg.listshop.lmt.dish;

import com.meg.listshop.lmt.api.model.DishSortDirection;
import com.meg.listshop.lmt.api.model.DishSortKey;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by margaretmartin on 26/11/2017.
 */
public class DishSearchCriteria {
    private final Long userId;
    private List<Long> includedTagIds;
    private List<Long> excludedTagIds;
    private DishSortKey sortKey;
    private DishSortDirection sortDirection;
    private String nameFragment;

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

    public DishSortKey getSortKey() {
        return sortKey;
    }

    public void setSortKey(DishSortKey sortKey) {
        this.sortKey = sortKey;
    }

    public DishSortDirection getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(DishSortDirection sortDirection) {
        this.sortDirection = sortDirection;
    }

    public String getNameFragment() {
        return nameFragment;
    }

    public void setNameFragment(String nameFragment) {
        this.nameFragment = nameFragment;
    }

    @Override
    public String toString() {
        return "DishSearchCriteria{" +
                "userId=" + userId +
                ", includedTagIds=" + includedTagIds +
                ", excludedTagIds=" + excludedTagIds +
                ", sortKey=" + sortKey +
                ", sortDirection=" + sortDirection +
                ", nameFragment=" + nameFragment +
                '}';
    }

    public boolean hasSorting() {
        return sortKey != null || sortDirection != null;
    }
}
