package com.meg.atable.data.repository;


import com.meg.atable.data.entity.TargetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TargetRepository extends JpaRepository<TargetEntity, Long> {


    TargetEntity findTargetByUserIdAndTargetId(Long userId, Long targetId);

    List<TargetEntity> findTargetsByUserId(Long userId);

    List<TargetEntity> findTargetsByUserIdAndExpiresIsNull(Long userId);

    // MM implement this
    TargetEntity findTargetByProposalId(Long id);
}