/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.lmt.service;

import com.meg.listshop.Application;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.conversion.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.*;
import com.meg.listshop.conversion.data.repository.UnitRepository;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;
import com.meg.listshop.conversion.exceptions.ExceedsAllowedScaleException;
import com.meg.listshop.conversion.service.ConversionService;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.conversion.tools.RoundingUtils;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class ConversionFactorsTest {

    private static final Long ounceId = 1009L;
    private static final Long mgId = 1016L;
    private static final Long gId = 1013L;
    private static final Long kgId = 1014L;
    private static final Long lbId = 1008L;

    private static final Long gallonId = 1005L;
    private static final Long literId = 1003L;
    private static final Long quartId = 1010L;
    private static final Long pintId = 1006L;
    private static final Long cupsId = 1000L;
    private static final Long centileterId = 1015L;
    private static final Long milliliterId = 1004L;
    private static final Long flOzId = 1007L;
    @Autowired
    ConversionService conversionService;

    @Autowired
    UnitRepository unitRepository;


    @ClassRule
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    @Test
    public void unitTestsMetricScaling() throws ConversionPathException, ConversionFactorException, ExceedsAllowedScaleException {
        Optional<UnitEntity> litersOpt = unitRepository.findById(literId);
        Optional<UnitEntity> milliliterOpt = unitRepository.findById(milliliterId);
        Optional<UnitEntity> centiliterOpt = unitRepository.findById(centileterId);
        Optional<UnitEntity> gramOpt = unitRepository.findById(gId);

        ConversionContext listContext = new ConversionContext(ConversionContextType.List, UnitType.METRIC, UnitSubtype.WEIGHT);
        ConversionContext listContextVolume = new ConversionContext(ConversionContextType.List, UnitType.METRIC, UnitSubtype.VOLUME);
        ConversionContext dishContextVolume = new ConversionContext(ConversionContextType.Dish, UnitType.METRIC, UnitSubtype.VOLUME);


//        688 Gram = 0.688 Kilogram
        ConvertibleAmount amount = new SimpleAmount(688, gramOpt.get());
        ConvertibleAmount converted = conversionService.convert(amount, listContext);
        assertNotNull(converted);
        assertEquals(0.688, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(kgId, converted.getUnit().getId());
        ;
//        .15 Liter = 15 Centiliter
        amount = new SimpleAmount(0.15, litersOpt.get());
        converted = conversionService.convert(amount, dishContextVolume);
        assertNotNull(converted);
        assertEquals(15.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(centileterId, converted.getUnit().getId());

//        2.345 Liter = 2345 Milliliter
        amount = new SimpleAmount(.2345, litersOpt.get());
        converted = conversionService.convert(amount, listContextVolume);
        assertNotNull(converted);
        assertEquals(234.5, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(milliliterId, converted.getUnit().getId());

        //        900 Milliliter = 0.9 Liter
        amount = new SimpleAmount(900, milliliterOpt.get());
        converted = conversionService.convert(amount, listContextVolume);
        assertNotNull(converted);
        assertEquals(literId, converted.getUnit().getId());
        assertEquals(0.9, RoundingUtils.roundToThousandths(converted.getQuantity()));

        //        600 Milliliter = 60 Centiliter
        amount = new SimpleAmount(300, milliliterOpt.get());
        converted = conversionService.convert(amount, dishContextVolume);
        assertNotNull(converted);
        assertEquals(30.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(centileterId, converted.getUnit().getId());

        //        50 Centiliter = 0.5 Liter
        amount = new SimpleAmount(50.0, centiliterOpt.get());
        converted = conversionService.convert(amount, listContextVolume);
        assertNotNull(converted);
        assertEquals(0.5, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(literId, converted.getUnit().getId());

        //        0.25 Centiliter = 2.5 Milliliter
        amount = new SimpleAmount(0.25, centiliterOpt.get());
        converted = conversionService.convert(amount, listContextVolume);
        assertNotNull(converted);
        assertEquals(2.5, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(milliliterId, converted.getUnit().getId());

    }

    @Test
    public void unitTestsMetricConversion() throws ConversionPathException, ConversionFactorException, ExceedsAllowedScaleException {
        Optional<UnitEntity> gallonsOpt = unitRepository.findById(gallonId);
        Optional<UnitEntity> litersOpt = unitRepository.findById(literId);
        Optional<UnitEntity> milliliterOpt = unitRepository.findById(milliliterId);
        Optional<UnitEntity> cupsOpt = unitRepository.findById(cupsId);
        Optional<UnitEntity> quartOpt = unitRepository.findById(quartId);
        Optional<UnitEntity> pintOpt = unitRepository.findById(pintId);
        Optional<UnitEntity> ounceOpt = unitRepository.findById(ounceId);
        Optional<UnitEntity> poundOpt = unitRepository.findById(lbId);
        Optional<UnitEntity> gramOpt = unitRepository.findById(gId);
        Optional<UnitEntity> kilogOpt = unitRepository.findById(kgId);

        //        75 Gram = 0.1653467 Pound
        ConvertibleAmount amount = new SimpleAmount(75, gramOpt.get());
        ConvertibleAmount converted = conversionService.convert(amount, poundOpt.get());
        assertNotNull(converted);
        assertEquals(0.165, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(lbId, converted.getUnit().getId());
        ConvertibleAmount andback = conversionService.convert(converted, gramOpt.get());
        assertEquals(75.0, RoundingUtils.roundToThousandths(andback.getQuantity()));
        assertEquals(gId, andback.getUnit().getId());

//        3456 gram = 121.9068125 ounce
        amount = new SimpleAmount(3456, gramOpt.get());
        converted = conversionService.convert(amount, ounceOpt.get());
        assertNotNull(converted);
        assertEquals(121.905, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(ounceId, converted.getUnit().getId());
        andback = conversionService.convert(converted, gramOpt.get());
        assertEquals(3456.0, RoundingUtils.roundToThousandths(andback.getQuantity()));
        assertEquals(gId, andback.getUnit().getId());

//        10 Kilogram = 22.0462262 Pound
        amount = new SimpleAmount(10, kilogOpt.get());
        converted = conversionService.convert(amount, poundOpt.get());
        assertNotNull(converted);
        assertEquals(22.046, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(lbId, converted.getUnit().getId());
        andback = conversionService.convert(converted, kilogOpt.get());
        assertEquals(10.0, RoundingUtils.roundToThousandths(andback.getQuantity()));
        assertEquals(kgId, andback.getUnit().getId());

//        750 Gram = 26.4554715 Ounce
        amount = new SimpleAmount(750, gramOpt.get());
        converted = conversionService.convert(amount, ounceOpt.get());
        assertNotNull(converted);
        assertEquals(26.455, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(ounceId, converted.getUnit().getId());
        andback = conversionService.convert(converted, gramOpt.get());
        assertEquals(750.0, RoundingUtils.roundToThousandths(andback.getQuantity()));
        assertEquals(gId, andback.getUnit().getId());

//        0.75 Liter = 3.17006463 US cup
        amount = new SimpleAmount(0.75, litersOpt.get());
        converted = conversionService.convert(amount, cupsOpt.get());
        assertNotNull(converted);
        assertEquals(3.17, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(cupsId, converted.getUnit().getId());
        andback = conversionService.convert(converted, litersOpt.get());
        assertEquals(0.75, RoundingUtils.roundToThousandths(andback.getQuantity()));
        assertEquals(literId, andback.getUnit().getId());

//        10 Liter = 2.64172052 US gallon
        amount = new SimpleAmount(10, litersOpt.get());
        converted = conversionService.convert(amount, gallonsOpt.get());
        assertNotNull(converted);
        assertEquals(2.642, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gallonId, converted.getUnit().getId());
        andback = conversionService.convert(converted, litersOpt.get());
        assertEquals(10.0, RoundingUtils.roundToThousandths(andback.getQuantity()));
        assertEquals(literId, andback.getUnit().getId());

//        2 Liter = 4.22675284 US pint
        amount = new SimpleAmount(2, litersOpt.get());
        converted = conversionService.convert(amount, pintOpt.get());
        assertNotNull(converted);
        assertEquals(4.227, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(pintId, converted.getUnit().getId());
        andback = conversionService.convert(converted, litersOpt.get());
        assertEquals(2.0, RoundingUtils.roundToThousandths(andback.getQuantity()));
        assertEquals(literId, andback.getUnit().getId());

//        2.5 Liter = 2.64172052 US quart
        amount = new SimpleAmount(2.5, litersOpt.get());
        converted = conversionService.convert(amount, quartOpt.get());
        assertNotNull(converted);
        assertEquals(2.642, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(quartId, converted.getUnit().getId());
        andback = conversionService.convert(converted, litersOpt.get());
        assertEquals(2.5, RoundingUtils.roundToThousandths(andback.getQuantity()));
        assertEquals(literId, andback.getUnit().getId());

//        600 Milliliter = 2.5360517 US cup
        amount = new SimpleAmount(600, milliliterOpt.get());
        converted = conversionService.convert(amount, cupsOpt.get());
        assertNotNull(converted);
        assertEquals(2.536, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(cupsId, converted.getUnit().getId());
        andback = conversionService.convert(converted, milliliterOpt.get());
        assertEquals(600.0, RoundingUtils.roundToThousandths(andback.getQuantity()));
        assertEquals(milliliterId, andback.getUnit().getId());

    }

    @Test
    public void unitTestsUsScaling() throws ConversionPathException, ConversionFactorException, ExceedsAllowedScaleException {
        Optional<UnitEntity> gallonsOpt = unitRepository.findById(gallonId);
        Optional<UnitEntity> ounceOpt = unitRepository.findById(ounceId);
        Optional<UnitEntity> pintOpt = unitRepository.findById(pintId);
        Optional<UnitEntity> cupsOpt = unitRepository.findById(cupsId);
        Optional<UnitEntity> quartOpt = unitRepository.findById(quartId);
        Optional<UnitEntity> flOzOpt = unitRepository.findById(flOzId);
        Optional<UnitEntity> poundOpt = unitRepository.findById(lbId);
        Optional<UnitEntity> kilogOpt = unitRepository.findById(kgId);

        ConversionContext listContext = new ConversionContext(ConversionContextType.List, UnitType.US, UnitSubtype.WEIGHT);
        ConversionContext listContextVolume = new ConversionContext(ConversionContextType.List, UnitType.US, UnitSubtype.VOLUME);
        ConversionContext dishContext = new ConversionContext(ConversionContextType.Dish, UnitType.US, UnitSubtype.WEIGHT);
        ConversionContext dishContextVolume = new ConversionContext(ConversionContextType.Dish, UnitType.US, UnitSubtype.VOLUME);


        //        688 Gram = 0.688 Kilogram

// 6 US cup = 0.375 US gallon
        ConvertibleAmount amount = new SimpleAmount(12.0, cupsOpt.get());
        ConvertibleAmount converted = conversionService.convert(amount, listContextVolume);
        assertNotNull(converted);
        assertEquals(0.75, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gallonId, converted.getUnit().getId());

        // 68 US ounce = 0.53125 US gallon
        amount = new SimpleAmount(68.0, flOzOpt.get());
        converted = conversionService.convert(amount, listContextVolume);
        assertNotNull(converted);
        assertEquals(0.531, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gallonId, converted.getUnit().getId());

        // 35 US pint = 4.375 US gallon
        amount = new SimpleAmount(35.0, pintOpt.get());
        converted = conversionService.convert(amount, listContextVolume);
        assertNotNull(converted);
        assertEquals(4.375, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gallonId, converted.getUnit().getId());

        // 15 US quart = 3.75 US gallon
        amount = new SimpleAmount(15.0, quartOpt.get());
        converted = conversionService.convert(amount, listContextVolume);
        assertNotNull(converted);
        assertEquals(3.75, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gallonId, converted.getUnit().getId());

        // 7 fluid ounce (US) = 0.875 cup (US)
        amount = new SimpleAmount(7.0, flOzOpt.get());
        converted = conversionService.convert(amount, dishContextVolume);
        assertNotNull(converted);
        assertEquals(0.875, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(cupsId, converted.getUnit().getId());

        // 0.2 Pound = 3.2 Ounce
        amount = new SimpleAmount(0.2, poundOpt.get());
        converted = conversionService.convert(amount, listContext);
        assertNotNull(converted);
        assertEquals(3.2, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(ounceId, converted.getUnit().getId());

        //.4 US cup = 0.1 US quart
        amount = new SimpleAmount(0.4, cupsOpt.get());
        converted = conversionService.convert(amount, listContextVolume);
        assertNotNull(converted);
        assertEquals(0.1, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(quartId, converted.getUnit().getId());

        // 36 US ounce = 2.25 US lb
        amount = new SimpleAmount(36.00, ounceOpt.get());
        converted = conversionService.convert(amount, listContext);
        assertNotNull(converted);
        assertEquals(2.25, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(lbId, converted.getUnit().getId());

        // 38 US ounce = 1.1875 US quart
        amount = new SimpleAmount(38.00, flOzOpt.get());
        converted = conversionService.convert(amount, listContextVolume);
        assertNotNull(converted);
        assertEquals(1.188, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(quartId, converted.getUnit().getId());

        // 6 US cup = 1.5 US quart
        amount = new SimpleAmount(6.0, cupsOpt.get());
        converted = conversionService.convert(amount, listContextVolume);
        assertNotNull(converted);
        assertEquals(1.5, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(quartId, converted.getUnit().getId());

        // 1 US pint = .5 US quart
        amount = new SimpleAmount(1.00, pintOpt.get());
        converted = conversionService.convert(amount, listContextVolume);
        assertNotNull(converted);
        assertEquals(.5, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(quartId, converted.getUnit().getId());

        // 28 Ounce = 1.75 Pound
        amount = new SimpleAmount(28, ounceOpt.get());
        converted = conversionService.convert(amount, listContext);
        assertNotNull(converted);
        assertEquals(1.75, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(lbId, converted.getUnit().getId());
    }

}
