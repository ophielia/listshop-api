package com.meg.atable.api;

import com.meg.atable.Application;
import com.meg.atable.api.model.TagType;
import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.service.TagService;
import org.junit.Before;
import org.junit.Ignore;
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
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
public class TagInfoRestControllerTest {

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

        this.parentTag = buildTag(null, "name", "description", TagType.TagType);

        level1 = buildTag(parentTag, "tag1", "desc", TagType.TagType);
        this.tagList.add(level1);
        this.tagList.add(buildTag(parentTag, "tag2", "desc", TagType.TagType));

        level2 = buildTag(level1, "tag2l", "desc", TagType.TagType);
        this.tagList.add(level2);

    }

    private TagEntity buildTag(TagEntity parent, String name, String description, TagType tagType) {
        TagEntity tagEntity = new TagEntity();
        tagEntity.setName(name);
        tagEntity.setDescription(description);
        tagEntity.setTagType(tagType);
        return tagService.createTag(parent, tagEntity);
    }


    @Test
    @Ignore
    public void readTagInfo() throws Exception {
        Long testId = this.tagList.get(0).getId().longValue();
        Long testId2 = this.tagList.get(1).getId().longValue();
        mockMvc.perform(get("/taginfo"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.tagInfo.baseIds", hasSize(1)))
                .andExpect(jsonPath("$.tagInfo.tagList", hasSize(4)));
                /*.andExpect(jsonPath("$.tagInfo.tagList").value(testId))
                .andExpect(jsonPath("$._embedded.tagResourceList[1].tag.name", is("tag1")))
                .andExpect(jsonPath("$._embedded.tagResourceList[2].tag.id").value(testId2))
                .andExpect(jsonPath("$._embedded.tagResourceList[2].tag.name", is("tag2")))
                .andExpect(jsonPath("$._embedded.tagResourceList[0].tag.id").value(parentTag.getLayoutId()))
                .andExpect(jsonPath("$._embedded.tagResourceList[0].tag.name", is(parentTag.getName())));*/
    }


    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
