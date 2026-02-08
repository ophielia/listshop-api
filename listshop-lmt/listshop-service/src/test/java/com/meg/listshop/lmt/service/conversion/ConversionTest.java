/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.lmt.service.conversion;

import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.common.data.entity.UnitEntity;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;/**/

@ExtendWith(SpringExtension.class)
@Testcontainers
@SpringBootTest()
@ActiveProfiles("test")
@Sql(value = "data/ConversionTest.sql")
@Sql(value = "data/ConversionTest-rollback.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ConversionTest {

    private static final Long OUNCE_ID = 1009L;
    private static final Long MG_ID = 1016L;
    private static final Long GRAM_ID = 1013L;
    private static final Long BUTTER_STICK_ID = 1049L;
    private static final Long SLICE_ID = 1022L;
    private static final Long KG_ID = 1014L;
    private static final Long LB_ID = 1008L;
    private static final Long UNIT_ID = 1011L;

    private static final Long GALLON_ID = 1005L;
    private static final Long LITER_ID = 1003L;
    private static final Long QUART_ID = 1010L;
    private static final Long PINT_ID = 1006L;
    private static final Long FL_CUPS_ID = 1017L;
    private static final Long CUPS_ID = 1000L;
    private static final Long CENTILETER_ID = 1015L;
    private static final Long MILLILITER_ID = 1004L;
    private static final Long TEASPOON_ID = 1002L;
    private static final Long TABLESPOON_ID = 1001L;

    private static final Long FLUID_OZ_ID = 1007L;

    private static final Long BUTTER_TAG_ID = 87209L;
    private static final Long BUTTER_CONVERSION_ID = 87209L;
    private static final Long CHEDDAR_CONVERSION_ID = 95915L;
    private static final Long CHICKEN_DRUMSTICK_ID = 227959L;
    private static final Long ONION_CONVERSION_ID = 56630L;
    private static final Long TOMATO_CONVERSION_ID = 225744L;

    @Autowired
    ConverterService converterService;

    @Autowired
    UnitRepository unitRepository;


    @Container
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();


    @Test
    void blowUpTest() throws ConversionPathException, ConversionFactorException {
        Optional<UnitEntity> gramUnitOpt = unitRepository.findById(1013L);
        Optional<UnitEntity> ozUnitOpt = unitRepository.findById(1009L);
        ConvertibleAmount amount = new SimpleAmount(1, gramUnitOpt.get());
        ConvertibleAmount converted = converterService.convert(amount, ozUnitOpt.get());
        assertNotNull(converted);
        assertEquals(0.035, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(1009L, converted.getUnit().getId());

    }

    @Test
    void testSingleHandlerWeightExact() throws ConversionPathException, ConversionFactorException {
        Optional<UnitEntity> ounceUnitOpt = unitRepository.findById(OUNCE_ID);
        Optional<UnitEntity> kgUnitOpt = unitRepository.findById(KG_ID);
        ConvertibleAmount amount = new SimpleAmount(20.0, ounceUnitOpt.get());
        ConvertibleAmount converted = converterService.convert(amount, kgUnitOpt.get());
        assertNotNull(converted);
        assertEquals(0.567, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(KG_ID, converted.getUnit().getId());

        Optional<UnitEntity> mgUnitOpt = unitRepository.findById(MG_ID);
        Optional<UnitEntity> lbUnitOpt = unitRepository.findById(LB_ID);
        amount = new SimpleAmount(400.0, mgUnitOpt.get());
        converted = converterService.convert(amount, lbUnitOpt.get());
        assertNotNull(converted);
        assertEquals(0.001, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(LB_ID, converted.getUnit().getId());

        Optional<UnitEntity> gUnitOpt = unitRepository.findById(GRAM_ID);
        Optional<UnitEntity> ozUnitOpt = unitRepository.findById(OUNCE_ID);
        amount = new SimpleAmount(500.0, gUnitOpt.get());
        converted = converterService.convert(amount, ozUnitOpt.get());
        assertNotNull(converted);
        assertEquals(17.637, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(OUNCE_ID, converted.getUnit().getId());
    }

    @Test
    void testSingleHandlerVolumeExact() throws ConversionPathException, ConversionFactorException {
        Optional<UnitEntity> gallonsOpt = unitRepository.findById(GALLON_ID);
        Optional<UnitEntity> litersOpt = unitRepository.findById(LITER_ID);
        Optional<UnitEntity> milliliterOpt = unitRepository.findById(MILLILITER_ID);
        Optional<UnitEntity> cupsOpt = unitRepository.findById(FL_CUPS_ID);
        Optional<UnitEntity> quartOpt = unitRepository.findById(QUART_ID);
        Optional<UnitEntity> centiliterOpt = unitRepository.findById(CENTILETER_ID);

        ConvertibleAmount amount = new SimpleAmount(20.0, litersOpt.get());
        ConvertibleAmount converted = converterService.convert(amount, gallonsOpt.get());
        assertNotNull(converted);
        assertEquals(5.283, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(GALLON_ID, converted.getUnit().getId());

        amount = new SimpleAmount(50, centiliterOpt.get());
        converted = converterService.convert(amount, quartOpt.get());
        assertNotNull(converted);
        assertEquals(0.528, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(QUART_ID, converted.getUnit().getId());

        amount = new SimpleAmount(3.0, cupsOpt.get());
        converted = converterService.convert(amount, milliliterOpt.get());
        assertNotNull(converted);
        assertEquals(709.765, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(MILLILITER_ID, converted.getUnit().getId());
    }

    @Test
    void testSingleHandlerVolumeType() throws ConversionPathException, ConversionFactorException {
        Optional<UnitEntity> litersOpt = unitRepository.findById(LITER_ID);
        Optional<UnitEntity> cupsOpt = unitRepository.findById(FL_CUPS_ID);
        Optional<UnitEntity> centiliterOpt = unitRepository.findById(CENTILETER_ID);

        ConvertibleAmount amount = new SimpleAmount(20.0, litersOpt.get());
        ConvertibleAmount converted = converterService.convert(amount, DomainType.US);
        assertNotNull(converted);
        assertEquals(5.283, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(GALLON_ID, converted.getUnit().getId());

        amount = new SimpleAmount(50, centiliterOpt.get());
        converted = converterService.convert(amount, DomainType.US);
        assertNotNull(converted);
        assertEquals(1.057, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(PINT_ID, converted.getUnit().getId());


        amount = new SimpleAmount(3.0, cupsOpt.get());
        converted = converterService.convert(amount, DomainType.METRIC);
        assertNotNull(converted);
        assertEquals(0.71, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(LITER_ID, converted.getUnit().getId());
    }

    @Test
    void testSimpleMetricVolumeListContext() throws ConversionPathException, ConversionFactorException {
        Optional<UnitEntity> quartOpt = unitRepository.findById(QUART_ID);
        Optional<UnitEntity> pintOpt = unitRepository.findById(PINT_ID);

        ConvertibleAmount amount = new SimpleAmount(3, quartOpt.get());
        ConversionRequest listContext = new ConversionRequest(ConversionTargetType.List, DomainType.METRIC);
        ConvertibleAmount converted = converterService.convert(amount, listContext);
        assertNotNull(converted);
        assertEquals(2.839, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(LITER_ID, converted.getUnit().getId());

        amount = new SimpleAmount(0.021133764, pintOpt.get());
        converted = converterService.convert(amount, DomainType.METRIC);
        assertNotNull(converted);
        // confirm that we have this pint fraction translated to 1 centiliter
        assertEquals(1.000, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(CENTILETER_ID, converted.getUnit().getId());

        // now, converting the same pint fraction in a list context should not result in a conversion to centiliter.
        // It should be milliliters - since that's the closest unit to this quantity permitted in a list
        amount = new SimpleAmount(0.021133764, pintOpt.get());
        converted = converterService.convert(amount, listContext);
        assertNotNull(converted);
        assertEquals(MILLILITER_ID, converted.getUnit().getId());
        assertEquals(10.000, RoundingUtils.roundToThousandths(converted.getQuantity()));
    }

    @Test
    void testHybridDishContext() throws ConversionPathException, ConversionFactorException {
        Optional<UnitEntity> teaspoonOpt = unitRepository.findById(TEASPOON_ID);

        ConvertibleAmount amount = new SimpleAmount(3, teaspoonOpt.get());
        ConversionRequest listContext = new ConversionRequest(ConversionTargetType.Dish, DomainType.METRIC);
        ConvertibleAmount converted = converterService.convert(amount, listContext);
        assertNotNull(converted);
        assertEquals(1, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(TABLESPOON_ID, converted.getUnit().getId());

    }

    @Test
    void testSimpleUsVolumeListContext() throws ConversionPathException, ConversionFactorException {
        Optional<UnitEntity> milliliterOpt = unitRepository.findById(MILLILITER_ID);
        Optional<UnitEntity> centiliterOpt = unitRepository.findById(CENTILETER_ID);

        ConvertibleAmount amount = new SimpleAmount(473.17, milliliterOpt.get());
        ConversionRequest listContext = new ConversionRequest(ConversionTargetType.List, DomainType.US);

        ConvertibleAmount converted = converterService.convert(amount, listContext);
        assertNotNull(converted);
        assertEquals(0.5, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(QUART_ID, converted.getUnit().getId());

        amount = new SimpleAmount(23.6588, centiliterOpt.get());
        converted = converterService.convert(amount, DomainType.US);
        assertNotNull(converted);
        // confirm that we have this centiliter converted to 1 cup
        assertEquals(1.000, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(FL_CUPS_ID, converted.getUnit().getId());

        // now, converting the same amount in a list context should not result in a conversion to cups.
        // It should be quarts - since that's the closest unit to this quantity permitted in a list
        amount = new SimpleAmount(23.6588, centiliterOpt.get());
        converted = converterService.convert(amount, listContext);
        assertNotNull(converted);
        assertEquals(FLUID_OZ_ID, converted.getUnit().getId());
        assertEquals(8.0, RoundingUtils.roundToThousandths(converted.getQuantity()));

    }

    @Test
    void testSimpleUsWeightListContext() throws ConversionPathException, ConversionFactorException {
        Optional<UnitEntity> gramOpt = unitRepository.findById(GRAM_ID);

        // 113.398093 gm = 4 ounces => ounce destination
        ConvertibleAmount amount = new SimpleAmount(113.398093, gramOpt.get());
        ConversionRequest listContext = new ConversionRequest(ConversionTargetType.List, DomainType.US);
        ConvertibleAmount converted = converterService.convert(amount, listContext);
        assertNotNull(converted);
        assertEquals(OUNCE_ID, converted.getUnit().getId());
        assertEquals(3.992, RoundingUtils.roundToThousandths(converted.getQuantity()));

        // 15 Ounce = 425.242847 Gram  => pound destination
        amount = new SimpleAmount(425.242847, gramOpt.get());
        listContext = new ConversionRequest(ConversionTargetType.List, DomainType.US);
        converted = converterService.convert(amount, listContext);
        assertNotNull(converted);
        assertEquals(0.936, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(LB_ID, converted.getUnit().getId());

    }

    @Test
    void testVolumeToWeightConversion() throws ConversionPathException, ConversionFactorException {
        UnitEntity tablespoon = unitRepository.findById(TABLESPOON_ID).orElse(null);
        UnitEntity grams = unitRepository.findById(GRAM_ID).orElse(null);

        ConversionRequest listContext = new ConversionRequest(ConversionTargetType.List, DomainType.METRIC);
        // tablespoon of butter to grams
        ConvertibleAmount amount = new SimpleAmount(1, tablespoon, 348L, false, null);
        ConvertibleAmount converted = converterService.convert(amount, grams);
        assertNotNull(converted);
        assertEquals(14.175, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(GRAM_ID, converted.getUnit().getId());

        // 1 tablespoon of butter to metric
        converted = converterService.convert(amount, listContext);
        assertNotNull(converted);
        assertEquals(14.175, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(GRAM_ID, converted.getUnit().getId());


        // 16 tablespoons of butter to metric
        ConvertibleAmount bigAmount = new SimpleAmount(16.0, tablespoon, BUTTER_TAG_ID, false, null);
        converted = converterService.convert(bigAmount, grams);
        assertNotNull(converted);
        assertEquals(227.2, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(GRAM_ID, converted.getUnit().getId());

        // 16 tablespoons of butter to list context - metric
        bigAmount = new SimpleAmount(16.0, tablespoon, BUTTER_TAG_ID, false, null);
        converted = converterService.convert(bigAmount, listContext);
        assertNotNull(converted);
        assertEquals(227.2, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(GRAM_ID, converted.getUnit().getId());

        // 16 tablespoons of butter to dish context - metric
        listContext = new ConversionRequest(ConversionTargetType.Dish, DomainType.METRIC);
        bigAmount = new SimpleAmount(16.0, tablespoon, BUTTER_TAG_ID, false, null);
        converted = converterService.convert(bigAmount, listContext);
        assertNotNull(converted);
        assertEquals(16.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(TABLESPOON_ID, converted.getUnit().getId());

        // 8 tablespoons of butter to dish context - metric
        listContext = new ConversionRequest(ConversionTargetType.Dish, DomainType.METRIC);
        bigAmount = new SimpleAmount(8.0, tablespoon, BUTTER_TAG_ID, false, null);
        converted = converterService.convert(bigAmount, listContext);
        assertNotNull(converted);
        assertEquals(8.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(TABLESPOON_ID, converted.getUnit().getId());

        // 7 tablespoons of butter to dish context - metric
        listContext = new ConversionRequest(ConversionTargetType.Dish, DomainType.METRIC);
        bigAmount = new SimpleAmount(7.0, tablespoon, BUTTER_TAG_ID, false, null);
        converted = converterService.convert(bigAmount, listContext);
        assertNotNull(converted);
        assertEquals(7.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(TABLESPOON_ID, converted.getUnit().getId());


        // 8 tablespoons of butter to context dish - us
        ConversionRequest dishContext = new ConversionRequest(ConversionTargetType.Dish, DomainType.US);
        bigAmount = new SimpleAmount(8.0, tablespoon, BUTTER_TAG_ID, false, null);
        converted = converterService.convert(bigAmount, dishContext);
        assertNotNull(converted);
        assertEquals(1.005, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(BUTTER_STICK_ID, converted.getUnit().getId());

        // 16 tablespoons of butter to list context - us
        listContext = new ConversionRequest(ConversionTargetType.List, DomainType.US);
        bigAmount = new SimpleAmount(16.0, tablespoon, BUTTER_TAG_ID, false, null);
        converted = converterService.convert(bigAmount, listContext);
        assertNotNull(converted);
        assertEquals(0.5, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(LB_ID, converted.getUnit().getId());
    }


    @Test
    void testTagSpecificConversion() throws ConversionPathException, ConversionFactorException {
        UnitEntity tablespoon = unitRepository.findById(CUPS_ID).orElse(null);
        UnitEntity grams = unitRepository.findById(GRAM_ID).orElse(null);

        ConversionRequest listContext = new ConversionRequest(ConversionTargetType.List, DomainType.METRIC);
        // 1/2 cup onions to unit, marker chopped
        ConvertibleAmount amount = new SimpleAmount(0.5, tablespoon, ONION_CONVERSION_ID, false, "chopped");
        ConvertibleAmount converted = converterService.convert(amount, grams);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(80.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(GRAM_ID, converted.getUnit().getId());

        // 1/2 cup onions to unit, marker chopped
        amount = new SimpleAmount(0.5, tablespoon, ONION_CONVERSION_ID, false, "chopped");
        converted = converterService.convert(amount, listContext);
        assertNotNull(converted);
        assertEquals(2.105, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(UNIT_ID, converted.getUnit().getId());
    }

    @Test
    void testTagSpecificConversionAndScale() throws ConversionPathException, ConversionFactorException {
        UnitEntity tablespoon = unitRepository.findById(TABLESPOON_ID).orElse(null);
        UnitEntity grams = unitRepository.findById(GRAM_ID).orElse(null);

        ConversionRequest listContext = new ConversionRequest(ConversionTargetType.List, DomainType.METRIC);
        // tablespoon of butter to grams
        ConvertibleAmount amount = new SimpleAmount(1, tablespoon, BUTTER_CONVERSION_ID, false, null);
        ConvertibleAmount converted = converterService.convert(amount, grams);
        assertNotNull(converted);
        assertEquals(14.2, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(GRAM_ID, converted.getUnit().getId());

        converted = converterService.convert(amount, listContext);
        assertNotNull(converted);
        assertEquals(14.2, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(GRAM_ID, converted.getUnit().getId());

        // 16 tablespoons of butter to metric
        ConvertibleAmount bigAmount = new SimpleAmount(16.0, tablespoon, BUTTER_CONVERSION_ID, false, null);
        converted = converterService.convert(bigAmount, listContext);
        assertNotNull(converted);
        assertEquals(227.2, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(GRAM_ID, converted.getUnit().getId());

        // 16 tablespoons of butter to list context - metric
        bigAmount = new SimpleAmount(16.0, tablespoon, BUTTER_CONVERSION_ID, false, null);
        converted = converterService.convert(bigAmount, listContext);
        assertNotNull(converted);
        assertEquals(227.2, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(GRAM_ID, converted.getUnit().getId());

        // 16 tablespoons of butter to dish context - metric
        listContext = new ConversionRequest(ConversionTargetType.Dish, DomainType.METRIC);
        bigAmount = new SimpleAmount(16.0, tablespoon, BUTTER_CONVERSION_ID, false, null);
        converted = converterService.convert(bigAmount, listContext);
        assertNotNull(converted);
        assertEquals(16.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(TABLESPOON_ID, converted.getUnit().getId());

        // 8 tablespoons of butter to dish context - metric
        listContext = new ConversionRequest(ConversionTargetType.Dish, DomainType.METRIC);
        bigAmount = new SimpleAmount(8.0, tablespoon, BUTTER_CONVERSION_ID, false, null);
        converted = converterService.convert(bigAmount, listContext);
        assertNotNull(converted);
        assertEquals(8.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(TABLESPOON_ID, converted.getUnit().getId());

        // 7 tablespoons of butter to dish context - metric
        listContext = new ConversionRequest(ConversionTargetType.Dish, DomainType.METRIC);
        bigAmount = new SimpleAmount(7.0, tablespoon, BUTTER_CONVERSION_ID, false, null);
        converted = converterService.convert(bigAmount, listContext);
        assertNotNull(converted);
        assertEquals(7.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(TABLESPOON_ID, converted.getUnit().getId());

        // 16 tablespoons of butter to list context - us
        listContext = new ConversionRequest(ConversionTargetType.List, DomainType.US);
        bigAmount = new SimpleAmount(16.0, tablespoon, BUTTER_CONVERSION_ID, false, null);
        converted = converterService.convert(bigAmount, listContext);
        assertNotNull(converted);
        assertEquals(0.5, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(LB_ID, converted.getUnit().getId());


        // 16 tablespoons of butter to context dish - us
        ConversionRequest dishContext = new ConversionRequest(ConversionTargetType.Dish, DomainType.US);
        bigAmount = new SimpleAmount(8.0, tablespoon, BUTTER_CONVERSION_ID, false, null);
        converted = converterService.convert(bigAmount, dishContext);
        assertNotNull(converted);
        assertEquals(1.005, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(BUTTER_STICK_ID, converted.getUnit().getId());
    }

    @Test
    void testTagSpecificIntegral() throws ConversionPathException, ConversionFactorException {
        UnitEntity slice = unitRepository.findById(SLICE_ID).orElse(null);
        UnitEntity grams = unitRepository.findById(GRAM_ID).orElse(null);

        // list context, metric
        // tablespoon of butter to grams
        ConvertibleAmount amount = new SimpleAmount(1, slice, CHEDDAR_CONVERSION_ID, false, null);
        ConvertibleAmount converted = converterService.convert(amount, grams);
        assertNotNull(converted);
        assertEquals(21.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(GRAM_ID, converted.getUnit().getId());

        // dish context metric
        ConversionRequest dishContext = new ConversionRequest(ConversionTargetType.Dish, DomainType.METRIC);
        converted = converterService.convert(amount, dishContext);
        assertNotNull(converted);
        assertEquals(1, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(SLICE_ID, converted.getUnit().getId());


        // cheddar slice to us dish
        dishContext = new ConversionRequest(ConversionTargetType.Dish, DomainType.US);
        ConvertibleAmount bigAmount = new SimpleAmount(6.0, slice, CHEDDAR_CONVERSION_ID, false, null);
        converted = converterService.convert(bigAmount, dishContext);
        assertNotNull(converted);
        assertEquals(4.435, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(OUNCE_ID, converted.getUnit().getId());

        // cheddar slice to us list
        dishContext = new ConversionRequest(ConversionTargetType.List, DomainType.US);
        bigAmount = new SimpleAmount(16.0, slice, CHEDDAR_CONVERSION_ID, false, null);
        converted = converterService.convert(bigAmount, dishContext);
        assertNotNull(converted);
        assertEquals(0.739, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(LB_ID, converted.getUnit().getId());
    }

    @Test
    void testTagSpecificUnitToGrams() throws ConversionPathException, ConversionFactorException {
        UnitEntity singleUnit = unitRepository.findById(UNIT_ID).orElse(null);
        UnitEntity grams = unitRepository.findById(GRAM_ID).orElse(null);
        UnitEntity pounds = unitRepository.findById(LB_ID).orElse(null);

        // one chicken drumstick to grams
        ConvertibleAmount amount = new SimpleAmount(1, singleUnit, CHICKEN_DRUMSTICK_ID, false, null);
        ConvertibleAmount converted = converterService.convert(amount, grams);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(88.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(GRAM_ID, converted.getUnit().getId());

        // list context metric
        ConversionRequest listContext = new ConversionRequest(ConversionTargetType.List, DomainType.METRIC);
        converted = converterService.convert(amount, listContext);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(1.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(UNIT_ID, converted.getUnit().getId());


        // dish context metric
        ConversionRequest dishContext = new ConversionRequest(ConversionTargetType.Dish, DomainType.METRIC);
        converted = converterService.convert(amount, dishContext);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(1, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(1.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(UNIT_ID, converted.getUnit().getId());



        // chicken drumstick us dish
        dishContext = new ConversionRequest(ConversionTargetType.Dish, DomainType.US);
        ConvertibleAmount bigAmount = new SimpleAmount(6.0, singleUnit, CHICKEN_DRUMSTICK_ID, false, null);
        converted = converterService.convert(bigAmount, dishContext);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(6.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(UNIT_ID, converted.getUnit().getId());


        // chicken drumstick us list
        dishContext = new ConversionRequest(ConversionTargetType.List, DomainType.US);
        bigAmount = new SimpleAmount(16.0, singleUnit, CHICKEN_DRUMSTICK_ID, false, null);
        converted = converterService.convert(bigAmount, dishContext);
        System.out.println(converted);
        assertNotNull(converted);
        assertEquals(16.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(UNIT_ID, converted.getUnit().getId());

        // chicken drumstick us pounds
        bigAmount = new SimpleAmount(16.0, singleUnit, CHICKEN_DRUMSTICK_ID, false, null);
        converted = converterService.convert(bigAmount, pounds);
        System.out.println(converted);
        assertNotNull(converted);
        assertEquals(3.098, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(LB_ID, converted.getUnit().getId());

        // chicken drumstick us domain
        bigAmount = new SimpleAmount(16.0, singleUnit, CHICKEN_DRUMSTICK_ID, false, null);
        converted = converterService.convert(bigAmount, DomainType.US);
        System.out.println(converted);
        assertNotNull(converted);
        assertEquals(3.098, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(LB_ID, converted.getUnit().getId());

        // chicken drumstick metric domain
        bigAmount = new SimpleAmount(16.0, singleUnit, CHICKEN_DRUMSTICK_ID, false, null);
        converted = converterService.convert(bigAmount, DomainType.METRIC);
        System.out.println(converted);
        assertNotNull(converted);
        assertEquals(1.408, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(KG_ID, converted.getUnit().getId());
    }

    @Test
    void testTagSpecificMarker() throws ConversionPathException, ConversionFactorException {
        UnitEntity slice = unitRepository.findById(SLICE_ID).orElse(null);
        UnitEntity grams = unitRepository.findById(GRAM_ID).orElse(null);

        // list context, metric

        // tomato slice to grams - marker sliced
        ConvertibleAmount amount = new SimpleAmount(1, slice, TOMATO_CONVERSION_ID, false, "sliced");
        ConvertibleAmount converted = converterService.convert(amount, grams);
        System.out.println(converted);
        assertNotNull(converted);
        assertEquals(20.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(GRAM_ID, converted.getUnit().getId());

        // list context metric
        ConversionRequest listContext = new ConversionRequest(ConversionTargetType.List, DomainType.METRIC);
        converted = converterService.convert(amount, listContext);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(0.135, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(UNIT_ID, converted.getUnit().getId());

        // dish context metric
        ConversionRequest dishContext = new ConversionRequest(ConversionTargetType.Dish, DomainType.METRIC);
        converted = converterService.convert(amount, dishContext);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(1, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(SLICE_ID, converted.getUnit().getId());

        // tomato slice to us dish
         dishContext = new ConversionRequest(ConversionTargetType.Dish, DomainType.US);
        ConvertibleAmount bigAmount = new SimpleAmount(6.0, slice, TOMATO_CONVERSION_ID, false, "sliced");
         converted = converterService.convert(bigAmount, dishContext);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(4.224, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(OUNCE_ID, converted.getUnit().getId());

        // tomato slice to us list
        dishContext = new ConversionRequest(ConversionTargetType.List, DomainType.US);
        bigAmount = new SimpleAmount(16.0, slice, TOMATO_CONVERSION_ID, false, null);
        converted = converterService.convert(bigAmount, dishContext);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(2.162, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(UNIT_ID, converted.getUnit().getId());
    }

    @Test
    void testCupOfDicedTomatoes() throws ConversionPathException, ConversionFactorException {
        UnitEntity grams = unitRepository.findById(GRAM_ID).orElse(null);
        UnitEntity cup = unitRepository.findById(CUPS_ID).orElse(null);

        // list context, metric

        // tomato slice to grams
        ConvertibleAmount amount = new SimpleAmount(1, cup, TOMATO_CONVERSION_ID, false, "chopped");
        ConvertibleAmount converted = converterService.convert(amount, grams);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(180.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(GRAM_ID, converted.getUnit().getId());

        // list context metric
        ConversionRequest listContext = new ConversionRequest(ConversionTargetType.List, DomainType.METRIC);
        converted = converterService.convert(amount, listContext);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(1.216, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(UNIT_ID, converted.getUnit().getId());

        // dish context metric
        ConversionRequest dishContext = new ConversionRequest(ConversionTargetType.Dish, DomainType.METRIC);
        converted = converterService.convert(amount, dishContext);
        assertNotNull(converted);
        assertEquals(180.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(GRAM_ID, converted.getUnit().getId());

        dishContext = new ConversionRequest(ConversionTargetType.Dish, DomainType.US);
        ConvertibleAmount bigAmount = new SimpleAmount(6.0, cup, TOMATO_CONVERSION_ID, false, "chopped");
        converted = converterService.convert(bigAmount, dishContext);
        assertNotNull(converted);
        assertEquals(2.376, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(LB_ID, converted.getUnit().getId());

        // cup chopped tomatoes to us list
        dishContext = new ConversionRequest(ConversionTargetType.List, DomainType.US);
        bigAmount = new SimpleAmount(6.0, cup, TOMATO_CONVERSION_ID, false, "chopped");
        converted = converterService.convert(bigAmount, dishContext);
        assertNotNull(converted);
        assertEquals(7.297, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(UNIT_ID, converted.getUnit().getId());
        assertEquals("medium", converted.getUnitSize());
    }

    @Test
    void testCupOfDicedTomatoesUnitSize() throws ConversionPathException, ConversionFactorException {
        UnitEntity cup = unitRepository.findById(CUPS_ID).orElse(null);
        UnitEntity unit = unitRepository.findById(UNIT_ID).orElse(null);

        // list context, metric
        ConversionRequest listContext = new ConversionRequest(ConversionTargetType.List, DomainType.METRIC);
        // tomato slice to units - no size passed
         ConvertibleAmount amount = new SimpleAmount(1, cup, TOMATO_CONVERSION_ID, false, "chopped");
         ConvertibleAmount converted = converterService.convert(amount, listContext);
         assertNotNull(converted);
         System.out.println(converted);
         assertEquals(1.216, RoundingUtils.roundToThousandths(converted.getQuantity()));
         assertEquals(UNIT_ID, converted.getUnit().getId());

        // to unit, with size
        converted = converterService.convert(amount, unit, "large");
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(0.989, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(UNIT_ID, converted.getUnit().getId());

        // to metric list, with size
        ConvertibleAmount bigAmount = new SimpleAmount(6.0, cup, TOMATO_CONVERSION_ID, false, "chopped");
        ConversionRequest listContextSmall = new ConversionRequest(ConversionTargetType.List, DomainType.METRIC, "small");
        converted = converterService.convert(bigAmount, listContextSmall);
        System.out.println(converted);
        assertNotNull(converted);
        assertEquals(8.78, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(UNIT_ID, converted.getUnit().getId());

        // to us list, with size
        bigAmount = new SimpleAmount(6.0, cup, TOMATO_CONVERSION_ID, false, "chopped");
        ConversionRequest listContextLarge = new ConversionRequest(ConversionTargetType.List, DomainType.US, "large");
        converted = converterService.convert(bigAmount, listContextLarge);
        System.out.println(converted);
        assertNotNull(converted);
        assertEquals(5.934, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(UNIT_ID, converted.getUnit().getId());
    }

    @Test
    void testTagSpecificRunThroughMetric() throws ConversionPathException, ConversionFactorException {
        UnitEntity tablespoon = unitRepository.findById(TABLESPOON_ID).orElse(null);
        UnitEntity grams = unitRepository.findById(GRAM_ID).orElse(null);

        // 8 tablespoons to grams
        ConvertibleAmount amount = new SimpleAmount(8.0, tablespoon, BUTTER_CONVERSION_ID, false, null);
        ConvertibleAmount converted = converterService.convert(amount, grams);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(GRAM_ID, converted.getUnit().getId());
        assertEquals(113.6, RoundingUtils.roundToThousandths(converted.getQuantity()));

        // 8 tablespoons to dish, metric
        ConversionRequest dishContext = new ConversionRequest(ConversionTargetType.Dish, DomainType.METRIC);
        converted = converterService.convert(amount, dishContext);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(TABLESPOON_ID, converted.getUnit().getId());
        assertEquals(8.0, RoundingUtils.roundToThousandths(converted.getQuantity()));


        // 8 tablespoons to list, metric
        ConversionRequest listContext = new ConversionRequest(ConversionTargetType.List, DomainType.METRIC);
        converted = converterService.convert(amount, listContext);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(GRAM_ID, converted.getUnit().getId());
        assertEquals(113.6, RoundingUtils.roundToThousandths(converted.getQuantity()));

        // 8 tablespoons to metric
         converted = converterService.convert(amount, DomainType.METRIC);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(113.6, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(GRAM_ID, converted.getUnit().getId());
    }

    @Test
    void testTagSpecificRunThroughUS() throws ConversionPathException, ConversionFactorException {
        UnitEntity tablespoon = unitRepository.findById(TABLESPOON_ID).orElse(null);
        UnitEntity ounce = unitRepository.findById(OUNCE_ID).orElse(null);

        // 8 tablespoons to grams
        ConvertibleAmount amount = new SimpleAmount(8.0, tablespoon, BUTTER_CONVERSION_ID, false, null);
        ConvertibleAmount converted = converterService.convert(amount, ounce);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(OUNCE_ID, converted.getUnit().getId());
        assertEquals(4.007, RoundingUtils.roundToThousandths(converted.getQuantity()));

        // 8 tablespoons to dish, us
        ConversionRequest dishContext = new ConversionRequest(ConversionTargetType.Dish, DomainType.US);
        converted = converterService.convert(amount, dishContext);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(BUTTER_STICK_ID, converted.getUnit().getId());
        assertEquals(1.005, RoundingUtils.roundToThousandths(converted.getQuantity()));


        // 8 tablespoons to list, us
        ConversionRequest listContext = new ConversionRequest(ConversionTargetType.List, DomainType.US);
        converted = converterService.convert(amount, listContext);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(OUNCE_ID, converted.getUnit().getId());
        assertEquals(3.999, RoundingUtils.roundToThousandths(converted.getQuantity()));

        // 8 tablespoons to us
         converted = converterService.convert(amount, DomainType.US);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(3.999, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(OUNCE_ID, converted.getUnit().getId());
    }

    @Test
    void testHalfAKiloTomatoesToUnit() throws ConversionPathException, ConversionFactorException {
        UnitEntity unit = unitRepository.findById(UNIT_ID).orElse(null);
        UnitEntity kilo = unitRepository.findById(KG_ID).orElse(null);

        // half a kilo of tomatoes
        ConvertibleAmount amount = new SimpleAmount(0.5, kilo, TOMATO_CONVERSION_ID, false, null);
        ConvertibleAmount converted = converterService.convert(amount, unit);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(3.378, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(UNIT_ID, converted.getUnit().getId());
    }

    @Test
    void testCupOfDicedTomatoesToList() throws ConversionPathException, ConversionFactorException {
        UnitEntity cupUnit = unitRepository.findById(CUPS_ID).orElse(null);

        // Not converting diced tomatoes to us weight
        // problem in tag specific version
        // also - should fix this so that if tag specific conversion fails, the scaling doesn't happen -
        // it gives really weird results like 0.000006 pounds.....

        // 1 cup diced tomatoes
        ConvertibleAmount amount = new SimpleAmount(1, cupUnit, TOMATO_CONVERSION_ID, false, "chopped");
        ConversionRequest listContext = new ConversionRequest(ConversionTargetType.List, DomainType.US);
        ConvertibleAmount converted = converterService.convert(amount, listContext);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(1.216, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(UNIT_ID, converted.getUnit().getId());
    }

    @Test
    void testHalfAKiloTomatoesToListUS() throws ConversionPathException, ConversionFactorException {
        UnitEntity kiloUnit = unitRepository.findById(KG_ID).orElse(null);

        // Not converting diced tomatoes to us weight
        // problem in tag specific version
        // also - should fix this so that if tag specific conversion fails, the scaling doesn't happen -
        // it gives really weird results like 0.000006 pounds.....

        // 1 cup diced tomatoes
        ConvertibleAmount amount = new SimpleAmount(0.5, kiloUnit, TOMATO_CONVERSION_ID, false, null);
        ConversionRequest listContext = new ConversionRequest(ConversionTargetType.List, DomainType.US);
        ConvertibleAmount converted = converterService.convert(amount, listContext);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(3.378, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(UNIT_ID, converted.getUnit().getId());

    }
}
