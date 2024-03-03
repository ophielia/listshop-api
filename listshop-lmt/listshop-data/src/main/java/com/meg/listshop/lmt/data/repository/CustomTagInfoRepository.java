package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.data.pojos.TagInfoDTO;
import com.meg.listshop.lmt.data.pojos.TagSearchCriteria;

import java.util.List;

public interface CustomTagInfoRepository {


    List<TagInfoDTO> retrieveTagInfoByUser(Long userId, List<TagType> tagTypes);

    List<TagInfoDTO> findTagInfoByCriteria(TagSearchCriteria criteria);

    List<TagInfoDTO> retrieveRatingInfoForDish(Long dishId);
}