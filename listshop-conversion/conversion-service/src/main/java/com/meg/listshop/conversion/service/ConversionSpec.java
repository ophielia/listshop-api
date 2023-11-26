package com.meg.listshop.conversion.service;

import com.meg.listshop.conversion.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.*;
import com.meg.listshop.conversion.tools.ConversionTools;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ConversionSpec {

    private final Long unitId;

    private final UnitType unitType;
    private final UnitSubtype unitSubtype;

    private final Set<UnitFlavor> flavors;

    private ConversionSpec(Long unitId, UnitType unitType, UnitSubtype subtype, Set<UnitFlavor> flavors) {
        this.unitId = unitId;
        this.unitType = unitType;
        this.flavors = flavors;
        this.unitSubtype = subtype;
    }

    public static ConversionSpec fromExactUnit(UnitEntity unitSource) {
        return new ConversionSpec(unitSource.getId(), unitSource.getType(), unitSource.getSubtype(), ConversionTools.flavorsForUnit(unitSource));
    }

    private static UnitType oppositeType(UnitType unitType) {
        switch (unitType) {
            case Imperial:
                return UnitType.Metric;
            case Metric:
                return UnitType.Imperial;
        }
        return unitType;
    }

    public static ConversionSpec convertedFromUnit(UnitEntity unitSource) {
        return new ConversionSpec(null, oppositeType(unitSource.getType()), unitSource.getSubtype(), ConversionTools.flavorsForUnit(unitSource));
    }

    public static ConversionSpec basicSpec(UnitType type, UnitSubtype subtype, UnitFlavor... flavors) {
        Set<UnitFlavor> flavorSet = new HashSet<>();
        for (UnitFlavor flavor : flavors) {
            flavorSet.add(flavor);
        }
        return new ConversionSpec(null, type, subtype, flavorSet);
    }

    public static ConversionSpec fromContextAndSource(ConversionContext context, UnitEntity source) {
        return new ConversionSpec(null, context.getUnitType(), context.getUnitSubtype(), flavorsForContextAndSource(context, source));
    }


    private static Set<UnitFlavor> flavorsForContextAndSource(ConversionContext context, UnitEntity source) {
        ConversionContextType conversionContextType = context.getContextType();
        Set<UnitFlavor> flavors = new HashSet<>();

        switch (conversionContextType) {
            case Dish:
                flavors.add(UnitFlavor.DishUnit);
                break;
            case List:
                flavors.add(UnitFlavor.ListUnit);

        }
        if (source.isLiquid()) {
            flavors.add(UnitFlavor.Liquid);
        } else if (source.isWeight()) {
            flavors.add(UnitFlavor.Weight);
        } else if (source.isVolume()) {
            flavors.add(UnitFlavor.Volume);
        }
        return flavors;
    }

    public static ConversionSpec basicSpec(Long unitId, UnitType type, UnitSubtype subtype, Set<UnitFlavor> flavorSet) {
        return new ConversionSpec(unitId, type, subtype, flavorSet);
    }

    public boolean matches(UnitEntity unit) {
        ConversionSpec toCheck = ConversionSpec.fromExactUnit(unit);
        return this.equals(toCheck);
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
                ", flavors=" + flavors +
                '}';
    }

}
