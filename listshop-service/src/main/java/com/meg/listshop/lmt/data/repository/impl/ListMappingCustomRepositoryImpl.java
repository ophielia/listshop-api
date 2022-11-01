package com.meg.listshop.lmt.data.repository.impl;

import com.meg.listshop.lmt.data.pojos.ItemMappingDTO;
import com.meg.listshop.lmt.data.repository.ListMappingCustomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Repository
public class ListMappingCustomRepositoryImpl implements ListMappingCustomRepository {

    private static final String STANDARD_MAPPING_QUERY =
            "select i.item_id, i.added_on, i.removed_on, i.crossed_off, i.updated_on, i.tag_id, " +
                    "       t.name as tagname, i.used_count, i.dish_sources, i.list_sources,lc.category_id, " +
                    "       lc.name as categoryname,lc.display_order,null as user_category_id,null as user_category_name,0 as user_display_order" +
                    " from list_item i " +
                    "         join tag t on i.tag_id = t.tag_id " +
                    "         join category_tags ct on t.tag_id = ct.tag_id " +
                    "         join list_category lc on ct.category_id = lc.category_id " +
                    "         join list_layout ll on lc.layout_id = ll.layout_id " +
                    "where list_id = :list_id " +
                    "  and ll.user_id is null " +
                    "  and ll.is_default = true";
    private static final String USER_MAPPING_QUERY_PART_1 =
            "select i.item_id, i.added_on, i.removed_on, i.crossed_off, i.updated_on, i.tag_id, " +
                    "       t.name as tagname, i.used_count, i.dish_sources, i.list_sources, min(lc.category_id) as category_id, min(lc.name) as categoryname, min(lc.display_order) as display_order , " +
                    "       min(uc.category_id) as user_category_id, min(uc.name) as user_category_name, min(uc.display_order) as user_display_order " +
                    " from list_item i " +
                    "         join tag t on i.tag_id = t.tag_id " +
                    "         join category_tags ct on t.tag_id = ct.tag_id " +
                    "         join list_category lc on ct.category_id = lc.category_id " +
                    "         join list_layout ll on lc.layout_id = ll.layout_id " +
                    "         left outer join category_tags ut on t.tag_id = ut.tag_id " +
                    "         left outer join list_category uc on ut.category_id = uc.category_id and uc.layout_id =   ";

    public static final String USER_MAPPING_QUERY_PART_2 =
            " where list_id = :list_id " +
                    "  and ll.user_id is null " +
                    "  and ll.is_default = true " +
                    "group by 1,2,3,4,5,6,7,8,9,10 ";
    NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public ListMappingCustomRepositoryImpl(
            DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public List<ItemMappingDTO> getListMappings(Long userLayoutId, Long shoppingListId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        // construct sql
        String sql = retrieveAppropriateSQL(userLayoutId);

        parameters.addValue("list_id", shoppingListId);

        return this.jdbcTemplate.query(sql, parameters, new ListMappingCustomRepositoryImpl.ItemMappingDTOMapper());
    }

    private String retrieveAppropriateSQL(Long userLayoutId) {
        if (userLayoutId == null) {
            // this query should use the default mappings only
            return STANDARD_MAPPING_QUERY;
        }
        return new StringBuffer(USER_MAPPING_QUERY_PART_1)
                .append(userLayoutId)
                .append(USER_MAPPING_QUERY_PART_2)
                .toString();
    }

    private static final class ItemMappingDTOMapper implements RowMapper<ItemMappingDTO> {

        public ItemMappingDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            Long itemId = rs.getLong("item_id");
            Date addedOn = rs.getDate("added_on");
            Date removedOn = rs.getDate("removed_on");
            Date crossedOffOn = rs.getDate("crossed_off");
            Date updatedOn = rs.getDate("updated_on");
            Long tagId = rs.getLong("tag_id");
            String tagName = rs.getString("tagname");
            int usedCount = rs.getInt("used_count");
            String rawDishSources = rs.getString("dish_sources");
            String rawListSources = rs.getString("list_sources");
            Long categoryId = rs.getLong("category_id");
            String categoryName = rs.getString("categoryname");
            int displayOrder = rs.getInt("display_order");
            Long userCategoryId = rs.getLong("user_category_id");
            String userCategoryName = rs.getString("user_category_name");
            int userDisplayOrder = rs.getInt("user_display_order");


            return new ItemMappingDTO(
                    itemId, addedOn, removedOn, crossedOffOn, updatedOn, tagId, tagName, usedCount, rawDishSources, rawListSources, categoryId, categoryName, displayOrder, userCategoryId, userCategoryName, userDisplayOrder
            );

        }
    }

}


