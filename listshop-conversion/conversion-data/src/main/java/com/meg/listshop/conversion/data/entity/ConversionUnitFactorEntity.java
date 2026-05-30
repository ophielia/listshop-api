/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.data.entity;

import com.meg.listshop.common.data.entity.UnitEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "unit_factors")
public class ConversionUnitFactorEntity implements ConversionFactor{
    @Id
    @Column(name = "FACTOR_ID")
    private Long factorId;

    private Double factor;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "to_unit")
    private UnitEntity toUnit;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "from_unit")
    private UnitEntity fromUnit;

    @Column(name = "CONVERSION_ID")
    private Long conversionId;

    @Column(name = "from_marker")
    private String fromMarker;

    @Column(name = "from_unit_size")
    private String fromUnitSize;

    @Column(name = "from_unit_default")
    private Boolean fromUnitDefault;

    @Column(name = "to_marker")
    private String toMarker;

    @Column(name = "to_unit_size")
    private String toUnitSize;

    @Column(name = "to_unit_default")
    private Boolean toUnitDefault;

    public ConversionUnitFactorEntity() {
        // empty constructor for jpa
    }

    public ConversionUnitFactorEntity(Long factorId,Double factor, UnitEntity toUnit, UnitEntity fromUnit, Long conversionId,
                                      String fromMarker, String fromUnitSize, Boolean fromUnitDefault, String toMarker,
                                      String toUnitSize, Boolean toUnitDefault) {
        this.factorId = factorId;
        this.factor = factor;
        this.toUnit = toUnit;
        this.fromUnit = fromUnit;
        this.conversionId = conversionId;
        this.fromMarker = fromMarker;
        this.fromUnitSize = fromUnitSize;
        this.fromUnitDefault = fromUnitDefault;
        this.toMarker = toMarker;
        this.toUnitSize = toUnitSize;
        this.toUnitDefault = toUnitDefault;
    }

    public Long getFactorId() {
        return factorId;
    }

    public void setFactorId(Long factorId) {
        this.factorId = factorId;
    }

    public Double getFactor() {
        return factor;
    }

    public void setFactor(Double factor) {
        this.factor = factor;
    }

    public UnitEntity getToUnit() {
        return toUnit;
    }

    @Override
    public String getMarker() {
        return "";
    }

    @Override
    public String getUnitSize() {
        return toUnitSize;
    }

    @Override
    public Boolean isUnitDefault() {
        return null;
    }

    public void setToUnit(UnitEntity toUnit) {
        this.toUnit = toUnit;
    }

    public UnitEntity getFromUnit() {
        return fromUnit;
    }

    public void setFromUnit(UnitEntity fromUnit) {
        this.fromUnit = fromUnit;
    }

    public Long getConversionId() {
        return conversionId;
    }

    public void setConversionId(Long tagId) {
        this.conversionId = tagId;
    }

    public String getFromMarker() {
        return fromMarker;
    }

    public String getFromUnitSize() {
        return fromUnitSize;
    }

    public String getToMarker() {
        return toMarker;
    }

    public String getToUnitSize() {
        return toUnitSize;
    }

    public Boolean isToUnitDefault() {
        return toUnitDefault != null && toUnitDefault;
    }

    public String toString() {
        return "ConversionUnitFactorEntity{" +
                "factorId=" + factorId +
                ", factor=" + factor +
                ", toUnit=" + toUnit +
                ", fromUnit=" + fromUnit +
                ", conversionId=" + conversionId +
                ", fromMarker='" + fromMarker + '\'' +
                ", fromUnitSize='" + fromUnitSize + '\'' +
                ", fromUnitDefault=" + fromUnitDefault +
                ", toMarker='" + toMarker + '\'' +
                ", toUnitSize='" + toUnitSize + '\'' +
                ", toUnitDefault=" + toUnitDefault +
                '}';
    }
}
