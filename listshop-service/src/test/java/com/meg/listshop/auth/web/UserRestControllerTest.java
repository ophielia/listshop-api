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

import static org.junit.Assert.assertNotNull;
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
    private UserDetails userDetails;

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

        UserEntity userAccount = userService.getUserByUserEmail(TestConstants.USER_3_NAME);
        userDetails = new JwtUser(userAccount.getId(),
                TestConstants.USER_3_NAME,
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


    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
