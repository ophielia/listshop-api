package com.meg.atable.data.repository;

import com.meg.atable.data.entity.TagSearchGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface TagSearchGroupRepository extends JpaRepository<TagSearchGroupEntity, Long> {

    @Modifying
    @Query("delete from TagSearchGroupEntity e where e.groupId in :groupIdList and e.memberId in :memberIdList")
    void deleteByGroupAndMember(@Param("groupIdList")List<Long> origParentTags, @Param("memberIdList")List<Long> childrenTags);

    @Modifying
    @Query("delete from TagSearchGroupEntity e where e.groupId in :groupIdList and e.memberId in :memberIdList")
    void deleteByMember(@Param("groupIdList")List<Long> origParentTags, @Param("memberIdList")List<Long> childrenTags);

    List<TagSearchGroupEntity> findByGroupIdIn(Set<Long> allTags);

    @SuppressWarnings("SpringDataRepositoryMethodReturnTypeInspection")
    @Query(value = "select dt.dish_id, s.group_id, s.member_id from dish_tags dt, tag_search_group s " +
            "where dt.dish_id in (:dishIds) and dt.tag_id = s.member_id and s.group_id in (:tagIds) and dt.tag_id <> s.group_id", nativeQuery = true)
    List<Object[]> getTagSwapoutsByDishesAndGroups(@Param("dishIds") List<Long> dishIds, @Param("tagIds")List<Long> tagListForSlot);

    List<TagSearchGroupEntity> findByMemberId(Long memberid);

    List<TagSearchGroupEntity> findByGroupId(Long groupId);
}