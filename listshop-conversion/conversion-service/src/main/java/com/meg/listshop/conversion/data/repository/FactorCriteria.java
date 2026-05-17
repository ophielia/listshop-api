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
    private String fromSize;
    private String fromModifier;
    private UnitType toDomain;
    private ConversionTargetType toContext;
    private Long toUnitId;
    private String toSize;
    private String toModifier;
    private Long conversionId;
    private boolean bridgeThroughMetric;
    private boolean bridgeToUnits;
    private boolean toDefaultUnit;
    private boolean toDefaultSize;
    private boolean targetVolumeOnly;

    public FactorCriteria() {
    }

    public static FactorCriteria from(FactorCriteria criteria) {
        FactorCriteriaBuilder builder = new FactorCriteriaBuilder();
        builder.withBridgeThroughMetric(criteria.bridgeThroughMetric())
                .withBridgeToUnits(criteria.bridgeToUnits())
                .withConversionId(criteria.getConversionId())
                .withFromModifier(criteria.getFromModifier())
                .withFromSize(criteria.getFromSize())
                .withFromUnit(criteria.getFromUnit())
                .withTargetDefaultSize(criteria.isToDefaultSize())
                .withTargetDefaultUnit(criteria.isToDefaultUnit())
                .withToModifier(criteria.getToModifier())
                .withToSize(criteria.getToSize())
                .withToUnit(criteria.getToUnitId());
        return builder.build();
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

    public boolean bridgeThroughMetric() {
        return bridgeThroughMetric;
    }

    public boolean bridgeToUnits() {
        return bridgeToUnits;
    }

    public void setBridgeThroughMetric(boolean bridgeThroughMetric) {
        this.bridgeThroughMetric = bridgeThroughMetric;
    }

    public void setBridgeToUnits(boolean bridgeToUnits) {
        this.bridgeToUnits = bridgeToUnits;
    }

    public void setToDefaultUnit(boolean b) {
        this.toDefaultUnit = b;
    }
    public void setToDefaultSize(boolean b) {
        this.toDefaultSize = b;
    }



    public boolean isToDefaultUnit() {
        return toDefaultUnit;
    }

    public boolean isToDefaultSize() {
        return toDefaultSize;
    }

    public void setTargetVolumeOnly(boolean volumeOnly) {
        this.targetVolumeOnly = volumeOnly;
    }

    public String getFromSize() {
        return fromSize;
    }

    public void setFromSize(String fromSize) {
        this.fromSize = fromSize;
    }

    public String getFromModifier() {
        return fromModifier;
    }

    public void setFromModifier(String fromModifier) {
        this.fromModifier = fromModifier;
    }

    public String getToSize() {
        return toSize;
    }

    public void setToSize(String toSize) {
        this.toSize = toSize;
    }

    public String getToModifier() {
        return toModifier;
    }

    public void setToModifier(String toModifier) {
        this.toModifier = toModifier;
    }
}
