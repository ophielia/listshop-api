package com.meg.listshop.lmt.api;


import com.meg.listshop.Application;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.auth.service.impl.JwtUser;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.data.repository.TagRepository;
import com.meg.listshop.lmt.service.ListLayoutService;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
@Sql(value = {"/sql/com/meg/atable/lmt/api/LayoutRestControlleTest.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sql/com/meg/atable/lmt/api/LayoutRestController_rollback.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class LayoutRestControllerTest {

    @ClassRule
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();
    private static Long baseUserId = 99999L;
    private static String baseUserEmail = "username@testitytest.com";


    private static Long emptyUserId = 101010L;
    private static String emptyUserEmail = "user@emptyuser.com";

    private static Long newUserId = 121212L;
    private static String newUserEmail = "user@brandnewuser.com";
    private static UserEntity baseUserAccount;
    private static UserDetails baseUserDetails;

    private static UserDetails emptyUserDetails;
    private static UserDetails newUserDetails;

    private static String tagIdApple = "65";
    private static String tagIdOrange = "45";
    private static String tagIdLemon = "357";

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

        emptyUserDetails = new JwtUser(emptyUserId,
                emptyUserEmail,
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
                        .with(user(emptyUserDetails))
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
    public void testPostUserMappingsExisting() throws Exception {
        // create mapping post - map apples and oranges to default category Frozen
        // 10, 'Frozen'
        String categoryTemplateId = "10";

        MappingPost mapping = new MappingPost();
        mapping.setCategoryId(categoryTemplateId);
        mapping.setTagIds( Arrays.asList(tagIdApple, tagIdLemon, tagIdOrange));
        String payload = json(mapping);

        // make call
        String url = "/layout/user/mapping";
        this.mockMvc.perform(post(url)
                        .with(user(baseUserDetails))
                        .contentType(contentType)
                        .content(payload))
                .andExpect(status().isOk())
                .andReturn();

        // retrieve user layouts
        String getResultUrl = "/layout/user";
        MvcResult result = this.mockMvc.perform(get(getResultUrl)
                        .with(user(baseUserDetails))
                        .contentType(contentType))
                .andExpect(status().isOk())
                .andReturn();

        // get default layout
        ListLayoutListResource resource = parseResourceFromString(result.getResponse().getContentAsString());
        Assert.assertNotNull(resource);
        Optional<ListLayout> listLayoutResult = resource.getEmbeddedList().getListLayoutResourceList().stream()
                .map(ListLayoutResource::getListLayout)
                .filter(listLayout -> listLayout.isDefault())
                .findFirst();
        Assert.assertTrue("default exists", listLayoutResult.isPresent());
        ListLayout layout = listLayoutResult.get();

        // assert category "Frozen" exists
        Optional<ListLayoutCategory> frozenCategoryOpt = layout.getCategories().stream()
                .filter(listLayoutCategory -> listLayoutCategory.getName().equalsIgnoreCase("Frozen"))
                .findFirst();
        Assert.assertTrue(frozenCategoryOpt.isPresent());
        ListLayoutCategory category = frozenCategoryOpt.get();

        // assert apples, oranges, and lemon are in frozen
        Set<String> tagIds = category.getTags().stream()
                .map(Tag::getId)
                .collect(Collectors.toSet());
        Assert.assertTrue("apples are there", tagIds.contains(tagIdApple));
        Assert.assertTrue("oranges are there", tagIds.contains(tagIdOrange));
        Assert.assertTrue("lemons are there", tagIds.contains(tagIdLemon));

        // Part II - move these tags to a different category
        //  999901, 'Special Category'
        categoryTemplateId = "999901";
        mapping.setCategoryId(categoryTemplateId);
        mapping.setTagIds( Arrays.asList( tagIdLemon));
        payload = json(mapping);

        // make the call
        this.mockMvc.perform(post(url)
                        .with(user(baseUserDetails))
                        .contentType(contentType)
                        .content(payload))
                .andExpect(status().isOk())
                .andReturn();

        // retrieve user layouts
        result = this.mockMvc.perform(get(getResultUrl)
                        .with(user(baseUserDetails))
                        .contentType(contentType))
                .andExpect(status().isOk())
                .andReturn();

        // get default layout
        resource = parseResourceFromString(result.getResponse().getContentAsString());
        Assert.assertNotNull(resource);
        listLayoutResult = resource.getEmbeddedList().getListLayoutResourceList().stream()
                .map(ListLayoutResource::getListLayout)
                .filter(listLayout -> listLayout.isDefault())
                .findFirst();
        Assert.assertTrue("default exists", listLayoutResult.isPresent());
        layout = listLayoutResult.get();

        // assert category "Special" exists
        Map<String, ListLayoutCategory> allCategories = layout.getCategories().stream()
                .collect(Collectors.toMap(ListLayoutCategory::getName, Function.identity()));
        ListLayoutCategory specialCategory = allCategories.get("Special Category");
        ListLayoutCategory frozenCategory = allCategories.get("Frozen");

        //  lemon is in special
        Set<String> tagIdsInSpecial = specialCategory.getTags().stream()
                .map(Tag::getId)
                .collect(Collectors.toSet());
        Assert.assertFalse("apples are not there", tagIdsInSpecial.contains(tagIdApple));
        Assert.assertFalse("oranges are not there", tagIdsInSpecial.contains(tagIdOrange));
        Assert.assertTrue("lemons are there", tagIdsInSpecial.contains(tagIdLemon));

        //  lemon is not in frozen
        Set<String> tagIdsInFrozen = frozenCategory.getTags().stream()
                .map(Tag::getId)
                .collect(Collectors.toSet());
        Assert.assertTrue("apples are  there", tagIdsInFrozen.contains(tagIdApple));
        Assert.assertTrue("oranges are  there", tagIdsInFrozen.contains(tagIdOrange));
        Assert.assertFalse("lemons are not there", tagIdsInFrozen.contains(tagIdLemon));
    }


    @Test
    @WithMockUser
    public void testPostUserMappingsNewUser() throws Exception {
        // do mappings with user which doesn't have any layout or categoriew
        String categoryTemplateId = "10";

        MappingPost mapping = new MappingPost();
        mapping.setCategoryId(categoryTemplateId);
        mapping.setTagIds( Arrays.asList(tagIdApple, tagIdLemon, tagIdOrange));
        String payload = json(mapping);

        // make call
        String url = "/layout/user/mapping";
        this.mockMvc.perform(post(url)
                        .with(user(newUserDetails))
                        .contentType(contentType)
                        .content(payload))
                .andExpect(status().isOk())
                .andReturn();

        // retrieve user layouts
        String getResultUrl = "/layout/user";
        MvcResult result = this.mockMvc.perform(get(getResultUrl)
                        .with(user(newUserDetails))
                        .contentType(contentType))
                .andExpect(status().isOk())
                .andReturn();

        // get default layout
        ListLayoutListResource resource = parseResourceFromString(result.getResponse().getContentAsString());
        Assert.assertNotNull(resource);
        Optional<ListLayout> listLayoutResult = resource.getEmbeddedList().getListLayoutResourceList().stream()
                .map(ListLayoutResource::getListLayout)
                .filter(listLayout -> listLayout.isDefault())
                .findFirst();
        Assert.assertTrue("default exists", listLayoutResult.isPresent());
        ListLayout layout = listLayoutResult.get();

        // assert category "Frozen" exists
        Optional<ListLayoutCategory> frozenCategoryOpt = layout.getCategories().stream()
                .filter(listLayoutCategory -> listLayoutCategory.getName().equalsIgnoreCase("Frozen"))
                .findFirst();
        Assert.assertTrue(frozenCategoryOpt.isPresent());
        ListLayoutCategory category = frozenCategoryOpt.get();

        // assert apples, oranges, and lemon are in frozen
        Set<String> tagIds = category.getTags().stream()
                .map(Tag::getId)
                .collect(Collectors.toSet());
        Assert.assertTrue("apples are there", tagIds.contains(tagIdApple));
        Assert.assertTrue("oranges are there", tagIds.contains(tagIdOrange));
        Assert.assertTrue("lemons are there", tagIds.contains(tagIdLemon));

        // Part II - move these tags to a different category
        //  999901, 'Special Category'
        categoryTemplateId = "999901";
        mapping.setCategoryId(categoryTemplateId);
        mapping.setTagIds( Arrays.asList( tagIdLemon));
        payload = json(mapping);

        // make the call
        this.mockMvc.perform(post(url)
                        .with(user(newUserDetails))
                        .contentType(contentType)
                        .content(payload))
                .andExpect(status().isOk())
                .andReturn();

        // retrieve user layouts
        result = this.mockMvc.perform(get(getResultUrl)
                        .with(user(newUserDetails))
                        .contentType(contentType))
                .andExpect(status().isOk())
                .andReturn();

        // get default layout
        resource = parseResourceFromString(result.getResponse().getContentAsString());
        Assert.assertNotNull(resource);
        listLayoutResult = resource.getEmbeddedList().getListLayoutResourceList().stream()
                .map(ListLayoutResource::getListLayout)
                .filter(listLayout -> listLayout.isDefault())
                .findFirst();
        Assert.assertTrue("default exists", listLayoutResult.isPresent());
        layout = listLayoutResult.get();

        // assert category "Special" exists
        Map<String, ListLayoutCategory> allCategories = layout.getCategories().stream()
                .collect(Collectors.toMap(ListLayoutCategory::getName, Function.identity()));
        ListLayoutCategory specialCategory = allCategories.get("Special Category");
        ListLayoutCategory frozenCategory = allCategories.get("Frozen");

        //  lemon is in special
        Set<String> tagIdsInSpecial = specialCategory.getTags().stream()
                .map(Tag::getId)
                .collect(Collectors.toSet());
        Assert.assertFalse("apples are not there", tagIdsInSpecial.contains(tagIdApple));
        Assert.assertFalse("oranges are not there", tagIdsInSpecial.contains(tagIdOrange));
        Assert.assertTrue("lemons are there", tagIdsInSpecial.contains(tagIdLemon));

        //  lemon is not in frozen
        Set<String> tagIdsInFrozen = frozenCategory.getTags().stream()
                .map(Tag::getId)
                .collect(Collectors.toSet());
        Assert.assertTrue("apples are  there", tagIdsInFrozen.contains(tagIdApple));
        Assert.assertTrue("oranges are  there", tagIdsInFrozen.contains(tagIdOrange));
        Assert.assertFalse("lemons are not there", tagIdsInFrozen.contains(tagIdLemon));
    }





    private String json(Object o) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        //objectMapper.setPropertyNamingStrategy();

        return objectMapper.writeValueAsString(o);

        //MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        //this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        //return mockHttpOutputMessage.getBodyAsString();
    }

    private ListLayoutListResource parseResourceFromString(String resultString) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        ListLayoutListResource afterList = objectMapper.readValue(resultString, ListLayoutListResource.class);
        Assert.assertNotNull(afterList);
        return afterList;
    }
}
