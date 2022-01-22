package com.meg.listshop.lmt.service.task;

import com.meg.listshop.Application;
import com.meg.listshop.auth.data.entity.UserDeviceEntity;
import com.meg.listshop.auth.data.repository.UserDeviceRepository;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by margaretmartin on 21/03/2018.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@Sql(value = {"/sql/com/meg/atable/lmt/service/task/ExpiredTokenCleanupTaskTest.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sql/com/meg/atable/lmt/service/task/ExpiredTokenCleanupTaskTest-rollback.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ExpiredTokenCleanupTaskTest {

    @ClassRule
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    @Autowired
    private ExpiredTokenCleanupTask expiredTokenCleanupTask;

    @Autowired
    private UserDeviceRepository userDeviceRepository;

    @Test
    public void testCleanupTask() {
        LocalDate testRemoveDate = LocalDate.now().minusDays(12);

        // 3 items in test data set which are stale
        // get all tags
        List<UserDeviceEntity> alltokens = userDeviceRepository.findAll();
        // count them
        long count = alltokens.size();

        // call cleanup task
        expiredTokenCleanupTask.removeExpiredLogins();

        // get all tags and count them
        alltokens = userDeviceRepository.findAll();
        long newCount = alltokens.stream().count();

        // count should be 3 less
        Assert.assertEquals(count - 1, newCount);
    }

}