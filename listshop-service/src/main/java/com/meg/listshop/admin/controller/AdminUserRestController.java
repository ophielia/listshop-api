package com.meg.listshop.admin.controller;

import com.meg.listshop.admin.model.AdminUserListResource;
import com.meg.listshop.admin.model.PostSearchUsers;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.lmt.api.exception.BadParameterException;
import com.meg.listshop.lmt.api.model.ModelMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@CrossOrigin
public class AdminUserRestController implements AdminUserRestControllerApi {

    private static final Logger logger = LogManager.getLogger(AdminUserRestController.class);

    private UserService userService;

    @Autowired
    public AdminUserRestController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ResponseEntity<AdminUserListResource> searchUsers(@RequestBody PostSearchUsers input) throws BadParameterException {
        if (input == null) {
            throw new BadParameterException("No Search Parameters sent for admin user search.");
        }
        String searchEmail = input.getEmail();
        String userIdString = input.getUserId();
        String listIdString = input.getListId();

        List<UserEntity> users = new ArrayList<>();
        if (searchEmail != null) {
            users = userService.findUsersByEmail(searchEmail);
        } else if (userIdString != null) {
            try {
                Long userId = Long.valueOf(userIdString);
                UserEntity user = userService.getUserById(userId);
                if (user != null) {
                    users = Collections.singletonList(user);
                }
            } catch (NumberFormatException e) {
                logger.warn(String.format("Passed user id [%s] can't be passed into long.", userIdString));
            }

        } else if (listIdString != null) {
            try {
                Long listId = Long.valueOf(listIdString);
                UserEntity user = userService.getUserByListId(listId);
                if (user != null) {
                    users = Collections.singletonList(user);
                }
            } catch (NumberFormatException e) {
                logger.warn(String.format("Passed list id [%s] can't be passed into long.", userIdString));
            }
        } else {
            logger.warn("No search parameters passed to admin user search");
        }

        AdminUserListResource result = new AdminUserListResource(users.stream()
                .map(d -> ModelMapper.toAdminModel(d))
                .collect(Collectors.toList()));

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
