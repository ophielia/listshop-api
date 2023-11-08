package com.meg.listshop.lmt.data.repository.impl;

import com.meg.listshop.lmt.api.model.TagFilterType;
import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.pojos.LongTagIdPairDTO;
import com.meg.listshop.lmt.data.pojos.TagSearchCriteria;
import com.meg.listshop.lmt.data.repository.CustomTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
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

    public List<TagEntity> findTagsByCriteria(TagSearchCriteria criteria) {
        if (criteria == null) {
            criteria = new TagSearchCriteria();
        }

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TagEntity> cq = cb.createQuery(TagEntity.class);

        Root<TagEntity> tagEntityRoot = cq.from(TagEntity.class);
        List<Predicate> predicates = new ArrayList<>();

        TagFilterType tagFilterType = criteria.getTagFilterType() != null ? criteria.getTagFilterType() : TagFilterType.All;
        ParameterExpression<Boolean> parameterGroupInclude = cb.parameter(Boolean.class);
        ParameterExpression<Boolean> verifiedInclude = cb.parameter(Boolean.class);
        Boolean groupFilterParameter = null;
        Boolean verifiedFilterParamter = null;
        if (tagFilterType == TagFilterType.NoGroups ||
                tagFilterType == TagFilterType.GroupsOnly) {
            predicates.add(cb.equal(tagEntityRoot.get("isGroup"), parameterGroupInclude));
            groupFilterParameter = tagFilterType != TagFilterType.NoGroups;
        }
        if (tagFilterType == TagFilterType.ToReview) {
            predicates.add(cb.isNotNull(tagEntityRoot.get("userId")));
            predicates.add(cb.or(cb.equal(tagEntityRoot.get("isVerified"), verifiedInclude),
                    cb.isNull(tagEntityRoot.get("isVerified"))));
            verifiedFilterParamter = false;
        }

        Long userIdParameter = null;
        ParameterExpression<Long> userSearchSelect = cb.parameter(Long.class);
        if (criteria.getUserId() != null) {
            predicates.add(cb.equal(tagEntityRoot.get("userId"), userSearchSelect));
            userIdParameter = criteria.getUserId();
        } else if (tagFilterType != TagFilterType.ToReview) {
            predicates.add(cb.isNull(tagEntityRoot.get("userId")));
        }

        List<TagType> tagTypeParameter = null;
        ParameterExpression<Collection> paramTagTypes = cb.parameter(Collection.class);
        if (criteria.getTagTypes() != null && !criteria.getTagTypes().isEmpty()) {
            predicates.add(tagEntityRoot.get("tagType").in(paramTagTypes));
            tagTypeParameter = criteria.getTagTypes();
        }
        predicates.add(cb.isFalse(tagEntityRoot.get("toDelete")));

        cq.where(predicates.toArray(new Predicate[0]));

        TypedQuery<TagEntity> typedQuery = em.createQuery(cq);

        if (groupFilterParameter != null) {
            typedQuery.setParameter(parameterGroupInclude, groupFilterParameter);
        }
        if (verifiedFilterParamter != null) {
            typedQuery.setParameter(verifiedInclude, verifiedFilterParamter);
        }
        if (userIdParameter != null) {
            typedQuery.setParameter(userSearchSelect, userIdParameter);
        }
        if (tagTypeParameter != null) {
            typedQuery.setParameter(paramTagTypes, tagTypeParameter);
        }

        return typedQuery.getResultList();
    }

    @Override
    public Long findRatingTagIdForStep(Long ratingId, Integer step) {
        Query query = em.createNamedQuery("TagEntity.findRatingByParent");
        query.setParameter("rating_parent", ratingId);
        query.setMaxResults(step);
        List<BigInteger> ratingTags = query.getResultList();
        if (!ratingTags.isEmpty() && (ratingTags.size() == step)) {
            BigInteger tagId = ratingTags.get(ratingTags.size() - 1);
            return tagId.longValue();
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
