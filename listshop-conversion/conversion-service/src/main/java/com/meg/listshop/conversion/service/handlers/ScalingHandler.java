package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.data.pojo.ConversionTargetType;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.service.ConversionContext;
import com.meg.listshop.conversion.service.ConvertibleAmount;

public interface ScalingHandler extends ConversionHandler {

    boolean scalerFor(ConversionContext context);

    ConvertibleAmount scale(ConvertibleAmount amount, ConversionContext context) throws ConversionFactorException;

    void setScalerType(ConversionTargetType scalerType);

    void setSkipNoConversionRequiredCheck(boolean b);

    boolean isTagSpecific();

    boolean doesScaleToUnit();

    Integer scalarWeight();
}
