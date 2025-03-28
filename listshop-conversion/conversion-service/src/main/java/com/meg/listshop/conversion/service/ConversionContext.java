package com.meg.listshop.conversion.service;

import com.meg.listshop.common.UnitSubtype;
import com.meg.listshop.common.UnitType;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.pojo.ConversionTargetType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ConversionContext {

    Long conversionId;
    ConversionSpec targetSpec;

    List<ConversionFactor> unitFactors = new ArrayList<>();
    List<ConversionFactor> destinationFactors = new ArrayList<>();
    private double gramWeight;
    private boolean isUnitToUnit;


    public ConversionContext(ConvertibleAmount amount, ConversionSpec conversionSpec) {
        conversionId = amount.getConversionId();
        targetSpec = conversionSpec;
        UnitEntity unit = amount.getUnit();
        boolean sourceIsUnit = unit.getType() == UnitType.UNIT;
        if (sourceIsUnit && conversionSpec.getUnitType() != null) {
            isUnitToUnit = sourceIsUnit && conversionSpec.getUnitType().equals(UnitType.UNIT);
        }
    }

    public boolean isTagSpecfic() {
        return conversionId != null;
    }


    public boolean isUnitToUnit() {
        return isUnitToUnit;
    }

    public boolean requiresAndCanDoTagSpecificConversion(ConvertibleAmount amount) {
        if (amount.getConversionId() != null) {
            return true;
        }
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
        if (unitFactors != null &&
                !unitFactors.isEmpty() &&
                (ConversionTargetType.List.equals(targetSpec.getContextType()) ||
                        targetSpec.getUnitType().equals(UnitType.UNIT))) {
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

    public String getTargetUnitSize() {
        return targetSpec.getUnitSize();
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
        if (conversionId == null) {
            return;
        }
        this.unitFactors = new ArrayList<>();
        this.destinationFactors = new ArrayList<>();
        String defaultUnitSize = null;
        for (ConversionFactor factor : factors) {
            if (factor.getFromUnit().getType().equals(UnitType.UNIT) ||
                    factor.getToUnit().getType().equals(UnitType.UNIT)) {
                this.unitFactors.add(factor);
                if (defaultUnitSize == null && factor.isUnitDefault()) {
                    defaultUnitSize = factor.getUnitSize();
                }
            } else {
                this.destinationFactors.add(factor);
            }
        }
        this.destinationFactors = pullScalingFactorsByTargetType(targetSpec.getContextType(), this.destinationFactors);

        // check unit size default
        checkUnitSizeDefaultAndTarget(defaultUnitSize);
    }

    private void checkUnitSizeDefaultAndTarget(String defaultUnitSize) {
        if (defaultUnitSize == null) {
            return;
        }
        // target may have null unit size  - this should be filled in, so we
        // check it with the default here
        if (targetSpec.getUnitSize() == null) {
            // remake the target with the unit size
            ConversionSpec correctedSpec = ConversionSpec.specWithUnitSize(targetSpec, defaultUnitSize);
            targetSpec = correctedSpec;
        }
    }

    private List<ConversionFactor> pullScalingFactorsByTargetType(ConversionTargetType contextType, List<ConversionFactor> factors) {
        if (contextType == ConversionTargetType.List) {
            return factors.stream()
                    .filter(f -> f.getFromUnit().isListUnit())
                    .filter(f -> f.getFromUnit().isAvailableForDomain(targetSpec.getUnitType()))
                    .collect(Collectors.toList());
        } else if (contextType == ConversionTargetType.Dish) {
            return factors.stream()
                    .filter(f -> f.getFromUnit().isDishUnit())
                    .filter(f -> f.getFromUnit().isAvailableForDomain(targetSpec.getUnitType()))
                    .collect(Collectors.toList());
        }
        return null;
    }

    public Long getConversionId() {
        return conversionId;
    }

    public List<ConversionFactor> getUnitConversionFactors() {
        return unitFactors;
    }

    public boolean shouldScaleToUnit() {
        // unit factors exits, and target is list (or unit)
        boolean unitFactorsExist = unitFactors != null && !unitFactors.isEmpty();
        boolean contextIsList = targetSpec.getContextType() != null && targetSpec.getContextType().equals(ConversionTargetType.List);
        boolean targetIsUnit = targetSpec.getUnitType() != null && targetSpec.getUnitType().equals(UnitType.UNIT);
        return unitFactorsExist && (contextIsList || targetIsUnit);
    }

    public boolean canScaleForTagSpecific() {
        return this.destinationFactors != null && !this.destinationFactors.isEmpty();
    }

    public List<ConversionFactor> getTagSpecificFactors() {
        return this.destinationFactors;
    }

    public double getGramWeight() {
        return this.gramWeight;
    }

    public void setGramWeight(double gramWeight) {
        this.gramWeight = gramWeight;
    }
}
