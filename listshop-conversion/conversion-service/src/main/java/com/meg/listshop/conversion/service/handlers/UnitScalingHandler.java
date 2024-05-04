package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.SimpleConversionFactor;
import com.meg.listshop.conversion.data.pojo.ConversionTargetType;
import com.meg.listshop.conversion.data.pojo.UnitType;
import com.meg.listshop.conversion.service.ConversionContext;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class UnitScalingHandler extends AbstractScalingHandler {
    private static final Logger LOG = LoggerFactory.getLogger(UnitScalingHandler.class);


    public UnitScalingHandler() {

        setConversionSource(null);
        setScalerType(ConversionTargetType.List);
        setSkipNoConversionRequiredCheck(true);
    }


    @Override
    public List<ConversionFactor> findFactors(ConvertibleAmount toConvert, ConversionContext context) {
        List<ConversionFactor> factors = reverseIfNecessary(context.getUnitConversionFactors());
        if (factors.isEmpty()) {
            return new ArrayList<>();
        }
        if (factors.size() == 1) {
            return factors;
        }
        // we have more than one factor. Look for marker match
        String markerToFind = toConvert.getMarker();
        Map<String, ConversionFactor> markerMap = new HashMap<>();
        for (ConversionFactor factor : factors) {
            if (markerMap.containsKey(factor.getMarker())) {
                continue;
            }
            markerMap.put(factor.getMarker(), factor);
        }
        if (markerMap.containsKey(markerToFind)) {
            return Collections.singletonList(markerMap.get(markerToFind));
        }
        if (markerMap.containsKey("medium")) {
            return Collections.singletonList(markerMap.get("medium"));
        }
        // no direct match, medium isn't there.  Just return the first one
        return Collections.singletonList(factors.get(0));

        // to make the integrals right - need
        // * fix the db
        // * way to pass the prefered integral size in (part of ConversionRequest)
        // * factors need to be saved with integrals
        // * this method needs to pull preferred first, then medium...
        // *    otherwise - all, for dynamic size change when scaling
        // *                or first, for fewer changes, less predictability
        // * integral returned in result
        // * integral saved in dish item - list item
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
}

