package com.meg.listshop.lmt.data.repository;


import com.meg.listshop.lmt.data.entity.ListLayoutEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by margaretmartin on 09/11/2017.
 */
public interface ListLayoutRepository extends JpaRepository<ListLayoutEntity, Long> {


    @Query("select e from ListLayoutEntity e where e.userId = ?1 and e.layoutId = ?2")
    ListLayoutEntity getUserListLayout(Long userId, Long listLayoutId);

    @Query("select e from ListLayoutEntity e where e.userId = ?1 and e.isDefault = true")
    ListLayoutEntity getDefaultUserLayout(Long userId);

    @Query("select e from ListLayoutEntity e where e.userId is null and e.isDefault = true")
    ListLayoutEntity getStandardLayout();
}
