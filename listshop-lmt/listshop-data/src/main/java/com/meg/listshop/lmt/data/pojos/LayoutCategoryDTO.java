package com.meg.listshop.lmt.data.pojos;

import com.meg.listshop.lmt.data.entity.ListLayoutCategoryEntity;

public class LayoutCategoryDTO {

    private String categoryId;

    private String categoryName;

    public LayoutCategoryDTO() {
    }


    public LayoutCategoryDTO(ListLayoutCategoryEntity listLayoutCategoryEntity) {
        this.categoryId = listLayoutCategoryEntity.getId() + "";
        this.categoryName = listLayoutCategoryEntity.getName();
    }


    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return "LayoutCategoryDTO{" +
                ", categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}
