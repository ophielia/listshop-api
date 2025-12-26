/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.auth.web;

import com.meg.listshop.Application;
import com.meg.listshop.auth.api.model.*;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.CustomUserDetails;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.api.model.TokenType;
import com.meg.listshop.lmt.data.repository.TokenRepository;
import com.meg.listshop.test.TestConstants;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DevicePlatform;
import org.springframework.mobile.device.DeviceType;
import org.springframework.mobile.device.LiteDevice;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ExtendWith(SpringExtension.class)
@Testcontainers
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
@Sql(value = {"/sql/com/meg/atable/auth/api/UserRestControllerTest-rollback.sql",
        "/sql/com/meg/atable/auth/api/CopyUser.sql",
        "/sql/com/meg/atable/auth/api/UserRestControllerTest.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserRestControllerTest {

    private static final Long USER_WITH_PROPERTIES_ID = 999L;
    private static final String USER_WITH_PROPERTIES_NAME = "rufus@barkingmad.com";
    @Container
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));
    private MockMvc mockMvc;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;
    private UserDetails userDetailsChangePassword;
    private UserDetails userDetailsAnotherChangePassword;
    private UserDetails userWithoutProperties;

    private UserDetails userWithProperties;
    private UserDetails userToDelete;
    private UserDetails userNotFound;

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

        Assertions.assertNotNull("the JSON message converter must not be null");
    }

    @BeforeEach
    @WithMockUser
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        UserEntity userAccount = userService.getUserByUserEmail(TestConstants.USER_1_EMAIL);
        userDetailsChangePassword = new CustomUserDetails(userAccount.getId(),
                TestConstants.USER_1_EMAIL,
                null,
                "Passw0rd", // $2a$10$RFahccrkDPR1aUHfyS457Oc7n.2f7wU/sDUXQ.99wOvNL3xzaiPxK
                null,
                true,
                null);

        userDetailsAnotherChangePassword = new CustomUserDetails(TestConstants.USER_5_ID,
                TestConstants.USER_5_NAME,
                null,
                "Passw0rd",
                null,
                true,
                null);

        userWithoutProperties = new CustomUserDetails(TestConstants.USER_4_ID,
                TestConstants.USER_4_NAME,
                null,
                "Passw0rd",
                null,
                true,
                null);

        userWithProperties = new CustomUserDetails(USER_WITH_PROPERTIES_ID,
                USER_WITH_PROPERTIES_NAME,
                null,
                "Passw0rd",
                null,
                true,
                null);

        userToDelete = new CustomUserDetails(userAccount.getId(),
                "bravenewworld@test.com",
                "bravenewworld@test.com",
                "Passw0rd",
                null,
                true,
                null);

        userNotFound = new CustomUserDetails(userAccount.getId(),
                "notappearinginthisfilm",
                "notappearinginthisfilm",
                "Passw0rd",
                null,
                true,
                null);
    }

    @Test
    void testCreateUser_KO() throws Exception {
        Device device = LiteDevice.from(DeviceType.NORMAL, DevicePlatform.IOS);
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
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @Test
    void testCreateUser() throws Exception {
        ClientDeviceInfo deviceInfo = new ClientDeviceInfo();
        deviceInfo.setModel("dummy device");
        deviceInfo.setBuildNumber("99.9");
        deviceInfo.setClientVersion("1.11");
        final String username = "dXNlcm5hbWU=";
        final String email = "ZW1haWw=";
        final String password = "UGFzc3cwcmQ=";
        User user = new User(username, email);
        user.setPassword(password);
        PutCreateUser createPayload = new PutCreateUser();
        createPayload.setUser(user);
        createPayload.setDeviceInfo(deviceInfo);
        String payload = json(createPayload);

        mockMvc.perform(post("/user")
                        .contentType(contentType)
                        .content(payload)
                        .characterEncoding("utf-8"))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    @Test
    void testGetToken() throws Exception {
        // get tokens -> count
        var tokenCountBefore = tokenRepository.count();

        // make payload
        PostTokenRequest tokenRequest = new PostTokenRequest();
        tokenRequest.setTokenType(TokenType.PasswordReset.toString());
        tokenRequest.setTokenParameter(TestConstants.USER_1_EMAIL);
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
        Assertions.assertEquals(tokenCountBefore + 1, tokenCountAfter, "token count should have increased by 1");
    }

    @Test
    void testPostToken() throws Exception {
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
        Assertions.assertEquals(tokenCountBefore - 1, tokenCountAfter, "token count should have decreased by 1");
    }

    @Test
    void testPostToken_NoPasswordKO() throws Exception {
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
    void testPostToken_NoTokenTypeKO() throws Exception {
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
    void testPostToken_TokenNotFoundKO() throws Exception {
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
    void testPostToken_TokenExpiredKO() throws Exception {
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
    void testGetUser() throws Exception {

        String url = "/user";
        mockMvc.perform(get(url)
                        .with(user(userDetailsChangePassword))
                        .contentType(contentType)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email", Matchers.equalToIgnoringCase("testuser@testuser.com")))
                .andExpect(jsonPath("$.user.roles", Matchers.contains("ROLE_USER")))
                .andDo(print());


    }

    @Test
    @WithMockUser
    void testChangePassword() throws Exception {
        byte[] originalPasswordBytes = "Passw0rd".getBytes(StandardCharsets.UTF_8);
        String originalPasswordEncoded = Base64.getEncoder().encodeToString(originalPasswordBytes);
        byte[] newPasswordBytes = "newpassword".getBytes(StandardCharsets.UTF_8);
        String newPasswordEncoded = Base64.getEncoder().encodeToString(newPasswordBytes);


        // get user before password change in order to compare password
        UserEntity userAccount = userService.getUserByUserEmail(TestConstants.USER_5_NAME);
        var passwordBeforeChange = userAccount.getPassword();
        long startTime = new Date().getTime();

        // make payload
        var postChangePassword = new PostChangePassword();
        postChangePassword.setNewPassword(newPasswordEncoded);
        postChangePassword.setOriginalPassword(originalPasswordEncoded);
        String payload = json(postChangePassword);

        // make call - ensure 200 as return code
        String url = "/user/password";
        mockMvc.perform(post(url)
                        .with(user(userDetailsAnotherChangePassword))
                        .contentType(contentType)
                        .content(payload)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk());

        // get user info after password change
        UserEntity userAccountAfter = userService.getUserByUserEmail(TestConstants.USER_5_NAME);
        var passwordAfterChange = userAccountAfter.getPassword();
        var passwordResetDate = userAccountAfter.getLastPasswordResetDate();

        // ensure password has changed, and password reset is updated
        Assertions.assertNotEquals(passwordBeforeChange, passwordAfterChange, "password should have changed");
        Assertions.assertTrue(passwordResetDate.getTime() >= startTime, "reset date should be equal or after start of test");
    }

    @Test
    @WithMockUser
    void testChangePassword_badOriginalPassKO() throws Exception {
        byte[] originalPasswordBytes = "badPasswordBadPassword".getBytes(StandardCharsets.UTF_8);
        String originalPasswordEncoded = Base64.getEncoder().encodeToString(originalPasswordBytes);
        byte[] newPasswordBytes = "newpassword".getBytes(StandardCharsets.UTF_8);
        String newPasswordEncoded = Base64.getEncoder().encodeToString(newPasswordBytes);


        // get user before password change in order to compare password
        UserEntity userAccount = userService.getUserByUserEmail(TestConstants.USER_1_EMAIL);
        var passwordBeforeChange = userAccount.getPassword();

        // make payload
        var postChangePassword = new PostChangePassword();
        postChangePassword.setNewPassword(newPasswordEncoded);
        postChangePassword.setOriginalPassword(originalPasswordEncoded);
        String payload = json(postChangePassword);

        // make call - ensure 200 as return code
        String url = "/user/password";
        mockMvc.perform(post(url)
                        .with(user(userDetailsChangePassword))
                        .contentType(contentType)
                        .content(payload)
                        .characterEncoding("utf-8"))
                .andExpect(status().is4xxClientError());

        // get user info after password change
        UserEntity userAccountAfter = userService.getUserByUserEmail(TestConstants.USER_1_EMAIL);
        var passwordAfterChange = userAccountAfter.getPassword();

        // ensure password has not changed
        Assertions.assertEquals(passwordBeforeChange, passwordAfterChange, "password should not have changed");
    }

    @Test
    @WithMockUser
    void testDeleteUser_NotFoundKO() throws Exception {

        // make call - ensure 200 as return code
        String url = "/user";
        mockMvc.perform(MockMvcRequestBuilders.delete(url)
                        .with(user(userNotFound))
                        .contentType(contentType)
                        .characterEncoding("utf-8"))
                .andExpect(status().isNotFound());

    }

    @Test
    @WithMockUser
    void testDeleteUser() throws Exception {

        // make call - ensure 200 as return code
        String url = "/user";
        mockMvc.perform(MockMvcRequestBuilders.delete(url)
                        .with(user(userToDelete))
                        .contentType(contentType)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk());

        UserEntity user = userService.getUserByUserEmail("bravenewworld@test.com");
        Assertions.assertNull(user);
    }

    @Test
    void testUserNameIsTaken() throws Exception {
        ListShopPayload payload = new ListShopPayload();
        List<String> parameters = new ArrayList<>();
        parameters.add("name@whichdoesntexist.com");
        payload.setParameters(parameters);

        String payloadAsString = json(payload);

        // make call - ensure 200 as return code
        String url = "/user/name";
        MvcResult result = mockMvc.perform(post(url)
                        .contentType(contentType)
                        .content(payloadAsString)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andReturn();

        String resultBody = result.getResponse().getContentAsString();
        Assertions.assertEquals("false", resultBody, "Expect false for nonsense name");

        ListShopPayload payloadForExistingUser = new ListShopPayload();
        parameters = new ArrayList<>();
        parameters.add(TestConstants.USER_5_NAME);
        payloadForExistingUser.setParameters(parameters);

        payloadAsString = json(payloadForExistingUser);
        result = mockMvc.perform(post(url)
                        .contentType(contentType)
                        .content(payloadAsString)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andReturn();

        resultBody = result.getResponse().getContentAsString();
        Assertions.assertEquals("true", resultBody, "Expect true for existing name");
    }

    @Test
    void testMinimumClientVersion() throws Exception {
        // make call - ensure 200 as return code
        String url = "/user/client/version";
        mockMvc.perform(get(url)
                        .contentType(contentType)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ios_min_version", Matchers.equalTo("1.0")))
                .andExpect(jsonPath("$.android_min_version", Matchers.equalTo("1.0")))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void testGetUserProperties() throws Exception {

        String url = "/user/properties";
        mockMvc.perform(get(url)
                        .with(user(userDetailsChangePassword))
                        .contentType(contentType)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_properties", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.user_properties[0].key", Matchers.equalTo("test_property")))
                .andExpect(jsonPath("$.user_properties[0].value", Matchers.equalTo("ho hum value")))
                .andExpect(jsonPath("$.user_properties[1].key", Matchers.equalTo("another_property")))
                .andExpect(jsonPath("$.user_properties[1].value", Matchers.equalTo("good value")));

    }

    @Test
    @WithMockUser
    void testGetUserProperties_NoProperties() throws Exception {

        String url = "/user/properties";
        mockMvc.perform(get(url)
                        .with(user(userDetailsAnotherChangePassword))
                        .contentType(contentType)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_properties", Matchers.hasSize(0)));


    }

    @Test
    @WithMockUser
    void testGetUserProperty() throws Exception {

        String url = "/user/properties/key/test_property";
        mockMvc.perform(get(url)
                        .with(user(userDetailsChangePassword))
                        .contentType(contentType)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_property.key", Matchers.equalTo("test_property")))
                .andExpect(jsonPath("$.user_property.value", Matchers.equalTo("ho hum value")));

    }

    @Test
    @WithMockUser
    void testGetUserProperty_NoProperty() throws Exception {

        String url = "/user/properties/key/test_missing_property";
        mockMvc.perform(get(url)
                        .with(user(userDetailsChangePassword))
                        .contentType(contentType)
                        .characterEncoding("utf-8"))
                .andExpect(status().isNotFound());

    }

    @Test
    @WithMockUser
    void testSetUserProperties() throws Exception {
        UserProperty property1 = new UserProperty("key1", "value1");
        UserProperty property2 = new UserProperty("key2", "value2");
        PostUserProperties propertiesPost = new PostUserProperties();
        propertiesPost.setProperties(Arrays.asList(property1, property2));

        String url = "/user/properties";
        mockMvc.perform(post(url)
                        .with(user(userWithoutProperties))
                        .contentType(contentType)
                        .content(json(propertiesPost))
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk());

        // now retrieve what we just saved
        mockMvc.perform(get(url)
                        .with(user(userWithoutProperties))
                        .contentType(contentType)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_properties", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.user_properties[*].key", Matchers.hasItem("key1")))
                .andExpect(jsonPath("$.user_properties[*].value", Matchers.hasItem("value1")))
                .andExpect(jsonPath("$.user_properties[*].key", Matchers.hasItem("key2")))
                .andExpect(jsonPath("$.user_properties[*].value", Matchers.hasItem("value2")))
        ;


    }

    @Test
    @WithMockUser
    void testSetUserProperties_Existing() throws Exception {
        UserProperty property1 = new UserProperty("key1", "value1");
        UserProperty property2 = new UserProperty("key2", "value2");
        PostUserProperties propertiesPost = new PostUserProperties();
        propertiesPost.setProperties(Arrays.asList(property1, property2));

        String url = "/user/properties";
        mockMvc.perform(post(url)
                        .with(user(userWithProperties))
                        .contentType(contentType)
                        .content(json(propertiesPost))
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk());

        // now retrieve what we just saved
        mockMvc.perform(get(url)
                        .with(user(userWithProperties))
                        .contentType(contentType)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_properties", Matchers.hasSize(4)))
                .andExpect(jsonPath("$.user_properties[*].key", Matchers.hasItem("key1")))
                .andExpect(jsonPath("$.user_properties[*].key", Matchers.hasItem("key2")))
                .andExpect(jsonPath("$.user_properties[*].key", Matchers.hasItem("test_property")))
                .andExpect(jsonPath("$.user_properties[*].key", Matchers.hasItem("another_property")))
                .andExpect(jsonPath("$.user_properties[*].value", Matchers.hasItem("value1")))
                .andExpect(jsonPath("$.user_properties[*].value", Matchers.hasItem("value2")))
                .andExpect(jsonPath("$.user_properties[*].value", Matchers.hasItem("ho hum value")))
                .andExpect(jsonPath("$.user_properties[*].value", Matchers.hasItem("good value")))
        ;
    }

    @Test
    @WithMockUser
    void testSetUserProperties_Update() throws Exception {
        UserProperty property1 = new UserProperty("test_property", "scintillating value");
        PostUserProperties propertiesPost = new PostUserProperties();
        propertiesPost.setProperties(Arrays.asList(property1));

        String url = "/user/properties";
        mockMvc.perform(post(url)
                        .with(user(userWithProperties))
                        .contentType(contentType)
                        .content(json(propertiesPost))
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk());

        // now retrieve what we just saved
        mockMvc.perform(get(url)
                        .with(user(userWithProperties))
                        .contentType(contentType)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_properties[*].key", Matchers.hasItem("test_property")))
                .andExpect(jsonPath("$.user_properties[*].value", Matchers.hasItem("scintillating value")))
        ;


    }

    @Test
    @WithMockUser
    void testSetUserProperties_UpdateToNull() throws Exception {
        UserProperty property1 = new UserProperty("another_property", null);
        PostUserProperties propertiesPost = new PostUserProperties();
        propertiesPost.setProperties(Arrays.asList(property1));

        String url = "/user/properties";
        mockMvc.perform(post(url)
                        .with(user(userWithProperties))
                        .contentType(contentType)
                        .content(json(propertiesPost))
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk());

        // now retrieve what we just saved
        mockMvc.perform(get(url)
                        .with(user(userWithProperties))
                        .contentType(contentType)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_properties[*].key", Matchers.not(Matchers.hasItem("another_property"))))
        ;


    }

    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
