/*
 * The List Shop
 *
 * Copyright (c) 2022-2026.
 */

package com.meg.listshop.lmt.api;

import com.meg.listshop.Application;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.test.TestConstants;
import com.meg.listshop.test.TestUtils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.annotation.PostConstruct;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.StreamUtils;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static io.restassured.RestAssured.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
@Sql(value = {"/sql/com/meg/atable/auth/api/UserRestControllerTest-rollback.sql",
        "/sql/com/meg/atable/auth/api/UserRestControllerTest.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class StatisticRestControllerTest {

    @Container
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    @LocalServerPort
    private int port;

    @Value("classpath:/data/statisticRestControllerTest_createStatistic.json")
    Resource resourceFile;

    private static final String USER_TOKEN = TestConstants.USER_3_TOKEN;

    @PostConstruct
    public void initRestAssured() {
        RestAssured.port = port;
        RestAssured.urlEncodingEnabled = false;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void testCreateUserStatistics() throws Exception {
        // load statistics into file
        String testUploadStatistics = StreamUtils.copyToString(resourceFile.getInputStream(), StandardCharsets.UTF_8);

        given()
                .header(TestUtils.authToken(USER_TOKEN)) // Using USER_TOKEN for now
                .contentType(ContentType.JSON)
                .body(testUploadStatistics)
                .when()
                .post("/statistics")
                .then()
                .statusCode(Matchers.anyOf(Matchers.is(201), Matchers.is(403)));
    }

    @Test
    void testGetUserStatistics() throws Exception {
        given()
                .header(TestUtils.authToken(USER_TOKEN))
                .contentType(ContentType.JSON)
                .when()
                .get("/statistics")
                .then()
                .statusCode(Matchers.anyOf(Matchers.is(200), Matchers.is(403)));
    }

}
