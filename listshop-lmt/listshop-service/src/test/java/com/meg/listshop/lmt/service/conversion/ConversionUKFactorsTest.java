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
import com.meg.listshop.conversion.data.pojo.SimpleAmount;
import com.meg.listshop.common.data.repository.UnitRepository;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;
import com.meg.listshop.conversion.service.ConverterService;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.conversion.tools.RoundingUtils;
import org.junit.ClassRule;
import org.junit.Ignore;
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
@Ignore
public class ConversionUKFactorsTest {

    private static final Long cupsId = 1028L;
    private static final Long gallonId = 1024L;
    private static final Long tspId = 1051L;
    private static final Long tbspId = 1052L;
    private static final Long pintId = 1025L;
    private static final Long quartId = 1027L;
    private static final Long flOzId = 1026L;
    private static final Long mlId = 1004L;
    private static final Long literId = 1003L;
    private static final Long centiliterId = 1015L;

    private static final Long cupsUsId = 1017L;
    private static final Long gallonUsId = 1005L;
    private static final Long tspUsId = 1019L;
    private static final Long tbspUsId = 1021L;
    private static final Long pintUsId = 1006L;
    private static final Long quartUsId = 1010L;
    private static final Long flOzUsId = 1007L;


    @Autowired
    ConverterService converterService;

    @Autowired
    UnitRepository unitRepository;


    @ClassRule
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    @Test
    public void unitUKUnitConversions() throws ConversionPathException, ConversionFactorException {
        UnitEntity pintOpt = unitRepository.findById(pintId).orElse(null);
        UnitEntity quartOpt = unitRepository.findById(quartId).orElse(null);
        UnitEntity mlOpt = unitRepository.findById(mlId).orElse(null);
        UnitEntity literOpt = unitRepository.findById(literId).orElse(null);
        UnitEntity centiliterOpt = unitRepository.findById(centiliterId).orElse(null);

        // 0.5 liter => 0.87987699319635 pint (UK)
        ConvertibleAmount amount = new SimpleAmount(0.5, literOpt);
        ConvertibleAmount converted = converterService.convert(amount, pintOpt);
        assertNotNull(converted);
        assertEquals(0.88, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(pintId, converted.getUnit().getId());

        // 1.2 liter => 1.05585239183562 quart (UK)
        amount = new SimpleAmount(1.056, quartOpt);
        converted = converterService.convert(amount, literOpt);
        assertNotNull(converted);
        assertEquals(1.2, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(literId, converted.getUnit().getId());

        // 200 milliliter => 0.35195079727854 pint (UK)
        amount = new SimpleAmount(200.0, mlOpt);
        converted = converterService.convert(amount, pintOpt);
        assertNotNull(converted);
        assertEquals(0.352, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(pintId, converted.getUnit().getId());

        // 200 milliliter => 0.17597539863927 quart (UK)
        amount = new SimpleAmount(0.176, quartOpt);
        converted = converterService.convert(amount, mlOpt);
        assertNotNull(converted);
        assertEquals(200.028, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(mlId, converted.getUnit().getId());

        // 20 centiliter => 0.17597539863927 quart (UK)
        amount = new SimpleAmount(20, centiliterOpt);
        converted = converterService.convert(amount, quartOpt);
        assertNotNull(converted);
        assertEquals(0.176, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(quartId, converted.getUnit().getId());

        // 20 centiliter => 0.35195079727854 pint (UK)
        amount = new SimpleAmount(0.352, pintOpt);
        converted = converterService.convert(amount, centiliterOpt);
        assertNotNull(converted);
        assertEquals(20.003, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(centiliterId, converted.getUnit().getId());


    }

    @Test
    public void unitUKtoUSConversions() throws ConversionPathException, ConversionFactorException {
        UnitEntity gallonOpt = unitRepository.findById(gallonId).orElse(null);
        UnitEntity tspOpt = unitRepository.findById(tspId).orElse(null);
        UnitEntity tbspOpt = unitRepository.findById(tbspId).orElse(null);
        UnitEntity pintOpt = unitRepository.findById(pintId).orElse(null);
        UnitEntity quartOpt = unitRepository.findById(quartId).orElse(null);
        UnitEntity flOzIOpt = unitRepository.findById(flOzId).orElse(null);
        UnitEntity cupsOpt = unitRepository.findById(cupsId).orElse(null);

        UnitEntity gallonUsOpt = unitRepository.findById(gallonUsId).orElse(null);
        UnitEntity tspUsOpt = unitRepository.findById(tspUsId).orElse(null);
        UnitEntity tbspUsOpt = unitRepository.findById(tbspUsId).orElse(null);
        UnitEntity pintUsOpt = unitRepository.findById(pintUsId).orElse(null);
        UnitEntity quartUsOpt = unitRepository.findById(quartUsId).orElse(null);
        UnitEntity flOzIUsOpt = unitRepository.findById(flOzUsId).orElse(null);
        UnitEntity cupsUsOpt = unitRepository.findById(cupsUsId).orElse(null);

        // US to UK Gallon
        ConvertibleAmount amount = new SimpleAmount(1.0, gallonUsOpt);
        ConvertibleAmount converted = converterService.convert(amount, gallonOpt);
        assertNotNull(converted);
        assertEquals(0.833, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gallonId, converted.getUnit().getId());

        // US to UK Quart
        amount = new SimpleAmount(1.056, quartUsOpt);
        converted = converterService.convert(amount, quartOpt);
        assertNotNull(converted);
        assertEquals(0.879, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(quartId, converted.getUnit().getId());

        // UK to US Pint
        amount = new SimpleAmount(1.056, pintUsOpt);
        converted = converterService.convert(amount, pintOpt);
        assertNotNull(converted);
        assertEquals(0.879, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(pintId, converted.getUnit().getId());

        // UK to US Fl Ounces
        amount = new SimpleAmount(100, flOzIOpt);
        converted = converterService.convert(amount, flOzIUsOpt);
        assertNotNull(converted);
        assertEquals(96.076, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(flOzUsId, converted.getUnit().getId());

        // UK to US Cups
        amount = new SimpleAmount(1, cupsOpt);
        converted = converterService.convert(amount, cupsUsOpt);
        assertNotNull(converted);
        assertEquals(1.201, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(cupsUsId, converted.getUnit().getId());

        // UK to US Teaspoons
        amount = new SimpleAmount(1, tspOpt);
        converted = converterService.convert(amount, tspUsOpt);
        assertNotNull(converted);
        assertEquals(1.201, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(tspUsId, converted.getUnit().getId());

        // UK to US Tablespoon
        amount = new SimpleAmount(100, tbspOpt);
        converted = converterService.convert(amount, tbspUsOpt);
        assertNotNull(converted);
        assertEquals(120.094, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(tbspUsId, converted.getUnit().getId());


    }

    @Test
    public void unitFocus() throws ConversionPathException, ConversionFactorException {

        // cup (fluid) (UK)	gallon (UK)
        // cup (fluid) (UK)	pint (UK)
        // cup (fluid) (UK)	quart (UK)
        // cup (fluid) (UK)	fl oz (UK)
        // milliliter	gallon (UK)
        //         milliliter	pint (UK)
        // milliliter	quart (UK)
        //         milliliter	fl oz (UK)


        UnitEntity pintOpt = unitRepository.findById(pintId).orElse(null);
        UnitEntity cupsOpt = unitRepository.findById(cupsId).orElse(null);


        // cups to pint      3.122 cups => 1.3 pints
        ConvertibleAmount amount = new SimpleAmount(3.122, cupsOpt);
        ConvertibleAmount converted = converterService.convert(amount, pintOpt);
        assertNotNull(converted);
        assertEquals(1.561, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(pintId, converted.getUnit().getId());
    }

}
