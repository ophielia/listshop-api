package com.meg.listshop.lmt.data.repository.impl;

import com.meg.listshop.lmt.data.entity.TagInfoDTO;
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

@Repository
public class TagInfoRepositoryImpl implements TagInfoCustomRepository {

    NamedParameterJdbcTemplate jdbcTemplate;

    private static final String tagInfoPrefix = "with test as (select tag_id,tr.parent_tag_id, name, tag_type, user_id, case when user_id is not null then tag_id end as user_tag_id, case when user_id is null then tag_id end as standard_tag_id, case when user_id is not null then parent_tag_id end as user_parent_id, case when user_id is null then parent_tag_id end as standard_parent_id from tag t left join tag_relation tr on t.tag_id = tr.child_tag_id and tr.parent_tag_id is not null where ";
    private static final String standardFilter = "t.user_id is null ";
    private static final String singleUserFilter = "(t.user_id is null or t.user_id = :userId) ";
    private static final String tagInfoSuffix = "), consolidated_user_list as (select name, tag_type,coalesce(max(user_tag_id), max(standard_tag_id)) as tag_id, coalesce(max(user_parent_id), max(standard_parent_id)) as parent_tag_id ,  max(user_id) as user_id from test group by 1,2 ) select tag.tag_id, tag.name, tag.description, power , tag.tag_type, is_group, cl.parent_tag_id as parent_id ,tag.to_delete , cl.user_id from tag join consolidated_user_list cl using (tag_id)";

    @Autowired
    public TagInfoRepositoryImpl(
            DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public List<TagInfoDTO> retrieveTagInfoByUser(Long userId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        // construct sql
        StringBuilder sql = new StringBuilder(tagInfoPrefix);
        if (userId == null) {
            sql.append(standardFilter);
        } else {
            sql.append(singleUserFilter);
        }
        sql.append(tagInfoSuffix);


        if (userId != null) {
            parameters.addValue("userId", userId);
        }

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

            TagInfoDTO tagInfo = new TagInfoDTO(
                    id, name, description, power, userId, tagType, isGroup, parentId, toDelete
            );

            return tagInfo;
        }
    }

}


