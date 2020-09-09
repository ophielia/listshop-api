package com.meg.listshop.auth.api.web;

import com.meg.listshop.auth.api.controller.UserRestControllerApi;
import com.meg.listshop.auth.api.model.ClientDeviceInfo;
import com.meg.listshop.auth.api.model.PutCreateUser;
import com.meg.listshop.auth.api.model.User;
import com.meg.listshop.auth.api.model.UserResource;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.auth.service.impl.JwtTokenUtil;
import com.meg.listshop.lmt.api.exception.BadParameterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;
import java.util.Base64;

@Controller
public class UserRestController implements UserRestControllerApi {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;


    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public ResponseEntity<Object> createUser(@RequestBody PutCreateUser inputPut) throws BadParameterException {
        User user = inputPut.getUser();
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
        String decodedEmail = new String(emailBytes);
        byte[] passwordBytes = Base64.getDecoder().decode(password);
        String decodedPassword = new String(passwordBytes);
        byte[] usernameBytes = Base64.getDecoder().decode(username);
        String decodedUsername = new String(usernameBytes);

        // call service to create user
        UserEntity newUser = userService.createUser(decodedUsername, decodedEmail, decodedPassword);

        // authenticate new user
        final Authentication authentication = authenticationManager.authenticate(
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
        return ResponseEntity.ok(new UserResource(userDetails, token));
    }

    public ResponseEntity<UserResource> getUser(Principal principal) {
        UserEntity user = this.userService.getUserByUserEmail(principal.getName());
        UserResource userResource = new UserResource(user, "");

        return new ResponseEntity(userResource, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> userNameIsTaken(String name) {
        UserEntity user = this.userService.getUserByUserEmail(name);

        return ResponseEntity.ok(user != null);
    }


}
