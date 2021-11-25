package com.meg.listshop.lmt.service.task;

import com.meg.listshop.auth.data.repository.UserDeviceRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.sql.Date;
import java.time.LocalDate;

/**
 * Created by margaretmartin on 30/10/2017.
 */
@Component
public class ExpiredTokenCleanupTask {

    private static final Logger logger = LogManager.getLogger(ExpiredTokenCleanupTask.class);


    @Value("${web.login.period.in.days:8}")
    int deleteLoginsOlderThan;

    @Autowired
    UserDeviceRepository userDeviceRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void removeExpiredLogins() {
        logger.info("About to delete expired logins from user_device table.");
        LocalDate removLoginBeforeDate = LocalDate.now().minusDays(deleteLoginsOlderThan);

        long deleted = userDeviceRepository.deleteByLastLoginBefore(Date.valueOf(removLoginBeforeDate));
        logger.info("... found [" + deleted + "] items to delete.");

        logger.info("StaleItemCleanupTask complete.");
    }


}
