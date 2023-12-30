package com.meg.listshop.conversion.service;

import com.meg.listshop.conversion.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.*;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;
import com.meg.listshop.conversion.exceptions.ExceedsAllowedScaleException;
import com.meg.listshop.conversion.service.handlers.ConversionHandler;
import com.meg.listshop.conversion.service.handlers.TestConversionHandler;
import com.meg.listshop.conversion.tools.ConversionTestTools;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class ConversionServiceMockTest {

    UnitEntity hybridTeaspoon;
    UnitEntity hybridTablespoon;


    ConversionService service;

    ConversionHandler handlerOne = Mockito.mock(TestConversionHandler.class);
    ConversionHandler handlerTwo = Mockito.mock(TestConversionHandler.class);


    @BeforeEach
    void setUp() {
        hybridTeaspoon = ConversionTestTools.makeHybridUnit(7L, UnitSubtype.NONE);
        hybridTablespoon = ConversionTestTools.makeHybridUnit(8L, UnitSubtype.NONE);

        List<ConversionHandler> handlers = Arrays.asList(handlerOne, handlerTwo);
        service = new ConversionServiceImpl(handlers);
    }

    @Test
    void testConvertHybrid() throws ConversionPathException, ConversionFactorException, ExceedsAllowedScaleException {
        ConvertibleAmount amount = new SimpleAmount(1, hybridTeaspoon);
        ConvertibleAmount resultingAmount = new SimpleAmount(2, hybridTablespoon);
        ConversionContext requestedContext = new ConversionContext(ConversionContextType.Dish, UnitType.US, UnitSubtype.VOLUME);
        ConversionSpec sourceSpec = ConversionSpec.basicSpec(hybridTeaspoon.getId(), UnitType.HYBRID, UnitSubtype.NONE, Collections.emptySet());
        ConversionSpec hybridSpec = ConversionSpec.fromContextAndSource(requestedContext, amount.getUnit(), true);
        ConversionSpec retrySpec = ConversionSpec.fromContextAndSource(requestedContext, amount.getUnit(), true);

        when(handlerOne.handles(sourceSpec, hybridSpec)).thenReturn(true);
        when(handlerTwo.handles(sourceSpec, hybridSpec)).thenReturn(false);
        when(handlerOne.convert(amount, hybridSpec)).thenThrow(ExceedsAllowedScaleException.class);
        when(handlerOne.handles(sourceSpec, retrySpec)).thenReturn(false);
        when(handlerTwo.handles(sourceSpec, retrySpec)).thenReturn(true);
        when(handlerTwo.convert(amount, retrySpec)).thenReturn(resultingAmount);


        ConvertibleAmount result = service.convert(amount, requestedContext);
        assertNotNull(result);
        assertEquals(result, resultingAmount);

    }
}
