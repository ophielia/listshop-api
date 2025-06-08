package com.meg.listshop.lmt.dish.impl;

import com.meg.listshop.lmt.api.model.DishSortDirection;
import com.meg.listshop.lmt.api.model.DishSortKey;
import com.meg.listshop.lmt.data.entity.DishEntity;
import com.meg.listshop.lmt.data.entity.TargetSlotEntity;
import com.meg.listshop.lmt.dish.DishSearchCriteria;
import com.meg.listshop.lmt.dish.DishSearchService;
import com.meg.listshop.lmt.dish.DishTagSearchResult;
import com.meg.listshop.lmt.service.tag.TagStructureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
public class DishSearchServiceImpl implements DishSearchService {

    private static final Logger  logger = LoggerFactory.getLogger(DishSearchServiceImpl.class);

    private TagStructureService tagStructureService;

    private NamedParameterJdbcTemplate jdbcTemplate;


    private static final String FILTER_CASE_BEGIN = "having sum(case ";
    private static final String FILTER_CASE_END = " else 0 end) =  ";
    private static final String FILTER_CASE_END_EXCLUDE_ONLY = " else 0 end) >= 0  ";
    private static final String GROUP_BY_BEGIN = " group by 1,2,3,4,5 ";

    @Autowired
    public void init(DataSource dataSource, TagStructureService tagStructureService) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.tagStructureService = tagStructureService;
    }

    @Override
    public List<DishEntity> findDishes(DishSearchCriteria criteria) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("userId", criteria.getUserId())
        ;
        String sqlBase = "select distinct d.user_id, lower(d.dish_name), d.description, d.dish_id, d.last_added  from dish d join dish_items dt using (dish_id) ";
        String whereClause = "where d.user_id = :userId ";
        StringBuilder groupByFilter = new StringBuilder("");
        StringBuilder nameWhereClause = new StringBuilder("");
        StringBuilder sortClause = new StringBuilder(" ");

        List<String> filterClauses = new ArrayList<>();
        int excludeClauseCount = 0;
        if (!criteria.getIncludedTagIds().isEmpty()) {
            List<String> includeClauses = getFilterClauses(criteria.getIncludedTagIds(), false, criteria.getUserId());
            filterClauses.addAll(includeClauses);
        }
        if (!criteria.getExcludedTags().isEmpty()) {
            List<String> excludeClauses = getFilterClauses(criteria.getExcludedTags(), true, criteria.getUserId());
            filterClauses.addAll(excludeClauses);
            excludeClauseCount = excludeClauses.size();
        }
        // create tag filter, if we have something to filter
        if (!filterClauses.isEmpty()) {
            // add all clauses to builder, separating by space
            int countWithoutExclude = filterClauses.size() - excludeClauseCount;
            filterClauses.stream().forEach(c -> groupByFilter.append(" ").append(c).append(" "));
            groupByFilter.insert(0, FILTER_CASE_BEGIN);
            groupByFilter.insert(0, GROUP_BY_BEGIN);
            if (countWithoutExclude > 0) {
                groupByFilter.append(FILTER_CASE_END);
                groupByFilter.append(countWithoutExclude);
            } else {
                groupByFilter.append(FILTER_CASE_END_EXCLUDE_ONLY);
            }
        }
        if (!ObjectUtils.isEmpty(criteria.getNameFragment())) {
            nameWhereClause.append(" and d.dish_name ilike '%");
            nameWhereClause.append(criteria.getNameFragment());
            nameWhereClause.append("%'");
        }
        if (criteria.hasSorting()) {
            // sort key or default
            DishSortKey key = criteria.getSortKey() != null ? criteria.getSortKey() : DishSortKey.CreatedOn;
            DishSortDirection direction = criteria.getSortDirection() != null ? criteria.getSortDirection() : DishSortDirection.ASC;
            sortClause.append(" order by ");
            sortClause.append(columnForSortKey(key));
            sortClause.append(" ");
            sortClause.append(direction);
            sortClause.append(" NULLS LAST");
        }

        String sql = sqlBase + whereClause + nameWhereClause + groupByFilter + sortClause;
        logger.debug("Querying dishes: sql [{}], parameters [{}]", sql, parameters);
        return this.jdbcTemplate.query(sql, parameters, new DishMapper());
    }

    private List<String> getFilterClauses(List<Long> filterTagIds, boolean isExclude, Long userId) {
        List<String> collectedClauses = new ArrayList<>();
        String whenWeight = isExclude ? "-900" : "1";

        // pull ratings tags
        Map<Long, List<Long>> ratingTags = tagStructureService.getRatingsWithSiblingsByPower(filterTagIds, isExclude, userId);
        collectedClauses.addAll(tagsToFilterClause(ratingTags, whenWeight));


        Set<Long> tagIds = filterTagIds.stream()
                .filter(t -> !ratingTags.containsKey(t))
                .collect(Collectors.toSet());
        if (!tagIds.isEmpty()) {
            Map<Long, List<Long>> otherTags = tagStructureService.getDescendantTagIds(tagIds, userId);
            collectedClauses.addAll(tagsToFilterClause(otherTags, whenWeight));
        }

        return collectedClauses;
    }

    private List<String> tagsToFilterClause(Map<Long, List<Long>> ratingTags, String whenWeight) {
        List<String> collectedClauses = new ArrayList<>();
        for (Map.Entry<Long, List<Long>> entry : ratingTags.entrySet()) {
            var tagList = String.join(", ", entry.getValue().stream().map(String::valueOf).collect(Collectors.toList()));
            var clause = String.format("when tag_id in (%s) then %s", tagList, whenWeight);
            collectedClauses.add(clause);
        }
        return collectedClauses;
    }

    private String columnForSortKey(DishSortKey key) {
        switch (key) {
            case Name:
                return " lower(d.dish_name)";
            case LastUsed:
                return " d.last_added";
            case CreatedOn:
                return " d.dish_id";
        }
        return " d.dish_id";
    }

    @Override
    public List<DishTagSearchResult> retrieveDishResultsForTags(Long userId, TargetSlotEntity targetSlotEntity, int size, List<String> tagListForSlot, Map<Long, List<Long>> searchGroups, List<Long> sqlFilteredDishes) {
        // create sql
        Object[] sqlAndParams = createSqlForDishTagSearchResult(tagListForSlot, searchGroups, sqlFilteredDishes);
        String sql = (String) sqlAndParams[0];
        Map<String, Object> params = (Map<String, Object>) sqlAndParams[1];
        // create parameters
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("userId", userId);
        parameters.addValue("slotTagId", targetSlotEntity.getSlotDishTagId());
        params.forEach(parameters::addValue);

        return this.jdbcTemplate.query(sql, parameters, new DishTagSearchResultMapper(size, tagListForSlot.size()));

    }

    private Object[] createSqlForDishTagSearchResult(List<String> tagListForSlot, Map<Long, List<Long>> searchGroups, List<Long> sqlFilteredDishes) {
        Object[] returnvalue = new Object[2];
        Map<String, Object> parameters = new HashMap<>();

        StringBuilder selectClause = new StringBuilder("select distinct dt.dish_id , d.last_added ");
        StringBuilder outerJoins = new StringBuilder();
        StringBuilder orderByClause = new StringBuilder(" order by d.last_added NULLS FIRST ");
        // construct basic joins and from clause
        String fromClause = " from dish_items dt join dish d on d.dish_id = dt.dish_id and d.user_id = :userId and dt.tag_id = :slotTagId";
        String groupByClause = " group by dt.dish_id, d.last_added ";
        // construct outerJoins and add to selectClause
        int i = 0;
        for (String id : tagListForSlot) {
            selectClause.append(", count(iT")
                    .append(i)
                    .append(".tag_id )");

            outerJoins.append(" left outer join dish_items iT")
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
            sqlFilteredDishes.forEach(d -> whereClause.append(d).append(","));
            whereClause.setLength(whereClause.length() - 1);
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
            String dishName = rs.getString(2);
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
