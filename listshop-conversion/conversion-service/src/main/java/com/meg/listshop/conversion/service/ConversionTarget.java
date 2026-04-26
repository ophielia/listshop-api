/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service;

import com.meg.listshop.common.UnitType;
import com.meg.listshop.conversion.data.pojo.ConversionTargetType;
import com.meg.listshop.conversion.data.pojo.DomainType;

public record ConversionTarget(UnitType domainType,
                               Long unitId,
                               ConversionTargetType conversionContext) {
}
