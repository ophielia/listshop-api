package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.SimpleAmount;
import com.meg.listshop.conversion.data.pojo.UnitFlavor;
import com.meg.listshop.conversion.data.pojo.UnitSubtype;
import com.meg.listshop.conversion.data.pojo.UnitType;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.service.ConversionContext;
import com.meg.listshop.conversion.service.ConversionSpec;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.conversion.service.tools.ChainConversionHandlerBuilder;
import com.meg.listshop.conversion.service.tools.ConversionSpecBuilder;
import com.meg.listshop.conversion.tools.ConversionTestTools;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConversionHandlerTest {

    ChainConversionHandler conversionHandler;

    @BeforeEach
    void setUp() {
        // make conversion source
        conversionHandler = (ChainConversionHandler) new ChainConversionHandlerBuilder()
                .withFactor(ConversionTestTools.makeUSUnit(1L, UnitSubtype.WEIGHT),
                        ConversionTestTools.makeMetricUnit(2L, UnitSubtype.WEIGHT),
                        0.5)
                .withFactor(ConversionTestTools.makeUSUnit(3L, UnitSubtype.WEIGHT),
                        ConversionTestTools.makeMetricUnit(4L, UnitSubtype.WEIGHT),
                        0.25)
                .withFactor(ConversionTestTools.makeUSUnit(5L, UnitSubtype.WEIGHT),
                        ConversionTestTools.makeMetricUnit(6L, UnitSubtype.WEIGHT),
                        .9)
                .withFactor(ConversionTestTools.makeUSUnit(1L, UnitSubtype.WEIGHT),
                        ConversionTestTools.makeMetricUnit(6L, UnitSubtype.WEIGHT),
                        .5)
                .withFromSpec(UnitType.US, UnitSubtype.WEIGHT)
                .withToSpec(UnitType.METRIC, UnitSubtype.WEIGHT)
                .build();

    }

    @Test
    void testHandles() {
        // test basic - imperial to metric weight
        ConversionSpec fromSpec = new ConversionSpecBuilder().withUnitType(UnitType.US).withUnitSubtype(UnitSubtype.WEIGHT).build();
        ConversionSpec toSpec = new ConversionSpecBuilder().withUnitType(UnitType.METRIC).withUnitSubtype(UnitSubtype.WEIGHT).build();
        ConversionSpec notHandledSpec = new ConversionSpecBuilder().withUnitType(UnitType.METRIC).withUnitSubtype(UnitSubtype.WEIGHT).withFlavor(UnitFlavor.ListUnit).build();
        assertTrue(conversionHandler.handlesDomain(fromSpec.getUnitType(), toSpec.getUnitType()));
        assertTrue(conversionHandler.handlesDomain(toSpec.getUnitType(), fromSpec.getUnitType()));
        assertFalse(conversionHandler.handlesDomain(toSpec.getUnitType(), notHandledSpec.getUnitType()));

    }

    @Test
    void testFlavorHandles() {
        ChainConversionHandler flavorHandler = (ChainConversionHandler) new ChainConversionHandlerBuilder()
                .withFactor(ConversionTestTools.makeUSUnit(1L, UnitSubtype.WEIGHT),
                        ConversionTestTools.makeMetricUnit(2L, UnitSubtype.WEIGHT),
                        0.5)
                .withFactor(ConversionTestTools.makeUSUnit(3L, UnitSubtype.WEIGHT),
                        ConversionTestTools.makeMetricUnit(4L, UnitSubtype.WEIGHT),
                        0.25)
                .withFactor(ConversionTestTools.makeUSUnit(5L, UnitSubtype.WEIGHT),
                        ConversionTestTools.makeMetricUnit(6L, UnitSubtype.WEIGHT),
                        .9)
                .withFactor(ConversionTestTools.makeUSUnit(1L, UnitSubtype.WEIGHT),
                        ConversionTestTools.makeMetricUnit(6L, UnitSubtype.WEIGHT),
                        .5)
                .withFromSpec(UnitType.US, UnitSubtype.WEIGHT)
                .withToSpec(UnitType.US, UnitSubtype.WEIGHT, UnitFlavor.ListUnit)
                .withOneWay()
                .build();

        // test basic - imperial to metric weight
        ConversionSpec fromSpec = new ConversionSpecBuilder().withUnitType(UnitType.US).withUnitSubtype(UnitSubtype.WEIGHT).build();
        ConversionSpec flavorSpec = new ConversionSpecBuilder().withUnitType(UnitType.US).withUnitSubtype(UnitSubtype.WEIGHT).withFlavor(UnitFlavor.ListUnit).build();
        //assertTrue(flavorHandler.convertsToDomain(fromSpec.getUnitType(), flavorSpec.getUnitType()));

    }

    @Test
    void testDomainHandles_DoesntHandle() {
        ChainConversionHandler flavorHandler = (ChainConversionHandler) new ChainConversionHandlerBuilder()
                .withFactor(ConversionTestTools.makeUSUnit(1L, UnitSubtype.WEIGHT),
                        ConversionTestTools.makeMetricUnit(2L, UnitSubtype.WEIGHT),
                        0.5)
                .withFactor(ConversionTestTools.makeUSUnit(3L, UnitSubtype.WEIGHT),
                        ConversionTestTools.makeMetricUnit(4L, UnitSubtype.WEIGHT),
                        0.25)
                .withFactor(ConversionTestTools.makeUSUnit(5L, UnitSubtype.WEIGHT),
                        ConversionTestTools.makeMetricUnit(6L, UnitSubtype.WEIGHT),
                        .9)
                .withFactor(ConversionTestTools.makeUSUnit(1L, UnitSubtype.WEIGHT),
                        ConversionTestTools.makeMetricUnit(6L, UnitSubtype.WEIGHT),
                        .5)
                .withFromSpec(UnitType.US, UnitSubtype.WEIGHT)
                .withToSpec(UnitType.US, UnitSubtype.WEIGHT)
                .build();

        // test basic - imperial to metric weight
        ConversionSpec fromSpec = new ConversionSpecBuilder().withUnitType(UnitType.US).withUnitSubtype(UnitSubtype.WEIGHT).build();
        ConversionSpec flavorSpec = new ConversionSpecBuilder().withUnitType(UnitType.UNIT).withUnitSubtype(UnitSubtype.WEIGHT).withFlavor(UnitFlavor.ListUnit).build();
        //assertFalse(flavorHandler.convertsToDomain(fromSpec.getUnitType(), flavorSpec.getUnitType()));

    }



    @Test
    void testConvert() throws ConversionFactorException {
        UnitEntity startUnit = ConversionTestTools.makeMetricUnit(null, UnitSubtype.WEIGHT);
        startUnit.setId(6L);
        ConvertibleAmount startAmount = new SimpleAmount(1D, startUnit);
        ConversionSpec toSpec = new ConversionSpecBuilder().withUnitType(UnitType.US).withFlavor(UnitFlavor.Weight).build();
        ConversionContext context = new ConversionContext(startAmount, toSpec);
        ConvertibleAmount result = conversionHandler.convert(startAmount, context);
        assertNotNull(result);
        assertEquals(1.11, ConversionTestTools.roundToHundredths(result.getQuantity()));
        assertEquals(5L, result.getUnit().getId());
        assertEquals(UnitType.US, result.getUnit().getType());

        ConversionSpec exactSpec = new ConversionSpecBuilder()
                .withUnitId(1L)
                .withUnitType(UnitType.US).withFlavor(UnitFlavor.Weight).build();
        context = new ConversionContext(startAmount, exactSpec);
        ConvertibleAmount exactResult = conversionHandler.convert(startAmount, context);
        assertNotNull(exactResult);
        assertEquals(2D, ConversionTestTools.roundToHundredths(exactResult.getQuantity()));
        assertEquals(1L, exactResult.getUnit().getId());
        assertEquals(UnitType.US, exactResult.getUnit().getType());
    }



}
