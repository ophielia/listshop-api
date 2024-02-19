package com.meg.listshop.conversion.service;


import com.meg.listshop.conversion.data.entity.ConversionFactorEntity;
import com.meg.listshop.conversion.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.repository.ConversionFactorRepository;
import com.meg.listshop.conversion.data.repository.UnitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConversionFactorServiceImpl implements ConversionFactorService {
    private static final Logger LOG = LoggerFactory.getLogger(ConversionFactorServiceImpl.class);
    private final ConversionFactorRepository conversionFactorRepository;
    private final UnitRepository unitRepository;

    @Value("${conversionservice.gram.unit.id:1013}")
    private Long GRAM_UNIT_ID;

    @Autowired
    public ConversionFactorServiceImpl(ConversionFactorRepository conversionFactorRepository,
                                       UnitRepository unitRepository) {
        this.conversionFactorRepository = conversionFactorRepository;
        this.unitRepository = unitRepository;
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

}
