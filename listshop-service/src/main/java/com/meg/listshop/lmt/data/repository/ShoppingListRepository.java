package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.data.entity.ShoppingListEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ShoppingListRepository extends JpaRepository<ShoppingListEntity, Long>, ListMappingCustomRepository {

    List<ShoppingListEntity> findByListIdAndUserId(Long listId, Long userId);

    @EntityGraph(value = "list-tag-entity-graph")
    Optional<ShoppingListEntity> getWithItemsByListId(Long listid);

    @EntityGraph(value = "list-tag-entity-graph")
    Optional<ShoppingListEntity> getWithItemsByListIdAndItemsRemovedOnIsNull(Long listid);

    List<ShoppingListEntity> findByUserIdAndIsStarterListTrue(Long userid);

    List<ShoppingListEntity> findByUserIdOrderByLastUpdateDesc(Long userid);

    List<ShoppingListEntity> findByUserIdAndName(Long userid, String name);

    List<ShoppingListEntity> findByUserIdAndNameLike(Long userid, String name);

    @Modifying
    @Query("delete from ShoppingListEntity t where t.listId = ?1")
    void delete(Long entityId);
}