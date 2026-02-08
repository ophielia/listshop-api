package com.meg.listshop.lmt.conversion;

import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.lmt.data.entity.ListItemDetailEntity;

import java.util.ArrayList;
import java.util.List;

public class SummaryConvertibleAmount implements ConvertibleAmount {

    private UnitEntity unitEntity;
    private double quantity;
    private String marker;
    private Boolean isLiquid;
    private String unitSize;

    private List<ListItemDetailEntity> details = new ArrayList<>();

    public SummaryConvertibleAmount() {
    }

    public SummaryConvertibleAmount(UnitEntity unitEntity, String unitSize) {
        this.unitSize = unitSize;
        this.unitEntity = unitEntity;
        this.isLiquid = unitEntity.isLiquid();
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
        return 0L;
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

    public void add(ListItemDetailEntity detail) {
        quantity = quantity + detail.getQuantity();
        details.add(detail);
    }

    public List<ListItemDetailEntity> getDetails() {
        return details;
    }
}
