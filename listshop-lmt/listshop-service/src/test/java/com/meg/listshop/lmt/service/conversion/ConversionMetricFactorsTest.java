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
import com.meg.listshop.conversion.data.pojo.*;
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
class ConversionMetricFactorsTest {

    private static final Long ounceId = 1009L;
    private static final Long gId = 1013L;
    private static final Long kgId = 1014L;
    private static final Long lbId = 1008L;
    private static final Long flTeaspoonId = 1019L;
    private static final Long flTablespoonId = 1021L;
    private static final Long tspId = 1002L;
    private static final Long tbId = 1001L;

    private static final Long gallonId = 1005L;
    private static final Long literId = 1003L;
    private static final Long quartId = 1010L;
    private static final Long pintId = 1006L;
    private static final Long cupsId = 1017L;
    private static final Long centileterId = 1015L;
    private static final Long milliliterId = 1004L;
    private static final Long flOzId = 1007L;
    @Autowired
    ConverterService converterService;

    @Autowired
    UnitRepository unitRepository;


    @Container
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    @Test
    void unitTestsMetricScaling() throws ConversionPathException, ConversionFactorException {
        UnitEntity litersOpt = unitRepository.findById(literId).orElse(null);
        UnitEntity milliliterOpt = unitRepository.findById(milliliterId).orElse(null);
        UnitEntity centiliterOpt = unitRepository.findById(centileterId).orElse(null);
        UnitEntity gramOpt = unitRepository.findById(gId).orElse(null);
        UnitEntity kgOpt = unitRepository.findById(kgId).orElse(null);

        ConversionRequest listContext = new ConversionRequest(ConversionTargetType.List, DomainType.METRIC);
        ConversionRequest listContextVolume = new ConversionRequest(ConversionTargetType.List, DomainType.METRIC);
        ConversionRequest dishContextVolume = new ConversionRequest(ConversionTargetType.Dish, DomainType.METRIC);


//        688 Gram = 0.688 Kilogram
        ConvertibleAmount amount = new SimpleAmount(688, gramOpt);
        ConvertibleAmount converted = converterService.convert(amount, kgOpt);
        assertNotNull(converted);
        assertEquals(0.688, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(kgId, converted.getUnit().getId());

//        .15 Liter = 15 Centiliter
        amount = new SimpleAmount(0.15, litersOpt);
        converted = converterService.convert(amount, centiliterOpt);
        assertNotNull(converted);
        assertEquals(15.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(centileterId, converted.getUnit().getId());

//        2.345 Liter = 2345 Milliliter
        amount = new SimpleAmount(.2345, litersOpt);
        converted = converterService.convert(amount, milliliterOpt);
        assertNotNull(converted);
        assertEquals(234.5, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(milliliterId, converted.getUnit().getId());

        //        900 Milliliter = 0.9 Liter
        amount = new SimpleAmount(900, milliliterOpt);
        converted = converterService.convert(amount, litersOpt);
        assertNotNull(converted);
        assertEquals(literId, converted.getUnit().getId());
        assertEquals(0.9, RoundingUtils.roundToThousandths(converted.getQuantity()));

        //        600 Milliliter = 60 Centiliter
        amount = new SimpleAmount(300, milliliterOpt);
        converted = converterService.convert(amount, centiliterOpt);
        assertNotNull(converted);
        assertEquals(30.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(centileterId, converted.getUnit().getId());

        //        50 Centiliter = 0.5 Liter
        amount = new SimpleAmount(50.0, centiliterOpt);
        converted = converterService.convert(amount, litersOpt);
        assertNotNull(converted);
        assertEquals(0.5, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(literId, converted.getUnit().getId());

        //        0.25 Centiliter = 2.5 Milliliter
        amount = new SimpleAmount(0.25, centiliterOpt);
        converted = converterService.convert(amount, milliliterOpt);
        assertNotNull(converted);
        assertEquals(2.5, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(milliliterId, converted.getUnit().getId());

    }

    @Test
    void unitTestsMetricConversion() throws ConversionPathException, ConversionFactorException {
        UnitEntity gallonsOpt = unitRepository.findById(gallonId).orElse(null);
        UnitEntity litersOpt = unitRepository.findById(literId).orElse(null);
        UnitEntity milliliterOpt = unitRepository.findById(milliliterId).orElse(null);
        UnitEntity cupsOpt = unitRepository.findById(cupsId).orElse(null);
        UnitEntity quartOpt = unitRepository.findById(quartId).orElse(null);
        UnitEntity pintOpt = unitRepository.findById(pintId).orElse(null);
        UnitEntity ounceOpt = unitRepository.findById(ounceId).orElse(null);
        UnitEntity poundOpt = unitRepository.findById(lbId).orElse(null);
        UnitEntity gramOpt = unitRepository.findById(gId).orElse(null);
        UnitEntity kilogOpt = unitRepository.findById(kgId).orElse(null);

        //        75 Gram = 0.1653467 Pound
        ConvertibleAmount amount = new SimpleAmount(75, gramOpt);
        ConvertibleAmount converted = converterService.convert(amount, poundOpt);
        assertNotNull(converted);
        assertEquals(0.165, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(lbId, converted.getUnit().getId());
        ConvertibleAmount andback = converterService.convert(converted, gramOpt);
        assertEquals(75.0, RoundingUtils.roundToThousandths(andback.getQuantity()));
        assertEquals(gId, andback.getUnit().getId());

//        3456 gram = 121.9068125 ounce
        amount = new SimpleAmount(3456, gramOpt);
        converted = converterService.convert(amount, ounceOpt);
        assertNotNull(converted);
        assertEquals(121.905, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(ounceId, converted.getUnit().getId());
        andback = converterService.convert(converted, gramOpt);
        assertEquals(3456.0, RoundingUtils.roundToThousandths(andback.getQuantity()));
        assertEquals(gId, andback.getUnit().getId());

//        10 Kilogram = 22.0462262 Pound
        amount = new SimpleAmount(10, kilogOpt);
        converted = converterService.convert(amount, poundOpt);
        assertNotNull(converted);
        assertEquals(22.046, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(lbId, converted.getUnit().getId());
        andback = converterService.convert(converted, kilogOpt);
        assertEquals(10.0, RoundingUtils.roundToThousandths(andback.getQuantity()));
        assertEquals(kgId, andback.getUnit().getId());

//        750 Gram = 26.4554715 Ounce
        amount = new SimpleAmount(750, gramOpt);
        converted = converterService.convert(amount, ounceOpt);
        assertNotNull(converted);
        assertEquals(26.455, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(ounceId, converted.getUnit().getId());
        andback = converterService.convert(converted, gramOpt);
        assertEquals(750.0, RoundingUtils.roundToThousandths(andback.getQuantity()));
        assertEquals(gId, andback.getUnit().getId());

//        0.75 Liter = 3.17006463 US cup
        amount = new SimpleAmount(0.75, litersOpt);
        converted = converterService.convert(amount, cupsOpt);
        assertNotNull(converted);
        assertEquals(3.17, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(cupsId, converted.getUnit().getId());
        andback = converterService.convert(converted, litersOpt);
        assertEquals(0.75, RoundingUtils.roundToThousandths(andback.getQuantity()));
        assertEquals(literId, andback.getUnit().getId());

//        10 Liter = 2.64172052 US gallon
        amount = new SimpleAmount(10, litersOpt);
        converted = converterService.convert(amount, gallonsOpt);
        assertNotNull(converted);
        assertEquals(2.642, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gallonId, converted.getUnit().getId());
        andback = converterService.convert(converted, litersOpt);
        assertEquals(10.0, RoundingUtils.roundToThousandths(andback.getQuantity()));
        assertEquals(literId, andback.getUnit().getId());

//        2 Liter = 4.22675284 US pint
        amount = new SimpleAmount(2, litersOpt);
        converted = converterService.convert(amount, pintOpt);
        assertNotNull(converted);
        assertEquals(4.227, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(pintId, converted.getUnit().getId());
        andback = converterService.convert(converted, litersOpt);
        assertEquals(2.0, RoundingUtils.roundToThousandths(andback.getQuantity()));
        assertEquals(literId, andback.getUnit().getId());

//        2.5 Liter = 2.64172052 US quart
        amount = new SimpleAmount(2.5, litersOpt);
        converted = converterService.convert(amount, quartOpt);
        assertNotNull(converted);
        assertEquals(2.642, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(quartId, converted.getUnit().getId());
        andback = converterService.convert(converted, litersOpt);
        assertEquals(2.5, RoundingUtils.roundToThousandths(andback.getQuantity()));
        assertEquals(literId, andback.getUnit().getId());

//        600 Milliliter = 2.5360517 US cup
        amount = new SimpleAmount(600, milliliterOpt);
        converted = converterService.convert(amount, cupsOpt);
        assertNotNull(converted);
        assertEquals(2.536, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(cupsId, converted.getUnit().getId());
        andback = converterService.convert(converted, milliliterOpt);
        assertEquals(600.0, RoundingUtils.roundToThousandths(andback.getQuantity()));
        assertEquals(milliliterId, andback.getUnit().getId());

    }


}
