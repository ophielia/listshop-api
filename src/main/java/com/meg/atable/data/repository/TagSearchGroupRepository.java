package com.meg.atable.data.repository;

import com.meg.atable.api.model.TagType;
import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.data.entity.TagRelationEntity;
import com.meg.atable.data.entity.TagSearchGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TagSearchGroupRepository extends JpaRepository<TagSearchGroupEntity, Long> {

    @Modifying
    @Query("delete from TagSearchGroupEntity e where e.groupId in :groupIdList and e.memberId in :memberIdList")
    void deleteByGroupAndMember(@Param("groupIdList")List<Long> origParentTags, @Param("memberIdList")List<Long> childrenTags);
}