package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.Application;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.api.model.ContextType;
import com.meg.listshop.lmt.api.model.Statistic;
import com.meg.listshop.lmt.api.model.StatisticCountType;
import com.meg.listshop.lmt.data.entity.ListItemEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.list.ListItemCollector;
import com.meg.listshop.lmt.list.ListTagStatisticService;
import com.meg.listshop.lmt.service.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.*;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@Testcontainers
@ActiveProfiles("test")
@Sql(value = {"/sql/com/meg/atable/lmt/service/impl/ListTagStatisticServiceTest-rollback.sql",
        "/sql/com/meg/atable/lmt/service/impl/ListTagStatisticServiceTest.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ListTagStatisticServiceImplTest {

    @Container
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;


    @Autowired
    ListTagStatisticService listTagStatisticService;



    @Test
    @Sql(value = {"/sql/com/meg/atable/lmt/service/impl/ListTagStatisticServiceTest-rollback.sql"})
    void createStatisticsForUser_AllAdd() {
        List<Long> tagIds = Arrays.asList(16L, 18L, 20L);
        Long userId = 1L;
        List<Statistic> stats = dummyStatistics(tagIds);
        UserEntity user = new UserEntity();
        user.setId(userId);

        // call
        listTagStatisticService.createStatisticsForUser(user, stats);

    }


    @Test
    void legacyProcessCollectorStatistics_NoChange() {
        CollectorContext context = new CollectorContextBuilder()
                .create(ContextType.Item)
                .withStatisticCountType(StatisticCountType.Single)
                .build();

        ListItemCollector mockCollector = Mockito.mock(ListItemCollector.class);

        Mockito.when(mockCollector.getCollectedTagItems())
                .thenReturn(new ArrayList<>());

        listTagStatisticService.legacyProcessCollectorStatistics(1L, mockCollector, context);

        Assertions.assertEquals(0, getFieldResultForTag("removed_dish", 18L, 99L).intValue());
    }

    @Test
    @Sql(value = {"/sql/com/meg/atable/lmt/service/impl/ListTagStatisticServiceTest-rollback.sql"})
    void legacyProcessCollectorStatistics_AddAndInsert() {
        List<Long> tagIds = Arrays.asList(16L, 18L, 20L);
        List<CollectedItem> items = dummyItems(tagIds);

        items.get(0).setAddedOn(LocalDateTime.now());
        items.get(0).setIsAdded(true);
        items.get(0).setChanged(true);

        CollectorContext context = new CollectorContextBuilder()
                .create(ContextType.Item)
                .withStatisticCountType(StatisticCountType.Single)
                .build();

        ListItemCollector mockCollector = Mockito.mock(ListItemCollector.class);

        Mockito.when(mockCollector.getCollectedTagItems())
                .thenReturn(items);
        Mockito.when(mockCollector.getAllTagIds())
                .thenReturn(tagIds);


        listTagStatisticService.legacyProcessCollectorStatistics(1L, mockCollector, context);

        Assertions.assertEquals(1, getFieldResultForTag("added_single", 16L, 1L).intValue());
        Assertions.assertEquals(0, getFieldResultForTag("added_single", 18L, 1L).intValue());
    }

    @Test
    @Sql(value = {"/sql/com/meg/atable/lmt/service/impl/ListTagStatisticServiceTest-Existing.sql"})
    void legacyProcessCollectorStatistics_AddWithExisting() {
        List<Long> tagIds = Arrays.asList(16L, 18L, 20L);
        List<CollectedItem> items = dummyItems(tagIds);

        items.get(0).setAddedOn(LocalDateTime.now());
        items.get(0).setIsAdded(true);
        items.get(0).setChanged(true);

        CollectorContext context = new CollectorContextBuilder()
                .create(ContextType.Item)
                .withStatisticCountType(StatisticCountType.List)
                .build();

        ListItemCollector mockCollector = Mockito.mock(ListItemCollector.class);

        Mockito.when(mockCollector.getCollectedTagItems())
                .thenReturn(items);
        Mockito.when(mockCollector.getAllTagIds())
                .thenReturn(tagIds);


        listTagStatisticService.legacyProcessCollectorStatistics(1L, mockCollector, context);

        Assertions.assertEquals(2, getFieldResultForTag("added_list", 16L, 1L).intValue());
        Assertions.assertEquals(1, getFieldResultForTag("added_list", 18L, 1L).intValue());
    }

    @Test
    @Sql(value = {"/sql/com/meg/atable/lmt/service/impl/ListTagStatisticServiceTest-Existing.sql"})
    void legacyProcessCollectorStatistics_RemoveWithExisting() {
        List<Long> tagIds = Arrays.asList(16L, 18L, 20L);
        List<CollectedItem> items = dummyItems(tagIds);

        items.get(0).setRemovedOn(LocalDateTime.now());
        items.get(0).setRemoved(true);
        items.get(0).setChanged(true);
        items.get(1).setRemovedOn(LocalDateTime.now());
        items.get(1).setRemoved(true);
        items.get(1).setChanged(true);

        CollectorContext context = new CollectorContextBuilder()
                .create(ContextType.Item)
                .withStatisticCountType(StatisticCountType.StarterList)
                .build();

        ListItemCollector mockCollector = Mockito.mock(ListItemCollector.class);

        Mockito.when(mockCollector.getCollectedTagItems())
                .thenReturn(items);
        Mockito.when(mockCollector.getAllTagIds())
                .thenReturn(tagIds);


        listTagStatisticService.legacyProcessCollectorStatistics(1L, mockCollector, context);

        Assertions.assertEquals(2, getFieldResultForTag("removed_starterlist", 16L, 1L).intValue());
        Assertions.assertEquals(1, getFieldResultForTag("removed_starterlist", 18L, 1L).intValue());
    }


    @Test
    @Sql(value = {"/sql/com/meg/atable/lmt/service/impl/ListTagStatisticServiceTest-Existing.sql"})
    void legacyProcessCollectorStatistics_RemoveIgnoresCrossedOff() {
        List<Long> tagIds = Arrays.asList(16L, 18L, 20L);
        List<CollectedItem> items = dummyItems(tagIds);

        items.get(0).setRemovedOn(LocalDateTime.now());
        items.get(0).setRemoved(true);
        items.get(0).setCrossedOff(LocalDateTime.now());
        items.get(0).setChanged(true);
        items.get(1).setRemovedOn(LocalDateTime.now());
        items.get(1).setRemoved(true);
        items.get(1).setChanged(true);

        CollectorContext context = new CollectorContextBuilder()
                .create(ContextType.Item)
                .withStatisticCountType(StatisticCountType.Dish)
                .build();

        ListItemCollector mockCollector = Mockito.mock(ListItemCollector.class);

        Mockito.when(mockCollector.getCollectedTagItems())
                .thenReturn(items);
        Mockito.when(mockCollector.getAllTagIds())
                .thenReturn(tagIds);


        listTagStatisticService.legacyProcessCollectorStatistics(1L, mockCollector, context);

        Assertions.assertEquals(1, getFieldResultForTag("removed_dish", 16L, 1L).intValue());
        Assertions.assertEquals(1, getFieldResultForTag("removed_dish", 18L, 1L).intValue());
    }

    @Test
    @Sql(value = {"/sql/com/meg/atable/lmt/service/impl/ListTagStatisticServiceTest-Existing.sql"})
    void legacyProcessCollectorStatistics_AddAndRemove() {
        List<Long> tagIds = Arrays.asList(16L, 18L, 20L);
        List<CollectedItem> items = dummyItems(tagIds);

        items.get(0).setRemovedOn(LocalDateTime.now());
        items.get(0).setRemoved(true);
        items.get(0).setChanged(true);
        items.get(1).setAddedOn(LocalDateTime.now());
        items.get(1).setIsAdded(true);
        items.get(1).setChanged(true);

        CollectorContext context = new CollectorContextBuilder()
                .create(ContextType.Item)
                .withStatisticCountType(StatisticCountType.Dish)
                .build();

        ListItemCollector mockCollector = Mockito.mock(ListItemCollector.class);

        Mockito.when(mockCollector.getCollectedTagItems())
                .thenReturn(items);
        Mockito.when(mockCollector.getAllTagIds())
                .thenReturn(tagIds);


        listTagStatisticService.legacyProcessCollectorStatistics(1L, mockCollector, context);

        Assertions.assertEquals(2, getFieldResultForTag("removed_dish", 16L, 1L).intValue());
        Assertions.assertEquals(1, getFieldResultForTag("added_dish", 18L, 1L).intValue());
    }

    @Test
    @Sql(value = {"/sql/com/meg/atable/lmt/service/impl/ListTagStatisticServiceTest-rollback.sql"})
    void legacyProcessCollectorStatistics_NoUpdateWithNone() {
        List<Long> tagIds = Arrays.asList(16L, 18L, 20L);
        List<CollectedItem> items = dummyItems(tagIds);

        items.get(0).setAddedOn(LocalDateTime.now());
        items.get(0).setIsAdded(true);
        items.get(0).setChanged(true);
        Long changedId = items.get(0).getTag().getId();

        CollectorContext context = new CollectorContextBuilder()
                .create(ContextType.Item)
                .withStatisticCountType(StatisticCountType.None)
                .build();

        ListItemCollector mockCollector = Mockito.mock(ListItemCollector.class);

        Mockito.when(mockCollector.getCollectedTagItems())
                .thenReturn(items);
        Mockito.when(mockCollector.getAllTagIds())
                .thenReturn(tagIds);


        listTagStatisticService.legacyProcessCollectorStatistics(1L, mockCollector, context);

        Assertions.assertEquals(0, getFieldResultForTag("removed_dish", 16L, 1L).intValue());
        Assertions.assertEquals(0, getFieldResultForTag("removed_dish", 18L, 1L).intValue());
    }


    private Integer getFieldResultForTag(String fieldName, Long tagId, Long userId) {
        Map<Long, Integer> resultMap = new HashMap<>();

        String sql = new StringBuilder("select ")
                .append(fieldName)
                .append(" from list_tag_stats where ")
                .append("user_id = :userid")
                .append(" and tag_id in (:tagid)").toString();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("userid", userId);
        parameters.put("tagid", tagId);

        List<Integer> resultList = jdbcTemplate.queryForList(sql, parameters, Integer.class);
        if (resultList != null && !resultList.isEmpty()) {
            return resultList.get(0);
        }
        return 0;
    }

    private List<CollectedItem> dummyItems(List<Long> tagIds) {
        List<CollectedItem> dummyItems = new ArrayList<>();
        for (Long id : tagIds) {
            TagEntity tag = new TagEntity();
            tag.setId(id);
            tag.setName(String.valueOf(id));
            ListItemEntity item = new ListItemEntity();
            item.setTag(tag);
            item.setUsedCount(1);
            dummyItems.add(new CollectedItem(item));
        }
        return dummyItems;
    }


    private List<Statistic> dummyStatistics(List<Long> tagIds) {
        List<Statistic> dummyStats = new ArrayList<>();
        for (Long dummyId : tagIds) {
            Statistic stat = new Statistic()
                    .addedCount(1)
                    .removedCount(1)
                    .tagId(dummyId);
            dummyStats.add(stat);
        }
        return dummyStats;
    }
}
