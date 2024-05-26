package com.meg.listshop.conversion.service;

import com.meg.listshop.common.UnitSubtype;
import com.meg.listshop.common.UnitType;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.*;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ConversionSpecTest {

    @Test
    void testFromExactUnit() {
        UnitEntity unit = makeUSUnit(1L, false);
        Set<UnitFlavor> expectedFlavors = new HashSet<>();
        expectedFlavors.add(UnitFlavor.Weight);
        ConversionSpec spec = ConversionSpec.fromExactUnit(unit);
        assertEquals(1L, spec.getUnitId(), "id should be filled and equal 1");
        assertEquals(UnitType.US, spec.getUnitType(), "type should be Imperial");
        assertEquals(0, spec.getFlavors().size(), "no flavors here - weight is in subtype");

        unit = makeMetricUnit(1L, true);
        expectedFlavors = new HashSet<>();
        expectedFlavors.add(UnitFlavor.Volume);
        spec = ConversionSpec.fromExactUnit(unit);
        assertEquals(1L, spec.getUnitId(), "id should be filled and equal 1");
        assertEquals(UnitType.METRIC, spec.getUnitType(), "type should be Metric");
        assertEquals(0, spec.getFlavors().size(), "no flavors here - weight is in subtype");
    }



    private UnitEntity makeUSUnit(Long id, boolean isVolume) {
        UnitEntity unit = new UnitEntity();
        unit.setType(UnitType.US);
        unit.setId(id);
        if (isVolume) {
            unit.setSubtype(UnitSubtype.VOLUME);
        } else {
            unit.setSubtype(UnitSubtype.WEIGHT);
        }
        return unit;
    }

    private UnitEntity makeHybridUnit(Long id) {
        UnitEntity unit = new UnitEntity();
        unit.setType(UnitType.HYBRID);
        unit.setSubtype(UnitSubtype.LIQUID);
        unit.setId(id);
        return unit;
    }

    private UnitEntity makeMetricUnit(Long id, boolean isVolume) {
        UnitEntity unit = new UnitEntity();
        unit.setType(UnitType.METRIC);
        unit.setId(id);
        if (isVolume) {
            unit.setSubtype(UnitSubtype.VOLUME);
        } else {
            unit.setSubtype(UnitSubtype.WEIGHT);
        }
        return unit;
    }


}