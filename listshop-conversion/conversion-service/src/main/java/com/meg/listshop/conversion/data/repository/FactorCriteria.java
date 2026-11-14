/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.data.repository;


import com.meg.listshop.common.UnitType;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.ConversionTargetType;

import java.util.List;
import java.util.Set;

public class FactorCriteria {
    private UnitEntity fromUnit;
    private Long toUnitId;
    private String fromSize;
    private String toSize;
    private Set<Long> toUnitIds;
    private boolean toDefaultUnit;
    private String fromModifier;
    private UnitType fromDomain;
    private UnitType toDomain;
    private boolean fromDefaultSize;
    private boolean toDefaultSize;
    private UnitType fromUnitType;
    private List<UnitType> fromUnitTypes;
    private ConversionTargetType fromContext;
    private ConversionTargetType toContext;
    private UnitType fromExcludeDomain;
    private boolean fromNotOneWay;
    private boolean toNotOneWay;
    private Long conversionId;
    private boolean bridgeThroughMetric;
    private boolean exactModifierMatch;
    private String markerOrNull;
    private String sizeOrDefault;

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

    public UnitType getFromDomain() {
        return fromDomain;
    }

    public void setFromDomain(UnitType fromDomain) {
        this.fromDomain = fromDomain;
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

    public void setBridgeThroughMetric(boolean bridgeThroughMetric) {
        this.bridgeThroughMetric = bridgeThroughMetric;
    }

    public boolean isToDefaultUnit() {
        return toDefaultUnit;
    }

    public void setToDefaultUnit(boolean b) {
        this.toDefaultUnit = b;
    }

    public boolean isToDefaultSize() {
        return toDefaultSize;
    }

    public void setToDefaultSize(boolean b) {
        this.toDefaultSize = b;
    }

    public boolean isFromDefaultSize() {
        return fromDefaultSize;
    }

    public void setFromDefaultSize(boolean b) {
        this.fromDefaultSize = b;
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

    public boolean isExactModifierMatch() {
        return exactModifierMatch;
    }

    public void setExactModifierMatch(boolean exactModifierMatch) {
        this.exactModifierMatch = exactModifierMatch;
    }

    public void setFromType(UnitType unitType) {
        this.fromUnitType = unitType;
    }

    public UnitType getFromUnitType() {
        return fromUnitType;
    }

    public List<UnitType> getFromUnitTypes() {
        return fromUnitTypes;
    }

    public void setFromUnitTypes(List<UnitType> fromUnitTypes) {
        this.fromUnitTypes = fromUnitTypes;
    }

    public ConversionTargetType getFromContext() {
        return fromContext;
    }

    public void setFromContext(ConversionTargetType context) {
        this.fromContext = context;
    }

    public String getMarkerOrNull() {
        return markerOrNull;
    }

    public void setMarkerOrNull(String marker) {
        this.markerOrNull = marker;
    }

    public String getSizeOrDefault() {
        return sizeOrDefault;
    }

    public void setSizeOrDefault(String size) {
        this.sizeOrDefault = size;
    }

    public UnitType getFromExcludeDomain() {
        return fromExcludeDomain;
    }

    public void setFromExcludeDomain(UnitType domainType) {
        this.fromExcludeDomain = domainType;
    }

    public Set<Long> getToUnitIds() {
        return toUnitIds;
    }

    public void setToUnitIds(Set<Long> toUnitIds) {
        this.toUnitIds = toUnitIds;
    }

    public boolean toNotOneWay() {
        this.toNotOneWay = true;
        return this.toNotOneWay;
    }

    public boolean fromNotOneWay() {
        this.fromNotOneWay = true;
        return this.fromNotOneWay;
    }

    public boolean isToNotOneWay() {
        return toNotOneWay;
    }

    public boolean isFromNotOneWay() {
        return fromNotOneWay;
    }

}
