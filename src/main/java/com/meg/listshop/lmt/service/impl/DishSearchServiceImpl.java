package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.lmt.api.model.DishSortDirection;
import com.meg.listshop.lmt.api.model.DishSortKey;
import com.meg.listshop.lmt.data.entity.DishEntity;
import com.meg.listshop.lmt.data.entity.TargetSlotEntity;
import com.meg.listshop.lmt.service.DishSearchCriteria;
import com.meg.listshop.lmt.service.DishSearchService;
import com.meg.listshop.lmt.service.DishTagSearchResult;
import com.meg.listshop.lmt.service.tag.TagStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

    @PersistenceContext
    private EntityManager entityManager;

    private TagStructureService tagStructureService;

    private NamedParameterJdbcTemplate jdbcTemplate;

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
        String sqlBase = "select d.* from dish d ";
        StringBuilder includedWith = new StringBuilder("");
        StringBuilder includedJoin = new StringBuilder("");
        StringBuilder whereClause = new StringBuilder("where d.user_id = :userId ");
        StringBuilder excludeWhereClause = new StringBuilder("");
        StringBuilder nameWhereClause = new StringBuilder("");
        StringBuilder sortClause = new StringBuilder(" ");
        HashSet<Long> allTagIds = getAllTagIdsForCriteria(criteria);
        Map<Long, List<Long>> groupDictionary = tagStructureService.getSearchGroupsForTagIds(allTagIds);
        if (!criteria.getIncludedTagIds().isEmpty()) {
            Set<Long> includeTagList = tagIdsWithSearchTags(criteria.getIncludedTagIds(), groupDictionary);
            String includeTagQueryString = String.join(", ", includeTagList.stream().map(String::valueOf).collect(Collectors.toList()));
            int setCount = includeTagList.size();
            // right now, just as strings - no named parameters
            includedWith.append("with included as (");
            includedWith.append("select dish_id, count(distinct tag_id) from dish_tags ");
            includedWith.append("where tag_id in ( ");
            includedWith.append(includeTagQueryString);
            includedWith.append(")");
            includedWith.append("group by dish_id having count(distinct tag_id) = ");
            includedWith.append(setCount);
            includedWith.append(")");

            includedJoin.append(" join included i using (dish_id) ");

        }
        if (!criteria.getExcludedTags().isEmpty()) {
            Set<Long> excludeTagList = tagIdsWithSearchTags(criteria.getIncludedTagIds(), groupDictionary);
            String excludeTagQueryString = String.join(", ", excludeTagList.stream().map(String::valueOf).collect(Collectors.toList()));


            excludeWhereClause.append("and d.dish_id not in (  ");
            excludeWhereClause.append("select dish_id from dish_tags ");
            excludeWhereClause.append("        where tag_id in (");
            excludeWhereClause.append(excludeTagQueryString);
            excludeWhereClause.append(")");

        }
        if (!StringUtils.isEmpty(criteria.getNameFragment())) {
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

        String sql = includedWith + sqlBase + includedJoin + whereClause + excludeWhereClause + nameWhereClause + sortClause;

        return this.jdbcTemplate.query(sql, parameters, new DishMapper());
    }

    private Set<Long> tagIdsWithSearchTags(List<Long> tagIdList, Map<Long, List<Long>> groupDictionary) {
        // get dictionary for tag_ids
        Set<Long> tagIdsForInclude = new HashSet(tagIdList);
        tagIdList.forEach(tagid -> {
            if (groupDictionary.containsKey(tagid)) {
                tagIdsForInclude.addAll(groupDictionary.get(tagid).stream().collect(Collectors.toList()));
            }
        });
        return tagIdsForInclude;
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
    public List<DishTagSearchResult> retrieveDishResultsForTags(Long userId, TargetSlotEntity targetSlotEntity, int size, List<String> tagListForSlot, Map<Long, List<Long>> searchGroups, List<Long> sqlFilteredDishes) {
        // create sql
        Object[] sqlAndParams = createSqlForDishTagSearchResult(tagListForSlot, searchGroups, sqlFilteredDishes);
        String sql = (String) sqlAndParams[0];
        Map<String, Object> params = (Map<String, Object>) sqlAndParams[1];
        // create parameters
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("userId", userId);
        parameters.addValue("slotTagId", targetSlotEntity.getSlotDishTagId());
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
        String fromClause = " from dish_tags dt join dish d on d.dish_id = dt.dish_id and d.user_id = :userId and dt.tag_id = :slotTagId";
        String groupByClause = " group by dt.dish_id, d.last_added ";
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
