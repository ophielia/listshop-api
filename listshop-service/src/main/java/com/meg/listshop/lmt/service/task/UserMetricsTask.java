package com.meg.listshop.lmt.service.task;

import com.meg.listshop.auth.data.repository.UserRepository;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

/**
 * Created by margaretmartin on 30/10/2017.
 */
@Component
public class UserMetricsTask {

    private static final Logger logger = LoggerFactory.getLogger(UserMetricsTask.class);


    @Value("${web.login.period.in.days:8}")
    int deleteLoginsOlderThan;


    UserRepository userRepository;

    public static final String LAST_DAY_TAG = "last_day";
    public static final String LAST_WEEK_TAG = "last_week";
    public static final String LAST_MONTH_TAG = "last_month";

    @Autowired
    public UserMetricsTask(UserRepository userDeviceRepository) {
        this.userRepository = userDeviceRepository;
    }

    @Scheduled(cron = "${metrics.cron.expression:0 0 */4 * * *}")
    public void gatherUserMetrics() {
        logger.info("About to gather metrics for users.");

        LocalDate startDate = LocalDate.now();

        gatherStatsLoggedInUsers(startDate);

        gatherStatsActiveUsers(startDate);

        gatherStatsTotalUserCount();

        logger.info("UserMetricsTask complete.");
    }

    private void gatherStatsLoggedInUsers(LocalDate startDate) {
        logger.info("-- begin gatherStatsLoggedInUsers ");
        // last day
        publishLoggedInByClient(startDate.minusDays(1), LAST_DAY_TAG);
        publishLoggedIn(startDate.minusDays(1), LAST_DAY_TAG);
        // last week
        publishLoggedInByClient(startDate.minusDays(7), LAST_WEEK_TAG);
        publishLoggedIn(startDate.minusDays(7), LAST_WEEK_TAG);
        // last month
        publishLoggedInByClient(startDate.minusDays(30), LAST_MONTH_TAG);
        publishLoggedIn(startDate.minusDays(30), LAST_MONTH_TAG);


        logger.info("-- end gatherStatsLoggedInUsers ");
    }

    private void gatherStatsActiveUsers(LocalDate startDate) {
        logger.info("-- begin gatherStatsActiveUsers ");
        // last day
        publishActive(startDate.minusDays(1), LAST_DAY_TAG);
        // last week
        publishActive(startDate.minusDays(7), LAST_WEEK_TAG);
        // last month
        publishActive(startDate.minusDays(30), LAST_MONTH_TAG);


        logger.info("-- end gatherStatsActiveUsers ");
    }

    private void gatherStatsTotalUserCount() {
        logger.info("-- begin gatherStatsTotalUserCount ");

        Integer count = userRepository.getTotalUserCount();
        var metricName = String.format("users.total");
        Metrics.gauge(metricName, count);

        logger.info("-- end gatherStatsTotalUserCount ");
    }

    private void publishLoggedInByClient(LocalDate date, String timeTag) {
        Map<String, Integer> loggedInByClient = userRepository.getLoggedInUserCountByClient(date);

        loggedInByClient.forEach((key, value) -> {
            var metricName = String.format("users.logged-in.%s.%s", key, timeTag);
            Metrics.gauge(metricName, value);
        });
    }

    private void publishLoggedIn(LocalDate date, String timeTag) {
        Integer count = userRepository.getLoggedInUserCount(date);
        var metricName = String.format("users.logged-in.%s", timeTag);
        Metrics.gauge(metricName, count);
    }

    private void publishActive(LocalDate date, String timeTag) {
        Integer count = userRepository.getActiveUserCount(date);
        var metricName = String.format("users.active.%s", timeTag);
        Metrics.gauge(metricName, count);
    }


}
