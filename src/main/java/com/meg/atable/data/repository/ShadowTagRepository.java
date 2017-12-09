package com.meg.atable.data.repository;


import com.meg.atable.data.entity.ShadowTags;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShadowTagRepository extends JpaRepository<ShadowTags, Long> {

    List<ShadowTags> findShadowTagsByDishId(Long dishId);
}