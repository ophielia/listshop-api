package com.meg.listshop.lmt.data.repository.impl;

import com.meg.listshop.lmt.data.pojos.LongTagIdPairDTO;
import com.meg.listshop.lmt.data.repository.CustomTagRelationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * Created by margaretmartin on 21/10/2017.
 */

public class CustomTagRelationRepositoryImpl implements CustomTagRelationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private NamedParameterJdbcTemplate jdbcTemplate;

    private final String DESCENDANT_TAG_QUERY = "WITH RECURSIVE all_children AS ( " +
            "    select t.tag_id from tag t where tag_id = ?1 " +
            "    UNION " +
            "    select t.tag_id from all_children n " +
            "                                     join tag_relation tr on n.tag_id = tr.parent_tag_id " +
            "                                     join tag t on t.tag_id = tr.child_tag_id " +
            ") " +
            "SELECT * FROM all_children;";

    private final String MULTI_DESCENDANT_TAG_QUERY = "WITH RECURSIVE all_children AS ( " +
            "    select t.tag_id as descendant_id,  t.tag_id as queried_id " +
            "    from tag t where tag_id in (:tagIds)  " +
            "    UNION " +
            "    select t.tag_id as descendant_id,  n.queried_id " +
            "    from all_children n " +
            "                                     join tag_relation tr on n.descendant_id = tr.parent_tag_id " +
            "                                     join tag t on t.tag_id = tr.child_tag_id " +
            "   where (t.user_id is null or t.user_id = :userId)" +
            ") " +
            "SELECT * FROM all_children";

    private final String ASCENDANT_TAG_QUERY = "WITH RECURSIVE all_children AS ( " +
            "    select t.tag_id from tag t where tag_id = ?1 " +
            "    UNION " +
            "    select t.tag_id from all_children n " +
            "                                     join tag_relation tr on n.tag_id = tr.child_tag_id " +
            "                                     join tag t on t.tag_id = tr.parent_tag_id " +
            ") " +
            "SELECT * FROM all_children;";

    private final String MULTI_RATINGS_POWER_ABOVE_QUERY = "select o.tag_id as queried_id,s.tag_id as sibling_id  from tag o  join tag_relation tro on o.tag_id = tro.child_tag_id and o.tag_type = 'Rating'  join tag_relation trs on tro.parent_tag_id = trs.parent_tag_id  join tag s on trs.child_tag_id = s.tag_id  where o.tag_id in (:tagIds)  and o.power <= s.power  and (s.user_id is null or s.user_id = :userId)";

    private final String MULTI_RATINGS_POWER_BELOW_QUERY = "select o.tag_id as queried_id,s.tag_id as sibling_id  from tag o  join tag_relation tro on o.tag_id = tro.child_tag_id and o.tag_type = 'Rating'  join tag_relation trs on tro.parent_tag_id = trs.parent_tag_id  join tag s on trs.child_tag_id = s.tag_id  where o.tag_id in (:tagIds)  and o.power > s.power  and (s.user_id is null or s.user_id = :userId)";

    @Autowired
    public void init(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public List<Long> getTagWithDescendants(Long tagId) {
        return entityManager.createNativeQuery(DESCENDANT_TAG_QUERY)
                .setParameter(1, tagId)
                .getResultList();
    }

    public List<Long> getTagWithAscendants(Long tagId) {
        return entityManager.createNativeQuery(ASCENDANT_TAG_QUERY)
                .setParameter(1, tagId)
                .getResultList();
    }

    public Map<Long, List<Long>> getDescendantMap(Set<Long> tagIds, Long userId) {

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("userId", userId);
        parameters.addValue("tagIds", tagIds);

        String sql = MULTI_DESCENDANT_TAG_QUERY;

        List<LongTagIdPairDTO> results = this.jdbcTemplate.query(sql, parameters, new CustomTagRelationRepositoryImpl.LongTagIdPairMapper());

        return results.stream()
                .collect(groupingBy(LongTagIdPairDTO::getRightId, Collectors.mapping(LongTagIdPairDTO::getLeftId, Collectors.toList())));

    }

    public Map<Long, List<Long>> getRatingsWithSiblingsByPower(List<Long> tagIds, boolean powerBelow, Long userId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("userId", userId);
        parameters.addValue("tagIds", tagIds);

        String sql = powerBelow ? MULTI_RATINGS_POWER_BELOW_QUERY : MULTI_RATINGS_POWER_ABOVE_QUERY;

        List<LongTagIdPairDTO> results = this.jdbcTemplate.query(sql, parameters, new CustomTagRelationRepositoryImpl.LongTagIdPairMapper());

        return results.stream()
                .collect(groupingBy(LongTagIdPairDTO::getLeftId, Collectors.mapping(LongTagIdPairDTO::getRightId, Collectors.toList())));


    }


    private static final class LongTagIdPairMapper implements RowMapper<LongTagIdPairDTO> {

        public LongTagIdPairDTO mapRow(ResultSet rs, int rowNum) throws SQLException {

            Long queried_id = rs.getLong(1);
            Long sibling_id = rs.getLong(2);

            return new LongTagIdPairDTO(queried_id, sibling_id);
        }
    }


}
