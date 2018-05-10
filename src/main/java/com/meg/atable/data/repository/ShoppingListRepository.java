package com.meg.atable.data.repository;

import com.meg.atable.api.model.ListType;
import com.meg.atable.data.entity.ShoppingListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShoppingListRepository extends JpaRepository<ShoppingListEntity, Long> {

    List<ShoppingListEntity> findByUserId(Long userid);

    ShoppingListEntity findByUserIdAndListType(Long userid, ListType listType);

    @Modifying
    @Query(value="delete from list_item where list_id = :listid", nativeQuery=true)
    void bulkDeleteItemsFromList( @Param("listid") Long listid);


}