package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.SimpleAmount;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.service.ConversionSpec;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.conversion.service.factors.ConversionFactorSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class AbstractConversionHandler implements ConversionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractConversionHandler.class);

    private ConversionSpec source;
    private ConversionSpec target;
    private ConversionFactorSource conversionSource;

    public AbstractConversionHandler(ConversionSpec source, ConversionSpec target, ConversionFactorSource conversionSource) {
        this.source = source;
        this.target = target;
        this.conversionSource = conversionSource;
    }

    public AbstractConversionHandler() {

    }

    public boolean convertsTo(ConversionSpec spec) {
        return target.equals(spec) || source.equals(spec);
    }

    public boolean handles(ConversionSpec sourceSpec, ConversionSpec targetSpec) {
        return (source.equals(sourceSpec) && target.equals(targetSpec)) ||
                (target.equals(sourceSpec) && source.equals(targetSpec));
    }

    public ConvertibleAmount convert(ConvertibleAmount toConvert, ConversionSpec targetSpec) throws ConversionFactorException {
        if (doesntRequireConversion(toConvert, targetSpec)) {
            LOG.debug("No conversion required for spec: [{}], amount [{}].", targetSpec, toConvert);
            return toConvert;
        }

        List<ConversionFactor> factors = new ArrayList<>();
        if (targetSpec.getUnitId() != null && toConvert.getUnit() != null && toConvert.getUnit().getId() != null) {
            ConversionFactor exact = conversionSource.getFactor(toConvert.getUnit().getId(), targetSpec.getUnitId());
            if (exact != null) {
                LOG.trace("...exact match found for unitId: [{}], found [{}].", targetSpec.getUnitId(), exact.getToUnit().getId());
                factors.add(exact);
            }
        }
        if (factors.isEmpty()) {
            factors.addAll(conversionSource.getFactors(toConvert.getUnit().getId()));
        }

        if (factors.isEmpty()) {
            String message = String.format("No factors found in handler %s.", this.getClass().getName());
            LOG.warn(message);
            throw new ConversionFactorException(message);
        }

        ConversionFactor factor = findBestFactor(factors, toConvert.getQuantity());

        double newQuantity = toConvert.getQuantity() * factor.getFactor();
        UnitEntity newUnit = factor.getToUnit();

        return new SimpleAmount(newQuantity, newUnit, toConvert);
    }

    private boolean doesntRequireConversion(ConvertibleAmount toConvert, ConversionSpec targetSpec) {
        boolean specMatches = targetSpec.matches(toConvert.getUnit());
        if (!specMatches) {
            return false;
        }
        if (targetSpec.getUnitId() != null) {
            return targetSpec.getUnitId().equals(toConvert.getUnit().getId());
        }
        return false;
    }


    private ConversionFactor findBestFactor(List<ConversionFactor> factors, double quantity) {
        if (factors.size() == 1) {
            return factors.get(0);
        }

        Comparator<ConversionFactor> comparator = (f1, f2) -> {
            Double f1ToOne = Math.abs(1 - (f1.getFactor() * quantity));
            Double f2ToOne = Math.abs(1 - (f2.getFactor() * quantity));
            return f1ToOne.compareTo(f2ToOne);
        };

        factors.sort(comparator);
        return factors.get(0);

    }

    protected ConversionSpec getSource() {
        return source;
    }

    protected ConversionSpec getTarget() {
        return target;
    }

    public void setSource(ConversionSpec source) {
        this.source = source;
    }

    public void setTarget(ConversionSpec target) {
        this.target = target;
    }

    public void setConversionSource(ConversionFactorSource conversionSource) {
        this.conversionSource = conversionSource;
    }
}

