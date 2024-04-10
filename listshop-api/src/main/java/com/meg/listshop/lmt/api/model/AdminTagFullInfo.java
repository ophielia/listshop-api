package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminTagFullInfo {

    @JsonProperty("tag_id")
    private String tagId;

    @JsonProperty("user_id")
    private String userId;
    private String name;

    private String description;

    @JsonProperty("tag_type")
    private String tagType;

    @JsonProperty("is_group")
    private boolean isGroup;
    private Double power;

    @JsonProperty("parent_id")
    private String parentId;

    @JsonProperty("to_delete")
    private Boolean toDelete;

    @JsonProperty("status_display")
    private String statusDisplay;

    @JsonProperty("parent_name")
    private String parentName;
    @JsonProperty("is_liquid")
    private Boolean isLiquid;
    @JsonProperty("food_id")
    private String foodId;
    @JsonProperty("conversion_id")
    private String conversionId;
    @JsonProperty("food_name")
    private String foodName;
    @JsonProperty("conversion_grid")
    private ConversionGrid conversionGrid;

    public AdminTagFullInfo() {
    }




    public String getTag_id() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTagType() {
        return tagType;
    }

    public void setTagType(String tagType) {
        this.tagType = tagType;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public Double getPower() {
        return power;
    }

    public void setPower(Double power) {
        this.power = power;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Boolean getToDelete() {
        return toDelete;
    }

    public void setToDelete(Boolean toDelete) {
        this.toDelete = toDelete;
    }

    public String getStatusDisplay() {
        return statusDisplay;
    }

    public void setStatusDisplay(String statusDisplay) {
        this.statusDisplay = statusDisplay;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public Boolean getLiquid() {
        return isLiquid;
    }

    public void setLiquid(Boolean liquid) {
        isLiquid = liquid;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getConversionId() {
        return conversionId;
    }

    public void setConversionId(String conversionId) {
        this.conversionId = conversionId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public void setConversionGrid(ConversionGrid conversionGrid) {
        this.conversionGrid = conversionGrid;
    }

    public ConversionGrid getConversionGrid() {
        return conversionGrid;
    }

    @Override
    public String toString() {
        return "AdminTagFullInfo{" +
                "tag_id='" + tagId + '\'' +
                ", name='" + name + '\'' +
                ", userId='" + userId + '\'' +
                ", parentId='" + parentId + '\'' +
                ", parentName='" + parentName + '\'' +
                ", description='" + description + '\'' +
                ", tagType='" + tagType + '\'' +
                ", isGroup=" + isGroup +
                ", power=" + power +
                ", toDelete=" + toDelete +
                ", statusDisplay='" + statusDisplay + '\'' +
                ", isLiquid=" + isLiquid +
                ", foodId='" + foodId + '\'' +
                ", foodName='" + foodName + '\'' +
                ", conversionGrid='" + conversionGrid + '\'' +
                '}';
    }

}