package com.meg.listshop.conversion.service;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.pojo.ConversionTargetType;
import com.meg.listshop.conversion.data.pojo.UnitSubtype;
import com.meg.listshop.conversion.data.pojo.UnitType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConversionContext {

    Long conversionId;
    String marker;
    ConversionSpec targetSpec;

    List<ConversionFactor> factors;

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
        //MM also check unit here - if it converts to unit, and context is list no domain conversion necessary
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
            this.factors = factors;
        }
    }

    public Long getConversionId() {
        return conversionId;
    }
}
