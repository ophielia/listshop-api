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
public class ConversionUSFactorsTest {

    private static final Long ounceId = 1009L;
    private static final Long lbId = 1008L;
    private static final Long flTeaspoonId = 1019L;
    private static final Long flTablespoonId = 1021L;

    private static final Long gallonId = 1005L;
    private static final Long quartId = 1010L;
    private static final Long pintId = 1006L;
    private static final Long cupsId = 1017L;
    private static final Long flOzId = 1007L;
    @Autowired
    ConverterService converterService;

    @Autowired
    UnitRepository unitRepository;


    @ClassRule
    public static ListShopPostgresqlContainer postgresSQLContainer = ListShopPostgresqlContainer.getInstance();

    @Test
    public void unitTestsUsScaling() throws ConversionPathException, ConversionFactorException {
        UnitEntity ounceOpt = unitRepository.findById(ounceId).orElse(null);
        UnitEntity pintOpt = unitRepository.findById(pintId).orElse(null);
        UnitEntity cupsOpt = unitRepository.findById(cupsId).orElse(null);
        UnitEntity quartOpt = unitRepository.findById(quartId).orElse(null);
        UnitEntity flOzOpt = unitRepository.findById(flOzId).orElse(null);
        UnitEntity poundOpt = unitRepository.findById(lbId).orElse(null);
        UnitEntity gallonOpt = unitRepository.findById(gallonId).orElse(null);


// 6 US cup = 0.375 US gallon
        ConvertibleAmount amount = new SimpleAmount(12.0, cupsOpt);
        ConvertibleAmount converted = converterService.convert(amount, gallonOpt);
        assertNotNull(converted);
        assertEquals(0.75, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gallonId, converted.getUnit().getId());

        // 68 US ounce = 0.53125 US gallon
        amount = new SimpleAmount(68.0, flOzOpt);
        converted = converterService.convert(amount, gallonOpt);
        assertNotNull(converted);
        assertEquals(0.531, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gallonId, converted.getUnit().getId());

        // 35 US pint = 4.375 US gallon
        amount = new SimpleAmount(35.0, pintOpt);
        converted = converterService.convert(amount, gallonOpt);
        assertNotNull(converted);
        assertEquals(4.375, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gallonId, converted.getUnit().getId());

        // 15 US quart = 3.75 US gallon
        amount = new SimpleAmount(15.0, quartOpt);
        converted = converterService.convert(amount, gallonOpt);
        assertNotNull(converted);
        assertEquals(3.75, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gallonId, converted.getUnit().getId());

        // 7 fluid ounce (US) = 0.875 cup (US)
        amount = new SimpleAmount(7.0, flOzOpt);
        converted = converterService.convert(amount, cupsOpt);
        assertNotNull(converted);
        assertEquals(0.875, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(cupsId, converted.getUnit().getId());

        // 0.2 Pound = 3.2 Ounce
        amount = new SimpleAmount(0.2, poundOpt);
        converted = converterService.convert(amount, ounceOpt);
        assertNotNull(converted);
        assertEquals(3.2, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(ounceId, converted.getUnit().getId());

        // 4 US cup = 1 US quart
        amount = new SimpleAmount(4, cupsOpt);
        converted = converterService.convert(amount, quartOpt);
        assertNotNull(converted);
        assertEquals(1, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(quartId, converted.getUnit().getId());

        // 36 US ounce = 2.25 US lb
        amount = new SimpleAmount(36.00, ounceOpt);
        converted = converterService.convert(amount, poundOpt);
        assertNotNull(converted);
        assertEquals(2.25, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(lbId, converted.getUnit().getId());

        // 38 US ounce = 1.1875 US quart
        amount = new SimpleAmount(38.00, flOzOpt);
        converted = converterService.convert(amount, quartOpt);
        assertNotNull(converted);
        assertEquals(1.188, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(quartId, converted.getUnit().getId());

        // 6 US cup = 1.5 US quart
        amount = new SimpleAmount(6.0, cupsOpt);
        converted = converterService.convert(amount, quartOpt);
        assertNotNull(converted);
        assertEquals(1.5, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(quartId, converted.getUnit().getId());

        // 1 US pint = .5 US quart
        amount = new SimpleAmount(1.00, pintOpt);
        converted = converterService.convert(amount, quartOpt);
        assertNotNull(converted);
        assertEquals(.5, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(quartId, converted.getUnit().getId());

        // 28 Ounce = 1.75 Pound
        amount = new SimpleAmount(28, ounceOpt);
        converted = converterService.convert(amount, poundOpt);
        assertNotNull(converted);
        assertEquals(1.75, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(lbId, converted.getUnit().getId());
    }

    @Test
    public void unitUSVolumeScaling() throws ConversionPathException, ConversionFactorException {
        UnitEntity flTspOpt = unitRepository.findById(flTeaspoonId).orElse(null);
        UnitEntity flTbOpt = unitRepository.findById(flTablespoonId).orElse(null);

        ConversionRequest dishConversionContext = new ConversionRequest(ConversionTargetType.Dish, DomainType.METRIC);

        // teaspoon to centiliter  12 US teaspoon => 2 fl oz
        ConvertibleAmount amount = new SimpleAmount(12, flTspOpt);
        ConvertibleAmount converted = converterService.convert(amount, dishConversionContext);
        assertNotNull(converted);
        assertEquals(2.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(flOzId, converted.getUnit().getId());

        // teaspoon to liter  121 teaspoons => 1.26 pints
        amount = new SimpleAmount(121, flTspOpt);
        converted = converterService.convert(amount, dishConversionContext);
        assertNotNull(converted);
        assertEquals(1.26, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(pintId, converted.getUnit().getId());

        // teaspoon to centiliter 14.787 Centiliter = 30.0 US teaspoon
        amount = new SimpleAmount(30.0, flTspOpt);
        converted = converterService.convert(amount, dishConversionContext);
        assertNotNull(converted);
        assertEquals(0.625, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(cupsId, converted.getUnit().getId());

        // tablespoon to liter   0.6 Liter = 40.5768272 US tablespoon
        amount = new SimpleAmount(40.577, flTbOpt);
        converted = converterService.convert(amount, dishConversionContext);
        assertNotNull(converted);
        assertEquals(1.268, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(pintId, converted.getUnit().getId());

        // tablespoon to centiliter  3 Centiliter = 2 US tablespoon
        amount = new SimpleAmount(10.0, flTbOpt);
        converted = converterService.convert(amount, dishConversionContext);
        assertNotNull(converted);
        assertEquals(0.625, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(cupsId, converted.getUnit().getId());

        //  24 teaspoons = 0.5 cup
        amount = new SimpleAmount(24.0, flTspOpt);
        converted = converterService.convert(amount, dishConversionContext);
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

}
