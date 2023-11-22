/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.lmt.service;

import com.meg.listshop.Application;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.conversion.data.pojo.SimpleAmount;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class ConversionTest {

    @Autowired
    ConversionService conversionService;

    @Autowired
    UnitRepository unitRepository;


    @ClassRule
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    @Test
    public void blowUpTest() {
        Unit
        ConvertibleAmount amount = new SimpleAmount(1, metricGrams);
        ConvertibleAmount converted = conversionService.convert(amount, imperialOunces);
        assertNotNull(converted);
        assertEquals(0.5, converted.getQuantity());

        conversionService.processEmail(parameters);
    }
}
