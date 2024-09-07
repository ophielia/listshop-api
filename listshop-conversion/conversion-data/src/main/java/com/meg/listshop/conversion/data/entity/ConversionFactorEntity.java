package com.meg.listshop.conversion.data.entity;

import com.meg.listshop.common.data.entity.UnitEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;

@Entity
@Table(name = "factors")
public class ConversionFactorEntity implements ConversionFactor {
    @Id
    @Tsid
    @Column(name = "FACTOR_ID")
    private Long factorId;

    private Double factor;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "toUnit")
    private UnitEntity toUnit;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fromUnit")
    private UnitEntity fromUnit;

    @Column(name = "CONVERSION_ID")
    private Long conversionId;

    @Column(name = "reference_id")
    private Long referenceId;

    private String marker;

    @Column(name = "unit_size")
    private String unitSize;

    @Column(name = "unit_default")
    private Boolean unitDefault;

    public ConversionFactorEntity() {
        // empty constructor for jpa
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

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public String getUnitSize() {
        return unitSize;
    }

    public void setUnitSize(String unitSize) {
        this.unitSize = unitSize;
    }

    public Boolean isUnitDefault() {
        return unitDefault != null && unitDefault;
    }

    public void setUnitDefault(Boolean unitDefault) {
        this.unitDefault = unitDefault;
    }

    @Override
    public String toString() {
        return "ConversionFactorEntity{" +
                "factorId=" + factorId +
                ", factor=" + factor +
                ", toUnit=" + toUnit +
                ", fromUnit=" + fromUnit +
                ", conversionId=" + conversionId +
                ", referenceId=" + referenceId +
                ", marker='" + marker + '\'' +
                ", unitSize='" + unitSize + '\'' +
                ", unitDefault=" + unitDefault +
                '}';
    }
}
