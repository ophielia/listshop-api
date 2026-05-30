/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service.scaling;

import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.conversion.service.ProcessingContext;

public interface ScaleHandler {

    boolean shouldScale(ProcessingContext context);

    ConvertibleAmount scale(ProcessingContext context);
}
