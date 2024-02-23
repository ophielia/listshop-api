/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.admin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.meg.listshop.Application;
import com.meg.listshop.admin.model.PostSearchTags;
import com.meg.listshop.auth.service.impl.JwtUser;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.pojos.IncludeType;
import com.meg.listshop.lmt.data.pojos.TagInternalStatus;
import com.meg.listshop.lmt.data.repository.TagRepository;
import com.meg.listshop.test.TestConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@AutoConfigureJsonTesters
@Sql(value = {"/sql/com/meg/atable/admin/AdminTagControllerTest.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sql/com/meg/atable/admin/AdminTagControllerTest_rollback.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)

@ActiveProfiles("test")
public class AdminTagRestControllerTest {

    @ClassRule
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    private static UserDetails userDetails;
    @Autowired
    private
    ObjectMapper objectMapper;
    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype());

    private MockMvc mockMvc;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private WebApplicationContext webApplicationContext;


    @Before
    @WithMockUser
    public void setup() throws Exception {

        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();


        userDetails = new JwtUser(TestConstants.USER_1_ID,
                TestConstants.USER_1_EMAIL,
                null,
                null,
                null,
                true,
                null);
    }

    @Test
    public void updateTag() throws Exception {
        Optional<TagEntity> toUpdateOpt = tagRepository.findById(TestConstants.TAG_3_ID);
        Assert.assertTrue(toUpdateOpt.isPresent());
        TagEntity toUpdate = toUpdateOpt.get();
        String updateName = "updated:" + toUpdate.getName();
        String updateDescription = "updated:" + (toUpdate.getDescription() == null ? "" : toUpdate.getDescription());
        toUpdate.setName(updateName);
        toUpdate.setDescription(updateDescription);
        toUpdate.setCategories(new ArrayList<>());
        String tagJson = json(toUpdate);

        this.mockMvc.perform(put("/admin/tag/" + toUpdate.getId())
                        .with(user(userDetails))
                        .contentType(contentType)
                        .content(tagJson))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @WithMockUser
    public void replaceTagsInDishes() throws Exception {
        this.mockMvc.perform(put("/admin/tag/" + TestConstants.TAG_CARROTS + "/dish/" + TestConstants.TAG_MEAT)
                        .with(user(userDetails)))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @WithMockUser
    public void findFoodSuggestions() throws Exception {
        // @GetMapping(value = "/{tagId}/food/suggestions")
        MvcResult result = this.mockMvc.perform(get("/admin/tag/888999/food/suggestions" )
                        .with(user(userDetails)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        String jsonList = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        FoodListResource afterList = objectMapper.readValue(jsonList,FoodListResource.class);
Assert.assertNotNull(afterList);


        long countForCategoryThree = afterList.getEmbeddedList().getFoodResourceList().stream()
                .map(FoodResource::getFood)
                .filter(f -> f.getCategoryId().equals("3"))
                .count();
        Assertions.assertEquals(2, countForCategoryThree, "expected two foods for category 3 found");

        long countForContainsButter = afterList.getEmbeddedList().getFoodResourceList().stream()
                .map(FoodResource::getFood)
                .filter(f -> f.getName().contains("butterlike"))
                .count();
        Assertions.assertEquals(4, countForContainsButter, "expected all foods contains butter");

    }

    @Test
    @WithMockUser
    public void assignFoodToTag() throws Exception {
        MvcResult result = this.mockMvc.perform(post("/admin/tag/888999/food/9000")
                        .with(user(userDetails)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        Assertions.assertNotNull(result);
    }

    @Test
    @WithMockUser
    public void testGetFoodCategoryMappings() throws Exception {
        //    @GetMapping(value = "/food/categories")
        MvcResult result = this.mockMvc.perform(get("/admin/tag/food/categories")
                        .with(user(userDetails)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Assertions.assertNotNull(result);
                ObjectMapper mapper = new ObjectMapper();
        CategoryMappingListResource embeddedList = mapper.readValue(result.getResponse().getContentAsString(), CategoryMappingListResource.class);
        List<FoodCategoryMappingResource> mappingList = embeddedList.getEmbeddedList() != null ? embeddedList.getEmbeddedList().getMappingResourceList() : new ArrayList<>();
        Assertions.assertNotNull(mappingList);
        FoodCategoryMapping withCategory = mappingList.stream()
                .map(FoodCategoryMappingResource::getFoodCategoryMapping)
                .filter(foodCategoryMapping -> foodCategoryMapping.getTagId().equals("8881019"))
                .findFirst().orElse(null);
        Assertions.assertNotNull(withCategory);
        Assertions.assertEquals("3", withCategory.getFoodCategoryId());
    }

    @Test
    @WithMockUser
    public void testGetFullTagInfo() throws Exception {
        Long tagId = 9991029L;
        MvcResult result = this.mockMvc.perform(get("/admin/tag/" + tagId + "/fullinfo")
                        .with(user(userDetails)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Assertions.assertNotNull(result);
        ObjectMapper mapper = new ObjectMapper();
        AdminTagFullInfoResource resultObject = mapper.readValue(result.getResponse().getContentAsString(), AdminTagFullInfoResource.class);
        Assertions.assertNotNull(resultObject);
    }

    @Test
    public void addChildren() throws Exception {
        String url = "/admin/tag/" + TestConstants.PARENT_TAG_ID_1 + "/children?tagIds=" + TestConstants.TAG_MEAT + "," + TestConstants.TAG_CARROTS + "," + TestConstants.TAG_CROCKPOT;

        this.mockMvc.perform(post(url).contentType(contentType)
                        .with(user(userDetails)))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void assignChildToParent() throws Exception {
        String url = "/admin/tag/" + TestConstants.PARENT_TAG_ID_2
                + "/child/" + TestConstants.CHILD_TAG_ID_1;

        Optional<TagEntity> toUpdateOpt = tagRepository.findById(TestConstants.CHILD_TAG_ID_1);
        Assert.assertTrue(toUpdateOpt.isPresent());
        TagEntity toUpdate = toUpdateOpt.get();
        String updateName = "updated:" + toUpdate.getName();
        String updateDescription = "updated:" + (toUpdate.getDescription() == null ? "" : toUpdate.getDescription());
        toUpdate.setName(updateName);
        toUpdate.setDescription(updateDescription);

        this.mockMvc.perform(put(url).with(user(userDetails)))
                .andExpect(status().is2xxSuccessful());
    }


    @Test
    public void assignChildToBaseTag() throws Exception {
        String url = "/admin/tag/" + TestConstants.PARENT_TAG_ID_1 + "/child/" + TestConstants.TAG_MEAT;

        this.mockMvc.perform(put(url)
                        .with(user(userDetails)))
                .andExpect(status().is2xxSuccessful());


    }

    @Test
    public void testAssignFoodCategoryToTag() throws Exception {
        Long testTagId = 9991019L;
        long testCategoryId = 3L;
        String url = "/admin/tag/" + testTagId + "/food/category/" + testCategoryId;

        this.mockMvc.perform(post(url)
                        .with(user(userDetails)))
                .andExpect(status().is2xxSuccessful());

        TagEntity tagResult = tagRepository.findById(testTagId).orElse(null);
        Assert.assertNotNull(tagResult);
        Assert.assertEquals(tagResult.getInternalStatus(), (Long) TagInternalStatus.CATEGORY_ASSIGNED.value());
    }

    @Test
    public void getTagInfoListGroupInclude() throws Exception {
        PostSearchTags postSearchTags = new PostSearchTags();
        postSearchTags.setGroupIncludeType(IncludeType.ONLY.getDisplayName());

        String payload = json(postSearchTags);
        String url = "/admin/tag/search";
        MvcResult result = this.mockMvc.perform(post(url)
                        .with(user(userDetails))
                .with(user(userDetails))
                .contentType(contentType)
                .content(payload))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Assert.assertNotNull(result);
        String resultBody = result.getResponse().getContentAsString();
        System.out.println("\n\n" + resultBody + "\n\n");
        Assert.assertNotNull(resultBody);
        TagListResource resultResource = deserializeTagListResource(resultBody);
        Assert.assertNotNull(resultResource);

        // check that results do not contain "carrots" (since carrots are not a group)

        TagResource notFoundCarrots = resultResource.getEmbeddedList().getTagResourceList().stream()
                .filter(t -> t.getTag().getName().equalsIgnoreCase("carrots"))
                .findFirst()
                .orElse(null);
        // check that results do  contain "Frozen"
        TagResource foundDryGoods = resultResource.getEmbeddedList().getTagResourceList().stream()
                .filter(t -> t.getTag().getName().equalsIgnoreCase("frozen"))
                .findFirst()
                .orElse(null);
        Assert.assertNotNull(foundDryGoods);
        Assert.assertNull(notFoundCarrots);

        // now test exclude groups
        postSearchTags.setGroupIncludeType(IncludeType.EXCLUDE.getDisplayName());

         payload = json(postSearchTags);
         url = "/admin/tag/search";
         result = this.mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .with(user(userDetails))
                        .contentType(contentType)
                        .content(payload))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Assert.assertNotNull(result);
         resultBody = result.getResponse().getContentAsString();
        System.out.println("\n\n" + resultBody + "\n\n");
        Assert.assertNotNull(resultBody);
         resultResource = deserializeTagListResource(resultBody);
        Assert.assertNotNull(resultResource);

        // check that results do not contain "carrots" (since carrots are not a group)

         notFoundCarrots = resultResource.getEmbeddedList().getTagResourceList().stream()
                .filter(t -> t.getTag().getName().equalsIgnoreCase("carrots"))
                .findFirst()
                .orElse(null);
        // check that results do  contain "Frozen"
         foundDryGoods = resultResource.getEmbeddedList().getTagResourceList().stream()
                .filter(t -> t.getTag().getName().equalsIgnoreCase("frozen"))
                .findFirst()
                .orElse(null);
        Assert.assertNull(foundDryGoods);
        Assert.assertNotNull(notFoundCarrots);
    }

    @Test
    public void getTagInfoListUser() throws Exception {
        PostSearchTags postSearchTags = new PostSearchTags();
        postSearchTags.setUserId("0");  // should return the default tags only, with userId = 0

        String payload = json(postSearchTags);
        String url = "/admin/tag/search";
        MvcResult result = this.mockMvc.perform(post(url)
                        .with(user(userDetails))
                .with(user(userDetails))
                .contentType(contentType)
                .content(payload))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Assert.assertNotNull(result);
        String resultBody = result.getResponse().getContentAsString();
        System.out.println("\n\n" + resultBody + "\n\n");
        Assert.assertNotNull(resultBody);
        TagListResource resultResource = deserializeTagListResource(resultBody);
        Assert.assertNotNull(resultResource);

        // check that results do not contain "carrots" (since carrots are not a group)

        TagResource notFoundSomeGreenThing = resultResource.getEmbeddedList().getTagResourceList().stream()
                .filter(t -> t.getTag().getName().equalsIgnoreCase("some green thing"))
                .findFirst()
                .orElse(null);
        // check that results do  contain "Frozen"
        TagResource foundCarrots = resultResource.getEmbeddedList().getTagResourceList().stream()
                .filter(t -> t.getTag().getName().equalsIgnoreCase("carrots"))
                .findFirst()
                .orElse(null);
        Assert.assertNotNull(foundCarrots);
        Assert.assertNull(notFoundSomeGreenThing);

        // now test userId - 101010
        postSearchTags.setUserId("101010");

         payload = json(postSearchTags);
         url = "/admin/tag/search";
         result = this.mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .with(user(userDetails))
                        .contentType(contentType)
                        .content(payload))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Assert.assertNotNull(result);
         resultBody = result.getResponse().getContentAsString();
        System.out.println("\n\n" + resultBody + "\n\n");
        Assert.assertNotNull(resultBody);
         resultResource = deserializeTagListResource(resultBody);
        Assert.assertNotNull(resultResource);

        // check that results do  contain "some green things"
        TagResource foundSomeGreenThing = resultResource.getEmbeddedList().getTagResourceList().stream()
                .filter(t -> t.getTag().getName().equalsIgnoreCase("some green thing"))
                .findFirst()
                .orElse(null);
        // check that results do not contain "carrots"
        TagResource notFoundCarrots = resultResource.getEmbeddedList().getTagResourceList().stream()
                .filter(t -> t.getTag().getName().equalsIgnoreCase("carrots"))
                .findFirst()
                .orElse(null);
        Assert.assertNull(notFoundCarrots);
        Assert.assertNotNull(foundSomeGreenThing);
    }


    private String json(Object o) throws IOException {
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        return objectMapper.writeValueAsString(o);
    }

    private TagListResource deserializeTagListResource(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, TagListResource.class);

    }
}
