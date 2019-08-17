package com.meg.atable.lmt.data.repository;

import com.meg.atable.lmt.data.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {


    List<ItemEntity> findByListId(Long listId);

    List<ItemEntity> findByRemovedOnBefore(Date removedOnDate);

    @Query(value="select * from list_item where list_id = :listid and tag_id = :tagid", nativeQuery=true)
    List<ItemEntity> getItemsForTag(@Param("listid") Long listid, @Param("tagid") Long tagid);

    @Query(value="select distinct dish_sources from list_item where list_id = :listid and dish_sources is not null", nativeQuery=true)
    List<String> findDishSourcesForList(@Param("listid") Long listid);

    @Query(value="select distinct list_sources from list_item where list_id = :listid and list_sources is not null", nativeQuery=true)
    List<String> findListSourcesForList(@Param("listid") Long listid);

    @Query(value="select * from list_item i join dish_tags t on t.tag_id = i.tag_id where dish_id = :dishId and list_id = :listId", nativeQuery=true)
    List<ItemEntity> getItemsForDish(@Param("listId") Long listId,@Param("dishId") Long dishId);

    /*    @Query(value = "select * from list_item where (updated_on > :changedAfter" +
                " or removed_on > :changedAfter " +
                " or crossed_off > :changedAfter" +
                " or added_on > :changedAfter" +
                " ) and list_id = :listId", nativeQuery = true)*/
//@Query(value = "select * from list_item where list_id = :listId and added_on > :changedAfter", nativeQuery = true)
    @Query(value = "select i from ItemEntity i where i.listId = :listId and (" +
            " i.addedOn > :changedAfter or" +
            " i.removedOn > :changedAfter )"
    )
    List<ItemEntity> getItemsChangedAfter(@Param("changedAfter") Date changedAfter, @Param("listId") Long shoppingListId);
}