package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.SimpleAmount;
import com.meg.listshop.conversion.data.pojo.UnitFlavor;
import com.meg.listshop.conversion.data.pojo.UnitSubtype;
import com.meg.listshop.conversion.data.pojo.UnitType;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.service.ConversionSpec;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.conversion.service.tools.ConversionHandlerBuilder;
import com.meg.listshop.conversion.service.tools.ConversionSpecBuilder;
import com.meg.listshop.conversion.tools.ConversionTestTools;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConversionHandlerTest {

    ConversionHandler conversionHandler;

    @BeforeEach
    void setUp() {
        // make conversion source
        conversionHandler = new ConversionHandlerBuilder()
                .withFactor(ConversionTestTools.makeImperialUnit(1L, UnitSubtype.WEIGHT),
                        ConversionTestTools.makeMetricUnit(2L, UnitSubtype.WEIGHT),
                        0.5)
                .withFactor(ConversionTestTools.makeImperialUnit(3L, UnitSubtype.WEIGHT),
                        ConversionTestTools.makeMetricUnit(4L, UnitSubtype.WEIGHT),
                        0.25)
                .withFactor(ConversionTestTools.makeImperialUnit(5L, UnitSubtype.WEIGHT),
                        ConversionTestTools.makeMetricUnit(6L, UnitSubtype.WEIGHT),
                        .9)
                .withFactor(ConversionTestTools.makeImperialUnit(1L, UnitSubtype.WEIGHT),
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
        assertTrue(conversionHandler.handles(fromSpec, toSpec));
        assertTrue(conversionHandler.handles(toSpec, fromSpec));
        assertFalse(conversionHandler.handles(toSpec, notHandledSpec));

    }

    @Test
    void testFlavorHandles() {
        ConversionHandler flavorHandler = new ConversionHandlerBuilder()
                .withFactor(ConversionTestTools.makeImperialUnit(1L, UnitSubtype.WEIGHT),
                        ConversionTestTools.makeMetricUnit(2L, UnitSubtype.WEIGHT),
                        0.5)
                .withFactor(ConversionTestTools.makeImperialUnit(3L, UnitSubtype.WEIGHT),
                        ConversionTestTools.makeMetricUnit(4L, UnitSubtype.WEIGHT),
                        0.25)
                .withFactor(ConversionTestTools.makeImperialUnit(5L, UnitSubtype.WEIGHT),
                        ConversionTestTools.makeMetricUnit(6L, UnitSubtype.WEIGHT),
                        .9)
                .withFactor(ConversionTestTools.makeImperialUnit(1L, UnitSubtype.WEIGHT),
                        ConversionTestTools.makeMetricUnit(6L, UnitSubtype.WEIGHT),
                        .5)
                .withFromSpec(UnitType.US, UnitSubtype.WEIGHT)
                .withToSpec(UnitType.US, UnitSubtype.WEIGHT, UnitFlavor.ListUnit)
                .withOneWay()
                .build();

        // test basic - imperial to metric weight
        ConversionSpec fromSpec = new ConversionSpecBuilder().withUnitType(UnitType.US).withUnitSubtype(UnitSubtype.WEIGHT).build();
        ConversionSpec flavorSpec = new ConversionSpecBuilder().withUnitType(UnitType.US).withUnitSubtype(UnitSubtype.WEIGHT).withFlavor(UnitFlavor.ListUnit).build();
        assertTrue(flavorHandler.handles(fromSpec, flavorSpec));

    }

    @Test
    void testFlavorHandles_DoesntHandle() {
        ConversionHandler flavorHandler = new ConversionHandlerBuilder()
                .withFactor(ConversionTestTools.makeImperialUnit(1L, UnitSubtype.WEIGHT),
                        ConversionTestTools.makeMetricUnit(2L, UnitSubtype.WEIGHT),
                        0.5)
                .withFactor(ConversionTestTools.makeImperialUnit(3L, UnitSubtype.WEIGHT),
                        ConversionTestTools.makeMetricUnit(4L, UnitSubtype.WEIGHT),
                        0.25)
                .withFactor(ConversionTestTools.makeImperialUnit(5L, UnitSubtype.WEIGHT),
                        ConversionTestTools.makeMetricUnit(6L, UnitSubtype.WEIGHT),
                        .9)
                .withFactor(ConversionTestTools.makeImperialUnit(1L, UnitSubtype.WEIGHT),
                        ConversionTestTools.makeMetricUnit(6L, UnitSubtype.WEIGHT),
                        .5)
                .withFromSpec(UnitType.US, UnitSubtype.WEIGHT)
                .withToSpec(UnitType.US, UnitSubtype.WEIGHT)
                .build();

        // test basic - imperial to metric weight
        ConversionSpec fromSpec = new ConversionSpecBuilder().withUnitType(UnitType.US).withUnitSubtype(UnitSubtype.WEIGHT).build();
        ConversionSpec flavorSpec = new ConversionSpecBuilder().withUnitType(UnitType.US).withUnitSubtype(UnitSubtype.WEIGHT).withFlavor(UnitFlavor.ListUnit).build();
        assertFalse(flavorHandler.handles(fromSpec, flavorSpec));

    }

    @Test
    void testConvertsTo() {
        ConversionSpec fromSpec = new ConversionSpecBuilder().withUnitType(UnitType.US).withUnitSubtype(UnitSubtype.WEIGHT).build();
        ConversionSpec toSpec = new ConversionSpecBuilder().withUnitType(UnitType.METRIC).withUnitSubtype(UnitSubtype.WEIGHT).build();
        ConversionSpec notHandledSpec = new ConversionSpecBuilder().withUnitType(UnitType.METRIC).withUnitSubtype(UnitSubtype.VOLUME).build();

        assertTrue(conversionHandler.convertsTo(fromSpec));
        assertTrue(conversionHandler.convertsTo(toSpec));
        assertFalse(conversionHandler.convertsTo(notHandledSpec));
    }

    @Test
    void testConvert() throws ConversionFactorException {
        UnitEntity startUnit = ConversionTestTools.makeMetricUnit(null, UnitSubtype.WEIGHT);
        startUnit.setId(6L);
        ConvertibleAmount startAmount = new SimpleAmount(1D, startUnit);
        ConversionSpec toSpec = new ConversionSpecBuilder().withUnitType(UnitType.US).withFlavor(UnitFlavor.Weight).build();

        ConvertibleAmount result = conversionHandler.convert(startAmount, toSpec);
        assertNotNull(result);
        assertEquals(1.11, ConversionTestTools.roundToHundredths(result.getQuantity()));
        assertEquals(5L, result.getUnit().getId());
        assertEquals(UnitType.US, result.getUnit().getType());

        ConversionSpec exactSpec = new ConversionSpecBuilder()
                .withUnitId(1L)
                .withUnitType(UnitType.US).withFlavor(UnitFlavor.Weight).build();

        ConvertibleAmount exactResult = conversionHandler.convert(startAmount, exactSpec);
        assertNotNull(exactResult);
        assertEquals(2D, ConversionTestTools.roundToHundredths(exactResult.getQuantity()));
        assertEquals(1L, exactResult.getUnit().getId());
        assertEquals(UnitType.US, exactResult.getUnit().getType());
    }
}
