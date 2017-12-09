package com.meg.atable.data.repository;

import com.meg.atable.api.model.ListType;
import com.meg.atable.data.entity.DishEntity;
import com.meg.atable.data.entity.ShoppingListEntity;
import com.meg.atable.data.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShoppingListRepository extends JpaRepository<ShoppingListEntity, Long> {

    List<ShoppingListEntity> findByUserId(Long userid);

    ShoppingListEntity findByUserIdAndListType(Long userid, ListType listType);

    @Modifying
    @Query(value="delete from list_item where item_id in (select i.item_id  " +
            "from list_item i, list l " +
            "where i.list_id = l.list_id " +
            "and l.user_id = :userid " +
            "and l.list_id = :listid " +
            "and i.tag_id in (:taglist));", nativeQuery=true)
    void bulkDeleteFromList(@Param("userid")Long id,@Param("listid") Long listid, @Param("taglist")List<Long> tagids);
}