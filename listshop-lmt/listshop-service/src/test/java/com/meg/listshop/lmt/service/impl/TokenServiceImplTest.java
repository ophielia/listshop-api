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
import com.meg.listshop.test.TestConstants;
import com.meg.postoffice.service.MailService;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class TokenServiceImplTest {


    private TokenServiceImpl tokenService;

    @MockBean
    private TokenRepository tokenRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private MailService mailService;

    private UserEntity userAccount;

    @BeforeEach
    public void setUp() throws InvocationTargetException, IllegalAccessException {

        this.tokenService = new TokenServiceImpl(userService,
                tokenRepository,
                mailService);
        this.tokenService.TOKEN_VALIDITY_IN_SECONDS = 86400;

        userAccount = createTestUser(TestConstants.USER_1_ID,
                TestConstants.USER_1_EMAIL);
        userAccount.setEmail(TestConstants.USER_1_EMAIL);

    }

    @Test
    void testGenerateTokenForUser() throws BadParameterException, TemplateException, MessagingException, IOException {
        String userEmail = TestConstants.USER_1_EMAIL;
        Long testStart = new Date().getTime();

        // mock setup
        Mockito.when(userService.getUserByUserEmail(userAccount.getEmail()))
                .thenReturn(userAccount);
        ArgumentCaptor<TokenEntity> tokenCapture = ArgumentCaptor.forClass(TokenEntity.class);
        Mockito.when(tokenRepository.save(tokenCapture.capture())).thenAnswer(i -> i.getArguments()[0]);

        // test call
        tokenService.generateTokenForUser(TokenType.PasswordReset, userEmail);

        // check saved token
        TokenEntity resultToken = tokenCapture.getValue();
        Assertions.assertNotNull(resultToken);
        Assertions.assertNotNull(resultToken.getTokenType());
        Assertions.assertNotNull(resultToken.getUserId());
        Assertions.assertNotNull(resultToken.getTokenValue());
        Assertions.assertNotNull(resultToken.getCreatedOn());

        Assertions.assertEquals(TokenType.PasswordReset, resultToken.getTokenType());
        Assertions.assertEquals(userAccount.getId(), resultToken.getUserId());
        Assertions.assertTrue(resultToken.getCreatedOn().getTime() >= testStart);


    }

    @Test
    void testGenerateTokenForUser_UserNotFoundKO() throws BadParameterException, TemplateException, MessagingException, IOException {
        // encrypted param (matches testuser@test.com)
        String encrpytedEmail = "testuser@test.com";

        // mock setup
        Mockito.when(userService.getUserByUserEmail(userAccount.getEmail()))
                .thenReturn(null);
        ArgumentCaptor<TokenEntity> tokenCapture = ArgumentCaptor.forClass(TokenEntity.class);
        Mockito.when(tokenRepository.save(tokenCapture.capture())).thenAnswer(i -> i.getArguments()[0]);

        // test call
        Assertions.assertThrows(ObjectNotFoundException.class, () -> {
            tokenService.generateTokenForUser(TokenType.PasswordReset, encrpytedEmail);
        });


    }

    @Test
    void testProcessToken() throws BadParameterException, TokenException {
        TokenType tokenType = TokenType.PasswordReset;
        String tokenValue = UUID.randomUUID().toString();
        String tokenParameter = TestConstants.USER_1_EMAIL;
        TokenEntity testTokenEntity = new TokenEntity();
        testTokenEntity.setTokenType(tokenType);
        testTokenEntity.setTokenValue(tokenValue);
        testTokenEntity.setCreatedOn(DateUtils.addHours(new Date(), -1));
        testTokenEntity.setUserId(TestConstants.USER_1_ID);

        Mockito.when(tokenRepository.findByTokenValue(tokenValue))
                .thenReturn(Collections.singletonList(testTokenEntity));
        userService.changePassword(TestConstants.USER_1_ID, tokenParameter);
        ArgumentCaptor<TokenEntity> deleteCapture = ArgumentCaptor.forClass(TokenEntity.class);
        Mockito.doNothing().when(tokenRepository).delete(deleteCapture.capture());

        // service call
        tokenService.processTokenFromUser(tokenType, tokenValue, tokenParameter);

        // assertions
        Assertions.assertNotNull(deleteCapture.getValue());

    }

    @Test
    void testProcessToken_NoTokenFoundKO() throws BadParameterException, TokenException {
        TokenType tokenType = TokenType.PasswordReset;
        String tokenValue = UUID.randomUUID().toString();
        String tokenParameter = TestConstants.USER_1_EMAIL;
        TokenEntity testTokenEntity = new TokenEntity();
        testTokenEntity.setTokenType(tokenType);
        testTokenEntity.setTokenValue(tokenValue);
        testTokenEntity.setCreatedOn(DateUtils.addHours(new Date(), -1));
        testTokenEntity.setUserId(TestConstants.USER_1_ID);

        Mockito.when(tokenRepository.findByTokenValue(tokenValue))
                .thenReturn(null);

        // service call
        Assertions.assertThrows(TokenException.class, () -> {
            tokenService.processTokenFromUser(tokenType, tokenValue, tokenParameter);
        });
    }

    @Test
    void testProcessToken_NoUserIdFoundKO() throws BadParameterException, TokenException {
        TokenType tokenType = TokenType.PasswordReset;
        String tokenValue = UUID.randomUUID().toString();
        String tokenParameter = TestConstants.USER_1_EMAIL;
        TokenEntity testTokenEntity = new TokenEntity();
        testTokenEntity.setTokenType(tokenType);
        testTokenEntity.setTokenValue(tokenValue);
        testTokenEntity.setCreatedOn(DateUtils.addHours(new Date(), -1));
        testTokenEntity.setUserId(null);

        Mockito.when(tokenRepository.findByTokenValue(tokenValue))
                .thenReturn(null);

        // service call
        Assertions.assertThrows(TokenException.class, () -> {
            tokenService.processTokenFromUser(tokenType, tokenValue, tokenParameter);
        });
    }

    private UserEntity createTestUser(Long userId, String userName) {
        UserEntity testUser = new UserEntity(userName, userName);
        testUser.setEmail(userName + "@test.com");
        testUser.setId(userId);
        return testUser;
    }

}
