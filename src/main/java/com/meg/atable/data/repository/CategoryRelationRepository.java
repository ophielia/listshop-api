package com.meg.atable.data.repository;

import com.meg.atable.data.entity.CategoryRelationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by margaretmartin on 09/11/2017.
 */
public interface CategoryRelationRepository extends JpaRepository<CategoryRelationEntity, Long> {

    @Query("select t FROM CategoryRelationEntity t  " +
            "where t.child.categoryId = ?1")
    List<CategoryRelationEntity> findCategoryRelationsByChildId(Long categoryId);
}
