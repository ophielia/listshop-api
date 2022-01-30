package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.lmt.api.exception.BadParameterException;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.api.model.TokenType;
import com.meg.listshop.lmt.data.entity.TokenEntity;
import com.meg.listshop.lmt.data.repository.TokenRepository;
import com.meg.listshop.test.TestConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;


@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class TokenServiceImplTest {

    private TokenServiceImpl tokenService;

    @MockBean
    private TokenRepository tokenRepository;

    @MockBean
    private UserService userService;

    private UserEntity userAccount;

    @Before
    public void setUp() {

        tokenService = new TokenServiceImpl(userService,
                tokenRepository);

        userAccount = createTestUser(TestConstants.USER_1_ID,
                TestConstants.USER_1_NAME);

    }

    @Test
    public void testGenerateTokenForUser() throws BadParameterException {
        // encrypted param (matches testuser@test.com)
        String encrpytedEmail = "dGVzdHVzZXJAdGVzdC5jb20=";
        Date testStart = new Date();

        // mock setup
        Mockito.when(userService.getUserByUserEmail(userAccount.getEmail()))
                .thenReturn(userAccount);
        ArgumentCaptor<TokenEntity> tokenCapture = ArgumentCaptor.forClass(TokenEntity.class);
        Mockito.when(tokenRepository.save(tokenCapture.capture())).thenAnswer(i -> i.getArguments()[0]);

        // test call
        tokenService.generateTokenForUser(TokenType.PasswordReset, encrpytedEmail);

        // check saved token
        TokenEntity resultToken = tokenCapture.getValue();
        Assert.assertNotNull(resultToken);
        Assert.assertNotNull(resultToken.getTokenType());
        Assert.assertNotNull(resultToken.getUserId());
        Assert.assertNotNull(resultToken.getTokenValue());
        Assert.assertNotNull(resultToken.getCreatedOn());

        Assert.assertEquals(TokenType.PasswordReset, resultToken.getTokenType());
        Assert.assertEquals(userAccount.getId(), resultToken.getUserId());
        Assert.assertTrue(resultToken.getCreatedOn().after(testStart));


    }

    @Test(expected = BadParameterException.class)
    public void testGenerateTokenForUser_EncryptionKO() throws BadParameterException {
        // encrypted param (matches testuser@test.com)
        String encrpytedEmail = "badEncryptedEmail";
        Date testStart = new Date();

        // mock setup
        Mockito.when(userService.getUserByUserEmail(userAccount.getEmail()))
                .thenReturn(userAccount);
        ArgumentCaptor<TokenEntity> tokenCapture = ArgumentCaptor.forClass(TokenEntity.class);
        Mockito.when(tokenRepository.save(tokenCapture.capture())).thenAnswer(i -> i.getArguments()[0]);

        // test call
        tokenService.generateTokenForUser(TokenType.PasswordReset, encrpytedEmail);


    }

    @Test(expected = ObjectNotFoundException.class)
    public void testGenerateTokenForUser_UserNotFoundKO() throws BadParameterException {
        // encrypted param (matches testuser@test.com)
        String encrpytedEmail = "dGVzdHVzZXJAdGVzdC5jb20=";
        Date testStart = new Date();

        // mock setup
        Mockito.when(userService.getUserByUserEmail(userAccount.getEmail()))
                .thenReturn(null);
        ArgumentCaptor<TokenEntity> tokenCapture = ArgumentCaptor.forClass(TokenEntity.class);
        Mockito.when(tokenRepository.save(tokenCapture.capture())).thenAnswer(i -> i.getArguments()[0]);

        // test call
        tokenService.generateTokenForUser(TokenType.PasswordReset, encrpytedEmail);


    }

    private UserEntity createTestUser(Long userId, String userName) {
        UserEntity testUser = new UserEntity(userName, userName);
        testUser.setEmail(userName + "@test.com");
        testUser.setId(userId);
        return testUser;
    }

}