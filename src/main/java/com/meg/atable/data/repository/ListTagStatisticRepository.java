package com.meg.atable.data.repository;

import com.meg.atable.data.entity.ListTagStatistic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by margaretmartin on 21/10/2017.
 */
public interface ListTagStatisticRepository extends JpaRepository<ListTagStatistic, Long> {
    ListTagStatistic findByUserIdAndTagId(Long userId, Long tagId);

    List<ListTagStatistic> findByUserIdAndTagIdIn(Long userId, List<Long> tagIds);
}
