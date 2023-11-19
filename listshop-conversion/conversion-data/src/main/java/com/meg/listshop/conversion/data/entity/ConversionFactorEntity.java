package com.meg.listshop.conversion.data.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "factors")
@GenericGenerator(
        name = "factor_sequence",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@org.hibernate.annotations.Parameter(
                name = "sequence_name",
                value = "factor_sequence"),
                @org.hibernate.annotations.Parameter(
                        name = "initial_value",
                        value = "1000"),
                @org.hibernate.annotations.Parameter(
                        name = "increment_size",
                        value = "1")}
)
public class ConversionFactorEntity implements ConversionFactor {
    @Id
    @Column(name = "FACTOR_ID")
    private Long factorId;

    private Double factor;

    @Column(name = "TO_UNIT")
    private UnitEntity toUnit;

    @Column(name = "FROM_UNIT")
    private UnitEntity fromUnit;

    public ConversionFactorEntity() {
    }

    public Long getFactorId() {
        return factorId;
    }

    public void setFactorId(Long factorId) {
        this.factorId = factorId;
    }

    @Override
    public Double getFactor() {
        return factor;
    }

    public void setFactor(Double factor) {
        this.factor = factor;
    }

    @Override
    public UnitEntity getToUnit() {
        return toUnit;
    }

    public void setToUnit(UnitEntity toUnit) {
        this.toUnit = toUnit;
    }

    @Override
    public UnitEntity getFromUnit() {
        return fromUnit;
    }

    public void setFromUnit(UnitEntity fromUnit) {
        this.fromUnit = fromUnit;
    }

    @Override
    public String toString() {
        return "ConversionFactorEntity{" +
                "factorId=" + factorId +
                ", factor=" + factor +
                ", toUnit=" + toUnit +
                ", fromUnit=" + fromUnit +
                '}';
    }
}
