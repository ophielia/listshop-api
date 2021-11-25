package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.data.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {


    List<ItemEntity> findByListId(Long listId);

    @Query(value = "select * from list_item where list_id = :listid and removed_on is null", nativeQuery = true)
    List<ItemEntity> findByListIdAAndRemovedOnIsNull(@Param("listid") Long listId);

    List<ItemEntity> findByRemovedOnBefore(Date removedOnDate);

    @Query(value = "select distinct dish_sources from list_item where list_id = :listid and dish_sources is not null  and removed_on is null", nativeQuery = true)
    List<String> findDishSourcesForList(@Param("listid") Long listid);

    @Query(value = "select distinct list_sources from list_item where list_id = :listid and list_sources is not null and removed_on is null", nativeQuery = true)
    List<String> findListSourcesForList(@Param("listid") Long listid);

    @Query(value = "select i from ItemEntity i where i.listId = :listId and (" +
            " i.addedOn > :changedAfter or" +
            " i.removedOn > :changedAfter )"
    )
    List<ItemEntity> getItemsChangedAfter(@Param("changedAfter") Date changedAfter, @Param("listId") Long shoppingListId);

    @Query(value = "select * from list_item i " +
            "where list_id = :listId and tag_id = :tagId",
            nativeQuery = true)
    ItemEntity getItemByListAndTag(Long listId, Long tagId);
}