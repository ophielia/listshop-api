package com.meg.listshop.admin.controller;

import com.meg.listshop.admin.model.AdminUserListResource;
import com.meg.listshop.admin.model.PostSearchUsers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@Controller
@CrossOrigin
public class AdminUserRestController implements AdminUserRestControllerApi {

    private static final Logger logger = LogManager.getLogger(AdminUserRestController.class);

    @Override
    public ResponseEntity<AdminUserListResource> searchUsers(PostSearchUsers input) {
        return null;
    }
}
