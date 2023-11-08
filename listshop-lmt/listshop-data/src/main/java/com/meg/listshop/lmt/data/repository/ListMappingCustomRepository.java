package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.data.pojos.ItemMappingDTO;

import java.util.List;

public interface ListMappingCustomRepository {
    List<ItemMappingDTO> getListMappings(Long userLayoutId, Long id1);
}