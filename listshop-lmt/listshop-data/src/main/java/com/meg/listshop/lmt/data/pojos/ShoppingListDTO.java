/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.data.pojos;

import com.meg.listshop.lmt.data.entity.ShoppingListEntity;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class ShoppingListDTO {

    private final Long listId;
    private final String name;
    private final Date createdOn;
    private final Date lastUpdate;
    private final Long userId;
    private final boolean isStarterList;
    private Long layoutId;
    private int itemCount;
    private List<CategoryDTO> categories;
    private List<SourceDTO> sources;
    private Map<Long, String> unitMapping;

    public ShoppingListDTO(Object listId,
                           String name,
                           Object createdOn,
                           Object lastUpdate,
                           Object userId,
                           Object isStarterList,
                           Object layoutId,
                           Object itemCount) {
        this.listId = listId instanceof Long ? (Long) listId : ((Number) listId).longValue();
        this.name = name;
        this.createdOn = castToDate(createdOn);
        this.lastUpdate = castToDate(lastUpdate);
        this.userId = userId instanceof Long ? (Long) userId : ((Number) userId).longValue();
        this.isStarterList = isStarterList instanceof Boolean ? (Boolean) isStarterList : ((Number) isStarterList).intValue() != 0;
        this.layoutId = convertToLong(layoutId);
        this.itemCount = itemCount instanceof Integer ? (Integer) itemCount : ((Number) itemCount).intValue();
    }

    private Long convertToLong(Object layoutId) {
        if (layoutId == null) {
            return null;
        }
        return layoutId instanceof Long ? (Long) layoutId : ((Number) layoutId).longValue();
    }

    private Date castToDate(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Date) {
            return (Date) object;
        }
        if (object instanceof java.time.Instant) {
            return Date.from((java.time.Instant) object);
        }
        if (object instanceof java.sql.Timestamp) {
            return new Date(((java.sql.Timestamp) object).getTime());
        }
        return null;
    }

    public ShoppingListDTO(ShoppingListEntity shoppingList, List<CategoryDTO> categories, List<SourceDTO> sources, Map<Long, String> unitMapping) {
        this.listId = shoppingList.getId();
        this.name = shoppingList.getName();
        this.createdOn = shoppingList.getCreatedOn();
        this.lastUpdate = shoppingList.getLastUpdate();
        this.userId = shoppingList.getUserId();
        this.isStarterList = shoppingList.getIsStarterList();
        this.unitMapping = unitMapping;
        this.layoutId  = shoppingList.getListLayoutId();

        if (categories != null) {
            itemCount = (int)categories.stream()
                    .filter(category -> category.getItems() != null)
                    .flatMap(category -> category.getItems().stream())
                    .filter(item -> item.getRemovedOn() == null && item.getCrossedOff() == null)
                    .count();
        }
        this.categories = categories;
        this.sources = sources;
    }

    public Long getListId() {
        return listId;
    }

    public String getName() {
        return name;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public Long getUserId() {
        return userId;
    }

    public boolean isStarterList() {
        return isStarterList;
    }

    public int getItemCount() {
        return itemCount;
    }

    public Long getLayoutId() {
        return layoutId;
    }

    public List<CategoryDTO> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryDTO> categories) {
        this.categories = categories;
    }

    public List<SourceDTO> getSources() {
        return sources;
    }

    public void setSources(List<SourceDTO> sources) {
        this.sources = sources;
    }

    public Map<Long, String> getUnitMapping() {
        return unitMapping;
    }

    public void setUnitMapping(Map<Long, String> unitMapping) {
        this.unitMapping = unitMapping;
    }

    public void setLayoutId(Long layoutId) {
        this.layoutId = layoutId;
    }

    @Override
    public String toString() {
        return "ShoppingListDTO{" +
                "listId=" + listId +
                ", name='" + name + '\'' +
                ", createdOn=" + createdOn +
                ", lastUpdate=" + lastUpdate +
                ", userId=" + userId +
                ", isStarterList=" + isStarterList +
                ", itemCount=" + itemCount +
                '}';
    }


}
