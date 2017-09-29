package com.meg.atable.api;

import com.meg.atable.Application;
import com.meg.atable.api.model.Dish;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.service.JwtUser;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.data.entity.DishEntity;
import com.meg.atable.service.DishService;
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
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    private UserAccountEntity userAccount;

    private DishEntity dish;

    private final List<DishEntity> dishList = new ArrayList<>();

    private UserDetails userDetails;
    private UserDetails userDetailsBad;

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


        String userName = "testname";
        this.userAccount = userService.save(new UserAccountEntity(userName, "password"));
        userDetails = new JwtUser(this.userAccount.getId(),
                "testname",
                null,
                null,
                null,
                true,
                null);

        this.dishList.add(dishService.save(new DishEntity(this.userAccount.getId(), "dish1")));
        this.dishList.add(dishService.save(new DishEntity(userAccount.getId(), "dish2")));

        this.userAccount = userService.save(new UserAccountEntity("updateUser", "password"));
        userDetailsBad = new JwtUser(this.userAccount.getId(),
                "updateUser",
                null,
                null,
                null,
                true,
                null);
        this.dish = dishService.save(new DishEntity(userAccount.getId(), "dish2"));

    }


    @Test
    @WithMockUser
    public void readSingleDish() throws Exception {
        Long testId = this.dishList.get(0).getId();
        mockMvc.perform(get("/dish/"
                + this.dishList.get(0).getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.dish.id", Matchers.isA(Number.class)))
                .andExpect(jsonPath("$.dish.id").value(testId));

    }

    @Test
    @WithMockUser
    public void readDishes() throws Exception {
        Long testId = this.dishList.get(0).getId();
        Long testId2 = this.dishList.get(1).getId();
        mockMvc.perform(get("/dish")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$._embedded.dishResourceList", hasSize(2)))
                .andExpect(jsonPath("$._embedded.dishResourceList[0].dish.id").value(testId))
                .andExpect(jsonPath("$._embedded.dishResourceList[0].dish.dishName", is("dish1")))
                .andExpect(jsonPath("$._embedded.dishResourceList[1].dish.id").value(testId2))
                .andExpect(jsonPath("$._embedded.dishResourceList[1].dish.dishName", is("dish2")));
    }

    @Test
    @WithMockUser
    public void createDish() throws Exception {

        UserAccountEntity createUserAccount = userService.save(new UserAccountEntity("createRecipe", "password"));
        JwtUser createUserDetails = new JwtUser(createUserAccount.getId(),
                "createRecipe",
                null,
                null,
                null,
                true,
                null);
        String dishJson = json(new Dish(
                this.userAccount.getId(), "created dish"));

        this.mockMvc.perform(post("/dish")
                .with(user(createUserDetails))
                .contentType(contentType)
                .content(dishJson))
                .andExpect(status().isCreated());
    }


    @Test
    @Ignore
    @WithMockUser
    public void updateDish() throws Exception {
        DishEntity toUpdate = this.dishList.get(0);
        String updateName = "updated:" + dish.getDishName();
        String updateDescription = "updated:" + (dish.getDescription() == null ? "" : dish.getDescription());
        toUpdate.setDishName(updateName);
        toUpdate.setDescription(updateDescription);
        toUpdate.setUserId(userAccount.getId());

        String dishJson = json(toUpdate);

        this.mockMvc.perform(put("/dish/" + dish.getId())
                .with(user(userDetailsBad))
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
