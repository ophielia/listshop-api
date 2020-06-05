package com.meg.listshop.auth.api.controller;

import com.meg.listshop.auth.service.impl.JwtAuthorizationRequest;
import com.meg.listshop.lmt.api.exception.BadParameterException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by margaretmartin on 23/09/2017.
 */
@RequestMapping("/auth")
public interface AuthenticationRestControllerApi {

    @PostMapping(produces = "application/json", consumes = "application/json")
    ResponseEntity<Object> authorizeUser(@RequestBody JwtAuthorizationRequest authorizationRequest) throws BadParameterException;

    @PostMapping(value = "/authenticate", produces = "application/json", consumes = "application/json")
    ResponseEntity<Object> authenticateUser(HttpServletRequest request) throws BadParameterException;

}
