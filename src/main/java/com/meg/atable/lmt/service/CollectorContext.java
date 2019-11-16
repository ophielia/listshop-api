package com.meg.atable.lmt.service;

import com.meg.atable.lmt.api.model.ContextType;

/**
 * Created by margaretmartin on 02/11/2017.
 */
public class CollectorContext {

    private ContextType contextType;
    private Long listId;
    private Long dishId;
    private boolean incrementStatistics;
    private boolean removeEntireItem;


    public CollectorContext(ContextType contextType, Long listId, Long dishId, boolean incrementStatistics, boolean removeEntireItem) {
        this.contextType = contextType;
        this.listId = listId;
        this.dishId = dishId;
        this.incrementStatistics = incrementStatistics;

    }

    public CollectorContext() {
    }

    public ContextType getContextType() {
        return contextType;
    }

    public void setContextType(ContextType contextType) {
        this.contextType = contextType;
    }

    public Long getListId() {
        return listId;
    }

    public void setListId(Long listId) {
        this.listId = listId;
    }

    public Long getDishId() {
        return dishId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    public boolean isIncrementStatistics() {
        return incrementStatistics;
    }

    public void setIncrementStatistics(boolean incrementStatistics) {
        this.incrementStatistics = incrementStatistics;
    }

    public boolean isRemoveEntireItem() {
        return removeEntireItem;
    }

    public void setRemoveEntireItem(boolean removeEntireItem) {
        this.removeEntireItem = removeEntireItem;
    }

    public boolean hasListId() {
        return this.listId != null;
    }

    public boolean hasDishId() {
        return this.dishId != null;
    }

    public boolean eligibleForListSourceChange() {
        return contextType == ContextType.List || contextType == ContextType.Merge;
    }

    public boolean eligibleForDishSourceChange() {
        return contextType == ContextType.Dish || contextType == ContextType.Merge;
    }


}
