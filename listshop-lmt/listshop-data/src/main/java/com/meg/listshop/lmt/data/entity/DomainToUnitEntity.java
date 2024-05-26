package com.meg.listshop.lmt.data.entity;


import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.DomainType;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "domain_unit")
public class DomainToUnitEntity {

    @Id
    @Column(name = "domain_unit_id")
    private Long domainUnitId;

    @Enumerated(EnumType.STRING)
    private DomainType domainType;

    @ManyToOne
    @JoinColumn(name = "unit_id")
    private UnitEntity unit;

    public DomainToUnitEntity() {
        // for jpa
    }

    public Long getDomainUnitId() {
        return domainUnitId;
    }

    public void setDomainUnitId(Long domainUnitId) {
        this.domainUnitId = domainUnitId;
    }

    public DomainType getDomainType() {
        return domainType;
    }

    public void setDomainType(DomainType domain_type) {
        this.domainType = domain_type;
    }

    public UnitEntity getUnit() {
        return unit;
    }

    public void setUnit(UnitEntity unit) {
        this.unit = unit;
    }

    public DomainToUnitEntity(Long domainUnitId, DomainType domainType, UnitEntity unit) {
        this.domainUnitId = domainUnitId;
        this.domainType = domainType;
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "DomainToUnitEntity{" +
                "domainUnitId=" + domainUnitId +
                ", domain_type=" + domainType +
                ", unit=" + unit +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomainToUnitEntity that = (DomainToUnitEntity) o;
        return Objects.equals(domainUnitId, that.domainUnitId) && domainType == that.domainType && Objects.equals(unit, that.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(domainUnitId, domainType, unit);
    }
}
