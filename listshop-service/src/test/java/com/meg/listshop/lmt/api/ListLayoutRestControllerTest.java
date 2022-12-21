package com.meg.listshop.lmt.api;


import com.meg.listshop.Application;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.auth.service.impl.JwtUser;
import com.meg.listshop.common.FlatStringUtils;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.api.model.Category;
import com.meg.listshop.lmt.api.model.ListLayout;
import com.meg.listshop.lmt.api.model.ModelMapper;
import com.meg.listshop.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.listshop.lmt.data.entity.ListLayoutEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.repository.TagRepository;
import com.meg.listshop.lmt.service.ListLayoutService;
import com.meg.listshop.test.TestConstants;
import org.hamcrest.Matchers;
import org.junit.*;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.JsonNode;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
@Ignore
public class ListLayoutRestControllerTest {

    @ClassRule
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    private static UserEntity userAccount;
    private static UserDetails userDetails;
    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype());
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private UserService userService;
    @Autowired
    private ListLayoutService listLayoutSErvice;
    @Autowired
    private TagRepository tagRepository;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;
    private MockMvc mockMvc;
    private String userName = "testname";

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

        userAccount = userService.getUserByUserEmail(TestConstants.USER_3_NAME);
        userDetails = new JwtUser(userAccount.getId(),
                userName,
                null,
                null,
                null,
                true,
                null);
    }



    @Test
    @WithMockUser
    public void testAddCategoryToListLayout() throws Exception {
        JwtUser createUserDetails = new JwtUser(userAccount.getId(),
                userAccount.getEmail(),
                null,
                null,
                null,
                true,
                null);
        ListLayoutCategoryEntity testcategory = new ListLayoutCategoryEntity();
        testcategory.setName("new category");
        List<Long> ids = Stream.of(TestConstants.TAG_1_ID, TestConstants.TAG_2_ID,
                        TestConstants.TAG_3_ID, TestConstants.TAG_4_ID)
                .collect(Collectors.toList());
        List<TagEntity> tags = tagRepository.findAllById(ids);
        testcategory.setTags(new HashSet<>(tags));
        Category categoryModel = ModelMapper.toModel(testcategory, false);
        String url = "/listlayout/"
                + TestConstants.LIST_LAYOUT_1_ID + "/category";
        String listLayoutJson = json(categoryModel);
        MvcResult result = this.mockMvc.perform(post(url)
                        .with(user(createUserDetails))
                        .contentType(contentType)
                        .content(listLayoutJson))
                .andExpect(status().isNoContent())
                .andReturn();
        Assert.assertEquals(1, 1);
    }

    @Test
    @WithMockUser
    public void testAddTagsToCategory() throws Exception {
        List<String> idList = Stream.of(TestConstants.TAG_1_ID, TestConstants.TAG_2_ID,
                TestConstants.TAG_3_ID, TestConstants.TAG_4_ID)
                .map(String::valueOf)
                .collect(Collectors.toList());
        String taglist = "?tags=" + FlatStringUtils.flattenListToString(idList, ",");
        ListLayoutEntity test = listLayoutSErvice.getListLayoutById(TestConstants.LIST_LAYOUT_1_ID);
        ListLayoutCategoryEntity testcategory = test.getCategories().iterator().next();
        mockMvc.perform(post("/listlayout/"
                + TestConstants.LIST_LAYOUT_1_ID + "/category/"
                + testcategory.getId() + "/tag" + taglist)
                .with(user(userDetails)))
                .andExpect(status().isNoContent());


    }

    @Test
    @WithMockUser
    public void getTagForCategory() throws Exception {
        String listLayoutId = "5";
        String tagId = "89";


        MvcResult result = mockMvc.perform(get("/listlayout/"
                + listLayoutId + "/tag/"
                + tagId + "/category"))
                .andExpect(status().isOk())
                .andReturn();

        Assert.assertNotNull(result);
        String content = result.getResponse().getContentAsString();
        Assert.assertTrue(content.contains("category_id\":5"));

    }

    @Test
    @WithMockUser
    public void testCreateListLayout() throws Exception {
        JwtUser createUserDetails = new JwtUser(TestConstants.USER_3_ID,
                TestConstants.USER_3_NAME,
                null,
                null,
                null,
                true,
                null);
        ListLayoutEntity test = new ListLayoutEntity();
        test.setName("new list layout");
        ListLayout model = ModelMapper.toModel(test, null);
        String url = "/listlayout";
        String listLayoutJson = json(model);
        this.mockMvc.perform(post(url)
                .with(user(createUserDetails))
                .contentType(contentType)
                .content(listLayoutJson))
                .andExpect(status().isCreated());

    }


    @Test
    @WithMockUser
    public void testGetTagsForCategory() throws Exception {
        ListLayoutEntity test = listLayoutSErvice.getListLayoutById(TestConstants.LIST_LAYOUT_2_ID);
        ListLayoutCategoryEntity testcategory = test.getCategories().iterator().next();
        MvcResult result = mockMvc.perform(get("/listlayout/"
                + TestConstants.LIST_LAYOUT_2_ID + "/category/"
                + testcategory.getId() + "/tag")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$._embedded.tagResourceList", Matchers.hasSize(33)))
                .andReturn();
        Assert.assertTrue(1 == 1);
    }


    @Test
    @WithMockUser
    public void testGetUncategorizedTags() throws Exception {
        mockMvc.perform(get("/listlayout/"
                + TestConstants.LIST_LAYOUT_1_ID + "/tag")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));
    }

    @Test
    @WithMockUser
    public void testUpdateCategoryFromListLayout() throws Exception {
        JwtUser createUserDetails = new JwtUser(userAccount.getId(),
                userName,
                null,
                null,
                null,
                true,
                null);
        ListLayoutEntity test = listLayoutSErvice.getListLayoutById(TestConstants.LIST_LAYOUT_1_ID);
        ListLayoutCategoryEntity testcategory = test.getCategories().iterator().next();
        List<TagEntity> tags = listLayoutSErvice.getTagsForLayoutCategory(testcategory.getId());
        testcategory.setTags(new HashSet<>(tags));
        testcategory.setName("wokkawokka");
        Category categoryModel = ModelMapper.toModel(testcategory, false);
        String url = "/listlayout/"
                + TestConstants.LIST_LAYOUT_1_ID + "/category/"
                + test.getId();
        String listLayoutJson = json(categoryModel);

        this.mockMvc.perform(put(url)
                .with(user(createUserDetails))
                .contentType(contentType)
                .content(listLayoutJson))
                .andExpect(status().isNoContent());

    }

    @Test
    @WithMockUser
    public void testRetrieveListLayouts() throws Exception {
        mockMvc.perform(get("/listlayout")
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$._embedded.listLayoutResourceList", Matchers.hasSize(4)))
                .andReturn();
    }

    @Test
    @WithMockUser
    public void testDeleteListLayout() throws Exception {
        Long testId = TestConstants.LIST_LAYOUT_4_ID;
        mockMvc.perform(delete("/listlayout/"
                + testId)
                .with(user(userDetails)))
                .andExpect(status().isNoContent());

    }


    @Test
    @WithMockUser
    public void testDeleteCategoryFromListLayout() throws Exception {
        String testName = "new category which will be deleted";
        // preparing test - adding category, which will be deleted afterwards
        JwtUser createUserDetails = new JwtUser(userAccount.getId(),
                userAccount.getEmail(),
                null,
                null,
                null,
                true,
                null);
        ListLayoutCategoryEntity testcategory = new ListLayoutCategoryEntity();
        testcategory.setName(testName);
        List<Long> ids = Arrays.asList(TestConstants.TAG_1_ID, TestConstants.TAG_2_ID,
                TestConstants.TAG_3_ID, TestConstants.TAG_4_ID);
        List<TagEntity> tags = tagRepository.findAllById(ids);
        testcategory.setTags(new HashSet<>(tags));
        Category categoryModel = ModelMapper.toModel(testcategory, false);
        String url = "/listlayout/"
                + TestConstants.LIST_LAYOUT_2_ID + "/category";
        String listLayoutJson = json(categoryModel);
        this.mockMvc.perform(post(url)
                        .with(user(createUserDetails))
                        .contentType(contentType)
                        .content(listLayoutJson))
                .andExpect(status().isNoContent());
        // get list layout, to find id of category to delete
        MvcResult result = mockMvc.perform(get("/listlayout/"
                        + TestConstants.LIST_LAYOUT_2_ID)
                        .with(user(userDetails)))
                .andReturn();
        String idToDelete = getCategoryIdForCategoryFromResult(testName, result.getResponse().getContentAsString());


        ListLayoutEntity list = listLayoutSErvice.getListLayoutById(TestConstants.LIST_LAYOUT_2_ID);
        Optional<ListLayoutCategoryEntity> categoryOpt = list.getCategories().stream()
                .filter(c -> c.getId().equals(Long.valueOf(idToDelete))).findFirst();
        ListLayoutCategoryEntity category = categoryOpt.get();
        Long testId = category.getId();
        mockMvc.perform(delete("/listlayout/" + category.getLayoutId()
                        + "/category/" +
                        idToDelete)
                        .with(user(userDetails)))
                .andExpect(status().isNoContent());


    }


    @Test
    @WithMockUser
    public void testDeleteTagsFromCategory() throws Exception {
        List<String> idList = Stream.of(TestConstants.TAG_1_ID, TestConstants.TAG_2_ID,
                TestConstants.TAG_3_ID, TestConstants.TAG_4_ID)
                .map(String::valueOf)
                .collect(Collectors.toList());
        String taglist = "?tags=" + String.join(",", idList);
        ListLayoutEntity test = listLayoutSErvice.getListLayoutById(TestConstants.LIST_LAYOUT_1_ID);
        ListLayoutCategoryEntity testcategory = test.getCategories().iterator().next();
        mockMvc.perform(delete("/listlayout/"
                + TestConstants.LIST_LAYOUT_1_ID + "/category/"
                + testcategory.getId() + "/tag" + taglist)
                .with(user(userDetails)))
                .andExpect(status().isNoContent());

    }

    @Test
    @WithMockUser
    public void testAddSubcategoryToCategory() throws Exception {

        String url = "/listlayout/category/" + TestConstants.LIST_LAYOUT_2_CATEGORY_ID4
                + "/parent/" + TestConstants.LIST_LAYOUT_2_CATEGORY_ID1;

        mockMvc.perform(post(url)
                .with(user(userDetails)))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    public void testAddSubcategoryToCategory_ErrorDifferentLayouts() throws Exception {

        String url = "/listlayout/category/" + TestConstants.LIST_LAYOUT_2_CATEGORY_ID2
                + "/parent/" + TestConstants.LIST_LAYOUT_1_CATEGORY_ID;

        mockMvc.perform(post(url)
                .with(user(userDetails)))
                .andExpect(status().is4xxClientError());
    }


    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    private String getCategoryIdForCategoryFromResult(String testName, String contentAsString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode object = mapper.readTree(contentAsString);
        JsonNode categories = object.get("list_layout").get("categories");
        ArrayNode categoriesArray = (ArrayNode) categories;
        AtomicReference<String> resultId = new AtomicReference<>();
        categoriesArray.forEach(n -> {
            String name = n.get("name").asText();
            if (name.equals(testName)) {
                resultId.set(n.get("category_id").asText());
            }
        });
        return resultId.get();
    }


}
