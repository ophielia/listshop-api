package com.meg.atable.auth.api.controller;

import com.meg.atable.auth.api.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@RestController
@RequestMapping("/user")
@CrossOrigin
public interface UserRestControllerApi {

    @PostMapping(produces = "application/json", consumes = "application/json")
    ResponseEntity<Object> createUser(@RequestBody User input);


}
