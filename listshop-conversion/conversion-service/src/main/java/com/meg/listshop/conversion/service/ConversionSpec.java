package com.meg.listshop.conversion.service;

import com.meg.listshop.common.UnitSubtype;
import com.meg.listshop.common.UnitType;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.*;
import com.meg.listshop.conversion.tools.ConversionTools;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ConversionSpec {

    private final Long unitId;

    private final DomainType domainType;
    private final UnitType unitType;
    private final UnitSubtype unitSubtype;

    private final ConversionTargetType contextType;

    private final Set<UnitFlavor> flavors;
    private final String unitSize;

    private ConversionSpec(Long unitId, UnitType unitType, UnitSubtype subtype, ConversionTargetType contextType, String unitSize, DomainType domainType, Set<UnitFlavor> flavors) {
        this.unitId = unitId;
        this.unitType = unitType;
        this.flavors = flavors;
        this.unitSubtype = subtype;
        this.contextType = contextType;
        this.unitSize = unitSize;
        this.domainType = domainType;
    }

    public static ConversionSpec specForDomain(UnitEntity unitSource, DomainType domain) {
        UnitType unitType = unitTypeForDomain(domain);
        return new ConversionSpec(null, unitType, unitSource.getSubtype(), null, null, domain, new HashSet<>());
    }

    private static UnitType unitTypeForDomain(DomainType domain) {
        switch (domain) {
            case US:
                return UnitType.US;
            case METRIC:
                return UnitType.METRIC;
            case UK:
                return UnitType.UK;
            default:
                throw new IllegalArgumentException("Unsupported domain type: " + domain);
        }
    }

    public static ConversionSpec fromExactUnit(UnitEntity unitSource) {
        return new ConversionSpec(unitSource.getId(), unitSource.getType(), unitSource.getSubtype(), null, null, null, ConversionTools.flavorsForUnit(unitSource));
    }

    public static ConversionSpec basicSpec(UnitType type, UnitSubtype subtype, UnitFlavor... flavors) {
        Set<UnitFlavor> flavorSet = new HashSet<>(Arrays.asList(flavors));
        return new ConversionSpec(null, type, subtype, null, null, null, flavorSet);
    }

    public static ConversionSpec basicSpec(Long unitId, UnitType type, UnitSubtype subtype, Set<UnitFlavor> flavorSet) {
        return new ConversionSpec(unitId, type, subtype, null, null, null, flavorSet);
    }

    public static ConversionSpec basicSpec(Long unitId, UnitType type, UnitSubtype subtype, String unitSize, Set<UnitFlavor> flavorSet) {
        return new ConversionSpec(unitId, type, subtype, null, unitSize, null, flavorSet);
    }

    public static ConversionSpec specForConversionRequest(ConversionRequest request, UnitSubtype subtype) {
        UnitType mainType = unitTypeForDomain(request.getDomainType());
        return new ConversionSpec(null, mainType, subtype, request.getContextType(),
                request.getUnitSize(), request.getDomainType(), new HashSet<>());
    }

    public static ConversionSpec specForAddRequest(AddRequest request) {
        return new ConversionSpec(null, request.getUnitType(), request.getSubtype(),
                request.getContextType(), request.getUnitSize(), null, null);
    }

    public static ConversionSpec specWithUnitSize(ConversionSpec spec, String unitSize) {
        return new ConversionSpec(spec.getUnitId(), spec.getUnitType(), spec.getUnitSubtype(),
                spec.getContextType(), unitSize, null, null);
    }

    public boolean matches(UnitEntity unit) {
        if (unit.getType() != getUnitType() ||
                unit.getSubtype() != getUnitSubtype()) {
            return false;
        }
        return ConversionTools.flavorsForUnit(unit).containsAll(getFlavors());
    }

    public UnitType getUnitType() {
        return unitType;
    }


    public Long getUnitId() {
        return unitId;
    }

    public Set<UnitFlavor> getFlavors() {
        return flavors;
    }

    public UnitSubtype getUnitSubtype() {
        return unitSubtype;
    }

    public ConversionTargetType getContextType() {
        return contextType;
    }

    public String getUnitSize() {
        return unitSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConversionSpec that = (ConversionSpec) o;
        return unitType == that.unitType && unitSubtype == that.unitSubtype && Objects.equals(flavors, that.flavors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(unitType, unitSubtype, flavors);
    }

    @Override
    public String toString() {
        return "ConversionSpec{" +
                "unitId=" + unitId +
                ", unitType=" + unitType +
                ", unitSubtype=" + unitSubtype/**/ +
                ", contextType=" + contextType +
                ", flavors=" + flavors +
                ", unitSize=" + unitSize +
                '}';
    }

}
