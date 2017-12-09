package com.meg.atable.service.impl;

import com.meg.atable.data.entity.DishEntity;
import com.meg.atable.service.DishSearchCriteria;
import com.meg.atable.service.DishSearchService;
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
            int i=0;
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
            int i=0;
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
/*
dish_id, description, dish_name, user_id, last_added
     */

}
