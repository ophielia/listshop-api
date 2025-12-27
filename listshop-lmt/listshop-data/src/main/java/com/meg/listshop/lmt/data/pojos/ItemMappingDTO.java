package com.meg.listshop.lmt.data.pojos;

import com.meg.listshop.lmt.api.model.ListItemSource;
import com.meg.listshop.lmt.api.model.ShoppingListItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ItemMappingDTO {

    private Long itemId;
    private Date addedOn;
    private Date removedOn;
    private Date crossedOffOn;
    private Date updatedOn;

    private Long tagId;

    private String tagName;

    private String tagType;

    private int usedCount;

    private String rawDishSources;

    private String rawListSources;

    private Long categoryId;

    private String categoryName;

    private int displayOrder;

    private Long userCategoryId;

    private String userCategoryName;
    private int userDisplayOrder;

    private List<ListItemSource> details = new ArrayList<>();

    public ItemMappingDTO(Long itemId, Date addedOn, Date removedOn, Date crossedOffOn, Date updatedOn, Long tagId, String tagName, String tagType, int usedCount, String rawDishSources, String rawListSources, Long categoryId, String categoryName, int displayOrder, Long userCategoryId, String userCategoryName, int userDisplayOrder) {
        this.itemId = itemId;
        this.addedOn = addedOn;
        this.removedOn = removedOn;
        this.crossedOffOn = crossedOffOn;
        this.updatedOn = updatedOn;
        this.tagId = tagId;
        this.tagName = tagName;
        this.usedCount = usedCount;
        this.rawDishSources = rawDishSources;
        this.rawListSources = rawListSources;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.displayOrder = displayOrder;
        this.userCategoryId = userCategoryId;
        this.userDisplayOrder = userDisplayOrder;
        this.userCategoryName = userCategoryName;
        this.tagType = tagType;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Date getAddedOn() {
        return addedOn;
    }

    public void setAddedOn(Date addedOn) {
        this.addedOn = addedOn;
    }

    public Date getRemovedOn() {
        return removedOn;
    }

    public void setRemovedOn(Date removedOn) {
        this.removedOn = removedOn;
    }

    public Date getCrossedOffOn() {
        return crossedOffOn;
    }

    public void setCrossedOffOn(Date crossedOffOn) {
        this.crossedOffOn = crossedOffOn;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public int getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(int usedCount) {
        this.usedCount = usedCount;
    }

    public String getRawDishSources() {
        return rawDishSources;
    }

    public void setRawDishSources(String rawDishSources) {
        this.rawDishSources = rawDishSources;
    }

    public String getRawListSources() {
        return rawListSources;
    }

    public void setRawListSources(String rawListSources) {
        this.rawListSources = rawListSources;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        if (userCategoryName != null) {
            return userCategoryName;
        }
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Long getUserCategoryId() {
        return userCategoryId;
    }

    public void setUserCategoryId(Long userCategoryId) {
        this.userCategoryId = userCategoryId;
    }

    public int getUserDisplayOrder() {
        return userDisplayOrder;
    }

    public void setUserDisplayOrder(int userDisplayOrder) {
        this.userDisplayOrder = userDisplayOrder;
    }

    public String getUserCategoryName() {
        return userCategoryName;
    }

    public void setUserCategoryName(String userCategoryName) {
        this.userCategoryName = userCategoryName;
    }

    public String getTagType() {
        return tagType;
    }

    public void setTagType(String tagType) {
        this.tagType = tagType;
    }

    public List<ListItemSource> getDetails() {
        return details;
    }

    public void setDetails(List<ListItemSource> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "ItemMappingDTO{" +
                "itemId=" + itemId +
                ", addedOn=" + addedOn +
                ", removedOn=" + removedOn +
                ", crossedOffOn=" + crossedOffOn +
                ", updatedOn=" + updatedOn +
                ", tagId=" + tagId +
                ", tagName='" + tagName + '\'' +
                ", usedCount=" + usedCount +
                ", rawDishSources='" + rawDishSources + '\'' +
                ", rawListSources='" + rawListSources + '\'' +
                ", categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", displayOrder=" + displayOrder +
                ", userCategoryId=" + userCategoryId +
                ", userCategoryName='" + userCategoryName + '\'' +
                ", userDisplayOrder=" + userDisplayOrder +
                ", tagType=" + tagType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemMappingDTO that = (ItemMappingDTO) o;
        return itemId.equals(that.itemId) && categoryId.equals(that.categoryId) && Objects.equals(userCategoryId, that.userCategoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, categoryId, userCategoryId);
    }

    public ShoppingListItem mapToShoppingListItem() {
        return new ShoppingListItem(itemId)
                .addedOn(addedOn)
                .removed(removedOn)
                .updated(updatedOn)
                .crossedOff(crossedOffOn)
                .sources(details)
                .tagId(String.valueOf(tagId))
                .tagName(tagName)
                .tagType(tagType)
                .usedCount(calculateCount())
                .rawListSources(rawListSources)
                .rawDishSources(rawDishSources);

    }

    private Integer calculateCount() {
        if (details == null || details.isEmpty()) return 0;
        return details.stream().map(ListItemSource::getCount).reduce(0, Integer::sum);
    }


}
