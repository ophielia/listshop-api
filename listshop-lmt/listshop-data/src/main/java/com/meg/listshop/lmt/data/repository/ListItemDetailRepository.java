package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.data.entity.ListItemDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by margaretmartin on 21/10/2017.
 */
public interface ListItemDetailRepository extends JpaRepository<ListItemDetailEntity, Long>, CustomStatisticRepository {


}
