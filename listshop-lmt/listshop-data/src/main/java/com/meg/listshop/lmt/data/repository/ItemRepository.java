/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.lmt.data.entity.ListItemEntity;
import com.meg.listshop.lmt.data.pojos.ItemToCategoryDTO;
import com.meg.listshop.lmt.data.pojos.SourceDTO;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ItemRepository extends JpaRepository<ListItemEntity, Long> {


    List<ListItemEntity> findByListId(Long listId);

    @Query(value = "select * from list_item where list_id = :listid and removed_on is null", nativeQuery = true)
    List<ListItemEntity> findByListIdAAndRemovedOnIsNull(@Param("listid") Long listId);

    List<ListItemEntity> findByRemovedOnBefore(Date removedOnDate);

    @EntityGraph(value = "filledItem")
    List<ListItemEntity> findFilledObjectsByListId(Long listId);

    @Query(value = """
                    select distinct d.linked_dish_id, '', 'DISH' 
                        from list_item_details d 
                            join list_item i on d.item_id = i.item_id
                            where i.list_id = ?1 and d.linked_dish_id is not null
            """, nativeQuery = true)
    List<SourceDTO> findDishSourcesForList(Long listid);

    @Query(value = """
                    select distinct d.linked_list_id, '', 'LIST' 
                        from list_item_details d 
                            join list_item i on d.item_id = i.item_id
                            where i.list_id = ?1 and d.linked_list_id is not null
            """, nativeQuery = true)
    List<SourceDTO> findListSourcesForList(Long listid);

    @Query(value = """
            select distinct linked_dish_id from list_item_details lid join list_item i on i.item_id = lid.item_id
                                         where list_id = :listid and linked_dish_id is not null 
                                           and removed_on is null
            """, nativeQuery = true)
    List<Long> findDishSourcesForListFromItems(@Param("listid") Long listid);


    @Query(value = """
            select distinct linked_list_id from list_item_details lid join list_item i on i.item_id = lid.item_id
                                         where list_id = :listid and linked_list_id is not null 
                                           and removed_on is null
            """, nativeQuery = true)
    List<Long> findListSourcesForListForDetails(@Param("listid") Long listid);

    @Query(value = "select i from ListItemEntity i where i.listId = :listId and (" +
            " i.addedOn > :changedAfter or" +
            " i.removedOn > :changedAfter )"
    )
    List<ListItemEntity> getItemsChangedAfter(@Param("changedAfter") Date changedAfter, @Param("listId") Long shoppingListId);

    @Query(value = "select i from ListItemEntity i where i.listId = :listId and i.tag.tagId = :tagId")
    ListItemEntity getItemByListAndTag(@Param("listId") Long listId, @Param("tagId") Long tagId);

    @Query(value = "select i from ListItemEntity i where i.listId = :listId and i.tag.tagId = :tagId")
    @EntityGraph(value = "filledItem")
    ListItemEntity getFilledItemByListAndTag(@Param("listId") Long listId, @Param("tagId") Long tagId);

    @Query(value = """
            select distinct tag_id from list_item i join list_item_details id using (item_id)
            where i.list_id = :listId and linked_dish_id = :dishId
            """,
            nativeQuery = true)
    List<Long> findTagIdsInListByDishId(@Param("dishId") Long dishId, @Param("listId") Long listId);

    @Query(value = """
            select distinct tag_id from list_item i join list_item_details id using (item_id)
            where i.list_id = :listId and linked_list_id = :fromListId
            """,
            nativeQuery = true)
    List<Long> findTagIdsInListByListId(@Param("fromListId") Long fromListId, @Param("listId") Long listId);

    @Query(value = """
                    select distinct t.tag_id, trim(lower(lc.name)) 
            from list_category lc
            join category_tags ct on ct.category_id = lc.category_id
            join tag t on t.tag_id = ct.tag_id
            join list_item it on it.tag_id = t.tag_id
            where it.list_id = ?2
            and lc.layout_id = ?1
            """, nativeQuery = true)
    List<ItemToCategoryDTO> getUserItemToCategoryMapping(Long userLayoutId, Long listId);

    @Query(value = """
            select distinct t.tag_id, trim(lower(lc.name))
            from list_category lc
                join list_layout l on l.layout_id = lc.layout_id
            join category_tags ct on ct.category_id = lc.category_id
            join tag t on t.tag_id = ct.tag_id
            join list_item it on it.tag_id = t.tag_id
            where it.list_id = ?1
            and l.user_id is null and l.is_default = true;
            """, nativeQuery = true
    )
    List<ItemToCategoryDTO> getStandardItemToCategoryMapping(Long listId);

    @Query(value = """
            select u.*
            from list_item li
            join units u on u.unit_id = li.unit_id
            where li.list_id = ?1
            """, nativeQuery = true)
    List<UnitEntity> findUnitsForItems(Long listId);

    @Query(value = """
            select u.*
            from list_item_details li
                     join list_item i on i.item_id = li.item_id
                     join units u on u.unit_id = li.unit_id
                     where i.list_id = ?1
            """, nativeQuery = true)
    List<UnitEntity> findUnitsForItemDetails(Long listId);
}
