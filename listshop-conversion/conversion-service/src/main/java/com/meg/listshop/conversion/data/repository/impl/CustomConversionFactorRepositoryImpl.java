package com.meg.listshop.conversion.data.repository.impl;


import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.repository.CustomConversionFactorRepository;
import com.meg.listshop.conversion.service.ConversionSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomConversionFactorRepositoryImpl implements CustomConversionFactorRepository {

    @Autowired
    EntityManager entityManager;

    public List<ConversionFactor> findFactorsForSourceAndTarget(ConversionSpec source, ConversionSpec target) {
        return new ArrayList<>();
    }


}
