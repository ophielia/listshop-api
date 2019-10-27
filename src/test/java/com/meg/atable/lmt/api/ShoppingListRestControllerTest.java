package com.meg.atable.lmt.api;

import com.meg.atable.Application;
import com.meg.atable.auth.service.impl.JwtUser;
import com.meg.atable.lmt.api.model.Item;
import com.meg.atable.lmt.api.model.ListGenerateProperties;
import com.meg.atable.lmt.api.model.ShoppingList;
import com.meg.atable.test.TestConstants;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

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
    private MockMvc mockMvc;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        this.mappingJackson2HttpMessageConverter = Arrays.stream(converters)

                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null");
    }

    @Before
    @WithMockUser
    public void setup() throws Exception {

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

        mockMvc.perform(get("/shoppinglist/mostrecent")
                .with(user(meUserDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.shopping_list.list_id", Matchers.isA(Number.class)))
                .andExpect(jsonPath("$.shopping_list.list_id").value(testId));
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
        Long testId = TestConstants.LIST_1_ID;

        mockMvc.perform(get("/shoppinglist/" + TestConstants.LIST_1_ID)
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.shopping_list.list_id", Matchers.isA(Number.class)))
                .andExpect(jsonPath("$.shopping_list.list_id").value(testId));
    }

    @Test
    @WithMockUser
    public void testRetrieveListById_HighlightList() throws Exception {
        Long testId = 509990L;

        MvcResult result = mockMvc.perform(get("/shoppinglist/" + testId + "?highlightListId=509991")
                .with(user(meUserDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        // verify contains category highlight list
        Assert.assertTrue(jsonResponse.contains("\"category_type\":\"HighlightList\""));
        // asserts that the result contains the list source
        Assert.assertTrue(jsonResponse.contains("\"list_sources\":[{\"id\":509991,\"display\":\"added from\",\"type\":\"List\"}]"));

    }

    @Test
    @WithMockUser
    public void testUpdateList() throws Exception {
        Long testId = 509991L;

        ShoppingList shoppingList = new ShoppingList(testId)
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

        ShoppingList shoppingList = new ShoppingList(testId)
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
    public void testAddItemToList() throws Exception {
        String url = "/shoppinglist/" + TestConstants.LIST_1_ID
                + "/item";

        Item item = new Item()
                .tagId(String.valueOf(TestConstants.TAG_CARROTS));

        String itemJson = json(item);
        this.mockMvc.perform(post(url)
                .with(user(userDetails))
                .contentType(contentType)
                .content(itemJson))
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
        Long mealPlanId = TestConstants.MEAL_PLAN_1_ID;
        String url = "/shoppinglist/mealplan/" + mealPlanId;

        this.mockMvc.perform(post(url)
                .with(user(userDetails))
                .contentType(contentType))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    public void testNewCreateList() throws Exception {
        ListGenerateProperties properties = new ListGenerateProperties();
        properties.setRawListType("General");
        properties.setAddFromBase(true);
        properties.setAddFromPickup(true);
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
        Long listId = TestConstants.LIST_1_ID;
        String url = "/shoppinglist/" + listId + "/item/shop/" + TestConstants.ITEM_1_ID
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
        Assert.assertTrue(listAfterAdd.contains("\"list_sources\":[{\"id\":500,\"display\":\"Shopping List 1\",\"type\":\"List\"}]"));

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

        Long listId = TestConstants.LIST_1_ID;
        String url = "/shoppinglist/" + listId + "/item";
        mockMvc.perform(delete(url)
                .with(user(userDetails))
                .contentType(contentType))
                .andExpect(status().isNoContent());

    }


    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
