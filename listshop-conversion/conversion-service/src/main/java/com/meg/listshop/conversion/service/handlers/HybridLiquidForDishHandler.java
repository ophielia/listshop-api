package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.ConversionFactorEntity;
import com.meg.listshop.conversion.data.pojo.UnitFlavor;
import com.meg.listshop.conversion.data.pojo.UnitSubtype;
import com.meg.listshop.conversion.data.pojo.UnitType;
import com.meg.listshop.conversion.data.repository.ConversionFactorRepository;
import com.meg.listshop.conversion.exceptions.ExceedsAllowedScaleException;
import com.meg.listshop.conversion.service.ConversionSpec;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.conversion.service.factors.ConversionFactorSource;
import com.meg.listshop.conversion.service.factors.SimpleConversionFactorSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.meg.listshop.conversion.data.repository.UnitSpecifications.matchingFromWithSpec;
import static org.springframework.data.jpa.domain.Specification.where;

@Component
public class HybridLiquidForDishHandler extends AbstractOneWayConversionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(HybridLiquidForDishHandler.class);


    @Autowired
    public HybridLiquidForDishHandler(ConversionFactorRepository factorRepository) {
        super();
        // make source from unit
        ConversionSpec source = ConversionSpec.basicSpec(UnitType.HYBRID, UnitSubtype.LIQUID);
        // make target
        ConversionSpec target = ConversionSpec.basicSpec(UnitType.HYBRID, UnitSubtype.LIQUID, UnitFlavor.DishUnit);

        // initialize conversionSource
        List<ConversionFactorEntity> factorEntities = factorRepository.findAll(where(matchingFromWithSpec(source)));
        List<ConversionFactor> factors = factorEntities.stream().map(f -> (ConversionFactor) f).collect(Collectors.toList());
        factors.addAll(selfScalingFactors(factors));
        ConversionFactorSource conversionSource = new SimpleConversionFactorSource(factors, true);

        // initialize in abstract
        setSource(source);
        setTarget(target);
        setConversionSource(conversionSource);
        setRestrictRange();
        setDoesScaling(true);
    }

    public void checkBestResult(ConvertibleAmount bestResult, ConversionSpec targetSpec) throws ExceedsAllowedScaleException {
        // we check if the scaled value resulted in a unit which is not a hybrid unit.
        // if this is true, we throw a ExceedsAllowedScaleException
        // This exception will be handled to converting to a different type.
        if (!bestResult.getUnit().getType().equals(targetSpec.getUnitType())) {
            String message = String.format("result unit [%s] does not match target unit [%s]", bestResult.getUnit(), targetSpec.getUnitType());
            LOG.info(message);
            throw new ExceedsAllowedScaleException(message);
        }
    }

    @Override
    public ConvertibleAmount sortForBestResult(List<ConvertibleAmount> convertedList) {
        // for hybrids, we want to return hybrid if possible, and otherwise
        // the equivalant as a larger measure.

        // <= 8 T. should return hybrid, and anything else non-hybrid

        // because fluid ounces are so close to teaspoons and tablespoons in size, we
        // can't count on the hybrids always being returned as the nearest element

        // so - we'll check for max hybrid size
        // if <= 8, we'll return that as the best amount
        List<ConvertibleAmount> onlyHybrids = convertedList.stream()
                .filter(ca -> ca.getUnit().getType().equals(UnitType.HYBRID))
                .collect(Collectors.toList());
        Double maxHybridQuantity = onlyHybrids.stream()
                .max(Comparator.comparing(ConvertibleAmount::getQuantity))
                .map(ConvertibleAmount::getQuantity)
                .orElse(null);
        if (maxHybridQuantity != null && maxHybridQuantity <= 8) {
            return super.sortForBestResult(onlyHybrids);
        }
        // otherwise, we'll return the standard sort
        return super.sortForBestResult(convertedList);
    }


}

