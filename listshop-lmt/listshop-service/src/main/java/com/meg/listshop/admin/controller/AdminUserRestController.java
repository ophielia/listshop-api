package com.meg.listshop.admin.controller;

import com.meg.listshop.admin.model.AdminUser;
import com.meg.listshop.admin.model.AdminUserListResource;
import com.meg.listshop.admin.model.PostSearchUsers;
import com.meg.listshop.auth.data.entity.AdminUserDetailsEntity;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.data.entity.UserPropertyEntity;
import com.meg.listshop.auth.service.UserPropertyService;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.lmt.api.exception.BadParameterException;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.api.model.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@CrossOrigin
public class AdminUserRestController implements AdminUserRestControllerApi {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserRestController.class);

    private UserService userService;
    private UserPropertyService userPropertyService;

    @Autowired
    public AdminUserRestController(UserService userService, UserPropertyService userPropertyService) {
        this.userService = userService;
        this.userPropertyService = userPropertyService;
    }

    @Override
    public ResponseEntity<AdminUserListResource> searchUsers(@RequestBody PostSearchUsers input) throws BadParameterException {
        if (input == null) {
            throw new BadParameterException("No Search Parameters sent for admin user search.");
        }
        String searchEmail = input.getEmail();
        String userIdString = input.getUserId();
        String listIdString = input.getListId();

        logger.debug("Received search parameters: email [{}], userId [{}], listId [{}]", searchEmail, userIdString, listIdString);

        List<UserEntity> users = new ArrayList<>();
        if (searchEmail != null && !searchEmail.isEmpty()) {
            users = userService.findUsersByEmail(searchEmail);
        } else if (userIdString != null && !userIdString.isEmpty()) {
            try {
                Long userId = Long.valueOf(userIdString);
                UserEntity user = userService.getUserById(userId);
                if (user != null) {
                    users = Collections.singletonList(user);
                }
            } catch (NumberFormatException e) {
                logger.warn(String.format("Passed user id [%s] can't be passed into long.", userIdString));
            }

        } else if (listIdString != null && !listIdString.isEmpty()) {
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
                .map(ModelMapper::toAdminModel)
                .collect(Collectors.toList()));

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<AdminUser> getUser(@PathVariable Long userId) throws ObjectNotFoundException, BadParameterException {
        if (userId == null) {
            throw new BadParameterException("Can't get a user without an id!");
        }

        AdminUserDetailsEntity user = userService.getAdminUserById(userId);
        if (user == null) {
            throw new ObjectNotFoundException(String.format("User not found for userId [%s]", userId));
        }
        List<UserPropertyEntity> propertyEntities = this.userPropertyService.getPropertiesForUser(user.getUserName());

        return new ResponseEntity<>(ModelMapper.toAdminModel(user, propertyEntities), HttpStatus.OK);
    }


}
