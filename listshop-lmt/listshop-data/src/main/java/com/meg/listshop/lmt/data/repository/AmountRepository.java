package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.data.entity.ModifierMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AmountRepository extends JpaRepository<ModifierMappingEntity, Long> {


    @Query(value = "select distinct mm.mapped_modifier from food_conversions fc join modifier_mappings mm on mm.mapped_modifier = fc.marker " +
            "where modifier_type = 'Marker' and conversion_id = :conversionId and mm.modifier in (:markerList)", nativeQuery = true)
    List<String> mapMarkersForConversionId(@Param("conversionId") Long conversionId, @Param("markerList") List<String> markerList);

    @Query(value = "select distinct mm.mapped_modifier from food_conversions fc join modifier_mappings mm on mm.mapped_modifier = fc.marker " +
            "where modifier_type = 'UnitSize' and conversion_id = :conversionId and mm.modifier in (:modifierList)", nativeQuery = true)
    List<String> mapUnitSizesForConversionId(@Param("conversionId") Long conversionId, @Param("modifierList") List<String> modifierList);


}