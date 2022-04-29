package com.meg.listshop.lmt.data.repository.impl;

import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.pojos.StandardUserTagConflictDTO;
import com.meg.listshop.lmt.data.repository.TagRepositoryCustom;
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
public class TagRepositoryImpl implements TagRepositoryCustom {

    EntityManager em;


    NamedParameterJdbcTemplate jdbcTemplate;

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

    @Autowired
    public TagRepositoryImpl(EntityManager em, NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.em = em;
    }

    @Override
    public List<TagEntity> findTagsByCriteria(List<TagType> tagTypes, Boolean assignSelect, Boolean searchSelect) {


        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TagEntity> cq = cb.createQuery(TagEntity.class);

        Root<TagEntity> tagEntityRoot = cq.from(TagEntity.class);
        List<Predicate> predicates = new ArrayList<>();

        ParameterExpression<Boolean> paramAssignSelect = cb.parameter(Boolean.class);
        if (assignSelect != null) {
            predicates.add(cb.equal(tagEntityRoot.get("assignSelect"), paramAssignSelect));
        }
        ParameterExpression<Boolean> paramSearchSelect = cb.parameter(Boolean.class);
        if (searchSelect != null) {
            predicates.add(cb.equal(tagEntityRoot.get("searchSelect"), paramSearchSelect));
        }
        ParameterExpression<Collection> paramTagTypes = cb.parameter(Collection.class);
        if (tagTypes != null && !tagTypes.isEmpty()) {
            predicates.add(tagEntityRoot.get("tagType").in(paramTagTypes));
        }
        predicates.add(cb.isFalse(tagEntityRoot.get("toDelete")));

        cq.where(predicates.toArray(new Predicate[0]));

        TypedQuery<TagEntity> typedQuery = em.createQuery(cq);
        if (assignSelect != null) {
            typedQuery.setParameter(paramAssignSelect, assignSelect);
        }
        if (searchSelect != null) {
            typedQuery.setParameter(paramSearchSelect, searchSelect);
        }
        if (tagTypes != null && !tagTypes.isEmpty()) {
            typedQuery.setParameter(paramTagTypes, tagTypes);
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

    public List<StandardUserTagConflictDTO> getStandardUserDuplicates(Long userId, Set<Long> tagKeys) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("userId", userId);
        parameters.addValue("tagIdList", tagKeys);

        return this.jdbcTemplate.query(TAG_CONFLICT_QUERY, parameters, new TagRepositoryImpl.TagConflictMapper());

    }


    private static final class TagConflictMapper implements RowMapper<StandardUserTagConflictDTO> {

        public StandardUserTagConflictDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            Long standardTagId = rs.getLong("standard_tag_id");
            Long userTagId = rs.getLong("user_tag_id");

            return new StandardUserTagConflictDTO(standardTagId, userTagId);
        }
    }
}
