package com.meg.listshop.lmt.conversion;

import com.meg.listshop.common.CommonUtils;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.lmt.data.entity.DishItemEntity;
import com.meg.listshop.lmt.data.entity.ListItemDetailEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;

public class EntityConvertibleAmount implements ConvertibleAmount {

    private UnitEntity unitEntity;
    private double quantity;
    private String marker;
    private Boolean isLiquid;
    private String unitSize;
    private Long conversionId;

    public EntityConvertibleAmount(ListItemDetailEntity listItemDetail, UnitEntity unit, TagEntity tagEntity) {
        this.unitEntity = unit;
        this.quantity = listItemDetail.getQuantity();
        this.marker = listItemDetail.getMarker();
        this.isLiquid =  CommonUtils.elvis(tagEntity.getIsLiquid(),false);
        this.unitSize = listItemDetail.getUnitSize();
        this.conversionId = tagEntity.getConversionId();
    }

    public EntityConvertibleAmount(DishItemEntity dishItem, UnitEntity unit, TagEntity tagEntity) {
        this.unitEntity = unit;
        this.quantity = dishItem.getQuantity();
        this.marker = dishItem.getMarker();
        this.isLiquid = CommonUtils.elvis(tagEntity.getIsLiquid(),false);
        this.unitSize = dishItem.getUnitSize();
        this.conversionId = tagEntity.getConversionId();
    }

    public EntityConvertibleAmount(BasicAmount tagAmount, UnitEntity unit, TagEntity tagEntity) {
        this.unitEntity = unit;
        this.quantity = tagAmount.getQuantity();
        this.marker = tagAmount.getMarker();
        this.isLiquid =  CommonUtils.elvis(tagEntity.getIsLiquid(),false);
        this.unitSize = tagAmount.getUnitSize();
        this.conversionId = tagEntity.getConversionId();
    }

    @Override
    public double getQuantity() {
        return quantity;
    }

    @Override
    public UnitEntity getUnit() {
        return unitEntity;
    }

    @Override
    public Long getConversionId() {
        return conversionId;
    }

    @Override
    public String getMarker() {
        return marker;
    }

    @Override
    public Boolean getIsLiquid() {
        return isLiquid;
    }

    @Override
    public String getUnitSize() {
        return unitSize;
    }


    @Override
    public Boolean getUserSize() {
        return false;
    }

    @Override
    public double getQuantityRoundedUp() {
        return 0;
    }
}
