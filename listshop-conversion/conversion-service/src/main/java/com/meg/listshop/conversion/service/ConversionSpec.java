package com.meg.listshop.conversion.service;

import com.meg.listshop.conversion.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.*;
import com.meg.listshop.conversion.tools.ConversionTools;

import java.util.Arrays;
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
        if (Objects.requireNonNull(unitType) == UnitType.US) {
            return UnitType.METRIC;
        } else if (unitType == UnitType.METRIC) {
            return UnitType.US;
        }
        return unitType;
    }

    public static ConversionSpec convertedFromUnit(UnitEntity unitSource) {
        return new ConversionSpec(null, oppositeType(unitSource.getType()), unitSource.getSubtype(), ConversionTools.flavorsForUnit(unitSource));
    }

    public static ConversionSpec opposingSpec(ConversionSpec sourceSpec) {
        return new ConversionSpec(null, oppositeType(sourceSpec.getUnitType()), sourceSpec.getUnitSubtype(), sourceSpec.getFlavors());
    }

    public static ConversionSpec basicSpec(UnitType type, UnitSubtype subtype, UnitFlavor... flavors) {
        Set<UnitFlavor> flavorSet = new HashSet<>(Arrays.asList(flavors));
        return new ConversionSpec(null, type, subtype, flavorSet);
    }


    public static ConversionSpec fromContext(ConversionContext context, UnitEntity source) {
        UnitType specUnitType = unitTypeForContext(context, source, true);
        UnitSubtype specUnitSubtype = unitSubtypeForContext(context,source,true);

        return new ConversionSpec(null, specUnitType, specUnitSubtype, flavorsForContext(context));
    }

    public static ConversionSpec retryFromContext(ConversionContext context, UnitEntity source) {
        UnitType specUnitType = unitTypeForContext(context, source, false);
        UnitSubtype specUnitSubtype = unitSubtypeForContext(context,source,false);

        return new ConversionSpec(null, specUnitType, specUnitSubtype, flavorsForContext(context));
    }

    public static UnitType unitTypeForContext(ConversionContext context, UnitEntity source, boolean allowsHybrid) {
        UnitType specUnitType = context.getUnitType();

        if (allowsHybrid &&
                source.getType().equals(UnitType.HYBRID) &&
                context.getContextType().equals(ConversionContextType.Dish)) {
            specUnitType = UnitType.HYBRID;
        }

        return specUnitType;
    }

    public static UnitSubtype unitSubtypeForContext(ConversionContext context, UnitEntity source, boolean allowsHybrid) {
        UnitSubtype specUnitSubtype;

        if (source.getType().equals(UnitType.SPECIAL)) {
            specUnitSubtype = UnitSubtype.NONE;
        }  else if (!allowsHybrid &&
                source.getType().equals(UnitType.HYBRID) &&
                context.getContextType().equals(ConversionContextType.Dish) &&
                source.isLiquid()) {
            specUnitSubtype = UnitSubtype.VOLUME;
        }  else if (!allowsHybrid &&
                source.getType().equals(UnitType.HYBRID) &&
                context.getContextType().equals(ConversionContextType.Dish) &&
                !source.isLiquid()) {
            specUnitSubtype = UnitSubtype.WEIGHT;
        } else if (allowsHybrid &&
                source.getType().equals(UnitType.HYBRID) &&
                context.getContextType().equals(ConversionContextType.Dish)) {
            specUnitSubtype = UnitSubtype.NONE;
        } else if (context.getContextType().equals(ConversionContextType.List) &&
                source.isLiquid()) {
            specUnitSubtype = UnitSubtype.VOLUME;
        } else if (context.getContextType().equals(ConversionContextType.List) &&
                !source.isLiquid()) {
            specUnitSubtype = UnitSubtype.WEIGHT;
        } else {
            specUnitSubtype = source.getSubtype();
        }

        return specUnitSubtype;
    }


    private static Set<UnitFlavor> flavorsForContext(ConversionContext context) {
        ConversionContextType conversionContextType = context.getContextType();
        Set<UnitFlavor> flavors = new HashSet<>();

        if (Objects.requireNonNull(conversionContextType) == ConversionContextType.Dish) {
            flavors.add(UnitFlavor.DishUnit);
        } else if (conversionContextType == ConversionContextType.List) {
            flavors.add(UnitFlavor.ListUnit);
        }

        return flavors;
    }

    public static ConversionSpec basicSpec(Long unitId, UnitType type, UnitSubtype subtype, Set<UnitFlavor> flavorSet) {
        return new ConversionSpec(unitId, type, subtype, flavorSet);
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
