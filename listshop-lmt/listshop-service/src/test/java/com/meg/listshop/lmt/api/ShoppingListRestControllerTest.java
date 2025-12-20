/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.lmt.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meg.listshop.Application;
import com.meg.listshop.auth.service.CustomUserDetails;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.data.entity.ListItemEntity;
import com.meg.listshop.lmt.data.repository.ItemRepository;
import com.meg.listshop.test.TestConstants;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.StreamUtils;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ExtendWith(MockitoExtension.class)
@Testcontainers
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
@Sql(value = {"/sql/com/meg/atable/lmt/api/ShoppingListRestControllerTest.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sql/com/meg/atable/lmt/api/ShoppingListRestControllerTest_rollback.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ShoppingListRestControllerTest {

    @Container
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    private static UserDetails userDetails;
    private static UserDetails meUserDetails;
    private static UserDetails lastListUserDetails;
    private static UserDetails noStarterUserDetails;
    private static UserDetails dadStarterUserDetails;
    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype());
    @Autowired
    ItemRepository itemRepository;
    @Value("classpath:/data/shoppingListRestControllerTest_mergeList.json")
    Resource resourceFile;
    @Value("classpath:/data/shoppingListRestControllerTest_noMergeList.json")
    Resource resourceFileNoMerge;
    @Value("classpath:/data/shoppingListRestControllerTest_mergeListStale.json")
    Resource resourceFileStale;
    @Value("classpath:/data/shoppingListRestControllerTest_mergeListEmpty.json")
    Resource resourceFileEmpty;
    @Value("classpath:/data/shoppingListRestControllerTest_mergeListWithConflicts.json")
    Resource mergeConflictFileSource;
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        this.mappingJackson2HttpMessageConverter = Arrays.stream(converters)

                .filter(MappingJackson2HttpMessageConverter.class::isInstance)
                .findAny()
                .orElse(null);

        Assertions.assertNotNull("the JSON message converter must not be null");
    }

    @BeforeEach
    @WithMockUser
    void setup() {

        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();


        userDetails = new CustomUserDetails(TestConstants.USER_1_ID,
                TestConstants.USER_1_EMAIL,
                null,
                null,
                null,
                true,
                null);

        meUserDetails = new CustomUserDetails(TestConstants.USER_3_ID,
                TestConstants.USER_3_NAME,
                null,
                null,
                null,
                true,
                null);

        lastListUserDetails = new CustomUserDetails(99999L,
                "username@testitytest.com",
                "username@testitytest.com",
                null,
                null,
                true,
                null);

        noStarterUserDetails = new CustomUserDetails(TestConstants.USER_4_ID,
                TestConstants.USER_4_NAME,
                null,
                null,
                null,
                true,
                null);

        dadStarterUserDetails = new CustomUserDetails(34L,
                "dad@userdetails.com",
                null,
                null,
                null,
                true,
                null);

    }


    @Test
    @WithMockUser
    void testRetrieveLists() throws Exception {

        mockMvc.perform(get("/shoppinglist")
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(contentType));


    }

    @Test
    @WithMockUser
    void testRetrieveMostRecentList() throws Exception {
        Long testId = 509990L;

        // updating list, so that it _is_ the most recent
        ShoppingListPut shoppingList = new ShoppingListPut(testId)
                .name("updated list most recent")
                .isStarterList(false);
        String payload = json(shoppingList);

        mockMvc.perform(put("/shoppinglist/" + testId)
                        .with(user(meUserDetails))
                        .content(payload).contentType(contentType))
                .andExpect(status().isOk())
                .andReturn();
        // now, testing the most recent call
        mockMvc.perform(get("/shoppinglist/mostrecent")
                        .with(user(meUserDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.shopping_list.list_id", Matchers.isA(Number.class)))
                .andReturn();

    }

    @Test
    @WithMockUser
    void testRetrieveStarterList() throws Exception {
        Long testId = 509991L;

        mockMvc.perform(get("/shoppinglist/starter")
                        .with(user(meUserDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.shopping_list.list_id", Matchers.isA(Number.class)))
                .andExpect(jsonPath("$.shopping_list.list_id").value(testId));
    }

    @Test
    @WithMockUser
    void testRetrieveStarterListNotFound() throws Exception {
        mockMvc.perform(get("/shoppinglist/starter")
                        .with(user(noStarterUserDetails)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testRetrieveListById() throws Exception {
        Long testId = 509990L;

        mockMvc.perform(get("/shoppinglist/" + testId)
                        .with(user(meUserDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.shopping_list.list_id", Matchers.isA(Number.class)))
                .andExpect(jsonPath("$.shopping_list.legend", Matchers.hasSize(6)))
                .andExpect(jsonPath("$.shopping_list.legend[*].key",
                        Matchers.containsInAnyOrder("d5099901", "d50999010", "d509990100", "d509990101", "l6666", "l7777")))
                .andReturn();
    }

    @Test
    void testRetrieveListById_NotFound() throws Exception {
        Long dummyTestId = 12345678901L;

        mockMvc.perform(get("/shoppinglist/" + dummyTestId)
                        .with(user(meUserDetails)))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @WithMockUser
    void testCustomLayout() throws Exception {
        Long customLayoutListId = 10101010L;
        Long standardLayoutListId = 90909090L;

        ShoppingList standardLayoutList = retrieveList(dadStarterUserDetails, standardLayoutListId);
        Map<String, ShoppingListItem> standardResultMap = standardLayoutList.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assertions.assertNotNull(standardResultMap);


        ShoppingList customLayoutList = retrieveList(meUserDetails, customLayoutListId);
        Map<String, ShoppingListItem> customResultMap = customLayoutList.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
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
        Assertions.assertEquals("tomatoes", tomatoes.getTagName(), "tomatoes are tomatoes");
    }

    @Test
    @WithMockUser
    void testUpdateList() throws Exception {
        Long testId = 509991L;

        ShoppingListPut shoppingList = new ShoppingListPut(testId)
                .name("updated list")
                .isStarterList(false);

        String payload = json(shoppingList);

        mockMvc.perform(put("/shoppinglist/" + testId)
                        .with(user(meUserDetails))
                        .content(payload).contentType(contentType))
                .andExpect(status().isOk())
                .andReturn();

    }

    @Test
    @WithMockUser
    void testUpdateList_starterListChange() throws Exception {
        Long testId = 509990L;
        Long oldStarterId = 509991L;

        ShoppingListPut shoppingList = new ShoppingListPut(testId)
                .name("now is starter list")
                .isStarterList(true);

        String payload = json(shoppingList);

        mockMvc.perform(put("/shoppinglist/" + testId)
                        .with(user(meUserDetails))
                        .content(payload).contentType(contentType))
                .andExpect(status().isOk());

        // now retrieve old starter list and ensure that isStarter is false
        mockMvc.perform(get("/shoppinglist/" + oldStarterId)
                        .with(user(meUserDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.shopping_list.list_id", Matchers.isA(Number.class)))
                .andExpect(jsonPath("$.shopping_list.list_id").value(oldStarterId))
                .andExpect(jsonPath("$.shopping_list.is_starter_list").value(false));


    }

    @Test
    @WithMockUser
    void testDeleteList() throws Exception {
        Long testId = TestConstants.LIST_2_ID;

        mockMvc.perform(delete("/shoppinglist/" + testId)
                        .with(user(meUserDetails)))
                .andExpect(status().isNoContent());

    }

    @Test
    @WithMockUser
    void testDeleteList_LastList() throws Exception {
        Long testId = 99999L;

        mockMvc.perform(delete("/shoppinglist/" + testId)
                        .with(user(lastListUserDetails)))
                .andExpect(status().isBadRequest());

    }

    @Test
    @WithMockUser
    void testDeleteItemFromList() throws Exception {
        Long listId = TestConstants.LIST_3_ID;
        String url = "/shoppinglist/" + listId + "/item/" + 501L;
        mockMvc.perform(delete(url)
                        .with(user(userDetails)))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void testGenerateFromMealPlan() throws Exception {


        Long mealPlanId = 65505L;

        String url = "/shoppinglist/mealplan/" + mealPlanId;
        MvcResult result = this.mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .contentType(contentType))
                .andExpect(status().isCreated())
                .andReturn();

        List<String> responses = result.getResponse().getHeaders("Location");
        Assertions.assertNotNull(responses);
        Assertions.assertTrue(responses.size() > 0);
        String[] urlTokens = StringUtils.split(responses.get(0), "/");
        Long newListId = Long.valueOf(urlTokens[(urlTokens).length - 1]);

        // now, retrieve the list
        ShoppingList source = retrieveList(userDetails, newListId);
        Map<String, ShoppingListItem> resultMap = source.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assertions.assertNotNull(resultMap);

        // check tag occurences in result

        // 502 - 3
        Assertions.assertNotNull(resultMap.get("1"));
        // 503 - 2
        Assertions.assertNotNull(resultMap.get("12"));
        Assertions.assertEquals(Optional.of(1).get(), resultMap.get("12").getUsedCount()); // showing 1 in result map
        // 436 - 1
        Assertions.assertNotNull(resultMap.get("436"));
        Assertions.assertEquals(Integer.valueOf(1), resultMap.get("436").getUsedCount());

    }

    @Test
    @WithMockUser
    void testCreateList() throws Exception {
        ListGenerateProperties properties = new ListGenerateProperties();
        properties.setAddFromStarter(true);
        properties.setGenerateMealplan(false);

        String jsonProperties = json(properties);

        String url = "/shoppinglist";

        this.mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .contentType(contentType)
                        .content(jsonProperties))
                .andExpect(status().isCreated());

    }

    @Test
    @WithMockUser
    void testSetCrossedOffForItem() throws Exception {
        Long listId = 6666L;
        String url = "/shoppinglist/" + listId + "/item/shop/" + 60660L
                + "?crossOff=true";
        mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .contentType(contentType))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void testCrossOffAllItemsOnList() throws Exception {

        Long listId = TestConstants.LIST_1_ID;
        String url = "/shoppinglist/" + listId + "/item/shop"
                + "?crossOff=true";
        mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .contentType(contentType))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void testAddListToList() throws Exception {
        Long listId = TestConstants.LIST_3_ID;
        Long fromListId = TestConstants.LIST_1_ID;

        String url = "/shoppinglist/" + listId + "/list/" + fromListId;
        mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .contentType(contentType))
                .andExpect(status().isNoContent())
                .andReturn();

        // retrieve list and verify
        // now retrieve old starter list and ensure that isStarter is false
        mockMvc.perform(get("/shoppinglist/" + listId)
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.shopping_list.list_id", Matchers.isA(Number.class)))
                .andExpect(jsonPath("$.shopping_list.list_id").value(listId))
                .andReturn();
    }

    @Test
    @WithMockUser
    void testAddTagToList() throws Exception {
        Long tagId = TestConstants.TAG_PASTA;
        ListGenerateProperties properties = new ListGenerateProperties();
        properties.setAddFromStarter(true);
        properties.setGenerateMealplan(false);

        String jsonProperties = json(properties);


        MvcResult createResult = this.mockMvc.perform(post("/shoppinglist")
                        .with(user(userDetails))
                        .contentType(contentType)
                        .content(jsonProperties))
                .andExpect(status().isCreated())
                .andReturn();

        List<String> responses = createResult.getResponse().getHeaders("Location");
        Assertions.assertNotNull(responses);
        Assertions.assertTrue(responses.size() > 0);
        String[] urlTokens = StringUtils.split(responses.get(0), "/");
        Long listId = Long.valueOf(urlTokens[(urlTokens).length - 1]);


        String url = "/shoppinglist/" + listId + "/tag/" + tagId;
        mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .contentType(contentType))
                .andExpect(status().isNoContent())
                .andReturn();

        // retrieve list and verify
        ShoppingList listWithNewItem = retrieveList(userDetails, listId);
        Map<String, ShoppingListItem> standardResultMap = listWithNewItem.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assertions.assertNotNull(standardResultMap);
        Assertions.assertTrue(standardResultMap.containsKey(String.valueOf(tagId)));
    }

    @Test
    @WithMockUser
    void testRemoveListFromList() throws Exception {
        Long listId = 609990L;
        Long fromListId = 609991L;

        String url = "/shoppinglist/" + listId + "/list/" + fromListId;
        mockMvc.perform(delete(url)
                        .with(user(userDetails))
                        .contentType(contentType))
                .andExpect(status().isNoContent())
                .andReturn();

        // retrieve list and verify
        MvcResult result = mockMvc.perform(get("/shoppinglist/" + listId)
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.shopping_list.list_id", Matchers.isA(Number.class)))
                .andExpect(jsonPath("$.shopping_list.list_id").value(listId))
                .andReturn();


        String listAfterDelete = result.getResponse().getContentAsString();
        // contains tag ids 500, 503
        Assertions.assertTrue(listAfterDelete.contains("\"tag_id\":\"503\","));
        Assertions.assertTrue(listAfterDelete.contains("\"tag_id\":\"500\","));

        // doesn't contain tagIds 501, 502 504
        Assertions.assertFalse(listAfterDelete.contains("\"tag_id\":\"501\","));
        Assertions.assertFalse(listAfterDelete.contains("\"tag_id\":\"502\","));
        Assertions.assertFalse(listAfterDelete.contains("\"tag_id\":\"504\","));
    }

    @Test
    @WithMockUser
    void testAddDishToList() throws Exception {
        Long listId = TestConstants.LIST_1_ID;
        Long tagId1 = 500L;
        Long tagId2 = 501L;
        String url = "/shoppinglist/" + listId + "/dish/" + TestConstants.DISH_7_ID;
        mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .contentType(contentType))
                .andExpect(status().isNoContent());

        ShoppingList result = retrieveList(userDetails, listId);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getCategories().size() > 0);

        Map<String, ShoppingListItem> standardResultMap = result.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .filter(i -> i.getSourceKeys() != null &&
                        i.getSourceKeys().contains("d" + TestConstants.DISH_7_ID))
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assertions.assertNotNull(standardResultMap);
        Assertions.assertEquals(2, standardResultMap.keySet().size());
        Assertions.assertTrue(standardResultMap.containsKey(String.valueOf(tagId1)));
        Assertions.assertTrue(standardResultMap.containsKey(String.valueOf(tagId2)));

    }


    @Test
    @WithMockUser
    void testAddDishesToList() throws Exception {
        Long broccoliId = 21L;
        Long fetaId = 37L;
        ListGenerateProperties properties = new ListGenerateProperties();
        properties.setAddFromStarter(true);
        properties.setGenerateMealplan(false);
        String jsonProperties = json(properties);
        String listId = createList(jsonProperties, meUserDetails);

        ListAddProperties addProperties = new ListAddProperties();

        Long[] dishSourceArray = {TestConstants.DISH_8_ID, TestConstants.DISH_2_ID, TestConstants.DISH_1_ID};
        List<String> dishIdsAsStrings = Arrays.stream(dishSourceArray)
                .map(String::valueOf)
                .toList();
        addProperties.setDishSources(dishIdsAsStrings);
        String addDishProperties = json(addProperties);

        String url = "/shoppinglist/" + listId + "/dish";
        mockMvc.perform(post(url)
                        .with(user(meUserDetails))
                        .contentType(contentType)
                        .content(addDishProperties))
                .andExpect(status().is2xxSuccessful());

        ShoppingList result = retrieveList(meUserDetails, Long.valueOf(listId));
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getCategories().size() > 0);

        Map<String, ShoppingListItem> standardResultMap = result.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assertions.assertNotNull(standardResultMap);
        // test brocolli is there, twice, with sources 109, and 45 (dish)
        ShoppingListItem broccoliItem = standardResultMap.get(String.valueOf(broccoliId));
        Assertions.assertNotNull(broccoliItem, "broccoli item should be present");
        Assertions.assertEquals(2, broccoliItem.getSourceKeys().size());
        Assertions.assertTrue(broccoliItem.getSourceKeys().contains("d" + TestConstants.DISH_8_ID));
        Assertions.assertTrue(broccoliItem.getSourceKeys().contains("d" + TestConstants.DISH_2_ID));
        // test feta cheese is there, from dish 1, tag_id 37
        ShoppingListItem fetaItem = standardResultMap.get(String.valueOf(fetaId));
        Assertions.assertNotNull(fetaItem, "feta item should be present");
        Assertions.assertEquals(1, fetaItem.getSourceKeys().size());
        Assertions.assertTrue(fetaItem.getSourceKeys().contains("d" + TestConstants.DISH_1_ID));
    }

    private String createList(String jsonProperties, UserDetails userDetails) throws Exception {
        String url = "/shoppinglist";
        MvcResult result = this.mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .contentType(contentType)
                        .content(jsonProperties))
                .andExpect(status().isCreated())
                .andReturn();
        List<String> locations = result.getResponse().getHeaders("Location");
        String[] splitLocation = locations.get(0).split("/");
        return splitLocation[splitLocation.length - 1];
    }

    @Test
    @WithMockUser
    void testAddDishToNewList() throws Exception {
        ListGenerateProperties properties = new ListGenerateProperties();
        properties.setAddFromStarter(true);
        properties.setGenerateMealplan(false);
        String jsonProperties = json(properties);
        MvcResult newResult = this.mockMvc.perform(post("/shoppinglist")
                        .with(user(userDetails))
                        .contentType(contentType)
                        .content(jsonProperties))
                .andReturn();
        List<String> headers = newResult.getResponse().getHeaders("Location");
        String listIdString = headers.get(0).replace("http://localhost/tag/", "");
        Long newListId = Long.valueOf(listIdString);

        Long tagId1 = 500L;
        Long tagId2 = 501L;
        String url = "/shoppinglist/" + newListId + "/dish/" + TestConstants.DISH_7_ID;
        mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .contentType(contentType))
                .andExpect(status().isNoContent());

        ShoppingList result = retrieveList(userDetails, newListId);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getCategories().size() > 0);

        Map<String, ShoppingListItem> standardResultMap = result.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .filter(i -> i.getSourceKeys() != null &&
                        i.getSourceKeys().contains("d" + TestConstants.DISH_7_ID))
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assertions.assertNotNull(standardResultMap);
        Assertions.assertEquals(2, standardResultMap.keySet().size());
        Assertions.assertTrue(standardResultMap.containsKey(String.valueOf(tagId1)));
        Assertions.assertTrue(standardResultMap.containsKey(String.valueOf(tagId2)));

    }

    @Test
    @WithMockUser
    void testUpdateItemUsedCount() throws Exception {

        Long listId = 7777L;
        Long tagId = 500L;
        Integer usedCount = 6;
        String url = "/shoppinglist/" + listId + "/tag/" + tagId + "/count/" + usedCount;
        mockMvc.perform(put(url)
                        .with(user(meUserDetails))
                        .contentType(contentType))
                .andExpect(status().isNoContent());

        // make sure the item has been updated
        ListItemEntity resultItem = itemRepository.getItemByListAndTag(listId, tagId);

        Assertions.assertEquals(usedCount, resultItem.getUsedCount());
    }

    @Test
    @WithMockUser
    void testAddMealPlanToList() throws Exception {

        Long listId = 51000L;
        Long mealPlanId = 65505L;

        String url = "/shoppinglist/" + listId + "/mealplan/" + mealPlanId;
        mockMvc.perform(put(url)
                        .with(user(userDetails))
                        .contentType(contentType))
                .andExpect(status().isNoContent());

        // now, retrieve the list
        ShoppingList source = retrieveList(userDetails, listId);
        Map<String, ShoppingListItem> sourceResultMap = source.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));

        Assertions.assertNotNull(sourceResultMap);
        // check tag occurences in result
        // 501 - 1
        Assertions.assertNotNull(sourceResultMap.get("81"));
        Assertions.assertEquals(Optional.of(1).get(), sourceResultMap.get("81").getUsedCount());

        // 502 - 3
        Assertions.assertNotNull(sourceResultMap.get("1"));
        Assertions.assertEquals(Integer.valueOf(3), sourceResultMap.get("1").getUsedCount());  // showing 1 in result map
        // 503 - 2
        Assertions.assertNotNull(sourceResultMap.get("12"));
        Assertions.assertEquals(Integer.valueOf(2), sourceResultMap.get("12").getUsedCount()); // showing 1 in result map
        // 436 - 1
        Assertions.assertNotNull(sourceResultMap.get("436"));
        Assertions.assertEquals(Integer.valueOf(1), sourceResultMap.get("436").getUsedCount());

    }

    @Test
    @WithMockUser
    void testMergeList() throws Exception {
        String testMergeList = StreamUtils.copyToString(resourceFile.getInputStream(), StandardCharsets.UTF_8);

        Long listId = 110000L;

        String url = "/shoppinglist/shared";
        mockMvc.perform(put(url)
                        .with(user(meUserDetails))
                        .contentType(contentType)
                        .content(testMergeList))
                .andExpect(status().isOk());

        // now, retrieve the list
        ShoppingList source = retrieveList(meUserDetails, listId);
        Map<String, ShoppingListItem> sourceResultMap = source.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assertions.assertNotNull(sourceResultMap);

        // check result
        // should have 9 items
        Assertions.assertEquals(15, sourceResultMap.keySet().size(), "should have 15 items");
        // should not contain tag 32 (which was removed)
        Assertions.assertFalse(sourceResultMap.containsKey("32"), "shouldn't contain tag 32");
        // not crossed off - 33, 16
        Map<String, ShoppingListItem> activeMap = source.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .filter(i -> i.getCrossedOff() == null)
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assertions.assertEquals(10, activeMap.keySet().size(), "10 active items");
        Assertions.assertTrue(activeMap.containsKey("33"), "33 should be actice");
        Assertions.assertTrue(activeMap.containsKey("16"), "16 should be actice");

        //  crossed off - 19, 34
        Map<String, ShoppingListItem> crossedOffMap = source.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .filter(i -> i.getCrossedOff() != null)
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assertions.assertEquals(5, crossedOffMap.keySet().size(), "5 crossed off items");
        Assertions.assertTrue(crossedOffMap.containsKey("19"), "33 should be crossed off");
        Assertions.assertTrue(crossedOffMap.containsKey("34"), "16 should be crossed off");
    }

    @Test
    @WithMockUser
    void testMergeList_SkipMerge() throws Exception {
        String testMergeList = StreamUtils.copyToString(resourceFileNoMerge.getInputStream(), StandardCharsets.UTF_8);

        Long listId = 110099L;

        String url = "/shoppinglist/shared";
        mockMvc.perform(put(url)
                        .with(user(meUserDetails))
                        .contentType(contentType)
                        .content(testMergeList))
                .andReturn();

        // now, retrieve the list
        ShoppingList source = retrieveList(meUserDetails, listId);
        Map<String, ShoppingListItem> crossedOffItems = source.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .filter(c -> c.getCrossedOff() != null)
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assertions.assertNotNull(crossedOffItems);

        // check result
        // the merged list had crossed off items. The db list didn't have any crossed off items
        // the merged list had an offline change date older than the last sync, so no merge should have been done
        // we cann check this by ensuring that no items are crossed off
        Assertions.assertEquals(0, crossedOffItems.keySet().size(), "should have 0 items");
    }

    @Test
    @WithMockUser
    void testMergeList_Stale() throws Exception {
        String testMergeList = StreamUtils.copyToString(resourceFileStale.getInputStream(), StandardCharsets.UTF_8);

        Long listId = 11000001L;

        String url = "/shoppinglist/shared";
        mockMvc.perform(put(url)
                        .with(user(meUserDetails))
                        .contentType(contentType)
                        .content(testMergeList))
                .andExpect(status().isOk());

        // now, retrieve the list
        ShoppingList source = retrieveList(meUserDetails, listId);
        Map<String, ShoppingListItem> sourceResultMap = source.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assertions.assertNotNull(sourceResultMap);

        // check result
        // should have 12 items
        Assertions.assertEquals(13, sourceResultMap.keySet().size(), "should have 13 items");
        // should not contain tag 32 (which was removed)
        Assertions.assertFalse(sourceResultMap.containsKey("32"), "shouldn't contain tag 32");
        // not crossed off - 16
        Map<String, ShoppingListItem> activeMap = source.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .filter(i -> i.getCrossedOff() == null)
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assertions.assertEquals(9, activeMap.keySet().size(), "9 active items");
        Assertions.assertTrue(activeMap.containsKey("16"), "16 should be actice");

        //  crossed off - 19
        Map<String, ShoppingListItem> crossedOffMap = source.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .filter(i -> i.getCrossedOff() != null)
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assertions.assertEquals(4, crossedOffMap.keySet().size(), "4 crossed off items");
        Assertions.assertTrue(crossedOffMap.containsKey("19"), "19 should be crossed off");
    }

    @Test
    @WithMockUser
    void testMergeList_Empty() throws Exception {
        // load statistics into file
        String testMergeList = StreamUtils.copyToString(resourceFileEmpty.getInputStream(), StandardCharsets.UTF_8);

        Long listId = 130000L;

        String url = "/shoppinglist/shared";
        mockMvc.perform(put(url)
                        .with(user(meUserDetails))
                        .contentType(contentType)
                        .content(testMergeList))
                .andExpect(status().isOk());

        // now, retrieve the list
        ShoppingList source = retrieveList(meUserDetails, listId);
        Map<String, ShoppingListItem> sourceResultMap = source.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assertions.assertNotNull(sourceResultMap);

        // check result
        // should have 9 items
        Assertions.assertTrue(sourceResultMap.isEmpty(), "should be empty - empty list - empty merge request");
    }

    @Test
    @WithMockUser
    void testMergeList_TagConflict() throws Exception {
        String testMergeList = StreamUtils.copyToString(mergeConflictFileSource.getInputStream(), StandardCharsets.UTF_8);

        Long listId = 120000L;

        String url = "/shoppinglist/shared";
        mockMvc.perform(put(url)
                        .with(user(meUserDetails))
                        .contentType(contentType)
                        .content(testMergeList))
                .andExpect(status().isOk());

        // now, retrieve the list
        ShoppingList source = retrieveList(meUserDetails, listId);
        Map<String, ShoppingListItem> sourceResultMap = source.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assertions.assertNotNull(sourceResultMap);

        // check result
        // should have 9 items
        Assertions.assertEquals(3, sourceResultMap.keySet().size(), "should have 9 items");
        // should not contain tag 12001 or 12002 (standard)
        Assertions.assertFalse(sourceResultMap.containsKey("12001"), "shouldn't contain tag 12001");
        Assertions.assertFalse(sourceResultMap.containsKey("12002"), "shouldn't contain tag 12002");
        // should  contain tag 13001 or 13002 (standard)
        Assertions.assertTrue(sourceResultMap.containsKey("13001"), "shouldn't contain tag 13001");
        Assertions.assertTrue(sourceResultMap.containsKey("13002"), "shouldn't contain tag 13002");
        // should contain 21 - no conflict
        Assertions.assertTrue(sourceResultMap.containsKey("21"), "shouldn't contain tag 21");

    }

    @Test
    @WithMockUser
    void testRemoveDishFromList() throws Exception {
        Long listId = TestConstants.LIST_1_ID;
        String dish1Id = "66500";
        String dish2Id = "66501";
        String targetTagId = "1";

        // add dish 66500 - contains tag id 1
        String url = "/shoppinglist/" + listId + "/dish/" + dish1Id;
        mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .contentType(contentType))
                .andExpect(status().isNoContent());

        // add dish 66501, contains tag id 1
        url = "/shoppinglist/" + listId + "/dish/" + dish2Id;
        mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .contentType(contentType))
                .andExpect(status().isNoContent());

        // affirm list has tag_id 1 2 times (tag_id 1 is green chili)
        ShoppingList result = retrieveList(userDetails, listId);
        // affirm we have tag id 1 there - in Category Dry, with id 1
        ShoppingListItem resultItem = result.getCategories().stream().flatMap(c -> c.getItems().stream())
                .filter(item -> item.getTag().getId().equals(targetTagId))
                .findFirst().orElse(null);
        Assertions.assertNotNull(resultItem);
        Assertions.assertTrue(resultItem.getSourceKeys().contains("d" + dish1Id));
        Assertions.assertTrue(resultItem.getSourceKeys().contains("d" + dish2Id));
        Assertions.assertEquals(Integer.valueOf(2), resultItem.getUsedCount());


        // the remove test
        // remove dish 66500 (dish1Id)
        url = "/shoppinglist/" + listId + "/dish/" + dish1Id;
        mockMvc.perform(delete(url)
                        .with(user(userDetails))
                        .contentType(contentType))
                .andExpect(status().isNoContent());

        // assert list has tag_id 1 1 time
        result = retrieveList(userDetails, listId);
        // affirm we have tag id 1 there - in Category Dry, with id 1
        resultItem = result.getCategories().stream().flatMap(c -> c.getItems().stream())
                .filter(item -> item.getTag().getId().equals(targetTagId))
                .findFirst().orElse(null);
        Assertions.assertNotNull(resultItem);
        Assertions.assertFalse(resultItem.getSourceKeys().contains("d" + dish1Id));
        Assertions.assertTrue(resultItem.getSourceKeys().contains("d" + dish2Id));
        Assertions.assertEquals(Integer.valueOf(1), resultItem.getUsedCount());
    }

    @Test
    @WithMockUser
    void testChangeListLayout() throws Exception {

        Long listId = TestConstants.LIST_1_ID;
        String url = "/shoppinglist/" + listId + "/layout/" + TestConstants.LIST_LAYOUT_2_ID;
        mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .contentType(contentType))
                .andExpect(status().isNoContent());

    }

    @Test
    @WithMockUser
    void deleteAllItemsFromList() throws Exception {

        ListGenerateProperties properties = new ListGenerateProperties();
        properties.setAddFromStarter(true);
        properties.setGenerateMealplan(false);

        String jsonProperties = json(properties);

        String url = "/shoppinglist";

        MvcResult result = this.mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .contentType(contentType)
                        .content(jsonProperties))
                .andExpect(status().isCreated())
                .andReturn();

        List<String> responses = result.getResponse().getHeaders("Location");
        Assertions.assertNotNull(responses);
        Assertions.assertTrue(responses.size() > 0);
        String[] urlTokens = StringUtils.split(responses.get(0), "/");
        Long listId = Long.valueOf(urlTokens[(urlTokens).length - 1]);

        String clearUrl = "/shoppinglist/" + listId + "/item";
        mockMvc.perform(delete(clearUrl)
                        .with(user(userDetails))
                        .contentType(contentType))
                .andExpect(status().isNoContent());

        // now, retrieve the list
        ShoppingList source = retrieveList(userDetails, listId);
        Map<String, ShoppingListItem> resultMap = source.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assertions.assertNotNull(resultMap);
        Assertions.assertTrue(resultMap.isEmpty());
    }

    @Test
    @WithMockUser
    void deleteItemOperation_Move() throws Exception {
        Long sourceListId = 7777L;  // 500 (CrossedOff), 501, 502, 505 (CrossedOff)
        Long destinationListId = 6666L;  // 501, 502, 503, 505
        List<Long> tagIdsForUpdate = Arrays.asList(500L, 502L);

        ItemOperationPut operationUpdate = new ItemOperationPut();
        operationUpdate.setDestinationListId(destinationListId);
        operationUpdate.setOperation(ItemOperationType.Move.name());
        operationUpdate.setTagIds(tagIdsForUpdate);

        String jsonProperties = json(operationUpdate);

        String url = "/shoppinglist/" + sourceListId + "/item";

        this.mockMvc.perform(put(url)
                        .with(user(meUserDetails))
                        .contentType(contentType)
                        .content(jsonProperties))
                .andExpect(status().isOk())
                .andReturn();

        // now, retrieve the list
        ShoppingList source = retrieveList(meUserDetails, sourceListId);
        Map<String, ShoppingListItem> sourceResultMap = source.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));

        Assertions.assertNotNull(sourceResultMap);
        Assertions.assertEquals(2, sourceResultMap.keySet().size());
        // 500 shouldn't be there
        Assertions.assertFalse(sourceResultMap.containsKey("500"));
        // check destination list
        ShoppingList destination = retrieveList(meUserDetails, destinationListId);
        Map<String, ShoppingListItem> destinationResultMap = destination.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assertions.assertNotNull(destinationResultMap);
        Assertions.assertEquals(5, destinationResultMap.keySet().size());
        // 500 should be there with count 1
        Assertions.assertTrue(destinationResultMap.containsKey("500"));
        ShoppingListItem testElement = destinationResultMap.get("500");
        Assertions.assertEquals(Long.valueOf(1), Long.valueOf(testElement.getUsedCount()));
        // 502 should be there with a count of 2
        testElement = destinationResultMap.get("502");
        Assertions.assertEquals(Long.valueOf(2), Long.valueOf(testElement.getUsedCount()));

    }

    @Test
    @WithMockUser
    void deleteItemOperation_Copy() throws Exception {
        Long sourceListId = 7777L;  // 500 (CrossedOff), 501, 502, 505 (CrossedOff)
        Long destinationListId = 6666L;  // 501, 502, 503, 505
        List<Long> tagIdsForUpdate = Arrays.asList(500L, 501L, 504L);

        ItemOperationPut operationUpdate = new ItemOperationPut();
        operationUpdate.setDestinationListId(destinationListId);
        operationUpdate.setOperation(ItemOperationType.Copy.name());
        operationUpdate.setTagIds(tagIdsForUpdate);

        String jsonProperties = json(operationUpdate);

        String url = "/shoppinglist/" + sourceListId + "/item";

        this.mockMvc.perform(put(url)
                        .with(user(meUserDetails))
                        .contentType(contentType)
                        .content(jsonProperties))
                .andExpect(status().isOk())
                .andReturn();

        // now, retrieve the list
        ShoppingList source = retrieveList(meUserDetails, sourceListId);
        Map<String, ShoppingListItem> sourceResultMap = source.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));

        Assertions.assertEquals(4, sourceResultMap.keySet().size());
        // check destination list
        ShoppingList destination = retrieveList(meUserDetails, destinationListId);
        Map<String, ShoppingListItem> destinationResultMap = destination.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));

        Assertions.assertNotNull(destinationResultMap);
        Assertions.assertEquals(5, destinationResultMap.keySet().size());
        // 500 should be there with count 1
        Assertions.assertTrue(destinationResultMap.containsKey("500"));
        ShoppingListItem testElement = destinationResultMap.get("500");
        Assertions.assertEquals(Long.valueOf(1), Long.valueOf(testElement.getUsedCount()));
        // 501 should be there with a count of 2
        testElement = destinationResultMap.get("501");
        Assertions.assertEquals(Long.valueOf(2), Long.valueOf(testElement.getUsedCount()));

    }

    @Test
    @WithMockUser
    void deleteItemOperation_MoveCrossedOff_New() throws Exception {
        Long sourceListId = 7777L;  // 500 (CrossedOff), 501, 502, 505 (CrossedOff)
        Long destinationListId = 6666L;  // 501, 502, 503, 505, 505
        List<Long> tagIdsForUpdate = Arrays.asList(500L, 502L);

        ItemOperationPut operationUpdate = new ItemOperationPut();
        operationUpdate.setDestinationListId(destinationListId);
        operationUpdate.setOperation(ItemOperationType.Move.name());
        operationUpdate.setTagIds(tagIdsForUpdate);

        String jsonProperties = json(operationUpdate);

        String url = "/shoppinglist/" + sourceListId + "/item";

        this.mockMvc.perform(put(url)
                        .with(user(meUserDetails))
                        .contentType(contentType)
                        .content(jsonProperties))
                .andExpect(status().isOk())
                .andReturn();

        // now, retrieve the list
        ShoppingList source = retrieveList(meUserDetails, sourceListId);
        Map<String, ShoppingListItem> sourceResultMap = source.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));

        Assertions.assertNotNull(sourceResultMap);
        Assertions.assertEquals(2, sourceResultMap.keySet().size());
        // 500 shouldn't be there
        Assertions.assertFalse(sourceResultMap.containsKey(500L));
        // check destination list
        ShoppingList destination = retrieveList(meUserDetails, destinationListId);
        Map<String, ShoppingListItem> destinationResultMap = destination.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));

        Assertions.assertNotNull(destinationResultMap);
        Assertions.assertEquals(5, destinationResultMap.keySet().size());
        // 500 should be there with count 1, crossedOff
        Assertions.assertTrue(destinationResultMap.containsKey("500"));
        ShoppingListItem testElement = destinationResultMap.get("500");
        Assertions.assertEquals(Long.valueOf(1), Long.valueOf(testElement.getUsedCount()));
        Assertions.assertNull(testElement.getCrossedOff());  // when item is moved, it loses it's crossed off status
        // 502 should be there with a count of 2
        testElement = destinationResultMap.get("502");
        Assertions.assertEquals(Long.valueOf(2), Long.valueOf(testElement.getUsedCount()));
    }

    @Test
    @WithMockUser
    void deleteItemOperation_MoveCrossedOff_Existing() throws Exception {
        Long sourceListId = 7777L;  // 500 (CrossedOff), 501, 502, 505 (CrossedOff)
        Long destinationListId = 6666L;  // 501, 502, 503, 505, 505
        List<Long> tagIdsForUpdate = Arrays.asList(505L, 502L);

        ItemOperationPut operationUpdate = new ItemOperationPut();
        operationUpdate.setDestinationListId(destinationListId);
        operationUpdate.setOperation(ItemOperationType.Move.name());
        operationUpdate.setTagIds(tagIdsForUpdate);

        String jsonProperties = json(operationUpdate);

        String url = "/shoppinglist/" + sourceListId + "/item";

        this.mockMvc.perform(put(url)
                        .with(user(meUserDetails))
                        .contentType(contentType)
                        .content(jsonProperties))
                .andExpect(status().isOk());

        // now, retrieve the list and check results
        MvcResult listResultsAfter = this.mockMvc.perform(get("/shoppinglist/" + sourceListId)
                        .with(user(meUserDetails)))
                .andReturn();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonList = listResultsAfter.getResponse().getContentAsString();
        ShoppingListResource afterList = objectMapper.readValue(jsonList, ShoppingListResource.class);
        Assertions.assertNotNull(afterList);
        ShoppingList list = afterList.getShoppingList();

        Map<String, ShoppingListItem> allSourceResultMap = list.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));

        Assertions.assertNotNull(allSourceResultMap);
        Assertions.assertEquals(2, allSourceResultMap.keySet().size());
        // 505 shouldn't be there
        Assertions.assertFalse(allSourceResultMap.containsKey("505"));

        // check destination list
        ShoppingList destination = retrieveList(meUserDetails, destinationListId);
        Map<String, ShoppingListItem> destinationResultMap = destination.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));

        Assertions.assertNotNull(destinationResultMap);
        Assertions.assertEquals(4, destinationResultMap.keySet().size());
        // 505 should be there with count 2, not crossedOff
        Assertions.assertTrue(destinationResultMap.containsKey("505"));
        ShoppingListItem testElement = destinationResultMap.get("505");
        Assertions.assertEquals(Long.valueOf(2), Long.valueOf(testElement.getUsedCount()));
        Assertions.assertNull(testElement.getCrossedOff());
        // 502 should be there with a count of 2
        testElement = destinationResultMap.get("502");
        Assertions.assertEquals(Long.valueOf(2), Long.valueOf(testElement.getUsedCount()));
    }


    @Test
    @WithMockUser
    void deleteItemOperation_Remove() throws Exception {
        Long sourceListId = 7777L;  // 500 (CrossedOff), 501, 502, 505 (CrossedOff)
        List<Long> tagIdsForUpdate = Arrays.asList(500L, 501L, 504L);

        ItemOperationPut operationUpdate = new ItemOperationPut();
        operationUpdate.setDestinationListId(null);
        operationUpdate.setOperation(ItemOperationType.Remove.name());
        operationUpdate.setTagIds(tagIdsForUpdate);

        String jsonProperties = json(operationUpdate);

        String url = "/shoppinglist/" + sourceListId + "/item";

        this.mockMvc.perform(put(url)
                        .with(user(meUserDetails))
                        .contentType(contentType)
                        .content(jsonProperties))
                .andExpect(status().isOk())
                .andReturn();

        // now, retrieve the list
        ShoppingList source = retrieveList(meUserDetails, sourceListId);
        Map<String, ShoppingListItem> sourceResultMap = source.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));

        Assertions.assertNotNull(sourceResultMap);
        Assertions.assertEquals(2, sourceResultMap.keySet().size());
        // 500 shouldn't be there
        Assertions.assertFalse(sourceResultMap.containsKey(500L));

    }

    @Test
    @WithMockUser
    void deleteItemOperation_RemoveCrossedOff() throws Exception {
        Long sourceListId = 77777L;  // 500, 501, 502

        ItemOperationPut operationUpdate = new ItemOperationPut();
        operationUpdate.setDestinationListId(null);
        operationUpdate.setOperation(ItemOperationType.RemoveCrossedOff.name());
        operationUpdate.setTagIds(new ArrayList<>());

        String jsonProperties = json(operationUpdate);

        // get crossed off ids before call
        ShoppingList before = retrieveList(meUserDetails, sourceListId);
        List<String> crossedOffIds = before.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .filter(i -> i.getRemoved() != null)
                .map(i -> i.getTag().getId())
                .toList();

        String url = "/shoppinglist/" + sourceListId + "/item";

        this.mockMvc.perform(put(url)
                        .with(user(meUserDetails))
                        .contentType(contentType)
                        .content(jsonProperties))
                .andExpect(status().isOk())
                .andReturn();

        // now, retrieve the list
        ShoppingList source = retrieveList(meUserDetails, sourceListId);
        Map<String, ShoppingListItem> sourceResultMap = source.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));


        Assertions.assertNotNull(sourceResultMap);
        // check that none of the crossed off ids are in the map
        for (String crossedOffId : crossedOffIds) {
            Assertions.assertFalse(sourceResultMap.containsKey(crossedOffId), "crossed off ids shouldn't be in the list");
        }

    }

    @Test
    @WithMockUser
    void deleteItemOperation_RemoveAll() throws Exception {
        Long sourceListId = 77777L;  // 500, 501, 502

        ItemOperationPut operationUpdate = new ItemOperationPut();
        operationUpdate.setDestinationListId(null);
        operationUpdate.setOperation(ItemOperationType.RemoveAll.name());
        operationUpdate.setTagIds(new ArrayList<>());

        String jsonProperties = json(operationUpdate);

        String url = "/shoppinglist/" + sourceListId + "/item";

        this.mockMvc.perform(put(url)
                        .with(user(meUserDetails))
                        .contentType(contentType)
                        .content(jsonProperties))
                .andExpect(status().isOk())
                .andReturn();

        // now, retrieve the list
        // check destination list
        ShoppingList source = retrieveList(meUserDetails, sourceListId);
        Map<String, ShoppingListItem> destinationResultMap = source.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));

        Assertions.assertNotNull(destinationResultMap);
        Assertions.assertEquals(0, destinationResultMap.keySet().size());

    }


    @Test
    @WithMockUser
    void testRemoveDish_CrossedOffOk() throws Exception {
        // test for LS-883 here

        // create list
        ListGenerateProperties properties = new ListGenerateProperties();
        properties.setAddFromStarter(true);
        properties.setGenerateMealplan(false);

        String jsonProperties = json(properties);
        String listId = createList(jsonProperties, meUserDetails);

        String url = "/shoppinglist";
        MvcResult listResultBefore = this.mockMvc.perform(get(url + "/" + listId)
                        .with(user(meUserDetails)))
                .andReturn();
        String jsonList = listResultBefore.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ShoppingListResource beforeList = objectMapper.readValue(jsonList, ShoppingListResource.class);
        Assertions.assertNotNull(beforeList);

        // add dish which contains tag carrots - 109
        String addDishOneUrl = String.format("%s/%s/dish/%s", url, listId, "109");
        this.mockMvc.perform(post(addDishOneUrl)
                        .with(user(meUserDetails)))
                .andReturn();
        // add another dish containing tag carrots - 112
        String addDishTwoUrl = String.format("%s/%s/dish/%s", url, listId, "112");
        this.mockMvc.perform(post(addDishTwoUrl)
                        .with(user(meUserDetails)))
                .andReturn();

        // check results
        MvcResult listResultsAddDishTwo = this.mockMvc.perform(get(url + "/" + listId)
                        .with(user(meUserDetails)))
                .andReturn();
        jsonList = listResultsAddDishTwo.getResponse().getContentAsString();
        ShoppingListResource afterAddDishTwo = objectMapper.readValue(jsonList, ShoppingListResource.class);
        Assertions.assertNotNull(afterAddDishTwo);

        // cross off all items on list
        String crossOffItemsUrl = String.format("%s/%s/item/shop?crossOff=true", url, listId);
        this.mockMvc.perform(post(crossOffItemsUrl)
                        .with(user(meUserDetails)))
                .andReturn();
        // check results
        MvcResult listResultsCrossedOff = this.mockMvc.perform(get(url + "/" + listId)
                        .with(user(meUserDetails)))
                .andReturn();
        jsonList = listResultsCrossedOff.getResponse().getContentAsString();
        ShoppingListResource afterCrossedOff = objectMapper.readValue(jsonList, ShoppingListResource.class);
        Assertions.assertNotNull(afterCrossedOff);


        // remove first dish - 109
        String deleteDishUrl = String.format("%s/%s/dish/%s", url, listId, "109");
        this.mockMvc.perform(delete(deleteDishUrl)
                        .with(user(meUserDetails)))
                .andReturn();

        // get Shopping List and confirm that carrots are still crossed off
        MvcResult listResultsAfter = this.mockMvc.perform(get(url + "/" + listId)
                        .with(user(meUserDetails)))
                .andReturn();
        jsonList = listResultsAfter.getResponse().getContentAsString();
        ShoppingListResource afterList = objectMapper.readValue(jsonList, ShoppingListResource.class);
        Assertions.assertNotNull(afterList);
        ShoppingList list = afterList.getShoppingList();
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
        Assertions.assertEquals(1, carrot.getSourceKeys().size(), "only one source key for carrots shown");
        Assertions.assertNotNull(carrot.getCrossedOff(), "carrots should be crossed off");
    }

    private ShoppingList retrieveList(UserDetails userDetails, Long listId) throws Exception {
        MvcResult listResultsAfter = this.mockMvc.perform(get("/shoppinglist/" + listId)
                        .with(user(userDetails)))
                .andReturn();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonList = listResultsAfter.getResponse().getContentAsString();
        ShoppingListResource afterList = objectMapper.readValue(jsonList, ShoppingListResource.class);
        Assertions.assertNotNull(afterList);
        return afterList.getShoppingList();
    }

    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
