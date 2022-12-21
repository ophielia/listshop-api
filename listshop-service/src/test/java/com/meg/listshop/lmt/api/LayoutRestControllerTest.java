package com.meg.listshop.lmt.api;


import com.meg.listshop.Application;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.auth.service.impl.JwtUser;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.repository.TagRepository;
import com.meg.listshop.lmt.service.ListLayoutService;
import com.meg.listshop.test.TestConstants;
import org.hamcrest.Matchers;
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
import org.testcontainers.shaded.com.fasterxml.jackson.databind.JsonNode;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
@Sql(value = {"/sql/com/meg/atable/lmt/api/TempTest.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sql/com/meg/atable/lmt/api/TempTest_rollback.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class LayoutRestControllerTest {

    @ClassRule
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();
    private static Long baseUserId = 99999L;
    private static String baseUserEmail = "username@testitytest.com";


    private static Long newUserId = 101010L;
    private static String newUserEmail = "user@brandnewuser.com";

    private static UserEntity baseUserAccount;
    private static UserDetails baseUserDetails;

    private static UserDetails newUserDetails;
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

        baseUserAccount = userService.getUserByUserEmail(baseUserEmail);
        baseUserDetails = new JwtUser(baseUserAccount.getId(),
                baseUserEmail,
                null,
                null,
                null,
                true,
                null);

        newUserDetails = new JwtUser(newUserId,
                newUserEmail,
                null,
                null,
                null,
                true,
                null);
    }



    @Test
    @WithMockUser
    public void testGetUserLayouts() throws Exception {
        // get base user layouts - checking that given layout is fully filled in
        String url = "/layout/user";
        MvcResult result = this.mockMvc.perform(get(url)
                        .with(user(baseUserDetails))
                        .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.list_layout_list", Matchers.hasSize(2)))
                .andReturn();

        Assert.assertNotNull(result);
        ListLayoutListResource resource = parseResourceFromString(result.getResponse().getContentAsString());
        Assert.assertNotNull(resource);
        Map<Long, ListLayout> resultMap = resource.getEmbeddedList().getListLayoutResourceList().stream()
                .map(ListLayoutResource::getListLayout)
                .collect(Collectors.toMap(ListLayout::getLayoutId, Function.identity()));
        Assert.assertEquals("two layouts returned", 2, resultMap.keySet().size());
        Assert.assertTrue("contains layout 999",  resultMap.containsKey(999L));
        ListLayout defaultLayout = resultMap.get(999L);
        Assert.assertTrue("default layout is marked as such", defaultLayout.isDefault());
        Assert.assertEquals("default layout has 1 category", 1, defaultLayout.getCategories().size());
        Assert.assertEquals("default layouts category contains 3 tags", 3, defaultLayout.getCategories().get(0).getTags().size());
        ListLayout otherLayout = resultMap.get(998L);
        Assert.assertFalse("other layout is not default", otherLayout.isDefault());
        Assert.assertEquals("other layout has 1 category", 1, otherLayout.getCategories().size());
        Assert.assertEquals("other layouts category contains 3 tags", 3, otherLayout.getCategories().get(0).getTags().size());

    }


    @Test
    @WithMockUser
    public void testGetUserLayoutsEmpty() throws Exception {
        // get base user layouts - checking that given layout is fully filled in
        String url = "/layout/user";
        MvcResult result = this.mockMvc.perform(get(url)
                        .with(user(newUserDetails))
                        .contentType(contentType))
                .andExpect(status().isOk())
                .andReturn();

        Assert.assertNotNull(result);
        ListLayoutListResource resource = parseResourceFromString(result.getResponse().getContentAsString());
        Assert.assertNotNull(resource.getEmbeddedList().getListLayoutResourceList());
        Assert.assertEquals("empty list should be returned", 0, resource.getEmbeddedList().getListLayoutResourceList().size());
    }


    @Test
    @WithMockUser
    public void testPostUserMappings() throws Exception {
        JwtUser createUserDetails = new JwtUser(baseUserAccount.getId(),
                baseUserAccount.getEmail(),
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

    private ListLayoutListResource parseResourceFromString(String resultString) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        ListLayoutListResource afterList = objectMapper.readValue(resultString, ListLayoutListResource.class);
        Assert.assertNotNull(afterList);
        return afterList;
    }
}
