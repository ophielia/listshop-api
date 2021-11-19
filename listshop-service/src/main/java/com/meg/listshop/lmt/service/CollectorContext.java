package com.meg.listshop.lmt.service;

import com.meg.listshop.lmt.api.model.ContextType;
import com.meg.listshop.lmt.api.model.StatisticCountType;

/**
 * Created by margaretmartin on 02/11/2017.
 */
public class CollectorContext {

    private ContextType contextType;
    private Long listId;
    private Long dishId;
    private StatisticCountType statisticCountType = StatisticCountType.None;

    private boolean removeEntireItem;
    private boolean keepExistingCrossedOffStatus;
    private boolean copyCrossedOff;

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

    public StatisticCountType getStatisticCountType() {
        return statisticCountType;
    }

    public void setStatisticCountType(StatisticCountType statisticCountType) {
        this.statisticCountType = statisticCountType;
    }

    public void setKeepExistingCrossedOffStatus(boolean keepExistingCrossedOff) {
        this.keepExistingCrossedOffStatus = keepExistingCrossedOff;
    }

    public boolean isKeepExistingCrossedOffStatus() {
        return keepExistingCrossedOffStatus;
    }

    public boolean isCopyCrossedOff() {
        return copyCrossedOff;
    }

    public void setDoCopyCrossedOff(boolean copyCrossedOff) {
        this.copyCrossedOff = copyCrossedOff;
    }
}
