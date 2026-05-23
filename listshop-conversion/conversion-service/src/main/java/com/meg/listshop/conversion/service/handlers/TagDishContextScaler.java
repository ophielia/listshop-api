/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.common.UnitType;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.common.data.repository.UnitRepository;
import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.SimpleConversionFactor;
import com.meg.listshop.conversion.data.pojo.ConversionTargetType;
import com.meg.listshop.conversion.data.repository.ConversionFactorRepository;
import com.meg.listshop.conversion.data.repository.FactorCriteria;
import com.meg.listshop.conversion.data.repository.FactorCriteriaBuilder;
import com.meg.listshop.conversion.service.ConversionTarget;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.conversion.service.ProcessingContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Order(3)
public class TagDishContextScaler extends BaseScaleHandler {

    private ConversionFactorRepository conversionFactorRepository;
    private UnitRepository unitRepository;
    private Map<UnitType, ConversionFactor> gramsToDomain = new HashMap<>();

    public TagDishContextScaler(ConversionFactorRepository conversionFactorRepository,
                                UnitRepository unitRepository) {
        this.conversionFactorRepository = conversionFactorRepository;
        this.unitRepository = unitRepository;
    }

    public boolean shouldScale(ProcessingContext context) {
        return context.getCurrentAmount().getConversionId() != null &&
                context.getTarget().conversionContext() == ConversionTargetType.Dish &&
                context.getCurrentAmount().getUnit().getId().equals(GRAM_UNIT_ID);
    }

    public List<ConversionFactor> findFactors(ProcessingContext context) {
        ConvertibleAmount toConvert = context.getCurrentAmount();
        ConversionTarget target = context.getTarget();
        ConvertibleAmount originalAmount = context.getStartingAmount();

        // find factors with conversion id, marker match or null
        // from target domain and hybrid (if start is hybrid)
        boolean startIsHybrid = originalAmount.getUnit().getType() == UnitType.HYBRID;
        FactorCriteriaBuilder builder = new FactorCriteriaBuilder();
        builder.withConversionId(toConvert.getConversionId())
                .withToUnit(GRAM_UNIT_ID)
                .withFromContext(target.conversionContext())
                .withMarkerOrNull(originalAmount.getMarker())
                .withFromExcludeDomain(target.domainType());

        if (startIsHybrid) {
            builder = builder.withTypeIn(List.of(target.domainType(), UnitType.HYBRID));
        } else {
            builder = builder.withToDomain(target.domainType());
        }


        List<ConversionFactor> initialFactors = conversionFactorRepository.findFactors(builder.build());

        // marker match? hybrid exists?
        boolean markerMatch = false;
        boolean hybridExists = false;
        for (ConversionFactor factor : initialFactors) {
            markerMatch |= originalAmount.getMarker() != null &&
                    factor.getMarker() != null &&
                    factor.getMarker().equals(originalAmount.getMarker());
            hybridExists |= factor.getFromUnit().getType() == UnitType.HYBRID;
        }
        // if marker exists, limit to marker
        List<ConversionFactor> markerFactors = limitToMarkers(initialFactors, markerMatch, originalAmount.getMarker());
        // explode for domain
        List<ConversionFactor> explodedFactors = explodeFactors(markerFactors, context, hybridExists);
        // invert factors and return
        return explodedFactors.stream()
                .map(SimpleConversionFactor::reverseFactor)
                .toList();

    }

    private List<ConversionFactor> explodeFactors(List<ConversionFactor> markerFactors, ProcessingContext context, boolean hybridExists) {
        Map<Long, ConversionFactor> explodedFactors = new HashMap<>();
        UnitEntity gramUnit = unitRepository.findById(GRAM_UNIT_ID).orElse(null);
        // fill with existing
        for (ConversionFactor factor : markerFactors) {
            explodedFactors.put(factor.getFromUnit().getId(), factor);
        }
        // add self-referencing
        ConversionFactor selfReferencing = new SimpleConversionFactor(1.0, gramUnit, gramUnit, null, null, null);
        explodedFactors.put(GRAM_UNIT_ID, selfReferencing);
        if (hybridExists) {
            fillForDomain(explodedFactors, UnitType.HYBRID, gramUnit);
        }
        fillForDomain(explodedFactors, context.getTarget().domainType(), gramUnit);
        return explodedFactors.values().stream().toList();
    }

    private void fillForDomain(Map<Long, ConversionFactor> explodedFactors, UnitType unitType, UnitEntity gramUnit) {
        // pull first factor with domain
        ConversionFactor baseFactor = getBaseFactor(explodedFactors,unitType, gramUnit);
        // get factors by domain
        List<ConversionFactor> domainFactors = getDomainFactors(unitType, baseFactor);

        // add self scaling if not already there
        if (!explodedFactors.containsKey(baseFactor.getFromUnit().getId())) {
            explodedFactors.put(baseFactor.getFromUnit().getId(), baseFactor);
        }
        // calculate target factor for each domain factor which doesn't exist
        for (ConversionFactor factor : domainFactors) {
            if (explodedFactors.containsKey(factor.getToUnit().getId())) {
                continue;
            }
            double explodedFactor = baseFactor.getFactor() /  factor.getFactor()  ;
            ConversionFactor exploded = new SimpleConversionFactor(explodedFactor,gramUnit, factor.getToUnit(), baseFactor.getMarker(), null, null);
            explodedFactors.put(exploded.getFromUnit().getId(), exploded);
        }
    }

    private ConversionFactor getBaseFactor(Map<Long, ConversionFactor> explodedFactors, UnitType unitType, UnitEntity gramUnit) {
        ConversionFactor baseFactor = explodedFactors.values().stream()
                .filter(f -> f.getFromUnit().getType() == unitType)
                .filter( f -> !f.getFromUnit().isTagSpecific())
                .findFirst().orElse(null);
        if (baseFactor != null) {
            return baseFactor;
        }
        // if the unitType is not METRIC, we won't find a base factor
        // instead, we'll calculate from the gram factor
        if (!gramsToDomain.containsKey(unitType)) {
            FactorCriteriaBuilder builder = new FactorCriteriaBuilder();
            builder.withConversionId(null)
                    .withFromUnit(gramUnit)
                    .withToDomain(unitType)
                    .withToContext(ConversionTargetType.Dish);
            List<ConversionFactor> gramFactors = conversionFactorRepository.findFactors(builder.build());
            if (gramFactors.isEmpty() || gramFactors.size() < 2) {
                gramsToDomain.put(unitType, null);
            }
            ConversionFactor gramFactor = gramFactors.stream()
                    .filter(f-> !f.equals(1.0))
                    .findFirst().orElse(null);
            double factor = 1.0 / gramFactor.getFactor();
            ConversionFactor calculated = new SimpleConversionFactor(factor,gramUnit,gramFactor.getToUnit(), null, null, null);
            gramsToDomain.put(unitType, calculated);
        }
        return gramsToDomain.get(unitType);
    }

    private List<ConversionFactor> getDomainFactors(UnitType unitType, ConversionFactor baseFactor) {
        HashMap<UnitType, List<ConversionFactor>> domainFactorsByType = new HashMap<>();
        if (!domainFactorsByType.containsKey(unitType)) {
            List<ConversionFactor> conversionFactors = retrieveDomainFactorsByType(unitType);
            domainFactorsByType.put(unitType, conversionFactors);
        }
        // if the base factor is grams, the results should be inverted so that we have grams => unit
        // that way the explosion will be gram => kilogram => gram
        // - which is inverted at the end to be grams => kilogram
        if (baseFactor.getFromUnit().getId().equals(GRAM_UNIT_ID)) {
            return domainFactorsByType.get(unitType).stream()
                    .filter(f -> f.getFromUnit().getId().equals(baseFactor.getFromUnit().getId()))
                    .map(SimpleConversionFactor::reverseFactor)
                    .toList();
        } else {
            return domainFactorsByType.get(unitType).stream()
                    .filter(f -> f.getFromUnit().getId().equals(baseFactor.getFromUnit().getId()))
                    .toList();
        }
       /* return domainFactorsByType.get(unitType).stream()
                .filter(f -> f.getFromUnit().getId().equals(baseFactor.getFromUnit().getId()))
                .map(SimpleConversionFactor::reverseFactor)
                .toList();*/
    }

    private List<ConversionFactor> retrieveDomainFactorsByType(UnitType unitType) {
        FactorCriteriaBuilder builder = new FactorCriteriaBuilder();
        FactorCriteria criteria = builder.withFromType(unitType)
                .withToUnitType(unitType)
                .withFromContext(ConversionTargetType.Dish)
                .withConversionId(null)
                .build();
        return conversionFactorRepository.findFactors(criteria);
    }

    private List<ConversionFactor> limitToMarkers(List<ConversionFactor> initialFactors, boolean markerMatch, String marker) {
        if (markerMatch) {
            return initialFactors.stream()
                    .filter(f -> f.getMarker().equals(marker))
                    .toList();
        }
        return new ArrayList<>(initialFactors);
    }


}
