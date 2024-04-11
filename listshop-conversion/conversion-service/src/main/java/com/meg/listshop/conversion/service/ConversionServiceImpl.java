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

        Map<Long,ConversionFactorEntity> existing = conversionFactorRepository.findAllByConversionIdIs(conversionId).stream()
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
            conversionFactorRepository.save(toAdd);

    }


    }

    @Override
    public List<ConversionSampleDTO> conversionSamplesForTag(Long tagId, Boolean isLiquid) {
        List<ConversionSampleDTO> result = new ArrayList<>();
        if (tagId == null || (isLiquid != null && isLiquid)) {
            return result;
        }

        // find hybrid units
        List<UnitEntity> hybridUnits = unitRepository.findUnitEntitiesByTypeAndSubtypeIsNot(UnitType.HYBRID, UnitSubtype.LIQUID);

        UnitEntity gramUnit = unitRepository.findById(GRAM_UNIT_ID).orElse(null);
        // convert each hybrid unit, and add to result
        for (UnitEntity unit : hybridUnits) {
            SimpleAmount from = new SimpleAmount(1.0, unit, tagId, isLiquid, "");

            ConvertibleAmount to = null;
            try {
                to = converterService.convert(from, gramUnit);
                // rounded "to amount"
                SimpleAmount roundedTo = new SimpleAmount(RoundingUtils.roundToHundredths(to.getQuantity()),to.getUnit());
                result.add(new ConversionSampleDTO(from, roundedTo));
            } catch (ConversionPathException | ConversionFactorException g) {
                LOG.error("Exception [{}] thrown during conversion, but continuing to next conversion.",g.getClass(), g);
            }

        }
        // return results
        return result;
    }

}
