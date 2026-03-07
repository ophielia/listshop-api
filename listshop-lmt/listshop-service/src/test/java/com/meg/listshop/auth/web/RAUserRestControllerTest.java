/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.auth.web;

import com.google.gson.Gson;
import com.meg.listshop.Application;
import com.meg.listshop.auth.api.model.*;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.CustomUserDetails;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

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
class RAUserRestControllerTest {

    private static final Long USER_WITH_PROPERTIES_ID = 999L;
    private static final String USER_WITH_PROPERTIES_NAME = "rufus@barkingmad.com";
    @Container
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));
    @LocalServerPort
    public int serverPort;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;
    private CustomUserDetails userDetailsChangePassword;
    private CustomUserDetails userDetailsAnotherChangePassword;
    private CustomUserDetails userWithoutProperties;
    private CustomUserDetails userWithProperties;
    private CustomUserDetails userToDelete;
    private CustomUserDetails userNotFound;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private UserService userService;


    @PostConstruct
    public void initRestAssured() {
        RestAssured.port = serverPort;
        RestAssured.urlEncodingEnabled = false;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    public void setup() throws Exception {
        UserEntity userAccount = userService.getUserByUserEmail(TestConstants.USER_1_EMAIL);
        userDetailsChangePassword = new CustomUserDetails(userAccount.getId(),
                TestConstants.USER_1_EMAIL,
                TestConstants.USER_1_EMAIL,
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
    void testGetTags() {

        String url = "/v2/tag";
        given()
                //  .header(authToken("token123456"))
                .log().all()
                .when()
                .get(url)
                .then()

                // .statusCode(200)
                //       .body("user.email", equalToIgnoringCase("testuser@testuser.com"))
                .log().all();

     /*   mockMvc.perform(get(url)
                        .with(user(userDetailsChangePassword))
                        .contentType(contentType)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.user.email", Matchers.equalToIgnoringCase("testuser@testuser.com")))
//                .andExpect(jsonPath("$.user.roles", Matchers.contains("ROLE_USER")))
                .andDo(print());
*/

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
        final String userjson = json(user);

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
        String payload = json(createPayload);

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
        String payload = json(tokenRequest);

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
        String payload = json(postToken);

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
        String payload = json(postToken);

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
        String payload = json(postToken);

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
        String payload = json(postToken);

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
        String payload = json(postToken);

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
        String payload = json(postChangePassword);

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
        String payload = json(postChangePassword);

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

        String payloadAsString = json(payload);

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

        payloadAsString = json(payloadForExistingUser);
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
                .header(TestUtils.authToken(TestConstants.USER_1_TOKEN))
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


    private String json(Object o) throws IOException {
        Gson mapper = new Gson();
        return mapper.toJson(o);
    }


}
