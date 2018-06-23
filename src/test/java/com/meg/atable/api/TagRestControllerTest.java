package com.meg.atable.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.meg.atable.Application;
import com.meg.atable.api.model.Tag;
import com.meg.atable.api.model.TagType;
import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.data.repository.TagRepository;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Optional;

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
    private WebApplicationContext webApplicationContext;

/*
    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {


        assertNotNull("the JSON message converter must not be null");
    }
 */

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
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
    public void updateTag() throws Exception {
        Optional<TagEntity> toUpdateOpt = tagRepository.findById(TestConstants.TAG_3_ID);
        Assert.assertTrue(toUpdateOpt.isPresent());
        TagEntity toUpdate = toUpdateOpt.get();
        String updateName = "updated:" + toUpdate.getName();
        String updateDescription = "updated:" + (toUpdate.getDescription() == null ? "" : toUpdate.getDescription());
        toUpdate.setName(updateName);
        toUpdate.setDescription(updateDescription);
        toUpdate.setDishes(new ArrayList<>());
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


    private String json(Object o) throws IOException {
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        return objectMapper.writeValueAsString(o);

    }

}
