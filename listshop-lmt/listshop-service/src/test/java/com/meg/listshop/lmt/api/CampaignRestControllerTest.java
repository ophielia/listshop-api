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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@Testcontainers
@WebAppConfiguration
@ActiveProfiles("test")
class CampaignRestControllerTest {

    @Container
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

        Assertions.assertNotNull("the JSON message converter must not be null");
    }

    private MockMvc mockMvc;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;


    @BeforeEach
    @WithMockUser
    public void setup() {

        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }


    @Test
    void testHappyPath() throws Exception {
        String testinput = "{" +
                "\"email\": \"email@test.com\", " +
                "\"campaign\": \"betaCampaign\" " +
                ",\"text\": \"some random text\" " +
                "}";
        mockMvc.perform(post("/campaign")
                        .content(testinput).contentType(contentType))
                .andExpect(status().isNoContent())
                .andDo(print());


    }

    @Test
    void testValidation() throws Exception {
        String testinput = "{" +
                "\"email\": \"okddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaign\", " +
                "\"campaign\": \"okddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaign\" " +
                ",\"text\": \"okddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaign\" " +
                "}";
        mockMvc.perform(post("/campaign")
                        .content(testinput).contentType(contentType))
                .andExpect(status().isBadRequest())
                .andDo(print());

        testinput = "{" +
                "\"email\": \"email\", " +
                "\"campaign\": \"okddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaign\" " +
                ",\"text\": \"okddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaignokddssssssssssssssssssscampaign\" " +
                "}";
        mockMvc.perform(post("/campaign")
                        .content(testinput).contentType(contentType))
                .andExpect(status().isBadRequest())
                .andDo(print());


        testinput = "{" +
                "\"email\": \"ok\", " +
                "\"campaign\": \"ok\" " +
                ",\"text\": \"ok\" " +
                "}";
        mockMvc.perform(post("/campaign")
                        .content(testinput).contentType(contentType))
                .andExpect(status().isBadRequest())
                .andDo(print());

        testinput = "{" +
                "\"email\": \"a\\u0000b\\u0007c\\u008fd\", " +
                "\"campaign\": \"beta_campaign\", " +
                "\"text\": \"beta_campaign\" " +
                "}";
        mockMvc.perform(post("/campaign")
                        .content(testinput).contentType(contentType))
                .andExpect(status().isNoContent())
                .andDo(print());


        testinput = "{" +
                "\"email\": \"1\", " +
                "\"campaign\": \"2\" " +
                ",\"text\": \"2\" " +
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
