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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by margaretmartin on 30/10/2017.
 */
@Component
public class UserMetricsTask {

    private static final Logger logger = LoggerFactory.getLogger(UserMetricsTask.class);


    @Value("${web.login.period.in.days:8}")
    int deleteLoginsOlderThan;

    private final AtomicInteger usersTotal;
    private final AtomicInteger usersLoggedInWebLastDay;
    private final AtomicInteger usersLoggedInWebLastWeek;
    private final AtomicInteger usersLoggedInWebLastMonth;
    private final AtomicInteger usersLoggedInMobileLastDay;
    private final AtomicInteger usersLoggedInMobileLastWeek;
    private final AtomicInteger usersLoggedInMobileLastMonth;
    private final AtomicInteger usersLoggedInLastDay;
    private final AtomicInteger usersLoggedInLastWeek;
    private final AtomicInteger usersLoggedInLastMonth;
    private final AtomicInteger usersActiveLastDay;
    private final AtomicInteger usersActiveLastWeek;
    private final AtomicInteger usersActiveLastMonth;

    UserRepository userRepository;

    public static final String LAST_DAY_TAG = "last_day";
    public static final String LAST_WEEK_TAG = "last_week";
    public static final String LAST_MONTH_TAG = "last_month";
    public static final String WEB = "web";
    public static final String MOBILE = "mobile";
    public static final String LOGGED_IN = "loggedIn";
    public static final String ACTIVE = "active";

    private final HashMap<String, AtomicInteger> metricLookup;

    @Autowired
    public UserMetricsTask(UserRepository userDeviceRepository) {

        this.userRepository = userDeviceRepository;

        // create guages
        usersTotal = Metrics.gauge(String.format("users.total"), new AtomicInteger());
        usersLoggedInWebLastDay = Metrics.gauge(String.format("users.logged-in.web.last_day"), new AtomicInteger());
        usersLoggedInWebLastWeek = Metrics.gauge(String.format("users.logged-in.web.last_week"), new AtomicInteger());
        usersLoggedInWebLastMonth = Metrics.gauge(String.format("users.logged-in.web.last_month"), new AtomicInteger());
        usersLoggedInMobileLastDay = Metrics.gauge(String.format("users.logged-in.mobile.last_day"), new AtomicInteger());
        usersLoggedInMobileLastWeek = Metrics.gauge(String.format("users.logged-in.mobile.last_week"), new AtomicInteger());
        usersLoggedInMobileLastMonth = Metrics.gauge(String.format("users.logged-in.mobile.last_month"), new AtomicInteger());
        usersLoggedInLastDay = Metrics.gauge(String.format("users.logged-in.last_day"), new AtomicInteger());
        usersLoggedInLastWeek = Metrics.gauge(String.format("users.logged-in.last_week"), new AtomicInteger());
        usersLoggedInLastMonth = Metrics.gauge(String.format("users.logged-in.last_month"), new AtomicInteger());
        usersActiveLastDay = Metrics.gauge(String.format("users.active.last_day"), new AtomicInteger());
        usersActiveLastWeek = Metrics.gauge(String.format("users.active.last_week"), new AtomicInteger());
        usersActiveLastMonth = Metrics.gauge(String.format("users.active.last_month"), new AtomicInteger());

        metricLookup = new HashMap<>();
        metricLookup.put(WEB + LAST_DAY_TAG, usersLoggedInWebLastDay);
        metricLookup.put(WEB + LAST_WEEK_TAG, usersLoggedInWebLastWeek);
        metricLookup.put(WEB + LAST_MONTH_TAG, usersLoggedInWebLastMonth);
        metricLookup.put(MOBILE + LAST_DAY_TAG, usersLoggedInMobileLastDay);
        metricLookup.put(MOBILE + LAST_WEEK_TAG, usersLoggedInMobileLastWeek);
        metricLookup.put(MOBILE + LAST_MONTH_TAG, usersLoggedInMobileLastMonth);
        metricLookup.put(LOGGED_IN + LAST_DAY_TAG, usersLoggedInLastDay);
        metricLookup.put(LOGGED_IN + LAST_WEEK_TAG, usersLoggedInLastWeek);
        metricLookup.put(LOGGED_IN + LAST_MONTH_TAG, usersLoggedInLastMonth);
        metricLookup.put(ACTIVE + LAST_DAY_TAG, usersActiveLastDay);
        metricLookup.put(ACTIVE + LAST_WEEK_TAG, usersActiveLastWeek);
        metricLookup.put(ACTIVE + LAST_MONTH_TAG, usersActiveLastMonth);
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
        usersTotal.set(count);

        logger.info("-- end gatherStatsTotalUserCount ");
    }

    private void publishLoggedInByClient(LocalDate date, String timeTag) {
        Map<String, Integer> loggedInByClient = userRepository.getLoggedInUserCountByClient(date);
        logger.info("--- begin publishLoggedInByClient [{}][{}] ", date, timeTag);
        loggedInByClient
                .forEach((key, value) -> {
                    var lookupName = key + timeTag;
                    metricLookup.get(lookupName).set(value);
                    logger.info("---- begin publishLoggedInByClient [{}][{}] ", lookupName, value);
                });
    }

    private void publishLoggedIn(LocalDate date, String timeTag) {
        Integer count = userRepository.getLoggedInUserCount(date);
        var lookupName = LOGGED_IN + timeTag;
        metricLookup.get(lookupName).set(count);
        logger.info("---  publishLoggedIn [{}][{}][{}] ", date, timeTag, count);
    }

    private void publishActive(LocalDate date, String timeTag) {
        Integer count = userRepository.getActiveUserCount(date);
        var lookupName = ACTIVE + timeTag;
        metricLookup.get(lookupName).set(count);
        logger.info("---  publishActive [{}][{}][{}] ", date, timeTag, count);
    }


}
