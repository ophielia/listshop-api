package com.meg.listshop.conversion.data.repository;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.UnitEntity;
import com.meg.listshop.conversion.service.factors.ConversionFactorSource;
import com.meg.listshop.conversion.service.tools.ConversionFactorSourceBuilder;
import com.meg.listshop.conversion.tools.ConversionTestTools;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ConversionFactorSourceTest {

    ConversionFactorSource sourceToTest;

    @BeforeEach
    void setUp() {
        // load imperial unit, weight, id 1
        UnitEntity unit1 = ConversionTestTools.makeMetricUnit(1L, false);
        // load metric unit, weight, id 2
        UnitEntity unit2 = ConversionTestTools.makeImperialUnit(2L, false);
        // load metric unit, weight, id 3
        UnitEntity unit3 = ConversionTestTools.makeMetricUnit(3L, false);
        // load imperial unit, volume, id 3
        UnitEntity unit4 = ConversionTestTools.makeImperialUnit(4L, true);
        // load metric unit, volume, id 5
        UnitEntity unit5 = ConversionTestTools.makeMetricUnit(5L, true);

        // create factor source
        this.sourceToTest = new ConversionFactorSourceBuilder()
                .withFactor(unit1, unit2, 0.5)
                .withFactor(unit3, unit4, 0.25)
                .withFactor(unit5, unit2, 0.9)
                .withFactor(unit1, unit4, 0.9)
                .build();

    }

    @Test
    void testGetFactors() {
        List<ConversionFactor> resultList = sourceToTest.getFactors(1L);
        assertEquals(2, resultList.size(), "two factors should be returned");
        assertTrue(resultList.stream()
                .mapToDouble(ConversionFactor::getFactor)
                .sum() == 1.4, () -> "Sum should be 1.4");

        resultList = sourceToTest.getFactors(4L);
        assertEquals(2, resultList.size(), "two factors should be returned");
        double sum = resultList.stream()
                .mapToDouble(ConversionFactor::getFactor)
                .sum();
        assertEquals(5.11D, ConversionTestTools.roundToHundredths(sum), () -> "Sum should be 5.11");
    }

    @Test
    void testGetFactor() {
        ConversionFactor result = sourceToTest.getFactor(1L, 2L);
        assertNotNull(result, "factor should be found");
        assertEquals(0.5, result.getFactor(), "factor should be 0.5");

        result = sourceToTest.getFactor(4L, 3L);
        assertNotNull(result, "factor should be found");
        assertEquals(4, result.getFactor(), "factor should be 4");
    }
}
