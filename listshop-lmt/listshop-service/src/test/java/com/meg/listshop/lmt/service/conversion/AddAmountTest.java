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
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;
import com.meg.listshop.conversion.service.ConverterService;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@Sql(value = "data/ConversionTest.sql")
@Sql(value = "data/ConversionTest-rollback.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class AddAmountTest {

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

    @Autowired
    ConverterService converterService;

    @Autowired
    UnitRepository unitRepository;


    @ClassRule
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();


    @Test
    public void blowUpTest() throws ConversionPathException, ConversionFactorException {
        Optional<UnitEntity> gramUnitOpt = unitRepository.findById(1013L);
        Optional<UnitEntity> ozUnitOpt = unitRepository.findById(1009L);
        ConvertibleAmount toAdd = new SimpleAmount(1, gramUnitOpt.get());
        ConvertibleAmount addTo = new SimpleAmount(800, gramUnitOpt.get());
        AddRequest request = new AddRequest(ConversionTargetType.List, addTo);
        ConvertibleAmount summed = converterService.add(toAdd, addTo, request);
        assertNotNull(summed);
        assertEquals(0.875, summed.getQuantityRoundedUp(), 0.0);
        //assertEquals(1009L, converted.getUnit().getId());

    }

}
