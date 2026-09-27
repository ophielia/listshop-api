/*
 * The List Shop
 *
 * Copyright (c) 2022-2026.
 */

package com.meg.listshop.admin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meg.listshop.Application;
import com.meg.listshop.admin.model.PostSearchTags;
import com.meg.listshop.admin.model.PostUpdateTags;
import com.meg.listshop.auth.data.entity.AuthorityName;
import com.meg.listshop.auth.service.CustomUserDetails;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.pojos.IncludeType;
import com.meg.listshop.lmt.data.pojos.TagInternalStatus;
import com.meg.listshop.lmt.data.repository.TagRepository;
import com.meg.listshop.test.TestConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ExtendWith(SpringExtension.class)
@Testcontainers
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@AutoConfigureJsonTesters
@Sql(value = {"/sql/com/meg/atable/admin/AdminTagControllerTest.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sql/com/meg/atable/admin/AdminTagControllerTest_rollback.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)

@ActiveProfiles("test")
class AdminFoodRestControllerTest {

    @Container
    public static ListShopPostgresqlContainer postgresSQLContainer = ListShopPostgresqlContainer.getInstance();

    private static UserDetails userDetails;
    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype());
    @Autowired
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;


    @BeforeEach
    @WithMockUser
    void setup() {

        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        GrantedAuthority auth = new SimpleGrantedAuthority(AuthorityName.ROLE_ADMIN.name());
        List<GrantedAuthority> authorities = Collections.singletonList(auth);

        userDetails = new CustomUserDetails(TestConstants.USER_1_ID,
                TestConstants.USER_1_EMAIL,
                null,
                null,
                authorities,
                true,
                null);
    }

    @Test
    @WithMockUser
    void testGetFoodCategoryMappings() throws Exception {
        //    @GetMapping(value = "/food/categories")
        MvcResult result = this.mockMvc.perform(get("/admin/food/category/mappings")
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


    private String json(Object o) throws IOException {
        return objectMapper.writeValueAsString(o);
    }

    private TagListResource deserializeTagListResource(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, TagListResource.class);

    }


}
