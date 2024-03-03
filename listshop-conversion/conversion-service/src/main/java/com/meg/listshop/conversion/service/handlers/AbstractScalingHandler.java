package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.SimpleConversionFactor;
import com.meg.listshop.conversion.data.pojo.ConversionContextType;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.service.ConversionSpec;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.conversion.service.factors.ConversionFactorSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractScalingHandler extends AbstractConversionHandler implements ScalingHandler {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractScalingHandler.class);
    private static final double DEFAULT_MIN_RANGE = 0.4990;
    private static final double DEFAULT_MAX_RANGE = 500;

    private ConversionContextType scalerType;

    private ConversionSpec source;
    private ConversionSpec target;

    private double minRange = DEFAULT_MIN_RANGE;
    private double maxRange = DEFAULT_MAX_RANGE;

    protected AbstractScalingHandler(ConversionSpec source, ConversionSpec target, ConversionFactorSource conversionSource, ConversionContextType scalingType) {
        super(source, target, conversionSource);
        this.scalerType = scalingType;
    }

    protected AbstractScalingHandler() {

    }

    @Override
    public ConvertibleAmount sortForBestResult(List<ConvertibleAmount> convertedList) {
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
        ConvertibleAmount nearestUnitResult = convertedList.get(0);
            ConvertibleAmount best = convertedList.stream()
                    .filter(a -> a.getQuantity() >= minRange && a.getQuantity() <= maxRange)
                    .findFirst().orElse(null);
            if (best!=null) {
                return best;
            }
        return nearestUnitResult;
    }

    public boolean scalerFor(ConversionContextType listOrDish) {
        return listOrDish.equals(scalerType);
    }

    public ConvertibleAmount scale(ConvertibleAmount amount) throws ConversionFactorException {
        ConversionSpec targetSpec = ConversionSpec.basicSpec(amount.getUnit().getType(),
                amount.getUnit().getSubtype());
        return convert(amount, targetSpec);
    }
    protected List<ConversionFactor> selfScalingFactors(List<ConversionFactor> factors) {
        return factors.stream()
                .map(ConversionFactor::getToUnit)
                .distinct()
                .map(SimpleConversionFactor::passThroughFactor)
                .collect(Collectors.toList());
    }

    @Override
    public ConversionSpec getSource() {
        return source;
    }

    @Override
    public ConversionSpec getTarget() {
        return target;
    }

    @Override
    public void setSource(ConversionSpec source) {
        this.source = source;
    }

    @Override
    public void setTarget(ConversionSpec target) {
        this.target = target;
    }

    public ConversionContextType getScalerType() {
        return scalerType;
    }

    public void setScalerType(ConversionContextType scalerType) {
        this.scalerType = scalerType;
    }
}

