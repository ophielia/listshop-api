package com.meg.listshop.conversion.service;


import com.meg.listshop.conversion.data.entity.ConversionFactorEntity;
import com.meg.listshop.conversion.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.repository.ConversionFactorRepository;
import com.meg.listshop.conversion.data.repository.UnitRepository;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
            toAdd.setUnitSize(foodFactor.getUnitSize());
            toAdd.setUnitDefault(foodFactor.getUnitDefault());
            conversionFactorRepository.save(toAdd);
        }


    }

    @Override
    public ConvertibleAmount convertToUnit(ConvertibleAmount amount, UnitEntity targetUnit, String unitSize) throws ConversionPathException, ConversionFactorException {
        return converterService.convert(amount, targetUnit, unitSize);
    }

}
