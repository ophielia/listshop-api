/*
 * The List Shop
 *
 * Copyright (c) 2022-2026.
 */

package com.meg.listshop.lmt.api.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meg.listshop.Application;
import com.meg.listshop.common.DateUtils;
import com.meg.listshop.common.RoundingUtils;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.api.model.ItemOperationPut;
import com.meg.listshop.lmt.api.model.ItemOperationType;
import com.meg.listshop.lmt.api.model.ListAddProperties;
import com.meg.listshop.lmt.api.model.ListGenerateProperties;
import com.meg.listshop.lmt.api.model.v2.*;
import com.meg.listshop.lmt.data.entity.ListItemDetailEntity;
import com.meg.listshop.lmt.data.entity.ListItemEntity;
import com.meg.listshop.lmt.data.repository.ItemRepository;
import com.meg.listshop.lmt.data.repository.ListItemDetailRepository;
import com.meg.listshop.test.TestConstants;
import com.meg.listshop.test.TestUtils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.Resource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.StreamUtils;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

@ExtendWith(SpringExtension.class)
@Testcontainers
@SpringBootTest(classes = Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(value = {"/sql/com/meg/atable/lmt/api/ShoppingListRestControllerTest.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sql/com/meg/atable/lmt/api/ShoppingListRestControllerTest_rollback.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ShoppingListRestControllerTest {

    @Container
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();
    private static String jwtToken;
    private static String meJwtToken;
    private static String lastListJwtToken;
    private static String noStarterJwtToken;
    private static String dadStarterJwtToken;
    @LocalServerPort
    public int serverPort;
    @Autowired
    ItemRepository itemRepository;
    @Value("classpath:/data/v2shoppingListRestControllerTest_mergeList.json")
    Resource resourceFile;
    @Value("classpath:/data/shoppingListRestControllerTest_noMergeList.json")
    Resource resourceFileNoMerge;
    @Value("classpath:/data/v2shoppingListRestControllerTest_mergeListStale.json")
    Resource resourceFileStale;
    @Value("classpath:/data/shoppingListRestControllerTest_mergeListEmpty.json")
    Resource resourceFileEmpty;
    @Autowired
    private ListItemDetailRepository listItemDetailRepository;

    @PostConstruct
    public void initRestAssured() {
        RestAssured.port = serverPort;
        RestAssured.urlEncodingEnabled = false;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        Assertions.assertNotNull("the JSON message converter must not be null");
    }

    @BeforeEach
    void setup() {
        jwtToken = TestConstants.USER_1_TOKEN;
        meJwtToken = TestConstants.USER_3_TOKEN;
        lastListJwtToken = "token99999"; // this one is not in TestConstants, but used for user with ID 99999
        noStarterJwtToken = TestConstants.USER_4_TOKEN;
        dadStarterJwtToken = "token34user"; // not in TestConstants
    }


    @Test
    void testRetrieveLists() {
        String response = given()
                .header(TestUtils.authToken(jwtToken))
                .when()
                .get("/v2/shoppinglist")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().asString();

        Assertions.assertNotNull(response);
    }

    @Test
    void testRetrieveMostRecentList() throws Exception {
        Long testId = 509990L;

        // updating list, so that it _is_ the most recent
        ShoppingListPut shoppingList = new ShoppingListPut(String.valueOf(testId))
                .name("updated list most recent")
                .isStarterList(false);
        String payload = json(shoppingList);

        given()
                .header(TestUtils.authToken(meJwtToken))
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .put("/v2/shoppinglist/" + testId)
                .then()
                .statusCode(200);

        // now, testing the most recent call
        ShoppingList list = given()
                .header(TestUtils.authToken(meJwtToken))
                .when()
                .get("/v2/shoppinglist/mostrecent")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("list_id", Matchers.isA(String.class))
                .extract()
                .as(ShoppingList.class);

        Assertions.assertNotNull(list);
        // assert all items have "last_changed" filled in
        List<Date> itemsWithoutLastChanged = list.getCategories().stream()
                .flatMap(category -> category.getItems().stream())
                .map(ShoppingListItem::getLastChanged)
                .filter(Objects::isNull)
                .toList();
        Assertions.assertTrue(itemsWithoutLastChanged.isEmpty(), "all items should have last_changed");
    }

    @Test
    void testRetrieveStarterList() {
        Long testId = 509991L;

        given()
                .header(TestUtils.authToken(meJwtToken))
                .when()
                .get("/v2/shoppinglist/starter")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("list_id", Matchers.isA(String.class))
                .body("list_id", Matchers.equalTo(String.valueOf(testId)));
    }

    @Test
    void testRetrieveStarterListNotFound() {
        given()
                .header(TestUtils.authToken(noStarterJwtToken))
                .when()
                .get("/v2/shoppinglist/starter")
                .then()
                .statusCode(404);
    }

    @Test
    void testRetrieveListById() {
        Long testId = 509992L;

        String json = given()
                .header(TestUtils.authToken(meJwtToken))
                .when()
                .get("/v2/shoppinglist/" + testId)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("list_id", Matchers.isA(String.class))
                .body("list_id", Matchers.equalTo(String.valueOf(testId)))
                .body("categories", Matchers.hasSize(2))
                .body("categories.name", Matchers.hasItems("Produce"))
                .body("categories[0].items", Matchers.hasSize(5))
                .body("categories[0].items[0].tag.tag_id", Matchers.equalTo("500"))
                .body("categories[0].items[0].tag.name", Matchers.equalTo("tag1"))
                .body("categories[0].items[0].amount.quantity", Matchers.equalTo(0.5F))
                .body("categories[0].items[0].amount.rounded_quantity", Matchers.equalTo(0.5F))
                .body("categories[0].items[0].amount.quantity_display", Matchers.equalTo("1/2"))
                .body("categories[0].items[0].amount.display", Matchers.equalTo("0.5 lb"))
                .body("categories[0].items[0].amount.unit_id", Matchers.equalTo("1008"))
                .body("categories[0].items[0].amount.unit_display", Matchers.equalTo("lb"))
                .body("categories[0].items[0].amount.display", Matchers.equalTo("0.5 lb"))
                .body("categories[0].items[0].details", Matchers.hasSize(1))
                .body("categories[0].items[0].details[0]", Matchers.hasKey("amount"))
                .body("categories[0].items[0].details[0].dish_id", Matchers.equalTo("50999010"))


                .body("legend", Matchers.hasSize(6))
                .body("legend.source_type",
                        Matchers.hasItems("DISH", "LIST"))
                .extract().asString();
        Assertions.assertNotNull(json);

    }

    @Test
    void testRetrieveListById_NotFound() {
        Long dummyTestId = 12345678901L;

        given()
                .header(TestUtils.authToken(meJwtToken))
                .when()
                .get("/v2/shoppinglist/" + dummyTestId)
                .then()
                .statusCode(404);
    }

    @Test
    void testCustomLayout() throws Exception {
        Long customLayoutListId = 10101010L;
        Long standardLayoutListId = 90909090L;

        ShoppingList standardLayoutList = retrieveList(dadStarterJwtToken, standardLayoutListId);
        Map<String, ShoppingListItem> standardResultMap = standardLayoutList.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getTagId(), Function.identity()));
        Assertions.assertNotNull(standardResultMap);


        ShoppingList customLayoutList = retrieveList(meJwtToken, customLayoutListId);
        Map<String, ShoppingListItem> customResultMap = customLayoutList.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getTagId(), Function.identity()));
        Assertions.assertNotNull(customResultMap);

        // item count should be equal
        Assertions.assertEquals(customResultMap.keySet().size(), standardResultMap.keySet().size(), "item count should be equal.");
        // category count should not be equal
        Assertions.assertNotEquals(standardLayoutList.getCategories().size(), customLayoutList.getCategories().size(), "category count should not be equal.");
        // custom
        Assertions.assertEquals(3, customLayoutList.getCategories().size(), "custom should have 3 categories");
        Optional<ShoppingListCategory> specialCategory = customLayoutList.getCategories().stream()
                .filter(c -> c.getName().equals("Special")).findFirst();
        Assertions.assertTrue(specialCategory.isPresent(), "on category should be called 'Special'");
        Assertions.assertEquals(1, specialCategory.get().getItems().size(), "special contains one item");
        ShoppingListItem tomatoes = specialCategory.get().getItems().get(0);
        Assertions.assertEquals("tomatoes", tomatoes.getTag().getName(), "tomatoes are tomatoes");
    }

    @Test
    void testUpdateList() throws Exception {
        Long testId = 509991L;

        ShoppingListPut shoppingList = new ShoppingListPut(String.valueOf(testId))
                .name("updated list")
                .isStarterList(false);

        String payload = json(shoppingList);

        given()
                .header(TestUtils.authToken(meJwtToken))
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .put("/v2/shoppinglist/" + testId)
                .then()
                .statusCode(200);

    }

    @Test
    void testUpdateList_starterListChange() throws Exception {
        Long testId = 509990L;
        Long oldStarterId = 509991L;

        ShoppingListPut shoppingList = new ShoppingListPut(String.valueOf(testId))
                .name("now is starter list")
                .isStarterList(true);

        String payload = json(shoppingList);

        given()
                .header(TestUtils.authToken(meJwtToken))
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .put("/v2/shoppinglist/" + testId)
                .then()
                .statusCode(200);

        given()
                .header(TestUtils.authToken(meJwtToken))
                .when()
                .get("/v2/shoppinglist/" + testId)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("list_id", Matchers.isA(String.class))
                .body("list_id", Matchers.equalTo(String.valueOf(testId)))
                .body("is_starter_list", Matchers.equalTo(true));

        // now retrieve old starter list and ensure that isStarter is false
        given()
                .header(TestUtils.authToken(meJwtToken))
                .when()
                .get("/v2/shoppinglist/" + oldStarterId)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("list_id", Matchers.isA(String.class))
                .body("list_id", Matchers.equalTo(String.valueOf(oldStarterId)))
                .body("is_starter_list", Matchers.equalTo(false));


    }

    @Test
    void testDeleteItemFromList() {
        Long listId = TestConstants.LIST_3_ID;
        String url = "/v2/shoppinglist/" + listId + "/item/" + 501L;
        given()
                .header(TestUtils.authToken(jwtToken))
                .when()
                .delete(url)
                .then()
                .statusCode(204);
    }

    @Test
    void testGenerateFromMealPlan() throws Exception {


        Long mealPlanId = 65505L;

        String url = "/v2/shoppinglist/mealplan/" + mealPlanId;
        String location = given()
                .header(TestUtils.authToken(jwtToken))
                .contentType(ContentType.JSON)
                .when()
                .post(url)
                .then()
                .statusCode(201)
                .extract()
                .header("Location");

        Assertions.assertNotNull(location);
        String[] urlTokens = StringUtils.split(location, "/");
        Long newListId = Long.valueOf(urlTokens[(urlTokens).length - 1]);

        // now, retrieve the list
        ShoppingList source = retrieveList(jwtToken, newListId);
        Map<String, ShoppingListItem> resultMap = source.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getTagId(), Function.identity()));
        Assertions.assertNotNull(resultMap);

        // check tag occurences in result

        // 502 - 3
        Assertions.assertNotNull(resultMap.get("1"));
        // 503 - 2
        Assertions.assertNotNull(resultMap.get("12"));
        Assertions.assertEquals(Optional.of(1).get(), resultMap.get("12").getDetails().size()); // showing 1 in result map
        // 436 - 1
        Assertions.assertNotNull(resultMap.get("436"));
        Assertions.assertEquals(Integer.valueOf(1), resultMap.get("436").getDetails().size());

    }

    @Test
    void testCreateList() throws Exception {
        ListGenerateProperties properties = new ListGenerateProperties();
        properties.setAddFromStarter(true);
        properties.setGenerateMealplan(false);

        String jsonProperties = json(properties);

        String url = "/v2/shoppinglist";

        given()
                .header(TestUtils.authToken(jwtToken))
                .contentType(ContentType.JSON)
                .body(jsonProperties)
                .when()
                .post(url)
                .then()
                .statusCode(201);

    }

    @Test
    void testSetCrossedOffForItem() {
        Long listId = 6666L;
        String url = "/v2/shoppinglist/" + listId + "/item/shop/" + 60660L
                + "?crossOff=true";
        given()
                .header(TestUtils.authToken(jwtToken))
                .contentType(ContentType.JSON)
                .when()
                .post(url)
                .then()
                .statusCode(204);
    }

    @Test
    void testCrossOffAllItemsOnList() {

        Long listId = TestConstants.LIST_1_ID;
        String url = "/v2/shoppinglist/" + listId + "/item/shop"
                + "?crossOff=true";
        given()
                .header(TestUtils.authToken(jwtToken))
                .contentType(ContentType.JSON)
                .when()
                .post(url)
                .then()
                .statusCode(204);
    }

    @Test
    void testAddListToList() {
        Long listId = TestConstants.LIST_3_ID;
        Long fromListId = TestConstants.LIST_1_ID;

        String url = "/v2/shoppinglist/" + listId + "/list/" + fromListId;
        given()
                .header(TestUtils.authToken(jwtToken))
                .contentType(ContentType.JSON)
                .when()
                .post(url)
                .then()
                .statusCode(204);

        // retrieve list and verify
        given()
                .header(TestUtils.authToken(jwtToken))
                .when()
                .get("/v2/shoppinglist/" + listId)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("list_id", Matchers.isA(String.class))
                .body("list_id", Matchers.equalTo(String.valueOf(listId)));
    }

    @Test
    void testRemoveListFromList() {
        Long listId = 609990L;
        Long fromListId = 609991L;

        String url = "/v2/shoppinglist/" + listId + "/list/" + fromListId;
        String listAfterDelete = given()
                .header(TestUtils.authToken(jwtToken))
                .contentType(ContentType.JSON)
                .when()
                .delete(url)
                .then()
                .statusCode(204)
                .extract()
                .asString();

        // retrieve list and verify
        listAfterDelete = given()
                .header(TestUtils.authToken(jwtToken))
                .when()
                .get("/v2/shoppinglist/" + listId)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("list_id", Matchers.isA(String.class))
                .body("list_id", Matchers.equalTo(String.valueOf(listId)))
                .extract()
                .asString();


        // contains tag ids 500, 503
        Assertions.assertTrue(listAfterDelete.contains("\"tag_id\":\"503\","));
        Assertions.assertTrue(listAfterDelete.contains("\"tag_id\":\"500\","));

        // doesn't contain tagIds 501, 502 504
        Assertions.assertFalse(listAfterDelete.contains("\"tag_id\":\"501\","));
        Assertions.assertFalse(listAfterDelete.contains("\"tag_id\":\"502\","));
        Assertions.assertFalse(listAfterDelete.contains("\"tag_id\":\"504\","));
    }

    @Test
    void testAddDishToList() throws Exception {
        Long listId = TestConstants.LIST_1_ID;
        Long tagId1 = 500L;
        Long tagId2 = 501L;
        String url = "/v2/shoppinglist/" + listId + "/dish/" + TestConstants.DISH_7_ID;
        given()
                .header(TestUtils.authToken(jwtToken))
                .contentType(ContentType.JSON)
                .when()
                .post(url)
                .then()
                .statusCode(204);

        ShoppingList result = retrieveList(jwtToken, listId);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getCategories().size() > 0);

        Map<String, ShoppingListItem> standardResultMap = result.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .filter(i -> i.getSources() != null &&
                        i.getSources().contains("DISH" + TestConstants.DISH_7_ID))
                .collect(Collectors.toMap(item -> item.getTag().getTagId(), Function.identity()));
        Assertions.assertNotNull(standardResultMap);
        Assertions.assertEquals(2, standardResultMap.keySet().size());
        Assertions.assertTrue(standardResultMap.containsKey(String.valueOf(tagId1)));
        Assertions.assertTrue(standardResultMap.containsKey(String.valueOf(tagId2)));

    }

    @Test
    void testAddDishesToList() throws Exception {
        Long broccoliId = 21L;
        Long fetaId = 37L;
        ListGenerateProperties properties = new ListGenerateProperties();
        properties.setAddFromStarter(true);
        properties.setGenerateMealplan(false);
        String jsonProperties = json(properties);
        String listId = createList(jsonProperties, meJwtToken);

        ListAddProperties addProperties = new ListAddProperties();

        Long[] dishSourceArray = {TestConstants.DISH_8_ID, TestConstants.DISH_2_ID, TestConstants.DISH_1_ID};
        List<String> dishIdsAsStrings = Arrays.stream(dishSourceArray)
                .map(String::valueOf)
                .toList();
        addProperties.setDishSources(dishIdsAsStrings);
        String addDishProperties = json(addProperties);

        String url = "/v2/shoppinglist/" + listId + "/dish";
        given()
                .header(TestUtils.authToken(meJwtToken))
                .contentType(ContentType.JSON)
                .body(addDishProperties)
                .when()
                .post(url)
                .then()
                .statusCode(Matchers.is(Matchers.both(Matchers.greaterThanOrEqualTo(200)).and(Matchers.lessThan(300))));

        ShoppingList result = retrieveList(meJwtToken, Long.valueOf(listId));
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getCategories().size() > 0);

        Map<String, ShoppingListItem> standardResultMap = result.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getTagId(), Function.identity()));
        Assertions.assertNotNull(standardResultMap);
        // test brocolli is there, twice, with sources 109, and 45 (dish)
        ShoppingListItem broccoliItem = standardResultMap.get(String.valueOf(broccoliId));
        Assertions.assertNotNull(broccoliItem, "broccoli item should be present");
        Assertions.assertEquals(2, broccoliItem.getSources().size());
        Assertions.assertTrue(broccoliItem.getSources().contains("DISH" + TestConstants.DISH_8_ID));
        Assertions.assertTrue(broccoliItem.getSources().contains("DISH" + TestConstants.DISH_2_ID));
        // test feta cheese is there, from dish 1, tag_id 37
        ShoppingListItem fetaItem = standardResultMap.get(String.valueOf(fetaId));
        Assertions.assertNotNull(fetaItem, "feta item should be present");
        Assertions.assertEquals(1, fetaItem.getSources().size());
        Assertions.assertTrue(fetaItem.getSources().contains("DISH" + TestConstants.DISH_1_ID));
    }

    private String createList(String jsonProperties, String token) {
        String url = "/v2/shoppinglist";
        String location = given()
                .header(TestUtils.authToken(token))
                .contentType(ContentType.JSON)
                .body(jsonProperties)
                .when()
                .post(url)
                .then()
                .statusCode(201)
                .extract()
                .header("Location");
        String[] splitLocation = location.split("/");
        return splitLocation[splitLocation.length - 1];
    }

    @Test
    void testAddDishToNewList() throws Exception {
        ListGenerateProperties properties = new ListGenerateProperties();
        properties.setAddFromStarter(true);
        properties.setGenerateMealplan(false);
        String jsonProperties = json(properties);
        String location = given()
                .header(TestUtils.authToken(jwtToken))
                .contentType(ContentType.JSON)
                .body(jsonProperties)
                .when()
                .post("/v2/shoppinglist")
                .then()
                .statusCode(201)
                .extract()
                .header("Location");
        String[] splitLocation = location.split("/");
        Long newListId = Long.valueOf(splitLocation[splitLocation.length - 1]);

        Long tagId1 = 500L;
        Long tagId2 = 501L;
        String url = "/v2/shoppinglist/" + newListId + "/dish/" + TestConstants.DISH_7_ID;
        given()
                .header(TestUtils.authToken(jwtToken))
                .contentType(ContentType.JSON)
                .when()
                .post(url)
                .then()
                .statusCode(204);

        ShoppingList result = retrieveList(jwtToken, newListId);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getCategories().size() > 0);

        Map<String, ShoppingListItem> standardResultMap = result.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .filter(i -> i.getSources() != null &&
                        i.getSources().contains("DISH" + TestConstants.DISH_7_ID))
                .collect(Collectors.toMap(item -> item.getTag().getTagId(), Function.identity()));
        Assertions.assertNotNull(standardResultMap);
        Assertions.assertEquals(2, standardResultMap.keySet().size());
        Assertions.assertTrue(standardResultMap.containsKey(String.valueOf(tagId1)));
        Assertions.assertTrue(standardResultMap.containsKey(String.valueOf(tagId2)));

    }

    @Test
    @Disabled("used count isn't quite finished in v2")
    void testUpdateItemUsedCount() {

        Long listId = 7777L;
        Long tagId = 500L;
        Integer usedCount = 6;
        String url = "/v2/shoppinglist/" + listId + "/tag/" + tagId + "/count/" + usedCount;
        given()
                .header(TestUtils.authToken(meJwtToken))
                .contentType(ContentType.JSON)
                .when()
                .put(url)
                .then()
                .statusCode(204);

        // make sure the item has been updated
        ListItemEntity resultItem = itemRepository.getFilledItemByListAndTag(listId, tagId);

        Assertions.assertEquals(usedCount, resultItem.getDetails().size());
    }

    @Test
    void testAddMealPlanToList() throws Exception {

        Long listId = 51000L;
        Long mealPlanId = 65505L;

        String url = "/v2/shoppinglist/" + listId + "/mealplan/" + mealPlanId;
        given()
                .header(TestUtils.authToken(jwtToken))
                .contentType(ContentType.JSON)
                .when()
                .put(url)
                .then()
                .statusCode(204);

        // now, retrieve the list
        ShoppingList source = retrieveList(jwtToken, listId);
        Map<String, ShoppingListItem> sourceResultMap = source.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getTagId(), Function.identity()));

        Assertions.assertNotNull(sourceResultMap);
        // check tag occurences in result
        // 501 - 1
        Assertions.assertNotNull(sourceResultMap.get("81"));
        Assertions.assertEquals(Optional.of(1).get(), sourceResultMap.get("81").getDetails().size());

        // 502 - 3
        Assertions.assertNotNull(sourceResultMap.get("1"));
        Assertions.assertEquals(Integer.valueOf(3), sourceResultMap.get("1").getDetails().size());  // showing 1 in result map
        // 503 - 2
        Assertions.assertNotNull(sourceResultMap.get("12"));
        Assertions.assertEquals(Integer.valueOf(2), sourceResultMap.get("12").getDetails().size()); // showing 1 in result map
        // 436 - 1
        Assertions.assertNotNull(sourceResultMap.get("436"));
        Assertions.assertEquals(Integer.valueOf(1), sourceResultMap.get("436").getDetails().size());

    }

    @Test
    void testRemoveDishFromList() throws Exception {
        Long listId = TestConstants.LIST_1_ID;
        String dish1Id = "66500";
        String dish2Id = "66501";
        String targetTagId = "1";

        // add dish 66500 - contains tag id 1
        String url = "/v2/shoppinglist/" + listId + "/dish/" + dish1Id;
        given()
                .header(TestUtils.authToken(jwtToken))
                .contentType(ContentType.JSON)
                .when()
                .post(url)
                .then()
                .statusCode(204);

        // add dish 66501, contains tag id 1
        url = "/v2/shoppinglist/" + listId + "/dish/" + dish2Id;
        given()
                .header(TestUtils.authToken(jwtToken))
                .contentType(ContentType.JSON)
                .when()
                .post(url)
                .then()
                .statusCode(204);

        // affirm list has tag_id 1 2 times (tag_id 1 is green chili)
        ShoppingList result = retrieveList(jwtToken, listId);
        // affirm we have tag id 1 there - in Category Dry, with id 1
        ShoppingListItem resultItem = result.getCategories().stream().flatMap(c -> c.getItems().stream())
                .filter(item -> item.getTag().getTagId().equals(targetTagId))
                .findFirst().orElse(null);
        Assertions.assertNotNull(resultItem);
        Assertions.assertTrue(resultItem.getSources().contains("DISH" + dish1Id));
        Assertions.assertTrue(resultItem.getSources().contains("DISH" + dish2Id));
        Assertions.assertEquals(Integer.valueOf(2), resultItem.getDetails().size());


        // the remove test
        // remove dish 66500 (dish1Id)
        url = "/v2/shoppinglist/" + listId + "/dish/" + dish1Id;
        given()
                .header(TestUtils.authToken(jwtToken))
                .contentType(ContentType.JSON)
                .when()
                .delete(url)
                .then()
                .statusCode(204);

        // assert list has tag_id 1 1 time
        result = retrieveList(jwtToken, listId);
        // affirm we have tag id 1 there - in Category Dry, with id 1
        resultItem = result.getCategories().stream().flatMap(c -> c.getItems().stream())
                .filter(item -> item.getTag().getTagId().equals(targetTagId))
                .findFirst().orElse(null);
        Assertions.assertNotNull(resultItem);
        ShoppingListItemDetails toCheck = pullDetailWithDishId(resultItem, dish1Id);
        Assertions.assertNull(toCheck);
        Assertions.assertNotNull(pullDetailWithDishId(resultItem, dish2Id));
        Assertions.assertEquals(Integer.valueOf(1), resultItem.getDetails().size());
    }

    private ShoppingListItemDetails pullDetailWithDishId(ShoppingListItem resultItem, String dish1Id) {
        return resultItem.getDetails().stream()
                .filter(item -> item.getDishId().equals(dish1Id))
                .findFirst().orElse(null);
    }

    @Test
    void testChangeListLayout() {

        Long listId = TestConstants.LIST_1_ID;
        String url = "/v2/shoppinglist/" + listId + "/layout/" + TestConstants.LIST_LAYOUT_2_ID;
        given()
                .header(TestUtils.authToken(jwtToken))
                .contentType(ContentType.JSON)
                .when()
                .post(url)
                .then()
                .statusCode(204);

    }

    @Test
    void deleteAllItemsFromList() throws Exception {

        ListGenerateProperties properties = new ListGenerateProperties();
        properties.setAddFromStarter(true);
        properties.setGenerateMealplan(false);

        String jsonProperties = json(properties);

        String url = "/v2/shoppinglist";

        String location = given()
                .header(TestUtils.authToken(jwtToken))
                .contentType(ContentType.JSON)
                .body(jsonProperties)
                .when()
                .post(url)
                .then()
                .statusCode(201)
                .extract()
                .header("Location");

        Assertions.assertNotNull(location);
        String[] urlTokens = StringUtils.split(location, "/");
        Long listId = Long.valueOf(urlTokens[(urlTokens).length - 1]);

        String clearUrl = "/v2/shoppinglist/" + listId + "/item";
        given()
                .header(TestUtils.authToken(jwtToken))
                .contentType(ContentType.JSON)
                .when()
                .delete(clearUrl)
                .then()
                .statusCode(204);

        // now, retrieve the list
        ShoppingList source = retrieveList(jwtToken, listId);
        Map<String, ShoppingListItem> resultMap = source.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getTagId(), Function.identity()));
        Assertions.assertNotNull(resultMap);
        Assertions.assertTrue(resultMap.isEmpty());
    }

    @Test
    void deleteItemOperation_Move() throws Exception {
        Long sourceListId = 7777L;  // 500 (CrossedOff), 501, 502, 505 (CrossedOff)
        Long destinationListId = 6666L;  // 501, 502, 503, 505
        List<Long> tagIdsForUpdate = Arrays.asList(500L, 502L);

        ItemOperationPut operationUpdate = new ItemOperationPut();
        operationUpdate.setDestinationListId(destinationListId);
        operationUpdate.setOperation(ItemOperationType.Move.name());
        operationUpdate.setTagIds(tagIdsForUpdate);

        String jsonProperties = json(operationUpdate);

        String url = "/v2/shoppinglist/" + sourceListId + "/item";

        given()
                .header(TestUtils.authToken(meJwtToken))
                .contentType(ContentType.JSON)
                .body(jsonProperties)
                .when()
                .put(url)
                .then()
                .statusCode(200);

        // now, retrieve the list
        ShoppingList source = retrieveList(meJwtToken, sourceListId);
        Map<String, ShoppingListItem> sourceResultMap = source.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getTagId(), Function.identity()));

        Assertions.assertNotNull(sourceResultMap);
        Assertions.assertEquals(2, sourceResultMap.keySet().size());
        // 500 shouldn't be there
        Assertions.assertFalse(sourceResultMap.containsKey("500"));
        // check destination list
        ShoppingList destination = retrieveList(meJwtToken, destinationListId);
        Map<String, ShoppingListItem> destinationResultMap = destination.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getTagId(), Function.identity()));
        Assertions.assertNotNull(destinationResultMap);
        Assertions.assertEquals(5, destinationResultMap.keySet().size());
        // 500 should be there with count 1
        Assertions.assertTrue(destinationResultMap.containsKey("500"));
        ShoppingListItem testElement = destinationResultMap.get("500");
        Assertions.assertEquals(Long.valueOf(1), testElement.getDetails().size());
        // 502 should be there with a count of 2
        testElement = destinationResultMap.get("502");
        Assertions.assertEquals(Long.valueOf(2), testElement.getDetails().size());

    }

    @Test
    void deleteItemOperation_Copy() throws Exception {
        Long sourceListId = 7777L;  // 500 (CrossedOff), 501, 502, 505 (CrossedOff)
        Long destinationListId = 6666L;  // 501, 502, 503, 505
        List<Long> tagIdsForUpdate = Arrays.asList(500L, 501L, 504L);

        ItemOperationPut operationUpdate = new ItemOperationPut();
        operationUpdate.setDestinationListId(destinationListId);
        operationUpdate.setOperation(ItemOperationType.Copy.name());
        operationUpdate.setTagIds(tagIdsForUpdate);

        String jsonProperties = json(operationUpdate);

        String url = "/v2/shoppinglist/" + sourceListId + "/item";

        given()
                .header(TestUtils.authToken(meJwtToken))
                .contentType(ContentType.JSON)
                .body(jsonProperties)
                .when()
                .put(url)
                .then()
                .statusCode(200);

        // now, retrieve the list
        ShoppingList source = retrieveList(meJwtToken, sourceListId);
        Map<String, ShoppingListItem> sourceResultMap = source.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getTagId(), Function.identity()));

        Assertions.assertEquals(4, sourceResultMap.keySet().size());
        // check destination list
        ShoppingList destination = retrieveList(meJwtToken, destinationListId);
        Map<String, ShoppingListItem> destinationResultMap = destination.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getTagId(), Function.identity()));

        Assertions.assertNotNull(destinationResultMap);
        Assertions.assertEquals(5, destinationResultMap.keySet().size());
        // 500 should be there with count 1
        Assertions.assertTrue(destinationResultMap.containsKey("500"));
        ShoppingListItem testElement = destinationResultMap.get("500");
        Assertions.assertEquals(Long.valueOf(1), testElement.getDetails().size());
        // 501 should be there with a count of 2
        testElement = destinationResultMap.get("501");
        Assertions.assertEquals(Long.valueOf(2), testElement.getDetails().size());

    }

    @Test
    void deleteItemOperation_MoveCrossedOff_New() throws Exception {
        Long sourceListId = 7777L;  // 500 (CrossedOff), 501, 502, 505 (CrossedOff)
        Long destinationListId = 6666L;  // 501, 502, 503, 505, 505
        List<Long> tagIdsForUpdate = Arrays.asList(500L, 502L);

        ItemOperationPut operationUpdate = new ItemOperationPut();
        operationUpdate.setDestinationListId(destinationListId);
        operationUpdate.setOperation(ItemOperationType.Move.name());
        operationUpdate.setTagIds(tagIdsForUpdate);

        String jsonProperties = json(operationUpdate);

        String url = "/v2/shoppinglist/" + sourceListId + "/item";

        given()
                .header(TestUtils.authToken(meJwtToken))
                .contentType(ContentType.JSON)
                .body(jsonProperties)
                .when()
                .put(url)
                .then()
                .statusCode(200);

        // now, retrieve the list
        ShoppingList source = retrieveList(meJwtToken, sourceListId);
        Map<String, ShoppingListItem> sourceResultMap = source.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getTagId(), Function.identity()));

        Assertions.assertNotNull(sourceResultMap);
        Assertions.assertEquals(2, sourceResultMap.keySet().size());
        // 500 shouldn't be there
        Assertions.assertFalse(sourceResultMap.containsKey(500L));
        // check destination list
        ShoppingList destination = retrieveList(meJwtToken, destinationListId);
        Map<String, ShoppingListItem> destinationResultMap = destination.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getTagId(), Function.identity()));

        Assertions.assertNotNull(destinationResultMap);
        Assertions.assertEquals(5, destinationResultMap.keySet().size());
        // 500 should be there with count 1, crossedOff
        Assertions.assertTrue(destinationResultMap.containsKey("500"));
        ShoppingListItem testElement = destinationResultMap.get("500");
        Assertions.assertEquals(Long.valueOf(1), Long.valueOf(testElement.getDetails().size()));
        Assertions.assertNull(testElement.getCrossedOff());  // when item is moved, it loses it's crossed off status
        // 502 should be there with a count of 2
        testElement = destinationResultMap.get("502");
        Assertions.assertEquals(Long.valueOf(2), Long.valueOf(testElement.getDetails().size()));
    }

    @Test
    void deleteItemOperation_MoveCrossedOff_Existing() throws Exception {
        Long sourceListId = 7777L;  // 500 (CrossedOff), 501, 502, 505 (CrossedOff)
        Long destinationListId = 6666L;  // 501, 502, 503, 505, 505
        List<Long> tagIdsForUpdate = Arrays.asList(505L, 502L);

        ItemOperationPut operationUpdate = new ItemOperationPut();
        operationUpdate.setDestinationListId(destinationListId);
        operationUpdate.setOperation(ItemOperationType.Move.name());
        operationUpdate.setTagIds(tagIdsForUpdate);

        String jsonProperties = json(operationUpdate);

        String url = "/v2/shoppinglist/" + sourceListId + "/item";

        given()
                .header(TestUtils.authToken(meJwtToken))
                .contentType(ContentType.JSON)
                .body(jsonProperties)
                .when()
                .put(url)
                .then()
                .statusCode(200);

        // now, retrieve the list and check results
        ShoppingList list = retrieveList(meJwtToken, sourceListId);

        Map<String, ShoppingListItem> allSourceResultMap = list.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getTagId(), Function.identity()));

        Assertions.assertNotNull(allSourceResultMap);
        Assertions.assertEquals(2, allSourceResultMap.keySet().size());
        // 505 shouldn't be there
        Assertions.assertFalse(allSourceResultMap.containsKey("505"));

        // check destination list
        ShoppingList destination = retrieveList(meJwtToken, destinationListId);
        Map<String, ShoppingListItem> destinationResultMap = destination.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getTagId(), Function.identity()));

        Assertions.assertNotNull(destinationResultMap);
        Assertions.assertEquals(4, destinationResultMap.keySet().size());
        // 505 should be there with count 2, not crossedOff
        Assertions.assertTrue(destinationResultMap.containsKey("505"));
        ShoppingListItem testElement = destinationResultMap.get("505");
        Assertions.assertEquals(Long.valueOf(2), Long.valueOf(testElement.getDetails().size()));
        Assertions.assertNull(testElement.getCrossedOff());
        // 502 should be there with a count of 2
        testElement = destinationResultMap.get("502");
        Assertions.assertEquals(Long.valueOf(2), Long.valueOf(testElement.getDetails().size()));
    }

    @Test
    void deleteItemOperation_Remove() throws Exception {
        Long sourceListId = 7777L;  // 500 (CrossedOff), 501, 502, 505 (CrossedOff)
        List<Long> tagIdsForUpdate = Arrays.asList(500L, 501L, 504L);

        ItemOperationPut operationUpdate = new ItemOperationPut();
        operationUpdate.setDestinationListId(null);
        operationUpdate.setOperation(ItemOperationType.Remove.name());
        operationUpdate.setTagIds(tagIdsForUpdate);

        String jsonProperties = json(operationUpdate);

        String url = "/v2/shoppinglist/" + sourceListId + "/item";

        given()
                .header(TestUtils.authToken(meJwtToken))
                .contentType(ContentType.JSON)
                .body(jsonProperties)
                .when()
                .put(url)
                .then()
                .statusCode(200);

        // now, retrieve the list
        ShoppingList source = retrieveList(meJwtToken, sourceListId);
        Map<String, ShoppingListItem> sourceResultMap = source.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getTagId(), Function.identity()));

        Assertions.assertNotNull(sourceResultMap);
        Assertions.assertEquals(2, sourceResultMap.keySet().size());
        // 500 shouldn't be there
        Assertions.assertFalse(sourceResultMap.containsKey(500L));

    }

    @Test
    void deleteItemOperation_RemoveCrossedOff() throws Exception {
        Long sourceListId = 77777L;  // 500, 501, 502

        ItemOperationPut operationUpdate = new ItemOperationPut();
        operationUpdate.setDestinationListId(null);
        operationUpdate.setOperation(ItemOperationType.RemoveCrossedOff.name());
        operationUpdate.setTagIds(new ArrayList<>());

        String jsonProperties = json(operationUpdate);

        // get crossed off ids before call
        ShoppingList before = retrieveList(meJwtToken, sourceListId);
        List<String> crossedOffIds = before.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .filter(i -> i.getRemoved() != null)
                .map(i -> i.getTag().getTagId())
                .toList();

        String url = "/v2/shoppinglist/" + sourceListId + "/item";

        given()
                .header(TestUtils.authToken(meJwtToken))
                .contentType(ContentType.JSON)
                .body(jsonProperties)
                .when()
                .put(url)
                .then()
                .statusCode(200);

        // now, retrieve the list
        ShoppingList source = retrieveList(meJwtToken, sourceListId);
        Map<String, ShoppingListItem> sourceResultMap = source.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getTagId(), Function.identity()));


        Assertions.assertNotNull(sourceResultMap);
        // check that none of the crossed off ids are in the map
        for (String crossedOffId : crossedOffIds) {
            Assertions.assertFalse(sourceResultMap.containsKey(crossedOffId), "crossed off ids shouldn't be in the list");
        }

    }

    @Test
    void deleteItemOperation_RemoveAll() throws Exception {
        Long sourceListId = 77777L;  // 500, 501, 502

        ItemOperationPut operationUpdate = new ItemOperationPut();
        operationUpdate.setDestinationListId(null);
        operationUpdate.setOperation(ItemOperationType.RemoveAll.name());
        operationUpdate.setTagIds(new ArrayList<>());

        String jsonProperties = json(operationUpdate);

        ShoppingList before = retrieveList(meJwtToken, sourceListId);

        String url = "/v2/shoppinglist/" + sourceListId + "/item";

        given()
                .header(TestUtils.authToken(meJwtToken))
                .contentType(ContentType.JSON)
                .body(jsonProperties)
                .when()
                .put(url)
                .then()
                .statusCode(200);

        // now, retrieve the list
        // check destination list
        ShoppingList source = retrieveList(meJwtToken, sourceListId);
        Map<String, ShoppingListItem> destinationResultMap = source.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getTagId(), Function.identity()));

        Assertions.assertNotNull(destinationResultMap);
        Assertions.assertEquals(0, destinationResultMap.keySet().size());

    }

    @Test
    void testRemoveDish_CrossedOffOk() throws Exception {
        // test for LS-883 here

        // create list
        ListGenerateProperties properties = new ListGenerateProperties();
        properties.setAddFromStarter(true);
        properties.setGenerateMealplan(false);

        String jsonProperties = json(properties);
        String listId = createList(jsonProperties, meJwtToken);

        String url = "/v2/shoppinglist";
        ShoppingList beforeList = given()
                .header(TestUtils.authToken(meJwtToken))
                .when()
                .get(url + "/" + listId)
                .then()
                .statusCode(200)
                .extract()
                .as(ShoppingList.class);
        Assertions.assertNotNull(beforeList);

        // add dish which contains tag carrots - 109
        String addDishOneUrl = String.format("%s/%s/dish/%s", url, listId, "109");
        given()
                .header(TestUtils.authToken(meJwtToken))
                .when()
                .post(addDishOneUrl)
                .then()
                .statusCode(204);

        // add another dish containing tag carrots - 112
        String addDishTwoUrl = String.format("%s/%s/dish/%s", url, listId, "112");
        given()
                .header(TestUtils.authToken(meJwtToken))
                .when()
                .post(addDishTwoUrl)
                .then()
                .statusCode(204);

        // check results
        ShoppingList afterDishTwo = given()
                .header(TestUtils.authToken(meJwtToken))
                .when()
                .get(url + "/" + listId)
                .then()
                .statusCode(200)
                .extract()
                .as(ShoppingList.class);
        Assertions.assertNotNull(afterDishTwo);

        // cross off all items on list
        String crossOffItemsUrl = String.format("%s/%s/item/shop?crossOff=true", url, listId);
        given()
                .header(TestUtils.authToken(meJwtToken))
                .when()
                .post(crossOffItemsUrl)
                .then()
                .statusCode(204);

        // check results
        ShoppingList afterCrossedOff = given()
                .header(TestUtils.authToken(meJwtToken))
                .when()
                .get(url + "/" + listId)
                .then()
                .statusCode(200)
                .extract()
                .as(ShoppingList.class);
        Assertions.assertNotNull(afterCrossedOff);


        // remove first dish - 109
        String deleteDishUrl = String.format("%s/%s/dish/%s", url, listId, "109");
        given()
                .header(TestUtils.authToken(meJwtToken))
                .when()
                .delete(deleteDishUrl)
                .then()
                .statusCode(204);

        // get Shopping List and confirm that carrots are still crossed off
        ShoppingList list = given()
                .header(TestUtils.authToken(meJwtToken))
                .when()
                .get(url + "/" + listId)
                .then()
                .statusCode(200)
                .extract()
                .as(ShoppingList.class);
        Optional<ShoppingListCategory> produce = list.getCategories()
                .stream()
                .filter(c -> c.getName().equals("Produce"))
                .findFirst();
        Assertions.assertTrue(produce.isPresent(), "list contains category produce");
        Optional<ShoppingListItem> carrotOpt = produce.get().getItems().stream()
                .filter(i -> i.getTag().getName().equals("carrots"))
                .findFirst();
        Assertions.assertTrue(carrotOpt.isPresent(), "carrots present in list");
        ShoppingListItem carrot = carrotOpt.get();
        Assertions.assertEquals(1, carrot.getSources().size(), "only one source key for carrots shown");
        Assertions.assertNotNull(carrot.getCrossedOff(), "carrots should be crossed off");
    }

    private ShoppingList retrieveList(String token, Long listId) throws Exception {
        ShoppingList shoppingList = given()
                .header(TestUtils.authToken(token))
                .when()
                .get("/v2/shoppinglist/" + listId)
                .then()
                .statusCode(200)
                .extract()
                .as(ShoppingList.class);
        return shoppingList;
    }

    private String json(Object o) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(o);
    }

    @Nested
    class TagTests {
        @Test
        void testAddTagToList() throws Exception {
            Long tagId = TestConstants.TAG_PASTA;
            ListGenerateProperties properties = new ListGenerateProperties();
            properties.setAddFromStarter(true);
            properties.setGenerateMealplan(false);

            String jsonProperties = json(properties);


            String location = given()
                    .header(TestUtils.authToken(jwtToken))
                    .contentType(ContentType.JSON)
                    .body(jsonProperties)
                    .when()
                    .post("/v2/shoppinglist")
                    .then()
                    .statusCode(201)
                    .extract()
                    .header("Location");

            Assertions.assertNotNull(location);
            String[] urlTokens = StringUtils.split(location, "/");
            Long listId = Long.valueOf(urlTokens[(urlTokens).length - 1]);

            PostListItem listPost = new PostListItem(tagId.toString(), null);

            String url = "/v2/shoppinglist/" + listId + "/item";
            given()
                    .header(TestUtils.authToken(jwtToken))
                    .contentType(ContentType.JSON)
                    .body(json(listPost))
                    .when()
                    .post(url)
                    .then()
                    .statusCode(204);

            // retrieve list and verify
            ShoppingList listWithNewItem = retrieveList(jwtToken, listId);
            Map<String, ShoppingListItem> standardResultMap = listWithNewItem.getCategories().stream()
                    .flatMap(c -> c.getItems().stream())
                    .collect(Collectors.toMap(item -> item.getTag().getTagId(), Function.identity()));
            Assertions.assertNotNull(standardResultMap);
            Assertions.assertTrue(standardResultMap.containsKey(String.valueOf(tagId)));
        }

        @Test
        void testAddTagToListWithAmount() throws Exception {
            Long tagId = TestConstants.TAG_PASTA;
            Long kgUnitId = 1014L;
            Long listId = createNewList(jwtToken);

            Amount amount = new Amount()
                    .withQuantity(1.5)
                    .withWholeQuantity(1)
                    .withFractionalQuantity("OneHalf")
                    .withUnitId(kgUnitId.toString());

            PostListItem listPost = new PostListItem(tagId.toString(), amount);
            String url = "/v2/shoppinglist/" + listId + "/item";
            given()
                    .header(TestUtils.authToken(jwtToken))
                    .contentType(ContentType.JSON)
                    .body(json(listPost))
                    .when()
                    .post(url)
                    .then()
                    .statusCode(204);

            // retrieve list and verify
            ShoppingList listWithNewItem = retrieveList(jwtToken, listId);
            Map<String, ShoppingListItem> standardResultMap = listWithNewItem.getCategories().stream()
                    .flatMap(c -> c.getItems().stream())
                    .collect(Collectors.toMap(item -> item.getTag().getTagId(), Function.identity()));
            Assertions.assertNotNull(standardResultMap);
            Assertions.assertTrue(standardResultMap.containsKey(String.valueOf(tagId)));
            ShoppingListItem testElement = standardResultMap.get(String.valueOf(tagId));
            Assertions.assertEquals(Long.valueOf(1), Long.valueOf(testElement.getDetails().size()));
            Assertions.assertEquals(3.333, RoundingUtils.roundToThousandths(testElement.getDetails().get(0).getAmount().getQuantity()), 0.001);
            Assertions.assertEquals("1008", testElement.getDetails().get(0).getAmount().getUnitId());
            Assertions.assertEquals("lb", testElement.getDetails().get(0).getAmount().getUnitDisplay());
            Assertions.assertEquals("3 1/3 lb", testElement.getDetails().get(0).getAmount().getDisplay());
            Assertions.assertEquals(3.333, RoundingUtils.roundToThousandths(testElement.getAmount().getQuantity()), 0.001);
            Assertions.assertEquals("1008", testElement.getAmount().getUnitId());
            Assertions.assertEquals("lb", testElement.getAmount().getUnitDisplay());
            Assertions.assertEquals("3 1/3 lb", testElement.getAmount().getDisplay());
            Assertions.assertEquals(3, testElement.getAmount().getWholeQuantity());
            Assertions.assertEquals("OneThird", testElement.getAmount().getFractionalQuantity());
        }

        @Test
        void testAddTagWithExistingAmountSameDomain() throws Exception {
            Long tagId = TestConstants.TAG_PASTA;
            Amount amount = new Amount()
                    .withQuantity(1.5)
                    .withWholeQuantity(1)
                    .withFractionalQuantity("OneHalf")
                    .withUnitId(TestConstants.UNIT_ID_LB.toString());
            Long listId = prepareListWithTagAndAmount(jwtToken,tagId.toString(), amount);

            // now, add the same amount again
            PostListItem listPost = new PostListItem(tagId.toString(), amount);
            String url = "/v2/shoppinglist/" + listId + "/item";
            given()
                    .header(TestUtils.authToken(jwtToken))
                    .contentType(ContentType.JSON)
                    .body(json(listPost))
                    .when()
                    .post(url)
                    .then()
                    .statusCode(204);

            // retrieve list and verify
            ShoppingList listWithNewItem = retrieveList(jwtToken, listId);
            Map<String, ShoppingListItem> standardResultMap = listWithNewItem.getCategories().stream()
                    .flatMap(c -> c.getItems().stream())
                    .collect(Collectors.toMap(item -> item.getTag().getTagId(), Function.identity()));
            Assertions.assertNotNull(standardResultMap);
            Assertions.assertTrue(standardResultMap.containsKey(String.valueOf(tagId)));
            ShoppingListItem testElement = standardResultMap.get(String.valueOf(tagId));
            Assertions.assertEquals(Long.valueOf(1), Long.valueOf(testElement.getDetails().size()));
            Assertions.assertEquals(3.000, RoundingUtils.roundToThousandths(testElement.getDetails().get(0).getAmount().getQuantity()), 0.001);
            Assertions.assertEquals("1008", testElement.getDetails().get(0).getAmount().getUnitId());
            Assertions.assertEquals("lb", testElement.getDetails().get(0).getAmount().getUnitDisplay());
            Assertions.assertEquals("3 lb", testElement.getDetails().get(0).getAmount().getDisplay());
            Assertions.assertEquals(3.000, RoundingUtils.roundToThousandths(testElement.getAmount().getQuantity()), 0.001);
            Assertions.assertEquals("1008", testElement.getAmount().getUnitId());
            Assertions.assertEquals("lb", testElement.getAmount().getUnitDisplay());
            Assertions.assertEquals("3 lb", testElement.getAmount().getDisplay());
            Assertions.assertEquals(3, testElement.getAmount().getWholeQuantity());
            Assertions.assertNull(testElement.getAmount().getFractionalQuantity());
        }

        @Test
        void testAddTagWithExistingAmountDifferentDomain() throws Exception {
            Long tagId = TestConstants.TAG_PASTA;
            Long kgUnitId = TestConstants.UNIT_ID_KG;
            Long lbUnitId = TestConstants.UNIT_ID_LB;
            Amount amount = new Amount()
                    .withQuantity(1.5)
                    .withWholeQuantity(1)
                    .withFractionalQuantity("OneHalf")
                    .withUnitId(kgUnitId.toString());
            Long listId = prepareListWithTagAndAmount(jwtToken,tagId.toString(), amount);

            // add 1.5 pounds
            Amount amountToAdd = new Amount()
                    .withQuantity(1.5)
                    .withWholeQuantity(1)
                    .withFractionalQuantity("OneHalf")
                    .withUnitId(lbUnitId.toString());

            // add this amount
            PostListItem listPost = new PostListItem(tagId.toString(), amountToAdd);
            String url = "/v2/shoppinglist/" + listId + "/item";
            given()
                    .header(TestUtils.authToken(jwtToken))
                    .contentType(ContentType.JSON)
                    .body(json(listPost))
                    .when()
                    .post(url)
                    .then()
                    .statusCode(204);


            // retrieve list and verify
            ShoppingList listWithNewItem = retrieveList(jwtToken, listId);
            Map<String, ShoppingListItem> standardResultMap = listWithNewItem.getCategories().stream()
                    .flatMap(c -> c.getItems().stream())
                    .collect(Collectors.toMap(item -> item.getTag().getTagId(), Function.identity()));
            Assertions.assertNotNull(standardResultMap);
            Assertions.assertTrue(standardResultMap.containsKey(String.valueOf(tagId)));
            ShoppingListItem testElement = standardResultMap.get(String.valueOf(tagId));
            Assertions.assertEquals(Long.valueOf(1), Long.valueOf(testElement.getDetails().size()));
            Assertions.assertEquals(4.875, RoundingUtils.roundToThousandths(testElement.getDetails().get(0).getAmount().getQuantity()), 0.001);
            Assertions.assertEquals("1008", testElement.getDetails().get(0).getAmount().getUnitId());
            Assertions.assertEquals("lb", testElement.getDetails().get(0).getAmount().getUnitDisplay());
            Assertions.assertEquals("4 7/8 lb", testElement.getDetails().get(0).getAmount().getDisplay());
            Assertions.assertEquals(4.875, RoundingUtils.roundToThousandths(testElement.getAmount().getQuantity()), 0.001);
            Assertions.assertEquals("1008", testElement.getAmount().getUnitId());
            Assertions.assertEquals("lb", testElement.getAmount().getUnitDisplay());
            Assertions.assertEquals("4 7/8 lb", testElement.getAmount().getDisplay());
            Assertions.assertEquals(4, testElement.getAmount().getWholeQuantity());
            Assertions.assertEquals("SevenEighths", testElement.getAmount().getFractionalQuantity());
        }

        @Test
        void testAddTagExistingAmountNewWithoutAmount() throws Exception {
            Long tagId = TestConstants.TAG_PASTA;
            Amount amount = new Amount()
                    .withQuantity(1.5)
                    .withWholeQuantity(1)
                    .withFractionalQuantity("OneHalf")
                    .withUnitId(TestConstants.UNIT_ID_LB.toString());
            Long listId = prepareListWithTagAndAmount(jwtToken, tagId.toString(), amount);

            // now, add the same tag WITHOUT an amount
            PostListItem listPost = new PostListItem(tagId.toString(), null);
            String url = "/v2/shoppinglist/" + listId + "/item";
            given()
                    .header(TestUtils.authToken(jwtToken))
                    .contentType(ContentType.JSON)
                    .body(json(listPost))
                    .when()
                    .post(url)
                    .then()
                    .statusCode(204);

            // retrieve list and verify
            ShoppingList listWithNewItem = retrieveList(jwtToken, listId);
            Map<String, ShoppingListItem> standardResultMap = listWithNewItem.getCategories().stream()
                    .flatMap(c -> c.getItems().stream())
                    .collect(Collectors.toMap(item -> item.getTag().getTagId(), Function.identity()));
            Assertions.assertNotNull(standardResultMap);
            Assertions.assertTrue(standardResultMap.containsKey(String.valueOf(tagId)));
            ShoppingListItem testElement = standardResultMap.get(String.valueOf(tagId));
            // the amount should still be there (adding without amount shouldn't remove existing amount)
            Assertions.assertEquals(1.5, RoundingUtils.roundToThousandths(testElement.getAmount().getQuantity()), 0.001);
            Assertions.assertEquals("1008", testElement.getAmount().getUnitId());
            Assertions.assertEquals("MIXED", testElement.getAmountType());
            Assertions.assertTrue(testElement.getDetails().get(0).isContainsUnspecified());

        }

        @Test
        void testAddTagExistingNoAmountNewWithAmount() throws Exception {
            Long tagId = TestConstants.TAG_PASTA;
            Long listId = createNewList(jwtToken);

            // first, add the tag WITHOUT an amount
            PostListItem listPostNoAmount = new PostListItem(tagId.toString(), null);
            String url = "/v2/shoppinglist/" + listId + "/item";
            given()
                    .header(TestUtils.authToken(jwtToken))
                    .contentType(ContentType.JSON)
                    .body(json(listPostNoAmount))
                    .when()
                    .post(url)
                    .then()
                    .statusCode(204);

            // now, add the same tag WITH an amount
            Amount amount = new Amount()
                    .withQuantity(1.5)
                    .withWholeQuantity(1)
                    .withFractionalQuantity("OneHalf")
                    .withUnitId(TestConstants.UNIT_ID_LB.toString());
            PostListItem listPostWithAmount = new PostListItem(tagId.toString(), amount);

            given()
                    .header(TestUtils.authToken(jwtToken))
                    .contentType(ContentType.JSON)
                    .body(json(listPostWithAmount))
                    .when()
                    .post(url)
                    .then()
                    .statusCode(204);

            // retrieve list and verify
            ShoppingList listWithNewItem = retrieveList(jwtToken, listId);
            Map<String, ShoppingListItem> standardResultMap = listWithNewItem.getCategories().stream()
                    .flatMap(c -> c.getItems().stream())
                    .collect(Collectors.toMap(item -> item.getTag().getTagId(), Function.identity()));
            Assertions.assertNotNull(standardResultMap);
            Assertions.assertTrue(standardResultMap.containsKey(String.valueOf(tagId)));
            ShoppingListItem testElement = standardResultMap.get(String.valueOf(tagId));
            // the amount should be there now
            Assertions.assertEquals(1.5, RoundingUtils.roundToThousandths(testElement.getAmount().getQuantity()), 0.001);
            Assertions.assertEquals("1008", testElement.getAmount().getUnitId());

            Assertions.assertEquals("MIXED", testElement.getAmountType());
            Assertions.assertTrue(testElement.getDetails().get(0).isContainsUnspecified());
        }

        @Test
        void testAddTagLargeAmounts() throws Exception {
            Long tagId = TestConstants.TAG_PASTA;
            Amount amount = new Amount()
                    .withQuantity(10.5)
                    .withWholeQuantity(10)
                    .withFractionalQuantity("OneHalf")
                    .withUnitId(TestConstants.UNIT_ID_LB.toString());

            Long listId = prepareListWithTagAndAmount(jwtToken, tagId.toString(), amount);

            // first, add the tag again with a large amount
            Amount anotherLargeAmount = new Amount()
                    .withQuantity(11.25)
                    .withWholeQuantity(11)
                    .withFractionalQuantity("OneQuarter")
                    .withUnitId(TestConstants.UNIT_ID_LB.toString());
            PostListItem listPostNoAmount = new PostListItem(tagId.toString(), anotherLargeAmount);
            String url = "/v2/shoppinglist/" + listId + "/item";
            given()
                    .header(TestUtils.authToken(jwtToken))
                    .contentType(ContentType.JSON)
                    .body(json(listPostNoAmount))
                    .when()
                    .post(url)
                    .then()
                    .statusCode(204);
            // retrieve list and verify
            ShoppingList listWithNewItem = retrieveList(jwtToken, listId);
            Map<String, ShoppingListItem> standardResultMap = listWithNewItem.getCategories().stream()
                    .flatMap(c -> c.getItems().stream())
                    .collect(Collectors.toMap(item -> item.getTag().getTagId(), Function.identity()));
            Assertions.assertNotNull(standardResultMap);
            Assertions.assertTrue(standardResultMap.containsKey(String.valueOf(tagId)));
            ShoppingListItem testElement = standardResultMap.get(String.valueOf(tagId));
            // the amount should be there now
            Assertions.assertEquals(22, testElement.getAmount().getRoundedQuantity(), 0.001);
            Assertions.assertEquals(21.75, testElement.getAmount().getQuantity(), 0.001);
            Assertions.assertEquals("22", testElement.getAmount().getQuantityDisplay());
            Assertions.assertEquals("22 lb", testElement.getAmount().getDisplay());
}

        private Long prepareListWithTagAndAmount(String token,  String tagId, Amount amount) throws Exception {
            Long newListId = createNewList(token);


            PostListItem listPost = new PostListItem(tagId, amount);
            String url = "/v2/shoppinglist/" + newListId + "/item";
            given()
                    .header(TestUtils.authToken(jwtToken))
                    .contentType(ContentType.JSON)
                    .body(json(listPost))
                    .when()
                    .post(url)
                    .then()
                    .statusCode(204);
            return newListId;
        }
        private Long createNewList(String token) throws Exception {
            ListGenerateProperties properties = new ListGenerateProperties();
            properties.setAddFromStarter(true);
            properties.setGenerateMealplan(false);

            String jsonProperties = json(properties);


            String location = given()
                    .header(TestUtils.authToken(token))
                    .contentType(ContentType.JSON)
                    .body(jsonProperties)
                    .when()
                    .post("/v2/shoppinglist")
                    .then()
                    .statusCode(201)
                    .extract()
                    .header("Location");

            Assertions.assertNotNull(location);
            String[] urlTokens = StringUtils.split(location, "/");
            return Long.valueOf(urlTokens[(urlTokens).length - 1]);

        }
    }


    @Nested
    class DeleteListTests {
        @Test
        void testDeleteList() throws Exception {
            // generic list properties
            ListGenerateProperties properties = new ListGenerateProperties();
            properties.setAddFromStarter(false);
            properties.setGenerateMealplan(false);
            String jsonProperties = json(properties);

            // create list to be added to  list to test
            String addedListId = createList(jsonProperties, meJwtToken);
            // create list for test
            String testListId = createList(jsonProperties, meJwtToken);

            String addedDishId1 = "104"; // schnitzel
            String addedDishId2 = "105"; // chicken flautas
            Long tagId = TestConstants.TAG_PASTA;
            String url = "/v2/shoppinglist/" + testListId + "/dish/" + addedDishId1;
            given()
                    .header(TestUtils.authToken(meJwtToken))
                    .contentType(ContentType.JSON)
                    .when()
                    .post(url)
                    .then()
                    .statusCode(204);
            PostListItem listPost = new PostListItem(tagId.toString(), null);
            url = "/v2/shoppinglist/" + testListId + "/item";
            given()
                    .header(TestUtils.authToken(meJwtToken))
                    .contentType(ContentType.JSON)
                    .body(json(listPost))
                    .when()
                    .post(url)
                    .then()
                    .statusCode(204);

            // addedListId - add dish2 , flour and test list
            url = "/v2/shoppinglist/" + addedListId + "/dish/" + addedDishId2;
            given()
                    .header(TestUtils.authToken(meJwtToken))
                    .contentType(ContentType.JSON)
                    .when()
                    .post(url)
                    .then()
                    .statusCode(204);

            url = "/v2/shoppinglist/" + addedListId + "/list/" + testListId;
            given()
                    .header(TestUtils.authToken(meJwtToken))
                    .contentType(ContentType.JSON)
                    .when()
                    .post(url)
                    .then()
                    .statusCode(204);

            tagId = TestConstants.TAG_FLOUR;
            url = "/v2/shoppinglist/" + addedListId + "/dish/" + addedDishId1;
            given()
                    .header(TestUtils.authToken(meJwtToken))
                    .contentType(ContentType.JSON)
                    .when()
                    .post(url)
                    .then()
                    .statusCode(204);

            // verify start state
            ShoppingList tested = retrieveList(meJwtToken, Long.valueOf(testListId));
            ShoppingList addedToList = retrieveList(meJwtToken, Long.valueOf(addedListId));

            List<Long> itemIdList = tested.getCategories().stream()
                    .flatMap(category -> category.getItems().stream())
                    .map(ShoppingListItem::getItemId)
                    .map(Long::valueOf)
                    .toList();
            long addedListItemCount = addedToList.getCategories().stream()
                    .flatMap(category -> category.getItems().stream())
                    .count();

            // delete the tested list
            given()
                    .header(TestUtils.authToken(meJwtToken))
                    .when()
                    .delete("/v2/shoppinglist/" + testListId)
                    .then()
                    .statusCode(204);

            // reretrieve the added list
            // number of items should be the same
            addedToList = retrieveList(meJwtToken, Long.valueOf(addedListId));
            long afterAddedListItemCount = addedToList.getCategories().stream()
                    .flatMap(category -> category.getItems().stream())
                    .count();
            Assertions.assertEquals(addedListItemCount, afterAddedListItemCount);
            String linkedToDeleted = addedToList.getCategories().stream()
                    .flatMap(category -> category.getItems().stream())
                    .flatMap(item -> item.getDetails().stream())
                    .map(ShoppingListItemDetails::getListId)
                    .filter( id -> testListId.equals(id))
                    .findFirst()
                    .orElse(null);
            Assertions.assertNull(linkedToDeleted);


            List<ListItemDetailEntity> deletedDetails = listItemDetailRepository.findDetailsByItemIds(itemIdList);
            Assertions.assertTrue(deletedDetails.isEmpty());


        }

        @Test
        void testDeleteList_LastList() {
            Long testId = 99999L;

            given()
                    .header(TestUtils.authToken(lastListJwtToken))
                    .when()
                    .delete("/v2/shoppinglist/" + testId)
                    .then()
                    .statusCode(400);

        }

    }

    @Nested
    class MergeTests {

        @Test
        void testMergeListSingleOperation() throws Exception {
            String testMergeList = StreamUtils.copyToString(resourceFile.getInputStream(), StandardCharsets.UTF_8);
            ObjectMapper objectMapper = new ObjectMapper();
            MergeRequest massage = objectMapper.readValue(testMergeList, MergeRequest.class);
            Date yesterday = DateUtils.asDate(LocalDate.now().minusDays(1));
            Map<String, MergeItem> dictionary = massage.getMergeItems().stream()
                    .collect(Collectors.toMap(MergeItem::getTagId, Function.identity()));
            // cross off  21, 81,  34, 212, 19
            List.of( "21", "81",  "34", "212", "19").forEach(id -> {
                dictionary.get(id).crossedOff(yesterday);
                dictionary.get(id).setLastChanged(yesterday);
            });
            // add (new) - 15, 32
            List.of("15", "32").forEach(id -> {
                dictionary.get(id).setItemId(null);
                dictionary.get(id).addedOn(yesterday);
                dictionary.get(id).setLastChanged(yesterday);
            });
            // removed
            dictionary.get("443").crossedOff(null);
            dictionary.get("443").removed(yesterday);
            dictionary.get("443").setLastChanged(yesterday);


            String payload = objectMapper.writeValueAsString(massage);
            Long listId = 110000L;

            ShoppingList before = retrieveList(meJwtToken, listId);
            List<ShoppingListItem> items = before.getCategories().stream().flatMap(c -> c.getItems().stream()).toList();

            String url = "/v2/shoppinglist/shared";
            given()
                    .header(TestUtils.authToken(meJwtToken))
                    .contentType(ContentType.JSON)
                    .body(payload)
                    .when()
                    .put(url)
                    .then()
                    .statusCode(200);

            // now, retrieve the list
            ShoppingList source = retrieveList(meJwtToken, listId);
            List<ShoppingListItem> afteritems = before.getCategories().stream().flatMap(c -> c.getItems().stream()).toList();
            Map<String, ShoppingListItem> sourceResultMap = source.getCategories().stream()
                    .flatMap(c -> c.getItems().stream())
                    .collect(Collectors.toMap(item -> item.getTag().getTagId(), Function.identity()));
            Assertions.assertNotNull(sourceResultMap);

            // check result
            // should have 16 items
            Assertions.assertEquals(16, sourceResultMap.keySet().size(), "should have 16 items");
            // should not contain tag 443 (which was removed)
            Assertions.assertFalse(sourceResultMap.containsKey("443"), "shouldn't contain tag 443");
            // not crossed off - 33, 16
            Map<String, ShoppingListItem> activeMap = source.getCategories().stream()
                    .flatMap(c -> c.getItems().stream())
                    .filter(i -> i.getCrossedOff() == null)
                    .collect(Collectors.toMap(item -> item.getTag().getTagId(), Function.identity()));
            System.out.println(activeMap.keySet());
            Assertions.assertEquals(10, activeMap.keySet().size(), "10 active items");
            Assertions.assertTrue(activeMap.containsKey("33"), "33 should be active");
            Assertions.assertTrue(activeMap.containsKey("16"), "16 should be active");

            //  crossed off - 21,81,34,212,19
            List.of("21", "81", "34", "212", "19").forEach(id -> {
                Assertions.assertNotNull(sourceResultMap.get(id), id + " should be present");
                Assertions.assertNotNull(sourceResultMap.get(id).getCrossedOff(), id + " should be crossedOff");
                Assertions.assertTrue(sourceResultMap.get(id).getLastChanged().after(yesterday), id + " should be crossedOff");
            });

            // added - 32, 15
            List.of("15","32").forEach(id -> {
                Assertions.assertNotNull(sourceResultMap.get(id), id + " should be present");
                if (id.equals("15")) {
                    // 15 is added and crossed off in one blow
                    Assertions.assertNotNull(sourceResultMap.get(id).getCrossedOff(), id + " should be crossedOff");
                } else {
                    Assertions.assertNull(sourceResultMap.get(id).getCrossedOff(), id + " should be crossedOff");
                }
                Assertions.assertTrue(sourceResultMap.get(id).getLastChanged().after(yesterday), id + " should be crossedOff");
            });
            //MM this test passing - each of the items only had one operation - added, crossed off, removed
            // next test - multiple operations (dates) - added and crossed off, added and removed, crossed off and removed
        }

        @Test
        void testMergeListDoubleOperation() throws Exception {
            // such as - added _and_ crossed off
            String testMergeList = StreamUtils.copyToString(resourceFile.getInputStream(), StandardCharsets.UTF_8);
            ObjectMapper objectMapper = new ObjectMapper();
            MergeRequest massage = objectMapper.readValue(testMergeList, MergeRequest.class);
            Date yesterday = DateUtils.asDate(LocalDate.now().minusDays(1));
            Map<String, MergeItem> dictionary = massage.getMergeItems().stream()
                    .collect(Collectors.toMap(MergeItem::getTagId, Function.identity()));
            // cross off  21, 81,  34, 212, 19
            List.of( "21", "81",  "34", "212", "19").forEach(id -> {
                dictionary.get(id).crossedOff(yesterday);
                dictionary.get(id).setLastChanged(yesterday);
            });
            // add (new) - 15, 32
            List.of("15", "32").forEach(id -> {
                dictionary.get(id).setItemId(null);
                dictionary.get(id).addedOn(yesterday);
                dictionary.get(id).setLastChanged(yesterday);
                dictionary.get(id).crossedOff(yesterday);
            });
            // removed
            dictionary.get("443").crossedOff(null);
            dictionary.get("443").removed(yesterday);
            dictionary.get("443").setLastChanged(yesterday);


            String payload = objectMapper.writeValueAsString(massage);
            Long listId = 110000L;

            ShoppingList before = retrieveList(meJwtToken, listId);
            List<ShoppingListItem> items = before.getCategories().stream().flatMap(c -> c.getItems().stream()).toList();

            String url = "/v2/shoppinglist/shared";
            given()
                    .header(TestUtils.authToken(meJwtToken))
                    .contentType(ContentType.JSON)
                    .body(payload)
                    .when()
                    .put(url)
                    .then()
                    .statusCode(200);

            // now, retrieve the list
            ShoppingList source = retrieveList(meJwtToken, listId);
            List<ShoppingListItem> afteritems = before.getCategories().stream().flatMap(c -> c.getItems().stream()).toList();
            Map<String, ShoppingListItem> sourceResultMap = source.getCategories().stream()
                    .flatMap(c -> c.getItems().stream())
                    .collect(Collectors.toMap(item -> item.getTag().getTagId(), Function.identity()));
            Assertions.assertNotNull(sourceResultMap);

            // added  and crossed off - 32, 15
            List.of("15","32").forEach(id -> {
                Assertions.assertNotNull(sourceResultMap.get(id), id + " should be present");
                Assertions.assertNotNull(sourceResultMap.get(id).getCrossedOff(), id + " should be crossedOff");
                Assertions.assertTrue(sourceResultMap.get(id).getLastChanged().after(yesterday), id + " should be crossedOff");
            });
            //MM this test passing - each of the items only had one operation - added, crossed off, removed
            // next test - multiple operations (dates) - added and crossed off, added and removed, crossed off and removed
        }


        @Test
        void testMergeList_SkipMerge() throws Exception {
            String testMergeList = StreamUtils.copyToString(resourceFileNoMerge.getInputStream(), StandardCharsets.UTF_8);

            Long listId = 110099L;

            String url = "/v2/shoppinglist/shared";
            given()
                    .header(TestUtils.authToken(meJwtToken))
                    .contentType(ContentType.JSON)
                    .body(testMergeList)
                    .when()
                    .put(url)
                    .then()
                    .statusCode(Matchers.is(Matchers.both(Matchers.greaterThanOrEqualTo(200)).and(Matchers.lessThan(300))));

            // now, retrieve the list
            ShoppingList source = retrieveList(meJwtToken, listId);
            Map<String, ShoppingListItem> crossedOffItems = source.getCategories().stream()
                    .flatMap(c -> c.getItems().stream())
                    .filter(c -> c.getCrossedOff() != null)
                    .collect(Collectors.toMap(item -> item.getTag().getTagId(), Function.identity()));
            Assertions.assertNotNull(crossedOffItems);

            // check result
            // the merged list had crossed off items. The db list didn't have any crossed off items
            // the merged list had an offline change date older than the last sync, so no merge should have been done
            // we cann check this by ensuring that no items are crossed off
            Assertions.assertEquals(0, crossedOffItems.keySet().size(), "should have 0 items");
        }

        @Test
        void testMergeList_Stale() throws Exception {
            String testMergeList = StreamUtils.copyToString(resourceFileStale.getInputStream(), StandardCharsets.UTF_8);

            Long listId = 11000001L;

            // get before counts
            ShoppingList beforeSource = retrieveList(meJwtToken, listId);
            Map<String, ShoppingListItem> beforeSourceResultMap = beforeSource.getCategories().stream()
                    .flatMap(c -> c.getItems().stream())
                    .collect(Collectors.toMap(item -> item.getTag().getTagId(), Function.identity()));
            Map<String, ShoppingListItem> beforeActiveMap = beforeSource.getCategories().stream()
                    .flatMap(c -> c.getItems().stream())
                    .filter(i -> i.getCrossedOff() == null)
                    .collect(Collectors.toMap(item -> item.getTag().getTagId(), Function.identity()));
            int beforeCount = beforeSourceResultMap.keySet().size();
            int beforeActiveCount = beforeActiveMap.keySet().size();



            String url = "/v2/shoppinglist/shared";
            given()
                    .header(TestUtils.authToken(meJwtToken))
                    .contentType(ContentType.JSON)
                    .body(testMergeList)
                    .when()
                    .put(url)
                    .then()
                    .statusCode(200);

            // now, retrieve the list
            ShoppingList source = retrieveList(meJwtToken, listId);
            Map<String, ShoppingListItem> sourceResultMap = source.getCategories().stream()
                    .flatMap(c -> c.getItems().stream())
                    .collect(Collectors.toMap(item -> item.getTag().getTagId(), Function.identity()));
            Assertions.assertNotNull(sourceResultMap);
            Map<String, ShoppingListItem> activeMap = source.getCategories().stream()
                    .flatMap(c -> c.getItems().stream())
                    .filter(i -> i.getCrossedOff() == null)
                    .collect(Collectors.toMap(item -> item.getTag().getTagId(), Function.identity()));

            // check result
            // should have 12 items
            Assertions.assertEquals(beforeCount, sourceResultMap.keySet().size(), "should have 13 items");
            Assertions.assertEquals(beforeActiveCount, activeMap.keySet().size(), "should have 13 items");
            for (String tagId : sourceResultMap.keySet()) {
                ShoppingListItem beforeItem = beforeSourceResultMap.get(tagId);
                ShoppingListItem afterItem = sourceResultMap.get(tagId);
                Assertions.assertNotNull(beforeItem, "tag " + tagId + " should exist");
                Assertions.assertEquals(beforeItem.getLastChanged(),
                        afterItem.getLastChanged(),"tag " + tagId + " should be active");
            }

        }

        @Test
        void testMergeList_Empty() throws Exception {
            // load statistics into file
            String testMergeList = StreamUtils.copyToString(resourceFileEmpty.getInputStream(), StandardCharsets.UTF_8);

            Long listId = 130000L;

            String url = "/v2/shoppinglist/shared";
            given()
                    .header(TestUtils.authToken(meJwtToken))
                    .contentType(ContentType.JSON)
                    .body(testMergeList)
                    .when()
                    .put(url)
                    .then()
                    .statusCode(200);

            // now, retrieve the list
            ShoppingList source = retrieveList(meJwtToken, listId);
            Map<String, ShoppingListItem> sourceResultMap = source.getCategories().stream()
                    .flatMap(c -> c.getItems().stream())
                    .collect(Collectors.toMap(item -> item.getTag().getTagId(), Function.identity()));
            Assertions.assertNotNull(sourceResultMap);

            // check result
            // should have 9 items
            Assertions.assertTrue(sourceResultMap.isEmpty(), "should be empty - empty list - empty merge request");
        }

    }
}
