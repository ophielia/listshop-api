package com.meg.listshop.lmt.list.state;

import com.meg.listshop.conversion.data.pojo.DomainType;
import com.meg.listshop.lmt.conversion.BasicAmount;
import com.meg.listshop.lmt.data.entity.DishItemEntity;
import com.meg.listshop.lmt.data.entity.ListItemEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;

public class ItemStateContext {
    private ListItemEntity targetItem;
    private Long targetListId;
    private TagEntity tag;
    private Long dishId;
    private Long listId;
    private DishItemEntity dishItem;
    private ListItemEntity listItem;
    private boolean isCrossedOff;
    private BasicAmount tagAmount;
    private DomainType userDomain;

    public ItemStateContext(ListItemEntity targetItem, Long targetListId) {
        this.targetItem = targetItem;
        this.targetListId = targetListId;
    }

    public ListItemEntity getTargetItem() {
        return targetItem;
    }

    public void setTargetItem(ListItemEntity targetItem) {
        this.targetItem = targetItem;
    }

    public TagEntity getTag() {
        return tag;
    }

    public void setTag(TagEntity tag) {
        this.tag = tag;
    }

    public Long getDishId() {
        return dishId;
    }

    public Long getListId() {
        return listId;
    }

    public void setListId(Long listId) {
        this.listId = listId;
    }

    public DishItemEntity getDishItem() {
        return dishItem;
    }

    public void setDishItem(DishItemEntity dishItem) {
        this.dishItem = dishItem;
        this.dishId = dishItem.getDish().getId();
    }

    public ListItemEntity getListItem() {
        return listItem;
    }

    public void setListItem(ListItemEntity listItem) {
        this.listItem = listItem;
        this.listId = listItem.getListId();
    }

    public boolean isCrossedOff() {
        return isCrossedOff;
    }

    public void setCrossedOff(boolean crossedOff) {
        isCrossedOff = crossedOff;
    }

    public Long getTargetListId() {
        return targetListId;
    }

    public TagEntity getTargetTag() {
        if (tag != null) {
            return tag;
        } else if (dishItem != null) {
            return dishItem.getTag();
        } else if (listItem != null) {
            return listItem.getTag();
        }
        return null;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    public BasicAmount getTagAmount() {
        return tagAmount;
    }

    public void setTagAmount(BasicAmount basicAmount) {
        this.tagAmount = basicAmount;
    }

    public void setUserDomain(DomainType userDomainType) {
            this.userDomain = userDomainType;
    }

    public DomainType getUserDomain() {
        return userDomain;
    }
}
