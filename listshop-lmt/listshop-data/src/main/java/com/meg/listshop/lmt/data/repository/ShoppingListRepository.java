/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.data.entity.ShoppingListEntity;
import com.meg.listshop.lmt.data.pojos.ShoppingListDTO;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ShoppingListRepository extends JpaRepository<ShoppingListEntity, Long>, ListMappingCustomRepository {

    Optional<ShoppingListEntity> findByListIdAndUserId(Long listId, Long userId);

    @EntityGraph(value = "list-tag-entity-graph")
    Optional<ShoppingListEntity> getWithItemsByListId(Long listid);

    @EntityGraph(value = "list-tag-entity-graph")
    Optional<ShoppingListEntity> getWithItemsByListIdAndItemsRemovedOnIsNull(Long listid);

    List<ShoppingListEntity> findByUserIdAndIsStarterListTrue(Long userid);

    List<ShoppingListEntity> findByUserIdOrderByLastUpdateDesc(Long userid);

    @Query(value = """
        select l.list_id, l.name, l.created_on, l.last_update, l.user_id, l.is_starter_list, count(distinct t.item_id)  
                from list l
                left outer join list_item t on t.list_id = l.list_id
                where l.user_id = ?1
                and t.crossed_off is null and t.removed_on is null
                group by 1,2,3,4,5,6
                order by l.last_update desc
        """, nativeQuery = true)
    List<ShoppingListDTO> findByUserId(Long userid);

    List<ShoppingListEntity> findByUserIdAndName(Long userid, String name);

    List<ShoppingListEntity> findByUserIdAndNameLike(Long userid, String name);

    @Modifying
    @Query("delete from ShoppingListEntity t where t.listId = ?1")
    void delete(Long entityId);
}
