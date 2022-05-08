package com.meg.listshop.lmt.service.tag;

import com.meg.listshop.lmt.api.model.RatingUpdateInfo;
import com.meg.listshop.lmt.api.model.SortOrMoveDirection;
import com.meg.listshop.lmt.api.model.TagFilterType;
import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.entity.TagExtendedEntity;
import com.meg.listshop.lmt.data.pojos.LongTagIdPairDTO;
import com.meg.listshop.lmt.data.pojos.TagInfoDTO;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by margaretmartin on 13/05/2017.
 */
public interface TagService {
    @Value("${service.tagservice.main.dish.tagid}")
    Long MAIN_DISH_TAG_ID = 320L;

    TagEntity save(TagEntity tag);

    TagEntity getTagById(Long tagId);


    List<TagExtendedEntity> getTagExtendedList(TagFilterType tagFilterType, List<TagType> tagTypes);

    TagEntity createTag(TagEntity parent, TagEntity newTag, String username);

    List<TagEntity> getTagsForDish(String username, Long dishId);

    List<TagEntity> getTagsForDish(String username, Long dishId, List<TagType> tagtypes);

    void assignTagToParent(Long tagId, Long parentId);

    void assignChildrenToParent(Long parentId, List<Long> childrenIds);

    void addTagToDish(String userName, Long dishId, Long tagId);

    void addTagsToDish(String userName, Long id, Set<Long> tagIds);

    void removeTagsFromDish(String userName, Long dishId, Set<Long> tagIds);

    List<TagEntity> getIngredientTagsForDishes(List<Long> dishIdList);

    void deleteTagFromDish(String userName, Long dishId, Long tagId);

    Map<Long, TagEntity> getDictionaryForIds(Set<Long> tagIds);

    TagEntity updateTag(Long tagId, TagEntity toUpdate);

    void replaceTagInDishes(String name, Long fromTagId, Long toTagId);

    void addTagChangeListener(TagChangeListener tagChangeListener);

    void saveTagForDelete(Long tagId, Long replacementTagId);

    RatingUpdateInfo getRatingUpdateInfoForDishIds(String username, List<Long> dishIdList);

    void incrementDishRating(String name, Long dishId, Long ratingId, SortOrMoveDirection moveDirection);

    void setDishRating(String name, Long dishId, Long ratingId, Integer step);

    List<TagEntity> getReplacedTagsFromIds(Set<Long> tagKeys);


    List<TagInfoDTO> getTagInfoList(String name);

    List<LongTagIdPairDTO> getStandardUserDuplicates(Long userId, Set<Long> tagKeys);

    List<TagEntity> getTagList(TagSearchCriteria criteria);
}
