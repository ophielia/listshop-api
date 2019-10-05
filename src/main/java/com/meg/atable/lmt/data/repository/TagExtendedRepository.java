package com.meg.atable.lmt.data.repository;


import com.meg.atable.lmt.data.entity.TagExtendedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagExtendedRepository extends JpaRepository<TagExtendedEntity, Long>, TagExtendedRepositoryCustom {
}