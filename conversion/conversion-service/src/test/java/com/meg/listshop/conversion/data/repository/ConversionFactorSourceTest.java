package com.meg.listshop.conversion.data.repository;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.Unit;
import com.meg.listshop.conversion.data.pojo.UnitType;
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
        Unit unit1 = ConversionTestTools.makeMetricUnit(1L, false);
        // load metric unit, weight, id 2
        Unit unit2 = ConversionTestTools.makeImperialUnit(2L, false);
        // load metric unit, weight, id 3
        Unit unit3 = ConversionTestTools.makeMetricUnit(3L, false);
        // load imperial unit, volume, id 3
        Unit unit4 = ConversionTestTools.makeImperialUnit(4L, true);
        // load metric unit, volume, id 5
        Unit unit5 = ConversionTestTools.makeMetricUnit(5L, true);

        // create factor source
        this.sourceToTest = new ConversionFactorSourceBuilder()
                .withFactor(unit1, unit2, 0.5)
                .withFactor(unit3, unit4, 0.25)
                .withFactor(unit5, unit2, 0.9)
                .build();

    }

    @Test
    void testGetFactors() {
        List<ConversionFactor> resultList = sourceToTest.getFactors(UnitType.Metric);
        assertEquals(3, resultList.size(), "three factors should be returned");
        assertTrue(resultList.stream()
                .mapToDouble(ConversionFactor::getFactor)
                .sum() == 1.65, () -> "Sum should be 1.65");

        resultList = sourceToTest.getFactors(UnitType.Imperial);
        assertEquals(3, resultList.size(), "three factors should be returned");
        double sum = resultList.stream()
                .mapToDouble(ConversionFactor::getFactor)
                .sum();
        assertEquals(7.11D, ConversionTestTools.roundToHundredths(sum), () -> "Sum should be 7.11");
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
