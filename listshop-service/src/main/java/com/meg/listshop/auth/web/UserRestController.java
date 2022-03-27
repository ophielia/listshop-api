/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.auth.web;

import com.meg.listshop.auth.api.controller.UserRestControllerApi;
import com.meg.listshop.auth.api.model.*;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.auth.service.impl.JwtTokenUtil;
import com.meg.listshop.lmt.api.exception.BadParameterException;
import com.meg.listshop.lmt.api.exception.ProcessingException;
import com.meg.listshop.lmt.api.exception.TokenException;
import com.meg.listshop.lmt.api.model.ModelMapper;
import com.meg.listshop.lmt.api.model.TokenType;
import com.meg.listshop.lmt.service.TokenService;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.Principal;
import java.util.Base64;

@Controller
public class UserRestController implements UserRestControllerApi {
    private static final Logger LOG = LoggerFactory.getLogger(UserRestController.class);

    private final TokenService tokenService;

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenUtil jwtTokenUtil;

    @Value("${listshop.min.ios.version:1.0}")
    private String minIosClient;

    @Value("${listshop.min.android.version:1.0}")
    private String minAndroidClient;

    @Autowired
    public UserRestController(UserService userService, AuthenticationManager authenticationManager,
                              JwtTokenUtil jwtTokenUtil, TokenService tokenService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.tokenService = tokenService;
    }

    @Override
    public ResponseEntity<Object> createUser(@RequestBody PutCreateUser inputPut) throws BadParameterException {
        LOG.debug("Begin creating new user");
        decodeAndValidateCreateUserInput(inputPut);

        var user = inputPut.getUser();
        LOG.info(String.format("Create new user[%s], input validated.", user.getEmail()));
        ClientDeviceInfo deviceInfo = inputPut.getDeviceInfo();

        // get email and password
        String email = user.getEmail();
        String password = user.getPassword();


        // call service to create user
        UserEntity newUser = userService.createUser(email  , password);

        // authenticate new user
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        password
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Reload password post-security so we can generate token
        final UserEntity userDetails = userService.getUserByUserEmail(newUser.getEmail());
        final String token = jwtTokenUtil.generateExpiringToken(newUser, deviceInfo);

        // create user device
        this.userService.createDeviceForUserAndDevice(newUser.getId(), deviceInfo, token);

        // update last login time
        this.userService.updateLoginForUser(newUser.getUsername(), token,deviceInfo );

        // Return the token
        return ResponseEntity.ok(new UserResource(ModelMapper.toModel(userDetails, token)));
    }

    private void decodeAndValidateCreateUserInput(PutCreateUser inputPut) throws BadParameterException {
        var user = inputPut.getUser();
        ClientDeviceInfo deviceInfo = inputPut.getDeviceInfo();

        if (user == null || user.getEmail() == null || user.getPassword() == null) {
            throw new BadParameterException("Parameter user missing in PutCreateUser.");
        }
        if (deviceInfo == null) {
            throw new BadParameterException("Parameter deviceInfo missing in PutCreateUser.");
        }

        // decode email and password
        byte[] emailBytes = Base64.getDecoder().decode(user.getEmail());
        var decodedEmail = new String(emailBytes);
        byte[] passwordBytes = Base64.getDecoder().decode(user.getPassword());
        var decodedPassword = new String(passwordBytes);

        // clean email
        var cleanEmail = decodedEmail.trim().toLowerCase();

        // put cleaned and decoded values back in object
        inputPut.getUser().setPassword(decodedPassword);
        inputPut.getUser().setEmail(cleanEmail);
    }

    @Override
    public ResponseEntity<Object> deleteUser(Principal principal) {
        LOG.info(String.format("Begin delete user [%s]",principal.getName()));
        this.userService.deleteUser(principal.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<UserResource> getUser(Principal principal) {
        UserEntity user = this.userService.getUserByUserEmail(principal.getName());
        var userResource = new UserResource(ModelMapper.toModel(user, ""));

        return new ResponseEntity<>(userResource, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> userNameIsTaken(@RequestBody ListShopPayload payload) throws BadParameterException {
        var parameters = payload.getParameters();
        if (parameters == null || parameters.isEmpty()) {
            throw new BadParameterException("User email is required as first parameter");
        }
        var rawName = parameters.get(0);
        var name = rawName.substring(0, Math.min(60, rawName.length()));
        UserEntity user = this.userService.getUserByUserEmail(name);

        return ResponseEntity.ok(user != null);
    }

    @Override
    public ResponseEntity<Object> getToken(@RequestBody PostTokenRequest postTokenRequest) throws BadParameterException {
        validateTokenRequest(postTokenRequest);

        // convert token type string to token type
        TokenType type = Enum.valueOf(TokenType.class, postTokenRequest.getTokenType());

        // call service method
        try {
            tokenService.generateTokenForUser(type, postTokenRequest.getTokenParameter());
        } catch (TemplateException | MessagingException | IOException e) {
            LOG.error("Exception {} thrown while processing token request.", e.getClass());
            throw new ProcessingException("Unable to process token request.");
        }

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Object> processToken(@RequestBody PostToken postToken) throws BadParameterException, TokenException {
        LOG.debug(String.format("Entering processToken with tokenType[%s]", postToken.getTokenType()));
        validateToken(postToken);

        // convert token type string to TokenType
        TokenType type = Enum.valueOf(TokenType.class, postToken.getTokenType());

        // call service method
        tokenService.processTokenFromUser(type, postToken.getToken(), postToken.getTokenParameter());

        LOG.debug(String.format("Completing processToken for tokenType[%s]", postToken.getTokenType()));
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Object> changeUserPassword(Principal principal, @RequestBody PostChangePassword input) throws BadParameterException {
        LOG.debug(String.format("Begin changeUserPassword, user[%s]", principal.getName()));
        // get username from principal
        String principalUsername = principal.getName();
        validatateUserForPasswordChange(input, principalUsername);

        // get new password from input
        byte[] passwordBytes = Base64.getDecoder().decode(input.getNewPassword());
        var newPassword = new String(passwordBytes);
        // get original password from input
        byte[] origPasswordBytes = Base64.getDecoder().decode(input.getOriginalPassword());
        var originalPassword = new String(origPasswordBytes);
        userService.changePassword(principalUsername, newPassword, originalPassword );
        LOG.debug(String.format("Finished changeUserPassword, user[%s]", principal.getName()));
        return ResponseEntity.ok().build();

    }

    @Override
    public ResponseEntity<Object> getMinimumClientVersion() {
        ClientVersions clientVersions = new ClientVersions();
        clientVersions.setIosMinVersion(minIosClient);
        clientVersions.setAndroidMinVersion(minAndroidClient);
        return new ResponseEntity<>(clientVersions, HttpStatus.OK);
    }

    private void validatateUserForPasswordChange(PostChangePassword postChangePassword, String principalUsername) throws BadParameterException {
        if (principalUsername == null ) {
            throw new BadParameterException("User or username in input is blank or missing");
        }
        if (postChangePassword.getNewPassword() == null || postChangePassword.getNewPassword().isEmpty()) {
            throw new BadParameterException("Input for change password does not include the new password");
        }
        if (postChangePassword.getOriginalPassword() == null || postChangePassword.getOriginalPassword().isEmpty()) {
            throw new BadParameterException("Input for change password does not include the original password");
        }
    }


    private void validateTokenRequest(PostTokenRequest postTokenRequest) throws BadParameterException {
        if (postTokenRequest == null) {
            throw new BadParameterException("No TokenRequest in request");
        }
        if (postTokenRequest.getTokenParameter() == null) {
            throw new BadParameterException("No parameter in TokenRequest");
        }
        if (postTokenRequest.getTokenType() == null) {
            throw new BadParameterException("No token type in TokenRequest");
        }
    }

    private void validateToken(PostToken postToken) throws BadParameterException {
        if (postToken == null) {
            throw new BadParameterException("No Token in request");
        }
        if (postToken.getTokenParameter() == null) {
            throw new BadParameterException("No parameter in Token");
        }
        if (postToken.getTokenType() == null) {
            throw new BadParameterException("No token type in Token");
        }
        if (postToken.getToken() == null) {
            throw new BadParameterException("No token value in Token");
        }
    }


}
