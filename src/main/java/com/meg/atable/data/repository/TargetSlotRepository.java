package com.meg.atable.data.repository;


import com.meg.atable.data.entity.TargetEntity;
import com.meg.atable.data.entity.TargetSlotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TargetSlotRepository extends JpaRepository<TargetSlotEntity, Long> {


}