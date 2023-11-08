package com.meg.listshop.lmt.service.task;

import com.meg.listshop.auth.api.model.ClientType;
import com.meg.listshop.auth.data.repository.UserDeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(ExpiredTokenCleanupTask.class);


    @Value("${web.login.period.in.days:8}")
    int deleteWebLoginsOlderThan;

    @Value("${mobile.login.period.in.days:8}")
    int deleteMobileLoginsOlderThan;
    UserDeviceRepository userDeviceRepository;

    @Autowired
    public ExpiredTokenCleanupTask(UserDeviceRepository userDeviceRepository) {
        this.userDeviceRepository = userDeviceRepository;
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void removeExpiredLogins() {
        logger.info("About to delete expired logins from user_device table.");
        LocalDate removeWebLoginBeforeDate = LocalDate.now().minusDays(deleteWebLoginsOlderThan);
        LocalDate removeMobileLoginBeforeDate = LocalDate.now().minusDays(deleteMobileLoginsOlderThan);

        long deleted = userDeviceRepository.deleteByLastLoginBeforeAndClientTypeEquals(Date.valueOf(removeWebLoginBeforeDate), ClientType.Web);
        logger.info("... found [" + deleted + "] web tokens to delete.");

        long deletedMobile = userDeviceRepository.deleteByLastLoginBeforeAndClientTypeEquals(Date.valueOf(removeMobileLoginBeforeDate), ClientType.Mobile);
        logger.info("... found [" + deleted + "] mobile tokens to delete.");

        logger.info("StaleItemCleanupTask complete.");
    }


}
