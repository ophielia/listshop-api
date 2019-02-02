package com.meg.atable.lmt.data.repository;


import com.meg.atable.lmt.data.entity.ShadowTags;
import com.meg.atable.lmt.data.entity.TagInstructionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagInstructionRepository extends JpaRepository<TagInstructionEntity, Long> {


    List<TagInstructionEntity> findByAssignTagId(Long assignTagId);

}