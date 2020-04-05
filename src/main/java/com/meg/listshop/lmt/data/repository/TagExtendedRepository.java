package com.meg.listshop.lmt.data.repository;


import com.meg.listshop.lmt.data.entity.TagExtendedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagExtendedRepository extends JpaRepository<TagExtendedEntity, Long>, TagExtendedRepositoryCustom {
}