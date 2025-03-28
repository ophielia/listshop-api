package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.common.UnitType;
import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.SimpleConversionFactor;
import com.meg.listshop.conversion.data.pojo.ConversionTargetType;
import com.meg.listshop.conversion.service.ConversionContext;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Component
public class UnitToUnitScalingHandler extends AbstractScalingHandler {
    private static final Logger LOG = LoggerFactory.getLogger(UnitToUnitScalingHandler.class);


    public UnitToUnitScalingHandler() {

        setConversionSource(null);
        setScalerType(ConversionTargetType.List);
        setSkipNoConversionRequiredCheck(true);
        setScalarWeight(1);
    }


    @Override
    public List<ConversionFactor> findFactors(ConvertibleAmount toConvert, ConversionContext context) {

        List<ConversionFactor> resultList = new ArrayList<>();
        List<ConversionFactor> allUnitFactors = context.getUnitConversionFactors();
        ConversionFactor defaultFactor = allUnitFactors.stream()
                .filter(f -> f.isUnitDefault())
                .findFirst().orElse(null);
        String unitSize = context.getTargetUnitSize();
        String fromUnitSize = toConvert.getUnitSize();

        if (unitSize == null) {
            unitSize = defaultFactor.getUnitSize();
        }

        List<ConversionFactor> toConvertFactors = new ArrayList<>();
        ConversionFactor targetFactor = null;
        for (ConversionFactor factor : allUnitFactors) {
            if (factor.getFromUnit().getType() == UnitType.UNIT &&
                    factor.getFromUnit().getId().equals(toConvert.getUnit().getId()) &&
                    factor.getUnitSize() != null &&
                    factor.getUnitSize().equals(unitSize)) {
                //this is the target conversion factor
                targetFactor = factor;
                continue;
            }
            toConvertFactors.add(factor);
        }

        if (targetFactor == null) {
            return resultList;
        }

        HashMap<String, ConversionFactor> resultMap = new HashMap<>();
        for (ConversionFactor factor : toConvertFactors) {
            double conversionFactor = factor.getFactor() / targetFactor.getFactor();
            ConversionFactor newFactor = SimpleConversionFactor.conversionFactor(factor.getFromUnit(), targetFactor.getFromUnit(), conversionFactor, null, unitSize);
            resultMap.put(factor.getUnitSize(), newFactor);
        }
        if (resultMap.containsKey(fromUnitSize)) {
            resultList.add(resultMap.get(fromUnitSize));
            return resultList;
        }
        ConversionFactor newFactor = SimpleConversionFactor.conversionFactor(defaultFactor.getFromUnit(), defaultFactor.getFromUnit(), 1.0, null, defaultFactor.getUnitSize());
        resultList.add(newFactor);
        return resultList;
    }

    @Override
    public boolean doesScaleToUnit() {
        return true;
    }

    private List<ConversionFactor> returnDefaultFactor(List<ConversionFactor> factors) {
        ConversionFactor defaultFactor = factors.stream()
                .filter(ConversionFactor::isUnitDefault)
                .findFirst()
                .orElse(null);
        if (defaultFactor != null) {
            return Collections.singletonList(defaultFactor);
        } else if (!factors.isEmpty()) {
            return Collections.singletonList(factors.get(0));
        }
        return Collections.emptyList();
    }

    private List<ConversionFactor> reverseIfNecessary(List<ConversionFactor> factors) {
        List<ConversionFactor> reversedFactors = new ArrayList<>();
        for (ConversionFactor factor : factors) {
            if (factor.getFromUnit().getType().equals(UnitType.UNIT)) {
                // reverse
                reversedFactors.add(SimpleConversionFactor.reverseFactor(factor));
            } else {
                reversedFactors.add(factor);
            }
        }
        return reversedFactors;
    }


    public boolean scalerFor(ConversionContext context) {

        return context.shouldScaleToUnit() && context.isUnitToUnit();
    }
}

