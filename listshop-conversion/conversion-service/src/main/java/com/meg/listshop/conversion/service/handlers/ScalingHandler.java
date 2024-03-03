package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.data.pojo.ConversionContextType;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.service.ConvertibleAmount;

public interface ScalingHandler extends ConversionHandler {

    boolean scalerFor(ConversionContextType listOrDish);

    ConvertibleAmount scale(ConvertibleAmount amount) throws ConversionFactorException;

    void setScalerType(ConversionContextType scalerType);

    void setSkipNoConversionRequiredCheck(boolean b);
}
