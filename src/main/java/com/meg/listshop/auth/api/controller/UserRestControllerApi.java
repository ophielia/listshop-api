package com.meg.listshop.auth.api.controller;

import com.meg.listshop.auth.api.model.User;
import com.meg.listshop.auth.api.model.UserResource;
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
    ResponseEntity<Object> createUser(@RequestBody User input);


    @GetMapping(produces = "application/json")
    ResponseEntity<UserResource> getUser(Principal principal);
}
