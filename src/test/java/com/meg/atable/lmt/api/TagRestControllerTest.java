package com.meg.atable.lmt.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.meg.atable.Application;
import com.meg.atable.auth.service.impl.JwtUser;
import com.meg.atable.lmt.api.model.Tag;
import com.meg.atable.lmt.api.model.TagType;
import com.meg.atable.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.atable.lmt.data.entity.TagEntity;
import com.meg.atable.lmt.data.entity.TagRelationEntity;
import com.meg.atable.lmt.data.repository.TagRelationRepository;
import com.meg.atable.lmt.data.repository.TagRepository;
import com.meg.atable.lmt.service.ListLayoutService;
import com.meg.atable.test.TestConstants;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@AutoConfigureJsonTesters
@ActiveProfiles("test")
public class TagRestControllerTest {

    private static UserDetails userDetails;
    @Autowired
    private
    ObjectMapper objectMapper;
    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));
    private MediaType contentTypeWithHal = new MediaType(MediaType.APPLICATION_JSON.getType(),
            "hal+json",
            Charset.forName("utf8"));
    private MockMvc mockMvc;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private ListLayoutService listLayoutService;
    @Autowired
    private TagRelationRepository tagRelationRepository;
    @Autowired
    private WebApplicationContext webApplicationContext;


    @Before
    @WithMockUser
    public void setup() throws Exception {

        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();


        userDetails = new JwtUser(TestConstants.USER_1_ID,
                TestConstants.USER_1_NAME,
                null,
                null,
                null,
                true,
                null);


    }

    @Test
    public void readSingleTag() throws Exception {
        Long testId = TestConstants.TAG_1_ID;
        String url = "/tag/" + testId;
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.tag.tag_id", Matchers.isA(String.class)))
                .andExpect(jsonPath("$.tag.tag_id").value(testId));

    }

    @Test
    public void readTags() throws Exception {
        mockMvc.perform(get("/tag"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeWithHal));

    }

    @Test
    public void readTags_extended() throws Exception {
        MvcResult result = mockMvc.perform(get("/tag?extended=true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeWithHal))
                .andReturn();
        Assert.assertNotNull(result);
        String resultString = result.getResponse().getContentAsString();
        Assert.assertTrue(resultString.contains("parent_id\":null"));
        Assert.assertTrue(resultString.contains("parent_id\":\"393\""));
    }

    @Test
    public void createTag() throws Exception {
        Tag newtag = new Tag("created tag");
        newtag.tagType(TagType.Rating.name());
        String tagJson = json(newtag);

        this.mockMvc.perform(post("/tag")
                .contentType(contentType)
                .content(tagJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void createTag_checkDefaults() throws Exception {
        Tag newtag = new Tag("created tag with defaults");
        newtag.tagType(TagType.Rating.name());
        String tagJson = json(newtag);

        MvcResult result = this.mockMvc.perform(post("/tag")
                .contentType(contentType)
                .content(tagJson))
                .andExpect(status().isCreated())
                .andReturn();

        Assert.assertNotNull(result);
        List<String> headers = result.getResponse().getHeaders("Location");
        String[] locationParts = headers.get(0).split("/");
        String id = locationParts[locationParts.length - 1];
        Assert.assertNotNull(id);

        Optional<TagEntity> tagResult = tagRepository.findById(new Long(id));
        // verify that tag with found id belongs to default group
        Assert.assertTrue(tagResult.isPresent());
        Optional<TagRelationEntity> tagRelationEntity = tagRelationRepository.findByChild(tagResult.get());
        Assert.assertTrue(tagRelationEntity.isPresent());
        Optional<TagEntity> tagParent = tagRepository.findById(tagRelationEntity.get().getParent().getId());
        Assert.assertTrue(tagParent.isPresent());
        Assert.assertTrue(tagParent.get().getTagTypeDefault());
        // verify that tag with id belongs to default category
        List<ListLayoutCategoryEntity> categories = listLayoutService.getCategoriesForTag(tagResult.get());
        for (ListLayoutCategoryEntity category : categories) {
            Assert.assertTrue(category.getDefault());
        }
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
        toUpdate.setDishes(new ArrayList<>());
        toUpdate.setCategories(new ArrayList<>());
        String tagJson = json(toUpdate);

        this.mockMvc.perform(put("/tag/" + toUpdate.getId())
                .contentType(contentType)
                .content(tagJson))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void moveTag() throws Exception {
        String url = "/tag/" + TestConstants.PARENT_TAG_ID_2
                + "/child/" + TestConstants.CHILD_TAG_ID_1;

        Optional<TagEntity> toUpdateOpt = tagRepository.findById(TestConstants.CHILD_TAG_ID_1);
        Assert.assertTrue(toUpdateOpt.isPresent());
        TagEntity toUpdate = toUpdateOpt.get();
        String updateName = "updated:" + toUpdate.getName();
        String updateDescription = "updated:" + (toUpdate.getDescription() == null ? "" : toUpdate.getDescription());
        toUpdate.setName(updateName);
        toUpdate.setDescription(updateDescription);
        toUpdate.setDishes(new ArrayList<>());

        this.mockMvc.perform(put(url))
                .andExpect(status().is2xxSuccessful());
    }


    @Test
    public void addAsChild() throws Exception {
        String url = "/tag/" + TestConstants.PARENT_TAG_ID_2 + "/child";

        Tag tag = new Tag("testTag");
        tag = tag.tagType(TagType.Rating.name());
        String tagString = json(tag);

        this.mockMvc.perform(post(url)
                .with(user(userDetails))
                .contentType(contentType)
                .content(tagString))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void addChildren() throws Exception {
        String url = "/tag/" + TestConstants.PARENT_TAG_ID_1 + "/children?tagIds=" + TestConstants.TAG_MEAT + "," + TestConstants.TAG_CARROTS + "," + TestConstants.TAG_CROCKPOT;

        this.mockMvc.perform(post(url).contentType(contentType))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void assignChildToBaseTag() throws Exception {
        String url = "/tag/" + TestConstants.PARENT_TAG_ID_1 + "/child/" + TestConstants.TAG_MEAT;

        this.mockMvc.perform(put(url)
                .with(user(userDetails)))
                .andExpect(status().is2xxSuccessful());

    }

    @Test
    public void getChildrenTagDishAssignments() throws Exception {
        String url = "/tag/" + TestConstants.PARENT_TAG_ID_1 + "/dish";

        this.mockMvc.perform(get(url)
                .with(user(userDetails)))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @WithMockUser
    public void replaceTagsInDishes() throws Exception {
        this.mockMvc.perform(put("/tag/" + TestConstants.TAG_CARROTS + "/dish/" + TestConstants.TAG_MEAT)
                .with(user(userDetails)))
                .andExpect(status().is2xxSuccessful());
    }


    private String json(Object o) throws IOException {
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        return objectMapper.writeValueAsString(o);
    }

}
