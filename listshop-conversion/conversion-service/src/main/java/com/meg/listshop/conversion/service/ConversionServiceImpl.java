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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
    public void deleteFactorsForTag(Long tagId) {
        List<ConversionFactorEntity> entitiesToDelete = conversionFactorRepository.findAllByTagIdIs(tagId);
        conversionFactorRepository.deleteAll(entitiesToDelete);
    }

    @Override
    public void addFactorForTag(Long tagId, double amount, Long unitId, double gramWeight) {
        // massage amount and gramweight if amount is not 0
        double conversionGramWeight = gramWeight;
        if (amount != 1) {
            double factor = 1.0 / amount;
            conversionGramWeight = factor * gramWeight;
        }

        // get from unit
        UnitEntity fromUnit = unitRepository.findById(unitId).orElse(null);
        UnitEntity toUnit = unitRepository.findById(GRAM_UNIT_ID).orElse(null);

        ConversionFactorEntity newFactor = new ConversionFactorEntity();
        newFactor.setTagId(tagId);
        newFactor.setFromUnit(fromUnit);
        newFactor.setToUnit(toUnit);
        newFactor.setFactor(conversionGramWeight);
        conversionFactorRepository.save(newFactor);
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
                result.add(new ConversionSampleDTO(from, to));
            } catch (ConversionPathException | ConversionFactorException g) {
                LOG.error("Exception [{}]thrown during conversion, but continuing to next conversion.", g);
            }

        }
        // return results
        return result;
    }

}
