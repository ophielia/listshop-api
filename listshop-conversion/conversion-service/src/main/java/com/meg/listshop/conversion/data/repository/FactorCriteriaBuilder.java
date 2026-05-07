/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.data.repository;

import com.meg.listshop.common.UnitType;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.ConversionTargetType;
import com.meg.listshop.conversion.service.ConversionContext;

public class FactorCriteriaBuilder {
    private final FactorCriteria criteria;

    public FactorCriteriaBuilder() {
        this.criteria = new FactorCriteria();
    }

    public FactorCriteriaBuilder withFromUnit(UnitEntity fromUnit) {
        this.criteria.setFromUnit(fromUnit);
        return this;
    }

    public FactorCriteriaBuilder withToDomain(UnitType toDomain) {
        this.criteria.setToDomain(toDomain);
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

    public FactorCriteriaBuilder withBridgeThroughMetric(boolean bridgeThroughMetric) {
        this.criteria.setBridgeThroughMetric(bridgeThroughMetric);
        return this;
    }

    public FactorCriteriaBuilder withTargetDefaultUnit(boolean b) {
        this.criteria.setTargetDefaultUnit(b);
        return this;
    }

    public FactorCriteriaBuilder withTargetVolumeOnly(boolean volumeOnly) {
        this.criteria.setTargetVolumeOnly(volumeOnly);
        return this;
    }
}
