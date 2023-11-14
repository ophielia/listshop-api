package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.Unit;
import com.meg.listshop.conversion.data.pojo.SimpleAmount;
import com.meg.listshop.conversion.data.repository.ConversionFactorSource;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.service.ConversionSpec;
import com.meg.listshop.conversion.service.ConvertibleAmount;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class AbstractConversionHandler implements ConversionHandler {


    private ConversionSpec source;
    private ConversionSpec target;
    private ConversionFactorSource conversionSource;

    public AbstractConversionHandler(ConversionSpec source, ConversionSpec target, ConversionFactorSource conversionSource) {
        this.source = source;
        this.target = target;
        this.conversionSource = conversionSource;
    }

    public boolean convertsTo(ConversionSpec spec) {
        return target.equals(spec) || source.equals(spec);
    }

    public boolean handles(ConversionSpec sourceSpec, ConversionSpec targetSpec) {
        return (source.equals(sourceSpec) && target.equals(targetSpec)) ||
                (target.equals(sourceSpec) && source.equals(targetSpec));
    }

    public ConvertibleAmount convert(ConvertibleAmount toConvert, ConversionSpec targetSpec) throws ConversionFactorException {

        List<ConversionFactor> factors = new ArrayList<>();
        if (targetSpec.getUnitId() != null) {
            factors.add(conversionSource.getFactor(toConvert.getUnit().getId(), targetSpec.getUnitId()));
        } else {
            factors.addAll(conversionSource.getFactors(toConvert.getUnit().getType()));
        }

        if (factors.isEmpty()) {
            String message = String.format("No factors found in handler %s.", this.getClass().getName());
            throw new ConversionFactorException(message);
        }

        ConversionFactor factor = findBestFactor(factors);

        double newQuantity = toConvert.getQuantity() * factor.getFactor();
        Unit newUnit = factor.getToUnit();

        return new SimpleAmount(newQuantity, newUnit, toConvert);
    }

    private ConversionFactor findBestFactor(List<ConversionFactor> factors) {
        if (factors.size() == 1) {
            return factors.get(0);
        }

        Comparator<ConversionFactor> comparator = (f1, f2) -> {
            Double f1ToOne = Math.abs(f1.getFactor() - 1);
            Double f2ToOne = Math.abs(f2.getFactor() - 1);
            return f1ToOne.compareTo(f2ToOne);
        };

        factors.sort(comparator);
        return factors.get(0);

    }
}

