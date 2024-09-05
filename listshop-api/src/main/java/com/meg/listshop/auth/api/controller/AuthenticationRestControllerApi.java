package com.meg.listshop.auth.api.controller;

import com.meg.listshop.auth.api.model.ClientDeviceInfo;
import com.meg.listshop.auth.api.model.JwtAuthorizationRequest;
import com.meg.listshop.lmt.api.exception.BadParameterException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * Created by margaretmartin on 23/09/2017.
 */
@RequestMapping("/auth")
public interface AuthenticationRestControllerApi {

    @PostMapping(produces = "application/json", consumes = "application/json")
    ResponseEntity<Object> authorizeUser(@RequestBody JwtAuthorizationRequest authorizationRequest) throws BadParameterException;

    @PostMapping(value = "/authenticate", produces = "application/json")
    ResponseEntity<Object> authenticateUser(HttpServletRequest request, @RequestBody ClientDeviceInfo deviceInfo) throws BadParameterException;

    @GetMapping(value = "/logout")
    ResponseEntity<Object> logoutUser(Principal principal, HttpServletRequest request) throws BadParameterException;

}
