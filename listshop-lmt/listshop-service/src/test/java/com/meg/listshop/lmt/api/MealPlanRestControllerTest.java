/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meg.listshop.Application;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.data.entity.MealPlanEntity;
import com.meg.listshop.test.TestConstants;
import com.meg.listshop.test.TestUtils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.annotation.PostConstruct;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Sql(value = "/sql/com/meg/atable/lmt/api/MealPlanRestControllerTest.sql")
@Sql(value = "/sql/com/meg/atable/lmt/api/MealPlanRestControllerTest_rollback.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@ActiveProfiles("test")
class MealPlanRestControllerTest {

    @Container
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    @LocalServerPort
    private int port;

    @Autowired
    private UserService userService;

    private static final String user3Token = TestConstants.USER_3_TOKEN;
    private static final String user2Token = TestConstants.USER_1_TOKEN;

    @PostConstruct
    public void initRestAssured() {
        RestAssured.port = port;
        RestAssured.urlEncodingEnabled = false;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void readSingleMealPlan() throws Exception {
        Long testId = TestConstants.MENU_PLAN_3_ID;
        given()
                .header(TestUtils.authToken(user3Token))
                .when()
                .get("/mealplan/" + testId)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("meal_plan.meal_plan_id", Matchers.isA(Number.class))
                .body("meal_plan.meal_plan_id", Matchers.equalTo(testId.intValue()));
    }

    @Test
    @Sql(value = "/sql/com/meg/atable/lmt/api/MealPlanRestControllerTest.sql")
    @Sql(value = "/sql/com/meg/atable/lmt/api/MealPlanRestControllerTest_rollback.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void readMealPlanRatings() throws Exception {
        Long testId = 50485L;
        given()
                .header(TestUtils.authToken(user3Token))
                .when()
                .get("/mealplan/" + testId + "/ratings")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("ratingUpdateInfo.dish_ratings", Matchers.hasSize(5));
    }

    @Test
    void readMealPlans() throws Exception {
        given()
                .header(TestUtils.authToken(user3Token))
                .when()
                .get("/mealplan")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("_embedded.mealPlanResourceList", Matchers.hasSize(Matchers.greaterThan(2)));
    }

    @Test
    void readMealPlanBadUser() throws Exception {
        Long testId = TestConstants.MENU_PLAN_3_ID;
        given()
                .header(TestUtils.authToken(user2Token))
                .when()
                .get("/mealplan/" + testId)
                .then()
                .statusCode(Matchers.is(Matchers.both(Matchers.greaterThanOrEqualTo(400)).and(Matchers.lessThan(500))));
    }

    @Test
    void testDeleteMealPlan() throws Exception {
        Long testId = 506L; 
        given()
                .header(TestUtils.authToken(user3Token))
                .when()
                .delete("/mealplan/" + testId)
                .then()
                .statusCode(204);
    }

    @Test
    void testCreateMealPlan() throws Exception {
        UserEntity userAccount = userService.getUserByUserEmail(TestConstants.USER_3_NAME);
        MealPlanEntity mealPlanEntity = new MealPlanEntity();
        mealPlanEntity.setName("mealPlanCreate");
        mealPlanEntity.setMealPlanType(MealPlanType.Manual);
        mealPlanEntity.setUserId(userAccount.getId());
        MealPlan mealPlan = ModelMapper.toModel(mealPlanEntity, true);
        String mealPlanJson = json(mealPlan);

        given()
                .header(TestUtils.authToken(user3Token))
                .contentType(ContentType.JSON)
                .body(mealPlanJson)
                .when()
                .post("/mealplan")
                .then()
                .statusCode(201);
    }

    @Test
    void testCreateMealPlan_EmptyName() throws Exception {
        UserEntity userAccount = userService.getUserByUserEmail(TestConstants.USER_3_NAME);
        MealPlanEntity mealPlanEntity = new MealPlanEntity();
        mealPlanEntity.setMealPlanType(MealPlanType.Manual);
        mealPlanEntity.setUserId(userAccount.getId());
        MealPlan mealPlan = ModelMapper.toModel(mealPlanEntity, true);
        String mealPlanJson = json(mealPlan);

        given()
                .header(TestUtils.authToken(user3Token))
                .contentType(ContentType.JSON)
                .body(mealPlanJson)
                .when()
                .post("/mealplan")
                .then()
                .statusCode(201)
                .body("meal_plan.name", Matchers.isA(String.class));
    }

    @Test
    void testAddDishToMealPlan() throws Exception {
        String url = "/mealplan/" + TestConstants.MENU_PLAN_3_ID
                + "/dish/" + TestConstants.DISH_1_ID;
        given()
                .header(TestUtils.authToken(user3Token))
                .contentType(ContentType.JSON)
                .when()
                .post(url)
                .then()
                .statusCode(204);
    }

    @Test
    void testAddDishToMealPlan_DishExistsKO() throws Exception {
        var dishId = "500";
        var mealPlanId = "503";
        String url = "/mealplan/" + mealPlanId
                + "/dish/" + dishId;
        given()
                .header(TestUtils.authToken(user3Token))
                .contentType(ContentType.JSON)
                .when()
                .post(url)
                .then()
                .statusCode(404);
    }

    @Test
    void testRemoveDishFromMealPlan() throws Exception {
        String url = "/mealplan/" + TestConstants.MENU_PLAN_3_ID
                + "/dish/" + 501L;
        given()
                .header(TestUtils.authToken(user3Token))
                .contentType(ContentType.JSON)
                .when()
                .delete(url)
                .then()
                .statusCode(204);
    }

    @Test
    void testCreateMealPlanFromTargetProposal() throws Exception {
        String url = "/mealplan/proposal/" + TestConstants.PROPOSAL_3_ID;

        given()
                .header(TestUtils.authToken(user3Token))
                .contentType(ContentType.JSON)
                .when()
                .post(url)
                .then()
                .statusCode(201);
    }

    @Test
    void testRenameMealPlan() throws Exception {
        String url = "/mealplan/" + TestConstants.MENU_PLAN_3_ID + "/name/george";

        given()
                .header(TestUtils.authToken(user3Token))
                .contentType(ContentType.JSON)
                .when()
                .post(url)
                .then()
                .statusCode(204);
    }

    @Test
    void testCopyMealPlan() throws Exception {
        Long copyMealPlan = 504L;
        String url = "/mealplan/" + copyMealPlan;
        Long startTime = new Date().getTime();

        Response response = given()
                .header(TestUtils.authToken(user3Token))
                .contentType(ContentType.JSON)
                .when()
                .post(url)
                .then()
                .header("Location", Matchers.notNullValue())
                .statusCode(201)
                .extract()
                .response();

        String locationHeader = response.getHeader("Location");
        String newId = locationHeader.substring(locationHeader.lastIndexOf("/") + 1);

        Response sourceResponse = given()
                .header(TestUtils.authToken(user3Token))
                .contentType(ContentType.JSON)
                .when()
                .get(url)
                .then()
                .statusCode(200)
                .extract()
                .response();

        Response copiedResponse = given()
                .header(TestUtils.authToken(user3Token))
                .contentType(ContentType.JSON)
                .when()
                .get("/mealplan/" + newId)
                .then()
                .statusCode(200)
                .extract()
                .response();

        MealPlanResource sourceResource = toMealPlanResource(sourceResponse.asString());
        MealPlanResource copiedResource = toMealPlanResource(copiedResponse.asString());

        MealPlan source = sourceResource.getMealPlan();
        MealPlan copied = copiedResource.getMealPlan();
        
        Assertions.assertNotNull(copied.getName(), "copied name should not be null");
        Assertions.assertNotEquals(copied.getName(), source.getName(), "copied name should not equal source");
        Assertions.assertNotNull(copied.getCreated(), "copied should have created");
        Assertions.assertTrue(copied.getCreated().getTime() >= startTime, "copied created should be after start");
        
        List<Slot> sourceSlots = source.getSlots();
        List<Slot> copiedSlots = copied.getSlots();
        Assertions.assertNotNull(copiedSlots, "slots should exist - copied");
        Assertions.assertNotNull(sourceSlots, "slots should exist - source");
        Assertions.assertEquals(sourceSlots.size(), copiedSlots.size(), "size should match");
        Map<Long, Dish> dishIdsInSource = sourceSlots.stream()
                .map(Slot::getDish)
                .collect(Collectors.toMap(Dish::getId, Function.identity()));
        for (Slot slot : copiedSlots) {
            Assertions.assertEquals(newId, String.valueOf(slot.getMealPlanId()), "meal plan id should be correct");
            Assertions.assertTrue(dishIdsInSource.containsKey(slot.getDish().getId()), "dish id should match one in source");
        }
    }

    @Test
    void testCopyMealPlan_BadUserKO() throws Exception {
        Long copyMealPlan = 504L;
        String url = "/mealplan/" + copyMealPlan;

        given()
                .header(TestUtils.authToken(user2Token))
                .contentType(ContentType.JSON)
                .when()
                .post(url)
                .then()
                .statusCode(400);
    }

    @Test
    void testCopyMealPlan_BadMealPlanKO() throws Exception {
        Long copyMealPlan = 555504L;
        String url = "/mealplan/" + copyMealPlan;

        given()
                .header(TestUtils.authToken(user3Token))
                .contentType(ContentType.JSON)
                .when()
                .post(url)
                .then()
                .statusCode(404);
    }

    private String json(Object o) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(o);
    }

    private MealPlanResource toMealPlanResource(String input) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(input, MealPlanResource.class);
    }

}
