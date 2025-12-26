package com.meg.listshop.lmt.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meg.listshop.Application;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.CustomUserDetails;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.data.entity.MealPlanEntity;
import com.meg.listshop.test.TestConstants;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@Testcontainers
@WebAppConfiguration
@ActiveProfiles("test")
class MealPlanRestControllerTest {

    @Container
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    private static UserEntity userAccount;
    private static UserDetails differentAccount;
    private static UserDetails userDetails;
    private static UserDetails userDetailsDelete;
    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype());


    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private UserService userService;
    private MockMvc mockMvc;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        this.mappingJackson2HttpMessageConverter = Arrays.stream(converters)

                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        Assertions.assertNotNull("the JSON message converter must not be null");
    }

    @BeforeEach
    @WithMockUser
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        userAccount = userService.getUserByUserEmail(TestConstants.USER_3_NAME);
        var differentUser = userService.getUserByUserEmail(TestConstants.USER_2_NAME);
        String userName = TestConstants.USER_3_NAME;
        userDetails = new CustomUserDetails(userAccount.getId(),
                userName,
                null,
                null,
                null,
                true,
                null);
        differentAccount = new CustomUserDetails(differentUser.getId(),
                differentUser.getUsername(),
                differentUser.getEmail(),
                null,
                null,
                true,
                null);
        userDetailsDelete = new CustomUserDetails(TestConstants.USER_2_ID,
                TestConstants.USER_2_NAME,
                null,
                null,
                null,
                true,
                null);

    }


    @Test
    @WithMockUser
    void readSingleMealPlan() throws Exception {
        Long testId = TestConstants.MENU_PLAN_3_ID;
        mockMvc.perform(get("/mealplan/"
                        + testId)
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.meal_plan.meal_plan_id", Matchers.isA(Number.class)))
                .andExpect(jsonPath("$.meal_plan.meal_plan_id").value(testId))
                .andReturn();
    }

    @Test
    @WithMockUser
    @Sql(value = "/sql/com/meg/atable/lmt/api/MealPlanRestControllerTest.sql")
    @Sql(value = "/sql/com/meg/atable/lmt/api/MealPlanRestControllerTest_rollback.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void readMealPlanRatings() throws Exception {
        Long testId = 50485L;
        mockMvc.perform(get("/mealplan/"
                        + testId + "/ratings")
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.ratingUpdateInfo.dish_ratings", Matchers.hasSize(5)))
                .andDo(print());
    }


    @Test
    @WithMockUser
    void readMealPlans() throws Exception {
        MvcResult result = mockMvc.perform(get("/mealplan")
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$._embedded.mealPlanResourceList", Matchers.hasSize(Matchers.greaterThan(2))))
                .andReturn();


    }

    @Test
    @WithMockUser
    void readMealPlanBadUser() throws Exception {
        Long testId = TestConstants.MENU_PLAN_3_ID;
        mockMvc.perform(get("/mealplan" + "/" + testId)
                        .with(user(userDetailsDelete)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser
    void testDeleteMealPlan() throws Exception {
        Long testId = 506L; //TestConstants.MENU_PLAN_2_ID;
        mockMvc.perform(delete("/mealplan/"
                        + testId)
                        .with(user(userDetails)))
                .andExpect(status().isNoContent());

    }

    @Test
    @WithMockUser
    void testCreateMealPlan() throws Exception {

        MealPlanEntity mealPlanEntity = new MealPlanEntity();
        mealPlanEntity.setName("mealPlanCreate");
        mealPlanEntity.setMealPlanType(MealPlanType.Manual);
        mealPlanEntity.setUserId(userAccount.getId());
        MealPlan mealPlan = ModelMapper.toModel(mealPlanEntity, true);
        String mealPlanJson = json(mealPlan);

        this.mockMvc.perform(post("/mealplan")
                        .with(user(userDetails))
                        .contentType(contentType)
                        .content(mealPlanJson))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void testCreateMealPlan_EmptyName() throws Exception {
        MealPlanEntity mealPlanEntity = new MealPlanEntity();
        mealPlanEntity.setMealPlanType(MealPlanType.Manual);
        mealPlanEntity.setUserId(userAccount.getId());
        MealPlan mealPlan = ModelMapper.toModel(mealPlanEntity, true);
        String mealPlanJson = json(mealPlan);

        this.mockMvc.perform(post("/mealplan")
                        .with(user(userDetails))
                        .contentType(contentType)
                        .content(mealPlanJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.meal_plan.name", Matchers.isA(String.class)));
    }


    @Test
    @WithMockUser
    void testAddDishToMealPlan() throws Exception {
        String url = "/mealplan/" + TestConstants.MENU_PLAN_3_ID
                + "/dish/" + TestConstants.DISH_1_ID;
        this.mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .contentType(contentType))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void testAddDishToMealPlan_DishExistsKO() throws Exception {
        // dishid 500 exists for mealplan 503 in test data
        var dishId = "500";
        var mealPlanId = "503";
        String url = "/mealplan/" + mealPlanId
                + "/dish/" + dishId;
        this.mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .contentType(contentType))
                .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser
    void testRemoveDishFromMealPlan() throws Exception {
        String url = "/mealplan/" + TestConstants.MENU_PLAN_3_ID
                + "/dish/" + 501L;
        this.mockMvc.perform(delete(url)
                        .with(user(userDetails))
                        .contentType(contentType))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void testCreateMealPlanFromTargetProposal() throws Exception {
        String url = "/mealplan/proposal/" + TestConstants.PROPOSAL_3_ID;

        this.mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .contentType(contentType))
                .andExpect(status().isCreated());

    }

    @Test
    @WithMockUser
    void testRenameMealPlan() throws Exception {

        String url = "/mealplan/" + TestConstants.MENU_PLAN_3_ID + "/name/george";


        this.mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .contentType(contentType))
                .andExpect(status().isNoContent());

    }

    @Test
    @WithMockUser
    void testCopyMealPlan() throws Exception {
        Long copyMealPlan = 504L;
        String url = "/mealplan/" + copyMealPlan;
        Long startTime = new Date().getTime();

        MvcResult result = this.mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .contentType(contentType))
                .andExpect(header().exists("Location"))
                .andExpect(status().isCreated())
                .andReturn();

        // get header from result, to strip new id
        String locationHeader = (String) result.getResponse().getHeaderValue("Location");
        String newId = locationHeader.substring(locationHeader.lastIndexOf("/") + 1);

        // pull newly created mealplan
        MvcResult sourceResult = this.mockMvc.perform(get(url)
                        .with(user(userDetails))
                        .contentType(contentType))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult copiedResult = this.mockMvc.perform(get("/mealplan/" + newId)
                        .with(user(userDetails))
                        .contentType(contentType))
                .andExpect(status().isOk())
                .andReturn();

        MealPlanResource sourceResource = toMealPlanResource(sourceResult.getResponse().getContentAsString());
        MealPlanResource copiedResource = toMealPlanResource(copiedResult.getResponse().getContentAsString());

        MealPlan source = sourceResource.getMealPlan();
        MealPlan copied = copiedResource.getMealPlan();
        // check meal plan
        Assertions.assertNotNull(copied.getName(), "copied name should not be null");
        Assertions.assertNotEquals(copied.getName(), source.getName(), "copied name should not equal source");
        Assertions.assertNotNull(copied.getCreated(), "copied should have created");
        Assertions.assertTrue(copied.getCreated().getTime() >= startTime, "copied created should be after start");
        // check slots
        List<Slot> sourceSlots = source.getSlots();
        List<Slot> copiedSlots = copied.getSlots();
        Assertions.assertNotNull(copiedSlots, "slots should exist - copied");
        Assertions.assertNotNull(sourceSlots, "slots should exist - source");
        Assertions.assertEquals(sourceSlots.size(), copiedSlots.size(), "size should match");
        Map<Long, Dish> dishIdsInSource = sourceSlots.stream()
                .map(Slot::getDish)
                //.map(Dish::getId)
                .collect(Collectors.toMap(Dish::getId, Function.identity()));
        for (Slot slot : copiedSlots) {
            Assertions.assertEquals(newId, String.valueOf(slot.getMealPlanId()), "meal plan id should be correct");
            Assertions.assertTrue(dishIdsInSource.containsKey(slot.getDish().getId()), "dish id should match one in source");
        }
    }

    @Test
    @WithMockUser
    void testCopyMealPlan_BadUserKO() throws Exception {
        Long copyMealPlan = 504L;
        String url = "/mealplan/" + copyMealPlan;
        Long startTime = new Date().getTime();

        MvcResult result = this.mockMvc.perform(post(url)
                        .with(user(differentAccount))
                        .contentType(contentType))
                .andExpect(status().isBadRequest())
                .andReturn();

    }

    @Test
    @WithMockUser
    void testCopyMealPlan_BadMealPlanKO() throws Exception {
        Long copyMealPlan = 555504L;
        String url = "/mealplan/" + copyMealPlan;
        Long startTime = new Date().getTime();

        MvcResult result = this.mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .contentType(contentType))
                .andExpect(status().isNotFound())
                .andReturn();

    }

    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    private MealPlanResource toMealPlanResource(String input) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        MealPlanResource mealPlan = mapper.readValue(input, MealPlanResource.class);
        return mealPlan;
    }

}
