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
public class ConversionUSFactorsTest {

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
    public void unitTestsUsScaling() throws ConversionPathException, ConversionFactorException {
        UnitEntity ounceOpt = unitRepository.findById(ounceId).orElse(null);
        UnitEntity pintOpt = unitRepository.findById(pintId).orElse(null);
        UnitEntity cupsOpt = unitRepository.findById(cupsId).orElse(null);
        UnitEntity quartOpt = unitRepository.findById(quartId).orElse(null);
        UnitEntity flOzOpt = unitRepository.findById(flOzId).orElse(null);
        UnitEntity poundOpt = unitRepository.findById(lbId).orElse(null);

        ConversionRequest listContext = new ConversionRequest(ConversionTargetType.List, UnitType.US);
        ConversionRequest listContextVolume = new ConversionRequest(ConversionTargetType.List, UnitType.US);
        ConversionRequest dishContextVolume = new ConversionRequest(ConversionTargetType.Dish, UnitType.US);


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


}
