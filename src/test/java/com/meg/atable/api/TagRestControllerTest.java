package com.meg.atable.api;

import com.meg.atable.Application;
import com.meg.atable.api.model.Tag;
import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.service.TagService;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
public class TagRestControllerTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));


    private MediaType contentTypeWithHal = new MediaType(MediaType.APPLICATION_JSON.getType(),
            "hal+json",
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    private String userName = "testname";

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private TagEntity parentTag;
    private TagEntity level1;
    private TagEntity level2;

    private TagEntity tag;

    private List<TagEntity> tagList = new ArrayList<>();

    @Autowired
    private TagService tagService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null");
    }

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();

        this.tagService.deleteAllRelationships();
        this.tagService.deleteAll();

        this.parentTag = tagService.save(new TagEntity("name", "description"));
        level1 = tagService.createTag(parentTag,"tag1", "desc");
        this.tagList.add(level1);
        this.tagList.add(tagService.createTag(parentTag,"tag2", "desc"));

        level2 = tagService.createTag(level1,"tag2l", "desc");
        this.tagList.add(level2);

    }


    @Test
    public void readSingleTag() throws Exception {
        Long testId = this.tagList.get(0).getId();
        String url = "/tag/" + testId;
        Class<Number> targetType = Number.class;
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.tag.id", Matchers.isA(Number.class)))
                .andExpect(jsonPath("$.tag.id").value(testId));

    }

    @Test
    public void readTags() throws Exception {
        Long testId = this.tagList.get(0).getId().longValue();
        Long testId2 = this.tagList.get(1).getId().longValue();
        mockMvc.perform(get("/tag"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeWithHal))
                .andExpect(jsonPath("$._embedded.tagResourceList", hasSize(4)))
                .andExpect(jsonPath("$._embedded.tagResourceList[1].tag.id").value(testId))
                .andExpect(jsonPath("$._embedded.tagResourceList[1].tag.name", is("tag1")))
                .andExpect(jsonPath("$._embedded.tagResourceList[2].tag.id").value(testId2))
                .andExpect(jsonPath("$._embedded.tagResourceList[2].tag.name", is("tag2")))
                .andExpect(jsonPath("$._embedded.tagResourceList[0].tag.id").value(parentTag.getId()))
                .andExpect(jsonPath("$._embedded.tagResourceList[0].tag.name", is(parentTag.getName())));
    }

    @Test
    public void createTag() throws Exception {
        String tagJson = json(new Tag("created tag"));

        this.mockMvc.perform(post("/tag")
                .contentType(contentType)
                .content(tagJson))
                .andExpect(status().isCreated());
    }



    @Test
    public void updateTag() throws Exception {
        TagEntity toUpdate = this.tagList.get(0);
        String updateName = "updated:" + toUpdate.getName();
        String updateDescription = "updated:" + (toUpdate.getDescription()==null?"":toUpdate.getDescription());
        toUpdate.setName(updateName);
        toUpdate.setDescription(updateDescription);

        String tagJson = json(toUpdate);

        this.mockMvc.perform(put("/tag/" + toUpdate.getId())
                .contentType(contentType)
                .content(tagJson))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void moveTag() throws Exception {
        // move level2 to belong to parent instead of level1
        String url = "/taginfo/" + parentTag.getId()
                + "/child/" + level2.getId();

        TagEntity toUpdate = this.tagList.get(0);
        String updateName = "updated:" + toUpdate.getName();
        String updateDescription = "updated:" + (toUpdate.getDescription()==null?"":toUpdate.getDescription());
        toUpdate.setName(updateName);
        toUpdate.setDescription(updateDescription);

        String tagJson = json(toUpdate);

        this.mockMvc.perform(put(url))
                .andExpect(status().is2xxSuccessful());
    }


    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
