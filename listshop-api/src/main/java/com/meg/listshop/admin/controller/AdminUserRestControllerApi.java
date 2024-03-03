/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.admin.controller;

import com.meg.listshop.admin.model.AdminUser;
import com.meg.listshop.admin.model.AdminUserListResource;
import com.meg.listshop.admin.model.PostSearchUsers;
import com.meg.listshop.lmt.api.exception.BadParameterException;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/admin/user")
public interface AdminUserRestControllerApi {

    @PostMapping(produces = "application/json")
    ResponseEntity<AdminUserListResource> searchUsers(@RequestBody PostSearchUsers input) throws BadParameterException;

    @GetMapping(value = "/tags", produces = "application/json")
    ResponseEntity<AdminUserListResource> getAllUsersWithTags() ;

    @GetMapping(value = "/{userId}", produces = "application/json")
    ResponseEntity<AdminUser> getUser(@PathVariable Long userId) throws ObjectNotFoundException, BadParameterException;

}
