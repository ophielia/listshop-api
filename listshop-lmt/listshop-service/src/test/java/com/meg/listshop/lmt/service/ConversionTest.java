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

    public static final Long ounceId = 1009L;
    public static final Long mgId = 1016L;
    public static final Long kgId = 1014L;
    public static final Long lbId = 1008L;
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
        assertEquals(0.035, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(1009L, converted.getUnit().getId());

    }

    @Test
    public void testSingleHandlerWeightExact() throws ConversionPathException, ConversionFactorException {
        Optional<UnitEntity> ounceUnitOpt = unitRepository.findById(ounceId);
        Optional<UnitEntity> kgUnitOpt = unitRepository.findById(kgId);
        ConvertibleAmount amount = new SimpleAmount(20.0, ounceUnitOpt.get());
        ConvertibleAmount converted = conversionService.convert(amount, kgUnitOpt.get());
        assertNotNull(converted);
        assertEquals(0.567, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(kgId, converted.getUnit().getId());

        Optional<UnitEntity> mgUnitOpt = unitRepository.findById(mgId);
        Optional<UnitEntity> lbUnitOpt = unitRepository.findById(lbId);
        amount = new SimpleAmount(400.0, mgUnitOpt.get());
        converted = conversionService.convert(amount, lbUnitOpt.get());
        assertNotNull(converted);
        assertEquals(0.001, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(lbId, converted.getUnit().getId());

    }
}
