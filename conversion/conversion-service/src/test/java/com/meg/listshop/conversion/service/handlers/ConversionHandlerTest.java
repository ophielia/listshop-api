package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.data.entity.Unit;
import com.meg.listshop.conversion.data.pojo.SimpleAmount;
import com.meg.listshop.conversion.data.pojo.UnitFlavor;
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

public class ConversionHandlerTest {

    ConversionHandler conversionHandler;

    @BeforeEach
    void setUp() {
        // make conversion source
        conversionHandler = new ConversionHandlerBuilder()
                .withFactor(ConversionTestTools.makeImperialUnit(1L, UnitFlavor.Weight),
                        ConversionTestTools.makeMetricUnit(2L, UnitFlavor.Weight),
                        0.5)
                .withFactor(ConversionTestTools.makeImperialUnit(3L, UnitFlavor.Weight),
                        ConversionTestTools.makeMetricUnit(4L, UnitFlavor.Weight),
                        0.25)
                .withFactor(ConversionTestTools.makeImperialUnit(5L, UnitFlavor.Weight),
                        ConversionTestTools.makeMetricUnit(6L, UnitFlavor.Weight),
                        .9)
                .withFromSpec(UnitType.Imperial, UnitFlavor.Weight)
                .withToSpec(UnitType.Metric, UnitFlavor.Weight)
                .build();

    }

    @Test
    public void testHandles() {
        // test basic - imperial to metric weight
        ConversionSpec fromSpec = new ConversionSpecBuilder().withUnitType(UnitType.Imperial).withFlavor(UnitFlavor.Weight).build();
        ConversionSpec toSpec = new ConversionSpecBuilder().withUnitType(UnitType.Metric).withFlavor(UnitFlavor.Weight).build();
        ConversionSpec notHandledSpec = new ConversionSpecBuilder().withUnitType(UnitType.Metric).withFlavor(UnitFlavor.Volume).build();
        assertTrue(conversionHandler.handles(fromSpec, toSpec));
        assertTrue(conversionHandler.handles(toSpec, fromSpec));
        assertFalse(conversionHandler.handles(toSpec, notHandledSpec));

    }

    @Test
    void testConvertsTo() {
        ConversionSpec fromSpec = new ConversionSpecBuilder().withUnitType(UnitType.Imperial).withFlavor(UnitFlavor.Weight).build();
        ConversionSpec toSpec = new ConversionSpecBuilder().withUnitType(UnitType.Metric).withFlavor(UnitFlavor.Weight).build();
        ConversionSpec notHandledSpec = new ConversionSpecBuilder().withUnitType(UnitType.Metric).withFlavor(UnitFlavor.Volume).build();

        assertTrue(conversionHandler.convertsTo(fromSpec));
        assertTrue(conversionHandler.convertsTo(toSpec));
        assertFalse(conversionHandler.convertsTo(notHandledSpec));
    }

    @Test
    void testConvert() throws ConversionFactorException {
        Unit startUnit = ConversionTestTools.makeMetricUnit(null, UnitFlavor.Weight);
        startUnit.setId(2L);
        ConvertibleAmount startAmount = new SimpleAmount(1D, startUnit);
        ConversionSpec toSpec = new ConversionSpecBuilder().withUnitType(UnitType.Imperial).withFlavor(UnitFlavor.Weight).build();

        ConvertibleAmount result = conversionHandler.convert(startAmount, toSpec);
        assertNotNull(result);
        assertEquals(1.11, ConversionTestTools.roundToHundredths(result.getQuantity()));
        assertEquals(5L, result.getUnit().getId());
        assertEquals(UnitType.Imperial, result.getUnit().getType());

        ConversionSpec exactSpec = new ConversionSpecBuilder()
                .withUnitId(1L)
                .withUnitType(UnitType.Imperial).withFlavor(UnitFlavor.Weight).build();

        ConvertibleAmount exactResult = conversionHandler.convert(startAmount, exactSpec);
        assertNotNull(exactResult);
        assertEquals(2D, ConversionTestTools.roundToHundredths(exactResult.getQuantity()));
        assertEquals(1L, exactResult.getUnit().getId());
        assertEquals(UnitType.Imperial, exactResult.getUnit().getType());
    }
}
