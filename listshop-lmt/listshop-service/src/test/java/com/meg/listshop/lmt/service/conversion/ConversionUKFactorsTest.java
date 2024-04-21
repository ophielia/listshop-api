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

    @Autowired
    ConverterService converterService;

    @Autowired
    UnitRepository unitRepository;


    @ClassRule
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    @Test
    public void unitUKUnitScalingConversions() throws ConversionPathException, ConversionFactorException {

 // cup (fluid) (UK)	gallon (UK)
 // cup (fluid) (UK)	pint (UK)
 // cup (fluid) (UK)	quart (UK)
 // cup (fluid) (UK)	fl oz (UK)
 // milliliter	gallon (UK)
 //         milliliter	pint (UK)
 // milliliter	quart (UK)
 //         milliliter	fl oz (UK)




        UnitEntity gallonOpt = unitRepository.findById(gallonId).orElse(null);
        UnitEntity tspOpt = unitRepository.findById(tspId).orElse(null);
        UnitEntity tbspOpt = unitRepository.findById(tbspId).orElse(null);
        UnitEntity pintOpt = unitRepository.findById(pintId).orElse(null);
        UnitEntity quartOpt = unitRepository.findById(quartId).orElse(null);
        UnitEntity flOzIOpt = unitRepository.findById(flOzId).orElse(null);
        UnitEntity mlOpt = unitRepository.findById(mlId).orElse(null);
        UnitEntity literOpt = unitRepository.findById(literId).orElse(null);
        UnitEntity cupsOpt = unitRepository.findById(cupsId).orElse(null);


    // cups to gallon  28.823 cups => 1.801 gallons
        ConvertibleAmount amount = new SimpleAmount(28.823, cupsOpt);
        ConvertibleAmount converted = converterService.convert(amount, gallonOpt);
        assertNotNull(converted);
        assertEquals(1.801, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gallonId, converted.getUnit().getId());

        // cups to pint      3.122 cups => 1.5 pints
        amount = new SimpleAmount(3.122, cupsOpt);
        converted = converterService.convert(amount, pintOpt);
        assertNotNull(converted);
        assertEquals(1.561, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(pintId, converted.getUnit().getId());

    // cups to quart   6.245 cups => 1.561 quarts
        amount = new SimpleAmount(6.245, cupsOpt);
        converted = converterService.convert(amount, quartOpt);
        assertNotNull(converted);
        assertEquals(1.561, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(quartId, converted.getUnit().getId());

        // 2 cup (fluid) (UK) => 20 fl oz (UK)
        amount = new SimpleAmount(2.0, cupsOpt);
        converted = converterService.convert(amount, flOzIOpt);
        assertNotNull(converted);
        assertEquals(20.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(flOzId, converted.getUnit().getId());


        // quart to gallon  6 quarts => 1.5 gallons
        amount = new SimpleAmount(6.0, quartOpt);
        converted = converterService.convert(amount, gallonOpt);
        assertNotNull(converted);
        assertEquals(1.5, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gallonId, converted.getUnit().getId());



        // fl oz to pint  0.25 imperial pint => 5 fl oz
        amount = new SimpleAmount(0.25, pintOpt);
        converted = converterService.convert(amount, flOzIOpt);
        assertNotNull(converted);
        assertEquals(5.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(flOzId, converted.getUnit().getId());

        // 20 teaspoon (fluid) (UK) => 0.41666666666666 cup (fluid) (UK)
        amount = new SimpleAmount(20, tspOpt);
        converted = converterService.convert(amount, cupsOpt);
        assertNotNull(converted);
        assertEquals(0.417, RoundingUtils.roundToThousandths(converted.getQuantity()));
       assertEquals(cupsId, converted.getUnit().getId());

        // 10 tablespoon (fluid) (UK) => 0.2500001 pint (UK)
        amount = new SimpleAmount(10, tbspOpt);
        converted = converterService.convert(amount, pintOpt);
        assertNotNull(converted);
        assertEquals(0.250, RoundingUtils.roundToThousandths(converted.getQuantity()));
         assertEquals(pintId, converted.getUnit().getId());

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




        UnitEntity gallonOpt = unitRepository.findById(gallonId).orElse(null);
        UnitEntity pintOpt = unitRepository.findById(pintId).orElse(null);
        UnitEntity quartOpt = unitRepository.findById(quartId).orElse(null);
        UnitEntity flOzIOpt = unitRepository.findById(flOzId).orElse(null);
        UnitEntity mlOpt = unitRepository.findById(mlId).orElse(null);
        UnitEntity literOpt = unitRepository.findById(literId).orElse(null);
        UnitEntity cupsOpt = unitRepository.findById(cupsId).orElse(null);



        // cups to pint      3.122 cups => 1.3 pints
        //MM issue here
        ConvertibleAmount amount = new SimpleAmount(3.122, cupsOpt);
        ConvertibleAmount converted = converterService.convert(amount, pintOpt);
        assertNotNull(converted);
        assertEquals(1.561, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(pintId, converted.getUnit().getId());
 }

}
