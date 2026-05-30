/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service.scaling;

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
@Order(4)
public class TagContextScaler extends BaseScaleHandler {

    private ConversionFactorRepository conversionFactorRepository;
    private UnitRepository unitRepository;
    private Map<String, ConversionFactor> metricToDomain = new HashMap<>();

    public TagContextScaler(ConversionFactorRepository conversionFactorRepository,
                            UnitRepository unitRepository) {
        this.conversionFactorRepository = conversionFactorRepository;
        this.unitRepository = unitRepository;
    }

    public boolean shouldScale(ProcessingContext context) {
        return context.getCurrentAmount().getConversionId() != null &&
                context.getTarget().conversionContext() != null &&
                context.getMetricMeasure() != null;
    }

@Override
    public double getQuantity(ProcessingContext context) {
        return context.getMetricMeasure();
    }

    public List<ConversionFactor> findFactors(ProcessingContext context) {
        ConvertibleAmount toConvert = context.getCurrentAmount();
        ConversionTarget target = context.getTarget();
        ConvertibleAmount originalAmount = context.getStartingAmount();

        // find factors with conversion id, marker match or null
        // from target domain and hybrid (if start is hybrid)
        Long targetUnitId = context.getMetricUnit().getId();
        boolean startIsHybrid = originalAmount.getUnit().getType() == UnitType.HYBRID;
        FactorCriteriaBuilder builder = new FactorCriteriaBuilder();
        builder.withConversionId(toConvert.getConversionId())
                .withToUnit(targetUnitId)
                .withFromContext(target.conversionContext())
                .withMarkerOrNull(originalAmount.getMarker())
                .withFromExcludeDomain(target.domainType());

        List<UnitType> typeList = new ArrayList<>();
        typeList.addAll(List.of(target.domainType(), UnitType.UNIT));
        if (startIsHybrid) {
            typeList.add(UnitType.HYBRID);
        }
        builder = builder.withTypeIn(typeList);


        List<ConversionFactor> initialFactors = conversionFactorRepository.findFactors(builder.build());

        // marker match? hybrid exists?
        boolean markerMatch = false;
        boolean sizesExist= false;
        boolean defaultSizeExists = false;
        boolean hybridExists = false;
        for (ConversionFactor factor : initialFactors) {
            markerMatch |= originalAmount.getMarker() != null &&
                    factor.getMarker() != null &&
                    factor.getMarker().equals(originalAmount.getMarker());
            hybridExists |= factor.getFromUnit().getType() == UnitType.HYBRID;
            sizesExist |= factor.getUnitSize() != null;
            defaultSizeExists |= factor.isUnitDefault() != null && factor.isUnitDefault();
        }
        // if marker exists, limit to marker
        List<ConversionFactor> markerFactors = limitToMarkers(initialFactors, markerMatch, originalAmount.getMarker());
        // if sizes exist, limit to marker
        List<ConversionFactor> sizeFactors = limitBySize(markerFactors, sizesExist, defaultSizeExists, originalAmount.getUnitSize());

        // explode for domain
        List<ConversionFactor> explodedFactors = explodeFactors(sizeFactors, context, hybridExists, targetUnitId);
        // invert factors and return
        return explodedFactors.stream()
                .map(SimpleConversionFactor::reverseFactor)
                .toList();

    }

    private List<ConversionFactor> explodeFactors(List<ConversionFactor> markerFactors, ProcessingContext context, boolean hybridExists, Long targetUnitId) {
        Map<Long, ConversionFactor> explodedFactors = new HashMap<>();
        UnitEntity targetUnit = unitRepository.findById(targetUnitId).orElse(null);
        // fill with existing
        for (ConversionFactor factor : markerFactors) {
            explodedFactors.put(factor.getFromUnit().getId(), factor);
        }
        // add self-referencing
        ConversionFactor selfReferencing = new SimpleConversionFactor(1.0, targetUnit, targetUnit, null, null, null);
        explodedFactors.put(GRAM_UNIT_ID, selfReferencing);
        if (hybridExists) {
            fillForDomain(explodedFactors, UnitType.HYBRID, targetUnit);
        }
        fillForDomain(explodedFactors, context.getTarget().domainType(), targetUnit);
        return explodedFactors.values().stream().toList();
    }

    private void fillForDomain(Map<Long, ConversionFactor> explodedFactors, UnitType unitType, UnitEntity targetUnit) {
        // pull first factor with domain
        ConversionFactor baseFactor = getBaseFactor(explodedFactors, unitType, targetUnit);
        // get factors by domain
        List<ConversionFactor> domainFactors = getDomainFactors(unitType, baseFactor, targetUnit);

        // add self scaling if not already there
        if (!explodedFactors.containsKey(baseFactor.getFromUnit().getId())) {
            explodedFactors.put(baseFactor.getFromUnit().getId(), baseFactor);
        }
        // calculate target factor for each domain factor which doesn't exist
        for (ConversionFactor factor : domainFactors) {
            if (explodedFactors.containsKey(factor.getToUnit().getId())) {
                continue;
            }
            double explodedFactor = baseFactor.getFactor() / factor.getFactor();
            ConversionFactor exploded = new SimpleConversionFactor(explodedFactor, targetUnit, factor.getToUnit(), baseFactor.getMarker(), null, null);
            explodedFactors.put(exploded.getFromUnit().getId(), exploded);
        }
    }

    private ConversionFactor getBaseFactor(Map<Long, ConversionFactor> explodedFactors, UnitType unitType, UnitEntity targetUnit) {
        ConversionFactor baseFactor = explodedFactors.values().stream()
                .filter(f -> f.getFromUnit().getType() == unitType)
                .filter(f -> !f.getFromUnit().isTagSpecific())
                .findFirst().orElse(null);
        if (baseFactor != null) {
            return baseFactor;
        }
        // if the unitType is not METRIC, we won't find a base factor
        // instead, we'll calculate from the metric factor
        String key = targetUnit.getId().toString() + unitType.toString();
        if (!metricToDomain.containsKey(key)) {
            FactorCriteriaBuilder builder = new FactorCriteriaBuilder();
            builder.withConversionId(null)
                    .withFromUnit(targetUnit)
                    .withToDomain(unitType)
                    .withToContext(ConversionTargetType.Dish); //MM come back and change this to dish or list
            List<ConversionFactor> gramFactors = conversionFactorRepository.findFactors(builder.build());
            if (gramFactors.isEmpty() || gramFactors.size() < 2) {
                metricToDomain.put(key, null);
            }
            ConversionFactor metricFactor = gramFactors.stream()
                    .filter(f -> !f.equals(1.0))
                    .findFirst().orElse(null);
            double factor = 1.0 / metricFactor.getFactor();
            ConversionFactor calculated = new SimpleConversionFactor(factor, targetUnit, metricFactor.getToUnit(), null, null, null);
            metricToDomain.put(key, calculated);
        }
        return metricToDomain.get(key);
    }

    private List<ConversionFactor> getDomainFactors(UnitType unitType, ConversionFactor baseFactor, UnitEntity targetUnit) {
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
                .withToDomain(unitType)
                .withFromContext(ConversionTargetType.Dish)
                .withConversionId(null)
                .build();
        return conversionFactorRepository.findFactors(criteria);
    }

    private List<ConversionFactor> limitToMarkers(List<ConversionFactor> initialFactors, boolean markerMatch, String marker) {
        if (markerMatch ) {
            return initialFactors.stream()
                    .filter(f -> f.getMarker() != null && f.getMarker().equals(marker))
                    .toList();
        }
        return new ArrayList<>(initialFactors);
    }

    private List<ConversionFactor> limitBySize(List<ConversionFactor> initialFactors, boolean sizesExist, boolean defaultSizeExists, String size) {
        if (sizesExist && size != null && size.trim().length() > 0) {
            return initialFactors.stream()
                    .filter(f -> notSingleUnit(f) || sizeMatches(f, size))
                    .toList();
        } else if (defaultSizeExists) {
            return initialFactors.stream()
                    .filter(f -> notSingleUnit(f) ||
                            (!notSingleUnit(f) && f.isUnitDefault()))
                    .toList();
        }
        return new ArrayList<>(initialFactors);
    }

    private boolean sizeMatches(ConversionFactor f, String size) {
        return f.getUnitSize() != null && f.getUnitSize().equals(size);
    }

    private boolean notSingleUnit(ConversionFactor f) {
        return !f.getFromUnit().getId().equals(SINGLE_UNIT_ID);
    }


}
