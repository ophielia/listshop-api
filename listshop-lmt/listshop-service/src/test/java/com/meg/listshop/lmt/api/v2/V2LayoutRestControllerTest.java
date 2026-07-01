/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.api.v2;

import com.meg.listshop.Application;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.api.model.MappingPost;
import com.meg.listshop.lmt.api.model.v2.ListLayout;
import com.meg.listshop.lmt.api.model.v2.ListLayoutCategory;
import com.meg.listshop.lmt.api.model.v2.ListLayoutList;
import com.meg.listshop.lmt.api.model.v2.NestedTag;
import com.meg.listshop.test.TestUtils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.annotation.PostConstruct;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
@Sql(value = {"/sql/com/meg/atable/lmt/api/LayoutRestControlleTest.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sql/com/meg/atable/lmt/api/LayoutRestController_rollback.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class V2LayoutRestControllerTest {

    private static final String dadStarterJwtToken = "token34user";
    private static final String baseUserToken = "token99999";
    private static final String newUserToken = "token121212";
    private static final String tagIdApple = "65";
    private static final String tagIdOrange = "45";
    private static final String tagIdLemon = "357";

    @Container
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();
    @LocalServerPort
    public int serverPort;

    @PostConstruct
    public void initRestAssured() {
        RestAssured.port = serverPort;
        RestAssured.urlEncodingEnabled = false;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }


    @Test
    void testPostUserMappingsExisting() throws Exception {
        // create mapping post - map apples and oranges to default category Frozen
        // 10, 'Frozen'
        String categoryTemplateId = "10";
        String userId = "99999";

        MappingPost mapping = new MappingPost();
        mapping.setCategoryId(categoryTemplateId);
        mapping.setTagIds(Arrays.asList(tagIdApple, tagIdLemon, tagIdOrange));
        String payload = json(mapping);

        // make call
        String url = "/v2/layout/user/mapping";
        given()
                .header(TestUtils.authToken(baseUserToken))
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post(url)
                .then()
                .statusCode(200);

        // retrieve user layouts
        String getResultUrl = "/v2/layout/default";
        ListLayout defaultLayout = given()
                .header(TestUtils.authToken(baseUserToken))
                .contentType(ContentType.JSON)
                .when()
                .get(getResultUrl)
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(ListLayout.class);

        Assertions.assertNotNull(defaultLayout);
        ListLayoutCategory frozenCategory = defaultLayout.getCategories().stream()
                .filter(listLayoutCategory -> listLayoutCategory.getName().equalsIgnoreCase("frozen"))
                .findFirst()
                .orElse(null);
        Assertions.assertNotNull(frozenCategory);

        Set<String> tagIds = frozenCategory.getTags().stream()
                .map(NestedTag::getTagId)
                .collect(Collectors.toSet());
        Assertions.assertTrue(tagIds.contains(tagIdApple), "apples are there");
        Assertions.assertTrue(tagIds.contains(tagIdOrange), "oranges are there");
        Assertions.assertTrue(tagIds.contains(tagIdLemon), "lemons are there");


        // Part II - move these tags to a different category
        //  999901, 'Special Category'
        categoryTemplateId = "999901";
        mapping.setCategoryId(categoryTemplateId);
        mapping.setTagIds(Collections.singletonList(tagIdLemon));
        payload = json(mapping);

        // make the call
        given()
                .header(TestUtils.authToken(baseUserToken))
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post(url)
                .then()
                .statusCode(200);

        // retrieve user layouts
        ListLayout newLayout = given()
                .header(TestUtils.authToken(baseUserToken))
                .contentType(ContentType.JSON)
                .when()
                .get(getResultUrl)
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(ListLayout.class);

        Assertions.assertNotNull(newLayout);

        // assert category "Special" exists
        Map<String, ListLayoutCategory> allCategories = newLayout.getCategories().stream()
                .collect(Collectors.toMap(ListLayoutCategory::getName, Function.identity()));
        ListLayoutCategory specialCategory = allCategories.get("Special Category");
        ListLayoutCategory newFrozenCategory = allCategories.get("Frozen");

        //  lemon is in special
        Set<String> tagIdsInSpecial = specialCategory.getTags().stream()
                .map(NestedTag::getTagId)
                .collect(Collectors.toSet());
        Assertions.assertFalse(tagIdsInSpecial.contains(tagIdApple), "apples are not there");
        Assertions.assertFalse(tagIdsInSpecial.contains(tagIdOrange), "oranges are not there");
        Assertions.assertTrue(tagIdsInSpecial.contains(tagIdLemon), "lemons are there");

        //  lemon is not in frozen
        Set<String> tagIdsInFrozen = newFrozenCategory.getTags().stream()
                .map(NestedTag::getTagId)
                .collect(Collectors.toSet());
        Assertions.assertTrue(tagIdsInFrozen.contains(tagIdApple), "apples are  there");
        Assertions.assertTrue(tagIdsInFrozen.contains(tagIdOrange), "oranges are  there");
        Assertions.assertFalse(tagIdsInFrozen.contains(tagIdLemon), "lemons are not there");

    }

    @Test
    void testPostUserMappingsNewUser() throws Exception {
        // do mappings with user which doesn't have any layout or categoriew
        String categoryTemplateId = "10";
        String userId = "121212";

        MappingPost mapping = new MappingPost();
        mapping.setCategoryId(categoryTemplateId);
        mapping.setTagIds(Arrays.asList(tagIdApple, tagIdLemon, tagIdOrange));
        String payload = json(mapping);

        // make call
        String url = "/v2/layout/user/mapping";
        given()
                .header(TestUtils.authToken(newUserToken))
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post(url)
                .then()
                .statusCode(200);

        // retrieve user layouts
        String getResultUrl = "/v2/layout/default";
        ListLayout defaultLayout = given()
                .header(TestUtils.authToken(newUserToken))
                .contentType(ContentType.JSON)
                .when()
                .get(getResultUrl)
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(ListLayout.class);


        Assertions.assertNotNull(defaultLayout);
        // assert category "Frozen" exists
        ListLayoutCategory frozenCategory = defaultLayout.getCategories().stream()
                .filter(listLayoutCategory -> listLayoutCategory.getName().equalsIgnoreCase("Frozen"))
                .findFirst()
                .orElse(null);
        Assertions.assertNotNull(frozenCategory);

        // assert apples, oranges, and lemon are in frozen
        Set<String> tagIds = frozenCategory.getTags().stream()
                .map(NestedTag::getTagId)
                .collect(Collectors.toSet());
        Assertions.assertTrue(tagIds.contains(tagIdApple), "apples are there");
        Assertions.assertTrue(tagIds.contains(tagIdOrange), "oranges are there");
        Assertions.assertTrue(tagIds.contains(tagIdLemon), "lemons are there");

        // Part II - move these tags to a different category
        //  999901, 'Special Category'
        categoryTemplateId = "999901";
        mapping.setCategoryId(categoryTemplateId);
        mapping.setTagIds(Collections.singletonList(tagIdLemon));
        payload = json(mapping);

        // make the call
        given()
                .header(TestUtils.authToken(newUserToken))
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post(url)
                .then()
                .statusCode(200);

        // retrieve user layouts
        ListLayout afterDefault = given()
                .header(TestUtils.authToken(newUserToken))
                .contentType(ContentType.JSON)
                .when()
                .get(getResultUrl)
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(ListLayout.class);

        // assert category "Special" exists
        Map<String, ListLayoutCategory> allCategories = afterDefault.getCategories().stream()
                .collect(Collectors.toMap(ListLayoutCategory::getName, Function.identity()));
        ListLayoutCategory specialCategory = allCategories.get("Special Category");
        ListLayoutCategory afterFrozenCategory = allCategories.get("Frozen");

        //  lemon is in special
        Set<String> tagIdsInSpecial = specialCategory.getTags().stream()
                .map(NestedTag::getTagId)
                .collect(Collectors.toSet());
        Assertions.assertFalse(tagIdsInSpecial.contains(tagIdApple), "apples are not there");
        Assertions.assertFalse(tagIdsInSpecial.contains(tagIdOrange), "oranges are not there");
        Assertions.assertTrue(tagIdsInSpecial.contains(tagIdLemon), "lemons are there");

        //  lemon is not in frozen
        Set<String> tagIdsInFrozen = afterFrozenCategory.getTags().stream()
                .map(NestedTag::getTagId)
                .collect(Collectors.toSet());
        Assertions.assertTrue(tagIdsInFrozen.contains(tagIdApple), "apples are  there");
        Assertions.assertTrue(tagIdsInFrozen.contains(tagIdOrange), "oranges are  there");
        Assertions.assertFalse(tagIdsInFrozen.contains(tagIdLemon), "lemons are not there");

    }

    @Test
    void testGetLayoutsNoUser() {
        String url = "/v2/layout/default";
        ListLayout result = given()
                .contentType(ContentType.JSON)
                .when()
                .get(url)
                .then()
                .statusCode(200)
        //        .body("list_layouts[0].name", Matchers.equalTo("RoughGrained"))
        //        .body("list_layouts[0].categories", Matchers.hasSize(7))
        //        .body("list_layouts[0].layout_id", Matchers.equalTo("5"))
        //        .body("list_layouts[0].is_default", Matchers.equalTo(true))
        //        .body("list_layouts[0].user_id", Matchers.equalTo("null"))
        //        .body("list_layouts[0].categories.name", Matchers.hasItem("Dry"))
        //        .body("list_layouts[0].categories.is_default", Matchers.hasItem(false))
        //        .body("list_layouts[0].categories.category_id", Matchers.hasItem("8"))
                .extract().as(ListLayout.class);
        Assertions.assertNotNull(result, "response should not be null");
    }

    @Test
    void testGetLayoutsUser() {
        String url = "/v2/layout/default";
        ListLayout result = given()
                .contentType(ContentType.JSON)
                .header(TestUtils.authToken(baseUserToken))
                .when()
                .get(url)
                .then()
                .statusCode(200)
                .body("categories", Matchers.hasSize(8))
                .extract().as(ListLayout.class);
        Assertions.assertNotNull(result, "response should not be null");
    }

    @Test
    void testGetLayoutsUserNoLayouts() {
        String url = "/v2/layout/default";
        ListLayout result = given()
                .contentType(ContentType.JSON)
                .header(TestUtils.authToken(newUserToken))
                .when()
                .get(url)
                .then()
                .statusCode(200)
                .body("categories", Matchers.hasSize(7))
                .extract().as(ListLayout.class);
        Assertions.assertNotNull(result, "response should not be null");
    }



    @Test
    void testGetCategoryForTagUserCategoryExists() {
        String tagId = "1000128"; // carrots
        String url = "/v2/layout/tag/" + tagId ;
        ListLayoutCategory result = given()
                .contentType(ContentType.JSON)
                .header(TestUtils.authToken(dadStarterJwtToken))
                .when()
                .get(url)
                .then()
                .statusCode(200)
                .extract().as(ListLayoutCategory.class);
        Assertions.assertNotNull(result, "response should not be null");
        // we expect to retrieve the layout "special category", which is the default for this tag
        Assertions.assertEquals("Dads Special Category", result.getName());
        Assertions.assertEquals("1000128", result.getId());
    }

    @Test
    void testGetCategoryForTagUserNoUser() {
        String tagId = "81"; // carrots
        String url = "/v2/layout/tag/" + tagId ;
        ListLayoutCategory result = given()
                .contentType(ContentType.JSON)
                .header(TestUtils.authToken(baseUserToken))
                .when()
                .get(url)
                .then()
                .statusCode(200)
                .extract().as(ListLayoutCategory.class);
        Assertions.assertNotNull(result, "response should not be null");
        // we expect to retrieve the layout "Produce", which is the default for carrots
        Assertions.assertEquals("Produce", result.getName());
        Assertions.assertEquals("6", result.getId());
    }


    private String json(Object o) throws IOException {
        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

        return objectMapper.writeValueAsString(o);
    }

}
