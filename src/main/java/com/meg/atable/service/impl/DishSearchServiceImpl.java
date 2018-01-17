package com.meg.atable.service.impl;

import com.meg.atable.data.entity.DishEntity;
import com.meg.atable.service.DishSearchCriteria;
import com.meg.atable.service.DishSearchService;
import com.meg.atable.service.DishTagSearchResult;
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
import java.util.Date;
import java.util.List;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
public class DishSearchServiceImpl implements DishSearchService {

    @PersistenceContext
    private EntityManager entityManager;


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
        StringBuffer fromExtension = new StringBuffer();
        StringBuffer whereClause = new StringBuffer("where d.user_id = :userId ");
        if (!criteria.getIncludedTagIds().isEmpty()) {
            int i = 0;
            for (Long id : criteria.getIncludedTagIds()) {

                fromExtension.append("join dish_tags iT")
                        .append(i)
                        .append(" on d.dish_id = iT")
                        .append(i)
                        .append(".dish_id and iT")
                        .append(i)
                        .append(".tag_id = ")
                        .append(id)
                        .append(" ");
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
                        .append(".tag_id = ")
                        .append(id)
                        .append(" ");
                whereClause.append(" and eT")
                        .append(i)
                        .append(".tag_id is null ");
            }

        }

        String sql = sqlBase + fromExtension.toString() + whereClause;

        List<DishEntity> dishEntities = this.jdbcTemplate.query(sql, parameters, new DishMapper());
        return dishEntities;
    }

    @Override
    public List<DishTagSearchResult> retrieveDishResultsForTags(Long userId, Long slotDishTagId, int size, List<String> tagListForSlot) {
        // create sql
        String sql = createSqlForDishTagSearchResult(tagListForSlot);
        // create parameters
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("userId", userId);
        parameters.addValue("slotTagId", slotDishTagId);

        List<DishTagSearchResult> rawSearchResults = this.jdbcTemplate.query(sql, parameters, new DishTagSearchResultMapper(size, tagListForSlot.size()));

        return rawSearchResults;
    }

    private String createSqlForDishTagSearchResult(List<String> tagListForSlot) {
        StringBuffer selectClause = new StringBuffer("select distinct dt.dish_id , d.last_added ");
        StringBuffer outerJoins = new StringBuffer();
        StringBuffer orderByClause = new StringBuffer(" order by d.last_added NULLS FIRST ");
        // construct basic joins and from clause
        StringBuffer fromClause = new StringBuffer(" from dish_tags dt join dish d on d.dish_id = dt.dish_id and d.user_id = :userId and dt.tag_id = :slotTagId");
        // construct outerJoins and add to selectClause
        int i = 0;
        for (String id : tagListForSlot) {
            selectClause.append(", iT")
                    .append(i)
                    .append(".tag_id ");

            outerJoins.append(" left outer join dish_tags iT")
                    .append(i)
                    .append(" on d.dish_id = iT")
                    .append(i)
                    .append(".dish_id and iT")
                    .append(i)
                    .append(".tag_id = ")
                    .append(id)
                    .append(" ");
            i++;
        }

        return selectClause.append(fromClause).append(outerJoins).append(orderByClause).toString();
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
