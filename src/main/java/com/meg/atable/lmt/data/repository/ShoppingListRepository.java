package com.meg.atable.lmt.data.repository;

import com.meg.atable.lmt.data.entity.ShoppingListEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShoppingListRepository extends JpaRepository<ShoppingListEntity, Long> {

    List<ShoppingListEntity> findByUserId(Long userid);


    List<ShoppingListEntity> findByListIdAndUserId(Long listId, Long userId);

    @EntityGraph(value="list-entity-graph")
    Optional<ShoppingListEntity> getWithItemsByListId(Long listid);

    @EntityGraph(value="list-entity-graph")
    Optional<ShoppingListEntity> getWithItemsByListIdAndItemsRemovedOnIsNull(Long listid);

    List<ShoppingListEntity> findByUserIdAndIsStarterListTrue(Long userid);

    List<ShoppingListEntity> findByUserIdOrderByCreatedOnDesc(Long userid);

    List<ShoppingListEntity> findByUserIdAndName(Long userid, String name);

    List<ShoppingListEntity> findByUserIdAndNameLike(Long userid, String name);
}