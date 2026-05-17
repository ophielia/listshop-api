/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.conversion.service.ProcessingContext;

public interface TagHandler {

    boolean shouldConvert(ProcessingContext context);

    ConvertibleAmount convertTag(ProcessingContext context);
}
