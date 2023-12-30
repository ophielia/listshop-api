package com.meg.listshop.conversion.service;

import com.meg.listshop.conversion.data.entity.UnitEntity;
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

    @Test
    void testOppositeType() {
        UnitEntity unit = makeUSUnit(1L, false);
        Set<UnitFlavor> expectedFlavors = new HashSet<>();
        expectedFlavors.add(UnitFlavor.Weight);
        ConversionSpec spec = ConversionSpec.convertedFromUnit(unit);
        assertNull(spec.getUnitId(), "id should be empty");
        assertEquals(UnitType.METRIC, spec.getUnitType(), "type should be Metric");
        assertEquals(0, spec.getFlavors().size(), "no flavors - weight is in subtype");

    }

    @Test
    void testFromContextAndSource() {
        // make imperial weight, for list context
        UnitEntity sourceUnit = makeUSUnit(1L, false);
        ConversionContext context = new ConversionContext(ConversionContextType.List, UnitType.METRIC, UnitSubtype.WEIGHT);
        Set<UnitFlavor> expectedFlavors = createFlavors(false, false, false, true, false);
        ConversionSpec result = ConversionSpec.fromContext(context, sourceUnit);

        assertEquals(UnitType.METRIC, result.getUnitType(), "should be metric");
        assertEquals(expectedFlavors, result.getFlavors(), "flavors should include list");
    }

    @Test
    void testFromContextAndSourceHybrid() {
        // make imperial weight, for list context
        UnitEntity sourceUnit = makeHybridUnit(1L);
        ConversionContext context = new ConversionContext(ConversionContextType.Dish, UnitType.METRIC, UnitSubtype.WEIGHT);
        Set<UnitFlavor> expectedFlavors = createFlavors(false, false, false, false, true);

        // call with allowHybrid = true
        ConversionSpec result = ConversionSpec.fromContext(context, sourceUnit);

        // conversion context dish and hybrid unit should result
        // in a ConversionSpec with a type of hybrid, and a subtype of none.
        assertEquals(UnitType.HYBRID, result.getUnitType(), "should be hybrid");
        assertEquals(expectedFlavors, result.getFlavors(), "flavors should include dish");

        // call with allowHybrid = false
         result = ConversionSpec.retryFromContext(context, sourceUnit);

        // we're supressing the hybrid so the result should be metric
        assertEquals(UnitType.METRIC, result.getUnitType(), "should be metric");
        assertEquals(expectedFlavors, result.getFlavors(), "flavors should include dish");

    }

    @Test
    void testEquals() {
        // make exact spec - metric, weight flavor
        UnitEntity exactUnit = makeMetricUnit(1L, false);
        ConversionSpec specExact = ConversionSpec.fromExactUnit(exactUnit);
        // make unit - imperial, and then change to metric,
        // creating a spec with metric, weight flavor
        UnitEntity noExactUnit = makeUSUnit(2L, false);
        ConversionSpec notExactSpec = ConversionSpec.convertedFromUnit(noExactUnit);

        assertNotNull(specExact.getUnitId());
        assertNull(notExactSpec.getUnitId());
        assertEquals(specExact, notExactSpec, "Even though specExact has unit id, they should be considered equal");
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
        unit.setSubtype(UnitSubtype.NONE);
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

    private Set<UnitFlavor> createFlavors(boolean isVolume, boolean isWeight,
                                          boolean isLiquid, boolean isList,
                                          boolean isDish
    ) {
        Set<UnitFlavor> expectedFlavors = new HashSet<>();
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

}