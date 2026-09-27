/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.data.repository;


import com.meg.listshop.conversion.data.entity.ConversionFactor;

import java.util.List;

public interface CustomConversionFactorRepository {


    List<ConversionFactor> findAllFactors(FactorCriteria criteria);

    List<ConversionFactor> findFactors(FactorCriteria criteria);
}
