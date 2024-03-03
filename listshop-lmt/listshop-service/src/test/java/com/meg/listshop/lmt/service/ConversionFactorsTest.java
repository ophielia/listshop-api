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
import com.meg.listshop.conversion.data.pojo.ConversionContext;
import com.meg.listshop.conversion.data.pojo.ConversionContextType;
import com.meg.listshop.conversion.data.pojo.SimpleAmount;
import com.meg.listshop.conversion.data.pojo.UnitType;
import com.meg.listshop.conversion.data.repository.UnitRepository;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;
import com.meg.listshop.conversion.service.ConverterService;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.conversion.tools.RoundingUtils;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class ConversionFactorsTest {

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


    @ClassRule
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    @Test
    public void unitTestsMetricScaling() throws ConversionPathException, ConversionFactorException {
        UnitEntity litersOpt = unitRepository.findById(literId).orElse(null);
        UnitEntity milliliterOpt = unitRepository.findById(milliliterId).orElse(null);
        UnitEntity centiliterOpt = unitRepository.findById(centileterId).orElse(null);
        UnitEntity gramOpt = unitRepository.findById(gId).orElse(null);

        ConversionContext listContext = new ConversionContext(ConversionContextType.List, UnitType.METRIC);
        ConversionContext listContextVolume = new ConversionContext(ConversionContextType.List, UnitType.METRIC);
        ConversionContext dishContextVolume = new ConversionContext(ConversionContextType.Dish, UnitType.METRIC);


//        688 Gram = 0.688 Kilogram
        ConvertibleAmount amount = new SimpleAmount(688, gramOpt);
        ConvertibleAmount converted = converterService.convert(amount, listContext);
        assertNotNull(converted);
        assertEquals(0.688, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(kgId, converted.getUnit().getId());

//        .15 Liter = 15 Centiliter
        amount = new SimpleAmount(0.15, litersOpt);
        converted = converterService.convert(amount, dishContextVolume);
        assertNotNull(converted);
        assertEquals(15.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(centileterId, converted.getUnit().getId());

//        2.345 Liter = 2345 Milliliter
        amount = new SimpleAmount(.2345, litersOpt);
        converted = converterService.convert(amount, listContextVolume);
        assertNotNull(converted);
        assertEquals(234.5, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(milliliterId, converted.getUnit().getId());

        //        900 Milliliter = 0.9 Liter
        amount = new SimpleAmount(900, milliliterOpt);
        converted = converterService.convert(amount, listContextVolume);
        assertNotNull(converted);
        assertEquals(literId, converted.getUnit().getId());
        assertEquals(0.9, RoundingUtils.roundToThousandths(converted.getQuantity()));

        //        600 Milliliter = 60 Centiliter
        amount = new SimpleAmount(300, milliliterOpt);
        converted = converterService.convert(amount, dishContextVolume);
        assertNotNull(converted);
        assertEquals(30.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(centileterId, converted.getUnit().getId());

        //        50 Centiliter = 0.5 Liter
        amount = new SimpleAmount(50.0, centiliterOpt);
        converted = converterService.convert(amount, listContextVolume);
        assertNotNull(converted);
        assertEquals(0.5, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(literId, converted.getUnit().getId());

        //        0.25 Centiliter = 2.5 Milliliter
        amount = new SimpleAmount(0.25, centiliterOpt);
        converted = converterService.convert(amount, listContextVolume);
        assertNotNull(converted);
        assertEquals(2.5, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(milliliterId, converted.getUnit().getId());

    }

    @Test
    public void unitTestsMetricConversion() throws ConversionPathException, ConversionFactorException {
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

    @Test
    public void unitTestsUsScaling() throws ConversionPathException, ConversionFactorException {
        UnitEntity ounceOpt = unitRepository.findById(ounceId).orElse(null);
        UnitEntity pintOpt = unitRepository.findById(pintId).orElse(null);
        UnitEntity cupsOpt = unitRepository.findById(cupsId).orElse(null);
        UnitEntity quartOpt = unitRepository.findById(quartId).orElse(null);
        UnitEntity flOzOpt = unitRepository.findById(flOzId).orElse(null);
        UnitEntity poundOpt = unitRepository.findById(lbId).orElse(null);

        ConversionContext listContext = new ConversionContext(ConversionContextType.List, UnitType.US);
        ConversionContext listContextVolume = new ConversionContext(ConversionContextType.List, UnitType.US);
        ConversionContext dishContextVolume = new ConversionContext(ConversionContextType.Dish, UnitType.US);


        //        688 Gram = 0.688 Kilogram

// 6 US cup = 0.375 US gallon
        ConvertibleAmount amount = new SimpleAmount(12.0, cupsOpt);
        ConvertibleAmount converted = converterService.convert(amount, listContextVolume);
        assertNotNull(converted);
        assertEquals(0.75, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gallonId, converted.getUnit().getId());

        // 68 US ounce = 0.53125 US gallon
        amount = new SimpleAmount(68.0, flOzOpt);
        converted = converterService.convert(amount, listContextVolume);
        assertNotNull(converted);
        assertEquals(0.531, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gallonId, converted.getUnit().getId());

        // 35 US pint = 4.375 US gallon
        amount = new SimpleAmount(35.0, pintOpt);
        converted = converterService.convert(amount, listContextVolume);
        assertNotNull(converted);
        assertEquals(4.375, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gallonId, converted.getUnit().getId());

        // 15 US quart = 3.75 US gallon
        amount = new SimpleAmount(15.0, quartOpt);
        converted = converterService.convert(amount, listContextVolume);
        assertNotNull(converted);
        assertEquals(3.75, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gallonId, converted.getUnit().getId());

        // 7 fluid ounce (US) = 0.875 cup (US)
        amount = new SimpleAmount(7.0, flOzOpt);
        converted = converterService.convert(amount, dishContextVolume);
        assertNotNull(converted);
        assertEquals(0.875, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(cupsId, converted.getUnit().getId());

        // 0.2 Pound = 3.2 Ounce
        amount = new SimpleAmount(0.2, poundOpt);
        converted = converterService.convert(amount, listContext);
        assertNotNull(converted);
        assertEquals(3.2, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(ounceId, converted.getUnit().getId());

        // 4 US cup = 1 US quart
        amount = new SimpleAmount(4, cupsOpt);
        converted = converterService.convert(amount, listContextVolume);
        assertNotNull(converted);
        //assertEquals(1, RoundingUtils.roundToThousandths(converted.getQuantity()));
        //assertEquals(quartId, converted.getUnit().getId());

        // 36 US ounce = 2.25 US lb
        amount = new SimpleAmount(36.00, ounceOpt);
        converted = converterService.convert(amount, listContext);
        assertNotNull(converted);
        assertEquals(2.25, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(lbId, converted.getUnit().getId());

        // 38 US ounce = 1.1875 US quart
        amount = new SimpleAmount(38.00, flOzOpt);
        converted = converterService.convert(amount, listContextVolume);
        assertNotNull(converted);
        assertEquals(1.188, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(quartId, converted.getUnit().getId());

        // 6 US cup = 1.5 US quart
        amount = new SimpleAmount(6.0, cupsOpt);
        converted = converterService.convert(amount, listContextVolume);
        assertNotNull(converted);
        assertEquals(1.5, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(quartId, converted.getUnit().getId());

        // 1 US pint = .5 US quart
        amount = new SimpleAmount(1.00, pintOpt);
        converted = converterService.convert(amount, listContextVolume);
        assertNotNull(converted);
        assertEquals(.5, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(quartId, converted.getUnit().getId());

        // 28 Ounce = 1.75 Pound
        amount = new SimpleAmount(28, ounceOpt);
        converted = converterService.convert(amount, listContext);
        assertNotNull(converted);
        assertEquals(1.75, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(lbId, converted.getUnit().getId());
    }

    @Test
    public void unitHybridScaling() throws ConversionPathException, ConversionFactorException {
        UnitEntity flTspOpt = unitRepository.findById(flTeaspoonId).orElse(null);
        UnitEntity tablespoonOpt = unitRepository.findById(tbId).orElse(null);

        ConversionContext dishConversionContext = new ConversionContext(ConversionContextType.Dish, UnitType.US);


        // 4 tablespoons = 12 teaspoons

        // 3 teaspoons = 1 tablespoon - fluid
        ConvertibleAmount amount = new SimpleAmount(3.0, flTspOpt);
        ConvertibleAmount converted = converterService.convert(amount, dishConversionContext);
        assertNotNull(converted);
        //assertEquals(1.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        //assertEquals(flTablespoonId, converted.getUnit().getId());

        // 1/3 tablespoon = 1 teaspoons
        amount = new SimpleAmount(0.3334, tablespoonOpt);
        converted = converterService.convert(amount, dishConversionContext);
        assertNotNull(converted);
        assertEquals(1.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(tspId, converted.getUnit().getId());
    }

    @Test
    public void unitHybridVolumeUSScaling() throws ConversionPathException, ConversionFactorException {
        UnitEntity flTspOpt = unitRepository.findById(flTeaspoonId).orElse(null);
        UnitEntity flTbOpt = unitRepository.findById(flTablespoonId).orElse(null);

        ConversionContext dishConversionContext = new ConversionContext(ConversionContextType.Dish, UnitType.US);

        //  24 teaspoons = 0.5 cup
        ConvertibleAmount amount = new SimpleAmount(24.0, flTspOpt);
        ConvertibleAmount converted = converterService.convert(amount, dishConversionContext);
        assertNotNull(converted);
        assertEquals(0.5, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(cupsId, converted.getUnit().getId());

        // 6 teaspoons = 1 ounce
        amount = new SimpleAmount(18.0, flTspOpt);
        converted = converterService.convert(amount, dishConversionContext);
        assertNotNull(converted);
        assertEquals(3.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(flOzId, converted.getUnit().getId());

        // 16 tablespoons = 1 cup
        amount = new SimpleAmount(16.0, flTbOpt);
        converted = converterService.convert(amount, dishConversionContext);
        assertNotNull(converted);
        assertEquals(1.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(cupsId, converted.getUnit().getId());

        // 48 tablespoons = 0.75 quart
        amount = new SimpleAmount(48, flTbOpt);
        converted = converterService.convert(amount, dishConversionContext);
        assertNotNull(converted);
        assertEquals(0.75, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(quartId, converted.getUnit().getId());


        // 256 tablespoons = 1 gallon
        amount = new SimpleAmount(256, flTbOpt);
        converted = converterService.convert(amount, dishConversionContext);
        assertNotNull(converted);
        assertEquals(1, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gallonId, converted.getUnit().getId());

        // 24 T = 0.75 pint
        amount = new SimpleAmount(24, flTbOpt);
        converted = converterService.convert(amount, dishConversionContext);
        assertNotNull(converted);
        assertEquals(0.75, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(pintId, converted.getUnit().getId());

        // 4 tablespoons = 2 oz
        amount = new SimpleAmount(4, flTbOpt);
        converted = converterService.convert(amount, dishConversionContext);
        assertNotNull(converted);
        assertEquals(2, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(flOzId, converted.getUnit().getId());

        //  576 teaspoons = 0.75 gallon
        amount = new SimpleAmount(576, flTspOpt);
        converted = converterService.convert(amount, dishConversionContext);
        assertNotNull(converted);
        assertEquals(0.75, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gallonId, converted.getUnit().getId());

        // 96 teaspoons = 1 pint
        amount = new SimpleAmount(96, flTspOpt);
        converted = converterService.convert(amount, dishConversionContext);
        assertNotNull(converted);
        assertEquals(1.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(pintId, converted.getUnit().getId());

    }
    @Test
    public void unitHybridVolumeMetricScaling() throws ConversionPathException, ConversionFactorException {
        UnitEntity flTspOpt = unitRepository.findById(flTeaspoonId).orElse(null);
        UnitEntity flTbOpt = unitRepository.findById(flTablespoonId).orElse(null);

        ConversionContext dishConversionContext = new ConversionContext(ConversionContextType.Dish, UnitType.METRIC);

         // teaspoon to centiliter  5.914 Centiliter = 12 US teaspoon
        ConvertibleAmount amount = new SimpleAmount(12, flTspOpt);
        ConvertibleAmount converted = converterService.convert(amount, dishConversionContext);
        assertNotNull(converted);
        assertEquals(5.915, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(centileterId, converted.getUnit().getId());

         // teaspoon to liter  .5963 liters = 121 US teaspoon
        amount = new SimpleAmount(121, flTspOpt);
        converted = converterService.convert(amount, dishConversionContext);
        assertNotNull(converted);
        assertEquals(0.596, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(literId, converted.getUnit().getId());

        // teaspoon to centiliter 14.787 Centiliter = 30.0 US teaspoon
        amount = new SimpleAmount(30.0, flTspOpt);
        converted = converterService.convert(amount, dishConversionContext);
        assertNotNull(converted);
        assertEquals(14.787, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(centileterId, converted.getUnit().getId());

         // tablespoon to liter   0.6 Liter = 40.5768272 US tablespoon
        amount = new SimpleAmount(40.577, flTbOpt);
        converted = converterService.convert(amount, dishConversionContext);
        assertNotNull(converted);
        assertEquals(0.6, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(literId, converted.getUnit().getId());

         // tablespoon to centiliter  3 Centiliter = 2 US tablespoon
        amount = new SimpleAmount(10.0, flTbOpt);
        converted = converterService.convert(amount, dishConversionContext);
        assertNotNull(converted);
        assertEquals(14.787, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(centileterId, converted.getUnit().getId());

    }

}
