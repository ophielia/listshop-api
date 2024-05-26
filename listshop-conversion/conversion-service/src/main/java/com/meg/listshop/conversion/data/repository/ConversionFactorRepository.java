package com.meg.listshop.conversion.data.repository;


import com.meg.listshop.conversion.data.entity.ConversionFactorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ConversionFactorRepository extends JpaRepository<ConversionFactorEntity, Long>, JpaSpecificationExecutor<ConversionFactorEntity> {

    List<ConversionFactorEntity> findAllByConversionIdIs(Long tagId);

    @Query(value = "select f.* from factors f join domain_unit fdu on fdu.unit_id = f.from_unit " +
            "join domain_unit tdu on tdu.unit_id = f.to_unit join units fu on fu.unit_id = f.from_unit join units tu on " +
            "tu.unit_id = f.to_unit where fdu.domain_type = ?1 and tdu.domain_type = ?2 and tu.type <> 'HYBRID' " +
            "and fu.type <> 'HYBRID' ", nativeQuery = true)
    List<ConversionFactorEntity> findAllByDomains(String fromDomain, String toDomain);
}
