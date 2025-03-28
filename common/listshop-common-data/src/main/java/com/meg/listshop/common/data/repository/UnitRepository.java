package com.meg.listshop.common.data.repository;

import com.meg.listshop.common.UnitSubtype;
import com.meg.listshop.common.UnitType;
import com.meg.listshop.common.data.entity.UnitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface UnitRepository extends JpaRepository<UnitEntity, Long>, JpaSpecificationExecutor<UnitEntity> {

    @Query("select d.id FROM UnitEntity d where d.id in (?1) and d.type = 'UNIT'")
    Set<Long> findIntegralUnits(Set<Long> unitIds);

}
