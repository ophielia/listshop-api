package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.data.entity.ListItemDetailEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by margaretmartin on 21/10/2017.
 */
public interface ListItemDetailRepository extends JpaRepository<ListItemDetailEntity, Long>, CustomStatisticRepository {

    @EntityGraph("detail-item-tag-entity-graph")
    @Query("select d from ListItemDetailEntity d where d.item.listId = ?1")
    List<ListItemDetailEntity> findDetailsByListId(Long listId);
}
