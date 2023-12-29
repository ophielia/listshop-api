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
import com.meg.listshop.conversion.data.pojo.*;
import com.meg.listshop.conversion.data.repository.UnitRepository;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;
import com.meg.listshop.conversion.exceptions.ExceedsAllowedScaleException;
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

    private static final Long ounceId = 1009L;
    private static final Long mgId = 1016L;
    private static final Long gId = 1013L;
    private static final Long kgId = 1014L;
    private static final Long lbId = 1008L;

    private static final Long gallonId = 1005L;
    private static final Long literId = 1003L;
    private static final Long quartId = 1010L;
    private static final Long pintId = 1006L;
    private static final Long cupsId = 1000L;
    private static final Long centileterId = 1015L;
    private static final Long milliliterId = 1004L;
    private static final Long flOzId = 1007L;
    @Autowired
    ConversionService conversionService;

    @Autowired
    UnitRepository unitRepository;


    @ClassRule
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    @Test
    public void blowUpTest() throws ConversionPathException, ConversionFactorException, ExceedsAllowedScaleException {
        Optional<UnitEntity> gramUnitOpt = unitRepository.findById(1013L);
        Optional<UnitEntity> ozUnitOpt = unitRepository.findById(1009L);
        ConvertibleAmount amount = new SimpleAmount(1, gramUnitOpt.get());
        ConvertibleAmount converted = conversionService.convert(amount, ozUnitOpt.get());
        assertNotNull(converted);
        assertEquals(0.035, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(1009L, converted.getUnit().getId());

    }

    @Test
    public void testSingleHandlerWeightExact() throws ConversionPathException, ConversionFactorException, ExceedsAllowedScaleException {
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

        Optional<UnitEntity> gUnitOpt = unitRepository.findById(gId);
        Optional<UnitEntity> ozUnitOpt = unitRepository.findById(ounceId);
        amount = new SimpleAmount(500.0, gUnitOpt.get());
        converted = conversionService.convert(amount, ozUnitOpt.get());
        assertNotNull(converted);
        assertEquals(17.637, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(ounceId, converted.getUnit().getId());
    }

    @Test
    public void testSingleHandlerVolumeExact() throws ConversionPathException, ConversionFactorException, ExceedsAllowedScaleException {
        Optional<UnitEntity> gallonsOpt = unitRepository.findById(gallonId);
        Optional<UnitEntity> litersOpt = unitRepository.findById(literId);
        Optional<UnitEntity> milliliterOpt = unitRepository.findById(milliliterId);
        Optional<UnitEntity> cupsOpt = unitRepository.findById(cupsId);
        Optional<UnitEntity> quartOpt = unitRepository.findById(quartId);
        Optional<UnitEntity> centiliterOpt = unitRepository.findById(centileterId);

        ConvertibleAmount amount = new SimpleAmount(20.0, litersOpt.get());
        ConvertibleAmount converted = conversionService.convert(amount, gallonsOpt.get());
        assertNotNull(converted);
        assertEquals(5.283, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gallonId, converted.getUnit().getId());

        amount = new SimpleAmount(50, centiliterOpt.get());
        converted = conversionService.convert(amount, quartOpt.get());
        assertNotNull(converted);
        assertEquals(0.528, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(quartId, converted.getUnit().getId());

        amount = new SimpleAmount(3.0, cupsOpt.get());
        converted = conversionService.convert(amount, milliliterOpt.get());
        assertNotNull(converted);
        assertEquals(709.765, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(milliliterId, converted.getUnit().getId());
    }

    @Test
    public void testSingleHandlerVolumeType() throws ConversionPathException, ConversionFactorException, ExceedsAllowedScaleException {
        Optional<UnitEntity> litersOpt = unitRepository.findById(literId);
        Optional<UnitEntity> cupsOpt = unitRepository.findById(cupsId);
        Optional<UnitEntity> centiliterOpt = unitRepository.findById(centileterId);

        ConvertibleAmount amount = new SimpleAmount(20.0, litersOpt.get());
        ConvertibleAmount converted = conversionService.convert(amount, UnitType.US);
        assertNotNull(converted);
        assertEquals(5.283, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gallonId, converted.getUnit().getId());

        amount = new SimpleAmount(50, centiliterOpt.get());
        converted = conversionService.convert(amount, UnitType.US);
        assertNotNull(converted);
        assertEquals(1.057, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(pintId, converted.getUnit().getId());


        amount = new SimpleAmount(3.0, cupsOpt.get());
        converted = conversionService.convert(amount, UnitType.METRIC);
        assertNotNull(converted);
        assertEquals(0.71, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(literId, converted.getUnit().getId());
    }

    @Test
    public void testSimpleMetricVolumeListContext() throws ConversionPathException, ConversionFactorException, ExceedsAllowedScaleException {
        Optional<UnitEntity> gallonsOpt = unitRepository.findById(gallonId);
        Optional<UnitEntity> litersOpt = unitRepository.findById(literId);
        Optional<UnitEntity> milliliterOpt = unitRepository.findById(milliliterId);
        Optional<UnitEntity> cupsOpt = unitRepository.findById(cupsId);
        Optional<UnitEntity> quartOpt = unitRepository.findById(quartId);
        Optional<UnitEntity> pintOpt = unitRepository.findById(pintId);
        Optional<UnitEntity> centiliterOpt = unitRepository.findById(centileterId);

        ConvertibleAmount amount = new SimpleAmount(3, quartOpt.get());
        ConversionContext listContext = new ConversionContext(ConversionContextType.List, UnitType.METRIC, UnitSubtype.VOLUME);
        ConvertibleAmount converted = conversionService.convert(amount, listContext);
        assertNotNull(converted);
        assertEquals(2.839, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(literId, converted.getUnit().getId());

        amount = new SimpleAmount(0.021133764, pintOpt.get());
        converted = conversionService.convert(amount, UnitType.METRIC);
        assertNotNull(converted);
        // confirm that we have this pint fraction translated to 1 centiliter
        assertEquals(1.000, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(centileterId, converted.getUnit().getId());

        // now, converting the same pint fraction in a list context should not result in a conversion to centiliter.
        // It should be milliliters - since that's the closest unit to this quantity permitted in a list
        amount = new SimpleAmount(0.021133764, pintOpt.get());
        converted = conversionService.convert(amount, listContext);
        assertNotNull(converted);
        assertEquals(milliliterId, converted.getUnit().getId());
        assertEquals(10.000, RoundingUtils.roundToThousandths(converted.getQuantity()));
    }

    @Test
    public void testSimpleUsVolumeListContext() throws ConversionPathException, ConversionFactorException, ExceedsAllowedScaleException {
        Optional<UnitEntity> gallonsOpt = unitRepository.findById(gallonId);
        Optional<UnitEntity> litersOpt = unitRepository.findById(literId);
        Optional<UnitEntity> milliliterOpt = unitRepository.findById(milliliterId);
        Optional<UnitEntity> cupsOpt = unitRepository.findById(cupsId);
        Optional<UnitEntity> quartOpt = unitRepository.findById(quartId);
        Optional<UnitEntity> pintOpt = unitRepository.findById(pintId);
        Optional<UnitEntity> centiliterOpt = unitRepository.findById(centileterId);

        ConvertibleAmount amount = new SimpleAmount(473.17, milliliterOpt.get());
        ConversionContext listContext = new ConversionContext(ConversionContextType.List, UnitType.US, UnitSubtype.VOLUME);
        /**/
        ConvertibleAmount converted = conversionService.convert(amount, listContext);
        assertNotNull(converted);
        assertEquals(0.5, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(quartId, converted.getUnit().getId());

        amount = new SimpleAmount(23.6588, centiliterOpt.get());
        converted = conversionService.convert(amount, UnitType.US);
        assertNotNull(converted);
        // confirm that we have this centiliter converted to 1 cup
        assertEquals(1.000, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(cupsId, converted.getUnit().getId());

        // now, converting the same amount in a list context should not result in a conversion to cups.
        // It should be quarts - since that's the closest unit to this quantity permitted in a list
        amount = new SimpleAmount(23.6588, centiliterOpt.get());
        converted = conversionService.convert(amount, listContext);
        assertNotNull(converted);
        assertEquals(quartId, converted.getUnit().getId());
        assertEquals(0.25, RoundingUtils.roundToThousandths(converted.getQuantity()));

    }

    @Test
    public void testSimpleUsWeightListContext() throws ConversionPathException, ConversionFactorException , ExceedsAllowedScaleException{
        Optional<UnitEntity> poundOpt = unitRepository.findById(lbId);
        Optional<UnitEntity> gramOpt = unitRepository.findById(gId);
        Optional<UnitEntity> ounceOpt = unitRepository.findById(ounceId);
        Optional<UnitEntity> centiliterOpt = unitRepository.findById(centileterId);


        // 113.398093 gm = 4 ounces => ounce destination
        ConvertibleAmount amount = new SimpleAmount(113.398093, gramOpt.get());
        ConversionContext listContext = new ConversionContext(ConversionContextType.List, UnitType.US, UnitSubtype.WEIGHT);
        ConvertibleAmount converted = conversionService.convert(amount, listContext);
        assertNotNull(converted);
        assertEquals(ounceId, converted.getUnit().getId());
        assertEquals(3.992, RoundingUtils.roundToThousandths(converted.getQuantity()));

        // 15 Ounce = 425.242847 Gram  => pound destination
        amount = new SimpleAmount(425.242847, gramOpt.get());
        listContext = new ConversionContext(ConversionContextType.List, UnitType.US, UnitSubtype.WEIGHT);
        converted = conversionService.convert(amount, listContext);
        assertNotNull(converted);
        assertEquals(0.936, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(lbId, converted.getUnit().getId());

    }

}
