package com.meg.listshop.lmt.api.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meg.listshop.Application;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.CustomUserDetails;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.api.model.v2.*;
import com.meg.listshop.test.TestConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.Matchers.isA;
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
@Sql(value = {"/sql/com/meg/atable/lmt/api/v2/DishRestControllerTest.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sql/com/meg/atable/lmt/api/v2/DishRestControllerTest_rollback.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class V2DishRestControllerTest {

    @ClassRule
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    public static final Comparator<DishResource> CREATEDON = Comparator.comparing((DishResource o) -> o.getDish().getId());

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype());
    private MockMvc mockMvc;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;
    private UserDetails userDetails;
    private String urlRoot = "/v2/dish/";

    private Long dishIdNoAmounts = 9999992L;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserService userService;

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

        UserEntity userAccount = userService.getUserByUserEmail(TestConstants.USER_3_NAME);
        userDetails = new CustomUserDetails(userAccount.getId(),
                TestConstants.USER_3_NAME,
                null,
                null,
                null,
                true,
                null);

    }


    @Test
    @WithMockUser
    public void readSingleDishNoAmounts() throws Exception {
        Long testId = dishIdNoAmounts;
        MvcResult result = mockMvc.perform(get(urlRoot
                        + testId)
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.dish.dish_id", isA(Number.class)))
                .andExpect(jsonPath("$.dish.dish_id").value(testId))
                .andReturn();
        Assert.assertNotNull(result);
    }

    @Test
    @WithMockUser
    public void readSingleDish_ObjectNotFoundException() throws Exception {
        Long testId = TestConstants.DISH_7_ID;
        MvcResult result = mockMvc.perform(get(urlRoot
                        + testId)
                        .with(user(userDetails)))
                .andExpect(status().isNotFound())
                .andReturn();

    }


    @Test
    @WithMockUser
    public void testAddIngredientToDish() throws Exception {
        Dish dish = new Dish();
        dish.setDishName("Yummy new dish");


        Long testId = createDish(userDetails, dish);

        IngredientPut ingredientPut = new IngredientPut();
        ingredientPut.setTagId("12");
        ingredientPut.setWholeQuantity(1);
        ingredientPut.setFractionalQuantity("OneHalf");
        ingredientPut.setUnitId("1000");
        String payload = json(ingredientPut);
        String url = urlRoot + testId + "/ingredients";
        this.mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .content(payload)
                        .contentType(contentType))
                .andDo(print())
                .andExpect(status().isNoContent());

        Dish dishResult = retrieveDish(userDetails, testId);
        Assert.assertNotNull(dishResult);
        Assert.assertEquals("should be 1 ingredient", 1, dishResult.getIngredients().size());
        Ingredient ingredient = dishResult.getIngredients().get(0);
        Assert.assertEquals(ingredient.getUnitId(), ingredientPut.getUnitId());
        Assert.assertEquals(ingredient.getWholeQuantity(), ingredientPut.getWholeQuantity());
        Assert.assertEquals(ingredient.getFractionalQuantity(), ingredientPut.getFractionalQuantity());
        Assert.assertEquals("1 1/2", ingredient.getQuantityDisplay());
    }


    @Test
    @WithMockUser
    public void testUpdateIngredientInDish() throws Exception {
        Dish dish = new Dish();
        dish.setDishName("update new dish");

        Long testId = createDish(userDetails, dish);

        IngredientPut ingredientPut = new IngredientPut();
        ingredientPut.setTagId("12");
        ingredientPut.setWholeQuantity(1);
        ingredientPut.setFractionalQuantity("OneHalf");
        ingredientPut.setUnitId("1000");
        String payload = json(ingredientPut);
        String url = urlRoot + testId + "/ingredients";
        this.mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .content(payload)
                        .contentType(contentType))
                .andDo(print())
                .andExpect(status().isNoContent());

        Dish dishResult = retrieveDish(userDetails, testId);
        Assert.assertNotNull(dishResult);
        Assert.assertEquals("should be 1 ingredient", 1, dishResult.getIngredients().size());
        Ingredient ingredient = dishResult.getIngredients().get(0);
        String ingredientId = ingredient.getId();

        // now - update it
        IngredientPut ingredientUpdate = new IngredientPut();
        ingredientUpdate.setId(ingredientId);
        ingredientUpdate.setTagId("12");
        ingredientUpdate.setWholeQuantity(101);
        ingredientUpdate.setFractionalQuantity(null);
        ingredientUpdate.setUnitId("1011");
        payload = json(ingredientUpdate);

        this.mockMvc.perform(put(url)
                        .with(user(userDetails))
                        .content(payload)
                        .contentType(contentType))
                .andDo(print())
                .andExpect(status().isNoContent());

        dishResult = retrieveDish(userDetails, testId);
        Assert.assertNotNull(dishResult);
        Assert.assertEquals("should be 1 ingredient", 1, dishResult.getIngredients().size());
        ingredient = dishResult.getIngredients().get(0);

        Assert.assertEquals(ingredient.getUnitId(), ingredientUpdate.getUnitId());
        Assert.assertEquals(ingredient.getWholeQuantity(), ingredientUpdate.getWholeQuantity());
        Assert.assertEquals(ingredient.getFractionalQuantity(), ingredientUpdate.getFractionalQuantity());
        Assert.assertEquals("101", ingredient.getQuantityDisplay());
    }

    @Test
    @WithMockUser
    public void testDeleteIngredientFromDish() throws Exception {
        Dish dish = new Dish();
        dish.setDishName("update new dish");

        Long testId = createDish(userDetails, dish);

        IngredientPut ingredientPut = new IngredientPut();
        ingredientPut.setTagId("12");
        ingredientPut.setWholeQuantity(1);
        ingredientPut.setFractionalQuantity("OneHalf");
        ingredientPut.setUnitId("1000");
        String payload = json(ingredientPut);
        String url = urlRoot + testId + "/ingredients";
        this.mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .content(payload)
                        .contentType(contentType))
                .andDo(print())
                .andExpect(status().isNoContent());

        Dish dishResult = retrieveDish(userDetails, testId);
        Assert.assertNotNull(dishResult);
        Assert.assertEquals("should be 1 ingredient", 1, dishResult.getIngredients().size());
        Ingredient ingredient = dishResult.getIngredients().get(0);
        String ingredientId = ingredient.getId();

        // now - delete it
        String deleteUrl = urlRoot + testId + "/ingredients/" + ingredientId;
        this.mockMvc.perform(delete(deleteUrl)
                        .with(user(userDetails))
                        .content(payload)
                        .contentType(contentType))
                .andDo(print())
                .andExpect(status().isNoContent());

        dishResult = retrieveDish(userDetails, testId);
        Assert.assertNotNull(dishResult);
        Assert.assertEquals("ingredients should be empty", 0, dishResult.getIngredients().size());
    }


    @Test
    @WithMockUser
    public void testGetIngredientsForDish() throws Exception {
        Dish dish = new Dish();
        dish.setDishName("Yummy new dish");


        Long testId = createDish(userDetails, dish);

        IngredientPut ingredientPut = new IngredientPut();
        ingredientPut.setTagId("12");
        ingredientPut.setWholeQuantity(1);
        ingredientPut.setFractionalQuantity("OneHalf");
        ingredientPut.setUnitId("1000");
        String payload = json(ingredientPut);
        String url = urlRoot + testId + "/ingredients";
        this.mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .content(payload)
                        .contentType(contentType))
                .andDo(print())
                .andExpect(status().isNoContent());

        ingredientPut.setTagId("112");
        ingredientPut.setWholeQuantity(12);
        ingredientPut.setUnitId("1000");
        payload = json(ingredientPut);

        this.mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .content(payload)
                        .contentType(contentType))
                .andDo(print())
                .andExpect(status().isNoContent());

        ingredientPut.setTagId("113");
        ingredientPut.setWholeQuantity(13);
        ingredientPut.setUnitId("1011");
        payload = json(ingredientPut);

        this.mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .content(payload)
                        .contentType(contentType))
                .andDo(print())
                .andExpect(status().isNoContent());

        // now get the ingredients
        MvcResult listResultsAfter = this.mockMvc.perform(get(urlRoot + testId + "/ingredients")
                        .with(user(userDetails)))
                .andReturn();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonList = listResultsAfter.getResponse().getContentAsString();
        IngredientListResource afterList = objectMapper.readValue(jsonList, IngredientListResource.class);
        Assert.assertNotNull(afterList);
        Assert.assertEquals(3, afterList.getEmbeddedList().getIngredientResourceList().size());
    }


    private Long createDish(UserDetails userDetails, Dish dish) throws Exception {
        String dishJson = json(dish);

        String url = "/dish";

        MvcResult result = this.mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .contentType(contentType)
                        .content(dishJson))
                .andExpect(status().isCreated())
                .andReturn();

        List<String> headers = result.getResponse().getHeaders("Location");
        String header = headers.get(0);
        String stringId = header.substring(header.lastIndexOf("/") + 1);
        return Long.valueOf(stringId);
    }

    private Dish retrieveDish(UserDetails userDetails, Long dishId) throws Exception {
        MvcResult listResultsAfter = this.mockMvc.perform(get(urlRoot + dishId)
                        .with(user(userDetails)))
                .andReturn();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonList = listResultsAfter.getResponse().getContentAsString();
        DishResource afterList = objectMapper.readValue(jsonList, DishResource.class);
        Assert.assertNotNull(afterList);
        return afterList.getDish();
    }


    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
