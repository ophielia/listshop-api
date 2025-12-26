/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.lmt.service.conversion;

import com.meg.listshop.Application;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.conversion.data.pojo.*;
import com.meg.listshop.common.data.repository.UnitRepository;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;
import com.meg.listshop.conversion.service.ConverterService;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.common.RoundingUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@Testcontainers
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
class ConversionHybridFactorsTest {

    private static final Long ounceId = 1009L;
    private static final Long gId = 1013L;
    private static final Long kgId = 1014L;
    private static final Long lbId = 1008L;
    private static final Long flTeaspoonId = 1019L;
    private static final Long flTablespoonId = 1021L;
    private static final Long tspId = 1002L;
    private static final Long tbId = 1001L;

    private static final Long gallonId = 1005L;
    private static final Long literId = 1003L;
    private static final Long quartId = 1010L;
    private static final Long pintId = 1006L;
    private static final Long cupsId = 1017L;
    private static final Long centileterId = 1015L;
    private static final Long milliliterId = 1004L;
    private static final Long flOzId = 1007L;
    @Autowired
    ConverterService converterService;

    @Autowired
    UnitRepository unitRepository;


    @Container
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();


    @Test
    void unitHybridScaling() throws ConversionPathException, ConversionFactorException {
        UnitEntity flTspOpt = unitRepository.findById(flTeaspoonId).orElse(null);
        UnitEntity tablespoonOpt = unitRepository.findById(tbId).orElse(null);

        ConversionRequest dishConversionContext = new ConversionRequest(ConversionTargetType.Dish, DomainType.US);


        // 4 tablespoons = 12 teaspoons

        // 3 teaspoons = 1 tablespoon - fluid
        ConvertibleAmount amount = new SimpleAmount(3.0, flTspOpt);
        ConvertibleAmount converted = converterService.convert(amount, dishConversionContext);
        assertNotNull(converted);
        //assertEquals(1.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        //assertEquals(flTablespoonId, converted.getUnit().getId());

        // 1/3 tablespoon = 1 teaspoons
        amount = new SimpleAmount(0.3334, tablespoonOpt);
        converted = converterService.convert(amount, dishConversionContext);
        assertNotNull(converted);
        assertEquals(1.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(tspId, converted.getUnit().getId());
    }


}
