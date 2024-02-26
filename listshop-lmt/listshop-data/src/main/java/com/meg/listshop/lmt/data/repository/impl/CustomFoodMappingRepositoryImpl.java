package com.meg.listshop.lmt.data.repository.impl;

import com.meg.listshop.lmt.data.pojos.FoodMappingDTO;
import com.meg.listshop.lmt.data.repository.CustomFoodMappingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by margaretmartin on 21/10/2017.
 */
@Repository
public class CustomFoodMappingRepositoryImpl implements CustomFoodMappingRepository {

    private static final Logger logger = LoggerFactory.getLogger(CustomFoodMappingRepositoryImpl.class);

    private final String FOOD_MAPPING_DTO = "select t.tag_id, t.name as tag_name," +
            "    m.category_id, c.name as category_name from tag t\n" +
            "    left outer join food_category_mapping m on m.tag_id = t.tag_id\n" +
            "    left outer join food_categories c on c.category_id = m.category_id\n" +
            "where is_group = true and t.tag_type = 'Ingredient'" +
            " order by lower(t.name)";
    NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public CustomFoodMappingRepositoryImpl(
            DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public List<FoodMappingDTO> retrieveAllFoodMappingDTOs() {
        StringBuilder sql = new StringBuilder(FOOD_MAPPING_DTO);

        return this.jdbcTemplate.query(sql.toString(), new CustomFoodMappingRepositoryImpl.FoodMappingMapper());

    }

    private static final class FoodMappingMapper implements RowMapper<FoodMappingDTO> {
        @Override
        public FoodMappingDTO mapRow(ResultSet rs, int i) throws SQLException {
            Long tagId = rs.getLong("tag_id");
            String tagName = rs.getString("tag_name");
            Long categoryId = rs.getLong("category_id");
            String categoryName = rs.getString("category_name");

            return new FoodMappingDTO(tagId, tagName, categoryId, categoryName);
        }
    }
}
