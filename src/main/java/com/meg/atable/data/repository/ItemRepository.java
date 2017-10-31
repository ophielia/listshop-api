package com.meg.atable.data.repository;

import com.meg.atable.api.model.ListType;
import com.meg.atable.data.entity.ItemEntity;
import com.meg.atable.data.entity.ShoppingListEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {


    List<ItemEntity> findByListId(Long listId);
}