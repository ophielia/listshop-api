package com.meg.atable.data.repository;


import com.meg.atable.data.entity.ShadowTags;
import com.meg.atable.data.entity.TextInstructionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TextInstructionRepository extends JpaRepository<TextInstructionEntity, Long> {

}