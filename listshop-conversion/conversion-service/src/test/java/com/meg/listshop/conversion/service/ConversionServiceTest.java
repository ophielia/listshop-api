package com.meg.listshop.conversion.service;

import com.meg.listshop.conversion.data.entity.UnitEntity;
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

class ConversionServiceTest {

    UnitEntity metricGrams;
    UnitEntity imperialOunces;
    UnitEntity imperialQuart;
    UnitEntity imperialCups;
    UnitEntity metricLiter;
    UnitEntity imperialMadeUpCloser;

    ConversionService service;


    @BeforeEach
    void setUp() {
        metricGrams = ConversionTestTools.makeMetricUnit(1L, UnitSubtype.WEIGHT);
        imperialOunces = ConversionTestTools.makeImperialUnit(2L, UnitSubtype.WEIGHT);
        imperialMadeUpCloser = ConversionTestTools.makeImperialUnit(5L, UnitSubtype.WEIGHT);
        imperialQuart = ConversionTestTools.makeImperialUnit(3L, UnitSubtype.VOLUME);
        imperialCups = ConversionTestTools.makeImperialUnit(6L, UnitSubtype.VOLUME);
        metricLiter = ConversionTestTools.makeMetricUnit(4L, UnitSubtype.VOLUME);

        ConversionHandler metricToMetricWeight = new ConversionHandlerBuilder()
                .withFactor(metricGrams, imperialOunces, 0.5)
                .withFactor(metricGrams, imperialMadeUpCloser, 0.9)
                .withFromSpec(UnitType.METRIC, UnitSubtype.WEIGHT)
                .withToSpec(UnitType.US, UnitSubtype.WEIGHT)
                .build();
        ConversionHandler metricToMetricVolume = new ConversionHandlerBuilder()
                .withFactor(metricLiter, imperialQuart, 0.6)
                .withFromSpec(UnitType.METRIC, UnitSubtype.VOLUME)
                .withToSpec(UnitType.US, UnitSubtype.VOLUME)
                .build();
        ConversionHandler imperialListDestination = new ConversionHandlerBuilder()
                .withFactor(imperialOunces, imperialQuart, 0.7)
                .withFactor(imperialCups, imperialQuart, 4)
                .withFromSpec(UnitType.US, UnitSubtype.VOLUME)
                .withToSpec(UnitType.US, UnitSubtype.VOLUME, UnitFlavor.ListUnit)
                .withOneWay()
                .build();
        List<ConversionHandler> handlers = Arrays.asList(metricToMetricVolume, metricToMetricWeight, imperialListDestination);
        service = new ConversionServiceImpl(handlers);
    }

    @Test
    void testConvertExactUnit() throws ConversionPathException, ConversionFactorException {
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
    void testConvertByType() throws ConversionPathException, ConversionFactorException {
        // unittype - convert imperial volume to metric volume (say, liter to quart)
        ConvertibleAmount amount = new SimpleAmount(1, imperialQuart);
        ConvertibleAmount converted = service.convert(amount, UnitType.METRIC);
        assertNotNull(converted);
        assertEquals(1.67, ConversionTestTools.roundToHundredths(converted.getQuantity()));

        ConvertibleAmount backwardsAmount = new SimpleAmount(1, metricLiter);
        ConvertibleAmount backwardsConverted = service.convert(backwardsAmount, UnitType.US);
        assertNotNull(backwardsConverted);
        assertEquals(0.60, ConversionTestTools.roundToHundredths(backwardsConverted.getQuantity()));

        // test looking for closest element
        ConvertibleAmount closerAmount = new SimpleAmount(1, metricGrams);
        ConvertibleAmount closerConverted = service.convert(closerAmount, UnitType.US);
        assertNotNull(closerConverted);
        assertEquals(0.9, ConversionTestTools.roundToHundredths(closerConverted.getQuantity()));
        assertEquals(5L, closerConverted.getUnit().getId());
    }

    @Test
    void testConvertByContext() throws ConversionPathException, ConversionFactorException {
        // context - convert imperial volume to list unit (cups to quart)
        ConversionContext context = new ConversionContext(ConversionContextType.List, UnitType.US, UnitSubtype.VOLUME);
        ConvertibleAmount amount = new SimpleAmount(1, imperialCups);
        ConvertibleAmount converted = service.convert(amount, context);
        assertNotNull(converted);
        assertEquals(4, ConversionTestTools.roundToHundredths(converted.getQuantity()));

    }
}
