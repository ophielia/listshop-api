package com.meg.atable.api.controller;

import com.meg.atable.auth.service.JwtAuthenticationRequest;
import com.meg.atable.auth.service.JwtAuthenticationResponse;
import com.meg.atable.auth.service.JwtUser;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by margaretmartin on 23/09/2017.
 */
public interface AuthenticationRestControllerApi {

    @RequestMapping(value = "${jwt.route.authentication.path}", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    ResponseEntity<?> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest, Device device) throws AuthenticationException;

    @RequestMapping(value = "${jwt.route.authentication.refresh}", method = RequestMethod.GET)
    ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request);

}
