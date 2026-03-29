/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.api.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meg.listshop.Application;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.api.model.v2.*;
import com.meg.listshop.test.TestConstants;
import com.meg.listshop.test.TestUtils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.List;

import static io.restassured.RestAssured.given;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
@Sql(value = {"/sql/com/meg/atable/lmt/api/v2/DishRestControllerTest.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sql/com/meg/atable/lmt/api/v2/DishRestControllerTest_rollback.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class V2DishRestControllerTest {

    @Container
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    @LocalServerPort
    public int serverPort;

    private final String urlRoot = "/v2/dish/";

    @PostConstruct
    public void initRestAssured() {
        RestAssured.port = serverPort;
        RestAssured.urlEncodingEnabled = false;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void readSingleDishNoAmounts() throws Exception {
        Long testId = 9999992L;
        Dish afterDish = retrieveDish(testId);
        Assertions.assertEquals(String.valueOf(testId), afterDish.getDishId());
    }

    @Test
    void readSingleDishAmounts() throws Exception {
        Long testId = 9999993L;
        Dish result = retrieveDish(testId);
        Assertions.assertNotNull(result);

        Assertions.assertEquals(String.valueOf(testId), result.getDishId());
        // test collections
        Assertions.assertEquals(6, result.getIngredients().size(), "Ingredient size is wrong");
        Assertions.assertEquals(3, result.getTags().size(), "Tag size is wrong");
        Assertions.assertEquals(4, result.getRatings().size(), "Rating size is wrong");
        // test ingredient
        Ingredient cheddarCheese = result.getIngredients().stream()
                .filter(i -> i.getTag().getTagId().equals("18"))
                .findFirst()
                .orElse(null);
        Assertions.assertNotNull(cheddarCheese, "Cheddar cheese ingredient not found");
        Assertions.assertEquals("cheddar cheese", cheddarCheese.getTag().getName());
        Assertions.assertEquals("18", cheddarCheese.getTag().getTagId());

        Assertions.assertEquals(1.5, cheddarCheese.getAmount().getQuantity(),"quantity is wrong");
        Assertions.assertEquals(1, cheddarCheese.getAmount().getWholeQuantity(), "whole quantity is wrong");
        Assertions.assertEquals("OneHalf", cheddarCheese.getAmount().getFractionalQuantity(), "fractional quantity is wrong");
        Assertions.assertEquals("1 1/2", cheddarCheese.getAmount().getQuantityDisplay(), "quantity display is wrong");
        Assertions.assertEquals("1008", cheddarCheese.getAmount().getUnitId(), "unit id is wrong");
        Assertions.assertEquals("lb", cheddarCheese.getAmount().getUnitDisplay(), "unit display is wrong");
        Assertions.assertEquals("1 1/2 pound", cheddarCheese.getAmount().getDisplay(), "display is wrong");
        List<String> modifiers = cheddarCheese.getAmount().getModifiers();
        Assertions.assertEquals(1, modifiers.size(), "modifiers size is wrong");

        // test tags
        NestedTag nestedTag = result.getTags().stream()
                .filter(t -> t.getTagId().equals("199"))
                .findFirst()
                .orElse(null);
        Assertions.assertNotNull(nestedTag, "tag 199 not found");
        Assertions.assertEquals("Vegetarian", nestedTag.getName());
        // test ratings
        RatingInfo tasty = result.getRatings().stream()
                .filter(r -> r.getTag().getTagId().equals("391"))
                .findFirst()
                .orElse(null);
        Assertions.assertNotNull(tasty, "tasty rating not found");
        Assertions.assertEquals("Taste Factor", tasty.getTag().getName(), "tag name is wrong");
        Assertions.assertEquals(1, tasty.getPower(), "tasty rating power is wrong");
        Assertions.assertEquals(5, tasty.getMaxPower(), "tasty rating max power is wrong");

    }

    @Test
    void readSingleDish_ObjectNotFoundException() throws Exception {
        Long testId = 99999999394L;
        given()
                .header(TestUtils.authToken(TestConstants.USER_3_TOKEN))
                .when()
                .get(urlRoot + testId)
                .then()
                .statusCode(404);
    }


    @Test
    void testAddIngredientToDish() throws Exception {
        Dish dish = new Dish()
                .withDishName("Yummy new dish");


        Long testId = createDish(dish);

        IngredientPut ingredientPut = new IngredientPut();
        ingredientPut.setTagId("12");
        ingredientPut.setWholeQuantity(1);
        ingredientPut.setFractionalQuantity("OneHalf");
        ingredientPut.setUnitId("1000");
        String payload = json(ingredientPut);
        String url = urlRoot + testId + "/ingredients";
        given()
                .header(TestUtils.authToken(TestConstants.USER_3_TOKEN))
                .contentType(ContentType.JSON)
                .body(payload)
                .log().all()
                .when()
                .post(url)
                .then()
                .statusCode(204);

        Dish dishResult = retrieveDish(testId);
        Assertions.assertNotNull(dishResult);
        Assertions.assertEquals(1, dishResult.getIngredients().size(), "should be 1 ingredient");
        Ingredient ingredient = dishResult.getIngredients().get(0);
        Assertions.assertEquals(ingredientPut.getAmount().getUnitId(),ingredient.getAmount().getUnitId());
        Assertions.assertEquals(ingredientPut.getAmount().getWholeQuantity(),ingredient.getAmount().getWholeQuantity());
        Assertions.assertEquals(ingredientPut.getAmount().getFractionalQuantity(), ingredient.getAmount().getFractionalQuantity());
        Assertions.assertEquals("1 1/2", ingredient.getAmount().getQuantityDisplay());
    }


    @Test
    void testUpdateIngredientInDish() throws Exception {
        Dish dish = new Dish().withDishName("update new dish");

        Long testId = createDish(dish);

        IngredientPut ingredientPut = new IngredientPut();
        ingredientPut.setTagId("12");
        ingredientPut.setWholeQuantity(1);
        ingredientPut.setFractionalQuantity("OneHalf");
        ingredientPut.setUnitId("1000");
        String payload = json(ingredientPut);
        String url = urlRoot + testId + "/ingredients";
        given()
                .header(TestUtils.authToken(TestConstants.USER_3_TOKEN))
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post(url)
                .then()
                .statusCode(204);

        Dish dishResult = retrieveDish(testId);
        Assertions.assertNotNull(dishResult);
        Assertions.assertEquals(1, dishResult.getIngredients().size(), "should be 1 ingredient");
        Ingredient ingredient = dishResult.getIngredients().get(0);
        String ingredientId = ingredient.getItemId();

        // now - update it
        IngredientPut ingredientUpdate = new IngredientPut();
        ingredientUpdate.setId(ingredientId);
        ingredientUpdate.setTagId("12");
        ingredientUpdate.setWholeQuantity(101);
        ingredientUpdate.setFractionalQuantity(null);
        ingredientUpdate.setUnitId("1011");
        payload = json(ingredientUpdate);

        given()
                .header(TestUtils.authToken(TestConstants.USER_3_TOKEN))
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .put(url)
                .then()
                .statusCode(204);

        dishResult = retrieveDish(testId);
        Assertions.assertNotNull(dishResult);
        Assertions.assertEquals(1, dishResult.getIngredients().size(), "should be 1 ingredient");
        ingredient = dishResult.getIngredients().get(0);

        Assertions.assertEquals(ingredientUpdate.getAmount().getUnitId(),ingredient.getAmount().getUnitId());
        Assertions.assertEquals(ingredientUpdate.getAmount().getWholeQuantity(),ingredient.getAmount().getWholeQuantity());
        Assertions.assertEquals(ingredientUpdate.getAmount().getFractionalQuantity(),ingredient.getAmount().getFractionalQuantity());
        Assertions.assertEquals(ingredient.getAmount().getQuantityDisplay(),"101");
    }

    @Test
    void testDeleteIngredientFromDish() throws Exception {
        Dish dish = new Dish().withDishName("update new dish");

        Long testId = createDish(dish);

        IngredientPut ingredientPut = new IngredientPut();
        ingredientPut.setTagId("12");
        ingredientPut.setWholeQuantity(1);
        ingredientPut.setFractionalQuantity("OneHalf");
        ingredientPut.setUnitId("1000");
        String payload = json(ingredientPut);
        String url = urlRoot + testId + "/ingredients";
        given()
                .header(TestUtils.authToken(TestConstants.USER_3_TOKEN))
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post(url)
                .then()
                .statusCode(204);

        Dish dishResult = retrieveDish(testId);
        Assertions.assertNotNull(dishResult);
        Assertions.assertEquals(1, dishResult.getIngredients().size(), "should be 1 ingredient");
        Ingredient ingredient = dishResult.getIngredients().get(0);
        String ingredientId = ingredient.getItemId();

        // now - delete it
        String deleteUrl = urlRoot + testId + "/ingredients/" + ingredientId;
        given()
                .header(TestUtils.authToken(TestConstants.USER_3_TOKEN))
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .delete(deleteUrl)
                .then()
                .statusCode(204);

        dishResult = retrieveDish(testId);
        Assertions.assertNotNull(dishResult);
        Assertions.assertEquals(0, dishResult.getIngredients().size(), "ingredients should be empty");
    }


    @Test
    void testGetIngredientsForDish() throws Exception {
        Dish dish = new Dish().withDishName("Yummy new dish");


        Long testId = createDish(dish);

        IngredientPut ingredientPut = new IngredientPut();
        ingredientPut.setTagId("12");
        ingredientPut.setWholeQuantity(1);
        ingredientPut.setFractionalQuantity("OneHalf");
        ingredientPut.setUnitId("1000");
        String payload = json(ingredientPut);
        String url = urlRoot + testId + "/ingredients";
        given()
                .header(TestUtils.authToken(TestConstants.USER_3_TOKEN))
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post(url)
                .then()
                .statusCode(204);

        ingredientPut.setTagId("112");
        ingredientPut.setWholeQuantity(12);
        ingredientPut.setUnitId("1000");
        payload = json(ingredientPut);

        given()
                .header(TestUtils.authToken(TestConstants.USER_3_TOKEN))
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post(url)
                .then()
                .statusCode(204);

        ingredientPut.setTagId("113");
        ingredientPut.setWholeQuantity(13);
        ingredientPut.setUnitId("1011");
        payload = json(ingredientPut);

        given()
                .header(TestUtils.authToken(TestConstants.USER_3_TOKEN))
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post(url)
                .then()
                .statusCode(204);

        // now get the ingredients
        IngredientList afterList = given()
                .header(TestUtils.authToken(TestConstants.USER_3_TOKEN))
                .when()
                .get(urlRoot + testId + "/ingredients")
                .then()
                .statusCode(200)
                .extract()
                .as(IngredientList.class);

        Assertions.assertNotNull(afterList);
        Assertions.assertEquals(3, afterList.getIngredients().size());
    }


    private Long createDish(Dish dish) throws Exception {
        String dishJson = json(dish);

        String url = "/dish";

        String location = given()
                .header(TestUtils.authToken(TestConstants.USER_3_TOKEN))
                .contentType(ContentType.JSON)
                .body(dishJson)
                .when()
                .post(url)
                .then()
                .statusCode(201)
                .extract()
                .header("Location");

        String stringId = location.substring(location.lastIndexOf("/") + 1);
        return Long.valueOf(stringId);
    }

    private Dish retrieveDish(Long dishId) throws Exception {
        return given()
                .header(TestUtils.authToken(TestConstants.USER_3_TOKEN))
                .when()
                .get(urlRoot + dishId)
                .then()
                .statusCode(200)
                .extract()
                .as(Dish.class);
    }


    private String json(Object o) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(o);
    }

}
