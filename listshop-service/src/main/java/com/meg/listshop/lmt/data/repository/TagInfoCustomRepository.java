package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.data.pojos.TagInfoDTO;

import java.util.List;

public interface TagInfoCustomRepository {


    List<TagInfoDTO> retrieveTagInfoByUser(Long userId, List<TagType> tagTypes);
}