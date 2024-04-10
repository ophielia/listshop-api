package com.meg.listshop.conversion.data.repository;


import com.meg.listshop.conversion.data.entity.ConversionFactorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ConversionFactorRepository extends JpaRepository<ConversionFactorEntity, Long>, JpaSpecificationExecutor<ConversionFactorEntity> {

    List<ConversionFactorEntity> findAllByConversionIdIs(Long tagId);
}
