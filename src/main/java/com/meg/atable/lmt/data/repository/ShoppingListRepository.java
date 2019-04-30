package com.meg.atable.lmt.data.repository;

import com.meg.atable.lmt.api.model.ListType;
import com.meg.atable.lmt.data.entity.ShoppingListEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ShoppingListRepository extends JpaRepository<ShoppingListEntity, Long> {

    List<ShoppingListEntity> findByUserId(Long userid);

    @EntityGraph(value="list-entity-graph")
    Optional<ShoppingListEntity> getWithItemsByListId(Long listid);

    @EntityGraph(value="list-entity-graph")
    ShoppingListEntity findWithItemsByUserIdAndListType(Long userid, ListType listType);

    @Modifying
    @Query(value="delete from list_item where list_id = :listid", nativeQuery=true)
    void bulkDeleteItemsFromList( @Param("listid") Long listid);


}