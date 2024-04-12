package com.meg.listshop.conversion.service;

import com.meg.listshop.conversion.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.*;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;
import com.meg.listshop.conversion.service.handlers.ChainConversionHandler;
import com.meg.listshop.conversion.service.handlers.ConversionHandler;
import com.meg.listshop.conversion.service.handlers.ScalingHandler;
import com.meg.listshop.conversion.service.tools.ChainConversionHandlerBuilder;
import com.meg.listshop.conversion.service.tools.ScalingConversionHandlerBuilder;
import com.meg.listshop.conversion.service.tools.StandardConversionHandlerBuilder;
import com.meg.listshop.conversion.tools.ConversionTestTools;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConverterServiceTest {

    UnitEntity metricGrams;
    UnitEntity usOunces;
    UnitEntity usQuart;
    UnitEntity usCups;
    UnitEntity hybridTeaspoon;
    UnitEntity hybridTablespoon;
    UnitEntity metricLiter;
    UnitEntity usMadeUpCloser;

    ConverterService service;


    @BeforeEach
    void setUp() {
        metricGrams = ConversionTestTools.makeMetricUnit(1L, UnitSubtype.WEIGHT);
        usOunces = ConversionTestTools.makeUSUnit(2L, UnitSubtype.WEIGHT);
        usMadeUpCloser = ConversionTestTools.makeUSUnit(5L, UnitSubtype.WEIGHT);
        usQuart = ConversionTestTools.makeUSUnit(3L, UnitSubtype.VOLUME);
        usCups = ConversionTestTools.makeUSUnit(6L, UnitSubtype.VOLUME);
        usCups.setLiquid(true);
        hybridTeaspoon = ConversionTestTools.makeHybridUnit(7L, UnitSubtype.LIQUID);
        hybridTablespoon = ConversionTestTools.makeHybridUnit(8L, UnitSubtype.LIQUID);
        metricLiter = ConversionTestTools.makeMetricUnit(4L, UnitSubtype.VOLUME);

        ChainConversionHandler metricToUs = (ChainConversionHandler) new ChainConversionHandlerBuilder()
                .withFactor(metricGrams, usOunces, 0.5)
                .withFactor(metricGrams, usMadeUpCloser, 0.9)
                .withFactor(metricLiter, usQuart, 0.6)
                .withFromSpec(UnitType.METRIC, null)
                .withToSpec(UnitType.US, null)
                .build();

        ScalingHandler listHandler =  new ScalingConversionHandlerBuilder()
                .withFactor(usCups, usQuart, 0.25)
                .withForScalar(ConversionTargetType.List)
                .withFromSpec(UnitType.METRIC, UnitSubtype.VOLUME)
                .withToSpec(UnitType.US, UnitSubtype.VOLUME)
                .withForScalar(ConversionTargetType.List)
                .build();
        listHandler.setSkipNoConversionRequiredCheck(true);

        ConversionHandler weightToVolume = new StandardConversionHandlerBuilder()
                .withFactor(metricLiter, usQuart, 0.6)
                .withFromSpec(UnitType.METRIC, UnitSubtype.VOLUME)
                .withToSpec(UnitType.US, UnitSubtype.VOLUME)
                .build();

        List<ChainConversionHandler> handlers = Collections.singletonList( metricToUs);
        service = new ConverterServiceImpl(handlers, Collections.singletonList(listHandler), weightToVolume);
    }

    @Test
    void testConvertExactUnit() throws ConversionPathException, ConversionFactorException {
        // exact - convert metric grams to imperial ounces
        ConvertibleAmount amount = new SimpleAmount(1, metricGrams);
        ConvertibleAmount converted = service.convert(amount, usOunces);
        assertNotNull(converted);
        assertEquals(0.5, converted.getQuantity());

        ConvertibleAmount largeAmount = new SimpleAmount(100, metricGrams);
        ConvertibleAmount largeConverted = service.convert(largeAmount, usOunces);
        assertNotNull(largeConverted);
        assertEquals(50, largeConverted.getQuantity());

        ConvertibleAmount oppositeAmount = new SimpleAmount(1, usOunces);
        ConvertibleAmount oppositeConverted = service.convert(oppositeAmount, metricGrams);
        assertNotNull(oppositeConverted);
        assertEquals(2, oppositeConverted.getQuantity());


    }

    @Test
    void testConvertByType() throws ConversionPathException, ConversionFactorException {
        // unittype - convert imperial volume to metric volume (say, liter to quart)
        ConvertibleAmount amount = new SimpleAmount(1, usQuart);
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
        ConversionRequest context = new ConversionRequest(ConversionTargetType.List, UnitType.US);
        ConvertibleAmount amount = new SimpleAmount(4, usCups);
        ConvertibleAmount converted = service.convert(amount, context);
        assertNotNull(converted);
        assertEquals(1, ConversionTestTools.roundToHundredths(converted.getQuantity()));

    }
}
