package com.meg.listshop.lmt.service.tag;

import com.meg.listshop.lmt.api.exception.BadParameterException;
import com.meg.listshop.lmt.api.model.RatingUpdateInfo;
import com.meg.listshop.lmt.api.model.SortOrMoveDirection;
import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.data.entity.TagEntity;
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


    TagEntity createTag(Long parentId, TagEntity newTag, Long userId) throws BadParameterException;

    List<TagEntity> getTagsForDish(String username, Long dishId);

    List<TagEntity> getTagsForDish(Long userId, Long dishId);

    List<TagEntity> getTagsForDish(String username, Long dishId, List<TagType> tagtypes);

    void assignTagToParent(Long tagId, Long parentId);

    void assignChildrenToParent(Long parentId, List<Long> childrenIds);

    List<TagEntity> getTagsForDish(Long userId, Long dishId, List<TagType> tagtypes);

    void addTagToDish(Long userId, Long dishId, Long tagId);

    void addTagsToDish(Long userId, Long id, Set<Long> tagIds);

    int removeTagsFromDish(Long userId, Long dishId, Set<Long> tagIds);

    List<TagEntity> getIngredientTagsForDishes(List<Long> dishIdList);

    int deleteTagFromDish(Long userId, Long dishId, Long tagId);

    Map<Long, TagEntity> getDictionaryForIds(Set<Long> tagIds);

    TagEntity updateTag(Long tagId, TagEntity toUpdate);

    void replaceTagInDishes(Long userId, Long fromTagId, Long toTagId);

    void addTagChangeListener(TagChangeListener tagChangeListener);

    void saveTagForDelete(Long tagId, Long replacementTagId);

    RatingUpdateInfo getRatingUpdateInfoForDishIds(List<Long> dishIdList);

    void incrementDishRating(Long userId, Long dishId, Long ratingId, SortOrMoveDirection moveDirection);

    void setDishRating(Long userId, Long dishId, Long ratingId, Integer step);

    List<TagEntity> getReplacedTagsFromIds(Set<Long> tagKeys);


    List<TagInfoDTO> getTagInfoList(Long userId, List<TagType> tagTypes);

    List<LongTagIdPairDTO> getStandardUserDuplicates(Long userId, Set<Long> tagKeys);

    List<TagEntity> getTagList(TagSearchCriteria criteria);

    void assignTagsToUser(Long userId, List<Long> tagIds);

    void setTagsAsVerified(List<Long> tagIds);

    void createStandardTagsFromUserTags(List<Long> tagIds);

    void assignDefaultRatingsToDish(Long userId, Long dishId);
}
