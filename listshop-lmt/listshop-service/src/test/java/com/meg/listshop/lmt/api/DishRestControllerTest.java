/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meg.listshop.Application;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.common.FlatStringUtils;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.data.entity.DishEntity;
import com.meg.listshop.lmt.dish.DishService;
import com.meg.listshop.lmt.dish.DishTestBuilder;
import com.meg.listshop.test.TestConstants;
import com.meg.listshop.test.TestUtils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.annotation.PostConstruct;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
@Sql(value = {"/sql/com/meg/atable/lmt/api/DishRestControllerTest.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sql/com/meg/atable/lmt/api/DishRestControllerTest_rollback.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class DishRestControllerTest {

    @Container
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    @LocalServerPort
    public int serverPort;

    public static final Comparator<DishResource> CREATEDON = Comparator.comparing((DishResource o) -> o.getDish().getId());

    @Autowired
    private DishService dishService;

    @Autowired
    private UserService userService;

    @PostConstruct
    public void initRestAssured() {
        RestAssured.port = serverPort;
        RestAssured.urlEncodingEnabled = false;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void readSingleDish() throws Exception {
        Long testId = TestConstants.DISH_1_ID;
        given()
                .header(TestUtils.authToken(TestConstants.USER_3_TOKEN))
                .when()
                .get("/dish/" + testId)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("dish.dish_id", is(testId.intValue()));
    }

    @Test
    void readSingleDish_ObjectNotFoundException() throws Exception {
        Long testId = TestConstants.DISH_7_ID;
        given()
                .header(TestUtils.authToken(TestConstants.USER_3_TOKEN))
                .when()
                .get("/dish/" + testId)
                .then()
                .statusCode(404);
    }

    @Test
    void readDishes() throws Exception {
        given()
                .header(TestUtils.authToken(TestConstants.USER_3_TOKEN))
                .when()
                .get("/dish")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    @Test
    void createDish() throws Exception {
        String dishJson = json(new Dish(
                TestConstants.USER_3_ID, "created dish"));

        given()
                .header(TestUtils.authToken(TestConstants.USER_3_TOKEN))
                .contentType(ContentType.JSON)
                .body(dishJson)
                .when()
                .post("/dish")
                .then()
                .statusCode(201);
    }


    @Test
    void updateDish() throws Exception {
        DishEntity toUpdate = dishService.getDishForUserById(TestConstants.USER_3_NAME, TestConstants.DISH_1_ID);
        String updateName = "updated:" + toUpdate.getDishName();
        String updateDescription = "updated:" + (toUpdate.getDescription() == null ? "" : toUpdate.getDescription());
        Dish updateDish = new Dish(toUpdate.getUserId(), updateName);
        updateDish.reference("reference");
        updateDish.description(updateDescription);
        String dishJson = json(updateDish);

        given()
                .header(TestUtils.authToken(TestConstants.USER_3_TOKEN))
                .contentType(ContentType.JSON)
                .body(dishJson)
                .when()
                .put("/dish/" + toUpdate.getId())
                .then()
                .statusCode(anyOf(is(200), is(204), is(206)));

        DishEntity result = dishService.getDishForUserById(TestConstants.USER_3_NAME, TestConstants.DISH_1_ID);
        Assertions.assertEquals(updateName, result.getDishName());
        Assertions.assertEquals(updateDescription, result.getDescription());
        Assertions.assertEquals("reference", result.getReference());
    }

    @Test
    void testGetTagsByDishId() throws Exception {
        given()
                .header(TestUtils.authToken(TestConstants.USER_3_TOKEN))
                .when()
                .get("/dish/" + TestConstants.DISH_2_ID + "/tag")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    @Test
    void testAddTagToDish() throws Exception {
        String url = "/dish/" + TestConstants.DISH_1_ID + "/tag/" + TestConstants.TAG_CARROTS;
        given()
                .header(TestUtils.authToken(TestConstants.USER_3_TOKEN))
                .contentType(ContentType.JSON)
                .when()
                .post(url)
                .then()
                .statusCode(204);
    }

    @Test
    void testDeleteTagFromDish() throws Exception {
        String url = "/dish/" + TestConstants.DISH_1_ID + "/tag/344";
        given()
                .header(TestUtils.authToken(TestConstants.USER_3_TOKEN))
                .contentType(ContentType.JSON)
                .when()
                .delete(url)
                .then()
                .statusCode(anyOf(is(200), is(204), is(206)));

        Dish result = retrieveDish(TestConstants.USER_3_TOKEN, TestConstants.DISH_1_ID);
        Optional<Tag> threeFourFourTag = result.getTags().stream().filter(t -> t.getId().equals("344")).findFirst();
        Assertions.assertFalse(threeFourFourTag.isPresent());
    }

    @Test
    void testAddAndRemoveTags() throws Exception {
        List<Long> addTags = Arrays.asList(TestConstants.TAG_1_ID, TestConstants.TAG_2_ID, TestConstants.TAG_3_ID);
        List<Long> deleteTags = Arrays.asList(55L, 104L);

        String addList = FlatStringUtils.flattenListOfLongsToString(addTags, ",");
        String deleteList = FlatStringUtils.flattenListOfLongsToString(deleteTags, ",");
        String url = "/dish/" + TestConstants.DISH_1_ID + "/tag?addTags=" + addList + "&removeTags=" + deleteList;
        given()
                .header(TestUtils.authToken(TestConstants.USER_3_TOKEN))
                .contentType(ContentType.JSON)
                .when()
                .put(url)
                .then()
                .statusCode(anyOf(is(200), is(204), is(206)));
    }

    @Test
    void testFindDishes() throws Exception {
        List<Long> excludedTags = Arrays.asList(TestConstants.TAG_3_ID);
        List<Long> includedTags = Arrays.asList(TestConstants.TAG_PASTA);

        String includedList = FlatStringUtils.flattenListOfLongsToString(includedTags, ",");
        String excludedList = FlatStringUtils.flattenListOfLongsToString(excludedTags, ",");
        String url = "/dish?includedTags=" + includedList + "&excludedTags=" + excludedList
                + "&sortKey=Name" + "&sortDirection=ASC";
        String responseBody = given()
                .header(TestUtils.authToken(TestConstants.USER_3_TOKEN))
                .contentType(ContentType.JSON)
                .when()
                .get(url)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .body().asString();

        ObjectMapper mapper = new ObjectMapper();
        DishListResource embeddedList = mapper.readValue(responseBody, DishListResource.class);
        List<DishResource> dishList = embeddedList.getEmbeddedList() != null ? embeddedList.getEmbeddedList().getDishResourceList() : new ArrayList<DishResource>();
        // sort list by name, asc
        // list as expected
        List<String> listAsExpected = dishList.stream()
                .map(d -> d.getDish())
                .sorted(Comparator.comparing(Dish::getDishName, String.CASE_INSENSITIVE_ORDER))
                .map(d -> d.getDishName())
                .collect(Collectors.toList());
        Assertions.assertNotNull(listAsExpected);

        // list as received
        List<String> listAsReceived = dishList
                .stream()
                .map(rdr -> rdr.getDish().getDishName())
                .collect(Collectors.toList());
        Assertions.assertNotNull(listAsReceived);

        // order matches
        assertThat(listAsReceived, equalTo(listAsExpected));

        // check sort by created, desc
        url = "/dish?includedTags=" + includedList + "&excludedTags=" + excludedList
                + "&sortKey=CreatedOn" + "&sortDirection=DESC";
        responseBody = given()
                .header(TestUtils.authToken(TestConstants.USER_3_TOKEN))
                .contentType(ContentType.JSON)
                .when()
                .get(url)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .body().asString();

        embeddedList = mapper.readValue(responseBody, DishListResource.class);
        dishList = embeddedList.getEmbeddedList() != null ? embeddedList.getEmbeddedList().getDishResourceList() : new ArrayList<DishResource>();
        // sort list by id, desc
        // expected results
        List<Long> expectedCreatedOnResults = dishList.stream()
                .map(d -> Long.valueOf(d.getDish().getId()))
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        Assertions.assertNotNull(listAsExpected);


        // list as received
        List<Long> longsAsReceived = dishList
                .stream()
                .map(rdr -> Long.valueOf(rdr.getDish().getId()))
                .collect(Collectors.toList());
        Assertions.assertNotNull(listAsReceived);

        // order matches
        assertThat(longsAsReceived, equalTo(expectedCreatedOnResults));

    }

    @Test
    void testFindDishesOrig() throws Exception {
        List<Long> excludedTags = Arrays.asList(TestConstants.TAG_1_ID, TestConstants.TAG_2_ID, TestConstants.TAG_3_ID);
        List<Long> includedTags = Arrays.asList(TestConstants.TAG_MEAT, TestConstants.TAG_PASTA);

        String includedList = FlatStringUtils.flattenListOfLongsToString(includedTags, ",");
        String excludedList = FlatStringUtils.flattenListOfLongsToString(excludedTags, ",");
        String url = "/dish?includedTags=" + includedList + "&excludedTags=" + excludedList;
        given()
                .header(TestUtils.authToken(TestConstants.USER_3_TOKEN))
                .contentType(ContentType.JSON)
                .when()
                .get(url)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);


    }

    @Test
    void testSetRatingTagOnDish() throws Exception {
        // test case (from sql)
        Long dishId = 9999999L;
        String originalTag = "325";
        String expectedTag = "324";
        String ratingIdAsString = "391";
        String stepAsString = "5";


        // get dish, and assert tag 325 is present
        // (start condition)
        given()
                .header(TestUtils.authToken(TestConstants.USER_3_TOKEN))
                .when()
                .get("/dish/" + dishId + "/tag")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("", hasSize(17))
                .body("tag.tag_id", hasItem(originalTag))
                .body("tag.tag_id", not(hasItem(expectedTag)));

        // tested call
        given()
                .header(TestUtils.authToken(TestConstants.USER_3_TOKEN))
                .when()
                .put("/dish/" + dishId + "/rating/" + ratingIdAsString + "/" + stepAsString)
                .then()
                .statusCode(204);

        // verify after condition - should have tag 324 - and no longer have tag 325
        given()
                .header(TestUtils.authToken(TestConstants.USER_3_TOKEN))
                .when()
                .get("/dish/" + dishId + "/tag")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("", hasSize(17))
                .body("tag.tag_id", hasItem(expectedTag))
                .body("tag.tag_id", not(hasItem(originalTag)));


    }

    @Test
    void testSetRatingTagOnDish_KO() throws Exception {
        // test case for failure.
        // set step to 100
        Long dishId = 9999999L;
        String ratingIdAsString = "391";
        String stepAsString = "105";


        // get dish, and assert tags are present
        // (start condition)
        given()
                .header(TestUtils.authToken(TestConstants.USER_3_TOKEN))
                .when()
                .get("/dish/" + dishId + "/tag")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("", hasSize(17));

        // tested call
        given()
                .header(TestUtils.authToken(TestConstants.USER_3_TOKEN))
                .when()
                .put("/dish/" + dishId + "/rating/" + ratingIdAsString + "/" + stepAsString)
                .then()
                .statusCode(404);

    }

    @Test
    void testDeleteLastTag_NoDelete() throws Exception {
        Long mainDishId = 320L;
        Long oliveOilId = 336L;

        Dish dish = new DishTestBuilder()
                .withTag(mainDishId, TagType.DishType)
                .withTag(oliveOilId, TagType.Ingredient)
                .withName("testDeleteLastTag_NoDelete")
                .buildModel();

        Long createdId = createDish(TestConstants.USER_3_TOKEN, dish);

        Dish created = retrieveDish(TestConstants.USER_3_TOKEN, createdId);
        Assertions.assertNotNull(created);

        removeRatingTags(TestConstants.USER_3_TOKEN, createdId);

        // delete tag - main dish
        String url = String.format("/dish/%d/tag/%d", createdId, mainDishId);
        given()
                .header(TestUtils.authToken(TestConstants.USER_3_TOKEN))
                .when()
                .delete(url)
                .then()
                .statusCode(anyOf(is(200), is(204), is(206)));

        // retrieve dish
        Dish afterDelete = retrieveDish(TestConstants.USER_3_TOKEN, createdId);
        Assertions.assertNotNull(afterDelete);
        Set<String> dishTagIds = afterDelete.getTags().stream().map(Tag::getId).collect(Collectors.toSet());
        Assertions.assertEquals(2, dishTagIds.size());  // no change
        Assertions.assertTrue(dishTagIds.contains(String.valueOf(mainDishId)));  // main dish id wasn't deleted
    }

    @Test
    void testDeleteLastTag_OK() throws Exception {
        Long mainDishId = 320L;
        Long appetizerId = 333L;
        Long oliveOilId = 336L;

        Dish dish = new DishTestBuilder()
                .withTag(mainDishId, TagType.DishType)
                .withTag(appetizerId, TagType.DishType)
                .withTag(oliveOilId, TagType.Ingredient)
                .withName("testDeleteLastTag_NoDelete")
                .buildModel();

        Long createdId = createDish(TestConstants.USER_3_TOKEN, dish);

        Dish created = retrieveDish(TestConstants.USER_3_TOKEN, createdId);
        Assertions.assertNotNull(created);

        removeRatingTags(TestConstants.USER_3_TOKEN, createdId);

        // delete tag - main dish
        String url = String.format("/dish/%d/tag/%d", createdId, mainDishId);
        given()
                .header(TestUtils.authToken(TestConstants.USER_3_TOKEN))
                .when()
                .delete(url)
                .then()
                .statusCode(anyOf(is(200), is(204), is(206)));

        // retrieve dish
        Dish afterDelete = retrieveDish(TestConstants.USER_3_TOKEN, createdId);
        Assertions.assertNotNull(afterDelete);
        Set<String> dishTagIds = afterDelete.getTags().stream().map(Tag::getId).collect(Collectors.toSet());
        Assertions.assertEquals(2, dishTagIds.size());
        Assertions.assertFalse(dishTagIds.contains(String.valueOf(mainDishId)));  // main dish id was deleted
    }

    @Test
    void testDeleteLastTag_PartialDelete() throws Exception {
        Long mainDishId = 320L;
        Long blackPepperId = 334L;
        Long oliveOilId = 336L;

        Dish dish = new DishTestBuilder()
                .withTag(mainDishId, TagType.DishType)
                .withTag(blackPepperId, TagType.Ingredient)
                .withTag(oliveOilId, TagType.Ingredient)
                .withName("testDeleteLastTag_PartialDelete")
                .buildModel();

        Long createdId = createDish(TestConstants.USER_3_TOKEN, dish);

        Dish created = retrieveDish(TestConstants.USER_3_TOKEN, createdId);
        Assertions.assertNotNull(created);

        // remove rating tags
        removeRatingTags(TestConstants.USER_3_TOKEN, createdId);

        // remove mainDish, blackPepper, and oliveOil should
        // result in deletion of blackPepper and oliveOil, but not mainDish
        List<Long> deleteTags = Arrays.asList(mainDishId, blackPepperId, oliveOilId);
        String deleteList = FlatStringUtils.flattenListOfLongsToString(deleteTags, ",");
        String url = String.format("/dish/%d/tag?removeTags=%s", createdId, deleteList);
        given()
                .header(TestUtils.authToken(TestConstants.USER_3_TOKEN))
                .contentType(ContentType.JSON)
                .when()
                .put(url)
                .then()
                .statusCode(anyOf(is(200), is(204), is(206)));

        // retrieve list, and check
        Dish updated = retrieveDish(TestConstants.USER_3_TOKEN, createdId);
        Assertions.assertNotNull(updated);

        Assertions.assertEquals(1, updated.getTags().size());
        Tag lastTag = updated.getTags().get(0);
        Assertions.assertEquals(String.valueOf(mainDishId), lastTag.getId());

    }

    private void removeRatingTags(String token, Long createdId) throws Exception {
        Dish created = retrieveDish(token, createdId);
        List<String> deleteTags = created.getTags().stream()
                .filter(t -> t.getTagType().equals("Rating"))
                .map(Tag::getId)
                .collect(Collectors.toList());

        String deleteList = FlatStringUtils.flattenListToString(deleteTags, ",");
        String url = String.format("/dish/%d/tag?removeTags=%s", createdId, deleteList);
        given()
                .header(TestUtils.authToken(token))
                .contentType(ContentType.JSON)
                .when()
                .put(url)
                .then()
                .statusCode(anyOf(is(200), is(204), is(206)));

    }

    @Test
    void testDeleteLastTag_SuccessfulDelete() throws Exception {
        Long mainDishId = 320L;
        Long appetizerId = 333L;
        Long blackPepperId = 334L;
        Long oliveOilId = 336L;

        Dish dish = new DishTestBuilder()
                .withTag(mainDishId, TagType.DishType)
                .withTag(appetizerId, TagType.DishType)
                .withTag(blackPepperId, TagType.Ingredient)
                .withTag(oliveOilId, TagType.Ingredient)
                .withName("testDeleteLastTag_PartialDelete")
                .buildModel();

        Long createdId = createDish(TestConstants.USER_3_TOKEN, dish);

        Dish created = retrieveDish(TestConstants.USER_3_TOKEN, createdId);
        Assertions.assertNotNull(created);

        removeRatingTags(TestConstants.USER_3_TOKEN, createdId);

        // remove mainDish, blackPepper, and oliveOil should
        // result in deletion of mainDish, blackPepper and oliveOil: could deleted
        // dishtype mainDish since a different dish type was present.
        List<Long> deleteTags = Arrays.asList(mainDishId, blackPepperId, oliveOilId);
        String deleteList = FlatStringUtils.flattenListOfLongsToString(deleteTags, ",");
        String url = String.format("/dish/%d/tag?removeTags=%s", createdId, deleteList);
        given()
                .header(TestUtils.authToken(TestConstants.USER_3_TOKEN))
                .contentType(ContentType.JSON)
                .when()
                .put(url)
                .then()
                .statusCode(anyOf(is(200), is(204), is(206)));

        // retrieve list, and check
        Dish updated = retrieveDish(TestConstants.USER_3_TOKEN, createdId);
        Assertions.assertNotNull(updated);

        Assertions.assertEquals(1, updated.getTags().size());
        Tag lastTag = updated.getTags().get(0);
        Assertions.assertEquals(String.valueOf(appetizerId), lastTag.getId());
    }

    private Long createDish(String token, Dish dish) throws Exception {
        String dishJson = json(dish);

        String url = "/dish";

        Response response = given()
                .header(TestUtils.authToken(token))
                .contentType(ContentType.JSON)
                .body(dishJson)
                .when()
                .post(url)
                .then()
                .statusCode(201)
                .extract()
                .response();

        String header = response.getHeader("Location");
        String stringId = header.substring(header.lastIndexOf("/") + 1);
        return Long.valueOf(stringId);
    }

    private Dish retrieveDish(String token, Long dishId) throws Exception {
        String responseBody = given()
                .header(TestUtils.authToken(token))
                .when()
                .get("/dish/" + dishId)
                .then()
                .statusCode(200)
                .extract()
                .body().asString();

        ObjectMapper objectMapper = new ObjectMapper();
        DishResource afterList = objectMapper.readValue(responseBody, DishResource.class);
        Assertions.assertNotNull(afterList);
        return afterList.getDish();
    }


    private String json(Object o) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(o);
    }

}
