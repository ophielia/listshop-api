package com.meg.atable.service.impl;

import com.meg.atable.data.entity.DishEntity;
import com.meg.atable.service.DishSearchCriteria;
import com.meg.atable.service.DishSearchService;
import com.meg.atable.service.DishTagSearchResult;
import com.meg.atable.service.tag.TagStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
public class DishSearchServiceImpl implements DishSearchService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TagStructureService tagStructureService;

    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public void init(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public List<DishEntity> findDishes(DishSearchCriteria criteria) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("userId", criteria.getUserId())
        ;
        String sqlBase = "select distinct d.* from dish d ";
        StringBuilder fromExtension = new StringBuilder();
        StringBuilder whereClause = new StringBuilder("where d.user_id = :userId ");
        HashSet<Long> allTagIds = getAllTagIdsForCriteria(criteria);
        Map<Long, List<Long>> groupDictionary = tagStructureService.getSearchGroupsForTagIds(allTagIds);
        if (!criteria.getIncludedTagIds().isEmpty()) {
            // get dictionary for tag_ids

            int i = 0;
            for (Long id : criteria.getIncludedTagIds()) {

                fromExtension.append("join dish_tags iT")
                        .append(i)
                        .append(" on d.dish_id = iT")
                        .append(i)
                        .append(".dish_id and iT")
                        .append(i)
                        .append(".tag_id  ");
                if (groupDictionary.containsKey(id)) {
                    fromExtension.append(" in (");
                    for (Long memberid : groupDictionary.get(id)) {
                        fromExtension.append(memberid);
                        fromExtension.append(",");
                    }
                    fromExtension.setLength(fromExtension.length() - 1);
                    fromExtension.append(") ");
                } else {
                    fromExtension.append(" = ")
                            .append(id)
                            .append(" ");

                }


                i++;
            }
        }
        if (!criteria.getExcludedTags().isEmpty()) {
            int i = 0;
            for (Long id : criteria.getExcludedTags()) {

                fromExtension.append("left join dish_tags eT")
                        .append(i)
                        .append(" on d.dish_id = eT")
                        .append(i)
                        .append(".dish_id and eT")
                        .append(i)
                        .append(".tag_id ");

                if (groupDictionary.containsKey(id)) {
                    fromExtension.append(" in (");
                    for (Long memberid : groupDictionary.get(id)) {
                        fromExtension.append(memberid);
                        fromExtension.append(",");
                    }
                    fromExtension.setLength(fromExtension.length() - 1);
                    fromExtension.append(") ");
                } else {
                    fromExtension.append(" = ")
                            .append(id)
                            .append(" ");

                }


                whereClause.append(" and eT")
                        .append(i)
                        .append(".tag_id is null ");


                i++;
            }

        }

        String sql = sqlBase + fromExtension.toString() + whereClause;

        return this.jdbcTemplate.query(sql, parameters, new DishMapper());
    }

    private HashSet<Long> getAllTagIdsForCriteria(DishSearchCriteria criteria) {
        HashSet<Long> allTagIds = new HashSet<>();
        if (criteria.getIncludedTagIds() != null) {
            allTagIds.addAll(criteria.getIncludedTagIds());
        }
        if (criteria.getExcludedTags() != null) {
            allTagIds.addAll(criteria.getExcludedTags());
        }
        return allTagIds;
    }

    @Override
    public List<DishTagSearchResult> retrieveDishResultsForTags(Long userId, Long slotDishTagId, int size, List<String> tagListForSlot, Map<Long, List<Long>> searchGroups, List<Long> sqlFilteredDishes) {
        // create sql
        Object[] sqlAndParams = createSqlForDishTagSearchResult(tagListForSlot, searchGroups, sqlFilteredDishes);
        String sql = (String) sqlAndParams[0];
        Map<String, Object> params = (Map<String, Object>) sqlAndParams[1];
        // create parameters
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("userId", userId);
        parameters.addValue("slotTagId", slotDishTagId);
        params.forEach((key, value) -> parameters.addValue(key, value));

        return this.jdbcTemplate.query(sql, parameters, new DishTagSearchResultMapper(size, tagListForSlot.size()));

    }

    private Object[] createSqlForDishTagSearchResult(List<String> tagListForSlot, Map<Long, List<Long>> searchGroups, List<Long> sqlFilteredDishes) {
        Object[] returnvalue = new Object[2];
        Map<String, Object> parameters = new HashMap<>();

        StringBuilder selectClause = new StringBuilder("select distinct dt.dish_id , d.last_added ");
        StringBuilder outerJoins = new StringBuilder();
        StringBuilder orderByClause = new StringBuilder(" order by d.last_added NULLS FIRST ");
        // construct basic joins and from clause
        StringBuilder fromClause = new StringBuilder(" from dish_tags dt join dish d on d.dish_id = dt.dish_id and d.user_id = :userId and dt.tag_id = :slotTagId");
        StringBuilder groupByClause = new StringBuilder(" group by dt.dish_id, d.last_added ");
        // construct outerJoins and add to selectClause
        int i = 0;
        for (String id : tagListForSlot) {
            selectClause.append(", count(iT")
                    .append(i)
                    .append(".tag_id )");

            outerJoins.append(" left outer join dish_tags iT")
                    .append(i)
                    .append(" on d.dish_id = iT")
                    .append(i)
                    .append(".dish_id and iT")
                    .append(i);

            if (searchGroups.containsKey(Long.valueOf(id))) {
                String paramname = "taglist" + i;
                outerJoins.append(".tag_id in ")
                        .append(" (:")
                        .append(paramname)
                        .append(" )");
                parameters.put(paramname, searchGroups.get(Long.valueOf(id)));

            } else {
                outerJoins.append(".tag_id = ")
                        .append(id)
                        .append(" ");

            }
            i++;
        }

        StringBuilder whereClause = new StringBuilder(" ");
        if (sqlFilteredDishes != null && !sqlFilteredDishes.isEmpty()) {
            whereClause.append(" where d.dish_id not in (");
            sqlFilteredDishes.forEach( d -> whereClause.append(d).append(","));
            whereClause.setLength(whereClause.length()-1);
            whereClause.append(") ");
        }


        returnvalue[0] = selectClause.append(fromClause).append(outerJoins)
                .append(whereClause)
                .append(groupByClause).append(orderByClause).toString();
        returnvalue[1] = parameters;
        return returnvalue;
    }


    private static final class DishMapper implements RowMapper<DishEntity> {

        public DishEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            Long id = rs.getLong("user_id");
            String dishName = rs.getString("dish_name");
            DishEntity dishEntity = new DishEntity(id, dishName);
            dishEntity.setDescription(rs.getString("description"));
            dishEntity.setId(rs.getLong("dish_id"));
            dishEntity.setLastAdded(rs.getDate("last_added"));
            return dishEntity;
        }
    }

    private static final class DishTagSearchResultMapper implements RowMapper<DishTagSearchResult> {

        private final int targetTagCount;
        private final int queriedTagSize;

        public DishTagSearchResultMapper(int targetTagCount, int queriedTagSize) {
            this.targetTagCount = targetTagCount;
            this.queriedTagSize = queriedTagSize;
        }

        public DishTagSearchResult mapRow(ResultSet rs, int rowNum) throws SQLException {
            Long id = rs.getLong("dish_id");
            Date date = rs.getDate("last_added");
            DishTagSearchResult searchResult = new DishTagSearchResult(id, date, targetTagCount, queriedTagSize);

            for (int i = 1; i <= queriedTagSize; i++) {
                // passed with i-1 to compensate for offset for initial dish_id
                searchResult.addTagResult(i - 1, rs.getInt(i + 2));
            }

            return searchResult;
        }
    }


}
