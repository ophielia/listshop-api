/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.meg.listshop.Application;
import com.meg.listshop.auth.service.impl.JwtUser;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.repository.TagRelationRepository;
import com.meg.listshop.lmt.data.repository.TagRepository;
import com.meg.listshop.lmt.service.ListLayoutService;
import com.meg.listshop.test.TestConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
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
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@AutoConfigureJsonTesters
@ActiveProfiles("test")
public class AdminTagRestControllerTest {

    @ClassRule
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    private static UserDetails userDetails;
    @Autowired
    private
    ObjectMapper objectMapper;
    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype());

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
        toUpdate.setDishes(new ArrayList<>());
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
    public void addChildren() throws Exception {
        String url = "/admin/tag/" + TestConstants.PARENT_TAG_ID_1 + "/children?tagIds=" + TestConstants.TAG_MEAT + "," + TestConstants.TAG_CARROTS + "," + TestConstants.TAG_CROCKPOT;

        this.mockMvc.perform(post(url).contentType(contentType)
                        .with(user(userDetails)))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void moveTag() throws Exception {
        String url = "/admin/tag/" + TestConstants.PARENT_TAG_ID_2
                + "/child/" + TestConstants.CHILD_TAG_ID_1;

        Optional<TagEntity> toUpdateOpt = tagRepository.findById(TestConstants.CHILD_TAG_ID_1);
        Assert.assertTrue(toUpdateOpt.isPresent());
        TagEntity toUpdate = toUpdateOpt.get();
        String updateName = "updated:" + toUpdate.getName();
        String updateDescription = "updated:" + (toUpdate.getDescription() == null ? "" : toUpdate.getDescription());
        toUpdate.setName(updateName);
        toUpdate.setDescription(updateDescription);
        toUpdate.setDishes(new ArrayList<>());

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

    private String json(Object o) throws IOException {
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        return objectMapper.writeValueAsString(o);
    }


}