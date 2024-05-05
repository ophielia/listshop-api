/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.lmt.service.conversion;

import com.meg.listshop.Application;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.conversion.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.ConversionRequest;
import com.meg.listshop.conversion.data.pojo.ConversionTargetType;
import com.meg.listshop.conversion.data.pojo.SimpleAmount;
import com.meg.listshop.conversion.data.pojo.UnitType;
import com.meg.listshop.conversion.data.repository.UnitRepository;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;
import com.meg.listshop.conversion.service.ConverterService;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.conversion.tools.RoundingUtils;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@Sql(value = "data/ConversionTest.sql")
@Sql(value = "data/ConversionTest-rollback.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ConversionTest {

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
        ConvertibleAmount amount = new SimpleAmount(1, gramUnitOpt.get());
        ConvertibleAmount converted = converterService.convert(amount, ozUnitOpt.get());
        assertNotNull(converted);
        assertEquals(0.035, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(1009L, converted.getUnit().getId());

    }

    @Test
    public void testSingleHandlerWeightExact() throws ConversionPathException, ConversionFactorException {
        Optional<UnitEntity> ounceUnitOpt = unitRepository.findById(ounceId);
        Optional<UnitEntity> kgUnitOpt = unitRepository.findById(kgId);
        ConvertibleAmount amount = new SimpleAmount(20.0, ounceUnitOpt.get());
        ConvertibleAmount converted = converterService.convert(amount, kgUnitOpt.get());
        assertNotNull(converted);
        assertEquals(0.567, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(kgId, converted.getUnit().getId());

        Optional<UnitEntity> mgUnitOpt = unitRepository.findById(mgId);
        Optional<UnitEntity> lbUnitOpt = unitRepository.findById(lbId);
        amount = new SimpleAmount(400.0, mgUnitOpt.get());
        converted = converterService.convert(amount, lbUnitOpt.get());
        assertNotNull(converted);
        assertEquals(0.001, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(lbId, converted.getUnit().getId());

        Optional<UnitEntity> gUnitOpt = unitRepository.findById(gId);
        Optional<UnitEntity> ozUnitOpt = unitRepository.findById(ounceId);
        amount = new SimpleAmount(500.0, gUnitOpt.get());
        converted = converterService.convert(amount, ozUnitOpt.get());
        assertNotNull(converted);
        assertEquals(17.637, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(ounceId, converted.getUnit().getId());
    }

    @Test
    public void testSingleHandlerVolumeExact() throws ConversionPathException, ConversionFactorException {
        Optional<UnitEntity> gallonsOpt = unitRepository.findById(gallonId);
        Optional<UnitEntity> litersOpt = unitRepository.findById(literId);
        Optional<UnitEntity> milliliterOpt = unitRepository.findById(milliliterId);
        Optional<UnitEntity> cupsOpt = unitRepository.findById(flCupsId);
        Optional<UnitEntity> quartOpt = unitRepository.findById(quartId);
        Optional<UnitEntity> centiliterOpt = unitRepository.findById(centileterId);

        ConvertibleAmount amount = new SimpleAmount(20.0, litersOpt.get());
        ConvertibleAmount converted = converterService.convert(amount, gallonsOpt.get());
        assertNotNull(converted);
        assertEquals(5.283, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gallonId, converted.getUnit().getId());

        amount = new SimpleAmount(50, centiliterOpt.get());
        converted = converterService.convert(amount, quartOpt.get());
        assertNotNull(converted);
        assertEquals(0.528, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(quartId, converted.getUnit().getId());

        amount = new SimpleAmount(3.0, cupsOpt.get());
        converted = converterService.convert(amount, milliliterOpt.get());
        assertNotNull(converted);
        assertEquals(709.765, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(milliliterId, converted.getUnit().getId());
    }

    @Test
    public void testSingleHandlerVolumeType() throws ConversionPathException, ConversionFactorException {
        Optional<UnitEntity> litersOpt = unitRepository.findById(literId);
        Optional<UnitEntity> cupsOpt = unitRepository.findById(flCupsId);
        Optional<UnitEntity> centiliterOpt = unitRepository.findById(centileterId);

        ConvertibleAmount amount = new SimpleAmount(20.0, litersOpt.get());
        ConvertibleAmount converted = converterService.convert(amount, UnitType.US);
        assertNotNull(converted);
        assertEquals(5.283, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gallonId, converted.getUnit().getId());

        amount = new SimpleAmount(50, centiliterOpt.get());
        converted = converterService.convert(amount, UnitType.US);
        assertNotNull(converted);
        assertEquals(1.057, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(pintId, converted.getUnit().getId());


        amount = new SimpleAmount(3.0, cupsOpt.get());
        converted = converterService.convert(amount, UnitType.METRIC);
        assertNotNull(converted);
        assertEquals(0.71, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(literId, converted.getUnit().getId());
    }

    @Test
    public void testSimpleMetricVolumeListContext() throws ConversionPathException, ConversionFactorException {
        Optional<UnitEntity> quartOpt = unitRepository.findById(quartId);
        Optional<UnitEntity> pintOpt = unitRepository.findById(pintId);

        ConvertibleAmount amount = new SimpleAmount(3, quartOpt.get());
        ConversionRequest listContext = new ConversionRequest(ConversionTargetType.List, UnitType.METRIC);
        ConvertibleAmount converted = converterService.convert(amount, listContext);
        assertNotNull(converted);
        assertEquals(2.839, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(literId, converted.getUnit().getId());

        amount = new SimpleAmount(0.021133764, pintOpt.get());
        converted = converterService.convert(amount, UnitType.METRIC);
        assertNotNull(converted);
        // confirm that we have this pint fraction translated to 1 centiliter
        assertEquals(1.000, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(centileterId, converted.getUnit().getId());

        // now, converting the same pint fraction in a list context should not result in a conversion to centiliter.
        // It should be milliliters - since that's the closest unit to this quantity permitted in a list
        amount = new SimpleAmount(0.021133764, pintOpt.get());
        converted = converterService.convert(amount, listContext);
        assertNotNull(converted);
        assertEquals(milliliterId, converted.getUnit().getId());
        assertEquals(10.000, RoundingUtils.roundToThousandths(converted.getQuantity()));
    }

    @Test
    public void testHybridDishContext() throws ConversionPathException, ConversionFactorException {
        Optional<UnitEntity> teaspoonOpt = unitRepository.findById(teaspoonId);

        ConvertibleAmount amount = new SimpleAmount(3, teaspoonOpt.get());
        ConversionRequest listContext = new ConversionRequest(ConversionTargetType.Dish, UnitType.METRIC);
        ConvertibleAmount converted = converterService.convert(amount, listContext);
        assertNotNull(converted);
        assertEquals(1, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(tablespoonId, converted.getUnit().getId());

    }

    @Test
    public void testSimpleUsVolumeListContext() throws ConversionPathException, ConversionFactorException {
        Optional<UnitEntity> milliliterOpt = unitRepository.findById(milliliterId);
        Optional<UnitEntity> centiliterOpt = unitRepository.findById(centileterId);

        ConvertibleAmount amount = new SimpleAmount(473.17, milliliterOpt.get());
        ConversionRequest listContext = new ConversionRequest(ConversionTargetType.List, UnitType.US);

        ConvertibleAmount converted = converterService.convert(amount, listContext);
        assertNotNull(converted);
        assertEquals(0.5, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(quartId, converted.getUnit().getId());

        amount = new SimpleAmount(23.6588, centiliterOpt.get());
        converted = converterService.convert(amount, UnitType.US);
        assertNotNull(converted);
        // confirm that we have this centiliter converted to 1 cup
        assertEquals(1.000, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(flCupsId, converted.getUnit().getId());

        // now, converting the same amount in a list context should not result in a conversion to cups.
        // It should be quarts - since that's the closest unit to this quantity permitted in a list
        amount = new SimpleAmount(23.6588, centiliterOpt.get());
        converted = converterService.convert(amount, listContext);
        assertNotNull(converted);
        assertEquals(fluidOzId, converted.getUnit().getId());
        assertEquals(8.0, RoundingUtils.roundToThousandths(converted.getQuantity()));

    }

    @Test
    public void testSimpleUsWeightListContext() throws ConversionPathException, ConversionFactorException {
        Optional<UnitEntity> gramOpt = unitRepository.findById(gId);

        // 113.398093 gm = 4 ounces => ounce destination
        ConvertibleAmount amount = new SimpleAmount(113.398093, gramOpt.get());
        ConversionRequest listContext = new ConversionRequest(ConversionTargetType.List, UnitType.US);
        ConvertibleAmount converted = converterService.convert(amount, listContext);
        assertNotNull(converted);
        assertEquals(ounceId, converted.getUnit().getId());
        assertEquals(3.992, RoundingUtils.roundToThousandths(converted.getQuantity()));

        // 15 Ounce = 425.242847 Gram  => pound destination
        amount = new SimpleAmount(425.242847, gramOpt.get());
        listContext = new ConversionRequest(ConversionTargetType.List, UnitType.US);
        converted = converterService.convert(amount, listContext);
        assertNotNull(converted);
        assertEquals(0.936, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(lbId, converted.getUnit().getId());

    }

    @Test
    public void testVolumeToWeightConversion() throws ConversionPathException, ConversionFactorException {
        UnitEntity tablespoon = unitRepository.findById(tablespoonId).orElse(null);
        UnitEntity grams = unitRepository.findById(gId).orElse(null);

        ConversionRequest listContext = new ConversionRequest(ConversionTargetType.List, UnitType.METRIC);
        // tablespoon of butter to grams
        ConvertibleAmount amount = new SimpleAmount(1, tablespoon, 348L, false, null);
        //ConvertibleAmount amount = new SimpleAmount(1, tablespoon, butterTagId, false, null);
        ConvertibleAmount converted = converterService.convert(amount, grams);
        assertNotNull(converted);
        assertEquals(14.175, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gId, converted.getUnit().getId());

        // 1 tablespoon of butter to metric
        converted = converterService.convert(amount, listContext);
        assertNotNull(converted);
        assertEquals(14.175, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gId, converted.getUnit().getId());


        // 16 tablespoons of butter to metric
        ConvertibleAmount bigAmount = new SimpleAmount(16.0, tablespoon, butterTagId, false, null);
        converted = converterService.convert(bigAmount, grams);
        assertNotNull(converted);
        assertEquals(227.2, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gId, converted.getUnit().getId());

        // 16 tablespoons of butter to list context - metric
        bigAmount = new SimpleAmount(16.0, tablespoon, butterTagId, false, null);
        converted = converterService.convert(bigAmount, listContext);
        assertNotNull(converted);
        assertEquals(227.2, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gId, converted.getUnit().getId());

        // 16 tablespoons of butter to dish context - metric
        listContext = new ConversionRequest(ConversionTargetType.Dish, UnitType.METRIC);
        bigAmount = new SimpleAmount(16.0, tablespoon, butterTagId, false, null);
        converted = converterService.convert(bigAmount, listContext);
        assertNotNull(converted);
        assertEquals(16.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(tablespoonId, converted.getUnit().getId());

        // 8 tablespoons of butter to dish context - metric
        listContext = new ConversionRequest(ConversionTargetType.Dish, UnitType.METRIC);
        bigAmount = new SimpleAmount(8.0, tablespoon, butterTagId, false, null);
        converted = converterService.convert(bigAmount, listContext);
        assertNotNull(converted);
        assertEquals(8.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(tablespoonId, converted.getUnit().getId());

        // 7 tablespoons of butter to dish context - metric
        listContext = new ConversionRequest(ConversionTargetType.Dish, UnitType.METRIC);
        bigAmount = new SimpleAmount(7.0, tablespoon, butterTagId, false, null);
        converted = converterService.convert(bigAmount, listContext);
        assertNotNull(converted);
        assertEquals(7.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(tablespoonId, converted.getUnit().getId());


        // 8 tablespoons of butter to context dish - us
        ConversionRequest dishContext = new ConversionRequest(ConversionTargetType.Dish, UnitType.US);
        bigAmount = new SimpleAmount(8.0, tablespoon, butterTagId, false, null);
        converted = converterService.convert(bigAmount, dishContext);
        assertNotNull(converted);
        assertEquals(1.005, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(butterStickId, converted.getUnit().getId());

        // 16 tablespoons of butter to list context - us
        listContext = new ConversionRequest(ConversionTargetType.List, UnitType.US);
        bigAmount = new SimpleAmount(16.0, tablespoon, butterTagId, false, null);
        converted = converterService.convert(bigAmount, listContext);
        assertNotNull(converted);
        assertEquals(0.5, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(lbId, converted.getUnit().getId());
    }


    @Test
    public void testTagSpecificConversion() throws ConversionPathException, ConversionFactorException {
        UnitEntity tablespoon = unitRepository.findById(cupsId).orElse(null);
        UnitEntity grams = unitRepository.findById(gId).orElse(null);

        ConversionRequest listContext = new ConversionRequest(ConversionTargetType.List, UnitType.METRIC);
        // 1/2 cup onions to unit, marker chopped
        ConvertibleAmount amount = new SimpleAmount(0.5, tablespoon, onionConversionId, false, "chopped");
        ConvertibleAmount converted = converterService.convert(amount, grams);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(80.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gId, converted.getUnit().getId());

        // 1/2 cup onions to unit, marker chopped
        amount = new SimpleAmount(0.5, tablespoon, onionConversionId, false, "chopped");
        converted = converterService.convert(amount, listContext);
        assertNotNull(converted);
        //   assertEquals(80.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        //   assertEquals(gId, converted.getUnit().getId());
    }

    @Test
    public void testTagSpecificConversionAndScale() throws ConversionPathException, ConversionFactorException {
        UnitEntity tablespoon = unitRepository.findById(tablespoonId).orElse(null);
        UnitEntity grams = unitRepository.findById(gId).orElse(null);

        ConversionRequest listContext = new ConversionRequest(ConversionTargetType.List, UnitType.METRIC);
        // tablespoon of butter to grams
        ConvertibleAmount amount = new SimpleAmount(1, tablespoon, butterConversionId, false, null);
        ConvertibleAmount converted = converterService.convert(amount, grams);
        assertNotNull(converted);
        assertEquals(14.2, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gId, converted.getUnit().getId());

        converted = converterService.convert(amount, listContext);
        assertNotNull(converted);
        assertEquals(14.2, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gId, converted.getUnit().getId());

        // 16 tablespoons of butter to metric
        ConvertibleAmount bigAmount = new SimpleAmount(16.0, tablespoon, butterConversionId, false, null);
        converted = converterService.convert(bigAmount, listContext);
        assertNotNull(converted);
        assertEquals(227.2, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gId, converted.getUnit().getId());

        // 16 tablespoons of butter to list context - metric
        bigAmount = new SimpleAmount(16.0, tablespoon, butterConversionId, false, null);
        converted = converterService.convert(bigAmount, listContext);
        assertNotNull(converted);
        assertEquals(227.2, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gId, converted.getUnit().getId());

        // 16 tablespoons of butter to dish context - metric
        listContext = new ConversionRequest(ConversionTargetType.Dish, UnitType.METRIC);
        bigAmount = new SimpleAmount(16.0, tablespoon, butterConversionId, false, null);
        converted = converterService.convert(bigAmount, listContext);
        assertNotNull(converted);
        assertEquals(16.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(tablespoonId, converted.getUnit().getId());

        // 8 tablespoons of butter to dish context - metric
        listContext = new ConversionRequest(ConversionTargetType.Dish, UnitType.METRIC);
        bigAmount = new SimpleAmount(8.0, tablespoon, butterConversionId, false, null);
        converted = converterService.convert(bigAmount, listContext);
        assertNotNull(converted);
        assertEquals(8.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(tablespoonId, converted.getUnit().getId());

        // 7 tablespoons of butter to dish context - metric
        listContext = new ConversionRequest(ConversionTargetType.Dish, UnitType.METRIC);
        bigAmount = new SimpleAmount(7.0, tablespoon, butterConversionId, false, null);
        converted = converterService.convert(bigAmount, listContext);
        assertNotNull(converted);
        assertEquals(7.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(tablespoonId, converted.getUnit().getId());

        // 16 tablespoons of butter to list context - us
        listContext = new ConversionRequest(ConversionTargetType.List, UnitType.US);
        bigAmount = new SimpleAmount(16.0, tablespoon, butterConversionId, false, null);
        converted = converterService.convert(bigAmount, listContext);
        assertNotNull(converted);
        assertEquals(0.5, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(lbId, converted.getUnit().getId());


        // 16 tablespoons of butter to context dish - us
        ConversionRequest dishContext = new ConversionRequest(ConversionTargetType.Dish, UnitType.US);
        bigAmount = new SimpleAmount(8.0, tablespoon, butterConversionId, false, null);
        converted = converterService.convert(bigAmount, dishContext);
        assertNotNull(converted);
        assertEquals(1.005, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(butterStickId, converted.getUnit().getId());
    }

    @Test
    public void testTagSpecificIntegral() throws ConversionPathException, ConversionFactorException {
        UnitEntity slice = unitRepository.findById(sliceId).orElse(null);
        UnitEntity grams = unitRepository.findById(gId).orElse(null);

        // list context, metric
        // tablespoon of butter to grams
        ConvertibleAmount amount = new SimpleAmount(1, slice, cheddarConversionId, false, null);
        ConvertibleAmount converted = converterService.convert(amount, grams);
        assertNotNull(converted);
        assertEquals(21.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gId, converted.getUnit().getId());

        // dish context metric
        ConversionRequest dishContext = new ConversionRequest(ConversionTargetType.Dish, UnitType.METRIC);
        converted = converterService.convert(amount, dishContext);
        assertNotNull(converted);
        assertEquals(1, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(sliceId, converted.getUnit().getId());


        // cheddar slice to us dish
        dishContext = new ConversionRequest(ConversionTargetType.Dish, UnitType.US);
        ConvertibleAmount bigAmount = new SimpleAmount(6.0, slice, cheddarConversionId, false, null);
        converted = converterService.convert(bigAmount, dishContext);
        assertNotNull(converted);
        assertEquals(4.435, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(ounceId, converted.getUnit().getId());

        // cheddar slice to us list
        dishContext = new ConversionRequest(ConversionTargetType.List, UnitType.US);
        bigAmount = new SimpleAmount(16.0, slice, cheddarConversionId, false, null);
        converted = converterService.convert(bigAmount, dishContext);
        assertNotNull(converted);
        assertEquals(0.739, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(lbId, converted.getUnit().getId());
    }

    @Test
    public void testTagSpecificMarker() throws ConversionPathException, ConversionFactorException {
        UnitEntity slice = unitRepository.findById(sliceId).orElse(null);
        UnitEntity grams = unitRepository.findById(gId).orElse(null);

        // list context, metric

        // tomato slice to grams - marker sliced
        ConvertibleAmount amount = new SimpleAmount(1, slice, tomatoConversionId, false, "sliced");
        ConvertibleAmount converted = converterService.convert(amount, grams);
        System.out.println(converted);
        assertNotNull(converted);
        assertEquals(20.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gId, converted.getUnit().getId());

        // list context metric
        ConversionRequest listContext = new ConversionRequest(ConversionTargetType.List, UnitType.METRIC);
        converted = converterService.convert(amount, listContext);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(0.163, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(unitId, converted.getUnit().getId());

        // dish context metric
        ConversionRequest dishContext = new ConversionRequest(ConversionTargetType.Dish, UnitType.METRIC);
        converted = converterService.convert(amount, dishContext);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(1, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(sliceId, converted.getUnit().getId());

        // tomato slice to us dish
         dishContext = new ConversionRequest(ConversionTargetType.Dish, UnitType.US);
        ConvertibleAmount bigAmount = new SimpleAmount(6.0, slice, tomatoConversionId, false, "sliced");
         converted = converterService.convert(bigAmount, dishContext);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(4.224, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(ounceId, converted.getUnit().getId());

        // tomato slice to us list
        dishContext = new ConversionRequest(ConversionTargetType.List, UnitType.US);
        bigAmount = new SimpleAmount(16.0, slice, tomatoConversionId, false, null);
        converted = converterService.convert(bigAmount, dishContext);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(2.602, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(unitId, converted.getUnit().getId());
    }

    @Test
    public void testCupOfDicedTomatoes() throws ConversionPathException, ConversionFactorException {
        UnitEntity grams = unitRepository.findById(gId).orElse(null);
        UnitEntity cup = unitRepository.findById(cupsId).orElse(null);

        // list context, metric

        // tomato slice to grams
        ConvertibleAmount amount = new SimpleAmount(1, cup, tomatoConversionId, false, "chopped");
        ConvertibleAmount converted = converterService.convert(amount, grams);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(180.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gId, converted.getUnit().getId());

        // list context metric
        ConversionRequest listContext = new ConversionRequest(ConversionTargetType.List, UnitType.METRIC);
        converted = converterService.convert(amount, listContext);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(1.463, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(unitId, converted.getUnit().getId());

        // dish context metric
        ConversionRequest dishContext = new ConversionRequest(ConversionTargetType.Dish, UnitType.METRIC);
        converted = converterService.convert(amount, dishContext);
        assertNotNull(converted);
        assertEquals(180.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gId, converted.getUnit().getId());

        dishContext = new ConversionRequest(ConversionTargetType.Dish, UnitType.US);
        ConvertibleAmount bigAmount = new SimpleAmount(6.0, cup, tomatoConversionId, false, "chopped");
        converted = converterService.convert(bigAmount, dishContext);
        assertNotNull(converted);
        assertEquals(2.376, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(lbId, converted.getUnit().getId());

        // cup chopped tomatoes to us list
        dishContext = new ConversionRequest(ConversionTargetType.List, UnitType.US);
        bigAmount = new SimpleAmount(6.0, cup, tomatoConversionId, false, "chopped");
        converted = converterService.convert(bigAmount, dishContext);
        assertNotNull(converted);
        assertEquals(8.78, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(unitId, converted.getUnit().getId());
        assertEquals("medium", converted.getUnitSize());
    }

    @Test
    public void testCupOfDicedTomatoesUnitSize() throws ConversionPathException, ConversionFactorException {
        UnitEntity grams = unitRepository.findById(gId).orElse(null);
        UnitEntity cup = unitRepository.findById(cupsId).orElse(null);

        // list context, metric

        // tomato slice to grams
        ConvertibleAmount amount = new SimpleAmount(1, cup, tomatoConversionId, false, "chopped");
        ConvertibleAmount converted = converterService.convert(amount, grams);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(180.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gId, converted.getUnit().getId());

        // list context metric
        ConversionRequest listContext = new ConversionRequest(ConversionTargetType.List, UnitType.METRIC);
        converted = converterService.convert(amount, listContext);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(1.463, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(unitId, converted.getUnit().getId());

        // dish context metric
        ConversionRequest dishContext = new ConversionRequest(ConversionTargetType.Dish, UnitType.METRIC);
        converted = converterService.convert(amount, dishContext);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(180.0, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gId, converted.getUnit().getId());

        dishContext = new ConversionRequest(ConversionTargetType.Dish, UnitType.US);
        ConvertibleAmount bigAmount = new SimpleAmount(6.0, cup, tomatoConversionId, false, "chopped");
        converted = converterService.convert(bigAmount, dishContext);
        assertNotNull(converted);
        assertEquals(2.376, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(lbId, converted.getUnit().getId());

        // cup chopped tomatoes to us list
        dishContext = new ConversionRequest(ConversionTargetType.List, UnitType.US);
        bigAmount = new SimpleAmount(6.0, cup, tomatoConversionId, false, "chopped");
        converted = converterService.convert(bigAmount, dishContext);
        System.out.println(converted);
        assertNotNull(converted);
        assertEquals(8.78, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(unitId, converted.getUnit().getId());
    }

    @Test
    public void testTagSpecificRunThroughMetric() throws ConversionPathException, ConversionFactorException {
        UnitEntity tablespoon = unitRepository.findById(tablespoonId).orElse(null);
        UnitEntity grams = unitRepository.findById(gId).orElse(null);

        // 8 tablespoons to grams
        ConvertibleAmount amount = new SimpleAmount(8.0, tablespoon, butterConversionId, false, null);
        ConvertibleAmount converted = converterService.convert(amount, grams);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(gId, converted.getUnit().getId());
        assertEquals(113.6, RoundingUtils.roundToThousandths(converted.getQuantity()));

        // 8 tablespoons to dish, metric
        ConversionRequest dishContext = new ConversionRequest(ConversionTargetType.Dish, UnitType.METRIC);
        converted = converterService.convert(amount, dishContext);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(tablespoonId, converted.getUnit().getId());
        assertEquals(8.0, RoundingUtils.roundToThousandths(converted.getQuantity()));


        // 8 tablespoons to list, metric
        ConversionRequest listContext = new ConversionRequest(ConversionTargetType.List, UnitType.METRIC);
        converted = converterService.convert(amount, listContext);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(gId, converted.getUnit().getId());
        assertEquals(113.6, RoundingUtils.roundToThousandths(converted.getQuantity()));

        // 8 tablespoons to metric
         converted = converterService.convert(amount, UnitType.METRIC);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(113.6, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gId, converted.getUnit().getId());
    }

    @Test
    public void testTagSpecificRunThroughUS() throws ConversionPathException, ConversionFactorException {
        UnitEntity tablespoon = unitRepository.findById(tablespoonId).orElse(null);
        UnitEntity ounce = unitRepository.findById(ounceId).orElse(null);

        // 8 tablespoons to grams
        ConvertibleAmount amount = new SimpleAmount(8.0, tablespoon, butterConversionId, false, null);
        ConvertibleAmount converted = converterService.convert(amount, ounce);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(ounceId, converted.getUnit().getId());
        assertEquals(4.007, RoundingUtils.roundToThousandths(converted.getQuantity()));

        // 8 tablespoons to dish, us
        ConversionRequest dishContext = new ConversionRequest(ConversionTargetType.Dish, UnitType.US);
        converted = converterService.convert(amount, dishContext);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(butterStickId, converted.getUnit().getId());
        assertEquals(1.005, RoundingUtils.roundToThousandths(converted.getQuantity()));


        // 8 tablespoons to list, us
        ConversionRequest listContext = new ConversionRequest(ConversionTargetType.List, UnitType.US);
        converted = converterService.convert(amount, listContext);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(ounceId, converted.getUnit().getId());
        assertEquals(3.999, RoundingUtils.roundToThousandths(converted.getQuantity()));

        // 8 tablespoons to us
         converted = converterService.convert(amount, UnitType.US);
        assertNotNull(converted);
        System.out.println(converted);
        assertEquals(0.25, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(lbId, converted.getUnit().getId());
    }

}
