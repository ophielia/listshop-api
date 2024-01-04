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

public class HybridSolidConversionSource extends AbstractConversionFactorSource {
    private static final Logger LOG = LoggerFactory.getLogger(HybridSolidConversionSource.class);


    // a list of factors which convert tablespoons/teaspoons (HYBRID) to cups
    private final List<ConversionFactorEntity> inflationFactors;

    private final ConversionFactorRepository factorRepository;

    public HybridSolidConversionSource(ConversionFactorRepository factorRepository) {
        super(new ArrayList<>(), false);

        this.factorRepository = factorRepository;

        ConversionSpec inflationSource = ConversionSpec.basicSpec(UnitType.HYBRID, UnitSubtype.SOLID);  // teaspoons and tablespoons
        ConversionSpec inflationTarget = ConversionSpec.basicSpec(UnitType.HYBRID, UnitSubtype.SOLID);  // cups
        inflationFactors = factorRepository.findAll(where(matchingFromWithSpec(inflationSource).and(matchingToWithSpec(inflationTarget))));

    }

    @Override
    public List<ConversionFactor> getFactors(Long unitId, Long tagId) {
        LOG.trace("... getting factors from db for unitId: [{}]", unitId);

        // get factors from database for tag id
        List<ConversionFactorEntity> tagFactors = factorRepository.findAll(where(matchingFromTagId(tagId)));

        // if no factors found return empty list
        if ( tagFactors.isEmpty()) {
            return new ArrayList<>();
        }

        // amplify database factors to all factors possible for HYBRIDS
        // get base factor - which we'll use to inflate other factors
        ConversionFactorEntity baseFactor = tagFactors.get(0);

        // create hashmap from tag factors
        Map<Long, ConversionFactor> resultMap = tagFactors.stream()
                .collect(Collectors.toMap( f -> f.getFromUnit().getId(), Function.identity() ));

        // if we have a match, return it now
        if (resultMap.containsKey(unitId)) {
            return Collections.singletonList(resultMap.get(unitId));
        }
        // fill in any missing factors, by using the base factor to convert the missing factors
        inflationFactors.stream()
                .filter( f -> f.getToUnit().getId().equals(baseFactor.getFromUnit().getId())) // get all conversions from base factor
                .forEach( f -> resultMap.computeIfAbsent( f.getFromUnit().getId(), tc -> inflateFactorFromBase(baseFactor, f) ));

        // if we have a match, return it
        if (resultMap.containsKey(unitId)) {
            return Collections.singletonList(resultMap.get(unitId));
        }
        return new ArrayList<>();
    }

    private ConversionFactor inflateFactorFromBase(ConversionFactorEntity base, ConversionFactorEntity factorToConvert) {
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
