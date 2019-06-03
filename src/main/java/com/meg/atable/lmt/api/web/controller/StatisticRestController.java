package com.meg.atable.lmt.api.web.controller;

import com.meg.atable.auth.data.entity.UserEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.lmt.api.controller.StatisticRestControllerApi;
import com.meg.atable.lmt.api.model.ModelMapper;
import com.meg.atable.lmt.api.model.StatisticListResource;
import com.meg.atable.lmt.api.model.StatisticResource;
import com.meg.atable.lmt.data.entity.ListTagStatistic;
import com.meg.atable.lmt.service.ListTagStatisticService;
import com.sun.org.glassfish.external.statistics.Statistic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@CrossOrigin
public class StatisticRestController implements StatisticRestControllerApi {

    private final ListTagStatisticService listTagStatisticService;
    private final UserService userService;

    @Autowired
    StatisticRestController(UserService userService,
                            ListTagStatisticService listTagStatisticService) {
        this.userService = userService;
        this.listTagStatisticService = listTagStatisticService;
    }


    @Override
    public ResponseEntity<Resources<StatisticResource>> getUserStatistics(Principal principal) {
        UserEntity user = this.userService.getUserByUserEmail(principal.getName());

        List<ListTagStatistic> statistics = listTagStatisticService.getStatisticsForUser(user.getId());
        StatisticListResource resource = new StatisticListResource(statistics);

        return new ResponseEntity(resource, HttpStatus.OK);
    }


}
