package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.data.entity.ListTagStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by margaretmartin on 21/10/2017.
 */
public interface ListTagStatisticRepository extends JpaRepository<ListTagStatistic, Long>, CustomStatisticRepository {
    ListTagStatistic findByUserIdAndTagId(Long userId, Long tagId);

    @Query(value = "select * from list_tag_stats where user_id = :userId order by (added_count + added_to_dish) DESC", nativeQuery = true)
    List<ListTagStatistic> findByUserId(@Param("userId") Long userId);
}
