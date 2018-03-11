package com.meg.atable.data.repository;

import com.meg.atable.api.model.ListLayoutType;
import com.meg.atable.data.entity.CategoryRelationEntity;
import com.meg.atable.data.entity.ListLayoutEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by margaretmartin on 09/11/2017.
 */
public interface ListLayoutRepository extends JpaRepository<ListLayoutEntity, Long> {

    List<ListLayoutEntity> findByLayoutType(ListLayoutType listLayoutType);


    @Query("select t FROM CategoryRelationEntity t  " +
            "where t.child.layoutId = ?1")
    List<CategoryRelationEntity> getSubCategoryMappings(Long listLayoutId);

}
