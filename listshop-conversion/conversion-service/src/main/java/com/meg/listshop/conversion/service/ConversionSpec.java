package com.meg.listshop.conversion.service;

import com.meg.listshop.conversion.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.ConversionContext;
import com.meg.listshop.conversion.data.pojo.ConversionContextType;
import com.meg.listshop.conversion.data.pojo.UnitFlavor;
import com.meg.listshop.conversion.data.pojo.UnitType;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ConversionSpec {

    private final Long unitId;

    private final UnitType unitType;

    private final Set<UnitFlavor> flavors;

    private ConversionSpec(Long unitId, UnitType unitType, Set<UnitFlavor> flavors) {
        this.unitId = unitId;
        this.unitType = unitType;
        this.flavors = flavors;
    }

    public static ConversionSpec fromExactUnit(UnitEntity unitSource) {
        return new ConversionSpec(unitSource.getId(), unitSource.getType(), flavorsForUnit(unitSource));
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
        return new ConversionSpec(null, oppositeType(unitSource.getType()), flavorsForUnit(unitSource));
    }

    public static ConversionSpec fromContextAndSource(ConversionContext context, UnitEntity source) {
        return new ConversionSpec(null, context.getUnitType(), flavorsForContextAndSource(context, source));
    }

    private static Set<UnitFlavor> flavorsForUnit(UnitEntity unit) {
        Set<UnitFlavor> flavors = new HashSet<>();
        if (unit.isVolume()) {
            flavors.add(UnitFlavor.Volume);
        }
        if (unit.isWeight()) {
            flavors.add(UnitFlavor.Weight);
        }
        if (unit.isLiquid()) {
            flavors.add(UnitFlavor.Liquid);
        }
        if (unit.isListUnit()) {
            flavors.add(UnitFlavor.ListUnit);
        }
        if (unit.isDishUnit()) {
            flavors.add(UnitFlavor.DishUnit);
        }
        return flavors;
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
        return unitType == that.unitType && Objects.equals(flavors, that.flavors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(unitType, flavors);
    }

    @Override
    public String toString() {
        return "ConversionSpec{" +
                "unitId=" + unitId +
                ", unitType=" + unitType +
                ", flavors=" + flavors +
                '}';
    }

    public boolean containedIn(ConversionSpec toCheck) {
        // do all flavors in this object exit in toCheck
        return unitType.equals(toCheck.getUnitType()) && toCheck.getFlavors().containsAll(flavors);

    }
}