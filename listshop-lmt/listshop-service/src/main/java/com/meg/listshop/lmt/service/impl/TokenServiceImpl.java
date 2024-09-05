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
import com.meg.postoffice.api.model.EmailParameters;
import com.meg.postoffice.api.model.EmailType;
import com.meg.postoffice.service.MailService;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import jakarta.mail.MessagingException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by margaretmartin on 18/12/2017.
 */
@Service
public class TokenServiceImpl implements TokenService {

    private static final Logger  logger = LoggerFactory.getLogger(TokenServiceImpl.class);

    @Value("${listservice.email.sender:support@the-list-shop.com}")
    String EMAIL_SENDER;

    @Value("${listservice.email.passwordreset.subject:Password Reset}")
    String PASSWORD_RESET_SUBJECT;

    @Value("${listservice.email.static.root:https://nastyvarmits.fr/api/static}")
    String STATIC_RESOURCE_ROOT;

    @Value("${listservice.email.passwordreset.server.root:http://localhost:4200/user/gateway/PasswordReset}")
    String TOKEN_ROOT;

    @Value("${token.validity.period:86400}")
    int TOKEN_VALIDITY_IN_SECONDS;

    private UserService userService;

    private TokenRepository tokenRepository;

    private MailService mailService;

    @Autowired
    public TokenServiceImpl(UserService userService,
                            TokenRepository tokenRepository,
                            MailService mailService) {
        this.userService = userService;
        this.tokenRepository = tokenRepository;
        this.mailService = mailService;
    }

    public void processTokenFromUser(TokenType type, String tokenValue, String tokenParameter) throws BadParameterException, TokenException {
        // NOTE: as there is currently only one token type, that is implemented here.  When / if we
        // have more token types, we can select handlers or the such to handle different logic.
        // lookup token
        List<TokenEntity> tokenEntityList = tokenRepository.findByTokenValue(tokenValue);
        if (tokenEntityList == null || tokenEntityList.size() != 1) {
            logger.warn(String.format("Token[%s] not found.", tokenValue));
            throw new TokenException("Unique matching token not found.");
        }
        var token = tokenEntityList.get(0);
        long age = new Date().getTime() - token.getCreatedOn().getTime();
        if (age > (TOKEN_VALIDITY_IN_SECONDS * 1000)) {
            logger.warn(String.format("Token[%s] invalid.", tokenValue));
            throw new TokenException("Token is no longer valid.");
        }
        if (token.getUserId() == null) {
            logger.warn(String.format("Token[%s] corrupted.", tokenValue));
            throw new BadParameterException("Token is corrupted.");
        }
        if (tokenParameter == null) {
            logger.warn(String.format("Token[%s] incomplete.", tokenValue));
            throw new BadParameterException("Token is incomplete.");
        }
        logger.debug(String.format("Will change password for user[%s]", token.getUserId()));
        // change password for user to that contained in token parameter
        userService.changePassword(token.getUserId(), tokenParameter);

        // delete the token
        tokenRepository.delete(token);
    }

    public void generateTokenForUser(TokenType tokenType, String userEmail) throws BadParameterException, TemplateException, MessagingException, IOException {

        // find user
        UserEntity user = userService.getUserByUserEmail(userEmail);
        if (user == null) {
            throw new ObjectNotFoundException(String.format("User not found for username: %s", userEmail));
        }
        logger.info(String.format("Begin generateTokenForUser: user[%s], tokenType[%s]", user.getId(), tokenType));

        // generate the actual token
        String token = generateUniqueToken();
        String tokenUrl = TOKEN_ROOT + "/" + token;

        // create token
        var tokenEntity = new TokenEntity();
        tokenEntity.setUserId(user.getId());
        tokenEntity.setTokenType(tokenType);
        tokenEntity.setTokenValue(token);
        tokenEntity.setCreatedOn(new Date());
        tokenRepository.save(tokenEntity);

        // send email
        var parameters = new EmailParameters();
        parameters.setEmailType(EmailType.ResetPassword);
        parameters.setReceiver(user.getUsername());
        parameters.setSender(EMAIL_SENDER);
        parameters.setSubject(PASSWORD_RESET_SUBJECT);
        parameters.addParameter("staticRoot", STATIC_RESOURCE_ROOT);
        parameters.addParameter("tokenLink", tokenUrl);
        parameters.addParameter("supportEmail", EMAIL_SENDER);

        mailService.processEmail(parameters);
        logger.debug(String.format("Finished generateTokenForUser: user[%s], tokenType[%s]", user.getId(), tokenType));
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
