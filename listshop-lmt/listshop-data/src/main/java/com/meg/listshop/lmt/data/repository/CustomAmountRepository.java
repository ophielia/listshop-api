/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.data.pojos.SuggestionDTO;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface CustomAmountRepository {

    List<SuggestionDTO> getNonUnitSuggestions();

    List<SuggestionDTO> getUnitSuggestionsByIds(Set<Long> unitIds);

    Set<Long> getUnitIdsForCriteria(UnitSearchCriteria unitSearchCriteria);

    Set<Long> getUnitIdsNonTagSpecific();
}
