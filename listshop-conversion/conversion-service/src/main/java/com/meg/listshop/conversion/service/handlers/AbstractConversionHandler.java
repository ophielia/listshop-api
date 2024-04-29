package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.SimpleAmount;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.service.ConversionContext;
import com.meg.listshop.conversion.service.ConversionSpec;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.conversion.service.factors.ConversionFactorSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractConversionHandler implements ConversionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractConversionHandler.class);

    private ConversionSpec source;
    private ConversionSpec target;
    private ConversionFactorSource conversionSource;

    private boolean skipNoConversionRequiredCheck = false;

    private boolean doesScaling = false;

    protected AbstractConversionHandler(ConversionSpec source, ConversionSpec target, ConversionFactorSource conversionSource) {
        this.source = source;
        this.target = target;
        this.conversionSource = conversionSource;
    }

    protected AbstractConversionHandler() {

    }

    public boolean handles(ConversionSpec sourceSpec, ConversionSpec targetSpec) {
        return (source.equals(sourceSpec) && target.equals(targetSpec)) ||
                (target.equals(sourceSpec) && source.equals(targetSpec));
    }


    public ConvertibleAmount convert(ConvertibleAmount toConvert, ConversionContext context) throws ConversionFactorException {
        List<ConversionFactor> factors = findFactors(toConvert,context);
        if (factors == null || factors.isEmpty()) {
            LOG.debug("No conversion required for unitType: [{}], amount [{}].", context.getTargetUnitType(), toConvert);
            return toConvert;
        }

        // convert all factors, making list
        List<ConvertibleAmount> convertedList = factors.stream()
                .map(f -> {
                    double newQuantity = toConvert.getQuantity() * f.getFactor();
                    UnitEntity newUnit = f.getToUnit();

                    return new SimpleAmount(newQuantity, newUnit);
                }).collect(Collectors.toList());

        // sort for best result, according to sort type
        ConvertibleAmount bestResult = sortForBestResult(convertedList);
        if (bestResult == null ) {
            bestResult = toConvert;
        }

        // return best result
        return new SimpleAmount(bestResult.getQuantity(), bestResult.getUnit(), toConvert);
    }

    public List<ConversionFactor> findFactors(ConvertibleAmount toConvert , ConversionContext context) {
        if (!isSkipNoConversionRequiredCheck() && !doesScaling && context.doesntRequireConversion(toConvert)) {
            LOG.debug("No conversion required for unitType: [{}], amount [{}].", context.getTargetUnitType(), toConvert);
            return null;
        }

        List<ConversionFactor> factors = new ArrayList<>();
        boolean isOneWayConversion = toConvert.getUnit().isOneWayConversion();
        if (context.convertsToSpecificUnit(toConvert)) {
            ConversionFactor exact = conversionSource.getFactor(toConvert.getUnit().getId(), context.getTargetUnitId(), isOneWayConversion);
            if (exact != null) {
                LOG.trace("...exact match found for unitId: [{}], found [{}].", context.getTargetUnitId(), exact.getToUnit().getId());
                factors.add(exact);
                return factors;
            }
        }

            factors.addAll(conversionSource.getFactors( toConvert, context.getConversionId(), isOneWayConversion ));


        if (factors.isEmpty()) {
            String message = String.format("No factors found in handler %s.", this.getClass().getName());
            LOG.warn(message);
        }

        return factors;
    }

    public ConvertibleAmount sortForBestResult(List<ConvertibleAmount> convertedList) {
        if (convertedList.isEmpty()) {
            return null;
        }
        if (convertedList.size() == 1) {
            return convertedList.get(0);
        }
        // sort for nearest unit first, then weed for range if required
        Comparator<ConvertibleAmount> comparator = (f1, f2) -> {
            Double f1ToOne = Math.abs(1 - (f1.getQuantity()));
            Double f2ToOne = Math.abs(1 - (f2.getQuantity()));
            return f1ToOne.compareTo(f2ToOne);
        };

        convertedList.sort(comparator);
        return convertedList.get(0);
    }

    public ConversionSpec getSource() {
        return source;
    }

    public ConversionSpec getTarget() {
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

    public boolean isSkipNoConversionRequiredCheck() {
        return skipNoConversionRequiredCheck;
    }

    public void setSkipNoConversionRequiredCheck(boolean skipNoConversionRequiredCheck) {
        this.skipNoConversionRequiredCheck = skipNoConversionRequiredCheck;
    }
}

