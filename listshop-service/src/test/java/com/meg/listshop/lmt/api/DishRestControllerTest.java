package com.meg.listshop.lmt.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meg.listshop.Application;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.auth.service.impl.JwtUser;
import com.meg.listshop.common.FlatStringUtils;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.api.model.Dish;
import com.meg.listshop.lmt.data.entity.DishEntity;
import com.meg.listshop.lmt.model.EmbeddedDishResourceList;
import com.meg.listshop.lmt.model.ResultDishResource;
import com.meg.listshop.lmt.service.DishService;
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
import org.springframework.mock.web.MockHttpServletResponse;
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
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
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
@Sql(value = {"/sql/com/meg/atable/lmt/api/DishRestControllerTest.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sql/com/meg/atable/lmt/api/DishRestControllerTest_rollback.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class DishRestControllerTest {

    @ClassRule
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    public static final Comparator<ResultDishResource> DISHNAME = (ResultDishResource o1, ResultDishResource o2) -> o1.getDish().getDishName().toLowerCase().compareTo(o2.getDish().getDishName().toLowerCase());
    public static final Comparator<ResultDishResource> CREATEDON = (ResultDishResource o1, ResultDishResource o2) -> o1.getDish().getId().compareTo(o2.getDish().getId());

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype());
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

        UserEntity userAccount = userService.getUserByUserEmail(TestConstants.USER_3_NAME);
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
                .andExpect(jsonPath("$.dish.dish_id", isA(Number.class)))
                .andExpect(jsonPath("$.dish.dish_id").value(testId));

    }

    @Test
    @WithMockUser
    public void readSingleDish_ObjectNotFoundException() throws Exception {
        //MM work here
        Long testId = TestConstants.DISH_7_ID;
        MvcResult result = mockMvc.perform(get("/dish/"
                + testId)
                .with(user(userDetails)))
                .andExpect(status().isBadRequest())
                .andReturn();

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
        Dish updateDish = new Dish(toUpdate.getUserId(), updateName);
        updateDish.reference("reference");
        updateDish.description(updateDescription);
        String dishJson = json(updateDish);

        this.mockMvc.perform(put("/dish/" + toUpdate.getId())
                .with(user(userDetails))
                .contentType(contentType)
                .content(dishJson))
                .andExpect(status().is2xxSuccessful());

        DishEntity result = dishService.getDishForUserById(TestConstants.USER_3_NAME, TestConstants.DISH_1_ID);
        Assert.assertEquals(updateName, result.getDishName());
        Assert.assertEquals(updateDescription, result.getDescription());
        Assert.assertEquals("reference", result.getReference());
    }

    @Test
    @WithMockUser
    public void testGetTagsByDishId() throws Exception {
        mockMvc.perform(get("/dish/" + TestConstants.DISH_2_ID + "/tag")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(contentType));

    }

    @Test
    @WithMockUser
    public void testAddTagToDish() throws Exception {
        String url = "/dish/" + TestConstants.DISH_1_ID + "/tag/" + TestConstants.TAG_CARROTS;
        this.mockMvc.perform(post(url)
                .with(user(userDetails))
                .contentType(contentType))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    public void testDeleteTagFromDish() throws Exception {
        String url = "/dish/" + TestConstants.DISH_1_ID + "/tag/344";
        this.mockMvc.perform(delete(url)
                .with(user(userDetails))
                .contentType(contentType))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    public void testAddAndRemoveTags() throws Exception {
        List<Long> addTags = Arrays.asList(TestConstants.TAG_1_ID, TestConstants.TAG_2_ID, TestConstants.TAG_3_ID);
        List<Long> deleteTags = Arrays.asList(55L, 104L);

        String addList = FlatStringUtils.flattenListOfLongsToString(addTags, ",");
        String deleteList = FlatStringUtils.flattenListOfLongsToString(deleteTags, ",");
        String url = "/dish/" + TestConstants.DISH_1_ID + "/tag?addTags=" + addList + "&removeTags=" + deleteList;
        this.mockMvc.perform(put(url)
                .with(user(userDetails))
                .contentType(contentType))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    public void testFindDishes() throws Exception {
        List<Long> excludedTags = Arrays.asList(TestConstants.TAG_3_ID);
        List<Long> includedTags = Arrays.asList(TestConstants.TAG_PASTA);

        String includedList = FlatStringUtils.flattenListOfLongsToString(includedTags, ",");
        String excludedList = FlatStringUtils.flattenListOfLongsToString(excludedTags, ",");
        String url = "/dish?includedTags=" + includedList + "&excludedTags=" + excludedList
                + "&sortKey=Name" + "&sortDirection=ASC";
        MvcResult result = this.mockMvc.perform(get(url)
                .with(user(userDetails))
                .contentType(contentType))
                .andExpect(content().contentType(contentType))
                .andReturn();

        MockHttpServletResponse response = result.getResponse();

        ObjectMapper mapper = new ObjectMapper();
        EmbeddedDishResourceList embeddedList = mapper.readValue(result.getResponse().getContentAsString(), EmbeddedDishResourceList.class);
        ResultDishResource[] dishList = embeddedList.getEmbeddedList() != null ? embeddedList.getEmbeddedList().getDishList() : new ResultDishResource[0];
        // sort list by name, asc
        ResultDishResource[] sortedResults = dishList;
        Arrays.sort(sortedResults, DISHNAME);

        // list as received
        List<String> listAsReceived = Arrays.asList(dishList)
                .stream()
                .map(rdr -> rdr.getDish().getDishName())
                .collect(Collectors.toList());
        assertNotNull(listAsReceived);
        // list as expected
        List<String> listAsExpected = Arrays.asList(sortedResults)
                .stream()
                .map(rdr -> rdr.getDish().getDishName())
                .collect(Collectors.toList());
        ;
        assertNotNull(listAsExpected);

        // order matches
        assertThat(listAsReceived, equalTo(listAsExpected));

        // check sort by created, desc
        url = "/dish?includedTags=" + includedList + "&excludedTags=" + excludedList
                + "&sortKey=CreatedOn" + "&sortDirection=DESC";
        result = this.mockMvc.perform(get(url)
                .with(user(userDetails))
                .contentType(contentType))
                .andExpect(content().contentType(contentType))
                .andReturn();

        embeddedList = mapper.readValue(result.getResponse().getContentAsString(), EmbeddedDishResourceList.class);
        dishList = embeddedList.getEmbeddedList() != null ? embeddedList.getEmbeddedList().getDishList() : new ResultDishResource[0];
        // sort list by id, desc
        sortedResults = dishList;
        Arrays.sort(sortedResults, CREATEDON.reversed());

        // list as received
        listAsReceived = Arrays.asList(dishList)
                .stream()
                .map(rdr -> String.valueOf(rdr.getDish().getId()))
                .collect(Collectors.toList());
        assertNotNull(listAsReceived);
        // list as expected
        listAsExpected = Arrays.asList(sortedResults)
                .stream()
                .map(rdr -> String.valueOf(rdr.getDish().getId()))
                .collect(Collectors.toList());
        ;
        assertNotNull(listAsExpected);

        // order matches
        assertThat(listAsReceived, equalTo(listAsExpected));

    }

    @Test
    @WithMockUser
    public void testFindDishesOrig() throws Exception {
        List<Long> excludedTags = Arrays.asList(TestConstants.TAG_1_ID, TestConstants.TAG_2_ID, TestConstants.TAG_3_ID);
        List<Long> includedTags = Arrays.asList(TestConstants.TAG_MEAT, TestConstants.TAG_PASTA);

        String includedList = FlatStringUtils.flattenListOfLongsToString(includedTags, ",");
        String excludedList = FlatStringUtils.flattenListOfLongsToString(excludedTags, ",");
        String url = "/dish?includedTags=" + includedList + "&excludedTags=" + excludedList;
        this.mockMvc.perform(get(url)
                        .with(user(userDetails))
                        .contentType(contentType))
                .andExpect(content().contentType(contentType));


    }

    @Test
    public void testSetRatingTagOnDish() throws Exception {
        // test case (from sql)
        Long dishId = 9999999L;
        String originalTag = "325";
        String expectedTag = "324";
        String ratingIdAsString = "391";
        String stepAsString = "5";


        // get dish, and assert tag 325 is present
        // (start condition)
        MvcResult result = mockMvc.perform(get("/dish/"
                        + dishId + "/tag")
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$..tag", hasSize(17)))
                .andExpect(jsonPath("$..tag.tag_id", hasItem(originalTag)))
                .andExpect(jsonPath("$..tag.tag_id", not(hasItem(expectedTag))))
                .andReturn();

        // tested call
        mockMvc.perform(put("/dish/"
                        + dishId + "/rating/" + ratingIdAsString + "/" + stepAsString)
                        .with(user(userDetails)))
                .andExpect(status().isNoContent());

        // verify after condition - should have tag 324 - and no longer have tag 325
        mockMvc.perform(get("/dish/"
                        + dishId + "/tag")
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$..tag", hasSize(17)))
                .andExpect(jsonPath("$..tag.tag_id", hasItem(expectedTag)))
                .andExpect(jsonPath("$..tag.tag_id", not(hasItem(originalTag))));


    }

    @Test
    public void testSetRatingTagOnDish_KO() throws Exception {
        // test case for failure.
        // set step to 100
        Long dishId = 9999999L;
        String ratingIdAsString = "391";
        String stepAsString = "105";


        // get dish, and assert tags are present
        // (start condition)
        mockMvc.perform(get("/dish/"
                        + dishId + "/tag")
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$..tag", hasSize(17)));

        // tested call
        mockMvc.perform(put("/dish/"
                        + dishId + "/rating/" + ratingIdAsString + "/" + stepAsString)
                        .with(user(userDetails)))
                .andExpect(status().isBadRequest());

    }

    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
