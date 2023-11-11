package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.Unit;
import com.meg.listshop.conversion.data.pojo.SimpleAmount;
import com.meg.listshop.conversion.data.pojo.UnitType;
import com.meg.listshop.conversion.data.repository.ConversionFactorSource;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.service.ConvertibleAmount;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class AbstractConversionHandler implements ConversionHandler {

    private List<UnitType> fromUnitTypes = new ArrayList<>();
    private List<UnitType> toUnitTypes = new ArrayList<>();
    private ConversionFactorSource conversionSource;

    public AbstractConversionHandler(ConversionFactorSource conversionSource, List<UnitType> fromUnitTypes, List<UnitType> toUnitTypes) {
        this.fromUnitTypes = fromUnitTypes;
        this.toUnitTypes = toUnitTypes;
        this.conversionSource = conversionSource;
    }

    public AbstractConversionHandler(ConversionFactorSource conversionSource) {
        this.conversionSource = conversionSource;
    }

    public boolean convertsTo(UnitType toUnit) {
        return toUnitTypes.contains(toUnit);
    }

    public boolean handles(UnitType fromUnit, UnitType toUnit) {
        return fromUnitTypes.contains(fromUnit) && toUnitTypes.contains(toUnit) ||
                toUnitTypes.contains(fromUnit) && toUnitTypes.contains(toUnit);
    }

    public ConvertibleAmount convert(ConvertibleAmount toConvert) throws ConversionFactorException {
        boolean isReversed = false;
        if (toUnitTypes.contains(toConvert.getUnit().getType())) {
            isReversed = true;
        }
        List<ConversionFactor> factors = conversionSource.getFactors(toConvert.getUnit().getType());

        if (factors.isEmpty()) {
            String message = String.format("No factors found in handler %s.", this.getClass().getName());
            throw new ConversionFactorException(message);
        }
        // find best factor for quantity
        Comparator<ConversionFactor> comparator = (f1, f2) -> {
            Double f1ToOne = Math.abs(f1.getFactor() - 1);
            Double f2ToOne = Math.abs(f2.getFactor() - 1);
            return f1ToOne.compareTo(f2ToOne);
        };

        factors.sort(comparator);
        ConversionFactor factor = factors.get(0);

        double newQuantity = isReversed ? toConvert.getQuantity() / factor.getFactor() :
                toConvert.getQuantity() * factor.getFactor();
        Unit newUnit = factor.getUnit();

        return new SimpleAmount(newQuantity, newUnit, toConvert);
    }
}
