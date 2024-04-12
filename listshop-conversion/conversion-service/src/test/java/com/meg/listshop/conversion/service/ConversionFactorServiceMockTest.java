package com.meg.listshop.conversion.service;

import com.meg.listshop.conversion.data.entity.ConversionFactorEntity;
import com.meg.listshop.conversion.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.SimpleFoodFactor;
import com.meg.listshop.conversion.data.pojo.UnitSubtype;
import com.meg.listshop.conversion.data.repository.ConversionFactorRepository;
import com.meg.listshop.conversion.data.repository.UnitRepository;
import com.meg.listshop.conversion.tools.ConversionTestTools;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class ConversionFactorServiceMockTest {

    private ConversionServiceImpl conversionFactorService;

    @MockBean
    private ConversionFactorRepository conversionFactorRepository;

    @MockBean
    private UnitRepository unitRepository;

    @MockBean
    private ConverterService converterService;

    @BeforeEach
    void setUp() {
        conversionFactorService = new ConversionServiceImpl(conversionFactorRepository, unitRepository, converterService);

    }

    @Test
    void testSaveConversionFactors() {
        Long conversionId = 101L;
        Long referenceId = 11011L;
        double amount = 1.0;
        Long unitId = 1000L;
        Long gramUnitId = 1013L;
        double gramWeight = 150.0;

        UnitEntity fromUnit = ConversionTestTools.makeUSUnit(unitId, UnitSubtype.WEIGHT);
        UnitEntity gramUnit = ConversionTestTools.makeMetricUnit(gramUnitId, UnitSubtype.WEIGHT);
        FoodFactor foodFactor = new SimpleFoodFactor(referenceId, gramWeight, amount, unitId);
        List<FoodFactor> factorList = Collections.singletonList(foodFactor);

        ArgumentCaptor<ConversionFactorEntity> factorCaptor = ArgumentCaptor.forClass(ConversionFactorEntity.class);
        Mockito.when(unitRepository.findById(gramUnitId)).thenReturn(Optional.of(gramUnit));
        Mockito.when(conversionFactorRepository.findAllByConversionIdIs(conversionId)).thenReturn(new ArrayList<>());
        Mockito.when(unitRepository.findById(unitId)).thenReturn(Optional.of(fromUnit));
        Mockito.when(conversionFactorRepository.save(factorCaptor.capture())).thenReturn(null);

        conversionFactorService.saveConversionFactors(conversionId, factorList);

        ConversionFactorEntity entity = factorCaptor.getValue();
        Assertions.assertNotNull(entity);
        Assertions.assertEquals(unitId, entity.getFromUnit().getId());
        Assertions.assertEquals(gramWeight, entity.getFactor());
    }

    @Test
    void testSaveConversionFactorsExistingFactors() {
        Long conversionId = 101L;
        Long referenceId = 11011L;
        Long newReferenceId = 22022L;
        double amount = 1.0;
        Long unitId = 1000L;
        Long gramUnitId = 1013L;
        double gramWeight = 150.0;
        double newGramWeight = 300.0;
        Long newUnitId = 1002L;

        UnitEntity fromUnit = ConversionTestTools.makeUSUnit(unitId, UnitSubtype.WEIGHT);
        UnitEntity gramUnit = ConversionTestTools.makeMetricUnit(gramUnitId, UnitSubtype.WEIGHT);
        ConversionFactorEntity existingFoodFactor = new ConversionFactorEntity();
        existingFoodFactor.setReferenceId(referenceId);
        existingFoodFactor.setFactor(0.2);
        existingFoodFactor.setToUnit(fromUnit);
        List<ConversionFactorEntity> existingFactorList = Collections.singletonList(existingFoodFactor);

        FoodFactor newFoodFactor1 = new SimpleFoodFactor(referenceId, gramWeight, amount, unitId);
        FoodFactor newFoodFactor2 = new SimpleFoodFactor(newReferenceId, newGramWeight, amount, newUnitId);
        List<FoodFactor> newList = Arrays.asList(newFoodFactor1, newFoodFactor2);

        ArgumentCaptor<ConversionFactorEntity> factorCaptor = ArgumentCaptor.forClass(ConversionFactorEntity.class);
        Mockito.when(unitRepository.findById(gramUnitId)).thenReturn(Optional.of(gramUnit));
        Mockito.when(conversionFactorRepository.findAllByConversionIdIs(conversionId)).thenReturn(existingFactorList);
        Mockito.when(unitRepository.findById(newUnitId)).thenReturn(Optional.of(fromUnit));
        Mockito.when(conversionFactorRepository.save(factorCaptor.capture())).thenReturn(null);

        conversionFactorService.saveConversionFactors(conversionId, newList);

        ConversionFactorEntity entity = factorCaptor.getValue();
        Assertions.assertNotNull(entity);
        Assertions.assertEquals(unitId, entity.getFromUnit().getId());
        Assertions.assertEquals(unitId, entity.getFromUnit().getId());
        Assertions.assertEquals(newReferenceId, entity.getReferenceId());

    }

    @Test
    void testConversionSamplesForTag() {
        Long conversionId = 101L;
        Long referenceId = 11011L;
        Long newReferenceId = 22022L;
        double amount = 1.0;
        Long unitId = 1000L;
        Long gramUnitId = 1013L;
        double gramWeight = 150.0;
        double newGramWeight = 300.0;
        Long newUnitId = 1002L;

        UnitEntity fromUnit = ConversionTestTools.makeUSUnit(unitId, UnitSubtype.WEIGHT);
        UnitEntity gramUnit = ConversionTestTools.makeMetricUnit(gramUnitId, UnitSubtype.WEIGHT);
        ConversionFactorEntity existingFoodFactor = new ConversionFactorEntity();
        existingFoodFactor.setReferenceId(referenceId);
        existingFoodFactor.setFactor(0.2);
        existingFoodFactor.setToUnit(fromUnit);
        List<ConversionFactorEntity> existingFactorList = Collections.singletonList(existingFoodFactor);

        FoodFactor newFoodFactor1 = new SimpleFoodFactor(referenceId, gramWeight, amount, unitId);
        FoodFactor newFoodFactor2 = new SimpleFoodFactor(newReferenceId, newGramWeight, amount, newUnitId);
        List<FoodFactor> newList = Arrays.asList(newFoodFactor1, newFoodFactor2);

        ArgumentCaptor<ConversionFactorEntity> factorCaptor = ArgumentCaptor.forClass(ConversionFactorEntity.class);
        Mockito.when(unitRepository.findById(gramUnitId)).thenReturn(Optional.of(gramUnit));
        Mockito.when(conversionFactorRepository.findAllByConversionIdIs(conversionId)).thenReturn(existingFactorList);
        Mockito.when(unitRepository.findById(newUnitId)).thenReturn(Optional.of(fromUnit));
        Mockito.when(conversionFactorRepository.save(factorCaptor.capture())).thenReturn(null);

        conversionFactorService.saveConversionFactors(conversionId, newList);

        ConversionFactorEntity entity = factorCaptor.getValue();
        Assertions.assertNotNull(entity);
        Assertions.assertEquals(unitId, entity.getFromUnit().getId());
        Assertions.assertEquals(unitId, entity.getFromUnit().getId());
        Assertions.assertEquals(newReferenceId, entity.getReferenceId());

    }
}