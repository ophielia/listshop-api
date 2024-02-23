package com.meg.listshop.conversion.service;

import com.meg.listshop.conversion.data.entity.ConversionFactorEntity;
import com.meg.listshop.conversion.data.entity.UnitEntity;
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

import java.util.Optional;


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
    void testAddFactorForTag() {
        Long tagId = 101L;
        double amount = 1.0;
        Long unitId = 1000L;
        Long gramUnitId = 1013L;
        double gramWeight = 150.0;

        UnitEntity fromUnit = ConversionTestTools.makeUSUnit(unitId, UnitSubtype.WEIGHT);
        UnitEntity gramUnit = ConversionTestTools.makeMetricUnit(gramUnitId, UnitSubtype.WEIGHT);

        Mockito.when(unitRepository.findById(unitId)).thenReturn(Optional.of(fromUnit));
        Mockito.when(unitRepository.findById(gramUnitId)).thenReturn(Optional.of(gramUnit));
        ArgumentCaptor<ConversionFactorEntity> factorCaptor = ArgumentCaptor.forClass(ConversionFactorEntity.class);
        Mockito.when(conversionFactorRepository.save(factorCaptor.capture())).thenReturn(null);


        conversionFactorService.addFactorForTag(tagId, amount, unitId, gramWeight);

        ConversionFactorEntity entity = factorCaptor.getValue();
        Assertions.assertNotNull(entity);
        Assertions.assertEquals(unitId, entity.getFromUnit().getId());
        Assertions.assertEquals((150.0), entity.getFactor());
    }

}