/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.common.UnitSubtype;
import com.meg.listshop.common.UnitType;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.common.data.repository.UnitRepository;
import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.ConversionUnitFactorEntity;
import com.meg.listshop.conversion.data.pojo.SimpleAmount;
import com.meg.listshop.conversion.data.repository.FactorCriteria;
import com.meg.listshop.conversion.data.repository.FactorCriteriaBuilder;
import com.meg.listshop.conversion.data.repository.ConversionFactorRepository;
import com.meg.listshop.conversion.data.repository.TagFactorRepository;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.conversion.service.ProcessingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class AbstractTagHandler implements TagHandler {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractTagHandler.class);

    @Value("${listshop.single.unit.id:1011}")
    protected Long SINGLE_UNIT_ID;

    @Value("${conversionservice.gram.unit.id:1013}")
    protected Long GRAM_UNIT_ID;

    @Autowired
    protected ConversionFactorRepository factorRepository;

    @Autowired
    protected TagFactorRepository tagFactorRepository;

    @Autowired
    protected UnitRepository unitRepository;

    protected ConvertibleAmount convertToGrams(@NonNull ProcessingContext context) {
        ConvertibleAmount toConvert = context.getCurrentAmount();
        List<ConversionFactor> factors = findFactorsForGrams(toConvert);
        if (factors == null || factors.isEmpty()) {
            LOG.debug("No conversions available for domainConversion from unit [{}] to unitType: [{}].", toConvert.getUnit(), context.getTarget().domainType());
            return toConvert;
        }

        // convert all factors, making list
        List<ConvertibleAmount> convertedList = factors.stream()
                .map(f -> {
                    double newQuantity = toConvert.getQuantity() * f.getFactor();
                    UnitEntity newUnit = f.getToUnit();

                    return new SimpleAmount(newQuantity, newUnit, f.getUnitSize());
                }).collect(Collectors.toList());


        // return first result
        if (convertedList.isEmpty()) {
            return toConvert;

        }
        ConvertibleAmount converted = convertedList.get(0);

        return new SimpleAmount(converted.getQuantity(), converted.getUnit(), toConvert, converted.getUnitSize());
    }

    public abstract List<ConversionFactor> findFactorsForGrams(ConvertibleAmount toConvert);

    protected List<ConversionFactor> originalFindFactors(ConvertibleAmount toConvert, ProcessingContext context) {
        // create criteria - base criteria
        FactorCriteriaBuilder criteriaBuilder = new FactorCriteriaBuilder()
                .withFromUnit(toConvert.getUnit())
                .withConversionId(context.getCurrentAmount().getConversionId());
        List<ConversionFactor> exactResult;
        if (targetIsSingleUnit(context)) {
            // we're converting to single units
            boolean targetDefaultSize = context.getTarget().unitSize() == null;
            String requestedSize = targetDefaultSize ? null : context.getTarget().unitSize();
            criteriaBuilder.withBridgeToUnits(true)
                    .withTargetDefaultSize(targetDefaultSize)
                    .withToSize(requestedSize)
                    .withToUnit(SINGLE_UNIT_ID);


            exactResult = factorRepository.findAllFactors(criteriaBuilder.build()).stream()
                    .map(factor -> (ConversionFactor) factor)
                    .toList();
        } else {
            // converting to metric, to be converted and scaled down the line
            criteriaBuilder.withToDomain(UnitType.METRIC)
                    .withTargetDefaultUnit(true);
            exactResult = factorRepository.findAllFactors(criteriaBuilder.build()).stream()
                    .map(factor -> (ConversionFactor) factor)
                    .toList();
        }

        // retrieve factors - exact
        FactorCriteria criteria = criteriaBuilder.build();

        if (!exactResult.isEmpty()) {
            return exactResult;
        }

        // return best effort factors (potentially ignoring size and modifiers)
        return bestEffortFactors(context, criteria);
    }

    private List<ConversionFactor> bestEffortFactors(ProcessingContext context, FactorCriteria criteria) {
        if (!FactorRequestUtils.isSizeOrModifierRequest(context)) {
            return new ArrayList<>();
        }
        // get all factors matching units (and domains) only
        FactorCriteria generalCriteria = FactorCriteria.from(criteria);
        generalCriteria.setFromSize(null);
        generalCriteria.setFromModifier(null);
        generalCriteria.setToSize(null);
        generalCriteria.setToModifier(null);
        List<ConversionUnitFactorEntity> factors = tagFactorRepository.findAllFactors(generalCriteria);

        List<ConversionUnitFactorEntity> fromFactors = bestEffortFromFactors(factors, context);
        return bestEffortToFactors(fromFactors, context).stream()
                .map(f -> (ConversionFactor) f)
                .toList();

    }

    private List<ConversionUnitFactorEntity> bestEffortFromFactors(List<ConversionUnitFactorEntity> factors, ProcessingContext context) {
        //MM factor getters not pointing to the right place - just trying to complete a sketch here
        // to sum up - from factors will return exact matches for size and marker.
        // it also returns factors for size and marker if size or marker is requested, but no size are markers are available in the factors
        // finally, if size factors are found, but no matches for marker, size factors are returned.  In other words, the marker is ignored if
        // the size matches.

        //MM since then, thoughts on a better way to do this - but, IIABDFI

        //MM actually - not sure this is used
        List<ConversionUnitFactorEntity> fromFactors = new ArrayList<>();
        // match by size
        boolean sizeRequested = FactorRequestUtils.fromSizeRequested(context);
        // sizes for from
        boolean sizesAvailable = factors.stream().anyMatch(f -> f.getFromUnitSize() != null && !f.getFromUnitSize().isEmpty());
        if (sizeRequested && sizesAvailable) {
            // match by size
            List<ConversionUnitFactorEntity> sizeFactors = factors.stream()
                    .filter(fromUnitSizeFilter(context))
                    .toList();
            fromFactors.addAll(sizeFactors);
        }
        if (sizeRequested && !sizesAvailable) {
            // return all factors
            fromFactors.addAll(factors);
        }

        // return if empty after size search
        if (fromFactors.isEmpty()) {
            return fromFactors;
        }

        // modifiers
        boolean modifierRequested = FactorRequestUtils.fromModifierRequested(context);
        //MM note - will need to fix factor getter
        boolean modifiersAvailable = factors.stream().anyMatch(f -> f.getFromMarker() != null && !f.getFromMarker().isEmpty());

        if (!modifierRequested || !modifiersAvailable) {
            return fromFactors;
        }

        List<ConversionUnitFactorEntity> modifierFactors = fromFactors.stream()
                .filter(fromMarkerFilter(context))
                .toList();

        // if modifier factors available, return, otherwise return from factors
        return modifierFactors.isEmpty() ? fromFactors : modifierFactors;
    }


    private List<ConversionUnitFactorEntity> bestEffortToFactors(List<ConversionUnitFactorEntity> factors, ProcessingContext context) {
        List<ConversionUnitFactorEntity> toFactors = new ArrayList<>();
        // match by size
        boolean sizeRequested = FactorRequestUtils.toSizeRequested(context);
        // sizes for to
        boolean sizesAvailable = factors.stream().anyMatch(f -> f.getToUnitSize() != null && !f.getToUnitSize().isEmpty());
        if (sizeRequested && sizesAvailable) {
            // match by size
            List<ConversionUnitFactorEntity> sizeFactors = factors.stream()
                    .filter(toUnitSizeFilter(context))
                    .toList();
            toFactors.addAll(sizeFactors);
        }
        if (sizeRequested && !sizesAvailable) {
            // match by default size
            List<ConversionUnitFactorEntity> sizeFactors = factors.stream()
                    .filter(toDefaultUnitSizeFilter(context))
                    .toList();
            toFactors.addAll(sizeFactors);
        }

        // return if empty after size search
        if (toFactors.isEmpty()) {
            return toFactors;
        }

        // modifiers
        boolean modifierRequested = FactorRequestUtils.toModifierRequested(context);
        boolean modifiersAvailable = factors.stream().anyMatch(f -> f.getToMarker() != null && !f.getToMarker().isEmpty());

        if (!modifierRequested || !modifiersAvailable) {
            return toFactors;
        }

        List<ConversionUnitFactorEntity> modifierFactors = toFactors.stream()
                .filter(toMarkerFilterOrNull(context))
                .toList();

        // if modifier factors available, return, otherwise return from factors
        return modifierFactors.isEmpty() ? toFactors : modifierFactors;
    }

    private Predicate<? super ConversionUnitFactorEntity> toMarkerFilterOrNull(ProcessingContext context) {
        String requestedMarker = context.getTarget().marker();
        return factor -> {
            if (factor.getToMarker() != null) {
                return Objects.equals(factor.getToMarker(), requestedMarker) || factor.getToMarker().isEmpty();
            }
            return true;
        };
    }

    private Predicate<? super ConversionUnitFactorEntity> toUnitSizeFilter(ProcessingContext context) {
        String requestedSize = context.getTarget().unitSize();
        return factor -> {
            if (requestedSize != null) {
                return Objects.equals(factor.getToUnitSize(), requestedSize);
            }
            return false;
        };
    }

    private Predicate<? super ConversionUnitFactorEntity> toDefaultUnitSizeFilter(ProcessingContext context) {
        return factor -> {
            return factor.isToUnitDefault();

        };
    }

    private Predicate<ConversionUnitFactorEntity> fromUnitSizeFilter(ProcessingContext context) {
        String requestedSize = context.getCurrentAmount().getUnitSize();
        return factor -> {
            if (requestedSize != null) {
                return Objects.equals(factor.getFromUnitSize(), requestedSize);
            }
            return false;
        };
    }

    private Predicate<ConversionUnitFactorEntity> fromMarkerFilter(ProcessingContext context) {
        String marker = context.getCurrentAmount().getMarker();
        return factor -> {
            if (marker != null) {
                return Objects.equals(factor.getFromMarker(), marker);
            }
            return false;
        };
    }


    protected boolean targetIsSingleUnit(ProcessingContext context) {
        return context.getTarget().unitId() != null && Objects.equals(context.getTarget().unitId(), SINGLE_UNIT_ID);
    }

    protected boolean fromIsSingleUnit(ProcessingContext context) {
        return context.getTarget().unitId() != null && Objects.equals(context.getTarget().unitId(), SINGLE_UNIT_ID);
    }

    protected boolean fromIsHybrid(ProcessingContext context) {
        return context.getCurrentAmount().getUnit().getType() == UnitType.HYBRID;
    }

    protected boolean toIsWeight(ProcessingContext context) {
        Long unitId = context.getTarget().unitId();
        if (unitId == null) {
            return false;
        }
        UnitEntity unit = unitRepository.findById(unitId).orElse(null);
        if (unit == null) {
            return false;
        }
        return unit.getSubtype().equals(UnitSubtype.WEIGHT);
    }
}


