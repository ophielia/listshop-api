package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.data.pojo.ConversionContextType;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ExceedsAllowedScaleException;
import com.meg.listshop.conversion.service.ConversionSpec;
import com.meg.listshop.conversion.service.ConvertibleAmount;

import java.util.List;

public interface ScalingHandler extends ConversionHandler {

    boolean scalerFor(ConversionContextType listOrDish);

    ConvertibleAmount scale(ConvertibleAmount amount) throws ConversionFactorException, ExceedsAllowedScaleException;
}
