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
import com.meg.listshop.conversion.data.pojo.AddRequest;
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

    private static final Long ounceId = 1009L;
    private static final Long mgId = 1016L;
    private static final Long gId = 1013L;
    private static final Long butterStickId = 1049L;
    private static final Long sliceId = 1022L;
    private static final Long kgId = 1014L;
    private static final Long lbId = 1008L;
    private static final Long unitId = 1011L;

    private static final Long gallonId = 1005L;
    private static final Long literId = 1003L;
    private static final Long quartId = 1010L;
    private static final Long pintId = 1006L;
    private static final Long flCupsId = 1017L;
    private static final Long cupsId = 1000L;
    private static final Long centileterId = 1015L;
    private static final Long milliliterId = 1004L;
    private static final Long teaspoonId = 1002L;
    private static final Long tablespoonId = 1001L;

    private static final Long fluidOzId = 1007L;

    private static final Long butterTagId = 87209L;
    private static final Long butterConversionId = 87209L;
    private static final Long cheddarConversionId = 95915L;
    private static final Long chickenDrumstickId = 227959L;
    private static final Long onionConversionId = 56630L;
    private static final Long tomatoConversionId = 225744L;
    @Container
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();
    @Autowired
    ConverterService converterService;
    @Autowired
    UnitRepository unitRepository;

    @Test
    void blowUpTest() throws ConversionPathException, ConversionFactorException, ConversionAddException {
        Optional<UnitEntity> gramUnitOpt = unitRepository.findById(1013L);
        Optional<UnitEntity> ozUnitOpt = unitRepository.findById(1009L);
        ConvertibleAmount toAdd = new SimpleAmount(1, gramUnitOpt.get());
        ConvertibleAmount addTo = new SimpleAmount(800, gramUnitOpt.get());
        AddRequest request = new AddRequest(ConversionTargetType.List, addTo);
        ConvertibleAmount summed = converterService.add(toAdd, addTo, request);
        assertNotNull(summed);
        Assertions.assertEquals(0.875, summed.getQuantityRoundedUp(), 0.0);
        //assertEquals(1009L, converted.getUnit().getId());

    }

    @Test
    void addTagSpecific() throws ConversionPathException, ConversionFactorException, ConversionAddException {
        UnitEntity grams = unitRepository.findById(gId).orElse(null);
        UnitEntity cup = unitRepository.findById(cupsId).orElse(null);
        UnitEntity unit = unitRepository.findById(unitId).orElse(null);

        // add one tomato to another - no sizes given
        ConvertibleAmount mediumTomato = new SimpleAmount(1.0, unit, tomatoConversionId, false, null, null, false);
        ConvertibleAmount largeTomato = new SimpleAmount(1.0, unit, tomatoConversionId, false, null, null, false);

        AddRequest addRequest = new AddRequest(ConversionTargetType.List, largeTomato);
        ConvertibleAmount added = converterService.add(mediumTomato, largeTomato, addRequest);
        assertNotNull(added);
        Assertions.assertEquals(2.00, added.getQuantityRoundedUp(), 0.0);
        Assertions.assertEquals("medium", added.getUnitSize());

        // add one medium tomato to one large tomato - large tomato is user size
        mediumTomato = new SimpleAmount(1.0, unit, tomatoConversionId, false, null, "medium", false);
        largeTomato = new SimpleAmount(1.0, unit, tomatoConversionId, false, null, "large", true);

        // list context
        addRequest = new AddRequest(ConversionTargetType.List, largeTomato);
        added = converterService.add(mediumTomato, largeTomato, addRequest);
        assertNotNull(added);
        Assertions.assertEquals(1.875, added.getQuantityRoundedUp(), 0.0);
        Assertions.assertEquals("large", added.getUnitSize());
        assertTrue(added.getUserSize());

        // add one medium tomato to one large tomato - medium tomato is user size
        mediumTomato = new SimpleAmount(1.0, unit, tomatoConversionId, false, null, "medium", true);
        largeTomato = new SimpleAmount(1.0, unit, tomatoConversionId, false, null, "large", false);

        addRequest = new AddRequest(ConversionTargetType.List, largeTomato);
        added = converterService.add(mediumTomato, largeTomato, addRequest);
        assertNotNull(added);
        Assertions.assertEquals(2.25, added.getQuantityRoundedUp(), 0.0);
        Assertions.assertEquals("medium", added.getUnitSize());
    }

}
