package com.meg.listshop.conversion.data.repository;

import com.meg.listshop.conversion.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.UnitSubtype;
import com.meg.listshop.conversion.data.pojo.UnitType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UnitRepository extends JpaRepository<UnitEntity, Long>, JpaSpecificationExecutor<UnitEntity> {

    List<UnitEntity> findByType(UnitType type);

    List<UnitEntity> findUnitEntitiesByTypeAndSubtypeIsNot(UnitType unitType, UnitSubtype subtype);

    @Query("select d FROM UnitEntity d where d.type = ?1 and d.subtype <> ?2 and d.isTagSpecific = false")
    List<UnitEntity> findGenericWeightHybrids(UnitType unitType, UnitSubtype subtype);

}
