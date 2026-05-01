/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.common.UnitType;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.service.*;

public interface DomainConversionHandler  {

    boolean appliesTo(DeltaSpec fromSpec, DeltaSpec toSpec);

    ConvertibleAmount convert(ProcessingContext context);
}
