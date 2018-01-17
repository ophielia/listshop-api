package com.meg.atable.data.repository;


import com.meg.atable.data.entity.TargetProposalDishEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TargetProposalDishRepository extends JpaRepository<TargetProposalDishEntity, Long> {

    @Modifying
    @Query(value = "delete from TargetProposalDishEntity d " +
            "where d.targetProposalSlot.slotId in (:listIds)")
    void deleteDishesForSlots(@Param("listIds") List<Long> proposalId);
}