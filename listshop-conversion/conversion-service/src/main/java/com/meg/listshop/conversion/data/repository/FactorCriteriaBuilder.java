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

public class FactorCriteriaBuilder {
    private final FactorCriteria criteria;

    public FactorCriteriaBuilder() {
        this.criteria = new FactorCriteria();
    }


    public FactorCriteriaBuilder withFromUnit(UnitEntity fromUnit) {
        this.criteria.setFromUnit(fromUnit);
        return this;
    }

    public FactorCriteriaBuilder withFromType(UnitType unitType) {
        this.criteria.setFromType(unitType);
        return this;
    }

    public FactorCriteriaBuilder withToDomain(UnitType toDomain) {
        this.criteria.setToDomain(toDomain);
        return this;
    }

    public FactorCriteriaBuilder withFromDomain(UnitType fromDomain) {
        this.criteria.setFromDomain(fromDomain);
        return this;
    }

    public FactorCriteriaBuilder withToContext(ConversionTargetType toContext) {
        this.criteria.setToContext(toContext);
        return this;
    }

    public FactorCriteriaBuilder withConversionId(Long conversionId) {
        this.criteria.setConversionId(conversionId);
        return this;
    }

    public FactorCriteria build() {
        return this.criteria;
    }

    public FactorCriteriaBuilder withToUnit(Long toUnitId) {
        this.criteria.setToUnitId(toUnitId);
        return this;
    }

    public FactorCriteriaBuilder withToUnitList(Set<Long> toUnitIds) {
        this.criteria.setToUnitIds(toUnitIds);
        return this;
    }



    public FactorCriteriaBuilder withBridgeThroughMetric(boolean bridgeThroughMetric) {
        this.criteria.setBridgeThroughMetric(bridgeThroughMetric);
        return this;
    }

    public FactorCriteriaBuilder withBridgeToUnits(boolean bridgeToUnits) {
        this.criteria.setBridgeToUnits(bridgeToUnits);
        return this;
    }

    public FactorCriteriaBuilder withTargetDefaultUnit(boolean b) {
        this.criteria.setToDefaultUnit(b);
        return this;
    }

    public FactorCriteriaBuilder withTargetVolumeOnly(boolean volumeOnly) {
        this.criteria.setTargetVolumeOnly(volumeOnly);
        return this;
    }

    public FactorCriteriaBuilder withTargetDefaultSize(boolean targetDefaultSize) {
        this.criteria.setToDefaultSize(targetDefaultSize);
        return this;
    }

    public FactorCriteriaBuilder withFromModifier(String fromModifier) {
        this.criteria.setFromModifier(fromModifier);
        return this;
    }

    public FactorCriteriaBuilder withFromSize(String fromSize) {
        this.criteria.setFromSize(fromSize);
        return this;
    }

    public FactorCriteriaBuilder withToModifier(String toModifier) {
        this.criteria.setToModifier(toModifier);
        return this;
    }


    public FactorCriteriaBuilder withToSize(String toSize) {
        this.criteria.setToSize(toSize);
        return this;
    }

    public FactorCriteriaBuilder withFromModifierOrNull(String marker) {
        this.criteria.setFromModifier(marker);
        this.criteria.setExactModifierMatch(true);
        return this;
    }

    public FactorCriteriaBuilder withFromDefaultSize(boolean fromDefaultSize) {
        this.criteria.setFromDefaultSize(fromDefaultSize);
        return this;
    }

    public FactorCriteriaBuilder withFromContext(ConversionTargetType context) {
        this.criteria.setFromContext(context);
        return this;
    }

    public FactorCriteriaBuilder withTypeIn(List<UnitType> unitTypes) {
        this.criteria.setFromUnitTypes(unitTypes);
        return this;
    }

    public FactorCriteriaBuilder withMarkerOrNull(String marker) {
        this.criteria.setMarkerOrNull(marker);
        return this;
    }

    public FactorCriteriaBuilder withSizeOrDefault(String size) {
        this.criteria.setSizeOrDefault(size);
        return this;
    }

    public FactorCriteriaBuilder withToUnitType(UnitType unitType) {
        this.criteria.setToUnitType(unitType);
        return this;
    }

    public FactorCriteriaBuilder withFromExcludeDomain(UnitType domainType) {
        this.criteria.setFromExcludeDomain(domainType);
        return this;
    }
}
