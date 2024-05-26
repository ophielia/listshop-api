package com.meg.listshop.conversion.service.factors;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.ConversionFactorEntity;
import com.meg.listshop.conversion.data.entity.SimpleConversionFactor;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.common.UnitSubtype;
import com.meg.listshop.common.UnitType;
import com.meg.listshop.conversion.data.repository.ConversionFactorRepository;
import com.meg.listshop.conversion.service.ConversionContext;
import com.meg.listshop.conversion.service.ConversionSpec;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.meg.listshop.conversion.data.repository.UnitSpecifications.*;
import static org.springframework.data.jpa.domain.Specification.where;

@Component
public class TagSpecificConversionSource extends AbstractConversionFactorSource {
    private static final Logger LOG = LoggerFactory.getLogger(TagSpecificConversionSource.class);


    // a list of factors which convert tablespoons/teaspoons (HYBRID) to cups
    private final List<ConversionFactorEntity> hybridInflationFactors;

    private final ConversionFactorRepository factorRepository;

    private Long SINGLE_UNIT_ID;

    public TagSpecificConversionSource(ConversionFactorRepository factorRepository) {
        super(new ArrayList<>(), false);
        this.SINGLE_UNIT_ID = 1011L;
        this.factorRepository = factorRepository;

        ConversionSpec inflationSource = ConversionSpec.basicSpec(UnitType.HYBRID, UnitSubtype.SOLID);  // teaspoons , tablespoons , cups
        hybridInflationFactors = factorRepository.findAll(where(matchingFromWithSpecGenericOnly(inflationSource).and(matchingToWithSpecGenericOnly(inflationSource))));

    }

    @Override
    public List<ConversionFactor> getFactors(ConvertibleAmount convertibleAmount, Long conversionId, boolean isOneWay) {
// no implementation for this class
        return null;
    }

    private List<ConversionFactor> getAllPossibleFactors(ConvertibleAmount convertibleAmount, Long conversionId) {
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

    /**
     * The factors returned from this method are used to convert the amount, and to scale the amount after
     * conversion.  It returns 1) the unit factor, if available. 2) the conversion factor for the amount to be
     * converted and 3) any tag specific factors, for scaling later.
     *
     * @param convertibleAmount
     * @param context
     * @return
     */
    public List<ConversionFactor> getScalingFactors(ConvertibleAmount convertibleAmount, ConversionContext context) {
        Long conversionId = context.getConversionId();
        Long unitId = convertibleAmount.getUnit().getId();
        List<ConversionFactor> possibleFactors = getAllPossibleFactors(convertibleAmount, conversionId);
        if (possibleFactors.isEmpty()) {
            return new ArrayList<>();
        }
        LOG.trace("... selecting factors from list : [{}], unitId [{}]", possibleFactors, unitId);


        // classify factors by markers and pull unit factor
        // choose marker source - marker, null, or marker + null
        List<ConversionFactor> unitFactors = new ArrayList<>();
        Map<String, List<ConversionFactor>> factorsByMarker = new HashMap<>();
        for (ConversionFactor f : possibleFactors) {
            if (f.getFromUnit().getId().equals(SINGLE_UNIT_ID)) {
                unitFactors.add(f);
                continue;
            }
            String marker = f.getMarker();
            if (!factorsByMarker.containsKey(marker)) {
                factorsByMarker.put(marker, new ArrayList<>());
            }
            factorsByMarker.get(marker).add(f);
        }


        List<ConversionFactor> markerFactors = getFactorsByMarker(factorsByMarker, convertibleAmount.getMarker());

        // inflate factors
        List<ConversionFactor> inflatedFactors = inflateFactors(markerFactors);
        List<ConversionFactor> scalingFactors = new ArrayList<>();
        // find conversion factor and tag specific scaling factors
        ConversionFactor conversionFactor = null;
        for (ConversionFactor f : inflatedFactors) {
            // look for conversion factor
            if (f.getFromUnit().getId().equals(unitId)) {
                conversionFactor = f;
                continue;
            }
            if (f.getFromUnit().isTagSpecific()) {
                scalingFactors.add(f);
            }
        }

        // add unit factor and conversion factor to scaling factors, and return
        if (!unitFactors.isEmpty()) {
            scalingFactors.addAll(unitFactors);
        }
        if (conversionFactor != null) {
            scalingFactors.add(conversionFactor);
        }
        return scalingFactors;
    }

    private List<ConversionFactor> getFactorsByMarker(Map<String, List<ConversionFactor>> factorsByMarker, String marker) {
        List<ConversionFactor> factors = new ArrayList<>();
        if (factorsByMarker == null || factorsByMarker.isEmpty()) {
            return factors;
        }
        // do we have an exact match?
        if (factorsByMarker.containsKey(marker)) {
            return factorsByMarker.get(marker);
        }
        // no exact match - do we have any null marker factors?
        if (factorsByMarker.containsKey(null)) {
            return factorsByMarker.get(null);
        }
        // no null factors, we'll return the first marker available
        String guessMarker = factorsByMarker.keySet().stream().findFirst().orElse(null);
        if (guessMarker != null) {
            return factorsByMarker.get(guessMarker);
        }
        return factors;
    }


    private ConversionFactor inflateFactorFromBase(ConversionFactor base, ConversionFactor factorToConvert) {
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

    private List<ConversionFactor> inflateFactors(List<ConversionFactor> factors) {
        if (factors == null || factors.isEmpty()) {
            return new ArrayList<>();
        }
        ConversionFactor baseFactor = factors.stream()
                .filter(f -> !f.getFromUnit().isTagSpecific() && f.getFromUnit().getType().equals(UnitType.HYBRID))
                .findFirst()
                .orElse(null);
        if (baseFactor == null) {
            return factors;
        }

        Set<Long> mappedUnitIds = factors.stream()
                .map(ConversionFactor::getFromUnit)
                .map(UnitEntity::getId)
                .collect(Collectors.toSet());
        List<ConversionFactor> inflatedFactors = new ArrayList<>();
        for (ConversionFactor f : hybridInflationFactors) {
            if (mappedUnitIds.contains(f.getFromUnit().getId()) ||
                 !baseFactor.getFromUnit().getId().equals(f.getToUnit().getId())) {
                continue;
            }
            mappedUnitIds.add(f.getFromUnit().getId());
            inflatedFactors.add(inflateFactorFromBase(baseFactor, f));
        }
        factors.addAll(inflatedFactors);
        return factors;
    }

}


