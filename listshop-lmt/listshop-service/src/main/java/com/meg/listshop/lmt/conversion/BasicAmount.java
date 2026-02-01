package com.meg.listshop.lmt.conversion;

import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.lmt.data.entity.TagEntity;

public class BasicAmount implements ConvertibleAmount {

    private UnitEntity unitEntity;
    private Long unitId;
    private double quantity;
    private String marker;
    private Boolean isLiquid;
    private String unitSize;
    private Long conversionId;

    public BasicAmount(double quantity, String marker, String unitSize, UnitEntity unit, TagEntity tagEntity) {
        this.unitEntity = unit;
        this.unitId = unit.getId();
        this.quantity = quantity;
        this.marker = marker;
        this.isLiquid = tagEntity.getIsLiquid();
        this.unitSize = unitSize;
        this.conversionId = tagEntity.getConversionId();
    }

    public BasicAmount(double quantity, String marker, String unitSize, Long unitId, TagEntity tagEntity) {
        this.unitId = unitId;
        this.quantity = quantity;
        this.marker = marker;
        this.isLiquid = tagEntity.getIsLiquid();
        this.unitSize = unitSize;
        this.conversionId = tagEntity.getConversionId();
    }

    public UnitEntity getUnit() {
        return unitEntity;
    }

    public Long getUnitId() {
        return unitId;
    }

    @Override
    public double getQuantity() {
        return quantity;
    }


    @Override
    public String getMarker() {
        return marker;
    }

    @Override
    public Boolean getIsLiquid() {
        return false;
    }


    public Boolean getLiquid() {
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


    @Override
    public Long getConversionId() {
        return conversionId;
    }


}
