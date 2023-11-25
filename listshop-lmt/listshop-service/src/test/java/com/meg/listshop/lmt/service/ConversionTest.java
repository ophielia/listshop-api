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
import com.meg.listshop.conversion.data.pojo.UnitType;
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

    private static final Long ounceId = 1009L;
    private static final Long mgId = 1016L;
    private static final Long gId = 1013L;
    private static final Long kgId = 1014L;
    private static final Long lbId = 1008L;

    private static final Long gallonId = 1005L;
    private static final Long literId = 1003L;
    private static final Long quartId = 1010L;
    private static final Long cupsId = 1000L;
    private static final Long centileterId = 1015L;
    private static final Long milliliterId = 1004L;
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

        Optional<UnitEntity> gUnitOpt = unitRepository.findById(gId);
        Optional<UnitEntity> ozUnitOpt = unitRepository.findById(ounceId);
        amount = new SimpleAmount(500.0, gUnitOpt.get());
        converted = conversionService.convert(amount, ozUnitOpt.get());
        assertNotNull(converted);
        assertEquals(17.637, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(ounceId, converted.getUnit().getId());
    }

    @Test
    public void testSingleHandlerVolumeExact() throws ConversionPathException, ConversionFactorException {
        Optional<UnitEntity> gallonsOpt = unitRepository.findById(gallonId);
        Optional<UnitEntity> litersOpt = unitRepository.findById(literId);
        Optional<UnitEntity> milliliterOpt = unitRepository.findById(milliliterId);
        Optional<UnitEntity> cupsOpt = unitRepository.findById(cupsId);
        Optional<UnitEntity> quartOpt = unitRepository.findById(quartId);
        Optional<UnitEntity> centiliterOpt = unitRepository.findById(centileterId);

        ConvertibleAmount amount = new SimpleAmount(20.0, litersOpt.get());
        ConvertibleAmount converted = conversionService.convert(amount, gallonsOpt.get());
        assertNotNull(converted);
        assertEquals(4.399, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gallonId, converted.getUnit().getId());

        amount = new SimpleAmount(50, centiliterOpt.get());
        converted = conversionService.convert(amount, quartOpt.get());
        assertNotNull(converted);
        assertEquals(0.440, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(quartId, converted.getUnit().getId());

        amount = new SimpleAmount(3.0, cupsOpt.get());
        converted = conversionService.convert(amount, milliliterOpt.get());
        assertNotNull(converted);
        assertEquals(709.765, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(milliliterId, converted.getUnit().getId());
    }

    @Test
    public void testSingleHandlerVolumeType() throws ConversionPathException, ConversionFactorException {
        Optional<UnitEntity> gallonsOpt = unitRepository.findById(gallonId);
        Optional<UnitEntity> litersOpt = unitRepository.findById(literId);
        Optional<UnitEntity> milliliterOpt = unitRepository.findById(milliliterId);
        Optional<UnitEntity> cupsOpt = unitRepository.findById(cupsId);
        Optional<UnitEntity> quartOpt = unitRepository.findById(quartId);
        Optional<UnitEntity> centiliterOpt = unitRepository.findById(centileterId);

        ConvertibleAmount amount = new SimpleAmount(20.0, litersOpt.get());
        ConvertibleAmount converted = conversionService.convert(amount, UnitType.Imperial);
        assertNotNull(converted);
        assertEquals(4.399, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(gallonId, converted.getUnit().getId());

        amount = new SimpleAmount(50, centiliterOpt.get());
        converted = conversionService.convert(amount, UnitType.Imperial);
        assertNotNull(converted);
        assertEquals(0.440, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(quartId, converted.getUnit().getId());

        amount = new SimpleAmount(3.0, cupsOpt.get());
        converted = conversionService.convert(amount, UnitType.Metric);
        assertNotNull(converted);
        assertEquals(0.71, RoundingUtils.roundToThousandths(converted.getQuantity()));
        assertEquals(literId, converted.getUnit().getId());
    }

    @Test
    public void homeForTroubledTests() throws ConversionPathException, ConversionFactorException {
        Optional<UnitEntity> gallonsOpt = unitRepository.findById(gallonId);
        Optional<UnitEntity> litersOpt = unitRepository.findById(literId);
        Optional<UnitEntity> milliliterOpt = unitRepository.findById(milliliterId);
        Optional<UnitEntity> cupsOpt = unitRepository.findById(cupsId);
        Optional<UnitEntity> quartOpt = unitRepository.findById(quartId);
        Optional<UnitEntity> centiliterOpt = unitRepository.findById(centileterId);


        assertEquals(1, 1);
    }

}
