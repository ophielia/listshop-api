/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.data.repository;


import com.meg.listshop.common.UnitType;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.ConversionTargetType;

public class FactorCriteria {
    private UnitEntity fromUnit;
    private UnitType toDomain;
    private ConversionTargetType toContext;
    private Long toUnitId;
    private Long conversionId;

    public FactorCriteria() {
    }

    public UnitEntity getFromUnit() {
        return fromUnit;
    }

    public void setFromUnit(UnitEntity fromUnit) {
        this.fromUnit = fromUnit;
    }

    public UnitType getToDomain() {
        return toDomain;
    }

    public void setToDomain(UnitType toDomain) {
        this.toDomain = toDomain;
    }

    public ConversionTargetType getToContext() {
        return toContext;
    }

    public void setToContext(ConversionTargetType toContext) {
        this.toContext = toContext;
    }

    public Long getToUnitId() {
        return toUnitId;
    }

    public void setToUnitId(Long toUnitId) {
        this.toUnitId = toUnitId;
    }

    public Long getConversionId() {
        return conversionId;
    }

    public void setConversionId(Long conversionId) {
        this.conversionId = conversionId;
    }
}
