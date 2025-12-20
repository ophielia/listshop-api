/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.lmt.api;

import com.meg.listshop.Application;
import com.meg.listshop.auth.api.model.TargetType;
import com.meg.listshop.auth.service.CustomUserDetails;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.api.model.ModelMapper;
import com.meg.listshop.lmt.api.model.Target;
import com.meg.listshop.lmt.api.model.TargetSlot;
import com.meg.listshop.lmt.data.entity.TargetEntity;
import com.meg.listshop.lmt.data.entity.TargetSlotEntity;
import com.meg.listshop.lmt.service.TargetService;
import com.meg.listshop.test.TestConstants;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
public class TargetRestControllerTest {

    @ClassRule
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();


    private static UserDetails userDetails;
    private static UserDetails newUserDetails;
    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype());

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private TargetService targetService;

    private MockMvc mockMvc;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        this.mappingJackson2HttpMessageConverter = Arrays.stream(converters)

                .filter(MappingJackson2HttpMessageConverter.class::isInstance)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null");
    }

    @Before
    @WithMockUser
    void setup() {
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
        newUserDetails = new CustomUserDetails(TestConstants.USER_3_ID,
                TestConstants.USER_3_NAME,
                null,
                null,
                null,
                true,
                null);
    }


    @Test
    @WithMockUser
    public void testReadTarget() throws Exception {
        Long testId = TestConstants.TARGET_1_ID;
        mockMvc.perform(get("/target/"
                        + testId)
                        .with(user(newUserDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.target.target_id", Matchers.isA(Number.class)))
                .andExpect(jsonPath("$.target.target_id").value(testId))
                .andReturn();
    }

    @Test
    @WithMockUser
    @Ignore
    public void testRetrieveTargets() throws Exception {

        mockMvc.perform(get("/target")
                        .with(user(newUserDetails)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$._embedded.targetResourceList", hasSize(3)));
    }

    @Test
    @WithMockUser
    public void testDeleteTarget() throws Exception {
        Long testId = TestConstants.TARGET_2_ID;
        mockMvc.perform(delete("/target/"
                        + testId)
                        .with(user(userDetails)))
                .andExpect(status().isNoContent());

    }

    @Test
    @WithMockUser
    public void testCreateTarget() throws Exception {
        TargetEntity targetEntity = new TargetEntity();
        targetEntity.setTargetName("targetCreate");
        targetEntity.setUserId(TestConstants.USER_3_ID);
        targetEntity.setTargetType(TargetType.Standard);
        Target target = ModelMapper.toModel(targetEntity);
        String targetJson = json(target);

        this.mockMvc.perform(post("/target")
                        .with(user(newUserDetails))
                        .contentType(contentType)
                        .content(targetJson))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    public void testCreatePickupTarget() throws Exception {
        TargetEntity targetEntity = new TargetEntity();
        targetEntity.setTargetName("targetCreate");
        targetEntity.setUserId(TestConstants.USER_3_ID);
        targetEntity.setTargetType(TargetType.PickUp);
        Target target = ModelMapper.toModel(targetEntity);
        String targetJson = json(target);

        String tagids = "?pickupTags=" + TestConstants.TAG_CARROTS + "," + TestConstants.TAG_MEAT;
        String url = "/target/pickup" + tagids;
        this.mockMvc.perform(post(url)
                        .with(user(newUserDetails))
                        .contentType(contentType)
                        .content(targetJson))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    public void testUpdateTarget() throws Exception {
        String newName = "new super duper name";
        TargetEntity targetEntity = targetService.getTargetById(TestConstants.USER_3_NAME, TestConstants.TARGET_3_ID);

        Target toUpdate = ModelMapper.toModel(targetEntity);
        toUpdate.targetName(newName);
        String targetJson = json(toUpdate);

        this.mockMvc.perform(put("/target/" + targetEntity.getTargetId())
                        .with(user(newUserDetails))
                        .contentType(contentType)
                        .content(targetJson))
                .andExpect(status().isCreated());
    }


    @Test
    @WithMockUser
    public void testAddSlotToTarget() throws Exception {
        TargetSlotEntity slot = new TargetSlotEntity();
        slot.setSlotDishTagId(TestConstants.TAG_MAIN_DISH);
        TargetSlot slotDTO = ModelMapper.toModel(slot);
        String targetJson = json(slotDTO);

        String url = "/target/" + TestConstants.TARGET_3_ID
                + "/slot";
        this.mockMvc.perform(post(url)
                        .with(user(newUserDetails))
                        .contentType(contentType)
                        .content(targetJson))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    public void testDeleteSlotFromTarget() throws Exception {
        TargetEntity targetEntity = targetService.getTargetById(TestConstants.USER_3_NAME, TestConstants.TARGET_3_ID);
        TargetSlotEntity slot = targetEntity.getSlots().get(0);
        String url = "/target/" + TestConstants.TARGET_3_ID + "/slot/"
                + slot.getId();
        this.mockMvc.perform(delete(url)
                        .with(user(newUserDetails))
                        .contentType(contentType))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    public void testAddTagToSlot() throws Exception {
        TargetEntity targetEntity = targetService.getTargetById(TestConstants.USER_3_NAME, TestConstants.TARGET_3_ID);
        TargetSlotEntity slot = targetEntity.getSlots().get(0);

        String url = "/target/" + TestConstants.TARGET_3_ID + "/slot/"
                + slot.getId() + "/tag/" + TestConstants.TAG_CROCKPOT;
        this.mockMvc.perform(post(url)
                        .with(user(newUserDetails))
                        .contentType(contentType))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    public void testDeleteTagFromSlot() throws Exception {
        String url = "/target/" + TestConstants.TARGET_3_ID + "/slot/"
                + TestConstants.TARGET_3_SLOT_ID + "/tag/" + TestConstants.TAG_CARROTS;
        this.mockMvc.perform(delete(url)
                        .with(user(newUserDetails))
                        .contentType(contentType))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    public void testAddTagToTarget() throws Exception {
        String url = "/target/" + TestConstants.TARGET_3_ID
                + "/tag/" + TestConstants.CHILD_TAG_ID_1;
        this.mockMvc.perform(post(url)
                        .with(user(newUserDetails))
                        .contentType(contentType))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    public void testDeleteTagFromTarget() throws Exception {
        String url = "/target/" + TestConstants.TARGET_3_ID
                + "/tag/" + TestConstants.TAG_EASE_OF_PREP;
        this.mockMvc.perform(delete(url)
                        .with(user(newUserDetails))
                        .contentType(contentType))
                .andExpect(status().isNoContent());
    }

    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
