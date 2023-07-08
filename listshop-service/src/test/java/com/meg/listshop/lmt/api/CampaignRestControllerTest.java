/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.lmt.api;

import com.meg.listshop.Application;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.data.repository.ItemRepository;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
public class CampaignRestControllerTest {

    @ClassRule
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();


    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype());
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        this.mappingJackson2HttpMessageConverter = Arrays.stream(converters)

                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null");
    }

    private MockMvc mockMvc;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;


    @Before
    @WithMockUser
    public void setup() {

        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }


    @Test
    public void testHappyPath() throws Exception {
        String testinput = "{" +
                "\"email\": \"okemail\", " +
                "\"campaign\": \"okcampaign\" " +
                "}";
        mockMvc.perform(post("/campaign")
                        .content(testinput).contentType(contentType))
                .andExpect(status().isNoContent())
                .andDo(print());


    }

    @Test
    public void testValidation() throws Exception {
        String testinput = "{" +
                "\"email\": \"okddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaign\", " +
                "\"campaign\": \"okddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaign\" " +
                "}";
        mockMvc.perform(post("/campaign")
                        .content(testinput).contentType(contentType))
                .andExpect(status().isBadRequest())
                .andDo(print());

        testinput = "{" +
                "\"email\": \"email\", " +
                "\"campaign\": \"okddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaign\" " +
                "}";
        mockMvc.perform(post("/campaign")
                        .content(testinput).contentType(contentType))
                .andExpect(status().isBadRequest())
                .andDo(print());

        testinput = "{" +
                "\"email\": \"okddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaign\", " +
                "\"campaign\": \"campaign\" " +
                "}";
        mockMvc.perform(post("/campaign")
                        .content(testinput).contentType(contentType))
                .andExpect(status().isBadRequest())
                .andDo(print());

        testinput = "{" +
                "\"email\": \"ok\", " +
                "\"campaign\": \"ok\" " +
                "}";
        mockMvc.perform(post("/campaign")
                        .content(testinput).contentType(contentType))
                .andExpect(status().isBadRequest())
                .andDo(print());

        testinput = "{" +
                "\"email\": \"a\\u0000b\\u0007c\\u008fd\", " +
                "\"campaign\": \"beta_campaign\" " +
                "}";
        mockMvc.perform(post("/campaign")
                        .content(testinput).contentType(contentType))
                .andExpect(status().isNoContent())
                .andDo(print());


        testinput = "{" +
                "\"email\": \"1\", " +
                "\"campaign\": \"2\" " +
                "}";
        mockMvc.perform(post("/campaign")
                        .content(testinput).contentType(contentType))
                .andExpect(status().isBadRequest())
                .andDo(print());

        testinput = "{" +

                "}";
        mockMvc.perform(post("/campaign")
                        .content(testinput).contentType(contentType))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}
