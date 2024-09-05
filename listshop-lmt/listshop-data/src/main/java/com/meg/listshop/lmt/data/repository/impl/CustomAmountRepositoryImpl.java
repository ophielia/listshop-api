package com.meg.listshop.lmt.data.repository.impl;

import com.meg.listshop.conversion.data.pojo.DomainType;
import com.meg.listshop.lmt.data.pojos.SuggestionDTO;
import com.meg.listshop.lmt.data.repository.CustomAmountRepository;
import com.meg.listshop.lmt.data.repository.UnitSearchCriteria;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Repository
public class CustomAmountRepositoryImpl implements CustomAmountRepository {

    private static final Logger logger = LoggerFactory.getLogger(CustomAmountRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public CustomAmountRepositoryImpl(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

    }

    public List<SuggestionDTO> getNonUnitSuggestions() {
        Session session = entityManager.unwrap(Session.class);
        Query query = session.createNamedQuery("NonUnitSuggestions", SuggestionDTO.class);
        return query.getResultList();
    }

    public List<SuggestionDTO> getUnitSuggestionsByIds(Set<Long> unitIds) {
        if (unitIds == null || unitIds.isEmpty()) {
            return new ArrayList<>();
        }
        Session session = entityManager.unwrap(Session.class);
        Query query = session.createNamedQuery("UnitSuggestions", SuggestionDTO.class);
        query.setParameter("unitIds", unitIds);
        return query.getResultList();
    }

    @Override
    public Set<Long> getUnitIdsForCriteria(UnitSearchCriteria criteria) {
        logger.debug("Retrieving units for criteria");

        MapSqlParameterSource parameters = new MapSqlParameterSource();

        String sqlBase = "select distinct u.unit_id from units u ";
        String domainJoin = null;
        StringBuilder whereClause = new StringBuilder("where u.is_tag_specific is not true ");

        if (criteria.getDomainType() != null && criteria.getDomainType() != DomainType.ALL) {
            domainJoin = "join domain_unit du on du.unit_id = u.unit_id ";
            StringBuilder builder = new StringBuilder(" and du.domain_type = '");
            builder.append(criteria.getDomainType().name());
            builder.append("' ");
            whereClause.append(builder);
        }
        if (criteria.getLiquid() != null) {
            StringBuilder builder = new StringBuilder(" and u.is_liquid = ");
            builder.append(criteria.getLiquid());
            builder.append(" ");
            whereClause.append(builder);
        }

        StringBuilder sql = new StringBuilder(sqlBase);
        if (domainJoin != null) {
            sql.append(domainJoin);
        }
        sql.append(whereClause);

        logger.debug("Querying dishes: sql [{}], parameters [{}]", sql, parameters);
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql.toString(), parameters);

        return results.stream()
                .flatMap(m -> m.values().stream())
                .map(Long.class::cast)
                .collect(Collectors.toSet());

    }

}
