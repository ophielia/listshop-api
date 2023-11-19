package com.meg.listshop.conversion.data.repository;

import com.meg.listshop.conversion.data.entity.ConversionFactorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversionFactorRepository extends JpaRepository<ConversionFactorEntity, Long> {
}
