/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service.factors;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.service.ProcessingContext;

import java.util.List;

public interface FactorProvider {

    List<ConversionFactor> findFactors(ProcessingContext context);
}
