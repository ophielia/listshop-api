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
import com.meg.listshop.auth.data.entity.UserPropertyEntity;
import com.meg.listshop.auth.service.UserPropertyService;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.auth.service.impl.JwtTokenUtil;
import com.meg.listshop.lmt.api.exception.BadParameterException;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
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
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.Principal;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class UserRestController implements UserRestControllerApi {
    private static final Logger LOG = LoggerFactory.getLogger(UserRestController.class);

    private final TokenService tokenService;

    private final UserService userService;

    private final UserPropertyService userPropertyService;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenUtil jwtTokenUtil;

    @Value("${listshop.min.ios.version:1.0}")
    private String minIosClient;

    @Value("${listshop.min.android.version:1.0}")
    private String minAndroidClient;

    @Autowired
    public UserRestController(UserService userService, AuthenticationManager authenticationManager,
                              JwtTokenUtil jwtTokenUtil, TokenService tokenService, UserPropertyService userPropertyService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.tokenService = tokenService;
        this.userPropertyService = userPropertyService;
    }

    @Override
    public ResponseEntity<Object> createUser(@RequestBody PutCreateUser inputPut) throws BadParameterException {
        LOG.debug("Begin creating new user");
        decodeAndValidateCreateUserInput(inputPut);

        var user = inputPut.getUser();
        LOG.info("Create new user[{}], input validated.", user.getEmail());
        ClientDeviceInfo deviceInfo = inputPut.getDeviceInfo();

        // get email and password
        String email = user.getEmail();
        String password = user.getPassword();


        // call service to create user
        UserEntity newUser = userService.createUser(email, password);

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
        this.userService.updateLoginForUser(newUser.getUsername(), token, deviceInfo);

        // Return the token
        return ResponseEntity.ok(new UserResource(ModelMapper.toModel(userDetails, token)));
    }

    private void decodeAndValidateCreateUserInput(PutCreateUser inputPut) throws BadParameterException {
        var user = inputPut.getUser();
        ClientDeviceInfo deviceInfo = inputPut.getDeviceInfo();

        if (user == null || !StringUtils.hasText(user.getEmail()) || !StringUtils.hasText(user.getPassword())) {
            throw new BadParameterException("Parameter user missing in PutCreateUser.");
        }
        if (user.getEmail().length() > 255 || user.getPassword().length() > 255) {
            throw new BadParameterException("Parameter user or password too long in PutCreateUser.");
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
        LOG.info("Begin delete user [{}]", principal.getName());
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
        if (ObjectUtils.isEmpty(parameters) ) {
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
        LOG.debug("Entering processToken with tokenType[{}]", postToken.getTokenType());
        validateToken(postToken);

        // convert token type string to TokenType
        TokenType type = Enum.valueOf(TokenType.class, postToken.getTokenType());

        // call service method
        tokenService.processTokenFromUser(type, postToken.getToken(), postToken.getTokenParameter());

        LOG.debug("Completing processToken for tokenType[{}]", postToken.getTokenType());
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Object> changeUserPassword(Principal principal, @RequestBody PostChangePassword input) throws BadParameterException {
        LOG.debug("Begin changeUserPassword, user[{}]", principal.getName());
        // get username from principal
        String principalUsername = principal.getName();
        validatateUserForPasswordChange(input, principalUsername);

        // get new password from input
        byte[] passwordBytes = Base64.getDecoder().decode(input.getNewPassword());
        var newPassword = new String(passwordBytes);
        // get original password from input
        byte[] origPasswordBytes = Base64.getDecoder().decode(input.getOriginalPassword());
        var originalPassword = new String(origPasswordBytes);
        userService.changePassword(principalUsername, newPassword, originalPassword);
        LOG.debug("Finished changeUserPassword, user[{}]", principal.getName());
        return ResponseEntity.ok().build();

    }

    @Override
    public ResponseEntity<Object> getMinimumClientVersion() {
        ClientVersions clientVersions = new ClientVersions();
        clientVersions.setIosMinVersion(minIosClient);
        clientVersions.setAndroidMinVersion(minAndroidClient);
        return new ResponseEntity<>(clientVersions, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> getUserProperties(Principal principal) throws BadParameterException {
        List<UserPropertyEntity> propertyEntities = this.userPropertyService.getPropertiesForUser(principal.getName());
        List<UserProperty> properties = propertyEntities.stream()
                .map(ModelMapper::toModel)
                .collect(Collectors.toList());

        var userPropertiesResource = new UserPropertiesResource(properties);

        return new ResponseEntity<>(userPropertiesResource, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> getUserProperty(Principal principal, @PathVariable String key) throws BadParameterException {
        if (key == null) {
            throw new BadParameterException("key is required for /user/property/key");
        }
        UserPropertyEntity propertyEntity = this.userPropertyService.getPropertyForUser(principal.getName(), key);
        if (propertyEntity == null) {
            throw new ObjectNotFoundException(String.format("key [%s] not found for user", key));
        }
        UserProperty property = ModelMapper.toModel(propertyEntity);

        var userPropertyResource = new UserPropertyResource(property);

        return new ResponseEntity<>(userPropertyResource, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> setUserProperties(Principal principal, @RequestBody PostUserProperties properties) throws BadParameterException, IOException {
        validateUserProperties(properties);
        List<UserPropertyEntity> propertyEntities = properties.getProperties().stream()
                .map(ModelMapper::toEntity)
                .collect(Collectors.toList());

        this.userPropertyService.setPropertiesForUser(principal.getName(), propertyEntities);

        return ResponseEntity.ok().build();
    }

    private void validateUserProperties(PostUserProperties properties) throws BadParameterException {
        if (properties == null) {
            throw new BadParameterException("properties required in POST /user/properties");
        }
        for (UserProperty property : properties.getProperties()) {
            if (property.getKey() == null) {
                throw new BadParameterException("property key is required in POST /user/properties");
            }
        }
    }

    private void validatateUserForPasswordChange(PostChangePassword postChangePassword, String principalUsername) throws BadParameterException {
        if (!StringUtils.hasText(principalUsername)) {
            throw new BadParameterException("User or username in input is blank or missing");
        }
        if (!StringUtils.hasText(postChangePassword.getNewPassword())) {
            throw new BadParameterException("Input for change password does not include the new password");
        }
        if (!StringUtils.hasText(postChangePassword.getOriginalPassword())) {
            throw new BadParameterException("Input for change password does not include the original password");
        }
        if (principalUsername.length()>255 || postChangePassword.getNewPassword().length() > 255 ||
        postChangePassword.getOriginalPassword().length() > 255) {
            throw new BadParameterException("Input for change passowrd (userName, newPassword, oldPassword) contains an entry longer tahn 255 characters");
        }
    }


    private void validateTokenRequest(PostTokenRequest postTokenRequest) throws BadParameterException {
        if (postTokenRequest == null) {
            throw new BadParameterException("No TokenRequest in request");
        }
        if (!StringUtils.hasText(postTokenRequest.getTokenParameter())) {
            throw new BadParameterException("No parameter in TokenRequest");
        }
        if (!StringUtils.hasText(postTokenRequest.getTokenType())) {
            throw new BadParameterException("No token type in TokenRequest");
        }

        if (postTokenRequest.getTokenParameter().length() > 255) {
            throw new BadParameterException("Token Parameter too long");
        }
    }

    private void validateToken(PostToken postToken) throws BadParameterException {
        if (postToken == null) {
            throw new BadParameterException("No Token in request");
        }
        if (!StringUtils.hasText(postToken.getTokenParameter())) {
            throw new BadParameterException("No parameter in Token");
        }
        if (!StringUtils.hasText(postToken.getTokenType())) {
            throw new BadParameterException("No token type in Token");
        }
        if (!StringUtils.hasText(postToken.getToken())) {
            throw new BadParameterException("No token value in Token");
        }
        if (postToken.getToken().length() > 255) {
            throw new BadParameterException("Token Parameter too long");
        }
    }


}
