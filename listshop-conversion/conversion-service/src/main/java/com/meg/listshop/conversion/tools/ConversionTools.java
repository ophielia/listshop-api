package com.meg.listshop.conversion.tools;

import com.meg.listshop.conversion.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.UnitFlavor;

import java.util.HashSet;
import java.util.Set;

public class ConversionTools {

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
}
