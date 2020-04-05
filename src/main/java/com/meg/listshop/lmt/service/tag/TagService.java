package com.meg.listshop.lmt.service.tag;

import com.meg.listshop.lmt.api.model.RatingUpdateInfo;
import com.meg.listshop.lmt.api.model.SortOrMoveDirection;
import com.meg.listshop.lmt.api.model.TagFilterType;
import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.entity.TagExtendedEntity;
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

    TagEntity getTagById(Long tagId);


    List<TagExtendedEntity> getTagExtendedList(TagFilterType tagFilterType, List<TagType> tagTypes);

    TagEntity createTag(TagEntity parent, String name);

    TagEntity createTag(TagEntity parent, TagEntity newTag);

    List<TagEntity> getTagsForDish(String username, Long dishId);

    List<TagEntity> getTagsForDish(String username, Long dishId, List<TagType> tagtypes);

    boolean assignTagToParent(Long tagId, Long parentId);

    boolean assignChildrenToParent(Long parentId, List<Long> childrenIds);

    boolean assignTagToParent(TagEntity childTag, TagEntity newParentTag);

    void addTagToDish(String userName, Long dishId, Long tagId);

    void addTagsToDish(String userName, Long id, Set<Long> tagIds);

    void removeTagsFromDish(String userName, Long dishId, Set<Long> tagIds);

    List<TagEntity> getTagList(TagFilterType baseTags, List<TagType> tagType);


    void deleteTagFromDish(String userName, Long dishId, Long tagId);

    Map<Long, TagEntity> getDictionaryForIds(Set<Long> tagIds);

    TagEntity updateTag(Long tagId, TagEntity toUpdate);

    void replaceTagInDishes(String name, Long fromTagId, Long toTagId);

    void addTagChangeListener(TagChangeListener tagChangeListener);

    void saveTagForDelete(Long tagId, Long replacementTagId);

    TagEntity getTagById(Long tagId, Boolean swapIfReplaced);

    RatingUpdateInfo getRatingUpdateInfoForDishIds(String username, List<Long> dishIdList);

    void incrementDishRating(String name, Long dishId, Long ratingId, SortOrMoveDirection moveDirection);

    List<TagEntity> getReplacedTagsFromIds(Set<Long> tagKeys);
}
