/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.auth.api.controller;

import com.meg.listshop.auth.api.model.PostToken;
import com.meg.listshop.auth.api.model.PostTokenRequest;
import com.meg.listshop.auth.api.model.PutCreateUser;
import com.meg.listshop.auth.api.model.UserResource;
import com.meg.listshop.lmt.api.exception.BadParameterException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


    @GetMapping(produces = "application/json")
    ResponseEntity<UserResource> getUser(Principal principal);

    @GetMapping(value = "name", produces = "application/json")
    ResponseEntity<Object> userNameIsTaken(@RequestParam(value = "name") String email);

    @PostMapping(value = "token/tokenrequest", produces = "application/json")
    ResponseEntity<Object> getToken(@RequestBody PostTokenRequest postTokenRequest) throws BadParameterException;

    @PostMapping(value = "token", produces = "application/json")
    ResponseEntity<Object> processToken(@RequestBody PostToken postToken) throws BadParameterException;

}
