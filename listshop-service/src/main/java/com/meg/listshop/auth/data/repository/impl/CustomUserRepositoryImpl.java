package com.meg.listshop.auth.data.repository.impl;

import com.meg.listshop.auth.data.repository.CustomUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Transactional
public class CustomUserRepositoryImpl implements CustomUserRepository {

    private static final String LOGGED_IN_USERS_BY_CLIENT = "select lower(client_type) as client_type, count(distinct user_id) as clientcount " +
            "from user_devices " +
            "where last_login >= :dateLimit " +
            "group by 1;";
    private static final String LOGGED_IN_USERS = "select count(distinct user_id) " +
            "from user_devices " +
            "where last_login >= :dateLimit;";
    private static final String ACTIVE_USERS = "select count(distinct user_id) " +
            "from list_item li " +
            "join list l using (list_id) " +
            "where (added_on > :dateLimitAdd or updated_on > :dateLimitUpdate)";
    private static final String TOTAL_USERS = "select count(distinct user_id) from users;";
    @PersistenceContext
    private EntityManager entityManager;
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public void init(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public void deleteUser(Long userId) {
        var query = entityManager.createNativeQuery("delete from users where user_id = " + userId);
        query.setFlushMode(FlushModeType.COMMIT);
        query.executeUpdate();
    }

    @Override
    public Map<String, Integer> getLoggedInUserCountByClient(LocalDate dateLimit) {

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("dateLimit", dateLimit);

        String sql = LOGGED_IN_USERS_BY_CLIENT;

        List<Map<String, Object>> results = this.jdbcTemplate.queryForList(sql, parameters);
        Map<String, Integer> resultMap = new HashMap<>();

        if (results.isEmpty()) {
            resultMap.put("web", 0);
            resultMap.put("mobile", 0);
            return resultMap;
        }
        results.stream()
                .forEach(r -> {
                    var client = (String) r.get("client_type");
                    var count = (Long) r.get("clientcount");
                    resultMap.put(client, count.intValue());
                });
        return resultMap;

    }

    @Override
    public Integer getLoggedInUserCount(LocalDate dateLimit) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("dateLimit", dateLimit);

        String sql = LOGGED_IN_USERS;

        return this.jdbcTemplate.queryForObject(sql, parameters, Integer.class);
    }

    @Override
    public Integer getActiveUserCount(LocalDate dateLimit) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("dateLimitAdd", dateLimit);
        parameters.addValue("dateLimitUpdate", dateLimit);

        String sql = ACTIVE_USERS;

        return this.jdbcTemplate.queryForObject(sql, parameters, Integer.class);
    }

    @Override
    public Integer getTotalUserCount() {
        MapSqlParameterSource empty = new MapSqlParameterSource();
        String sql = TOTAL_USERS;

        return this.jdbcTemplate.queryForObject(sql, empty, Integer.class);
    }
}
