package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.lmt.api.model.Statistic;
import com.meg.listshop.lmt.data.entity.ListTagStatistic;
import com.meg.listshop.lmt.data.repository.ListTagStatisticRepository;
import com.meg.listshop.lmt.service.CollectedItem;
import com.meg.listshop.lmt.service.CollectorContext;
import com.meg.listshop.lmt.service.ItemCollector;
import com.meg.listshop.lmt.service.ListTagStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 20/10/2017.
 */
@Service
@Transactional
public class ListTagStatisticServiceImpl implements ListTagStatisticService {

    private static final String TAG_LIST_PARAM = "tagidlist";
    private static final String USER_ID_PARAMETER = "useridparam";
    private static final String TAG_ID_PARAMETER = "tagidparam";
    private static final String REMOVED_COUNT_PARAMETER = "removedcountparam";
    private static final String ADDED_COUNT_PARAMETER = "addedcountparam";

    private static final String MISSING_STAT_SELECT = "select t.tag_id  " +
            " from  tag t " +
            " left outer join list_tag_stats s on s.tag_id = t.tag_id " +
            " AND s.user_id = :" + USER_ID_PARAMETER +
            " where  t.tag_id in (:" + TAG_LIST_PARAM +
            ")" +
            " AND s is null";

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private ListTagStatisticRepository listTagStatisticRepo;

    @Override
    public void countTagAddedToDish(Long userId, Long tagId) {
        ListTagStatistic statistic = listTagStatisticRepo.findByUserIdAndTagId(userId, tagId);

        if (statistic == null) {
            statistic = new ListTagStatistic();
            statistic.setTagId(tagId);
            statistic.setUserId(userId);
        }
        int addedCount = statistic.getAddedToDishCount() != null ? statistic.getAddedToDishCount() : 0;
        statistic.setAddedToDishCount(addedCount + 1);
        listTagStatisticRepo.save(statistic);
    }

    @Override
    public void processCollectorStatistics(Long userId, ItemCollector collector, CollectorContext context) {
        // pull tagIds for removed and created tags
        List<Long> removedIds = new ArrayList<>();
        List<Long> addedIds = new ArrayList<>();
        collector.getCollectedTagItems().stream()
                .filter(CollectedItem::isChanged)
                .forEach(item -> {
                    if (item.isAdded() || item.isCountAdded()) {
                        addedIds.add(item.getTagId());
                    } else if ((item.isRemoved() || item.isCountDecreased()) && item.getCrossedOff() == null) {
                        removedIds.add(item.getTagId());
                    }
                });

        if (removedIds.isEmpty() && addedIds.isEmpty()) {
            return;
        }

        // check for and create missing statistics
        checkForAndCreateMissingStatistics(collector.getAllTagIds(), userId);

        // update removed
        if (!removedIds.isEmpty()) {
            updateStatistics(StatisticOperationType.remove, removedIds, context, userId);
        }
        // update added
        if (!addedIds.isEmpty()) {
            updateStatistics(StatisticOperationType.add, addedIds, context, userId);
        }
    }

    @Override
    public List<ListTagStatistic> getStatisticsForUser(Long userId, int resultLimit) {

        List<ListTagStatistic> statistics = listTagStatisticRepo.findByUserId(userId);
        if (statistics.size() > resultLimit) {
            return statistics.subList(0, resultLimit);
        }
        return statistics;
    }

    @Override
    public void createStatisticsForUser(UserEntity user, List<Statistic> statisticList) {
        // this is done from a context in which the user has just been created, and doesn't
        // have any statistics
        if (statisticList.isEmpty()) {
            return;
        }

        // put statistic objects into hash
        Map<Long, Statistic> statMap = statisticList.stream()
                .collect(Collectors.toMap(Statistic::getTagId, Function.identity()));

        // find which statistic objects already exist
        Map parameters = new HashMap<String, Object>();
        parameters.put(TAG_LIST_PARAM, statMap.keySet());
        parameters.put(USER_ID_PARAMETER, user.getId());

        List<Long> idsToInsert = jdbcTemplate.queryForList(MISSING_STAT_SELECT, parameters, Long.class);

        // return if nothing to insert
        if (idsToInsert == null) {
            return;
        }

        // insert all statistics
        for (Long insertId : idsToInsert) {
            Statistic insertStat = statMap.get(insertId);
            insertUserStatistic(insertId, user.getId(), insertStat.getAddedCount(), insertStat.getRemovedCount());
        }
        return;
    }

    @Override
    public List<Long> findFrequentIdsForList(Long listId, Long userId) {
        String sql = new StringBuilder("select i.tag_id  ")
                .append(" from list_item i ")
                .append(" join calculated_stats s using (tag_id) ")
                .append(" where i.list_id = ")
                .append(" :listId")
                .append(" and s.user_id = ")
                .append(" :userId")
                .append(" and factored_frequency > frequent_threshold;")
                .toString();

        Map<String, Long> parameters = new HashMap<>();
        parameters.put("listId", listId);
        parameters.put("userId", userId);
        return jdbcTemplate.queryForList(sql, parameters, Long.class);
    }

    private void insertUserStatistic(Long tagId, Long userId, Integer addedCount, Integer removedCount) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(USER_ID_PARAMETER, userId);
        parameters.put(TAG_ID_PARAMETER, userId);
        parameters.put(ADDED_COUNT_PARAMETER, addedCount);
        parameters.put(REMOVED_COUNT_PARAMETER, removedCount);

        String sql = constructStatisticInsertSql(tagId, userId, addedCount, removedCount);
        jdbcTemplate.update(sql, parameters);
    }

    private void updateStatistics(StatisticOperationType operation, List<Long> updateIds, CollectorContext context, Long userId) {
        String fieldPrefix = "added_";
        boolean isRemove = operation == StatisticOperationType.remove;
        if (isRemove) {
            fieldPrefix = "removed_";
        }

        String field = getFieldNameForContext(context);
        if (field == null) {
            return;
        }
        String fieldName = new StringBuilder(fieldPrefix).append(field).toString();


        String sql = constructStatisticUpdateSql(!isRemove, fieldName);

        Map updateParams = new HashMap<>();
        updateParams.put(USER_ID_PARAMETER, userId);
        updateParams.put(TAG_LIST_PARAM, updateIds);
        jdbcTemplate.update(sql, updateParams);


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
                .append(" + 1  WHERE s.user_id =  :")
                .append(USER_ID_PARAMETER)
                .append(" AND tag_id in ( :")
                .append(TAG_LIST_PARAM)
                .append(") ");
        return builder.toString();

    }

    private String constructStatisticInsertSql(Long userId) {
        StringBuilder builder = new StringBuilder("insert into list_tag_stats (list_tag_stat_id, tag_id, user_id) ")
                .append(" select nextval('list_tag_stats_sequence') , tag_id , ")
                .append(userId)
                .append(" as user_id")
                .append(" from tag where tag_id in (:")
                .append(TAG_LIST_PARAM)
                .append(");");
        return builder.toString();
    }

    private String constructStatisticInsertSql(Long tagId, Long userId, Integer addedCount, Integer removedCount) {
        StringBuilder builder = new StringBuilder("insert into list_tag_stats (list_tag_stat_id, tag_id, user_id, ")
                .append(" added_single, added_count, removed_single, removed_count) ")
                .append(" select nextval('list_tag_stats_sequence') , tag_id , :")
                .append(USER_ID_PARAMETER)
                .append(" as user_id, :")
                .append(ADDED_COUNT_PARAMETER)
                .append(" as added_single, :")
                .append(ADDED_COUNT_PARAMETER)
                .append(" as added_count, :")
                .append(REMOVED_COUNT_PARAMETER)
                .append(" as removed_single, :")
                .append(REMOVED_COUNT_PARAMETER)
                .append(" as removed_count")
                .append(" from tag where tag_id = :")
                .append(TAG_ID_PARAMETER)
                .append(";");
        return builder.toString();
    }

    private String getFieldNameForContext(CollectorContext context) {
        if (context.getStatisticCountType() == null) {
            return null;
        }
        switch (context.getStatisticCountType()) {
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

    private void checkForAndCreateMissingStatistics(List<Long> tagIds, Long userId) {
        Map parameters = new HashMap<String, Object>();
        parameters.put(TAG_LIST_PARAM, tagIds);
        parameters.put(USER_ID_PARAMETER, userId);

        List<Long> missingIds = jdbcTemplate.queryForList(MISSING_STAT_SELECT, parameters, Long.class);

        if (missingIds.isEmpty()) {
            return;
        }

        String insertSql = constructStatisticInsertSql(userId);
        Map insertParams = Collections.singletonMap(TAG_LIST_PARAM, missingIds);
        jdbcTemplate.update(insertSql, insertParams);
    }
}
