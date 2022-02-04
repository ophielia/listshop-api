/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.auth.web;

import com.meg.listshop.auth.api.controller.UserRestControllerApi;
import com.meg.listshop.auth.api.model.*;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.auth.service.impl.JwtTokenUtil;
import com.meg.listshop.lmt.api.exception.BadParameterException;
import com.meg.listshop.lmt.api.exception.TokenException;
import com.meg.listshop.lmt.api.model.ModelMapper;
import com.meg.listshop.lmt.api.model.TokenType;
import com.meg.listshop.lmt.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;
import java.util.Base64;

@Controller
public class UserRestController implements UserRestControllerApi {

    private final TokenService tokenService;

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public UserRestController(UserService userService, AuthenticationManager authenticationManager,
                              JwtTokenUtil jwtTokenUtil, TokenService tokenService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.tokenService = tokenService;
    }

    @Override
    public ResponseEntity<Object> createUser(@RequestBody PutCreateUser inputPut) throws BadParameterException {
        var user = inputPut.getUser();
        ClientDeviceInfo deviceInfo = inputPut.getDeviceInfo();

        if (user == null) {
            throw new BadParameterException("Parameter user missing in PutCreateUser.");
        } else if (deviceInfo == null) {
            throw new BadParameterException("Parameter deviceInfo missing in PutCreateUser.");
        }

        // get email and password
        String email = user.getEmail();
        String password = user.getPassword();
        String username = user.getUsername();

        // decode email and password
        byte[] emailBytes = Base64.getDecoder().decode(email);
        var decodedEmail = new String(emailBytes);
        byte[] passwordBytes = Base64.getDecoder().decode(password);
        var decodedPassword = new String(passwordBytes);
        byte[] usernameBytes = Base64.getDecoder().decode(username);
        var decodedUsername = new String(usernameBytes);

        // call service to create user
        UserEntity newUser = userService.createUser(decodedUsername, decodedEmail, decodedPassword);

        // authenticate new user
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        newUser.getEmail(),
                        decodedPassword
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Reload password post-security so we can generate token
        final UserEntity userDetails = userService.getUserByUserEmail(newUser.getEmail());
        final String token = jwtTokenUtil.generateExpiringToken(newUser, deviceInfo);

        // create user device
        this.userService.createDeviceForUserAndDevice(newUser.getId(), deviceInfo, token);

        // update last login time
        this.userService.updateLoginForUser(newUser.getUsername(), token);

        // Return the token
        return ResponseEntity.ok(new UserResource(ModelMapper.toModel(userDetails, token)));
    }

    public ResponseEntity<UserResource> getUser(Principal principal) {
        UserEntity user = this.userService.getUserByUserEmail(principal.getName());
        var userResource = new UserResource(ModelMapper.toModel(user, ""));

        return new ResponseEntity<>(userResource, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> userNameIsTaken(String name) {
        UserEntity user = this.userService.getUserByUserEmail(name);

        return ResponseEntity.ok(user != null);
    }

    @Override
    public ResponseEntity<Object> getToken(@RequestBody PostTokenRequest postTokenRequest) throws BadParameterException {
        validateTokenRequest(postTokenRequest);

        // convert token type string to token type
        TokenType type = Enum.valueOf(TokenType.class, postTokenRequest.getTokenType());

        // call service method
        tokenService.generateTokenForUser(type, postTokenRequest.getTokenParameter());

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Object> processToken(@RequestBody PostToken postToken) throws BadParameterException, TokenException {
        validateToken(postToken);

        // convert token type string to TokenType
        TokenType type = Enum.valueOf(TokenType.class, postToken.getTokenType());

        // call service method
        tokenService.processTokenFromUser(type, postToken.getToken(), postToken.getTokenParameter());

        return ResponseEntity.ok().build();
    }


    private void validateTokenRequest(PostTokenRequest postTokenRequest) throws BadParameterException {
        if (postTokenRequest == null) {
            throw new BadParameterException("No TokenRequest in request");
        }
        if (postTokenRequest.getTokenParameter() == null) {
            throw new BadParameterException("No parameter in TokenRequest");
        }
        if (postTokenRequest.getTokenType() == null) {
            throw new BadParameterException("No token type in TokenRequest");
        }
    }

    private void validateToken(PostToken postToken) throws BadParameterException {
        if (postToken == null) {
            throw new BadParameterException("No Token in request");
        }
        if (postToken.getTokenParameter() == null) {
            throw new BadParameterException("No parameter in Token");
        }
        if (postToken.getTokenType() == null) {
            throw new BadParameterException("No token type in Token");
        }
        if (postToken.getToken() == null) {
            throw new BadParameterException("No token value in Token");
        }
    }


}
