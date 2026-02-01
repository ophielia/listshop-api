/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.lmt.service.conversion;

import com.meg.listshop.Application;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.common.data.repository.UnitRepository;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.conversion.data.pojo.AddScaleRequest;
import com.meg.listshop.conversion.data.pojo.ConversionTargetType;
import com.meg.listshop.conversion.data.pojo.SimpleAmount;
import com.meg.listshop.conversion.exceptions.ConversionAddException;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;
import com.meg.listshop.conversion.service.ConverterService;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@Testcontainers
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@Sql(value = "data/ConversionTest.sql")
@Sql(value = "data/ConversionTest-rollback.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class AddAmountTest {

    private static final Long UNIT_ID = 1011L;

    private static final Long TOMATO_CONVERSION_ID = 225744L;

    @Container
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();
    @Autowired
    ConverterService converterService;
    @Autowired
    UnitRepository unitRepository;

    @Test
    void blowUpTest() throws ConversionPathException, ConversionFactorException, ConversionAddException {
        Optional<UnitEntity> gramUnitOpt = unitRepository.findById(1013L);
        ConvertibleAmount toAdd = new SimpleAmount(1, gramUnitOpt.get());
        ConvertibleAmount addTo = new SimpleAmount(800, gramUnitOpt.get());
        AddScaleRequest request = new AddScaleRequest(ConversionTargetType.List, addTo);
        ConvertibleAmount summed = converterService.add(toAdd, addTo, request);
        assertNotNull(summed);
        Assertions.assertEquals(0.875, summed.getQuantityRoundedUp(), 0.0);

    }

    @Test
    void addTagSpecific() throws ConversionPathException, ConversionFactorException, ConversionAddException {
        UnitEntity unit = unitRepository.findById(UNIT_ID).orElse(null);

        // add one tomato to another - no sizes given
        ConvertibleAmount mediumTomato = new SimpleAmount(1.0, unit, TOMATO_CONVERSION_ID, false, null, null, false);
        ConvertibleAmount largeTomato = new SimpleAmount(1.0, unit, TOMATO_CONVERSION_ID, false, null, null, false);

        AddScaleRequest addRequest = new AddScaleRequest(ConversionTargetType.List, largeTomato);
        ConvertibleAmount added = converterService.add(mediumTomato, largeTomato, addRequest);
        assertNotNull(added);
        Assertions.assertEquals(2.00, added.getQuantityRoundedUp(), 0.0);
        Assertions.assertEquals("medium", added.getUnitSize());

        // add one medium tomato to one large tomato - large tomato is user size
        mediumTomato = new SimpleAmount(1.0, unit, TOMATO_CONVERSION_ID, false, null, "medium", false);
        largeTomato = new SimpleAmount(1.0, unit, TOMATO_CONVERSION_ID, false, null, "large", true);

        // list context
        addRequest = new AddScaleRequest(ConversionTargetType.List, largeTomato);
        added = converterService.add(mediumTomato, largeTomato, addRequest);
        assertNotNull(added);
        Assertions.assertEquals(1.875, added.getQuantityRoundedUp(), 0.0);
        Assertions.assertEquals("large", added.getUnitSize());
        assertTrue(added.getUserSize());

        // add one medium tomato to one large tomato - medium tomato is user size
        mediumTomato = new SimpleAmount(1.0, unit, TOMATO_CONVERSION_ID, false, null, "medium", true);
        largeTomato = new SimpleAmount(1.0, unit, TOMATO_CONVERSION_ID, false, null, "large", false);

        addRequest = new AddScaleRequest(ConversionTargetType.List, largeTomato);
        added = converterService.add(mediumTomato, largeTomato, addRequest);
        assertNotNull(added);
        Assertions.assertEquals(2.25, added.getQuantityRoundedUp(), 0.0);
        Assertions.assertEquals("medium", added.getUnitSize());
    }

}
