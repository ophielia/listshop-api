package com.meg.atable.api;

import com.meg.atable.Application;
import com.meg.atable.model.Dish;
import com.meg.atable.model.User;
import com.meg.atable.service.DishService;
import com.meg.atable.service.UserService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class DishRestControllerTest {

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MediaType contentTypeWithHal = new MediaType(MediaType.APPLICATION_JSON.getType(),
            "hal+json",
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    private final String userName = "testname";

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private User user;

    private Dish dish;

    private final List<Dish> dishList = new ArrayList<>();

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
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();

        this.dishService.deleteAll();
        this.userService.deleteAll();

        this.user = userService.save(new User(userName, "password"));
        this.dishList.add(dishService.save(new Dish(user, "dish1")));
        this.dishList.add(dishService.save(new Dish(user, "dish2")));
    }

    @Test
    public void userNotFound() throws Exception {
        mockMvc.perform(post("/george/dish")
                .content(this.json(new Dish()))
                .contentType(contentType))
                .andExpect(status().isNotFound());

    }


    @Test
    public void readSingleDish() throws Exception {
        Long testId = this.dishList.get(0).getId();
        Class<Number> targetType = Number.class;
        mockMvc.perform(get("/" + this.userName + "/dish/"
                + this.dishList.get(0).getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.dish.id", Matchers.isA(Number.class)))
                .andExpect(jsonPath("$.dish.id").value(testId));

    }

    @Test
    public void readDishes() throws Exception {
        Long testId = this.dishList.get(0).getId();
        Long testId2 = this.dishList.get(1).getId();
        mockMvc.perform(get("/" + userName + "/dish"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$._embedded.dishResourceList", hasSize(2)))
                .andExpect(jsonPath("$._embedded.dishResourceList[0].dish.id").value(testId))
                .andExpect(jsonPath("$._embedded.dishResourceList[0].dish.dishName", is("dish1")))
                .andExpect(jsonPath("$._embedded.dishResourceList[1].dish.id").value(testId2))
                .andExpect(jsonPath("$._embedded.dishResourceList[1].dish.dishName", is("dish2")));
    }

    @Test
    public void createDish() throws Exception {
        String dishJson = json(new Dish(
                this.user, "created dish"));

        this.mockMvc.perform(post("/" + userName + "/dish")
                .contentType(contentType)
                .content(dishJson))
                .andExpect(status().isCreated());
    }


    @Test
    public void updateDish() throws Exception {
        Dish toUpdate = this.dishList.get(0);
        String updateName = "updated:" + toUpdate.getDishName();
        String updateDescription = "updated:" + (toUpdate.getDescription()==null?"":toUpdate.getDescription());
        toUpdate.setDishName(updateName);
        toUpdate.setDescription(updateDescription);

        String dishJson = json(toUpdate);

        this.mockMvc.perform(put("/" + userName + "/dish/" + toUpdate.getId())
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
