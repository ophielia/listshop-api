package com.meg.atable.lmt.api.controller;

import com.meg.atable.lmt.api.model.StatisticResource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@RestController
@RequestMapping("/statistics")
@CrossOrigin
public interface StatisticRestControllerApi {


    @GetMapping(produces = "application/json")
    ResponseEntity<Resources<StatisticResource>> getUserStatistics(Principal principal);
}
