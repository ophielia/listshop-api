package com.meg.listshop.lmt.data.repository;


import com.meg.listshop.lmt.data.entity.TagInstructionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagInstructionRepository extends JpaRepository<TagInstructionEntity, Long> {


    List<TagInstructionEntity> findByAssignTagId(Long assignTagId);

}