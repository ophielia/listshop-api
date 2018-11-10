package com.meg.atable.lmt.data.repository;


import com.meg.atable.lmt.data.entity.ShadowTags;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShadowTagRepository extends JpaRepository<ShadowTags, Long> {

    List<ShadowTags> findShadowTagsByDishId(Long dishId);
}