/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.api.v2;

import com.meg.listshop.Application;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.api.model.MappingPost;
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

        MappingPost mapping = new MappingPost();
        mapping.setCategoryId(categoryTemplateId);
        mapping.setTagIds(Arrays.asList(tagIdApple, tagIdLemon, tagIdOrange));
        String payload = json(mapping);

        // make call
        String url = "/layout/user/mapping";
        given()
                .header(TestUtils.authToken(baseUserToken))
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post(url)
                .then()
                .statusCode(200);
        Assertions.assertTrue(false);
        // retrieve user layouts
        String getResultUrl = "/layout/user";
        String responseContent = given()
                .header(TestUtils.authToken(baseUserToken))
                .contentType(ContentType.JSON)
                .when()
                .get(getResultUrl)
                .then()
                .statusCode(200)
                .extract()
                .body().asString();

        // get default layout
/*        ListLayoutListResource resource = parseResourceFromString(responseContent);
        Assertions.assertNotNull(resource);
        Optional<ListLayout> listLayoutResult = resource.getEmbeddedList().getListLayoutResourceList().stream()
                .map(ListLayoutResource::getListLayout)
                .filter(ListLayout::isDefault)
                .findFirst();
        Assertions.assertTrue(listLayoutResult.isPresent(), "default exists");
        ListLayout layout = listLayoutResult.get();

        // assert category "Frozen" exists
        Optional<ListLayoutCategory> frozenCategoryOpt = layout.getCategories().stream()
                .filter(listLayoutCategory -> listLayoutCategory.getName().equalsIgnoreCase("Frozen"))
                .findFirst();
        Assertions.assertTrue(frozenCategoryOpt.isPresent());
        ListLayoutCategory category = frozenCategoryOpt.get();

        // assert apples, oranges, and lemon are in frozen
        Set<String> tagIds = category.getTags().stream()
                .map(Tag::getId)
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
        responseContent = given()
                .header(TestUtils.authToken(baseUserToken))
                .contentType(ContentType.JSON)
                .when()
                .get(getResultUrl)
                .then()
                .statusCode(200)
                .extract()
                .body().asString();

        // get default layout
        resource = parseResourceFromString(responseContent);
        Assertions.assertNotNull(resource);
        listLayoutResult = resource.getEmbeddedList().getListLayoutResourceList().stream()
                .map(ListLayoutResource::getListLayout)
                .filter(ListLayout::isDefault)
                .findFirst();
        Assertions.assertTrue(listLayoutResult.isPresent(), "default exists");
        layout = listLayoutResult.get();

        // assert category "Special" exists
        Map<String, ListLayoutCategory> allCategories = layout.getCategories().stream()
                .collect(Collectors.toMap(ListLayoutCategory::getName, Function.identity()));
        ListLayoutCategory specialCategory = allCategories.get("Special Category");
        ListLayoutCategory frozenCategory = allCategories.get("Frozen");

        //  lemon is in special
        Set<String> tagIdsInSpecial = specialCategory.getTags().stream()
                .map(Tag::getId)
                .collect(Collectors.toSet());
        Assertions.assertFalse(tagIdsInSpecial.contains(tagIdApple), "apples are not there");
        Assertions.assertFalse(tagIdsInSpecial.contains(tagIdOrange), "oranges are not there");
        Assertions.assertTrue(tagIdsInSpecial.contains(tagIdLemon), "lemons are there");

        //  lemon is not in frozen
        Set<String> tagIdsInFrozen = frozenCategory.getTags().stream()
                .map(Tag::getId)
                .collect(Collectors.toSet());
        Assertions.assertTrue(tagIdsInFrozen.contains(tagIdApple), "apples are  there");
        Assertions.assertTrue(tagIdsInFrozen.contains(tagIdOrange), "oranges are  there");
        Assertions.assertFalse(tagIdsInFrozen.contains(tagIdLemon), "lemons are not there");
        */
    }

    @Test
    void testPostUserMappingsNewUser() throws Exception {
        // do mappings with user which doesn't have any layout or categoriew
        String categoryTemplateId = "10";

        MappingPost mapping = new MappingPost();
        mapping.setCategoryId(categoryTemplateId);
        mapping.setTagIds(Arrays.asList(tagIdApple, tagIdLemon, tagIdOrange));
        String payload = json(mapping);

        // make call
        String url = "/layout/user/mapping";
        given()
                .header(TestUtils.authToken(newUserToken))
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post(url)
                .then()
                .statusCode(200);

        // retrieve user layouts
        String getResultUrl = "/layout/user";
        String responseContent = given()
                .header(TestUtils.authToken(newUserToken))
                .contentType(ContentType.JSON)
                .when()
                .get(getResultUrl)
                .then()
                .statusCode(200)
                .extract()
                .body().asString();

        // get default layout
        Assertions.assertTrue(false);
       /* ListLayoutListResource resource = parseResourceFromString(responseContent);
        Assertions.assertNotNull(resource);
        Optional<ListLayout> listLayoutResult = resource.getEmbeddedList().getListLayoutResourceList().stream()
                .map(ListLayoutResource::getListLayout)
                .filter(ListLayout::isDefault)
                .findFirst();
        Assertions.assertTrue(listLayoutResult.isPresent(), "default exists");
        ListLayout layout = listLayoutResult.get();

        // assert category "Frozen" exists
        Optional<ListLayoutCategory> frozenCategoryOpt = layout.getCategories().stream()
                .filter(listLayoutCategory -> listLayoutCategory.getName().equalsIgnoreCase("Frozen"))
                .findFirst();
        Assertions.assertTrue(frozenCategoryOpt.isPresent());
        ListLayoutCategory category = frozenCategoryOpt.get();

        // assert apples, oranges, and lemon are in frozen
        Set<String> tagIds = category.getTags().stream()
                .map(Tag::getId)
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
        responseContent = given()
                .header(TestUtils.authToken(newUserToken))
                .contentType(ContentType.JSON)
                .when()
                .get(getResultUrl)
                .then()
                .statusCode(200)
                .extract()
                .body().asString();

        // get default layout
        resource = parseResourceFromString(responseContent);
        Assertions.assertNotNull(resource);
        listLayoutResult = resource.getEmbeddedList().getListLayoutResourceList().stream()
                .map(ListLayoutResource::getListLayout)
                .filter(ListLayout::isDefault)
                .findFirst();
        Assertions.assertTrue(listLayoutResult.isPresent(), "default exists");
        layout = listLayoutResult.get();

        // assert category "Special" exists
        Map<String, ListLayoutCategory> allCategories = layout.getCategories().stream()
                .collect(Collectors.toMap(ListLayoutCategory::getName, Function.identity()));
        ListLayoutCategory specialCategory = allCategories.get("Special Category");
        ListLayoutCategory frozenCategory = allCategories.get("Frozen");

        //  lemon is in special
        Set<String> tagIdsInSpecial = specialCategory.getTags().stream()
                .map(Tag::getId)
                .collect(Collectors.toSet());
        Assertions.assertFalse(tagIdsInSpecial.contains(tagIdApple), "apples are not there");
        Assertions.assertFalse(tagIdsInSpecial.contains(tagIdOrange), "oranges are not there");
        Assertions.assertTrue(tagIdsInSpecial.contains(tagIdLemon), "lemons are there");

        //  lemon is not in frozen
        Set<String> tagIdsInFrozen = frozenCategory.getTags().stream()
                .map(Tag::getId)
                .collect(Collectors.toSet());
        Assertions.assertTrue(tagIdsInFrozen.contains(tagIdApple), "apples are  there");
        Assertions.assertTrue(tagIdsInFrozen.contains(tagIdOrange), "oranges are  there");
        Assertions.assertFalse(tagIdsInFrozen.contains(tagIdLemon), "lemons are not there");

        */
    }

    @Test
    void testGetLayoutsNoUser() {
        String url = "/v2/layout";
        String result = given()
                .contentType(ContentType.JSON)
                .when()
                .get(url)
                .then()
                .statusCode(200)
                .body("list_layouts[0].name", Matchers.equalTo("RoughGrained"))
                .body("list_layouts[0].categories", Matchers.hasSize(7))
                .body("list_layouts[0].default", Matchers.equalTo(true))
                .body("list_layouts[0].layout_id", Matchers.equalTo("5"))
                .body("list_layouts[0].is_default", Matchers.equalTo(true))
                .body("list_layouts[0].user_id", Matchers.equalTo("null"))
                .body("list_layouts[0].categories.name", Matchers.hasItem("Dry"))
                .body("list_layouts[0].categories.is_default", Matchers.hasItem(false))
                .body("list_layouts[0].categories.category_id", Matchers.hasItem("8"))
                .extract().asString();
        Assertions.assertNotNull(result, "response should not be null");
    }

    @Test
    void testGetLayoutsUser() {
        String url = "/v2/layout";
        String result = given()
                .contentType(ContentType.JSON)
                .header(TestUtils.authToken(baseUserToken))
                .when()
                .get(url)
                .then()
                .statusCode(200)
                .body("list_layouts", Matchers.hasSize(3))
                .extract().asString();
        Assertions.assertNotNull(result, "response should not be null");
    }

    @Test
    void testGetLayoutsUserNoLayouts() {
        String url = "/v2/layout";
        String result = given()
                .contentType(ContentType.JSON)
                .header(TestUtils.authToken(dadStarterJwtToken))
                .when()
                .get(url)
                .then()
                .statusCode(200)
                .body("list_layouts", Matchers.hasSize(1))
                .extract().asString();
        Assertions.assertNotNull(result, "response should not be null");
    }


    void testGetCategoryForTag() {
    }

    void testGetCategoryForTagUserCategoryExists() {
    }

    void testGetCategoryForTagUserNoUser() {
    }


    private String json(Object o) throws IOException {
        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

        return objectMapper.writeValueAsString(o);
    }

}
