package com.meg.listshop.lmt.api;

import com.meg.listshop.Application;
import com.meg.listshop.auth.service.impl.JwtUser;
import com.meg.listshop.lmt.api.model.ItemOperationPut;
import com.meg.listshop.lmt.api.model.ItemOperationType;
import com.meg.listshop.lmt.api.model.ListGenerateProperties;
import com.meg.listshop.lmt.api.model.ShoppingListPut;
import com.meg.listshop.lmt.data.entity.ItemEntity;
import com.meg.listshop.lmt.data.entity.ShoppingListEntity;
import com.meg.listshop.lmt.data.repository.ItemRepository;
import com.meg.listshop.lmt.service.ShoppingListService;
import com.meg.listshop.test.TestConstants;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

    private static UserDetails userDetails;
    private static UserDetails meUserDetails;
    private static UserDetails noStarterUserDetails;
    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    ShoppingListService shoppingListService;

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


    private MockMvc mockMvc;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;


    @Before
    @WithMockUser
    public void setup() {

        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();


        userDetails = new JwtUser(TestConstants.USER_1_ID,
                TestConstants.USER_1_NAME,
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

        noStarterUserDetails = new JwtUser(TestConstants.USER_4_ID,
                TestConstants.USER_4_NAME,
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
                // .andExpect(jsonPath("$.shopping_list.list_id").value(testId))
                .andReturn();

        String beep = "bop";
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
                .andExpect(status().isBadRequest());
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
        ShoppingListEntity resultList = shoppingListService.getListById(TestConstants.USER_1_NAME, newListId);
        Map<Long, ItemEntity> resultMap = resultList.getItems().stream()
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assert.assertNotNull(resultMap);
        // check tag occurences in result

        // 502 - 3
        Assert.assertNotNull(resultMap.get(1L));
        Assert.assertTrue(resultMap.get(1L).getUsedCount() == 2);  // showing 1 in result map
        // 503 - 2
        Assert.assertNotNull(resultMap.get(12L));
        Assert.assertTrue(resultMap.get(12L).getUsedCount() == 1); // showing 1 in result map
        // 436 - 1
        Assert.assertNotNull(resultMap.get(436L));
        Assert.assertTrue(resultMap.get(436L).getUsedCount() == 1);

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
        ShoppingListEntity resultList = shoppingListService.getListById(TestConstants.USER_1_NAME, listId);
        Map<Long, ItemEntity> resultMap = resultList.getItems().stream()
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assert.assertNotNull(resultMap);
        // check tag occurences in result
        // 501 - 1
        Assert.assertNotNull(resultMap.get(81L));
        Assert.assertTrue(resultMap.get(81L).getUsedCount() == 1);

        // 502 - 3
        Assert.assertNotNull(resultMap.get(1L));
        Assert.assertTrue(resultMap.get(1L).getUsedCount() == 3);  // showing 1 in result map
        // 503 - 2
        Assert.assertNotNull(resultMap.get(12L));
        Assert.assertTrue(resultMap.get(12L).getUsedCount() == 2); // showing 1 in result map
        // 436 - 1
        Assert.assertNotNull(resultMap.get(436L));
        Assert.assertTrue(resultMap.get(436L).getUsedCount() == 1);

    }

    @Test
    @WithMockUser
    public void testMergeList() throws Exception {

        //// load statistics into file
        String testMergeList = StreamUtils.copyToString(resourceFile.getInputStream(), Charset.forName("utf8"));

        Long listId = 500777L;

        String url = "/shoppinglist/shared";
        mockMvc.perform(put(url)
                .with(user(meUserDetails))
                .contentType(contentType)
                .content(testMergeList))
                .andExpect(status().isOk());

        // now, retrieve the list
        ShoppingListEntity resultList = shoppingListService.getListById(TestConstants.USER_3_NAME, listId);
        Map<Long, ItemEntity> resultMap = resultList.getItems().stream()
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assert.assertNotNull(resultMap);
        // check tag occurences in result
        // 501 - 1
        Assert.assertNotNull(resultMap.get(363L));
        Assert.assertTrue(resultMap.get(363L).getUsedCount() == 1);

        // 502 - 3
        Assert.assertNotNull(resultMap.get(502L));
        Assert.assertTrue(resultMap.get(502L).getUsedCount() == 1);  // showing 1 in result map
        // 503 - 2
        Assert.assertNotNull(resultMap.get(503L));
        Assert.assertTrue(resultMap.get(503L).getUsedCount() == 1); // showing 1 in result map
        // 436 - 1
        Assert.assertNotNull(resultMap.get(501L));
        Assert.assertTrue(resultMap.get(501L).getUsedCount() == 2);

    }



    @Test
    @WithMockUser
    public void testRemoveDishFromList() throws Exception {
// MM clean this test....
        Long listId = TestConstants.LIST_1_ID;
        String url = "/shoppinglist/" + listId + "/dish/" + TestConstants.DISH_1_ID;
        mockMvc.perform(delete(url)
                .with(user(userDetails))
                .contentType(contentType))
                .andExpect(status().isBadRequest());  // bad request, because user doesn't own this dish

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
        ShoppingListEntity resultList = shoppingListService.getListById(TestConstants.USER_1_NAME, listId);
        Map<Long, ItemEntity> resultMap = resultList.getItems().stream()
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
        ShoppingListEntity sourceResultList = shoppingListService.getListById(TestConstants.USER_3_NAME, sourceListId);
        Map<Long, ItemEntity> allSourceResultMap = sourceResultList.getItems().stream()
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Map<Long, ItemEntity> withoutRemovedResultMap = sourceResultList.getItems().stream()
                .filter(item -> item.getRemovedOn() == null)
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assert.assertNotNull(allSourceResultMap);
        Assert.assertEquals(4, allSourceResultMap.keySet().size());
        Assert.assertEquals(2, withoutRemovedResultMap.keySet().size());
        // 500 shouldn't be there
        Assert.assertFalse(withoutRemovedResultMap.keySet().contains(500L));
        // check destination list
        ShoppingListEntity destinationResultList = shoppingListService.getListById(TestConstants.USER_3_NAME, destinationListId);
        Map<Long, ItemEntity> destinationResultMap = destinationResultList.getItems().stream()
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assert.assertNotNull(destinationResultMap);
        Assert.assertEquals(5, destinationResultMap.keySet().size());
        // 500 should be there with count 1
        Assert.assertTrue(destinationResultMap.containsKey(500L));
        ItemEntity testElement = destinationResultMap.get(500L);
        Assert.assertEquals(new Long(1), Long.valueOf(testElement.getUsedCount()));
        // 502 should be there with a count of 2
        testElement = destinationResultMap.get(502L);
        Assert.assertEquals(new Long(2), Long.valueOf(testElement.getUsedCount()));

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
        ShoppingListEntity sourceResultList = shoppingListService.getListById(TestConstants.USER_3_NAME, sourceListId);
        Map<Long, ItemEntity> sourceResultMap = sourceResultList.getItems().stream()
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assert.assertNotNull(sourceResultMap);
        Assert.assertEquals(4, sourceResultMap.keySet().size());
        // check destination list
        ShoppingListEntity destinationResultList = shoppingListService.getListById(TestConstants.USER_3_NAME, destinationListId);
        Map<Long, ItemEntity> destinationResultMap = destinationResultList.getItems().stream()
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assert.assertNotNull(destinationResultMap);
        Assert.assertEquals(5, destinationResultMap.keySet().size());
        // 500 should be there with count 1
        Assert.assertTrue(destinationResultMap.containsKey(500L));
        ItemEntity testElement = destinationResultMap.get(500L);
        Assert.assertEquals(new Long(1), Long.valueOf(testElement.getUsedCount()));
        // 501 should be there with a count of 2
        testElement = destinationResultMap.get(501L);
        Assert.assertEquals(new Long(2), Long.valueOf(testElement.getUsedCount()));

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

        MvcResult result = this.mockMvc.perform(put(url)
                .with(user(meUserDetails))
                .contentType(contentType)
                .content(jsonProperties))
                .andExpect(status().isOk())
                .andReturn();

        // now, retrieve the list
        ShoppingListEntity sourceResultList = shoppingListService.getListById(TestConstants.USER_3_NAME, sourceListId);
        Map<Long, ItemEntity> allSourceResultMap = sourceResultList.getItems().stream()
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Map<Long, ItemEntity> withoutRemovedResultMap = sourceResultList.getItems().stream()
                .filter(item -> item.getRemovedOn() == null)
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assert.assertNotNull(allSourceResultMap);
        Assert.assertEquals(4, allSourceResultMap.keySet().size());
        Assert.assertEquals(2, withoutRemovedResultMap.keySet().size());
        // 500 shouldn't be there
        Assert.assertFalse(withoutRemovedResultMap.keySet().contains(500L));
        // check destination list
        ShoppingListEntity destinationResultList = shoppingListService.getListById(TestConstants.USER_3_NAME, destinationListId);
        Map<Long, ItemEntity> destinationResultMap = destinationResultList.getItems().stream()
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assert.assertNotNull(destinationResultMap);
        Assert.assertEquals(5, destinationResultMap.keySet().size());
        // 500 should be there with count 1, crossedOff
        Assert.assertTrue(destinationResultMap.containsKey(500L));
        ItemEntity testElement = destinationResultMap.get(500L);
        Assert.assertEquals(new Long(1), Long.valueOf(testElement.getUsedCount()));
        Assert.assertNotNull(testElement.getCrossedOff());
        // 502 should be there with a count of 2
        testElement = destinationResultMap.get(502L);
        Assert.assertEquals(new Long(2), Long.valueOf(testElement.getUsedCount()));
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

        MvcResult result = this.mockMvc.perform(put(url)
                .with(user(meUserDetails))
                .contentType(contentType)
                .content(jsonProperties))
                .andExpect(status().isOk())
                .andReturn();

        // now, retrieve the list
        ShoppingListEntity sourceResultList = shoppingListService.getListById(TestConstants.USER_3_NAME, sourceListId);
        Map<Long, ItemEntity> allSourceResultMap = sourceResultList.getItems().stream()
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Map<Long, ItemEntity> withoutRemovedResultMap = sourceResultList.getItems().stream()
                .filter(item -> item.getRemovedOn() == null)
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assert.assertNotNull(allSourceResultMap);
        Assert.assertEquals(4, allSourceResultMap.keySet().size());
        Assert.assertEquals(2, withoutRemovedResultMap.keySet().size());
        // 505 shouldn't be there
        Assert.assertFalse(withoutRemovedResultMap.keySet().contains(505L));
        // check destination list
        ShoppingListEntity destinationResultList = shoppingListService.getListById(TestConstants.USER_3_NAME, destinationListId);
        Map<Long, ItemEntity> destinationResultMap = destinationResultList.getItems().stream()
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Assert.assertNotNull(destinationResultMap);
        Assert.assertEquals(4, destinationResultMap.keySet().size());
        // 505 should be there with count 2, not crossedOff
        Assert.assertTrue(destinationResultMap.containsKey(505L));
        ItemEntity testElement = destinationResultMap.get(505L);
        Assert.assertEquals(new Long(2), Long.valueOf(testElement.getUsedCount()));
        Assert.assertNull(testElement.getCrossedOff());
        // 502 should be there with a count of 2
        testElement = destinationResultMap.get(502L);
        Assert.assertEquals(new Long(2), Long.valueOf(testElement.getUsedCount()));
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
        ShoppingListEntity sourceResultList = shoppingListService.getListById(TestConstants.USER_3_NAME, sourceListId);
        Map<Long, ItemEntity> allSourceResultMap = sourceResultList.getItems().stream()
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Map<Long, ItemEntity> withoutRemovedResultMap = sourceResultList.getItems().stream()
                .filter(item -> item.getRemovedOn() == null)
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));

        Assert.assertNotNull(allSourceResultMap);
        Assert.assertEquals(4, allSourceResultMap.keySet().size());
        Assert.assertEquals(2, withoutRemovedResultMap.keySet().size());
        // 500 shouldn't be there
        Assert.assertFalse(withoutRemovedResultMap.keySet().contains(500L));

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

        String url = "/shoppinglist/" + sourceListId + "/item";

        MvcResult result = this.mockMvc.perform(put(url)
                .with(user(meUserDetails))
                .contentType(contentType)
                .content(jsonProperties))
                .andExpect(status().isOk())
                .andReturn();

        // now, retrieve the list
        ShoppingListEntity sourceResultList = shoppingListService.getListById(TestConstants.USER_3_NAME, sourceListId);
        Map<Long, ItemEntity> allSourceResultMap = sourceResultList.getItems().stream()
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Map<Long, ItemEntity> withoutRemovedResultMap = sourceResultList.getItems().stream()
                .filter(item -> item.getRemovedOn() == null)
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));

        Assert.assertNotNull(allSourceResultMap);
        Assert.assertEquals(3, allSourceResultMap.keySet().size());
        Assert.assertEquals(1, withoutRemovedResultMap.keySet().size());
        // 500 shouldn't be there
        Assert.assertFalse(withoutRemovedResultMap.keySet().contains(500L));
        Assert.assertFalse(withoutRemovedResultMap.keySet().contains(502L));
        Assert.assertTrue(withoutRemovedResultMap.keySet().contains(501L));

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

        MvcResult result = this.mockMvc.perform(put(url)
                .with(user(meUserDetails))
                .contentType(contentType)
                .content(jsonProperties))
                .andExpect(status().isOk())
                .andReturn();

        // now, retrieve the list
        ShoppingListEntity sourceResultList = shoppingListService.getListById(TestConstants.USER_3_NAME, sourceListId);
        Map<Long, ItemEntity> allSourceResultMap = sourceResultList.getItems().stream()
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));
        Map<Long, ItemEntity> withoutRemovedResultMap = sourceResultList.getItems().stream()
                .filter(item -> item.getRemovedOn() == null)
                .collect(Collectors.toMap(item -> item.getTag().getId(), Function.identity()));

        Assert.assertNotNull(allSourceResultMap);
        Assert.assertEquals(3, allSourceResultMap.keySet().size());
        Assert.assertEquals(0, withoutRemovedResultMap.keySet().size());
        // 500 shouldn't be there
        Assert.assertFalse(withoutRemovedResultMap.keySet().contains(500L));
        Assert.assertFalse(withoutRemovedResultMap.keySet().contains(502L));
        Assert.assertFalse(withoutRemovedResultMap.keySet().contains(501L));

    }

    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }


}
