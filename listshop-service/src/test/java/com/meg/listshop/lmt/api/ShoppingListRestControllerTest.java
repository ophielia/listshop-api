/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.lmt.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meg.listshop.Application;
import com.meg.listshop.auth.service.impl.JwtUser;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.data.entity.ItemEntity;
import com.meg.listshop.lmt.data.repository.ItemRepository;
import com.meg.listshop.test.TestConstants;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.StreamUtils;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
@Sql(value = {"/sql/com/meg/atable/lmt/api/ShoppingListRestControllerTest.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sql/com/meg/atable/lmt/api/ShoppingListRestControllerTest_rollback.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ShoppingListRestControllerTest {

    @ClassRule
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();


    private static UserDetails userDetails;
    private static UserDetails meUserDetails;
    private static UserDetails lastListUserDetails;
    private static UserDetails noStarterUserDetails;
    private static UserDetails dadStarterUserDetails;
    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype());
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        this.mappingJackson2HttpMessageConverter = Arrays.stream(converters)

                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null");
    }

    @Value("classpath:/data/shoppingListRestControllerTest_mergeList.json")
    Resource resourceFile;

    @Value("classpath:/data/shoppingListRestControllerTest_mergeListStale.json")
    Resource resourceFileStale;

    @Value("classpath:/data/shoppingListRestControllerTest_mergeListEmpty.json")
    Resource resourceFileEmpty;

    @Value("classpath:/data/shoppingListRestControllerTest_mergeListWithConflicts.json")
    Resource mergeConflictFileSource;


    private MockMvc mockMvc;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;


    @Before
    @WithMockUser
    public void setup() {

        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();


        userDetails = new JwtUser(TestConstants.USER_1_ID,
                TestConstants.USER_1_EMAIL,
                null,
                null,
                null,
                true,
                null);

        meUserDetails = new JwtUser(TestConstants.USER_3_ID,
                TestConstants.USER_3_NAME,
                null,
                null,
                null,
                true,
                null);

        lastListUserDetails = new JwtUser(99999L,
                "username@testitytest.com",
                "username@testitytest.com",
                null,
                null,
                true,
                null);

        noStarterUserDetails = new JwtUser(TestConstants.USER_4_ID,
                TestConstants.USER_4_NAME,
                null,
                null,
                null,
                true,
                null);

        dadStarterUserDetails = new JwtUser(34L,
                "dad@userdetails.com",
                null,
                null,
                null,
                true,
                null);

    }


    @Test
    @WithMockUser
    public void testRetrieveLists() throws Exception {

        mockMvc.perform(get("/shoppinglist")
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(contentType));


    }

    @Test
    @WithMockUser
    public void testRetrieveMostRecentList() throws Exception {
        Long testId = 509990L;

        // updating list, so that it _is_ the most recent
        ShoppingListPut shoppingList = new ShoppingListPut(testId)
                .name("updated list most recent")
                .isStarterList(false);
        String payload = json(shoppingList);

        MvcResult result = mockMvc.perform(put("/shoppinglist/" + testId)
                        .with(user(meUserDetails))
                        .content(payload).contentType(contentType))
                .andExpect(status().isOk())
                .andReturn();
        // now, testing the most recent call
        result = mockMvc.perform(get("/shoppinglist/mostrecent")
                        .with(user(meUserDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.shopping_list.list_id", Matchers.isA(Number.class)))
                .andReturn();

    }

    @Test
    @WithMockUser
    public void testRetrieveStarterList() throws Exception {
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
    public void testRetrieveStarterListNotFound() throws Exception {
        mockMvc.perform(get("/shoppinglist/starter")
                        .with(user(noStarterUserDetails)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void testRetrieveListById() throws Exception {
        Long testId = TestConstants.LIST_2_ID;

        MvcResult result = mockMvc.perform(get("/shoppinglist/" + TestConstants.LIST_2_ID)
                        .with(user(meUserDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.shopping_list.list_id", Matchers.isA(Number.class)))
                .andExpect(jsonPath("$.shopping_list.legend", Matchers.hasSize(8)))
                .andExpect(jsonPath("$.shopping_list.legend[*].key",
                        Matchers.containsInAnyOrder("d54", "d83", "d55", "d90", "l501", "l402", "d56", "d16")))
                .andReturn();


        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode containedListJson = jsonNode.get("shopping_list");
        jsonNode.get("shopping_list");

    }

    @Test
    @WithMockUser
    public void testCustomLayout() throws Exception {
        Long customLayoutListId = 10101010L;
        Long standardLayoutListId = 90909090L;

        ShoppingList standardLayoutList = retrieveList(dadStarterUserDetails, standardLayoutListId);
        Map<String, ShoppingListItem> standardResultMap = standardLayoutList.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assert.assertNotNull(standardResultMap);


        ShoppingList customLayoutList = retrieveList(meUserDetails, customLayoutListId);
        Map<String, ShoppingListItem> customResultMap = customLayoutList.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assert.assertNotNull(customResultMap);

        // item count should be equal
        Assert.assertEquals("item count should be equal.", customResultMap.keySet().size(), standardResultMap.keySet().size());
        // category count should not be equal
        Assert.assertNotEquals("category count should not be equal.", standardLayoutList.getCategories().size(), customLayoutList.getCategories().size());
        // custom
        Assert.assertEquals("custom should have 3 categories", 3, customLayoutList.getCategories().size());
        Optional<ShoppingListCategory> specialCategory = customLayoutList.getCategories().stream()
                .filter(c -> c.getName().equals("Special")).findFirst();
        Assert.assertTrue("on category should be called 'Special'", specialCategory.isPresent());
        Assert.assertEquals("special contains one item", 1, specialCategory.get().getItems().size());
        ShoppingListItem tomatoes = specialCategory.get().getItems().get(0);
        Assert.assertEquals("tomatoes are tomatoes", "tomatoes", tomatoes.getTagName());
    }


    @Test
    @WithMockUser
    public void testUpdateList() throws Exception {
        Long testId = 509991L;

        ShoppingListPut shoppingList = new ShoppingListPut(testId)
                .name("updated list")
                .isStarterList(false);

        String payload = json(shoppingList);

        MvcResult result = mockMvc.perform(put("/shoppinglist/" + testId)
                        .with(user(meUserDetails))
                        .content(payload).contentType(contentType))
                .andExpect(status().isOk())
                .andReturn();

    }

    @Test
    @WithMockUser
    public void testUpdateList_starterListChange() throws Exception {
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
    public void testDeleteList() throws Exception {
        Long testId = TestConstants.LIST_2_ID;

        mockMvc.perform(delete("/shoppinglist/" + testId)
                        .with(user(meUserDetails)))
                .andExpect(status().isNoContent());

    }

    @Test
    @WithMockUser
    public void testDeleteList_LastList() throws Exception {
        Long testId = 99999L;

        mockMvc.perform(delete("/shoppinglist/" + testId)
                        .with(user(lastListUserDetails)))
                .andExpect(status().isBadRequest());

    }

    @Test
    @WithMockUser
    public void testDeleteItemFromList() throws Exception {
        Long listId = TestConstants.LIST_3_ID;
        String url = "/shoppinglist/" + listId + "/item/" + 501L;
        mockMvc.perform(delete(url)
                        .with(user(userDetails)))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    public void testGenerateFromMealPlan() throws Exception {

        Long listId = 51000L;
        Long mealPlanId = 65505L;

        String url = "/shoppinglist/mealplan/" + mealPlanId;
        MvcResult result = this.mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .contentType(contentType))
                .andExpect(status().isCreated())
                .andReturn();

        List<String> responses = result.getResponse().getHeaders("Location");
        Assert.assertNotNull(responses);
        Assert.assertTrue(responses.size() > 0);
        String[] urlTokens = StringUtils.split(responses.get(0), "/");
        Long newListId = Long.valueOf(urlTokens[(urlTokens).length - 1]);

        // now, retrieve the list
        ShoppingList source = retrieveList(userDetails, listId);
        Map<String, ShoppingListItem> resultMap = source.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assert.assertNotNull(resultMap);

        // check tag occurences in result

        // 502 - 3
        Assert.assertNotNull(resultMap.get("1"));
        // 503 - 2
        Assert.assertNotNull(resultMap.get("12"));
        Assert.assertTrue(resultMap.get("12").getUsedCount() == 1); // showing 1 in result map
        // 436 - 1
        Assert.assertNotNull(resultMap.get("81"));
        Assert.assertEquals(Integer.valueOf(1), resultMap.get("81").getUsedCount());

    }

    @Test
    @WithMockUser
    public void testNewCreateList() throws Exception {
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
    public void testSetCrossedOffForItem() throws Exception {
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
    public void testCrossOffAllItemsOnList() throws Exception {

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
    public void testAddListToList() throws Exception {
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
        MvcResult result = mockMvc.perform(get("/shoppinglist/" + listId)
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.shopping_list.list_id", Matchers.isA(Number.class)))
                .andExpect(jsonPath("$.shopping_list.list_id").value(listId))
                .andReturn();
        //.andExpect(jsonPath("$.shopping_list.is_starter_list").value(false));

        String listAfterAdd = result.getResponse().getContentAsString();

    }

    @Test
    @WithMockUser
    public void testRemoveListFromList() throws Exception {
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
        Assert.assertTrue(listAfterDelete.contains("\"tag_id\":\"503\","));
        Assert.assertTrue(listAfterDelete.contains("\"tag_id\":\"500\","));

        // doesn't contain tagIds 501, 502, 504
        Assert.assertFalse(listAfterDelete.contains("\"tag_id\":\"501\","));
        Assert.assertFalse(listAfterDelete.contains("\"tag_id\":\"502\","));
        Assert.assertFalse(listAfterDelete.contains("\"tag_id\":\"504\","));
    }

    @Test
    @WithMockUser
    public void testAddDishToList() throws Exception {
        Long listId = TestConstants.LIST_1_ID;
        String url = "/shoppinglist/" + listId + "/dish/" + TestConstants.DISH_7_ID;
        mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .contentType(contentType))
                .andExpect(status().isNoContent());

    }

    @Test
    @WithMockUser
    public void testUpdateItemUsedCount() throws Exception {

        Long listId = 7777L;
        Long tagId = 500L;
        Integer usedCount = 6;
        String url = "/shoppinglist/" + listId + "/tag/" + tagId + "/count/" + usedCount;
        mockMvc.perform(put(url)
                        .with(user(meUserDetails))
                        .contentType(contentType))
                .andExpect(status().isNoContent());

        // make sure the item has been updated
        ItemEntity resultItem = itemRepository.getItemByListAndTag(listId, tagId);

        Assert.assertEquals(usedCount, resultItem.getUsedCount());
    }


    @Test
    @WithMockUser
    public void testAddMealPlanToList() throws Exception {

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

        Assert.assertNotNull(sourceResultMap);
        // check tag occurences in result
        // 501 - 1
        Assert.assertNotNull(sourceResultMap.get("81"));
        Assert.assertTrue(sourceResultMap.get("81").getUsedCount() == 1);

        // 502 - 3
        Assert.assertNotNull(sourceResultMap.get("1"));
        Assert.assertEquals(Integer.valueOf(3), sourceResultMap.get("1").getUsedCount());  // showing 1 in result map
        // 503 - 2
        Assert.assertNotNull(sourceResultMap.get("12"));
        Assert.assertEquals(Integer.valueOf(2), sourceResultMap.get("12").getUsedCount()); // showing 1 in result map
        // 436 - 1
        Assert.assertNotNull(sourceResultMap.get("436"));
        Assert.assertEquals(Integer.valueOf(1), sourceResultMap.get("436").getUsedCount());

    }

    @Test
    @WithMockUser
    public void testMergeList() throws Exception {
        String testMergeList = StreamUtils.copyToString(resourceFile.getInputStream(), Charset.forName("utf8"));

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
        Assert.assertNotNull(sourceResultMap);

        // check result
        // should have 9 items
        Assert.assertEquals("should have 15 items", 15, sourceResultMap.keySet().size());
        // should not contain tag 32 (which was removed)
        Assert.assertFalse("shouldn't contain tag 32", sourceResultMap.containsKey("32"));
        // not crossed off - 33, 16
        Map<String, ShoppingListItem> activeMap = source.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .filter(i -> i.getCrossedOff() == null)
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assert.assertEquals("10 active items", 10, activeMap.keySet().size());
        Assert.assertTrue("33 should be actice", activeMap.containsKey("33"));
        Assert.assertTrue("16 should be actice", activeMap.containsKey("16"));

        //  crossed off - 19, 34
        Map<String, ShoppingListItem> crossedOffMap = source.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .filter(i -> i.getCrossedOff() != null)
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assert.assertEquals("5 crossed off items", 5, crossedOffMap.keySet().size());
        Assert.assertTrue("33 should be crossed off", crossedOffMap.containsKey("19"));
        Assert.assertTrue("16 should be crossed off", crossedOffMap.containsKey("34"));
    }

    @Test
    @WithMockUser
    public void testMergeList_Stale() throws Exception {
        String testMergeList = StreamUtils.copyToString(resourceFileStale.getInputStream(), Charset.forName("utf8"));

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
        Assert.assertNotNull(sourceResultMap);

        // check result
        // should have 12 items
        Assert.assertEquals("should have 13 items", 13, sourceResultMap.keySet().size());
        // should not contain tag 32 (which was removed)
        Assert.assertFalse("shouldn't contain tag 32", sourceResultMap.containsKey("32"));
        // not crossed off - 16
        Map<String, ShoppingListItem> activeMap = source.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .filter(i -> i.getCrossedOff() == null)
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assert.assertEquals("9 active items", 9, activeMap.keySet().size());
        Assert.assertTrue("16 should be actice", activeMap.containsKey("16"));

        //  crossed off - 19
        Map<String, ShoppingListItem> crossedOffMap = source.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .filter(i -> i.getCrossedOff() != null)
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assert.assertEquals("4 crossed off items", 4, crossedOffMap.keySet().size());
        Assert.assertTrue("19 should be crossed off", crossedOffMap.containsKey("19"));
    }

    @Test
    @WithMockUser
    public void testMergeList_Empty() throws Exception {
        //// load statistics into file
        String testMergeList = StreamUtils.copyToString(resourceFileEmpty.getInputStream(), Charset.forName("utf8"));

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
        Assert.assertNotNull(sourceResultMap);

        // check result
        // should have 9 items
        Assert.assertTrue("should be empty - empty list - empty merge request", sourceResultMap.isEmpty());
    }

    @Test
    @WithMockUser
    public void testMergeList_TagConflict() throws Exception {
        String testMergeList = StreamUtils.copyToString(mergeConflictFileSource.getInputStream(), Charset.forName("utf8"));

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
        Assert.assertNotNull(sourceResultMap);

        // check result
        // should have 9 items
        Assert.assertEquals("should have 9 items", 3, sourceResultMap.keySet().size());
        // should not contain tag 12001 or 12002 (standard)
        Assert.assertFalse("shouldn't contain tag 12001", sourceResultMap.containsKey("12001"));
        Assert.assertFalse("shouldn't contain tag 12002", sourceResultMap.containsKey("12002"));
        // should  contain tag 13001 or 13002 (standard)
        Assert.assertTrue("shouldn't contain tag 13001", sourceResultMap.containsKey("13001"));
        Assert.assertTrue("shouldn't contain tag 13002", sourceResultMap.containsKey("13002"));
        // should contain 21 - no conflict
        Assert.assertTrue("shouldn't contain tag 21", sourceResultMap.containsKey("21"));

    }

    @Test
    @WithMockUser
    public void testRemoveDishFromList() throws Exception {
        Long listId = TestConstants.LIST_1_ID;
        String url = "/shoppinglist/" + listId + "/dish/" + TestConstants.DISH_1_ID;
        mockMvc.perform(delete(url)
                        .with(user(userDetails))
                        .contentType(contentType))
                .andExpect(status().isNotFound());  // bad request, because user doesn't own this dish

        listId = TestConstants.LIST_1_ID;
        url = "/shoppinglist/" + listId + "/dish/" + TestConstants.DISH_7_ID;
        mockMvc.perform(delete(url)
                        .with(user(userDetails))
                        .contentType(contentType))
                .andExpect(status().isNoContent());  // this one is successful
    }


    @Test
    @WithMockUser
    public void testChangeListLayout() throws Exception {

        Long listId = TestConstants.LIST_1_ID;
        String url = "/shoppinglist/" + listId + "/layout/" + TestConstants.LIST_LAYOUT_2_ID;
        mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .contentType(contentType))
                .andExpect(status().isNoContent());

    }


    @Test
    @WithMockUser
    public void deleteAllItemsFromList() throws Exception {

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
        Assert.assertNotNull(responses);
        Assert.assertTrue(responses.size() > 0);
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
        Assert.assertNotNull(resultMap);
        Assert.assertTrue(resultMap.isEmpty());
    }

    @Test
    @WithMockUser
    public void deleteItemOperation_Move() throws Exception {
        Long sourceListId = 7777L;  // 500 (CrossedOff), 501, 502, 505 (CrossedOff)
        Long destinationListId = 6666L;  // 501, 502, 503, 505
        List<Long> tagIdsForUpdate = Arrays.asList(500L, 502L);

        ItemOperationPut operationUpdate = new ItemOperationPut();
        operationUpdate.setDestinationListId(destinationListId);
        operationUpdate.setOperation(ItemOperationType.Move.name());
        operationUpdate.setTagIds(tagIdsForUpdate);

        String jsonProperties = json(operationUpdate);

        String url = "/shoppinglist/" + sourceListId + "/item";

        MvcResult result = this.mockMvc.perform(put(url)
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

        Assert.assertNotNull(sourceResultMap);
        Assert.assertEquals(2, sourceResultMap.keySet().size());
        // 500 shouldn't be there
        Assert.assertFalse(sourceResultMap.keySet().contains("500"));
        // check destination list
        ShoppingList destination = retrieveList(meUserDetails, destinationListId);
        Map<String, ShoppingListItem> destinationResultMap = destination.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assert.assertNotNull(destinationResultMap);
        Assert.assertEquals(5, destinationResultMap.keySet().size());
        // 500 should be there with count 1
        Assert.assertTrue(destinationResultMap.containsKey("500"));
        ShoppingListItem testElement = destinationResultMap.get("500");
        Assert.assertEquals(Long.valueOf(1), Long.valueOf(testElement.getUsedCount()));
        // 502 should be there with a count of 2
        testElement = destinationResultMap.get("502");
        Assert.assertEquals(Long.valueOf(2), Long.valueOf(testElement.getUsedCount()));

    }

    @Test
    @WithMockUser
    public void deleteItemOperation_Copy() throws Exception {
        Long sourceListId = 7777L;  // 500 (CrossedOff), 501, 502, 505 (CrossedOff)
        Long destinationListId = 6666L;  // 501, 502, 503, 505
        List<Long> tagIdsForUpdate = Arrays.asList(500L, 501L, 504L);

        ItemOperationPut operationUpdate = new ItemOperationPut();
        operationUpdate.setDestinationListId(destinationListId);
        operationUpdate.setOperation(ItemOperationType.Copy.name());
        operationUpdate.setTagIds(tagIdsForUpdate);

        String jsonProperties = json(operationUpdate);

        String url = "/shoppinglist/" + sourceListId + "/item";

        MvcResult result = this.mockMvc.perform(put(url)
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

        Assert.assertEquals(4, sourceResultMap.keySet().size());
        // check destination list
        ShoppingList destination = retrieveList(meUserDetails, destinationListId);
        Map<String, ShoppingListItem> destinationResultMap = destination.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));

        Assert.assertNotNull(destinationResultMap);
        Assert.assertEquals(5, destinationResultMap.keySet().size());
        // 500 should be there with count 1
        Assert.assertTrue(destinationResultMap.containsKey("500"));
        ShoppingListItem testElement = destinationResultMap.get("500");
        Assert.assertEquals(Long.valueOf(1), Long.valueOf(testElement.getUsedCount()));
        // 501 should be there with a count of 2
        testElement = destinationResultMap.get("501");
        Assert.assertEquals(Long.valueOf(2), Long.valueOf(testElement.getUsedCount()));

    }

    @Test
    @WithMockUser
    public void deleteItemOperation_MoveCrossedOff_New() throws Exception {
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

        Assert.assertNotNull(sourceResultMap);
        Assert.assertEquals(2, sourceResultMap.keySet().size());
        // 500 shouldn't be there
        Assert.assertFalse(sourceResultMap.keySet().contains(500L));
        // check destination list
        ShoppingList destination = retrieveList(meUserDetails, destinationListId);
        Map<String, ShoppingListItem> destinationResultMap = destination.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));

        Assert.assertNotNull(destinationResultMap);
        Assert.assertEquals(5, destinationResultMap.keySet().size());
        // 500 should be there with count 1, crossedOff
        Assert.assertTrue(destinationResultMap.containsKey("500"));
        ShoppingListItem testElement = destinationResultMap.get("500");
        Assert.assertEquals(Long.valueOf(1), Long.valueOf(testElement.getUsedCount()));
        Assert.assertNotNull(testElement.getCrossedOff());
        // 502 should be there with a count of 2
        testElement = destinationResultMap.get("502");
        Assert.assertEquals(Long.valueOf(2), Long.valueOf(testElement.getUsedCount()));
    }

    @Test
    @WithMockUser
    public void deleteItemOperation_MoveCrossedOff_Existing() throws Exception {
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
        Assert.assertNotNull(afterList);
        ShoppingList list = afterList.getShoppingList();

        Map<String, ShoppingListItem> allSourceResultMap = list.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));

        Assert.assertNotNull(allSourceResultMap);
        Assert.assertEquals(2, allSourceResultMap.keySet().size());
        // 505 shouldn't be there
        Assert.assertFalse(allSourceResultMap.keySet().contains("505"));

        // check destination list
        ShoppingList destination = retrieveList(meUserDetails, destinationListId);
        Map<String, ShoppingListItem> destinationResultMap = destination.getCategories().stream()
                .flatMap(c -> c.getItems().stream())
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));

        Assert.assertNotNull(destinationResultMap);
        Assert.assertEquals(4, destinationResultMap.keySet().size());
        // 505 should be there with count 2, not crossedOff
        Assert.assertTrue(destinationResultMap.containsKey("505"));
        ShoppingListItem testElement = destinationResultMap.get("505");
        Assert.assertEquals(Long.valueOf(2), Long.valueOf(testElement.getUsedCount()));
        Assert.assertNull(testElement.getCrossedOff());
        // 502 should be there with a count of 2
        testElement = destinationResultMap.get("502");
        Assert.assertEquals(Long.valueOf(2), Long.valueOf(testElement.getUsedCount()));
    }

    private ShoppingList retrieveList(UserDetails userDetails, Long listId) throws Exception {
        MvcResult listResultsAfter = this.mockMvc.perform(get("/shoppinglist/" + listId)
                        .with(user(userDetails)))
                .andReturn();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonList = listResultsAfter.getResponse().getContentAsString();
        ShoppingListResource afterList = objectMapper.readValue(jsonList, ShoppingListResource.class);
        Assert.assertNotNull(afterList);
        return afterList.getShoppingList();
    }


    @Test
    @WithMockUser
    public void deleteItemOperation_Remove() throws Exception {
        Long sourceListId = 7777L;  // 500 (CrossedOff), 501, 502, 505 (CrossedOff)
        List<Long> tagIdsForUpdate = Arrays.asList(500L, 501L, 504L);

        ItemOperationPut operationUpdate = new ItemOperationPut();
        operationUpdate.setDestinationListId(null);
        operationUpdate.setOperation(ItemOperationType.Remove.name());
        operationUpdate.setTagIds(tagIdsForUpdate);

        String jsonProperties = json(operationUpdate);

        String url = "/shoppinglist/" + sourceListId + "/item";

        MvcResult result = this.mockMvc.perform(put(url)
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

        Assert.assertNotNull(sourceResultMap);
        Assert.assertEquals(2, sourceResultMap.keySet().size());
        // 500 shouldn't be there
        Assert.assertFalse(sourceResultMap.keySet().contains(500L));

    }

    @Test
    @WithMockUser
    public void deleteItemOperation_RemoveCrossedOff() throws Exception {
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
                .collect(Collectors.toList());

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


        Assert.assertNotNull(sourceResultMap);
        // check that none of the crossed off ids are in the map
        for (String crossedOffId : crossedOffIds) {
            Assert.assertFalse("crossed off ids shouldn't be in the list", sourceResultMap.keySet().contains(crossedOffId));
        }

    }

    @Test
    @WithMockUser
    public void deleteItemOperation_RemoveAll() throws Exception {
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

        Assert.assertNotNull(destinationResultMap);
        Assert.assertEquals(0, destinationResultMap.keySet().size());

    }


    @Test
    @WithMockUser
    public void testRemoveDish_CrossedOffOk() throws Exception {
        // test for LS-883 here

        // create list
        ListGenerateProperties properties = new ListGenerateProperties();
        properties.setAddFromStarter(true);
        properties.setGenerateMealplan(false);

        String jsonProperties = json(properties);

        String url = "/shoppinglist";

        MvcResult createResult = this.mockMvc.perform(post(url)
                        .with(user(meUserDetails))
                        .contentType(contentType)
                        .content(jsonProperties))
                .andExpect(status().isCreated())
                .andReturn();
        String locationString = createResult.getResponse().getHeader("Location");
        String listId = locationString.substring(locationString.lastIndexOf("/") + 1);

        MvcResult listResultBefore = this.mockMvc.perform(get(url + "/" + listId)
                        .with(user(meUserDetails)))
                .andReturn();
        String jsonList = listResultBefore.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ShoppingListResource beforeList = objectMapper.readValue(jsonList, ShoppingListResource.class);
        Assert.assertNotNull(beforeList);

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

        // cross off all items on list
        String crossOffItemsUrl = String.format("%s/%s/item/shop?crossOff=true", url, listId);
        this.mockMvc.perform(post(crossOffItemsUrl)
                        .with(user(meUserDetails)))
                .andReturn();

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
        Assert.assertNotNull(afterList);
        ShoppingList list = afterList.getShoppingList();
        Optional<ShoppingListCategory> produce = list.getCategories()
                .stream()
                .filter(c -> c.getName().equals("Produce"))
                .findFirst();
        Assert.assertTrue("list contains category produce", produce.isPresent());
        Optional<ShoppingListItem> carrotOpt = produce.get().getItems().stream()
                .filter(i -> i.getTag().getName().equals("carrots"))
                .findFirst();
        Assert.assertTrue("carrots present in list", carrotOpt.isPresent());
        ShoppingListItem carrot = carrotOpt.get();
        Assert.assertEquals("only one source key for carrots shown", 1, carrot.getSourceKeys().size());
        Assert.assertNotNull("carrots should be crossed off", carrot.getCrossedOff());
    }

    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }


}
