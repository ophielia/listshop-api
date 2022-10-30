package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.data.entity.CategoryRelationEntity;
import com.meg.listshop.lmt.data.entity.ListLayoutEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by margaretmartin on 09/11/2017.
 */
public interface ListLayoutRepository extends JpaRepository<ListLayoutEntity, Long> {


    @Query("select t FROM CategoryRelationEntity t  " +
            "where t.child.layoutId = ?1")
    List<CategoryRelationEntity> getSubCategoryMappings(Long listLayoutId);

}
