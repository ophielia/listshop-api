package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.data.entity.ListItemEntity;
import com.meg.listshop.lmt.data.entity.ListTagStatistic;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Created by margaretmartin on 21/10/2017.
 */
public interface ListItemRepository extends JpaRepository<ListItemEntity, Long>, CustomStatisticRepository {


    @EntityGraph(attributePaths = {"details"})
    Optional<ListItemEntity> findWithDetailsById(Long id);
}
