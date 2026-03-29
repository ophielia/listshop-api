/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.api;

import com.meg.listshop.Application;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.api.model.*;
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
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.*;
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
class LayoutRestControllerTest {

    @Container
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    @LocalServerPort
    public int serverPort;

    private static final String baseUserToken = "token99999";
    private static final String emptyUserToken = "token101010";
    private static final String newUserToken = "token121212";

    private static final String tagIdApple = "65";
    private static final String tagIdOrange = "45";
    private static final String tagIdLemon = "357";

    @PostConstruct
    public void initRestAssured() {
        RestAssured.port = serverPort;
        RestAssured.urlEncodingEnabled = false;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void testDefaultLayoutAssignment() throws Exception {
        String categoryTemplateId = "998901";  // Forbidden Area
        String tagIdEliza = "1000124";
        String tagIdAlexander = "1000125";

        // Base User assigns alexander (1000125) to Layout "Forbidden Area" (998901)
        // Base User assigns eliza (1000124) to Layout "Forbidden Area" (998901)
        MappingPost mapping = new MappingPost();
        mapping.setCategoryId(categoryTemplateId);
        mapping.setTagIds(Arrays.asList(tagIdEliza, tagIdAlexander));
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

        // New User creates new tag as child of hamilton (1000123)
        String newTagUlr = "/tag/1000123/child";

        Tag tag = new Tag("Aaron Burr, sir");
        tag = tag.tagType(TagType.Ingredient.name());
        String tagString = json(tag);

        Response response = given()
                .header(TestUtils.authToken(newUserToken))
                .contentType(ContentType.JSON)
                .body(tagString)
                .when()
                .post(newTagUlr)
                .then()
                .statusCode(Matchers.is(Matchers.both(Matchers.greaterThanOrEqualTo(200)).and(Matchers.lessThan(300))))
                .extract()
                .response();
        String newTagId = extractResultId(response);

        // New user creates new list
        ListGenerateProperties properties = new ListGenerateProperties();
        properties.setAddFromStarter(false);
        properties.setGenerateMealplan(false);

        String jsonProperties = json(properties);

        String createListUrl = "/shoppinglist";
        response = given()
                .header(TestUtils.authToken(newUserToken))
                .contentType(ContentType.JSON)
                .body(jsonProperties)
                .when()
                .post(createListUrl)
                .then()
                .statusCode(201)
                .extract()
                .response();
        String newListId = extractResultId(response);

        // New user adds new tag to list
        String addToListUrl = String.format("/shoppinglist/%s/tag/%s", newListId, newTagId);
        given()
                .header(TestUtils.authToken(newUserToken))
                .contentType(ContentType.JSON)
                .body(jsonProperties)
                .when()
                .post(addToListUrl);

        // New user retrieves list
        String retrieveList = String.format("/shoppinglist/%s", newListId);
        String jsonList = given()
                .header(TestUtils.authToken(newUserToken))
                .when()
                .get(retrieveList)
                .then()
                .extract()
                .body().asString();
        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        ShoppingListResource afterList = objectMapper.readValue(jsonList, ShoppingListResource.class);
        ShoppingList resultList = afterList.getShoppingList();

        // Creation of new tag
        Assertions.assertNotNull(resultList);
        Assertions.assertEquals(1, resultList.getCategories().size());
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
        ListLayoutListResource resource = parseResourceFromString(responseContent);
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
        ListLayoutListResource resource = parseResourceFromString(responseContent);
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
    }

    @Test
    void testGetUserLayoutsEmpty() throws Exception {
        // get base user layouts - checking that given layout is fully filled in
        String url = "/layout/user";
        String responseContent = given()
                .header(TestUtils.authToken(emptyUserToken))
                .contentType(ContentType.JSON)
                .when()
                .get(url)
                .then()
                .statusCode(200)
                .extract()
                .body().asString();

        ListLayoutListResource resource = parseResourceFromString(responseContent);
        Assertions.assertNotNull(resource.getEmbeddedList().getListLayoutResourceList());
        Assertions.assertEquals(0, resource.getEmbeddedList().getListLayoutResourceList().size(), "empty list should be returned");
    }


    @Test
    void testGetUserLayouts() throws Exception {
        // get base user layouts - checking that given layout is fully filled in
        String url = "/layout/user";
        String responseContent = given()
                .header(TestUtils.authToken(baseUserToken))
                .contentType(ContentType.JSON)
                .when()
                .get(url)
                .then()
                .statusCode(200)
                .body("_embedded.list_layout_list", Matchers.hasSize(2))
                .extract()
                .body().asString();

        ListLayoutListResource resource = parseResourceFromString(responseContent);
        Assertions.assertNotNull(resource);
        Map<Long, ListLayout> resultMap = resource.getEmbeddedList().getListLayoutResourceList().stream()
                .map(ListLayoutResource::getListLayout)
                .collect(Collectors.toMap(ListLayout::getLayoutId, Function.identity()));
        Assertions.assertEquals(2, resultMap.keySet().size(), "two layouts returned");
        Assertions.assertTrue(resultMap.containsKey(999L), "contains layout 999");
        ListLayout defaultLayout = resultMap.get(999L);
        Assertions.assertTrue(defaultLayout.isDefault(), "default layout is marked as such");
        Assertions.assertEquals(1, defaultLayout.getCategories().size(), "default layout has 1 category");
        Assertions.assertEquals(3, defaultLayout.getCategories().get(0).getTags().size(), "default layouts category contains 3 tags");
        ListLayout otherLayout = resultMap.get(998L);
        Assertions.assertFalse(otherLayout.isDefault(), "other layout is not default");
        Assertions.assertEquals(1, otherLayout.getCategories().size(), "other layout has 1 category");
        Assertions.assertEquals(3, otherLayout.getCategories().get(0).getTags().size(), "other layouts category contains 3 tags");

    }

    @Test
    void testGetDefaultUserLayoutNotLoggedIn() throws Exception {
        // get base user layouts - checking that given layout is fully filled in
        String url = "/layout/default";
        String responseContent = given()
                .contentType(ContentType.JSON)
                .when()
                .get(url)
                .then()
                .statusCode(200)
                .extract()
                .body().asString();

        ListLayoutResource resource = parseSingleResourceFromString(responseContent);
        Assertions.assertNotNull(resource);
        ListLayout defaultLayout = resource.getListLayout();
        Assertions.assertTrue(defaultLayout.isDefault(), "default layout is marked as such");
        Assertions.assertTrue(defaultLayout.getCategories().size() > 5, "default layout has more than 5");
        Optional<ListLayoutCategory> produce = defaultLayout.getCategories().stream()
                .filter(c -> Objects.equals(c.getName(), "Produce"))
                .findFirst();
        Assertions.assertTrue(produce.isPresent(), "produce category exists");
        Assertions.assertTrue(produce.get().getTags().size() > 10, "produce has at least 10 tags");
        // tag "Peggy" is user-specific, and should not be in ListLayout
        Optional<Tag> peggyTag = defaultLayout.getCategories().stream()
                .flatMap(c -> c.getTags().stream())
                .filter(t -> Objects.equals(t.getName(), "Peggy"))
                .findFirst();
        Assertions.assertFalse(peggyTag.isPresent());
    }

    @Test
    void testGetDefaultLayoutLoggedIn() throws Exception {
        // New User creates new tag as child of hamilton (1000123)
        String newTagUlr = "/tag/1000123/child";

        Tag tag = new Tag("Thomas Jefferson");
        tag = tag.tagType(TagType.Ingredient.name());
        String tagString = json(tag);

        given()
                .header(TestUtils.authToken(baseUserToken))
                .contentType(ContentType.JSON)
                .body(tagString)
                .when()
                .post(newTagUlr)
                .then()
                .statusCode(Matchers.is(Matchers.both(Matchers.greaterThanOrEqualTo(200)).and(Matchers.lessThan(300))));


        // get base user layouts - checking that given layout is fully filled in
        String url = "/layout/default";
        String responseContent = given()
                .header(TestUtils.authToken(baseUserToken))
                .contentType(ContentType.JSON)
                .when()
                .get(url)
                .then()
                .statusCode(200)
                .extract()
                .body().asString();

        ListLayoutResource resource = parseSingleResourceFromString(responseContent);
        Assertions.assertNotNull(resource);
        ListLayout defaultLayout = resource.getListLayout();
        Assertions.assertTrue(defaultLayout.isDefault(), "default layout is marked as such");
        Assertions.assertTrue(defaultLayout.getCategories().size() > 5, "default layout has more than 5");
        Optional<ListLayoutCategory> produce = defaultLayout.getCategories().stream()
                .filter(c -> Objects.equals(c.getName(), "Produce"))
                .findFirst();
        Assertions.assertTrue(produce.isPresent(), "produce category exists");
        Assertions.assertTrue(produce.get().getTags().size() > 10, "produce has at least 10 tags");
        // check for existance of "Thomas Jefferson" tag
        Optional<Tag> jeffersonTag = defaultLayout.getCategories().stream()
                .flatMap(c -> c.getTags().stream())
                .filter(t -> Objects.equals(t.getName(), "Thomas Jefferson"))
                .findFirst();
        Assertions.assertTrue(jeffersonTag.isPresent());
    }

    @Test
    void testGetUserCategories() throws Exception {
        String url = "/layout/user/categories";
        String responseContent = given()
                .header(TestUtils.authToken(emptyUserToken))
                .contentType(ContentType.JSON)
                .when()
                .get(url)
                .then()
                .statusCode(200)
                .extract()
                .body().asString();

        CategoryListResource categoryList = parseCategoryListResourceFromString(responseContent);
        // the empty user should contain only the default categories
        // note - the layout id isn't included in the category resource, so we have to test with ids.
        Assertions.assertEquals(7, categoryList.getEmbeddedList().getCategoryResourceList().size());
        Set<Long> categoryIds = categoryList.getEmbeddedList().getCategoryResourceList().stream()
                .map(CategoryResource::getCategory)
                .map(Category::getId)
                .filter(cid -> cid > 10 && !cid.equals(1041L))
                .collect(Collectors.toSet());
        Assertions.assertTrue(categoryIds.isEmpty());
    }

    @Test
    void testGetUserCategoriesWithOverlap() throws Exception {
        // map tags to default category "Meat" (category_id 5)
        String categoryTemplateId = "5";

        MappingPost mapping = new MappingPost();
        mapping.setCategoryId(categoryTemplateId);
        mapping.setTagIds(Arrays.asList(tagIdApple, tagIdLemon, tagIdOrange));
        String payload = json(mapping);

        // make call
        String mapUrl = "/layout/user/mapping";
        given()
                .header(TestUtils.authToken(newUserToken))
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post(mapUrl)
                .then()
                .statusCode(200);

        // retrieve categories, and verify that category Meat does not have the id 10
        String url = "/layout/user/categories";
        String responseContent = given()
                .header(TestUtils.authToken(newUserToken))
                .contentType(ContentType.JSON)
                .when()
                .get(url)
                .then()
                .statusCode(200)
                .extract()
                .body().asString();

        CategoryListResource categoryList = parseCategoryListResourceFromString(responseContent);
        // the base user will overlap categories - the category Meat exists in both the default, and the user categories
        Map<String, Category> categoryMap = categoryList.getEmbeddedList().getCategoryResourceList().stream()
                .map(CategoryResource::getCategory)
                .collect(Collectors.toMap(Category::getName, Function.identity()));
        Assertions.assertTrue(categoryMap.containsKey("Meat"));
        Category meat = categoryMap.get("Meat");
        Assertions.assertNotEquals(10L, meat.getId(), "Id for category shouldn't be 10");
        // verify that Meat is the first category returned
        Assertions.assertEquals("Meat", categoryList.getEmbeddedList().getCategoryResourceList().get(0).getCategory().getName());
    }


    @Test
    void testGetUserCategoriesWithUserLayout() throws Exception {
        String url = "/layout/user/categories";
        String responseContent = given()
                .header(TestUtils.authToken(baseUserToken))
                .contentType(ContentType.JSON)
                .when()
                .get(url)
                .then()
                .statusCode(200)
                .extract()
                .body().asString();

        CategoryListResource categoryList = parseCategoryListResourceFromString(responseContent);
        // the base user will contain their categories, as well as the default categories
        // but there aren't any overlaps in the names - the use categories are distinct
        // note - the layout id isn't included in the category resource, so we have to test with ids.
        Assertions.assertEquals(8, categoryList.getEmbeddedList().getCategoryResourceList().size());
        Set<Long> categoryIds = categoryList.getEmbeddedList().getCategoryResourceList().stream()
                .map(CategoryResource::getCategory)
                .filter(c -> !c.getName().equalsIgnoreCase("special category"))  // exclude user category
                .map(Category::getId)
                .filter(cid -> cid > 10 && !cid.equals(1041L)) // exclude standard categories
                .collect(Collectors.toSet());
        Assertions.assertTrue(categoryIds.isEmpty());
    }

    @Test
    void testRetrieveUserDefaultLayout() throws Exception {
        String url = "/layout/user/categories";
        given()
                .header(TestUtils.authToken(baseUserToken))
                .contentType(ContentType.JSON)
                .when()
                .get(url)
                .then()
                .statusCode(200);
    }


    private String extractResultId(Response response) {
        String location = response.getHeader("Location");
        String[] locationParts = location.split("/");
        return locationParts[locationParts.length - 1];
    }

    private String json(Object o) throws IOException {
        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

        return objectMapper.writeValueAsString(o);
    }

    private ListLayoutListResource parseResourceFromString(String resultString) throws IOException {
        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

        ListLayoutListResource afterList = objectMapper.readValue(resultString, ListLayoutListResource.class);
        Assertions.assertNotNull(afterList);
        return afterList;
    }

    private ListLayoutResource parseSingleResourceFromString(String resultString) throws IOException {
        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

        ListLayoutResource afterList = objectMapper.readValue(resultString, ListLayoutResource.class);
        Assertions.assertNotNull(afterList);
        return afterList;
    }

    private CategoryListResource parseCategoryListResourceFromString(String resultString) throws IOException {
        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

        CategoryListResource afterList = objectMapper.readValue(resultString, CategoryListResource.class);
        Assertions.assertNotNull(afterList);
        return afterList;
    }
}
