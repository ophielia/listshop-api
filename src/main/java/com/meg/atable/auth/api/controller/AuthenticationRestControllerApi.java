package com.meg.atable.auth.api.controller;

import com.meg.atable.auth.service.impl.JwtAuthenticationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by margaretmartin on 23/09/2017.
 */
public interface  AuthenticationRestControllerApi {

    @PostMapping(value = "${jwt.route.authentication.path}", produces = "application/json", consumes = "application/json")
    ResponseEntity<Object> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest, Device device);

    @GetMapping(value = "${jwt.route.authentication.refresh}")
    ResponseEntity<Object> refreshAndGetAuthenticationToken(HttpServletRequest request);

}
