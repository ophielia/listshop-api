package com.meg.listshop.conversion.service;


import com.meg.listshop.conversion.data.entity.ConversionFactorEntity;
import com.meg.listshop.conversion.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.ConversionSampleDTO;
import com.meg.listshop.conversion.data.pojo.SimpleAmount;
import com.meg.listshop.conversion.data.pojo.UnitSubtype;
import com.meg.listshop.conversion.data.pojo.UnitType;
import com.meg.listshop.conversion.data.repository.ConversionFactorRepository;
import com.meg.listshop.conversion.data.repository.UnitRepository;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;
import com.meg.listshop.conversion.tools.RoundingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ConversionServiceImpl implements ConversionService {
    private static final Logger LOG = LoggerFactory.getLogger(ConversionServiceImpl.class);
    private final ConversionFactorRepository conversionFactorRepository;
    private final UnitRepository unitRepository;
    private final ConverterService converterService;

    @Value("${conversionservice.gram.unit.id:1013}")
    private Long GRAM_UNIT_ID;

    @Value("${conversionservice.single.unit.id:1011}")
    private Long SINGLE_UNIT_ID;

    @Autowired
    public ConversionServiceImpl(ConversionFactorRepository conversionFactorRepository,
                                 UnitRepository unitRepository,
                                 ConverterService converterService) {
        this.conversionFactorRepository = conversionFactorRepository;
        this.unitRepository = unitRepository;
        this.converterService = converterService;
    }

    @Override
    public void saveConversionFactors(Long conversionId, List<FoodFactor> foodFactors) {
        // get gram unit
        UnitEntity gramUnit = unitRepository.findById(GRAM_UNIT_ID).orElse(null);

        Map<Long, ConversionFactorEntity> existing = conversionFactorRepository.findAllByConversionIdIs(conversionId).stream()
                .collect(Collectors.toMap(ConversionFactorEntity::getReferenceId, Function.identity()));
        List<FoodFactor> filteredFactors = foodFactors.stream()
                .filter(f -> !existing.containsKey(f.getReferenceId()))
                .collect(Collectors.toList());
        for (FoodFactor foodFactor : filteredFactors) {
            // massage amount and gramweight if amount is not 0
            double conversionGramWeight = foodFactor.getGramWeight();
            if (foodFactor.getAmount() != 1) {
                double factor = 1.0 / foodFactor.getAmount();
                conversionGramWeight = factor * foodFactor.getGramWeight();
            }
            UnitEntity fromUnit = unitRepository.findById(foodFactor.getFromUnitId()).orElse(null);
            ConversionFactorEntity toAdd = new ConversionFactorEntity();
            toAdd.setConversionId(conversionId);
            toAdd.setReferenceId(foodFactor.getReferenceId());
            toAdd.setFromUnit(fromUnit);
            toAdd.setToUnit(gramUnit);
            toAdd.setFactor(conversionGramWeight);
            toAdd.setMarker(foodFactor.getMarker());
            conversionFactorRepository.save(toAdd);
//MM unit_sizes -
            //  start here to copy unit sizes from food_conversions to factors
            // next -
            //     add unit size to ConvertibleAmount
            //     Pass 1 - current functionality
            // d    follow TagSpecificHandler flow to
            // d         pull and save multiple unit factors
            // d         in scaler, use the default
            // d         test with tomatoes - no unit size passed - does it convert to medium unit?
            //     Pass 2 - request conversion with unit size
            //          in scaler, use the passed size if it exists
            //          test with tomatoes - pass with small - does it convert to small unit?
            //     Afterwards....
            //          Conversion Samples
            //          and other fixes in youtrack
        }


    }

    @Override
    public List<ConversionSampleDTO> conversionSamplesForId(Long conversionId, Boolean isLiquid) {
        List<ConversionSampleDTO> result = new ArrayList<>();
        if (conversionId == null || (isLiquid != null && isLiquid)) {
            return result;
        }

        // get conversion factors
        List<ConversionFactorEntity> factors = conversionFactorRepository.findAllByConversionIdIs(conversionId);

        // get target unit - grams or unit
        UnitEntity targetUnit = determineSampleTarget(factors);

        // get units to convert to do conversion for
        List<UnitEntity> unitsToConvert = determineUnitsToConvert(factors);

        // get markers in factors
        List<String> markers = pullAvailableMarkers(factors);

        // do conversions
        for (UnitEntity unit : unitsToConvert) {
            SimpleAmount from = new SimpleAmount(1.0, unit, conversionId, isLiquid, null);

            ConvertibleAmount to = null;
            try {
                to = converterService.convert(from, targetUnit);
                // rounded "to amount"
                SimpleAmount roundedTo = new SimpleAmount(RoundingUtils.roundToHundredths(to.getQuantity()), to.getUnit());
                if (roundedTo.getUnit().equals(targetUnit)) {
                    result.add(new ConversionSampleDTO(from, roundedTo));
                }
            } catch (ConversionPathException | ConversionFactorException g) {
                LOG.error("Exception [{}] thrown during conversion, but continuing to next conversion.", g.getClass(), g);
            }

        }
        // add conversions for markers
        for (String marker : markers) {
            for (UnitEntity unit : unitsToConvert) {
                SimpleAmount from = new SimpleAmount(1.0, unit, conversionId, isLiquid, marker);

                ConvertibleAmount to = null;
                try {
                    to = converterService.convert(from, targetUnit);
                    // rounded "to amount"
                    SimpleAmount roundedTo = new SimpleAmount(RoundingUtils.roundToHundredths(to.getQuantity()), to.getUnit());
                    result.add(new ConversionSampleDTO(from, roundedTo));
                } catch (ConversionPathException | ConversionFactorException g) {
                    LOG.error("Exception [{}] thrown during conversion, but continuing to next conversion.", g.getClass(), g);
                }

            }
        }

        // return results
        return result;
    }

    private List<String> pullAvailableMarkers(List<ConversionFactorEntity> factors) {
        Set<String> markers = factors.stream()
                .map(ConversionFactorEntity::getMarker)
                .collect(Collectors.toSet());
        return new ArrayList<>(markers);
    }

    private List<UnitEntity> determineUnitsToConvert(List<ConversionFactorEntity> factors) {
        // find hybrid units
        List<UnitEntity> hybridUnits = unitRepository.findGenericWeightHybrids(UnitType.HYBRID, UnitSubtype.LIQUID);
        List<UnitEntity> additionalUnits = factors.stream()
                .filter(f -> f.getFromUnit().isTagSpecific())
                .map(ConversionFactorEntity::getFromUnit)
                .collect(Collectors.toList());
        if (!additionalUnits.isEmpty()) {
            hybridUnits.addAll(additionalUnits);
        }
        return hybridUnits;
    }

    private UnitEntity determineSampleTarget(List<ConversionFactorEntity> factors) {
        ConversionFactorEntity unitFactor = factors.stream()
                .filter(f -> f.getFromUnit().getId().equals(SINGLE_UNIT_ID))
                .findFirst().orElse(null);
        if (unitFactor != null) {
            return unitFactor.getFromUnit();
        }
        return unitRepository.findById(GRAM_UNIT_ID).orElse(null);
    }

}
