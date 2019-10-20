package com.meg.atable.lmt.service.impl;

import com.meg.atable.Application;
import com.meg.atable.lmt.data.entity.ListTagStatistic;
import com.meg.atable.lmt.service.ListTagStatisticService;
import com.meg.atable.test.TestConstants;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@Transactional
@Sql(value = {"/sql/com/meg/atable/lmt/service/impl/ListTagStatisticServiceTest-rollback.sql",
        "/sql/com/meg/atable/lmt/service/impl/ListTagStatisticServiceTest.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ListTagStatisticServiceImplTest {

    @Autowired
    ListTagStatisticService listTagStatisticService;

    @Test
    public void testGetStatisticsForUser() {
        List<ListTagStatistic> list = listTagStatisticService.getStatisticsForUser(TestConstants.USER_3_ID, 100);
        Assert.assertNotNull(list);
        Assert.assertEquals(3, list.size());
    }

}