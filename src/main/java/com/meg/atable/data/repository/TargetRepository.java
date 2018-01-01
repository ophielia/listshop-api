package com.meg.atable.data.repository;


import com.meg.atable.api.model.TagType;
import com.meg.atable.data.entity.DishEntity;
import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.data.entity.TargetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.Tuple;
import java.util.List;

public interface TargetRepository extends JpaRepository<TargetEntity, Long> {


    TargetEntity findTargetByUserIdAndTargetId(Long userId, Long targetId);

    List<TargetEntity> findTargetsByUserId(Long userId);
}