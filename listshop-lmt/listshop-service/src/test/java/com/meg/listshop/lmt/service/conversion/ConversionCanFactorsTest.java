/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.lmt.service.conversion;

import com.meg.listshop.Application;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.DomainType;
import com.meg.listshop.conversion.data.pojo.SimpleAmount;
import com.meg.listshop.common.data.repository.UnitRepository;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;
import com.meg.listshop.conversion.service.ConverterService;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.common.RoundingUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@Testcontainers
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
class ConversionCanFactorsTest {

    private static final Long canId = 1029L;
    private static final Long largeCanId = 1030L;
    private static final Long smallCanId = 1031L;
    private static final Long nr2CanId = 1032L;
    private static final Long can145Id = 1033L;
    private static final Long can25Id = 1034L;
    private static final Long can3Id = 1035L;
    private static final Long can29Id = 1036L;


    @Autowired
    ConverterService converterService;

    @Autowired
    UnitRepository unitRepository;


    @Container
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    @Test
    void unitBasicEquivalencyTests() throws ConversionPathException, ConversionFactorException {
        UnitEntity canOpt = unitRepository.findById(canId).orElse(null);
        UnitEntity largeCanOpt = unitRepository.findById(largeCanId).orElse(null);
        UnitEntity smallCanOpt = unitRepository.findById(smallCanId).orElse(null);
        UnitEntity nr2CanOpt = unitRepository.findById(nr2CanId).orElse(null);
        UnitEntity can145Opt = unitRepository.findById(can145Id).orElse(null);
        UnitEntity can25Opt = unitRepository.findById(can25Id).orElse(null);
        UnitEntity can3Opt = unitRepository.findById(can3Id).orElse(null);
        UnitEntity can29Opt = unitRepository.findById(can29Id).orElse(null);

        // testing unit to unit
        // direction us to hybrid should work
        ConvertibleAmount amount = new SimpleAmount(1, nr2CanOpt);
        ConvertibleAmount converted = converterService.convert(amount, canOpt);
        assertNotNull(converted);
        assertEquals(1, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(canId, converted.getUnit().getId());

        amount = new SimpleAmount(1, can29Opt);
        converted = converterService.convert(amount, largeCanOpt);
        assertNotNull(converted);
        assertEquals(1, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(largeCanId, converted.getUnit().getId());

        // direction hybrid to us should not work
        amount = new SimpleAmount(1, canOpt);
        converted = converterService.convert(amount, nr2CanOpt);
        assertNotNull(converted);
        assertEquals(1, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(canId, converted.getUnit().getId());
    }

    // test domain - hybrid to us domain should not do conversion

    // test contexts -
    // dish context should pass through
    // list context should convert to hybrid
    @Test
    void unitDomainConversions() throws ConversionPathException, ConversionFactorException {
        UnitEntity canOpt = unitRepository.findById(canId).orElse(null);
        UnitEntity largeCanOpt = unitRepository.findById(largeCanId).orElse(null);
        UnitEntity smallCanOpt = unitRepository.findById(smallCanId).orElse(null);
        UnitEntity nr2CanOpt = unitRepository.findById(nr2CanId).orElse(null);
        UnitEntity can145Opt = unitRepository.findById(can145Id).orElse(null);
        UnitEntity can25Opt = unitRepository.findById(can25Id).orElse(null);
        UnitEntity can3Opt = unitRepository.findById(can3Id).orElse(null);
        UnitEntity can29Opt = unitRepository.findById(can29Id).orElse(null);

        // test domain - hybrid to us domain should not do conversion
        ConvertibleAmount amount = new SimpleAmount(1, largeCanOpt);
        ConvertibleAmount converted = converterService.convert(amount, DomainType.US);
        assertNotNull(converted);
        assertEquals(1, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(largeCanId, converted.getUnit().getId());

        // test domain - hybrid to uk domain should not do conversion
        amount = new SimpleAmount(1, smallCanOpt);
        converted = converterService.convert(amount, DomainType.UK);
        assertNotNull(converted);
        assertEquals(1, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(smallCanId, converted.getUnit().getId());
    }

    @Test
    void unitContextConversions() throws ConversionPathException, ConversionFactorException {
        UnitEntity canOpt = unitRepository.findById(canId).orElse(null);
        UnitEntity largeCanOpt = unitRepository.findById(largeCanId).orElse(null);
        UnitEntity smallCanOpt = unitRepository.findById(smallCanId).orElse(null);
        UnitEntity nr2CanOpt = unitRepository.findById(nr2CanId).orElse(null);
        UnitEntity can145Opt = unitRepository.findById(can145Id).orElse(null);
        UnitEntity can25Opt = unitRepository.findById(can25Id).orElse(null);
        UnitEntity can3Opt = unitRepository.findById(can3Id).orElse(null);
        UnitEntity can29Opt = unitRepository.findById(can29Id).orElse(null);

        // test domain - hybrid to us domain should not do conversion
        ConvertibleAmount amount = new SimpleAmount(1, largeCanOpt);
        ConvertibleAmount converted = converterService.convert(amount, DomainType.US);
        assertNotNull(converted);
        assertEquals(1, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(largeCanId, converted.getUnit().getId());

        // test domain - hybrid to uk domain should not do conversion
        amount = new SimpleAmount(1, smallCanOpt);
        converted = converterService.convert(amount, DomainType.UK);
        assertNotNull(converted);
        assertEquals(1, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(smallCanId, converted.getUnit().getId());
    }

    // test contexts - //ConversionRequest listContext = new ConversionRequest(ConversionTargetType.List, UnitType.METRIC);
    // dish context should pass through
    // list context should convert to hybrid

}
