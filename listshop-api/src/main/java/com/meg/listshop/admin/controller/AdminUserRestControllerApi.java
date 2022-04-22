/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.admin.controller;

import com.meg.listshop.admin.model.AdminUserListResource;
import com.meg.listshop.admin.model.PostSearchUsers;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/admin/users")
public interface AdminUserRestControllerApi {

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    ResponseEntity<AdminUserListResource> searchUsers(@RequestBody PostSearchUsers input);
}
