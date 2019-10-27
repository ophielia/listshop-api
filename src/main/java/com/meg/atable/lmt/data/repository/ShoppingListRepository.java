package com.meg.atable.lmt.data.repository;

import com.meg.atable.lmt.api.model.ListType;
import com.meg.atable.lmt.data.entity.ShoppingListEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShoppingListRepository extends JpaRepository<ShoppingListEntity, Long> {

    List<ShoppingListEntity> findByUserId(Long userid);

    List<ShoppingListEntity> findByUserIdAndListType(Long userid, ListType listType);

    @EntityGraph(value="list-entity-graph")
    Optional<ShoppingListEntity> getWithItemsByListId(Long listid);

    @EntityGraph(value="list-entity-graph")
    Optional<ShoppingListEntity> getWithItemsByListIdAndItemsRemovedOnIsNull(Long listid);

    @EntityGraph(value="list-entity-graph")
    ShoppingListEntity findWithItemsByUserIdAndListType(Long userid, ListType listType);


    List<ShoppingListEntity> findByUserIdAndName(Long userid, String name);

    List<ShoppingListEntity> findByUserIdAndNameLike(Long userid, String name);
}