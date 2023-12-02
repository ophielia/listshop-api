package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.SimpleConversionFactor;
import com.meg.listshop.conversion.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.ConversionSortType;
import com.meg.listshop.conversion.data.pojo.SimpleAmount;
import com.meg.listshop.conversion.data.pojo.UnitFlavor;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.service.ConversionSpec;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.conversion.service.factors.ConversionFactorSource;
import com.meg.listshop.conversion.tools.ConversionTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractConversionHandler implements ConversionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractConversionHandler.class);

    private ConversionSpec source;
    private ConversionSpec target;
    private ConversionFactorSource conversionSource;

    private boolean doesScaling = false;

    private ConversionSortType sortType = ConversionSortType.NEAREST_UNIT;

    protected AbstractConversionHandler(ConversionSpec source, ConversionSpec target, ConversionFactorSource conversionSource) {
        this.source = source;
        this.target = target;
        this.conversionSource = conversionSource;
    }

    protected AbstractConversionHandler() {

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

        // convert all factors, making list
        List<ConvertibleAmount> convertedList = factors.stream()
                .map(f -> {
                    double newQuantity = toConvert.getQuantity() * f.getFactor();
                    UnitEntity newUnit = f.getToUnit();

                    return new SimpleAmount(newQuantity, newUnit);
                }).collect(Collectors.toList());

        // sort for best result, according to sort type
        ConvertibleAmount bestResult = sortForBestResult(convertedList);
        // return best result


        return new SimpleAmount(bestResult.getQuantity(), bestResult.getUnit(), toConvert);
    }

    private ConvertibleAmount sortForBestResult(List<ConvertibleAmount> convertedList) {
        if (convertedList.size() == 1) {
            return convertedList.get(0);
        }
        switch (sortType) {
            case NEAREST_UNIT:
                return sortForNearestUnit(convertedList);
            case RANGE:
                return sortForRange(convertedList);
            default:
                return sortForNearestUnit(convertedList);
        }

    }

    private ConvertibleAmount sortForNearestUnit(List<ConvertibleAmount> convertedList) {
        Comparator<ConvertibleAmount> comparator = (f1, f2) -> {
            Double f1ToOne = Math.abs(1 - (f1.getQuantity()));
            Double f2ToOne = Math.abs(1 - (f2.getQuantity()));
            return f1ToOne.compareTo(f2ToOne);
        };

        convertedList.sort(comparator);
        return convertedList.get(0);
    }

    private ConvertibleAmount sortForRange(List<ConvertibleAmount> convertedList) {
        Comparator<ConvertibleAmount> compareByQuantity = Comparator.comparing(ConvertibleAmount::getQuantity);
        convertedList.sort(compareByQuantity);

        ConvertibleAmount best = convertedList.stream()
                .filter(a -> a.getQuantity() >= 0.5 && a.getQuantity() <= 500.0)
                .findFirst().orElse(null);

        if (best != null) {
            return best;
        }

        best = convertedList.stream()
                .filter(a -> a.getQuantity() >= 0.125 && a.getQuantity() <= 800.0)
                .findFirst().orElse(null);

        if (best != null) {
            return best;
        }

        return sortForNearestUnit(convertedList);
    }

    private boolean doesntRequireConversion(ConvertibleAmount toConvert, ConversionSpec targetSpec) {
        if (doesScaling) {
            return false;
        }
        boolean specMatches = targetSpec.matches(toConvert.getUnit());
        if (!specMatches) {
            return false;
        }
        if (targetSpec.getUnitId() != null) {
            return targetSpec.getUnitId().equals(toConvert.getUnit().getId());
        }
        return true;
    }

    protected List<ConversionFactor> selfScalingFactors(List<ConversionFactor> factors, UnitFlavor flavor) {
        Set<ConversionFactor> destinationFactors = factors.stream()
                .map(ConversionFactor::getToUnit)
                .filter(u -> ConversionTools.hasFlavor(u, flavor))
                .map(SimpleConversionFactor::passThroughFactor)
                .collect(Collectors.toSet());
        return new ArrayList<>(destinationFactors);
    }

    public ConversionSpec getSource() {
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

    public void setSortType(ConversionSortType sortType) {
        this.sortType = sortType;
    }

    public void setDoesScaling(boolean doesScaling) {
        this.doesScaling = doesScaling;
    }
}

