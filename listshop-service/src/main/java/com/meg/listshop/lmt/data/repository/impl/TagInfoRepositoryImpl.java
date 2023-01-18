package com.meg.listshop.lmt.data.repository.impl;

import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.data.pojos.TagInfoDTO;
import com.meg.listshop.lmt.data.repository.TagInfoCustomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class TagInfoRepositoryImpl implements TagInfoCustomRepository {

    NamedParameterJdbcTemplate jdbcTemplate;

    private static final String TAG_INFO_PREFIX = "with test as (select tag_id, is_group, power, tr.parent_tag_id, name, tag_type, user_id, " +
            "case when user_id is not null then tag_id end as user_tag_id, case when user_id is null then tag_id end as standard_tag_id," +
            " case when user_id is not null then parent_tag_id end as user_parent_id, " +
            "case when user_id is null then parent_tag_id end as standard_parent_id " +
            "from tag t left join tag_relation tr on t.tag_id = tr.child_tag_id and tr.parent_tag_id is not null where ";
    private static final String STANDARD_FILTER = "t.user_id is null ";
    private static final String SINGLE_USER_FILTER = "(t.user_id is null or t.user_id = :userId) ";
    private static final String TAG_INFO_SUFFIX = "), consolidated_user_list as (select name, is_group, power, tag_type," +
            "coalesce(max(user_tag_id), max(standard_tag_id)) as tag_id, coalesce(max(user_parent_id), " +
            "max(standard_parent_id)) as parent_tag_id ,  max(user_id) as user_id from test group by 1,2 ,3,4) " +
            "select tag.tag_id, tag.name, tag.description, tag.power , tag.tag_type, tag.is_group, cl.parent_tag_id as parent_id," +
            "tag.to_delete , cl.user_id from tag join consolidated_user_list cl using (tag_id) ";

    private static final String TAG_TYPE_FILTER = " where tag.tag_type in (:tagTypes) ";

    private static final String RATING_TAGS_FOR_DISH_QUERY = "select t.tag_id, " +
            "       t.name, " +
            "       t.description, " +
            "       t.power, " +
            "       t.tag_type, " +
            "       t.is_group, " +
            "       tr.parent_tag_id as parent_id, " +
            "       t.to_delete, " +
            "       t.user_id " +
            "from tag t " +
            "join tag_relation tr on tr.child_tag_id = t.tag_id " +
            "join dish_tags d on d.tag_id = t.tag_id " +
            "where d.dish_id = :dishId" +
            " and t.tag_type = 'Rating'";

    @Autowired
    public TagInfoRepositoryImpl(
            DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public List<TagInfoDTO> retrieveTagInfoByUser(Long userId, List<TagType> tagTypes) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        // construct sql
        StringBuilder sql = new StringBuilder(TAG_INFO_PREFIX);
        if (userId == null) {
            sql.append(STANDARD_FILTER);
        } else {
            sql.append(SINGLE_USER_FILTER);
        }
        sql.append(TAG_INFO_SUFFIX);


        if (userId != null) {
            parameters.addValue("userId", userId);
        }

        if (!tagTypes.isEmpty()) {
            sql.append(TAG_TYPE_FILTER);
            var tagTypesAsStrings = tagTypes.stream().map(Enum::toString).collect(Collectors.toList());
            parameters.addValue("tagTypes", tagTypesAsStrings);
        }

        return this.jdbcTemplate.query(sql.toString(), parameters, new TagInfoRepositoryImpl.TagInfoMapper());

    }

    public List<TagInfoDTO> retrieveRatingInfoForDish(Long dishId) {
        if (dishId == null) {
            throw new IllegalStateException("can't get rating id for empty dish id");
        }
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        // construct sql
        StringBuilder sql = new StringBuilder(RATING_TAGS_FOR_DISH_QUERY);


        parameters.addValue("dishId", dishId);

        return this.jdbcTemplate.query(sql.toString(), parameters, new TagInfoRepositoryImpl.TagInfoMapper());

    }

    private static final class TagInfoMapper implements RowMapper<TagInfoDTO> {

        public TagInfoDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            Long id = rs.getLong("tag_id");
            Long userId = rs.getLong("user_id");
            String name = rs.getString("name");
            String description = rs.getString("description");
            Double power = rs.getDouble("power");
            String tagType = rs.getString("tag_type");
            Boolean isGroup = rs.getBoolean("is_group");
            Long parentId = rs.getLong("parent_id");
            Boolean toDelete = rs.getBoolean("to_delete");

            return new TagInfoDTO(
                    id, name, description, power, userId, tagType, isGroup, parentId, toDelete
            );
        }
    }

}


