package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.data.entity.ListLayoutEntity;

import java.util.List;

public interface CustomListLayoutRepository {

    List<ListLayoutEntity> getFilledUserLayouts(Long userId);
}