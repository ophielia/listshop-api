package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.lmt.api.exception.BadParameterException;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.api.model.TokenType;
import com.meg.listshop.lmt.data.entity.TokenEntity;
import com.meg.listshop.lmt.data.repository.TokenRepository;
import com.meg.listshop.lmt.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by margaretmartin on 18/12/2017.
 */
@Service
public class TokenServiceImpl implements TokenService {

    private UserService userService;

    private TokenRepository tokenRepository;

    @Autowired
    public TokenServiceImpl(UserService userService,
                            TokenRepository tokenRepository) {
        this.userService = userService;
        this.tokenRepository = tokenRepository;
    }

    public void generateTokenForUser(TokenType tokenType, String encryptedEmail) throws BadParameterException {
        String decodedUsername = null;
        try {
            byte[] usernameBytes = Base64.getDecoder().decode(encryptedEmail);
            decodedUsername = new String(usernameBytes);
        } catch (IllegalArgumentException iae) {
            throw new BadParameterException("Cannot decrypt parameter", iae);
        }

        // find user
        UserEntity user = userService.getUserByUserEmail(decodedUsername);
        if (user == null) {
            throw new ObjectNotFoundException(String.format("User not found for username: %s", decodedUsername));
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
