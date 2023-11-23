/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.lmt.service;

import com.meg.listshop.Application;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.conversion.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.SimpleAmount;
import com.meg.listshop.conversion.data.repository.UnitRepository;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;
import com.meg.listshop.conversion.service.ConversionService;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.conversion.tools.RoundingUtils;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

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
    public void blowUpTest() throws ConversionPathException, ConversionFactorException {
        Optional<UnitEntity> gramUnitOpt = unitRepository.findById(1013L);
        Optional<UnitEntity> ozUnitOpt = unitRepository.findById(1009L);
        ConvertibleAmount amount = new SimpleAmount(1, gramUnitOpt.get());
        ConvertibleAmount converted = conversionService.convert(amount, ozUnitOpt.get());
        assertNotNull(converted);
        assertEquals(0.035, RoundingUtils.roundToThousenths(converted.getQuantity()));
        assertEquals(1009L, converted.getUnit().getId());

    }
}
