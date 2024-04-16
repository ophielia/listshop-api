package com.meg.listshop.conversion.service;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.pojo.ConversionTargetType;
import com.meg.listshop.conversion.data.pojo.UnitSubtype;
import com.meg.listshop.conversion.data.pojo.UnitType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ConversionContext {

    Long conversionId;
    String marker;
    ConversionSpec targetSpec;

    List<ConversionFactor> unitFactors = new ArrayList<>();


    public ConversionContext(ConvertibleAmount amount, ConversionSpec conversionSpec) {
        conversionId = amount.getConversionId();
        targetSpec = conversionSpec;
    }

    public boolean requiresAndCanDoTagSpecificConversion(ConvertibleAmount amount) {
        Set<UnitSubtype> subtypes = new HashSet<>();
        subtypes.add(amount.getUnit().getSubtype());
        subtypes.add(targetSpec.getUnitSubtype());

        if (subtypes.size() == 1) {
            // same subtype - no conversion necessary
            return false;
        }

        return subtypes.contains(UnitSubtype.SOLID) && conversionId != null;
    }

    public boolean requiresDomainConversion(ConvertibleAmount amount) {
        if (unitFactors == null || targetSpec.getContextType() == null ) {
            return !amount.getUnit().getType().equals(targetSpec.getUnitType());
        }
        if (!unitFactors.isEmpty() && targetSpec.getContextType().equals(ConversionTargetType.List)) {
            return false;
        }
        return !amount.getUnit().getType().equals(targetSpec.getUnitType());

    }

    public ConversionTargetType getTargetContextType() {
        return targetSpec.getContextType();
    }

    public UnitType getTargetUnitType() {
        return targetSpec.getUnitType();
    }

    public Long getTargetUnitId() {
        return targetSpec.getUnitId();
    }

    public UnitSubtype getTargetSubtype() {
        return targetSpec.getUnitSubtype();
    }

    public boolean doesntRequireConversion(ConvertibleAmount toConvert) {
        boolean specMatches = targetSpec.matches(toConvert.getUnit());
        if (!specMatches) {
            return false;
        }
        if (targetSpec.getUnitId() != null) {
            return targetSpec.getUnitId().equals(toConvert.getUnit().getId());
        }
        return true;
    }

    public boolean convertsToSpecificUnit(ConvertibleAmount toConvert) {
        return targetSpec.getUnitId() != null && toConvert.getUnit().getId() != null;
    }

    public void conversionFactorsFound(List<ConversionFactor> factors) {
        if (conversionId != null) {
            this.unitFactors = factors.stream()
                    .filter(f -> f.getFromUnit().getType().equals(UnitType.UNIT) ||
                            f.getToUnit().getType().equals(UnitType.UNIT))
                    .collect(Collectors.toList());
        }
    }

    public Long getConversionId() {
        return conversionId;
    }

    public List<ConversionFactor> getUnitConversionFactors() {
        return unitFactors;
    }

    public boolean shouldScaleToUnit() {
        // unit factors exits, and target is list
        return unitFactors!=null
                && !unitFactors.isEmpty()
                && targetSpec.getContextType() != null
        && targetSpec.getContextType().equals(ConversionTargetType.List);
    }
}
