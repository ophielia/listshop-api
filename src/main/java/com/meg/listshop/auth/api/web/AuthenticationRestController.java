package com.meg.listshop.auth.api.web;

import com.meg.listshop.auth.api.controller.AuthenticationRestControllerApi;
import com.meg.listshop.auth.api.model.ClientDeviceInfo;
import com.meg.listshop.auth.api.model.UserResource;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.auth.service.impl.JwtAuthorizationRequest;
import com.meg.listshop.auth.service.impl.JwtTokenUtil;
import com.meg.listshop.auth.service.impl.ListShopUserDetailsService;
import com.meg.listshop.lmt.api.exception.AuthenticationException;
import com.meg.listshop.lmt.api.exception.BadParameterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
public class AuthenticationRestController implements AuthenticationRestControllerApi {

    @Value("${jwt.header}")
    private String tokenHeader;


    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenUtil jwtTokenUtil;

    private final ListShopUserDetailsService userDetailsService;

    @Autowired
    public AuthenticationRestController(UserService userService,
                                        AuthenticationManager authenticationManager,
                                        JwtTokenUtil jwtTokenUtil,
                                        ListShopUserDetailsService userDetailsService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    public ResponseEntity<Object> authorizeUser(@RequestBody JwtAuthorizationRequest authorizationRequest) throws BadParameterException {

        String userName = authorizationRequest.getUsername();
        String password = authorizationRequest.getPassword();
        ClientDeviceInfo deviceInfo = authorizationRequest.getDeviceInfo();

        if (userName == null ||
                password == null ||
                deviceInfo == null) {
            throw new BadParameterException("missing parameter in authorizeUser: " +
                    "userName [" + userName + "], " +
                    "password is null [" + (password == null) + "], " +
                    "deviceInfo [" + deviceInfo + "]");
        }
        // Perform the security
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authorizationRequest.getUsername(),
                        authorizationRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Reload password post-security so we can generate token
        UserEntity userEntity = userService.getUserByUserEmail(authorizationRequest.getUsername());
        final String token = jwtTokenUtil.generateExpiringToken(userEntity, deviceInfo);

        // save token for user
        userService.saveTokenForUserAndDevice(userEntity, deviceInfo, token);

        // Return the token
        final UserResource user = new UserResource(userEntity, token);
        return ResponseEntity.ok(user);
    }

    public ResponseEntity<Object> authenticateUser(HttpServletRequest request) throws BadParameterException {
        String token = request.getHeader(tokenHeader);

        if (token == null) {
            throw new BadParameterException("no token passed.");
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        // get user for token
        UserDetails userDetails = this.userDetailsService.loadUserByToken(token);
        if (userDetails == null) {
            throw new AuthenticationException("user not found for token.");
        }
        // validate token
        if (!jwtTokenUtil.validateToken(token, userDetails)) {
            throw new AuthenticationException("Token invalid for user.");
        }
        // update last login time
        UserEntity userEntity = this.userService.updateLoginForUser(userDetails.getUsername(), token);

        // return user
        final UserResource user = new UserResource(userEntity, "");
        return ResponseEntity.ok(user);
    }

    public ResponseEntity<Object> logoutUser(Principal principal, HttpServletRequest request) throws BadParameterException {
        String token = request.getHeader(tokenHeader);


        if (token == null) {
            throw new BadParameterException("no token passed.");
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }


        // update last login time
        this.userService.removeLoginForUser(principal.getName(), token);

        // return user
        return ResponseEntity.ok().build();
    }

}
