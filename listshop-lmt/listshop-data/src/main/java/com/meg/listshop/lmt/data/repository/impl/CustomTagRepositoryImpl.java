package com.meg.listshop.lmt.data.repository.impl;

import com.meg.listshop.lmt.data.pojos.*;
import com.meg.listshop.lmt.data.repository.CustomTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@Repository
public class CustomTagRepositoryImpl implements CustomTagRepository {

    private final static String TAG_CONFLICT_QUERY = "with list_tags as (select tag_id, " +
            "                          lower(trim(name))                             as name, " +
            "                          tag_type, " +
            "                          user_id, " +
            "                          case when user_id is not null then tag_id end as user_tag_id, " +
            "                          case when user_id is null then tag_id end     as standard_tag_id " +
            "                   from tag t " +
            "                   where (t.user_id is null or t.user_id = :userId) " +
            "                     and lower(trim(name)) in (select lower(trim(name)) from tag where tag_id in (:tagIdList))), " +
            "     conflict_list as (select name, " +
            "                              tag_type, " +
            "                              max(user_tag_id)     as user_tag_id, " +
            "                              max(standard_tag_id) as standard_tag_id, " +
            "                              max(user_id)         as user_id " +
            "                       from list_tags " +
            "                       group by 1, 2) " +
            "select standard_tag_id, user_tag_id " +
            "from conflict_list " +
            "where user_id is not null";
    EntityManager em;
    NamedParameterJdbcTemplate jdbcTemplate;


    @Autowired
    public CustomTagRepositoryImpl(EntityManager em, NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.em = em;
    }

    private Integer calculateMagicNumberForStatuses(List<TagInternalStatus> includedStatuses) {
        Long magicNumber = 1L;
        for (TagInternalStatus status : includedStatuses) {
            magicNumber *= status.value();
        }
        return magicNumber.intValue();
    }

    @Override
    public Long findRatingTagIdForStep(Long ratingId, Integer step) {
        Query query = em.createNamedQuery("TagEntity.findRatingByParent");
        query.setParameter("rating_parent", ratingId);
        query.setMaxResults(step);
        List<Long> ratingTags = query.getResultList();
        if (!ratingTags.isEmpty() && (ratingTags.size() == step)) {
            Long tagId = ratingTags.get(ratingTags.size() - 1);
            return tagId;
        }
        return null;
    }

    public List<LongTagIdPairDTO> getStandardUserDuplicates(Long userId, Set<Long> tagKeys) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("userId", userId);
        parameters.addValue("tagIdList", tagKeys);

        return this.jdbcTemplate.query(TAG_CONFLICT_QUERY, parameters, new CustomTagRepositoryImpl.TagConflictMapper());

    }


    private static final class TagConflictMapper implements RowMapper<LongTagIdPairDTO> {

        public LongTagIdPairDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            Long standardTagId = rs.getLong("standard_tag_id");
            Long userTagId = rs.getLong("user_tag_id");

            return new LongTagIdPairDTO(standardTagId, userTagId);
        }
    }
}
