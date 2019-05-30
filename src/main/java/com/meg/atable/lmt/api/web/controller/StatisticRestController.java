package com.meg.atable.lmt.api.web.controller;

import com.meg.atable.auth.data.entity.UserEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.lmt.api.controller.StatisticRestControllerApi;
import com.meg.atable.lmt.api.model.StatisticResource;
import com.meg.atable.lmt.data.entity.ListTagStatistic;
import com.meg.atable.lmt.service.ListTagStatisticService;
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
        List<StatisticResource> statisticResourceList = statistics.stream()
                .map(StatisticResource::new)
                .collect(Collectors.toList());
        Resources<StatisticResource> resource = new Resources<>(statisticResourceList);

        return new ResponseEntity(resource, HttpStatus.OK);
    }


}
