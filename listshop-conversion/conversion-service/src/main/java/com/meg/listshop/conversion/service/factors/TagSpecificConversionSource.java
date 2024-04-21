package com.meg.listshop.conversion.service.factors;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.ConversionFactorEntity;
import com.meg.listshop.conversion.data.entity.SimpleConversionFactor;
import com.meg.listshop.conversion.data.pojo.UnitSubtype;
import com.meg.listshop.conversion.data.pojo.UnitType;
import com.meg.listshop.conversion.data.repository.ConversionFactorRepository;
import com.meg.listshop.conversion.service.ConversionSpec;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static com.meg.listshop.conversion.data.repository.UnitSpecifications.*;
import static org.springframework.data.jpa.domain.Specification.where;

public class TagSpecificConversionSource extends AbstractConversionFactorSource {
    private static final Logger LOG = LoggerFactory.getLogger(TagSpecificConversionSource.class);


    // a list of factors which convert tablespoons/teaspoons (HYBRID) to cups
    private final List<ConversionFactorEntity> hybridInflationFactors;

    private final List<ConversionFactorEntity> metricInflationFactors;

    private final ConversionFactorRepository factorRepository;

    public TagSpecificConversionSource(ConversionFactorRepository factorRepository) {
        super(new ArrayList<>(), false);

        this.factorRepository = factorRepository;

        ConversionSpec inflationSource = ConversionSpec.basicSpec(UnitType.HYBRID, UnitSubtype.SOLID);  // teaspoons , tablespoons , cups
        hybridInflationFactors = factorRepository.findAll(where(matchingFromWithSpecGenericOnly(inflationSource).and(matchingToWithSpecGenericOnly(inflationSource))));

        ConversionSpec metricInflationSource = ConversionSpec.basicSpec(UnitType.METRIC, UnitSubtype.WEIGHT);  // grams, kilogram
        metricInflationFactors = factorRepository.findAll(where(matchingFromWithSpec(metricInflationSource).and(matchingToWithSpec(metricInflationSource))));

    }

    public List<ConversionFactor> getAllPossibleFactors(ConvertibleAmount convertibleAmount, Long conversionId) {
        LOG.trace("... getting all possiblefactors from db for conversionId: [{}], unitId [{}]", conversionId, convertibleAmount.getUnit().getId());

        // get factors from database for conversion id
        List<ConversionFactor> tagFactors = factorRepository.findAll(where(matchingFromConversionId(conversionId))).stream()
                .map(f -> (ConversionFactor) f)
                .collect(Collectors.toList());

        // if no factors found return empty list
        if (tagFactors.isEmpty()) {
            return new ArrayList<>();
        }
        return tagFactors;
    }


    @Override
    public List<ConversionFactor> getFactors(ConvertibleAmount convertibleAmount, Long conversionId) {
        Long unitId = convertibleAmount.getUnit().getId();
        LOG.trace("... getting factors from db for conversionId: [{}], unitId [{}]", conversionId, unitId);

        // get factors from database for conversion id
        List<ConversionFactor> tagFactors = factorRepository.findAll(where(matchingFromConversionId(conversionId))).stream()
                .map(f -> (ConversionFactor) f)
                .collect(Collectors.toList());

        // if no factors found return empty list
        if (tagFactors.isEmpty()) {
            return new ArrayList<>();
        }

        // determine if this is inverted - going from grams to hybrid
        // needs work - this isn't working now inverted would be unitid (grams) = to unit
        // - and they all have grams as the to unit
        boolean isInverted = metricInflationFactors.stream().anyMatch(f -> f.getFromUnit().getId().equals(unitId));
        if (isInverted) {
            tagFactors = reverseTagFactors(tagFactors);
        }

        // create hashmap from tag factors
        // and check if generic hybrid is present (which would mean inflating makes sense)
        boolean genericHybridPresent = false;
        Map<ConversionFactorKey, ConversionFactor> resultMap = new HashMap<>();
        for (ConversionFactor f : tagFactors) {
            genericHybridPresent |= !f.getFromUnit().isTagSpecific();
            ConversionFactorKey key = new ConversionFactorKey(f.getFromUnit().getId(), f.getMarker());
            resultMap.put(key, f);
        }

        // if we have an exact match, return it now
        ConversionFactorKey exactMatch = new ConversionFactorKey(unitId, convertibleAmount.getMarker());
        ConversionFactorKey nullMarkerMatch = new ConversionFactorKey(unitId, null);
        if (resultMap.containsKey(exactMatch)) {
            return Collections.singletonList(resultMap.get(exactMatch));
        }

        // check for unit match without marker
        List<ConversionFactor> unitMatches = getUnitMatches(resultMap, unitId);
        if (!unitMatches.isEmpty()) {
            return unitMatches;
        }

        // amplify database factors to all factors possible for HYBRIDS
        if (genericHybridPresent) {
            final List<ConversionFactorEntity> inflationFactors = getInflationFactors(isInverted);
            // go through tagFactors
            for (ConversionFactor factor : tagFactors) {
                if (!factor.getFromUnit().isTagSpecific()) {
                    inflationFactors.stream()
                            .filter(f -> f.getToUnit().getId().equals(factor.getFromUnit().getId())) // get all conversions from base factor
                            .forEach(f -> resultMap.computeIfAbsent(new ConversionFactorKey(f.getFromUnit().getId(), f.getMarker()),
                                    tc -> inflateFactorFromBase(factor, f)));
                }
            }
        }
        // maybe we have an exact match now
        if (resultMap.containsKey(exactMatch)) {
            return Collections.singletonList(resultMap.get(exactMatch));
        }

        // or a unit match, where the marker is null
        if (resultMap.containsKey(nullMarkerMatch)) {
            return Collections.singletonList(resultMap.get(nullMarkerMatch));
        }

        // if not, check for unit match without marker
        unitMatches = getUnitMatches(resultMap, unitId);
        if (!unitMatches.isEmpty()) {
            return unitMatches;
        }
        return new ArrayList<>();
    }

    public List<ConversionFactor> selectFactorsForConversion(ConvertibleAmount convertibleAmount, List<ConversionFactor> tagFactors) {
        Long unitId = convertibleAmount.getUnit().getId();
        LOG.trace("... selecting factors from list : [{}], unitId [{}]", tagFactors, unitId);

        // determine if this is inverted - going from grams to hybrid
        // needs work - this isn't working now inverted would be unitid (grams) = to unit
        // - and they all have grams as the to unit
        boolean isInverted = metricInflationFactors.stream().anyMatch(f -> f.getFromUnit().getId().equals(unitId));
        if (isInverted) {
            tagFactors = reverseTagFactors(tagFactors);
        }

        // create hashmap from tag factors
        // and check if generic hybrid is present (which would mean inflating makes sense)
        boolean genericHybridPresent = false;
        Map<ConversionFactorKey, ConversionFactor> resultMap = new HashMap<>();
        for (ConversionFactor f : tagFactors) {
            genericHybridPresent |= !f.getFromUnit().isTagSpecific();
            ConversionFactorKey key = new ConversionFactorKey(f.getFromUnit().getId(), f.getMarker());
            resultMap.put(key, f);
        }

        // if we have an exact match, return it now
        ConversionFactorKey exactMatch = new ConversionFactorKey(unitId, convertibleAmount.getMarker());
        ConversionFactorKey nullMarkerMatch = new ConversionFactorKey(unitId, null);
        if (resultMap.containsKey(exactMatch)) {
            return Collections.singletonList(resultMap.get(exactMatch));
        }

        // check for unit match without marker
        List<ConversionFactor> unitMatches = getUnitMatches(resultMap, unitId);
        if (!unitMatches.isEmpty()) {
            return unitMatches;
        }

        // amplify database factors to all factors possible for HYBRIDS
        if (genericHybridPresent) {
            final List<ConversionFactorEntity> inflationFactors = getInflationFactors(isInverted);
            // go through tagFactors
            for (ConversionFactor factor : tagFactors) {
                if (!factor.getFromUnit().isTagSpecific()) {
                    inflationFactors.stream()
                            .filter(f -> f.getToUnit().getId().equals(factor.getFromUnit().getId())) // get all conversions from base factor
                            .forEach(f -> resultMap.computeIfAbsent(new ConversionFactorKey(f.getFromUnit().getId(), f.getMarker()),
                                    tc -> inflateFactorFromBase(factor, f)));
                }
            }
        }
        // maybe we have an exact match now
        if (resultMap.containsKey(exactMatch)) {
            return Collections.singletonList(resultMap.get(exactMatch));
        }

        // or a unit match, where the marker is null
        if (resultMap.containsKey(nullMarkerMatch)) {
            return Collections.singletonList(resultMap.get(nullMarkerMatch));
        }

        // if not, check for unit match without marker
        unitMatches = getUnitMatches(resultMap, unitId);
        if (!unitMatches.isEmpty()) {
            return unitMatches;
        }
        return new ArrayList<>();
    }

    private List<ConversionFactor> getUnitMatches(Map<ConversionFactorKey, ConversionFactor> resultMap, Long unitId) {
        return resultMap.keySet().stream()
                .filter(k -> Objects.equals(k.unitId, unitId))
                .map(resultMap::get)
                .collect(Collectors.toList());
    }

    private List<ConversionFactorEntity> getInflationFactors(boolean isInverted) {
        if (isInverted) {
            return metricInflationFactors;
        }
        return hybridInflationFactors;
    }

    private List<ConversionFactor> reverseTagFactors(List<ConversionFactor> tagFactors) {
        return tagFactors.stream().map(SimpleConversionFactor::reverseFactor).collect(Collectors.toList());
    }

    private ConversionFactor inflateFactorFromBase(ConversionFactor base, ConversionFactorEntity factorToConvert) {
        // basic idea - when base factor is cups to grams
        //     base is cups => grams  (5)
        //     factorToConvert is => teaspoons => cups (0.020833333333333)
        // goal is teaspoons to grams
        // calculate factor as factorToConvert (teaspoons => cups) * base (cups => grams) (0.104166666666667)

        // calculate factor as base factor * factorToConvert
        double calculatedFactor = base.getFactor() * factorToConvert.getFactor();

        SimpleConversionFactor newFactor = new SimpleConversionFactor();
        newFactor.setFactor(calculatedFactor);
        newFactor.setToUnit(base.getToUnit());
        newFactor.setFromUnit(factorToConvert.getFromUnit());
        newFactor.setMarker(factorToConvert.getMarker());
        return newFactor;
    }


    private class ConversionFactorKey {
        private Long unitId;
        private String marker;

        public ConversionFactorKey(Long unitId, String marker) {
            this.unitId = unitId;
            this.marker = marker;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ConversionFactorKey that = (ConversionFactorKey) o;
            return Objects.equals(unitId, that.unitId) && Objects.equals(marker, that.marker);
        }

        @Override
        public int hashCode() {
            return Objects.hash(unitId, marker);
        }
    }
}

