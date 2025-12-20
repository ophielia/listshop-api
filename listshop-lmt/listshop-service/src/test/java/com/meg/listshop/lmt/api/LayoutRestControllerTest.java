package com.meg.listshop.lmt.api;

import com.meg.listshop.Application;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.CustomUserDetails;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.api.model.*;
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
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@Testcontainers
@WebAppConfiguration
@ActiveProfiles("test")
@Sql(value = {"/sql/com/meg/atable/lmt/api/LayoutRestControlleTest.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sql/com/meg/atable/lmt/api/LayoutRestController_rollback.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class LayoutRestControllerTest {

    @Container
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    private static UserDetails baseUserDetails;

    private static UserDetails emptyUserDetails;
    private static UserDetails newUserDetails;

    private static final String tagIdApple = "65";
    private static final String tagIdOrange = "45";
    private static final String tagIdLemon = "357";

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype());
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private UserService userService;
    private MockMvc mockMvc;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        HttpMessageConverter mappingJackson2HttpMessageConverter = Arrays.stream(converters)

                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        Assertions.assertNotNull(mappingJackson2HttpMessageConverter, "the JSON message converter must not be null");
    }

    @BeforeEach
    @WithMockUser
    public void setup() throws Exception {

        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        String baseUserEmail = "username@testitytest.com";
        UserEntity baseUserAccount = userService.getUserByUserEmail(baseUserEmail);
        baseUserDetails = new CustomUserDetails(baseUserAccount.getId(),
                baseUserEmail,
                null,
                null,
                null,
                true,
                null);

        Long emptyUserId = 101010L;
        String emptyUserEmail = "user@emptyuser.com";
        emptyUserDetails = new CustomUserDetails(emptyUserId,
                emptyUserEmail,
                null,
                null,
                null,
                true,
                null);


        Long newUserId = 121212L;
        String newUserEmail = "user@brandnewuser.com";
        newUserDetails = new CustomUserDetails(newUserId,
                newUserEmail,
                null,
                null,
                null,
                true,
                null);
    }


    @Test
    @WithMockUser
    void testDefaultLayoutAssignment() throws Exception {
        String categoryTemplateId = "998901";  // Forbidden Area
        String tagIdEliza = "1000124";
        String tagIdAlexander = "1000125";

        // Base User assigns alexander (1000125) to Layout "Forbidden Area" (998901)
        // Base User assigns eliza (1000124) to Layout "Forbidden Area" (998901)
        MappingPost mapping = new MappingPost();
        mapping.setCategoryId(categoryTemplateId);
        mapping.setTagIds(Arrays.asList(tagIdEliza, tagIdAlexander));
        String payload = json(mapping);

        // make call
        String url = "/layout/user/mapping";
        this.mockMvc.perform(post(url)
                        .with(user(baseUserDetails))
                        .contentType(contentType)
                        .content(payload))
                .andExpect(status().isOk());

        // New User creates new tag as child of hamilton (1000123)
        String newTagUlr = "/tag/1000123/child";

        Tag tag = new Tag("Aaron Burr, sir");
        tag = tag.tagType(TagType.Ingredient.name());
        String tagString = json(tag);

        MvcResult resultNewTag = this.mockMvc.perform(post(newTagUlr)
                        .with(user(newUserDetails))
                        .contentType(contentType)
                        .content(tagString))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        String newTagId = extractResultId(resultNewTag);

        // New user creates new list
        ListGenerateProperties properties = new ListGenerateProperties();
        properties.setAddFromStarter(false);
        properties.setGenerateMealplan(false);

        String jsonProperties = json(properties);

        String createListUrl = "/shoppinglist";
        MvcResult resultNewList = this.mockMvc.perform(post(createListUrl)
                        .with(user(newUserDetails))
                        .contentType(contentType)
                        .content(jsonProperties))
                .andExpect(status().isCreated())
                .andReturn();
        String newListId = extractResultId(resultNewList);

        // New user adds new tag to list
        String addToListUrl = String.format("/shoppinglist/%s/tag/%s", newListId, newTagId);
        this.mockMvc.perform(post(addToListUrl)
                        .with(user(newUserDetails))
                        .contentType(contentType)
                        .content(jsonProperties))
                .andReturn();

        // New user retrieves list
        String retrieveList = String.format("/shoppinglist/%s", newListId);
        MvcResult listResultsAfter = this.mockMvc.perform(get(retrieveList)
                        .with(user(newUserDetails)))
                .andReturn();
        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        String jsonList = listResultsAfter.getResponse().getContentAsString();
        ShoppingListResource afterList = objectMapper.readValue(jsonList, ShoppingListResource.class);
        ShoppingList resultList = afterList.getShoppingList();

        // Creation of new tag
        Assertions.assertNotNull(resultList);
        Assertions.assertEquals(1, resultList.getCategories().size());
    }


    @Test
    @WithMockUser
    void testPostUserMappingsExisting() throws Exception {
        // create mapping post - map apples and oranges to default category Frozen
        // 10, 'Frozen'
        String categoryTemplateId = "10";

        MappingPost mapping = new MappingPost();
        mapping.setCategoryId(categoryTemplateId);
        mapping.setTagIds(Arrays.asList(tagIdApple, tagIdLemon, tagIdOrange));
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
        Assertions.assertNotNull(resource);
        Optional<ListLayout> listLayoutResult = resource.getEmbeddedList().getListLayoutResourceList().stream()
                .map(ListLayoutResource::getListLayout)
                .filter(ListLayout::isDefault)
                .findFirst();
        Assertions.assertTrue(listLayoutResult.isPresent(), "default exists");
        ListLayout layout = listLayoutResult.get();

        // assert category "Frozen" exists
        Optional<ListLayoutCategory> frozenCategoryOpt = layout.getCategories().stream()
                .filter(listLayoutCategory -> listLayoutCategory.getName().equalsIgnoreCase("Frozen"))
                .findFirst();
        Assertions.assertTrue(frozenCategoryOpt.isPresent());
        ListLayoutCategory category = frozenCategoryOpt.get();

        // assert apples, oranges, and lemon are in frozen
        Set<String> tagIds = category.getTags().stream()
                .map(Tag::getId)
                .collect(Collectors.toSet());
        Assertions.assertTrue(tagIds.contains(tagIdApple), "apples are there");
        Assertions.assertTrue(tagIds.contains(tagIdOrange), "oranges are there");
        Assertions.assertTrue(tagIds.contains(tagIdLemon), "lemons are there");

        // Part II - move these tags to a different category
        //  999901, 'Special Category'
        categoryTemplateId = "999901";
        mapping.setCategoryId(categoryTemplateId);
        mapping.setTagIds(Collections.singletonList(tagIdLemon));
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
        Assertions.assertNotNull(resource);
        listLayoutResult = resource.getEmbeddedList().getListLayoutResourceList().stream()
                .map(ListLayoutResource::getListLayout)
                .filter(ListLayout::isDefault)
                .findFirst();
        Assertions.assertTrue(listLayoutResult.isPresent(), "default exists");
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
        Assertions.assertFalse(tagIdsInSpecial.contains(tagIdApple), "apples are not there");
        Assertions.assertFalse(tagIdsInSpecial.contains(tagIdOrange), "oranges are not there");
        Assertions.assertTrue(tagIdsInSpecial.contains(tagIdLemon), "lemons are there");

        //  lemon is not in frozen
        Set<String> tagIdsInFrozen = frozenCategory.getTags().stream()
                .map(Tag::getId)
                .collect(Collectors.toSet());
        Assertions.assertTrue(tagIdsInFrozen.contains(tagIdApple), "apples are  there");
        Assertions.assertTrue(tagIdsInFrozen.contains(tagIdOrange), "oranges are  there");
        Assertions.assertFalse(tagIdsInFrozen.contains(tagIdLemon), "lemons are not there");
    }

    @Test
    @WithMockUser
    void testPostUserMappingsNewUser() throws Exception {
        // do mappings with user which doesn't have any layout or categoriew
        String categoryTemplateId = "10";

        MappingPost mapping = new MappingPost();
        mapping.setCategoryId(categoryTemplateId);
        mapping.setTagIds(Arrays.asList(tagIdApple, tagIdLemon, tagIdOrange));
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
        Assertions.assertNotNull(resource);
        Optional<ListLayout> listLayoutResult = resource.getEmbeddedList().getListLayoutResourceList().stream()
                .map(ListLayoutResource::getListLayout)
                .filter(ListLayout::isDefault)
                .findFirst();
        Assertions.assertTrue(listLayoutResult.isPresent(), "default exists");
        ListLayout layout = listLayoutResult.get();

        // assert category "Frozen" exists
        Optional<ListLayoutCategory> frozenCategoryOpt = layout.getCategories().stream()
                .filter(listLayoutCategory -> listLayoutCategory.getName().equalsIgnoreCase("Frozen"))
                .findFirst();
        Assertions.assertTrue(frozenCategoryOpt.isPresent());
        ListLayoutCategory category = frozenCategoryOpt.get();

        // assert apples, oranges, and lemon are in frozen
        Set<String> tagIds = category.getTags().stream()
                .map(Tag::getId)
                .collect(Collectors.toSet());
        Assertions.assertTrue(tagIds.contains(tagIdApple), "apples are there");
        Assertions.assertTrue(tagIds.contains(tagIdOrange), "oranges are there");
        Assertions.assertTrue(tagIds.contains(tagIdLemon), "lemons are there");

        // Part II - move these tags to a different category
        //  999901, 'Special Category'
        categoryTemplateId = "999901";
        mapping.setCategoryId(categoryTemplateId);
        mapping.setTagIds(Collections.singletonList(tagIdLemon));
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
        Assertions.assertNotNull(resource);
        listLayoutResult = resource.getEmbeddedList().getListLayoutResourceList().stream()
                .map(ListLayoutResource::getListLayout)
                .filter(ListLayout::isDefault)
                .findFirst();
        Assertions.assertTrue(listLayoutResult.isPresent(), "default exists");
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
        Assertions.assertFalse(tagIdsInSpecial.contains(tagIdApple), "apples are not there");
        Assertions.assertFalse(tagIdsInSpecial.contains(tagIdOrange), "oranges are not there");
        Assertions.assertTrue(tagIdsInSpecial.contains(tagIdLemon), "lemons are there");

        //  lemon is not in frozen
        Set<String> tagIdsInFrozen = frozenCategory.getTags().stream()
                .map(Tag::getId)
                .collect(Collectors.toSet());
        Assertions.assertTrue(tagIdsInFrozen.contains(tagIdApple), "apples are  there");
        Assertions.assertTrue(tagIdsInFrozen.contains(tagIdOrange), "oranges are  there");
        Assertions.assertFalse(tagIdsInFrozen.contains(tagIdLemon), "lemons are not there");
    }

    @Test
    @WithMockUser
    void testGetUserLayoutsEmpty() throws Exception {
        // get base user layouts - checking that given layout is fully filled in
        String url = "/layout/user";
        MvcResult result = this.mockMvc.perform(get(url)
                        .with(user(emptyUserDetails))
                        .contentType(contentType))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertNotNull(result);
        ListLayoutListResource resource = parseResourceFromString(result.getResponse().getContentAsString());
        Assertions.assertNotNull(resource.getEmbeddedList().getListLayoutResourceList());
        Assertions.assertEquals(0, resource.getEmbeddedList().getListLayoutResourceList().size(), "empty list should be returned");
    }


    @Test
    @WithMockUser
    void testGetUserLayouts() throws Exception {
        // get base user layouts - checking that given layout is fully filled in
        String url = "/layout/user";
        MvcResult result = this.mockMvc.perform(get(url)
                        .with(user(baseUserDetails))
                        .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.list_layout_list", Matchers.hasSize(2)))
                .andReturn();

        Assertions.assertNotNull(result);
        ListLayoutListResource resource = parseResourceFromString(result.getResponse().getContentAsString());
        Assertions.assertNotNull(resource);
        Map<Long, ListLayout> resultMap = resource.getEmbeddedList().getListLayoutResourceList().stream()
                .map(ListLayoutResource::getListLayout)
                .collect(Collectors.toMap(ListLayout::getLayoutId, Function.identity()));
        Assertions.assertEquals(2, resultMap.keySet().size(), "two layouts returned");
        Assertions.assertTrue(resultMap.containsKey(999L), "contains layout 999");
        ListLayout defaultLayout = resultMap.get(999L);
        Assertions.assertTrue(defaultLayout.isDefault(), "default layout is marked as such");
        Assertions.assertEquals(1, defaultLayout.getCategories().size(), "default layout has 1 category");
        Assertions.assertEquals(3, defaultLayout.getCategories().get(0).getTags().size(), "default layouts category contains 3 tags");
        ListLayout otherLayout = resultMap.get(998L);
        Assertions.assertFalse(otherLayout.isDefault(), "other layout is not default");
        Assertions.assertEquals(1, otherLayout.getCategories().size(), "other layout has 1 category");
        Assertions.assertEquals(3, otherLayout.getCategories().get(0).getTags().size(), "other layouts category contains 3 tags");

    }

    @Test
    void testGetDefaultUserLayoutNotLoggedIn() throws Exception {
        // get base user layouts - checking that given layout is fully filled in
        String url = "/layout/default";
        MvcResult result = this.mockMvc.perform(get(url)
                        .contentType(contentType))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertNotNull(result);
        ListLayoutResource resource = parseSingleResourceFromString(result.getResponse().getContentAsString());
        Assertions.assertNotNull(resource);
        ListLayout defaultLayout = resource.getListLayout();
        Assertions.assertTrue(defaultLayout.isDefault(), "default layout is marked as such");
        Assertions.assertTrue(defaultLayout.getCategories().size() > 5, "default layout has more than 5");
        Optional<ListLayoutCategory> produce = defaultLayout.getCategories().stream()
                .filter(c -> Objects.equals(c.getName(), "Produce"))
                .findFirst();
        Assertions.assertTrue(produce.isPresent(), "produce category exists");
        Assertions.assertTrue(produce.get().getTags().size() > 10, "produce has at least 10 tags");
        // tag "Peggy" is user-specific, and should not be in ListLayout
        Optional<Tag> peggyTag = defaultLayout.getCategories().stream()
                .flatMap(c -> c.getTags().stream())
                .filter(t -> Objects.equals(t.getName(), "Peggy"))
                .findFirst();
        Assertions.assertFalse(peggyTag.isPresent());
    }

    @Test
    @WithMockUser
    void testGetDefaultLayoutLoggedIn() throws Exception {
        // New User creates new tag as child of hamilton (1000123)
        String newTagUlr = "/tag/1000123/child";

        Tag tag = new Tag("Thomas Jefferson");
        tag = tag.tagType(TagType.Ingredient.name());
        String tagString = json(tag);

        this.mockMvc.perform(post(newTagUlr)
                        .with(user(baseUserDetails))
                        .contentType(contentType)
                        .content(tagString))
                .andExpect(status().is2xxSuccessful());


        // get base user layouts - checking that given layout is fully filled in
        String url = "/layout/default";
        MvcResult result = this.mockMvc.perform(get(url)
                        .with(user(baseUserDetails))
                        .contentType(contentType))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertNotNull(result);
        ListLayoutResource resource = parseSingleResourceFromString(result.getResponse().getContentAsString());
        Assertions.assertNotNull(resource);
        ListLayout defaultLayout = resource.getListLayout();
        Assertions.assertTrue(defaultLayout.isDefault(), "default layout is marked as such");
        Assertions.assertTrue(defaultLayout.getCategories().size() > 5, "default layout has more than 5");
        Optional<ListLayoutCategory> produce = defaultLayout.getCategories().stream()
                .filter(c -> Objects.equals(c.getName(), "Produce"))
                .findFirst();
        Assertions.assertTrue(produce.isPresent(), "produce category exists");
        Assertions.assertTrue(produce.get().getTags().size() > 10, "produce has at least 10 tags");
        // check for existance of "Thomas Jefferson" tag
        Optional<Tag> jeffersonTag = defaultLayout.getCategories().stream()
                .flatMap(c -> c.getTags().stream())
                .filter(t -> Objects.equals(t.getName(), "Thomas Jefferson"))
                .findFirst();
        Assertions.assertTrue(jeffersonTag.isPresent());
    }

    @Test
    void testGetUserCategories() throws Exception {
        String url = "/layout/user/categories";
        MvcResult result = this.mockMvc.perform(get(url)
                        .with(user(emptyUserDetails))
                        .contentType(contentType))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertNotNull(result);
        CategoryListResource categoryList = parseCategoryListResourceFromString(result.getResponse().getContentAsString());
        // the empty user should contain only the default categories
        // note - the layout id isn't included in the category resource, so we have to test with ids.
        Assertions.assertEquals(7, categoryList.getEmbeddedList().getCategoryResourceList().size());
        Set<Long> categoryIds = categoryList.getEmbeddedList().getCategoryResourceList().stream()
                .map(CategoryResource::getCategory)
                .map(Category::getId)
                .filter(cid -> cid > 10 && !cid.equals(1041L))
                .collect(Collectors.toSet());
        Assertions.assertTrue(categoryIds.isEmpty());
    }

    @Test
    void testGetUserCategoriesWithOverlap() throws Exception {
        // map tags to default category "Meat" (category_id 5)
        String categoryTemplateId = "5";

        MappingPost mapping = new MappingPost();
        mapping.setCategoryId(categoryTemplateId);
        mapping.setTagIds(Arrays.asList(tagIdApple, tagIdLemon, tagIdOrange));
        String payload = json(mapping);

        // make call
        String mapUrl = "/layout/user/mapping";
        this.mockMvc.perform(post(mapUrl)
                        .with(user(newUserDetails))
                        .contentType(contentType)
                        .content(payload))
                .andExpect(status().isOk())
                .andReturn();

        // retrieve categories, and verify that category Meat does not have the id 10
        String url = "/layout/user/categories";
        MvcResult result = this.mockMvc.perform(get(url)
                        .with(user(newUserDetails))
                        .contentType(contentType))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertNotNull(result);
        CategoryListResource categoryList = parseCategoryListResourceFromString(result.getResponse().getContentAsString());
        // the base user will overlap categories - the category Meat exists in both the default, and the user categories
        Map<String, Category> categoryMap = categoryList.getEmbeddedList().getCategoryResourceList().stream()
                .map(CategoryResource::getCategory)
                .collect(Collectors.toMap(Category::getName, Function.identity()));
        Assertions.assertTrue(categoryMap.containsKey("Meat"));
        Category meat = categoryMap.get("Meat");
        Assertions.assertNotEquals(10L, meat.getId(), "Id for category shouldn't be 10");
        // verify that Meat is the first category returned
        Assertions.assertEquals("Meat", categoryList.getEmbeddedList().getCategoryResourceList().get(0).getCategory().getName());
    }


    @Test
    void testGetUserCategoriesWithUserLayout() throws Exception {
        String url = "/layout/user/categories";
        MvcResult result = this.mockMvc.perform(get(url)
                        .with(user(baseUserDetails))
                        .contentType(contentType))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertNotNull(result);
        CategoryListResource categoryList = parseCategoryListResourceFromString(result.getResponse().getContentAsString());
        // the base user will contain their categories, as well as the default categories
        // but there aren't any overlaps in the names - the use categories are distinct
        // note - the layout id isn't included in the category resource, so we have to test with ids.
        Assertions.assertEquals(8, categoryList.getEmbeddedList().getCategoryResourceList().size());
        Set<Long> categoryIds = categoryList.getEmbeddedList().getCategoryResourceList().stream()
                .map(CategoryResource::getCategory)
                .filter(c -> !c.getName().equalsIgnoreCase("special category"))  // exclude user category
                .map(Category::getId)
                .filter(cid -> cid > 10 && !cid.equals(1041L)) // exclude standard categories
                .collect(Collectors.toSet());
        Assertions.assertTrue(categoryIds.isEmpty());
    }

    @Test
    void testRetrieveUserDefaultLayout() throws Exception {
        String url = "/layout/user/categories";
        MvcResult result = this.mockMvc.perform(get(url)
                        .with(user(baseUserDetails))
                        .contentType(contentType))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertNotNull(result);
    }


    private String extractResultId(MvcResult result) {
        List<String> headers = result.getResponse().getHeaders("Location");
        String[] locationParts = headers.get(0).split("/");
        return locationParts[locationParts.length - 1];
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
        Assertions.assertNotNull(afterList);
        return afterList;
    }

    private ListLayoutResource parseSingleResourceFromString(String resultString) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        ListLayoutResource afterList = objectMapper.readValue(resultString, ListLayoutResource.class);
        Assertions.assertNotNull(afterList);
        return afterList;
    }

    private CategoryListResource parseCategoryListResourceFromString(String resultString) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        CategoryListResource afterList = objectMapper.readValue(resultString, CategoryListResource.class);
        Assertions.assertNotNull(afterList);
        return afterList;
    }
}
