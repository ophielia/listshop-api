package com.meg.listshop.conversion.service;

import com.meg.listshop.conversion.data.entity.Unit;
import com.meg.listshop.conversion.data.pojo.ConversionContext;
import com.meg.listshop.conversion.data.pojo.ConversionContextType;
import com.meg.listshop.conversion.data.pojo.UnitFlavor;
import com.meg.listshop.conversion.data.pojo.UnitType;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ConversionSpecTest {

    @Test
    void testFromExactUnit() {
        Unit unit = makeImperialUnit(1L, false);
        Set<UnitFlavor> expectedFlavors = new HashSet();
        expectedFlavors.add(UnitFlavor.Weight);
        ConversionSpec spec = ConversionSpec.fromExactUnit(unit);
        assertEquals(1L, spec.getUnitId(), "id should be filled and equal 1");
        assertEquals(UnitType.Imperial, spec.getUnitType(), "type should be Imperial");
        assertEquals(expectedFlavors, spec.getFlavors(), "flavors should only Weight and Weight only");

        unit = makeMetricUnit(1L, true);
        expectedFlavors = new HashSet<UnitFlavor>();
        expectedFlavors.add(UnitFlavor.Volume);
        spec = ConversionSpec.fromExactUnit(unit);
        assertEquals(1L, spec.getUnitId(), "id should be filled and equal 1");
        assertEquals(UnitType.Metric, spec.getUnitType(), "type should be Metric");
        assertEquals(expectedFlavors, spec.getFlavors(), "flavors should only Volume and Volume only");
    }

    @Test
    void testOppositeType() {
        Unit unit = makeImperialUnit(1L, false);
        Set<UnitFlavor> expectedFlavors = new HashSet();
        expectedFlavors.add(UnitFlavor.Weight);
        ConversionSpec spec = ConversionSpec.convertedFromUnit(unit);
        assertNull(spec.getUnitId(), "id should be empty");
        assertEquals(UnitType.Metric, spec.getUnitType(), "type should be Metric");
        assertEquals(expectedFlavors, spec.getFlavors(), "flavors should only Weight and Weight only");

    }

    @Test
    void testFromContextAndSource() {
        // make imperial weight, for list context
        Unit sourceUnit = makeImperialUnit(1L, false);
        ConversionContext context = new ConversionContext(ConversionContextType.List, UnitType.Metric);
        Set<UnitFlavor> expectedFlavors = createFlavors(false, true, false, true, false);
        ConversionSpec result = ConversionSpec.fromContextAndSource(context, sourceUnit);

        assertEquals(UnitType.Metric, result.getUnitType(), "should be metric");
        assertEquals(expectedFlavors, result.getFlavors(), "flavors should include weight and list");


    }

    @Test
    void testEquals() {
        // make exact spec - metric, weight flavor
        Unit exactUnit = makeMetricUnit(1L, false);
        ConversionSpec specExact = ConversionSpec.fromExactUnit(exactUnit);
        // make unit - imperial, and then change to metric,
        // creating a spec with metric, weight flavor
        Unit noExactUnit = makeImperialUnit(2L, false);
        ConversionSpec notExactSpec = ConversionSpec.convertedFromUnit(noExactUnit);

        assertNotNull(specExact.getUnitId());
        assertNull(notExactSpec.getUnitId());
        assertEquals(specExact, notExactSpec, "Even though specExact has unit id, they should be considered equal");
    }

    private Unit makeImperialUnit(Long id, boolean isVolume) {
        Unit unit = new Unit();
        unit.setType(UnitType.Imperial);
        unit.setId(id);
        if (isVolume) {
            unit.setVolume(true);
        } else {
            unit.setWeight(true);
        }
        return unit;
    }

    private Unit makeMetricUnit(Long id, boolean isVolume) {
        Unit unit = new Unit();
        unit.setType(UnitType.Metric);
        unit.setId(id);
        if (isVolume) {
            unit.setVolume(true);
        } else {
            unit.setWeight(true);
        }
        return unit;
    }

    private Set<UnitFlavor> createFlavors(boolean isVolume, boolean isWeight,
                                          boolean isLiquid, boolean isList,
                                          boolean isDish
    ) {
        Set<UnitFlavor> expectedFlavors = new HashSet();
        if (isVolume) {
            expectedFlavors.add(UnitFlavor.Volume);
        }
        if (isWeight) {
            expectedFlavors.add(UnitFlavor.Weight);
        }
        if (isLiquid) {
            expectedFlavors.add(UnitFlavor.Liquid);
        }
        if (isList) {
            expectedFlavors.add(UnitFlavor.ListUnit);
        }
        if (isDish) {
            expectedFlavors.add(UnitFlavor.DishUnit);
        }
        return expectedFlavors;

    }

    private void addFlavorsToUnit(Unit unit, boolean isVolume, boolean isWeight,
                                  boolean isLiquid, boolean isList,
                                  boolean isDish
    ) {
        unit.setVolume(isVolume);
        unit.setWeight(isWeight);
        unit.setLiquid(isLiquid);
        unit.setListUnit(isList);
        unit.setDishUnit(isDish);
    }


}