package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.pojo.ConversionTargetType;
import com.meg.listshop.conversion.service.ConversionContext;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class UnitScalingHandler extends AbstractScalingHandler {
    private static final Logger LOG = LoggerFactory.getLogger(UnitScalingHandler.class);


    @Autowired
    public UnitScalingHandler(List<ConversionFactor> scalingFactors) {

        setConversionSource(null);
        setScalerType(ConversionTargetType.List);
        setSkipNoConversionRequiredCheck(true);
    }


    @Override
    public List<ConversionFactor> findFactors(ConvertibleAmount toConvert, ConversionContext context) {
        List<ConversionFactor> factors = context.getUnitConversionFactors();
        if (factors == null || factors.isEmpty()) {
            return new ArrayList<>();
        }
        if (factors.size() == 1) {
            return factors;
        }
        // we have more than one factor. Look for marker match
        String markerToFind = toConvert.getMarker();
        Map<String, ConversionFactor> markerMap = factors.stream()
                .collect(Collectors.toMap(ConversionFactor::getMarker, Function.identity()));
        if (markerMap.containsKey(markerToFind)) {
            return Collections.singletonList(markerMap.get(markerToFind));
        }
        if (markerMap.containsKey("medium")) {
            return Collections.singletonList(markerMap.get("medium"));
        }
        // no direct match, medium isn't there.  Just return the first one
        return Collections.singletonList(factors.get(0));
    }
}

