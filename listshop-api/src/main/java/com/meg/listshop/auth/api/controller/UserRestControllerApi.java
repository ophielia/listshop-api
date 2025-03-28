/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.auth.api.controller;

import com.meg.listshop.auth.api.model.*;
import com.meg.listshop.lmt.api.exception.BadParameterException;
import com.meg.listshop.lmt.api.exception.TokenException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@RestController
@RequestMapping("/user")
@CrossOrigin
public interface UserRestControllerApi {

    @PostMapping(produces = "application/json", consumes = "application/json")
    ResponseEntity<Object> createUser(@RequestBody PutCreateUser input) throws BadParameterException;

    @DeleteMapping(produces = "application/json", consumes = "application/json")
    ResponseEntity<Object> deleteUser(Authentication authentication) ;

    @GetMapping(produces = "application/json")
    ResponseEntity<UserResource> getUser(Authentication authentication);

    @PostMapping(value = "name", produces = "application/json")
    ResponseEntity<Object> userNameIsTaken(@RequestBody ListShopPayload payload) throws BadParameterException;

    @PostMapping(value = "token/tokenrequest", produces = "application/json")
    ResponseEntity<Object> getToken(@RequestBody PostTokenRequest postTokenRequest) throws BadParameterException;

    @PostMapping(value = "token", produces = "application/json")
    ResponseEntity<Object> processToken(@RequestBody PostToken postToken) throws BadParameterException, TokenException;


    @PostMapping(value = "password", produces = "application/json", consumes = "application/json")
    ResponseEntity<Object> changeUserPassword(Authentication authentication, @RequestBody PostChangePassword input) throws BadParameterException;

    @GetMapping(value = "client/version", produces = "application/json")
    ResponseEntity<Object> getMinimumClientVersion();

    @GetMapping(value = "properties", produces = "application/json")
    ResponseEntity<Object> getUserProperties(Authentication authentication) throws BadParameterException;

    @GetMapping(value = "properties/key/{key}", produces = "application/json")
    ResponseEntity<Object> getUserProperty(Authentication authentication, @PathVariable String key) throws BadParameterException;

    @PostMapping(value = "properties", produces = "application/json")
    ResponseEntity<Object> setUserProperties(Authentication authentication, @RequestBody PostUserProperties properties) throws BadParameterException, IOException;
}
