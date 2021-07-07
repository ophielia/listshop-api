package com.meg.listshop.lmt.data.repository.impl;

import com.meg.listshop.lmt.api.model.StatisticCountType;
import com.meg.listshop.lmt.data.repository.CustomStatisticRepository;
import com.meg.listshop.lmt.service.impl.StatisticOperationType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by margaretmartin on 21/10/2017.
 */
public class CustomStatisticRepositoryImpl implements CustomStatisticRepository {

    private static final Logger logger = LogManager.getLogger(CustomStatisticRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    private static final String MISSING_STAT_SELECT = "select t.tag_id  " +
            " from  tag t " +
            " left outer join list_tag_stats s on s.tag_id = t.tag_id " +
            " AND s.user_id = ?1" +
            " where  t.tag_id in (?2" +
            ")" +
            " AND s is null";

    public static final String FREQUENT_TAGIDS = "select i.tag_id  " +
            " from list_item i " +
            " join calculated_stats s using (tag_id) " +
            " where i.list_id = " +
            " ?1" +
            " and s.user_id = " +
            " ?2" +
            " and factored_frequency > frequent_threshold";

    public static final String INSERT_SINGLE_STATISTIC = "insert into list_tag_stats (list_tag_stat_id, tag_id, user_id, added_single, added_count, removed_single, removed_count) " +
            "select nextval('list_tag_stats_sequence'), " +
            "       tag_id, " +
            "       ?1 as user_id, " +
            "       ?2 as added_single, " +
            "       ?3 as added_count, " +
            "       ?4 as removed_single, " +
            "       ?5 as removed_count " +
            "from tag " +
            "where tag_id = ?6";

    public static final String INSERT_MULTIPLE_STATISTICS = "insert into list_tag_stats (list_tag_stat_id, tag_id, user_id) " +
            "select nextval('list_tag_stats_sequence'), " +
            "       tag_id, " +
            "       ?1 as user_id " +
            "from tag " +
            "where tag_id in (?2)";

    public List<Long> getTagIdsForMissingStats(Long userId, Iterable<Long> tagIds) {
        return entityManager.createNativeQuery(MISSING_STAT_SELECT)
                .setParameter(1, userId)
                .setParameter(2, tagIds)
                .getResultList();
    }

    public List<Long> getFrequentTagIds(Long userId, Long listId) {
        return entityManager.createNativeQuery(FREQUENT_TAGIDS)
                .setParameter(1, listId)
                .setParameter(2, userId)
                .getResultList();
    }

    public void insertSingleUserStatistic(Long userId, Long tagId, Integer addedSingle, Integer removedSingle) {
        logger.debug("In CustomStatisticRepositoryImpl - user_id:" + userId + " , tagId: " + tagId + ", addedSingle: " + addedSingle + ", removedSingle: " + removedSingle + ".");
        Integer removed = removedSingle == null ? 0 : removedSingle;
        Integer added = addedSingle == null ? 0 : addedSingle;
        entityManager.createNativeQuery(INSERT_SINGLE_STATISTIC)
                .setParameter(1, userId)
                .setParameter(2, added)
                .setParameter(3, added)
                .setParameter(4, removed)
                .setParameter(5, removed)
                .setParameter(6, tagId)
                .executeUpdate();
    }

    @Override
    public void insertEmptyUserStatistics(Long userId, List<Long> tagIds) {
        entityManager.createNativeQuery(INSERT_MULTIPLE_STATISTICS)
                .setParameter(1, userId)
                .setParameter(2, tagIds)
                .executeUpdate();
    }

    @Override
    public void updateUserStatistics(Long userId, List<Long> updateIds, StatisticOperationType operation, StatisticCountType countType) {
        var fieldPrefix = "added_";
        boolean isRemove = operation == StatisticOperationType.remove;
        if (isRemove) {
            fieldPrefix = "removed_";
        }

        String field = getFieldNameForContext(countType);
        if (field == null) {
            return;
        }
        var fieldName = new StringBuilder(fieldPrefix).append(field).toString();


        String sql = constructStatisticUpdateSql(!isRemove, fieldName);

        entityManager.createNativeQuery(sql)
                .setParameter(1, userId)
                .setParameter(2, updateIds)
                .executeUpdate();

    }

    private String constructStatisticUpdateSql(boolean isAdd, String fieldName) {
        String totalFieldName = isAdd ? "added_count" : "removed_count";

        StringBuilder builder = new StringBuilder("UPDATE list_tag_stats s SET ")
                .append(totalFieldName)
                .append(" = ")
                .append(fieldName)
                .append(" + 1, ")
                .append(fieldName)
                .append(" = ")
                .append(fieldName)
                .append(" + 1  WHERE s.user_id =  ?1 ")
                .append(" AND tag_id in ( ?2 ) ");
        return builder.toString();

    }

    private String getFieldNameForContext(StatisticCountType countType) {
        if (countType == null) {
            return null;
        }
        switch (countType) {
            case Dish:
                return "dish";
            case List:
                return "list";
            case Single:
                return "single";
            case StarterList:
                return "starterlist";
            default:
                return null;
        }

    }


}
