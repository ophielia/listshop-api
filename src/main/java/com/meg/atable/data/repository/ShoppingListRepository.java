package com.meg.atable.data.repository;

import com.meg.atable.api.model.ListType;
import com.meg.atable.data.entity.DishEntity;
import com.meg.atable.data.entity.ShoppingListEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShoppingListRepository extends JpaRepository<ShoppingListEntity, Long> {

    List<ShoppingListEntity> findByUserId(Long userid);

    ShoppingListEntity findByUserIdAndListType(Long userid, ListType listType);


}