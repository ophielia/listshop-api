package com.meg.listshop.lmt.service.food;

import java.util.List;

/**
 * Created by margaretmartin on 13/05/2017.
 */
public interface AmountService {


    List<String> pullModifierTokens(String rawModifiers);

    List<String> pullMarkersForModifers(List<String> modifierTokens, Long conversionId);

    List<String> pullUnitSizesForModifiers(List<String> modifierTokens, Long conversionId);
}
