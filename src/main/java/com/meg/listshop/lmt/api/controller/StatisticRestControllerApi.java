package com.meg.listshop.lmt.api.controller;

import com.meg.listshop.lmt.api.model.StatisticListPost;
import com.meg.listshop.lmt.api.model.StatisticListResource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@RestController
@RequestMapping("/statistics")
@CrossOrigin
public interface StatisticRestControllerApi {


    @GetMapping(produces = "application/json")
    ResponseEntity<Resources<StatisticListResource>> getUserStatistics(Principal principal, @RequestParam(value = "limit", required = false) String limit);

    @PostMapping(produces = "application/json", consumes = "application/json")
    ResponseEntity<Object> createUserStatistics(Principal principal, @RequestBody StatisticListPost statisticList);

}
