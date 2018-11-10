package com.meg.atable.lmt.api;

import com.meg.atable.Application;
import com.meg.atable.lmt.api.model.ListLayout;
import com.meg.atable.lmt.api.model.ListLayoutCategory;
import com.meg.atable.lmt.api.model.ListLayoutType;
import com.meg.atable.lmt.api.model.ModelMapper;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.service.JwtUser;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.common.FlatStringUtils;
import com.meg.atable.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.atable.lmt.data.entity.ListLayoutEntity;
import com.meg.atable.lmt.data.entity.TagEntity;
import com.meg.atable.lmt.data.repository.TagRepository;
import com.meg.atable.lmt.service.ListLayoutService;
import com.meg.atable.test.TestConstants;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
public class ListLayoutRestControllerTest {


    private static UserAccountEntity userAccount;
    private static UserDetails userDetails;
    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));
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

        userAccount = userService.getUserByUserName(TestConstants.USER_3_NAME);
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
    public void testReadListLayout() throws Exception {
        Long testId = TestConstants.LIST_LAYOUT_1_ID;
        mockMvc.perform(get("/listlayout/"
                + TestConstants.LIST_LAYOUT_1_ID)
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.list_layout.layout_id", Matchers.isA(Number.class)))
                .andExpect(jsonPath("$.list_layout.layout_id").value(testId));

    }

    @Test
    @WithMockUser
    public void testAddCategoryToListLayout() throws Exception {
        JwtUser createUserDetails = new JwtUser(userAccount.getId(),
                userAccount.getUsername(),
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
        testcategory.setTags(tags);
        ListLayoutCategory categoryModel = ModelMapper.toModel(testcategory);
        String url = "/listlayout/"
                + TestConstants.LIST_LAYOUT_1_ID + "/category";
        String listLayoutJson = json(categoryModel);
        this.mockMvc.perform(post(url)
                .with(user(createUserDetails))
                .contentType(contentType)
                .content(listLayoutJson))
                .andExpect(status().isNoContent());
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
        ListLayoutCategoryEntity testcategory = test.getCategories().get(0);
        mockMvc.perform(post("/listlayout/"
                + TestConstants.LIST_LAYOUT_1_ID + "/category/"
                + testcategory.getId() + "/tag" + taglist)
                .with(user(userDetails)))
                .andExpect(status().isNoContent());


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
        test.setLayoutType(ListLayoutType.All);
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
        ListLayoutEntity test = listLayoutSErvice.getListLayoutById(TestConstants.LIST_LAYOUT_1_ID);
        ListLayoutCategoryEntity testcategory = test.getCategories().get(0);
        mockMvc.perform(get("/listlayout/"
                + TestConstants.LIST_LAYOUT_1_ID + "/category/"
                + testcategory.getId() + "/tag")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));
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
        ListLayoutCategoryEntity testcategory = test.getCategories().get(0);
        List<TagEntity> tags = listLayoutSErvice.getTagsForLayoutCategory(testcategory.getId());
        testcategory.setTags(tags);
        testcategory.setName("wokkawokka");
        ListLayoutCategory categoryModel = ModelMapper.toModel(testcategory);
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
                .andExpect(content().contentType(contentType));

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
        ListLayoutEntity list = listLayoutSErvice.getListLayoutById(TestConstants.LIST_LAYOUT_3_ID);
        ListLayoutCategoryEntity category = list.getCategories().get(0);
        Long testId = category.getId();
        mockMvc.perform(delete("/listlayout/" + category.getLayoutId()
                + "/category/" +
                +testId)
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
        ListLayoutCategoryEntity testcategory = test.getCategories().get(0);
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
    public void testMoveCategory() throws Exception {
        String url = "/listlayout/category/" + TestConstants.LIST_LAYOUT_2_CATEGORY_ID5
                + "?move=up";

        mockMvc.perform(post(url)
                .with(user(userDetails)))
                .andExpect(status().isNoContent());
    }


    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }


}
