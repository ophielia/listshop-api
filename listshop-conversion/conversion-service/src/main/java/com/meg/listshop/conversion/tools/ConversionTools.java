package com.meg.listshop.conversion.tools;

import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.UnitFlavor;

import java.util.HashSet;
import java.util.Set;

public class ConversionTools {

    private ConversionTools() {
    }

    public static Set<UnitFlavor> flavorsForUnit(UnitEntity unit) {
        Set<UnitFlavor> flavors = new HashSet<>();

        if (unit.isListUnit()) {
            flavors.add(UnitFlavor.ListUnit);
        }
        if (unit.isDishUnit()) {
            flavors.add(UnitFlavor.DishUnit);
        }
        return flavors;
    }

    public static boolean hasFlavor(UnitEntity unit, UnitFlavor flavor) {
        switch (flavor) {
            case ListUnit:
                return unit.isListUnit();
            case DishUnit:
                return unit.isDishUnit();
            default:
                return false;
        }
    }
}
