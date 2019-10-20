package com.meg.atable.lmt.api.web.controller;

import com.meg.atable.auth.data.entity.UserEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.lmt.api.controller.StatisticRestControllerApi;
import com.meg.atable.lmt.api.exception.ActionInvalidException;
import com.meg.atable.lmt.api.model.StatisticListPost;
import com.meg.atable.lmt.api.model.StatisticListResource;
import com.meg.atable.lmt.data.entity.ListTagStatistic;
import com.meg.atable.lmt.service.ListTagStatisticService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.List;

@Controller
@CrossOrigin
public class StatisticRestController implements StatisticRestControllerApi {

    private final ListTagStatisticService listTagStatisticService;
    private final UserService userService;

    private static final Logger logger = LogManager.getLogger(StatisticRestController.class);

    @Value("${statistic.list.default.limit:100}")
    private int statisticListDefaultLimit;

    @Autowired
    StatisticRestController(UserService userService,
                            ListTagStatisticService listTagStatisticService) {
        this.userService = userService;
        this.listTagStatisticService = listTagStatisticService;
    }


    @Override
    public ResponseEntity<Resources<StatisticListResource>> getUserStatistics(Principal principal, @RequestParam(value = "limit", required = false) String limit) {
        UserEntity user = this.userService.getUserByUserEmail(principal.getName());

        int resultLimit = 0;
        try {
            resultLimit = Integer.parseInt(limit);

        } catch (NumberFormatException nfe) {
            resultLimit = 100;
        }
        List<ListTagStatistic> statistics = listTagStatisticService.getStatisticsForUser(user.getId(), resultLimit);
        StatisticListResource resource = new StatisticListResource(statistics);

        return new ResponseEntity(resource, HttpStatus.OK);
    }

    public ResponseEntity<Object> createUserStatistics(Principal principal, @RequestBody StatisticListPost statisticList) {
        UserEntity user = this.userService.getUserByUserEmail(principal.getName());

        if (statisticList == null) {
            throw new ActionInvalidException("null statistic list received in createUserStatistics");
        }
        // pass list of statistics to service
        List<ListTagStatistic> statistics = listTagStatisticService.createStatisticsForUser(user, statisticList.getStatisticList());
        Link resourceLink = new StatisticListResource(statistics).getLink("self");
        URI uri = null;
        try {
            uri = new URI("/statistics");
            return ResponseEntity.created(uri).build();
        } catch (URISyntaxException e) {
            logger.error("can't make a uri");
        }
        return ResponseEntity.ok().build();
    }

}
