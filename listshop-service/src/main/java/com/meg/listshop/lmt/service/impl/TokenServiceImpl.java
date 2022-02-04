/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.lmt.api.exception.BadParameterException;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.api.exception.TokenException;
import com.meg.listshop.lmt.api.model.TokenType;
import com.meg.listshop.lmt.data.entity.TokenEntity;
import com.meg.listshop.lmt.data.repository.TokenRepository;
import com.meg.listshop.lmt.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by margaretmartin on 18/12/2017.
 */
@Service
public class TokenServiceImpl implements TokenService {


    @Value("${token.validity.period:86400}")
    int TOKEN_VALIDITY_IN_SECONDS;

    private UserService userService;

    private TokenRepository tokenRepository;

    @Autowired
    public TokenServiceImpl(UserService userService,
                            TokenRepository tokenRepository) {
        this.userService = userService;
        this.tokenRepository = tokenRepository;
    }

    public void processTokenFromUser(TokenType type, String tokenValue, String tokenParameter) throws BadParameterException, TokenException {
        // NOTE: as there is currently only one token type, that is implemented here.  When / if we
        // have more token types, we can select handlers or the such to handle different logic.
        // lookup token
        List<TokenEntity> tokenEntityList = tokenRepository.findByTokenValue(tokenValue);
        if (tokenEntityList == null || tokenEntityList.size() != 1) {
            throw new TokenException("Unique matching token not found.");
        }
        var token = tokenEntityList.get(0);
        long age = new Date().getTime() - token.getCreatedOn().getTime();
        if (age > (TOKEN_VALIDITY_IN_SECONDS * 1000)) {
            throw new TokenException("Token is no longer valid.");
        }
        if (token.getUserId() == null) {
            throw new BadParameterException("Token is corrupted.");
        }
        if (tokenParameter == null) {
            throw new BadParameterException("Token is incomplete.");
        }
        // change password for user to that contained in token parameter
        userService.changePassword(token.getUserId(), tokenParameter);

        // delete the token
        tokenRepository.delete(token);
    }

    public void generateTokenForUser(TokenType tokenType, String userEmail) throws BadParameterException {
        // find user
        UserEntity user = userService.getUserByUserEmail(userEmail);
        if (user == null) {
            throw new ObjectNotFoundException(String.format("User not found for username: %s", userEmail));
        }

        // generate the actual token
        String token = generateUniqueToken();


        // create token
        var tokenEntity = new TokenEntity();
        tokenEntity.setUserId(user.getId());
        tokenEntity.setTokenType(tokenType);
        tokenEntity.setTokenValue(token);
        tokenEntity.setCreatedOn(new Date());
        tokenRepository.save(tokenEntity);
        // send email
        // MM fill this in when it exists!

    }

    private String generateUniqueToken() {
        String token = null;
        List<TokenEntity> existing = null;
        do {
            token = UUID.randomUUID().toString();
            existing = tokenRepository.findByTokenValue(token);
        } while (!CollectionUtils.isEmpty(existing));
        return token;
    }
}
