package com.meg.atable.api;

import com.meg.atable.Application;
import com.meg.atable.api.model.Dish;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.service.JwtUser;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.data.entity.DishEntity;
import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.service.DishService;
import com.meg.atable.test.TestConstants;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
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
public class DishRestControllerTest {

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));
    private MockMvc mockMvc;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;
    private UserDetails userDetails;

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

    @Before
    @WithMockUser
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        UserAccountEntity userAccount = userService.getUserByUserName(TestConstants.USER_3_NAME);
        userDetails = new JwtUser(userAccount.getId(),
                TestConstants.USER_3_NAME,
                null,
                null,
                null,
                true,
                null);

    }


    @Test
    @WithMockUser
    public void readSingleDish() throws Exception {
        Long testId = TestConstants.DISH_1_ID;
        mockMvc.perform(get("/dish/"
                + testId)
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.dish.dish_id", Matchers.isA(Number.class)))
                .andExpect(jsonPath("$.dish.dish_id").value(testId));

    }

    @Test
    @WithMockUser
    public void readDishes() throws Exception {
        mockMvc.perform(get("/dish")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(contentType));
    }

    @Test
    @WithMockUser
    public void createDish() throws Exception {

        String dishJson = json(new Dish(
                TestConstants.USER_3_ID, "created dish"));

        this.mockMvc.perform(post("/dish")
                .with(user(userDetails))
                .contentType(contentType)
                .content(dishJson))
                .andExpect(status().isCreated());
    }


    @Test
    @WithMockUser
    public void updateDish() throws Exception {
        DishEntity toUpdate = dishService.getDishForUserById(TestConstants.USER_3_NAME, TestConstants.DISH_1_ID);
        String updateName = "updated:" + toUpdate.getDishName();
        String updateDescription = "updated:" + (toUpdate.getDescription() == null ? "" : toUpdate.getDescription());
        toUpdate.setDishName(updateName);
        toUpdate.setDescription(updateDescription);
        toUpdate.setUserId(TestConstants.USER_3_ID);
toUpdate.setTags(new ArrayList<TagEntity>());
        String dishJson = json(toUpdate);

        this.mockMvc.perform(put("/dish/" + toUpdate.getId())
                .with(user(userDetails))
                .contentType(contentType)
                .content(dishJson))
                .andExpect(status().is2xxSuccessful());
    }


    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
