package com.meg.listshop.conversion.service.factors;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.ConversionFactorEntity;
import com.meg.listshop.conversion.data.entity.SimpleConversionFactor;
import com.meg.listshop.conversion.data.pojo.UnitSubtype;
import com.meg.listshop.conversion.data.pojo.UnitType;
import com.meg.listshop.conversion.data.repository.ConversionFactorRepository;
import com.meg.listshop.conversion.service.ConversionSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.meg.listshop.conversion.data.repository.UnitSpecifications.*;
import static org.springframework.data.jpa.domain.Specification.where;

public class WeightVolumeConversionSource extends AbstractConversionFactorSource {
    private static final Logger LOG = LoggerFactory.getLogger(WeightVolumeConversionSource.class);


    // a list of factors which convert tablespoons/teaspoons (HYBRID) to cups
    private final List<ConversionFactorEntity> hybridInflationFactors;

    private final List<ConversionFactorEntity> metricInflationFactors;

    private final ConversionFactorRepository factorRepository;

    public WeightVolumeConversionSource(ConversionFactorRepository factorRepository) {
        super(new ArrayList<>(), false);

        this.factorRepository = factorRepository;

        ConversionSpec inflationSource = ConversionSpec.basicSpec(UnitType.HYBRID, UnitSubtype.SOLID);  // teaspoons , tablespoons , cups
        hybridInflationFactors = factorRepository.findAll(where(matchingFromWithSpec(inflationSource).and(matchingToWithSpec(inflationSource))));

        ConversionSpec metricInflationSource = ConversionSpec.basicSpec(UnitType.METRIC, UnitSubtype.WEIGHT);  // teaspoons , tablespoons , cups
        metricInflationFactors = factorRepository.findAll(where(matchingFromWithSpec(metricInflationSource).and(matchingToWithSpec(metricInflationSource))));

    }

    @Override
    public List<ConversionFactor> getFactors(Long unitId, Long tagId) {
        LOG.trace("... getting factors from db for unitId: [{}]", unitId);

        // get factors from database for tag id
        List<ConversionFactor> tagFactors = factorRepository.findAll(where(matchingFromTagId(tagId))).stream()
                .map(f->(ConversionFactor)f)
                .collect(Collectors.toList());

        // if no factors found return empty list
        if ( tagFactors.isEmpty()) {
            return new ArrayList<>();
        }

        // determine if this is inverted - going from grams to hybrid
        boolean isInverted = metricInflationFactors.stream().anyMatch(f -> f.getFromUnit().getId().equals(unitId));
        if (isInverted) {
            tagFactors = reverseTagFactors(tagFactors);
        }

        // amplify database factors to all factors possible for HYBRIDS
        // get base factor - which we'll use to inflate other factors
        ConversionFactor baseFactor = tagFactors.get(0);

        // create hashmap from tag factors
        Map<Long, ConversionFactor> resultMap = tagFactors.stream()
                .collect(Collectors.toMap( f -> f.getFromUnit().getId(), Function.identity() ));

        // if we have a match, return it now
        if (resultMap.containsKey(unitId)) {
            return Collections.singletonList(resultMap.get(unitId));
        }
        // fill in any missing factors, by using the base factor to convert the missing factors
         final List<ConversionFactorEntity> inflationFactors = getInflationFactors(isInverted);
        inflationFactors.stream()
                .filter( f -> f.getToUnit().getId().equals(baseFactor.getFromUnit().getId())) // get all conversions from base factor
                .forEach( f -> resultMap.computeIfAbsent( f.getFromUnit().getId(), tc -> inflateFactorFromBase(baseFactor, f) ));

        // if we have a match, return it
        if (resultMap.containsKey(unitId)) {
            return Collections.singletonList(resultMap.get(unitId));
        }
        return new ArrayList<>();
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
        return newFactor;
    }
}
