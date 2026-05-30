/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service.domain;

import com.meg.listshop.conversion.service.*;

public interface DomainConversionHandler  {

    boolean appliesTo(DeltaSpec fromSpec, DeltaSpec toSpec);

    ConvertibleAmount convert(ProcessingContext context);
}
