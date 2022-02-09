/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.auth.web;

import com.meg.listshop.Application;
import com.meg.listshop.auth.api.model.PostToken;
import com.meg.listshop.auth.api.model.PostTokenRequest;
import com.meg.listshop.auth.api.model.PutCreateUser;
import com.meg.listshop.auth.api.model.User;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.auth.service.impl.JwtUser;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.api.model.TokenType;
import com.meg.listshop.lmt.data.repository.TokenRepository;
import com.meg.listshop.test.TestConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceType;
import org.springframework.mobile.device.LiteDevice;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
@Sql(value = {"/sql/com/meg/atable/auth/api/UserRestControllerTest-rollback.sql",
        "/sql/com/meg/atable/auth/api/UserRestControllerTest.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class UserRestControllerTest {

    @ClassRule
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));
    private MockMvc mockMvc;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;
    private UserDetails userDetailsChangePassword;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserService userService;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        this.mappingJackson2HttpMessageConverter = Arrays.stream(converters)
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null");
    }

    @Before
    @WithMockUser
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        UserEntity userAccount = userService.getUserByUserEmail(TestConstants.USER_1_EMAIL);
        userDetailsChangePassword = new JwtUser(userAccount.getId(),
                TestConstants.USER_1_EMAIL,
                null,
                "Passw0rd",
                null,
                true,
                null);

    }


    @Test
    public void testCreateUser() throws Exception {
        Device device = new LiteDevice(DeviceType.NORMAL);
        final String username = "dXNlcm5hbWU=";
        final String email = "ZW1haWw=";
        final String password = "UGFzc3cwcmQ=";
        User user = new User(username, email);
        user.setPassword(password);
        final String userjson = json(user);
        final String deviceJson = json(device);

        mockMvc.perform(post("/user")
                        .contentType(contentType)
                        .content(userjson)
                        .characterEncoding("utf-8"))
                //.content(deviceJson))
                .andDo(print());
    }

    @Test
    public void testGetToken() throws Exception {
        // get tokens -> count
        var tokenCountBefore = tokenRepository.count();

        // make payload
        PostTokenRequest tokenRequest = new PostTokenRequest();
        tokenRequest.setTokenType(TokenType.PasswordReset.toString());
        tokenRequest.setTokenParameter("testuser");
        String payload = json(tokenRequest);

        // make call - ensure 200 as return code
        String url = "/user/token/tokenrequest";
        mockMvc.perform(post(url)
                        .contentType(contentType)
                        .content(payload)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk());

        // get tokens -> count
        var tokenCountAfter = tokenRepository.count();

        // ensure tokens have increased by 1
        Assert.assertEquals("token count should have increased by 1", tokenCountBefore + 1, tokenCountAfter);
    }

    @Test
    public void testPostToken() throws Exception {
        // get tokens -> count
        var tokenCountBefore = tokenRepository.count();

        // make payload
        PostToken postToken = new PostToken();
        postToken.setTokenType(TokenType.PasswordReset.toString());
        postToken.setTokenParameter("new password");
        postToken.setToken("token_password_reset");
        String payload = json(postToken);

        // make call - ensure 200 as return code
        String url = "/user/token";
        mockMvc.perform(post(url)
                        .contentType(contentType)
                        .content(payload)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk());

        // get tokens -> count
        var tokenCountAfter = tokenRepository.count();

        // ensure tokens have increased by 1
        Assert.assertEquals("token count should have decreased by 1", tokenCountBefore - 1, tokenCountAfter);
    }

    @Test
    public void testPostToken_NoPasswordKO() throws Exception {
        // get tokens -> count
        var tokenCountBefore = tokenRepository.count();

        // make payload
        PostToken postToken = new PostToken();
        postToken.setTokenType(TokenType.PasswordReset.toString());
        // no password in post token
        postToken.setToken("token_password_reset");
        String payload = json(postToken);

        // make call - ensure 400 as return code
        String url = "/user/token";
        mockMvc.perform(post(url)
                        .contentType(contentType)
                        .content(payload)
                        .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void testPostToken_NoTokenTypeKO() throws Exception {
        // get tokens -> count
        var tokenCountBefore = tokenRepository.count();

        // make payload
        PostToken postToken = new PostToken();
        // no token type
        postToken.setTokenParameter("new password");
        postToken.setToken("token_password_reset");
        String payload = json(postToken);

        // make call - ensure 400 as return code
        String url = "/user/token";
        mockMvc.perform(post(url)
                        .contentType(contentType)
                        .content(payload)
                        .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void testPostToken_TokenNotFoundKO() throws Exception {
        // get tokens -> count
        var tokenCountBefore = tokenRepository.count();

        // make payload
        PostToken postToken = new PostToken();
        postToken.setTokenType("PasswordReset");
        postToken.setTokenParameter("new password");
        postToken.setToken("token_password_resetnotfound");
        String payload = json(postToken);

        // make call - ensure 400 as return code
        String url = "/user/token";
        mockMvc.perform(post(url)
                        .contentType(contentType)
                        .content(payload)
                        .characterEncoding("utf-8"))
                .andExpect(status().isNotFound());

    }

    @Test
    public void testPostToken_TokenExpiredKO() throws Exception {
        // get tokens -> count
        var tokenCountBefore = tokenRepository.count();

        // make payload
        PostToken postToken = new PostToken();
        postToken.setTokenType("PasswordReset");
        postToken.setTokenParameter("new password");
        postToken.setToken("token_password_reset_expired");
        String payload = json(postToken);

        // make call - ensure 400 as return code
        String url = "/user/token";
        mockMvc.perform(post(url)
                        .contentType(contentType)
                        .content(payload)
                        .characterEncoding("utf-8"))
                .andExpect(status().isNotFound());

    }


    @Test
    @WithMockUser
    public void testChangePassword() throws Exception {

        // get user before password change in order to compare password
        UserEntity userAccount = userService.getUserByUserEmail(TestConstants.USER_1_EMAIL);
        var passwordBeforeChange = userAccount.getPassword();
        long startTime = new Date().getTime();

        // make payload
        var putCreateUser = new PutCreateUser();
        var user = new User(userAccount.getEmail(), userAccount.getEmail());
        user.setPassword("NEWPASSWORD");
        putCreateUser.setUser(user);
        String payload = json(putCreateUser);

        // make call - ensure 200 as return code
        String url = "/user/password";
        mockMvc.perform(post(url)
                        .with(user(userDetailsChangePassword))
                        .contentType(contentType)
                        .content(payload)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk());

        // get user info after password change
        UserEntity userAccountAfter = userService.getUserByUserEmail(TestConstants.USER_1_EMAIL);
        var passwordAfterChange = userAccountAfter.getPassword();
        var passwordResetDate = userAccountAfter.getLastPasswordResetDate();

        // ensure password has changed, and password reset is updated
        Assert.assertNotEquals("password should have changed", passwordBeforeChange, passwordAfterChange);
        Assert.assertTrue("reset date should be equal or after start of test", passwordResetDate.getTime() >= startTime);
    }

    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
