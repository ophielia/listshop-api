package com.meg.listshop.conversion.service;

import com.meg.listshop.conversion.data.entity.Unit;
import com.meg.listshop.conversion.data.pojo.*;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;
import com.meg.listshop.conversion.service.handlers.ConversionHandler;
import com.meg.listshop.conversion.service.tools.ConversionHandlerBuilder;
import com.meg.listshop.conversion.tools.ConversionTestTools;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConversionServiceTest {

    Unit metricGrams;
    Unit imperialOunces;
    Unit imperialQuart;
    Unit imperialCups;
    Unit metricLiter;
    Unit imperialMadeUpCloser;

    ConversionService service;


    @BeforeEach
    void setUp() {
        metricGrams = ConversionTestTools.makeMetricUnit(1L, false);
        imperialOunces = ConversionTestTools.makeImperialUnit(2L, false);
        imperialMadeUpCloser = ConversionTestTools.makeImperialUnit(5L, false);
        imperialQuart = ConversionTestTools.makeImperialUnit(3L, UnitFlavor.Volume, UnitFlavor.ListUnit);
        imperialCups = ConversionTestTools.makeImperialUnit(6L, UnitFlavor.Volume);
        metricLiter = ConversionTestTools.makeMetricUnit(4L, true);

        ConversionHandler metricToMetricWeight = new ConversionHandlerBuilder()
                .withFactor(metricGrams, imperialOunces, 0.5)
                .withFactor(metricGrams, imperialMadeUpCloser, 0.9)
                .withFromSpec(UnitType.Metric, UnitFlavor.Weight)
                .withToSpec(UnitType.Imperial, UnitFlavor.Weight)
                .build();
        ConversionHandler metricToMetricVolume = new ConversionHandlerBuilder()
                .withFactor(metricLiter, imperialQuart, 0.6)
                .withFromSpec(UnitType.Metric, UnitFlavor.Volume)
                .withToSpec(UnitType.Imperial, UnitFlavor.Volume)
                .build();
        ConversionHandler imperialListDestination = new ConversionHandlerBuilder()
                .withFactor(imperialOunces, imperialQuart, 0.7)
                .withFactor(imperialCups, imperialQuart, 4)
                .withFromSpec(UnitType.Imperial, UnitFlavor.Volume)
                .withToSpec(UnitType.Imperial, UnitFlavor.Volume, UnitFlavor.ListUnit)
                .build();
        List<ConversionHandler> handlers = Arrays.asList(metricToMetricVolume, metricToMetricWeight, imperialListDestination);
        service = new ConversionServiceImpl(handlers);
    }

    @Test
    public void testConvertExactUnit() throws ConversionPathException, ConversionFactorException {
        // exact - convert metric grams to imperial ounces
        ConvertibleAmount amount = new SimpleAmount(1, metricGrams);
        ConvertibleAmount converted = service.convert(amount, imperialOunces);
        assertNotNull(converted);
        assertEquals(0.5, converted.getQuantity());

        ConvertibleAmount largeAmount = new SimpleAmount(100, metricGrams);
        ConvertibleAmount largeConverted = service.convert(largeAmount, imperialOunces);
        assertNotNull(largeConverted);
        assertEquals(50, largeConverted.getQuantity());

        ConvertibleAmount oppositeAmount = new SimpleAmount(1, imperialOunces);
        ConvertibleAmount oppositeConverted = service.convert(oppositeAmount, metricGrams);
        assertNotNull(oppositeConverted);
        assertEquals(2, oppositeConverted.getQuantity());


    }

    @Test
    public void testConvertByType() throws ConversionPathException, ConversionFactorException {
        // unittype - convert imperial volume to metric volume (say, liter to quart)
        ConvertibleAmount amount = new SimpleAmount(1, imperialQuart);
        ConvertibleAmount converted = service.convert(amount, UnitType.Metric);
        assertNotNull(converted);
        assertEquals(1.67, ConversionTestTools.roundToHundredths(converted.getQuantity()));

        ConvertibleAmount backwardsAmount = new SimpleAmount(1, metricLiter);
        ConvertibleAmount backwardsConverted = service.convert(backwardsAmount, UnitType.Imperial);
        assertNotNull(backwardsConverted);
        assertEquals(0.60, ConversionTestTools.roundToHundredths(backwardsConverted.getQuantity()));

        // test looking for closest element
        ConvertibleAmount closerAmount = new SimpleAmount(1, metricGrams);
        ConvertibleAmount closerConverted = service.convert(closerAmount, UnitType.Imperial);
        assertNotNull(closerConverted);
        assertEquals(0.9, ConversionTestTools.roundToHundredths(closerConverted.getQuantity()));
        assertEquals(5L, closerConverted.getUnit().getId());
    }

    @Test
    public void testConvertByContext() throws ConversionPathException, ConversionFactorException {
        // context - convert imperial volume to list unit (cups to quart)
        ConversionContext context = new ConversionContext(ConversionContextType.List, UnitType.Imperial);
        ConvertibleAmount amount = new SimpleAmount(1, imperialCups);
        ConvertibleAmount converted = service.convert(amount, context);
        assertNotNull(converted);
        assertEquals(4, ConversionTestTools.roundToHundredths(converted.getQuantity()));

        /*
        ConvertibleAmount backwardsAmount = new SimpleAmount(1, metricLiter);
        ConvertibleAmount backwardsConverted = service.convert(backwardsAmount, UnitType.Imperial);
        assertNotNull(backwardsConverted);
        assertEquals(0.60, ConversionTestTools.roundToHundredths(backwardsConverted.getQuantity()));

        // test looking for closest element
        ConvertibleAmount closerAmount = new SimpleAmount(1, metricGrams);
        ConvertibleAmount closerConverted = service.convert(closerAmount, UnitType.Imperial);
        assertNotNull(closerConverted);
        assertEquals(0.9, ConversionTestTools.roundToHundredths(closerConverted.getQuantity()));
        assertEquals(5L, closerConverted.getUnit().getId());
         */
    }
}
