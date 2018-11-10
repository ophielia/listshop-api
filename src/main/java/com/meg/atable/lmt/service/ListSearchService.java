package com.meg.atable.lmt.service;

import com.meg.atable.lmt.data.entity.TagEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by margaretmartin on 13/05/2017.
 */
public interface ListSearchService {

    Map<Long, Long> getTagToCategoryMap(Long listLayoutId, List<TagEntity> tagEntities);
}
