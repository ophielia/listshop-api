/*
 * The List Shop
 *
 * Copyright (c) 2022-2026.
 */

package com.meg.listshop.lmt.service.conversion.client;

import com.meg.listshop.Application;
import com.meg.listshop.common.UnitType;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.common.data.repository.UnitRepository;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.pojo.ConversionTargetType;
import com.meg.listshop.conversion.data.repository.ConversionFactorRepository;
import com.meg.listshop.conversion.data.repository.FactorCriteria;
import com.meg.listshop.conversion.data.repository.FactorCriteriaBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@ExtendWith(SpringExtension.class)
@Testcontainers
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
class ConversionFactorRepositoryTest {


    private static final Long ounceId = 1009L;
    private static final Long gramId = 1013L;
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
    @Container
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();
    @Autowired
    ConversionFactorRepository repository;
    @Autowired
    UnitRepository unitRepository;

    @Test
    void testFindWithToUnit() {
        UnitEntity grams = unitRepository.findById(gramId).orElse(null);
        FactorCriteria criteria = new FactorCriteriaBuilder()
                .withFromUnit(grams)
                .withToUnit(kgId)
                .build();
        List<ConversionFactor> factors = repository.findFactors(criteria);
        Assertions.assertNotNull(factors);
        Assertions.assertEquals(1, factors.size(), "number of factors found incorrect.");
    }

    @Test
    void testFindWithContext() {
        UnitEntity grams = unitRepository.findById(gramId).orElse(null);
        FactorCriteria criteria = new FactorCriteriaBuilder()
                .withFromUnit(grams)
                .withToContext(ConversionTargetType.List)
                .withToDomain(UnitType.METRIC)
                .build();
        List<ConversionFactor> factors = repository.findAllFactors(criteria);
        Assertions.assertNotNull(factors);
        Assertions.assertEquals(5, factors.size(), "number of factors found incorrect.");
    }

}
