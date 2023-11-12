package com.meg.listshop.conversion.service;

import com.meg.listshop.conversion.data.entity.Unit;
import com.meg.listshop.conversion.data.pojo.UnitFlavor;
import com.meg.listshop.conversion.data.pojo.UnitType;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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

    void testFromContextAndSource() {
    }

    void testEquals() {

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

}