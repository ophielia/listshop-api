package com.meg.listshop.conversion.data.repository;


import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.service.ConversionSpec;

import java.util.List;

public interface CustomConversionFactorRepository {

    List<ConversionFactor> findFactorsForSourceAndTarget(ConversionSpec source, ConversionSpec target);

}
