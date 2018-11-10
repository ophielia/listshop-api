package com.meg.atable.lmt.service.tag;

import com.meg.atable.lmt.api.model.TagFilterType;
import com.meg.atable.lmt.api.model.TagType;
import com.meg.atable.lmt.data.entity.TagEntity;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by margaretmartin on 13/05/2017.
 */
public interface TagService {
    @Value("${service.tagservice.main.dish.tagid}")
    public final Long MAIN_DISH_TAG_ID = 320L;

    TagEntity save(TagEntity tag);

    TagEntity getTagById(Long dishId);


    TagEntity createTag(TagEntity parent, String name);

    TagEntity createTag(TagEntity parent, String name, String description);

    TagEntity createTag(TagEntity parent, TagEntity newTag);

    List<TagEntity> getTagsForDish(Long dishId);

    List<TagEntity> getTagsForDish(Long dishId, List<TagType> tagtypes);

    boolean assignTagToParent(Long tagId, Long parentId);

    boolean assignChildrenToParent(Long parentId, List<Long> childrenIds);

    boolean assignTagToParent(TagEntity childTag, TagEntity newParentTag);

    void addTagToDish(Long dishId, Long tagId);

    void addTagsToDish(Long id, Set<Long> tagIds);

    void removeTagsFromDish(Long dishId, Set<Long> tagIds);

    List<TagEntity> getTagList(TagFilterType baseTags, List<TagType> tagType);


    void deleteTagFromDish(Long dishId, Long tagId);

    Map<Long, TagEntity> getDictionaryForIds(Set<Long> tagIds);

    TagEntity updateTag(Long tagId, TagEntity toUpdate);

    void replaceTagInDishes(String name, Long fromTagId, Long toTagId);

    void addTagChangeListener(TagChangeListener tagChangeListener);


}
