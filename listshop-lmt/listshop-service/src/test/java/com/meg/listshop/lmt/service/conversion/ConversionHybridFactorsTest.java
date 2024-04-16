/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.lmt.service.conversion;

import com.meg.listshop.Application;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.conversion.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.ConversionRequest;
import com.meg.listshop.conversion.data.pojo.ConversionTargetType;
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
public class ConversionHybridFactorsTest {

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
    public void unitHybridScaling() throws ConversionPathException, ConversionFactorException {
        UnitEntity flTspOpt = unitRepository.findById(flTeaspoonId).orElse(null);
        UnitEntity tablespoonOpt = unitRepository.findById(tbId).orElse(null);

        ConversionRequest dishConversionContext = new ConversionRequest(ConversionTargetType.Dish, UnitType.US);


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

        ConversionRequest dishConversionContext = new ConversionRequest(ConversionTargetType.Dish, UnitType.US);

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

        ConversionRequest dishConversionContext = new ConversionRequest(ConversionTargetType.Dish, UnitType.METRIC);

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
