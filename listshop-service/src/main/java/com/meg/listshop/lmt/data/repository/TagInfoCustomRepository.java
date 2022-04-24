package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.data.entity.TagInfoDTO;

import java.util.List;

public interface TagInfoCustomRepository {


    List<TagInfoDTO> retrieveTagInfoByUser(Long userId);
}