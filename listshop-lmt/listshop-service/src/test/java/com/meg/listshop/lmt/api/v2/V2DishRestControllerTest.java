package com.meg.listshop.lmt.api.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meg.listshop.Application;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.CustomUserDetails;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.api.model.FractionType;
import com.meg.listshop.lmt.api.model.v2.*;
import com.meg.listshop.test.TestConstants;
import io.swagger.v3.core.util.ObjectMapperFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
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
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@Testcontainers
@ActiveProfiles("test")
@Sql(value = {"/sql/com/meg/atable/lmt/api/v2/DishRestControllerTest.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sql/com/meg/atable/lmt/api/v2/DishRestControllerTest_rollback.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class V2DishRestControllerTest {

    @Container
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype());
    private final String urlRoot = "/v2/dish/";
    private MockMvc mockMvc;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;
    private UserDetails userDetails;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserService userService;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        this.mappingJackson2HttpMessageConverter = Arrays.stream(converters)
                .filter(MappingJackson2HttpMessageConverter.class::isInstance)
                .findAny()
                .orElse(null);

    }

    @BeforeEach
    @WithMockUser
    void setup()  {
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
    void readSingleDishNoAmounts() throws Exception {
        Long testId = 9999992L;
        Dish afterDish = retrieveDish(userDetails, testId);
        Assertions.assertEquals(String.valueOf(testId), afterDish.getDishId());
    }

    @Test
    @WithMockUser
    void readSingleDishAmounts() throws Exception {
        Long testId = 9999993L;
        Dish result = retrieveDish(userDetails, testId);

        Assertions.assertEquals(String.valueOf(testId), result.getDishId());
        // test collections
        Assertions.assertEquals(6, result.getIngredients().size(), "Ingredient size is wrong");
        Assertions.assertEquals(3, result.getTags().size(), "Tag size is wrong");
        Assertions.assertEquals(4, result.getRatings().size(), "Rating size is wrong");
        // test ingredient
        Ingredient cheddarCheese = result.getIngredients().stream()
                .filter(i -> i.getTag().getTagId().equals("18"))
                .findFirst()
                .orElse(null);
        Assertions.assertNotNull(cheddarCheese, "Cheddar cheese ingredient not found");
        Assertions.assertEquals("cheddar cheese", cheddarCheese.getTag().getName());
        Assertions.assertEquals("18", cheddarCheese.getTag().getTagId());

        Assertions.assertEquals(1.5, cheddarCheese.getAmount().getQuantity(),"quantity is wrong");
        Assertions.assertEquals(1, cheddarCheese.getAmount().getWholeQuantity(), "whole quantity is wrong");
        Assertions.assertEquals("OneHalf", cheddarCheese.getAmount().getFractionalQuantity(), "fractional quantity is wrong");
        Assertions.assertEquals("1 1/2", cheddarCheese.getAmount().getQuantityDisplay(), "quantity display is wrong");
        Assertions.assertEquals("1008", cheddarCheese.getAmount().getUnitId(), "unit id is wrong");
        Assertions.assertEquals("lb", cheddarCheese.getAmount().getUnitDisplay(), "unit display is wrong");
        Assertions.assertEquals("1 1/2 pound", cheddarCheese.getAmount().getDisplay(), "display is wrong");
        List<String> modifiers = cheddarCheese.getAmount().getModifiers();
        Assertions.assertEquals(1, modifiers.size(), "modifiers size is wrong");
        // test tags
        NestedTag nestedTag = result.getTags().stream()
                .filter(t -> t.getTagId().equals("199"))
                .findFirst()
                .orElse(null);
        Assertions.assertNotNull(nestedTag, "tag 199 not found");
        Assertions.assertEquals("Vegetarian", nestedTag.getName());
        // test ratings
        RatingInfo tasty = result.getRatings().stream()
                .filter(r -> r.getTag().getTagId().equals("391"))
                .findFirst()
                .orElse(null);
        Assertions.assertNotNull(tasty, "tasty rating not found");
        Assertions.assertEquals("Taste Factor", tasty.getTag().getName(), "tag name is wrong");
        Assertions.assertEquals(1, tasty.getPower(), "tasty rating power is wrong");
        Assertions.assertEquals(5, tasty.getMaxPower(), "tasty rating max power is wrong");

    }

    @Test
    @WithMockUser
    void readSingleDish_ObjectNotFoundException() throws Exception {
        Long testId = 99999999394L;
        mockMvc.perform(get(urlRoot
                        + testId)
                        .with(user(userDetails)))
                .andExpect(status().isNotFound()) ;
    }


    @Test
    @WithMockUser
    void testAddIngredientToDish() throws Exception {
        Dish dish = new Dish()
                .withDishName("Yummy new dish");


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
        Assertions.assertNotNull(dishResult);
        Assertions.assertEquals(1, dishResult.getIngredients().size(), "should be 1 ingredient");
        Ingredient ingredient = dishResult.getIngredients().get(0);
        Assertions.assertEquals(ingredient.getAmount().getUnitId(), ingredientPut.getUnitId());
        Assertions.assertEquals(ingredient.getAmount().getWholeQuantity(), ingredientPut.getWholeQuantity());
        Assertions.assertEquals(ingredientPut.getFractionalQuantity(),ingredient.getAmount().getFractionalQuantity());
        Assertions.assertEquals("1 1/2", ingredient.getAmount().getQuantityDisplay());
    }


    @Test
    @WithMockUser
    void testUpdateIngredientInDish() throws Exception {
        Dish dish = new Dish().withDishName("update new dish");

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
        Assertions.assertNotNull(dishResult);
        Assertions.assertEquals(1, dishResult.getIngredients().size(), "should be 1 ingredient");
        Ingredient ingredient = dishResult.getIngredients().get(0);
        String ingredientId = ingredient.getItemId();

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
        Assertions.assertNotNull(dishResult);
        Assertions.assertEquals(1, dishResult.getIngredients().size(), "should be 1 ingredient");
        ingredient = dishResult.getIngredients().get(0);

        Assertions.assertEquals(ingredient.getAmount().getUnitId(), ingredientUpdate.getUnitId());
        Assertions.assertEquals(ingredient.getAmount().getWholeQuantity(), ingredientUpdate.getWholeQuantity());
        Assertions.assertEquals(ingredient.getAmount().getFractionalQuantity(), ingredientUpdate.getFractionalQuantity());
        Assertions.assertEquals("101", ingredient.getAmount().getQuantityDisplay());
    }

    @Test
    @WithMockUser
    void testDeleteIngredientFromDish() throws Exception {
        Dish dish = new Dish().withDishName("update new dish");

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
        Assertions.assertNotNull(dishResult);
        Assertions.assertEquals(1, dishResult.getIngredients().size(), "should be 1 ingredient");
        Ingredient ingredient = dishResult.getIngredients().get(0);
        String ingredientId = ingredient.getItemId();

        // now - delete it
        String deleteUrl = urlRoot + testId + "/ingredients/" + ingredientId;
        this.mockMvc.perform(delete(deleteUrl)
                        .with(user(userDetails))
                        .content(payload)
                        .contentType(contentType))
                .andDo(print())
                .andExpect(status().isNoContent());

        dishResult = retrieveDish(userDetails, testId);
        Assertions.assertNotNull(dishResult);
        Assertions.assertEquals(0, dishResult.getIngredients().size(), "ingredients should be empty");
    }


    @Test
    @WithMockUser
    void testGetIngredientsForDish() throws Exception {
        Dish dish = new Dish().withDishName("Yummy new dish");


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
        IngredientList afterList = objectMapper.readValue(jsonList, IngredientList.class);
        Assertions.assertNotNull(afterList);
        Assertions.assertEquals(3, afterList.getIngredients().size());
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
        Dish afterDish = objectMapper.readValue(jsonList, Dish.class);
        Assertions.assertNotNull(afterDish);
        return afterDish;
    }


    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
