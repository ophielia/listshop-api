/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.lmt.api.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.meg.listshop.Application;
import com.meg.listshop.auth.service.CustomUserDetails;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.api.model.Tag;
import com.meg.listshop.lmt.api.model.TagListResource;
import com.meg.listshop.lmt.api.model.TagResource;
import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.data.repository.TagRelationRepository;
import com.meg.listshop.lmt.data.repository.TagRepository;
import com.meg.listshop.lmt.service.LayoutService;
import com.meg.listshop.test.TestConstants;
import io.restassured.RestAssured;
import jakarta.annotation.PostConstruct;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@ExtendWith(SpringExtension.class)
@Testcontainers
@SpringBootTest(classes = Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class V2TagRestControllerTest {

    @Container
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    @LocalServerPort
    public int serverPort;

    private static UserDetails userDetails;
    @Autowired
    private ObjectMapper objectMapper;
    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype());

    private String token = TestConstants.USER_1_TOKEN;

    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private LayoutService listLayoutService;
    @Autowired
    private TagRelationRepository tagRelationRepository;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @PostConstruct
    public void initRestAssured() {
        RestAssured.port = serverPort;
        RestAssured.urlEncodingEnabled = false;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    @WithMockUser
    public void setup() throws Exception {

        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();


        userDetails = new CustomUserDetails(TestConstants.USER_1_ID,
                TestConstants.USER_1_EMAIL,
                null,
                null,
                null,
                true,
                null);


    }

    @Test
    void readSingleTag() throws Exception {
        Long testId = TestConstants.TAG_1_ID;
        String url = "/tag/" + testId;
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.tag.tag_id", Matchers.isA(String.class)))
                .andExpect(jsonPath("$.tag.tag_id").value(testId));

    }

    @Test
    void addAsChild() throws Exception {
        String url = "/tag/" + TestConstants.PARENT_TAG_ID_2 + "/child";

        Tag tag = new Tag("testTag");
        tag = tag.tagType(TagType.Rating.name());
        String tagString = json(tag);

        MvcResult result = this.mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .contentType(contentType)
                        .content(tagString))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        // affirm that newly created tag has user_id matching user_details
        // and parent id matching parent_tag_id_2
        // get newly created id
        String locationValue = result.getResponse().getHeader("Location");
        String idString = locationValue.substring(locationValue.lastIndexOf("/") + 1);
        Long newId = Long.valueOf(idString);

        String fetchUrl = "/tag/" + newId;
        mockMvc.perform(get(fetchUrl))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.tag.user_id").value(TestConstants.USER_1_ID));

        // now, check that parent id is correct"
        MvcResult allResultForParent = this.mockMvc.perform(get("/tag/user")
                        .with(user(userDetails))
                        .contentType(contentType))
                .andReturn();

        String resultJson = allResultForParent.getResponse().getContentAsString();
        TagListResource afterList = objectMapper.readValue(resultJson, TagListResource.class);
        Optional<TagResource> resultTag = afterList.getEmbeddedList().getTagResourceList().stream()
                .filter(t -> t.getTag().getId().equals(idString))
                .findFirst();
        Assertions.assertNotNull(resultTag);
        Assertions.assertEquals(String.valueOf(TestConstants.PARENT_TAG_ID_2), resultTag.get().getTag().getParentId(), "parent tag id should equal that given for create call");

    }

    @Test
    void addAsChild_Exists() throws Exception {
        Long parentId = 88L; // prepared meats
        String url = "/tag/" + parentId + "/child";

        Tag tag = new Tag("Meaty Ingredient");
        tag = tag.tagType(TagType.Ingredient.name());
        String tagString = json(tag);

        MvcResult result = this.mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .contentType(contentType)
                        .content(tagString))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        // get newly created id
        String locationValue = result.getResponse().getHeader("Location");
        String idString = locationValue.substring(locationValue.lastIndexOf("/") + 1);
        Long newId = Long.valueOf(idString);

        // now, try to recreate the same tag
        MvcResult resultRecreate = this.mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .contentType(contentType)
                        .content(tagString))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        // get the resulting id
        String recreateLocationValue = resultRecreate.getResponse().getHeader("Location");
        String recreateIdString = recreateLocationValue.substring(recreateLocationValue.lastIndexOf("/") + 1);
        Long recreatedId = Long.valueOf(recreateIdString);

        Assertions.assertEquals(newId, recreatedId);
    }

    @Test
    void addAsChild_Standard() throws Exception {
        String url = "/tag/" + TestConstants.PARENT_TAG_ID_2 + "/child?asStandard=true";

        Tag tag = new Tag("testTag-standard");
        tag = tag.tagType(TagType.Rating.name());
        String tagString = json(tag);

        MvcResult result = this.mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .contentType(contentType)
                        .content(tagString))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        // affirm that newly created tag has user_id matching user_details
        // and parent id matching parent_tag_id_2
        // get newly created id
        String locationValue = result.getResponse().getHeader("Location");
        String idString = locationValue.substring(locationValue.lastIndexOf("/") + 1);

        // now, check that parent id is correct"
        MvcResult allResultForParent = this.mockMvc.perform(get("/tag/user")
                        .with(user(userDetails))
                        .contentType(contentType))
                .andReturn();

        String resultJson = allResultForParent.getResponse().getContentAsString();
        TagListResource afterList = objectMapper.readValue(resultJson, TagListResource.class);
        Optional<TagResource> resultTag = afterList.getEmbeddedList().getTagResourceList().stream()
                .filter(t -> t.getTag().getId().equals(idString))
                .findFirst();
        Assertions.assertNotNull(resultTag);
        Assertions.assertEquals(String.valueOf(TestConstants.PARENT_TAG_ID_2), resultTag.get().getTag().getParentId(), "parent tag id should equal that given for create call");
        Assertions.assertEquals(String.valueOf(0L), resultTag.get().getTag().getUserId(), "user id should be 0");

    }

    @Test
    void addAsChild_StandardExists() throws Exception {
        Long parentId = 88L;
        String url = "/tag/" + parentId + "/child?asStandard=true";

        Tag tag = new Tag("mystery MEAT");
        tag = tag.tagType(TagType.Ingredient.name());
        String tagString = json(tag);

        MvcResult result = this.mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .contentType(contentType)
                        .content(tagString))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        // get newly created id
        String locationValue = result.getResponse().getHeader("Location");
        String idString = locationValue.substring(locationValue.lastIndexOf("/") + 1);

        // recreate same tag
        Tag recreateTag = new Tag("MYSTERY MEAT");
        recreateTag = recreateTag.tagType(TagType.Ingredient.name());
        String recreateTagString = json(recreateTag);

        MvcResult recreateResult = this.mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .contentType(contentType)
                        .content(recreateTagString))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        String recreateLocation = result.getResponse().getHeader("Location");
        String recreateId = recreateLocation.substring(recreateLocation.lastIndexOf("/") + 1);

        Assertions.assertEquals(idString, recreateId);
    }


    private String json(Object o) throws IOException {
        return objectMapper.writeValueAsString(o);
    }

}
