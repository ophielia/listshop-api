package com.meg.listshop.lmt.service.task;

import com.meg.listshop.Application;
import com.meg.listshop.auth.data.entity.UserDeviceEntity;
import com.meg.listshop.auth.data.repository.UserDeviceRepository;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by margaretmartin on 21/03/2018.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@Testcontainers
@ActiveProfiles("test")
@Sql(value = {"/sql/com/meg/atable/lmt/service/task/ExpiredTokenCleanupTaskTest.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sql/com/meg/atable/lmt/service/task/ExpiredTokenCleanupTaskTest-rollback.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ExpiredTokenCleanupTaskTest {

    @Container
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    @Autowired
    private ExpiredTokenCleanupTask expiredTokenCleanupTask;

    @Autowired
    private UserDeviceRepository userDeviceRepository;

    @Test
    void testCleanupTask() {
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
        long newCount = alltokens.size();

        // count should be 3 less
        Assertions.assertEquals(count - 3, newCount);
    }

}
