package com.meg.atable.api;

import com.meg.atable.Application;
import com.meg.atable.api.model.MealPlan;
import com.meg.atable.api.model.MealPlanType;
import com.meg.atable.api.model.ModelMapper;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.service.JwtUser;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.data.entity.DishEntity;
import com.meg.atable.data.entity.MealPlanEntity;
import com.meg.atable.service.DishService;
import com.meg.atable.service.MealPlanService;
import org.hamcrest.Matchers;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
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
public class MealPlanRestControllerTest {


    @Autowired
    private MealPlanService mealPlanService;

    @Autowired
    private DishService dishService;

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

    private Long mealPlanIdWithDish;
    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));
    private final List<MealPlanEntity> mealPlanList = new ArrayList<>();
    private MockMvc mockMvc;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;
    private UserAccountEntity userAccount;
    private String userName = "testname";
    private MealPlanEntity mealPlan;
    private UserDetails userDetails;
    private UserDetails userDetailsBad;
    private DishEntity dishEntity;

    @Before
    @WithMockUser
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();


        this.userAccount = userService.save(new UserAccountEntity(userName, "password"));
        userDetails = new JwtUser(this.userAccount.getId(),
                userName,
                null,
                null,
                null,
                true,
                null);

        MealPlanEntity mealPlanEntity = new MealPlanEntity();
        mealPlanEntity.setName("mealPlanOne");
        mealPlanEntity.setMealPlanType(MealPlanType.Manual);
        mealPlanEntity = mealPlanService.createMealPlan(userName,
                mealPlanEntity);
        this.mealPlanList.add(mealPlanEntity);
        MealPlanEntity mealPlanEntityTwo = new MealPlanEntity();
        mealPlanEntityTwo.setName("mealPlanTwo");
        mealPlanEntityTwo.setMealPlanType(MealPlanType.Manual);
        this.mealPlanList.add(mealPlanService.createMealPlan(userName,
                mealPlanEntityTwo));

        MealPlanEntity mealPlanEntityThree = new MealPlanEntity();
        mealPlanEntityThree.setName("mealPlanThree");
        mealPlanEntityThree.setMealPlanType(MealPlanType.Manual);

        dishEntity = new DishEntity(userAccount.getId(), "dishForMealplan");
        this.dishEntity = dishService.save(dishEntity);
        this.mealPlanService.addDishToMealPlan(userName, mealPlanEntityTwo.getId(), dishEntity.getId());
        this.mealPlanIdWithDish = mealPlanEntityTwo.getId();

        this.userAccount = userService.save(new UserAccountEntity("updateUser", "password"));
        userDetailsBad = new JwtUser(this.userAccount.getId(),
                "updateUser",
                null,
                null,
                null,
                true,
                null);
        this.mealPlan = mealPlanService.createMealPlan(userName, mealPlanEntityThree);

    }


    @Test
    @WithMockUser
    public void readSingleMealPlan() throws Exception {
        Long testId = this.mealPlanList.get(0).getId();
        mockMvc.perform(get("/mealplan/"
                + this.mealPlanList.get(0).getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.mealPlan.meal_plan_id", Matchers.isA(Number.class)))
                .andExpect(jsonPath("$.mealPlan.meal_plan_id").value(testId));

    }

    @Test
    @WithMockUser
    public void readMealPlans() throws Exception {
        Long testId = this.mealPlanList.get(0).getId();
        Long testId2 = this.mealPlanList.get(1).getId();
        mockMvc.perform(get("/mealplan")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$._embedded.mealPlanResourceList", hasSize(2)))
                .andExpect(jsonPath("$._embedded.mealPlanResourceList[0].mealPlan.meal_plan_id").value(testId))
                .andExpect(jsonPath("$._embedded.mealPlanResourceList[0].mealPlan.name", is("mealPlanOne")))
                .andExpect(jsonPath("$._embedded.mealPlanResourceList[1].mealPlan.meal_plan_id").value(testId2))
                .andExpect(jsonPath("$._embedded.mealPlanResourceList[1].mealPlan.name", is("mealPlanTwo")));
    }

    @Test
    @WithMockUser
    public void testDeleteMealPlan() throws Exception {
        Long testId = this.mealPlanList.get(0).getId();
        mockMvc.perform(delete("/mealplan/"
                + this.mealPlanList.get(0).getId())
                .with(user(userDetails)))
                .andExpect(status().isNoContent());

    }

    @Test
    @WithMockUser
    public void testCreateMealPlan() throws Exception {
        JwtUser createUserDetails = new JwtUser(userAccount.getId(),
                userName,
                null,
                null,
                null,
                true,
                null);
        MealPlanEntity mealPlanEntity = new MealPlanEntity();
        mealPlanEntity.setName("mealPlanCreate");
        mealPlanEntity.setMealPlanType(MealPlanType.Manual);
        mealPlanEntity.setUserId(userAccount.getId());
        MealPlan mealPlan = ModelMapper.toModel(mealPlanEntity);
        String mealPlanJson = json(mealPlan);

        this.mockMvc.perform(post("/mealplan")
                .with(user(createUserDetails))
                .contentType(contentType)
                .content(mealPlanJson))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    public void testAddDishToMealPlan() throws Exception {
        String url = "/mealplan/" + this.mealPlan.getId()
                + "/dish/" + this.dishEntity.getId();
        this.mockMvc.perform(post(url)
                .with(user(userDetails))
                .contentType(contentType))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    public void testRemoveDishFromMealPlan() throws Exception {
        String url = "/mealplan/" + this.mealPlanIdWithDish
                + "/dish/" + this.dishEntity.getId();
        this.mockMvc.perform(delete(url)
                .with(user(userDetails))
                .contentType(contentType))
                .andExpect(status().isNoContent());
    }


    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    // MM NEXT UP - add dish
    /*
    @Test
    @Ignore
    @WithMockUser
    public void updateMealPlan() throws Exception {
        MealPlanEntity toUpdate = this.mealPlanList.get(0);
        String updateName = "updated:" + mealPlan.getMealPlanName();
        String updateDescription = "updated:" + (mealPlan.getDescription() == null ? "" : mealPlan.getDescription());
        toUpdate.setMealPlanName(updateName);
        toUpdate.setDescription(updateDescription);
        toUpdate.setUserId(userAccount.getLayoutId());

        String mealPlanJson = json(toUpdate);

        this.mockMvc.perform(put("/mealPlan/" + mealPlan.getLayoutId())
                .with(user(userDetailsBad))
                .contentType(contentType)
                .content(mealPlanJson))
                .andExpect(status().is2xxSuccessful());
    }


    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
*/
}
