/*
 * The List Shop
 *
 * Copyright (c) 2022-2026.
 */
package com.meg.listshop.auth.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.meg.listshop.Application;
import com.meg.listshop.auth.api.model.*;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.api.model.TokenType;
import com.meg.listshop.lmt.data.repository.TokenRepository;
import com.meg.listshop.test.TestConstants;
import com.meg.listshop.test.TestUtils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.annotation.PostConstruct;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalToIgnoringCase;

@ExtendWith(SpringExtension.class)
@Testcontainers
@SpringBootTest(classes = Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(value = {"/sql/com/meg/atable/auth/api/UserRestControllerTest-rollback.sql",
        "/sql/com/meg/atable/auth/api/CopyUser.sql",
        "/sql/com/meg/atable/auth/api/UserRestControllerTest.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserRestControllerTest {

    private static final String USER_WITH_PROPERTIES_TOKEN = "tokenUserWithProperties";

    @Container
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    @LocalServerPort
    public int serverPort;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserService userService;


    @PostConstruct
    public void initRestAssured() {
        RestAssured.port = serverPort;
        RestAssured.urlEncodingEnabled = false;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void testGetUser() {

        String url = "/user";
        given()
                .header(TestUtils.authToken(TestConstants.USER_1_TOKEN))
                .log().all()
                .when()
                .get(url)
                .then()
                .statusCode(200)
                .body("user.email", equalToIgnoringCase("testuser@testuser.com"))
                .log().all();

    }

    @Test
    void testCreateUser_KO() throws Exception {
        final String username = "dXNlcm5hbWU=";
        final String email = "ZW1haWw=";
        final String password = "UGFzc3cwcmQ=";
        User user = new User(username, email);
        user.setPassword(password);
        final String userjson = jacksonJson(user);

        given()
                .contentType(ContentType.JSON)
                .when()
                .body(userjson)
                .log().all()
                .post("/user")
                .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
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
        String payload = jacksonJson(createPayload);

        given()
                .contentType(ContentType.JSON)
                .when()
                .body(payload)
                .log().all()
                .post("/user")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("user.email", equalToIgnoringCase("email"));
    }

    @Test
    void testGetToken() throws Exception {
        // get tokens -> count
        var tokenCountBefore = tokenRepository.count();

        // make payload
        PostTokenRequest tokenRequest = new PostTokenRequest();
        tokenRequest.setTokenType(TokenType.PasswordReset.toString());
        tokenRequest.setTokenParameter(TestConstants.USER_1_EMAIL);
        String payload = jacksonJson(tokenRequest);

        // make call - ensure 200 as return code
        String url = "/user/token/tokenrequest";
        given()
                .contentType(ContentType.JSON)
                .when()
                .body(payload)
                .log().all()
                .post(url)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value());


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
        String payload = jacksonJson(postToken);

        // make call - ensure 200 as return code
        String url = "/user/token";
        given()
                .contentType(ContentType.JSON)
                .when()
                .body(payload)
                .log().all()
                .post(url)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value());

        // get tokens -> count
        var tokenCountAfter = tokenRepository.count();

        // ensure tokens have increased by 1
        Assertions.assertEquals(tokenCountBefore - 1, tokenCountAfter, "token count should have decreased by 1");
    }


    @Test
    void testPostToken_NoPasswordKO() throws Exception {
        // make payload
        PostToken postToken = new PostToken();
        postToken.setTokenType(TokenType.PasswordReset.toString());
        // no password in post token
        postToken.setToken("token_password_reset");
        String payload = jacksonJson(postToken);

        // make call - ensure 400 as return code
        String url = "/user/token";
        given()
                .contentType(ContentType.JSON)
                .when()
                .body(payload)
                .log().all()
                .post(url)
                .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());

    }

    @Test
    void testPostToken_NoTokenTypeKO() throws Exception {

        // make payload
        PostToken postToken = new PostToken();
        // no token type
        postToken.setTokenParameter("new password");
        postToken.setToken("token_password_reset");
        String payload = jacksonJson(postToken);

        // make call - ensure 400 as return code
        String url = "/user/token";
        given()
                .contentType(ContentType.JSON)
                .when()
                .body(payload)
                .log().all()
                .post(url)
                .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());

    }

    @Test
    void testPostToken_TokenNotFoundKO() throws Exception {

        // make payload
        PostToken postToken = new PostToken();
        postToken.setTokenType("PasswordReset");
        postToken.setTokenParameter("new password");
        postToken.setToken("token_password_resetnotfound");
        String payload = jacksonJson(postToken);

        // make call - ensure 400 as return code
        String url = "/user/token";
        given()
                .contentType(ContentType.JSON)
                .when()
                .body(payload)
                .log().all()
                .post(url)
                .then()
                .log().all()
                .statusCode(HttpStatus.NOT_FOUND.value());

    }

    @Test
    void testPostToken_TokenExpiredKO() throws Exception {

        // make payload
        PostToken postToken = new PostToken();
        postToken.setTokenType("PasswordReset");
        postToken.setTokenParameter("new password");
        postToken.setToken("token_password_reset_expired");
        String payload = jacksonJson(postToken);

        // make call - ensure 400 as return code
        String url = "/user/token";
        given()
                .contentType(ContentType.JSON)
                .when()
                .body(payload)
                .log().all()
                .post(url)
                .then()
                .log().all()
                .statusCode(HttpStatus.NOT_FOUND.value());

    }

    @Test
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
        String payload = jacksonJson(postChangePassword);

        // make call - ensure 200 as return code
        String url = "/user/password";
        given()
                .header(TestUtils.authToken(TestConstants.USER_5_TOKEN))
                .contentType(ContentType.JSON)
                .when()
                .body(payload)
                .log().all()
                .post(url)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value());


        // get user info after password change
        UserEntity userAccountAfter = userService.getUserByUserEmail(TestConstants.USER_5_NAME);
        var passwordAfterChange = userAccountAfter.getPassword();
        var passwordResetDate = userAccountAfter.getLastPasswordResetDate();

        // ensure password has changed, and password reset is updated
        Assertions.assertNotEquals(passwordBeforeChange, passwordAfterChange, "password should have changed");
        Assertions.assertTrue(passwordResetDate.getTime() >= startTime, "reset date should be equal or after start of test");
    }

    @Test
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
        String payload = jacksonJson(postChangePassword);

        // make call - ensure 200 as return code
        String url = "/user/password";
        given()
                .contentType(ContentType.JSON)
                .when()
                .body(payload)
                .log().all()
                .post(url)
                .then()
                .log().all()
                .statusCode(HttpStatus.FORBIDDEN.value());

        // get user info after password change
        UserEntity userAccountAfter = userService.getUserByUserEmail(TestConstants.USER_1_EMAIL);
        var passwordAfterChange = userAccountAfter.getPassword();

        // ensure password has not changed
        Assertions.assertEquals(passwordBeforeChange, passwordAfterChange, "password should not have changed");
    }

    @Test
    void testDeleteUser_NotFoundKO() throws Exception {

        // make call - ensure 200 as return code
        String url = "/user";
        given()
                .contentType(ContentType.JSON)
                .when()
                .log().all()
                .delete(url)
                .then()
                .log().all()
                .statusCode(HttpStatus.NOT_FOUND.value());

    }

    @Test
    void testDeleteUser() throws Exception {

        // make call - ensure 200 as return code
        String url = "/user";
        given()
                .header(TestUtils.authToken("tokenUserToBeDeleted"))
                .contentType(ContentType.JSON)
                .when()
                .log().all()
                .delete(url)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value());

        UserEntity user = userService.getUserByUserEmail("bravenewworld@test.com");
        Assertions.assertNull(user);
    }

    @Test
    void testUserNameIsTaken() throws Exception {
        ListShopPayload payload = new ListShopPayload();
        List<String> parameters = new ArrayList<>();
        parameters.add("name@whichdoesntexist.com");
        payload.setParameters(parameters);

        String payloadAsString = jacksonJson(payload);

        // make call - ensure 200 as return code
        String url = "/user/name";
        String resultString = given()
                .contentType(ContentType.JSON)
                .when()
                .body(payloadAsString)
                .log().all()
                .post(url)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().asString();
        Assertions.assertEquals("false", resultString, "Expect false for nonsense name");

        ListShopPayload payloadForExistingUser = new ListShopPayload();
        parameters = new ArrayList<>();
        parameters.add(TestConstants.USER_5_NAME);
        payloadForExistingUser.setParameters(parameters);

        payloadAsString = jacksonJson(payloadForExistingUser);
        resultString = given()
                .contentType(ContentType.JSON)
                .when()
                .body(payloadAsString)
                .log().all()
                .post(url)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().asString();

        Assertions.assertEquals("true", resultString, "Expect true for existing name");
    }

    @Test
    void testMinimumClientVersion() throws Exception {
        // make call - ensure 200 as return code
        String url = "/user/client/version";
        given()
                .when()
                .log().all()
                .get(url)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("ios_min_version", equalToIgnoringCase("1.0"))
                .body("android_min_version", equalToIgnoringCase("1.0"));

    }


    @Test
    @WithMockUser
    void testGetUserProperties() throws Exception {

        String url = "/user/properties";
        given()
                .header(TestUtils.authToken(TestConstants.USER_5_TOKEN))
                .contentType(ContentType.JSON)
                .when()
                .log().all()
                .get(url)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("user_properties", Matchers.hasSize(2))
                .body("user_properties[0].key", Matchers.equalTo("test_property"))
                .body("user_properties[0].value", Matchers.equalTo("ho hum value"))
                .body("user_properties[1].key", Matchers.equalTo("another_property"))
                .body("user_properties[1].value", Matchers.equalTo("good value"));

    }

    @Test
    @WithMockUser
    void testGetUserProperties_NoProperties() throws Exception {

        String url = "/user/properties";
        given()
                .header(TestUtils.authToken(TestConstants.USER_5_TOKEN))
                .contentType(ContentType.JSON)
                .when()
                .log().all()
                .get(url)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("user_properties", Matchers.hasSize(0));


    }

    @Test
    void testSetUserProperties() throws Exception {
        UserProperty property1 = new UserProperty("key1", "value1");
        UserProperty property2 = new UserProperty("key2", "value2");
        PostUserProperties propertiesPost = new PostUserProperties();
        propertiesPost.setProperties(Arrays.asList(property1, property2));
        String payloadAsString = jacksonJson(propertiesPost);

        String url = "/user/properties";
        given()
                .header(TestUtils.authToken(TestConstants.USER_4_TOKEN))
                .contentType(ContentType.JSON)
                .when()
                .body(payloadAsString)
                .log().all()
                .post(url)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value());

        // now retrieve what we just saved
        given()
                .header(TestUtils.authToken(TestConstants.USER_4_TOKEN))
                .contentType(ContentType.JSON)
                .when()
                .get(url)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("user_properties", Matchers.hasSize(2))
                .body("user_properties.key", Matchers.hasItem("key1"))
                .body("user_properties.value", Matchers.hasItem("value1"))
                .body("user_properties.key", Matchers.hasItem("key2"))
                .body("user_properties.value", Matchers.hasItem("value2"));
    }

    @Test
    void testSetUserProperties_Existing() throws Exception {
        UserProperty property1 = new UserProperty("key1", "value1");
        UserProperty property2 = new UserProperty("key2", "value2");
        PostUserProperties propertiesPost = new PostUserProperties();
        propertiesPost.setProperties(Arrays.asList(property1, property2));
        String payloadAsString = jacksonJson(propertiesPost);

        String url = "/user/properties";
        given()
                .header(TestUtils.authToken(USER_WITH_PROPERTIES_TOKEN))
                .contentType(ContentType.JSON)
                .when()
                .body(payloadAsString)
                .post(url)
                .then()
                .statusCode(HttpStatus.OK.value());

        // retrieve what we just saved
        given()
                .header(TestUtils.authToken(USER_WITH_PROPERTIES_TOKEN))
                .contentType(ContentType.JSON)
                .when()
                .get(url)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("user_properties", Matchers.hasSize(4))
                .body("user_properties.key", Matchers.hasItem("key1"))
                .body("user_properties.key", Matchers.hasItem("key2"))
                .body("user_properties.key", Matchers.hasItem("test_property"))
                .body("user_properties.key", Matchers.hasItem("another_property"))
                .body("user_properties.value", Matchers.hasItem("value1"))
                .body("user_properties.value", Matchers.hasItem("value2"))
                .body("user_properties.value", Matchers.hasItem("ho hum value"))
                .body("user_properties.value", Matchers.hasItem("good value"));

    }

    @Test
    @WithMockUser
    void testSetUserProperties_Update() throws Exception {
        UserProperty property1 = new UserProperty("test_property", "scintillating value");
        PostUserProperties propertiesPost = new PostUserProperties();
        propertiesPost.setProperties(Arrays.asList(property1));
        String payloadAsString = jacksonJson(propertiesPost);

        String url = "/user/properties";
        given()
                .header(TestUtils.authToken(USER_WITH_PROPERTIES_TOKEN))
                .contentType(ContentType.JSON)
                .when()
                .body(payloadAsString)
                .post(url)
                .then()
                .statusCode(HttpStatus.OK.value());

        // retrieve what we just saved
        given()
                .header(TestUtils.authToken(USER_WITH_PROPERTIES_TOKEN))
                .contentType(ContentType.JSON)
                .when()
                .get(url)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("user_properties.key", Matchers.hasItem("test_property"))
                .body("user_properties.value", Matchers.hasItem("scintillating value"));

    }

    @Test
    void testSetUserProperties_UpdateToNull() throws Exception {
        UserProperty property1 = new UserProperty("another_property", null);
        PostUserProperties propertiesPost = new PostUserProperties();
        propertiesPost.setProperties(Arrays.asList(property1));
        String payloadAsString = jacksonJson(propertiesPost);

        String url = "/user/properties";
        given()
                .header(TestUtils.authToken(USER_WITH_PROPERTIES_TOKEN))
                .contentType(ContentType.JSON)
                .when()
                .body(payloadAsString)
                .post(url)
                .then()
                .statusCode(HttpStatus.OK.value());

        // retrieve what we just saved
        given()
                .header(TestUtils.authToken(USER_WITH_PROPERTIES_TOKEN))
                .contentType(ContentType.JSON)
                .when()
                .get(url)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("user_properties.key", Matchers.not(Matchers.hasItem("another_property")));

    }


    private String jacksonJson(Object payload) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(payload);
    }


}
