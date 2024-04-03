package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.data.entity.FoodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FoodRepository extends JpaRepository<FoodEntity, Long> {

}