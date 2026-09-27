/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service.processors;

import com.meg.listshop.conversion.service.ProcessingContext;

public interface ConverterProcessor {

    void process(ProcessingContext context);

    boolean appliesTo(ProcessingContext context);

}
