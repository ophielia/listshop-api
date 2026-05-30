/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service.processors;

import org.springframework.beans.factory.annotation.Value;

public abstract class AbstractConverterProcessor implements ConverterProcessor {

    @Value("${conversionservice.gram.unit.id:1013}")
    protected Long GRAM_UNIT_ID;

    @Value("${conversionservice.single.unit.id:1011}")
    protected Long SINGLE_UNIT_ID;







}
