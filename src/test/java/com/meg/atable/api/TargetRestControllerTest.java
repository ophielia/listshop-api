package com.meg.atable.api;

import com.meg.atable.Application;
import com.meg.atable.api.model.ModelMapper;
import com.meg.atable.api.model.Target;
import com.meg.atable.api.model.TargetSlot;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.service.JwtUser;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.data.entity.TargetEntity;
import com.meg.atable.data.entity.TargetSlotEntity;
import com.meg.atable.data.repository.TargetRepository;
import com.meg.atable.data.repository.TargetSlotRepository;
import com.meg.atable.service.TagService;
import com.meg.atable.service.TargetService;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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


    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserService userService;

    @Autowired
    private TagService tagService;

    @Autowired
    private TargetRepository targetRepository;

    @Autowired
    private TargetSlotRepository targetSlotRepository;
    private final static String userName = "targetControllerTest";

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        this.mappingJackson2HttpMessageConverter = Arrays.stream(converters)

                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null");
    }

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));
    private MockMvc mockMvc;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;
    private static boolean setUpComplete = false;
    private static UserAccountEntity userAccount;
    private static UserDetails userDetails;
    private static UserDetails newUserDetails;
    private static UserAccountEntity newUserAccount;
    private static TagEntity tag1;
    private static TagEntity tag21;
    private static TagEntity tag31;
    private static TargetEntity target1;
    private static TargetEntity target2;
    private static TargetEntity target3;
    private static TargetSlotEntity targetSlotEntity;
    private static TargetSlotEntity target1Slot;
    private static TagEntity dishTypeTag;

    @Before
    @WithMockUser
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        if (setUpComplete) {
            return;
        }
        // make tags
        dishTypeTag = new TagEntity("dishTypeTag", "main1");
        tag1 = new TagEntity("tag1", "main1");
        tag21 = new TagEntity("tag1", "main1");
        tag31 = new TagEntity("tag1", "main1");

        tag1 = tagService.save(tag1);
        dishTypeTag = tagService.save(dishTypeTag);
        tag21 = tagService.save(tag21);
        tag31 = tagService.save(tag31);
        String tagString = tag1.getId() + ";" + tag21.getId();
        // make users
        userAccount = userService.save(new UserAccountEntity(userName, "password"));
        userDetails = new JwtUser(this.userAccount.getId(),
                userName,
                null,
                null,
                null,
                true,
                null);
        newUserAccount = userService.save(new UserAccountEntity("newUserTRCT", "password"));
        newUserDetails = new JwtUser(this.newUserAccount.getId(),
                "newUserTRCT",
                null,
                null,
                null,
                true,
                null);
        TargetEntity targetEntity = new TargetEntity();
        targetEntity.setUserId(userAccount.getId());
        targetEntity.setTargetName("testTarget");
        targetEntity.setTargetTagIds(tagString);
        targetEntity.setSlots(null);
        targetEntity.setCreated(new Date());
        target1 = targetRepository.save(targetEntity);
        TargetSlotEntity slot = new TargetSlotEntity();
        slot.setTargetId(target1.getTargetId());
        slot.setSlotOrder(1);
        slot.setSlotDishTagId(dishTypeTag.getId());
        slot.addTagId(tag21.getId());
        target1Slot = targetSlotRepository.save(slot);
        target1.addSlot(target1Slot);
        target1 = targetRepository.save(target1);


        targetEntity = new TargetEntity();
        targetEntity.setUserId(userAccount.getId());
        targetEntity.setTargetName("testTarget");
        targetEntity.setTargetTagIds(tagString);
        targetEntity.setSlots(null);
        targetEntity.setCreated(new Date());

        target2 = targetRepository.save(targetEntity);
        targetSlotEntity = new TargetSlotEntity();
        targetSlotEntity.setTargetId(target2.getTargetId());
        targetSlotEntity.setSlotDishTagId(dishTypeTag.getId());
        targetSlotEntity = targetSlotRepository.save(targetSlotEntity);
        target2.addSlot(targetSlotEntity);
        target2 = targetRepository.save(targetEntity);

        targetEntity = new TargetEntity();
        targetEntity.setUserId(newUserAccount.getId());
        targetEntity.setTargetName("testTarget3");
        targetEntity.setTargetTagIds(tagString);
        targetEntity.setSlots(null);
        targetEntity.setCreated(new Date());

        target3 = targetRepository.save(targetEntity);
        targetSlotEntity = new TargetSlotEntity();
        targetSlotEntity.setTargetId(target3.getTargetId());
        targetSlotEntity.setSlotDishTagId(dishTypeTag.getId());
        targetSlotEntity.setTargetTagIds(tag1.getId().toString());
        targetSlotEntity = targetSlotRepository.save(targetSlotEntity);
        target3.addSlot(targetSlotEntity);
        target3 = targetRepository.save(target3);
        setUpComplete = true;

    }


    @Test
    @WithMockUser
    public void testReadTarget() throws Exception {
        Long testId = this.target1.getTargetId();
        MvcResult result = mockMvc.perform(get("/target/"
                + this.target1.getTargetId())
                .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.target.target_id", Matchers.isA(Number.class)))
                .andExpect(jsonPath("$.target.target_id").value(testId))
                .andReturn();
    }

    @Test
    @WithMockUser
    public void testRetrieveTargets() throws Exception {
        Long testId = this.target1.getTargetId();
        Long testId2 = this.target2.getTargetId();
        mockMvc.perform(get("/target")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$._embedded.targetResourceList", hasSize(3)));
    }

    @Test
    @WithMockUser
    public void testDeleteTarget() throws Exception {
        Long testId = this.target3.getTargetId();
        mockMvc.perform(delete("/target/"
                + this.target3.getTargetId())
                .with(user(newUserDetails)))
                .andExpect(status().isNoContent());

    }

    @Test
    @WithMockUser
    public void testCreateTarget() throws Exception {
        JwtUser createUserDetails = new JwtUser(userAccount.getId(),
                userName,
                null,
                null,
                null,
                true,
                null);
        TargetEntity targetEntity = new TargetEntity();
        targetEntity.setTargetName("targetCreate");
        targetEntity.setUserId(userAccount.getId());
        Target target = ModelMapper.toModel(targetEntity);
        String targetJson = json(target);

        this.mockMvc.perform(post("/target")
                .with(user(createUserDetails))
                .contentType(contentType)
                .content(targetJson))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    public void testUpdateTarget() throws Exception {
        String newName = "new super duper name";
        Target toUpdate = ModelMapper.toModel(this.target1);
        toUpdate.targetName(newName);
        String targetJson = json(toUpdate);

        this.mockMvc.perform(put("/target/" + this.target1.getTargetId())
                .with(user(userDetails))
                .contentType(contentType)
                .content(targetJson))
                .andExpect(status().isCreated());
    }


    @Test
    @WithMockUser
    public void testAddSlotToTarget() throws Exception {
        TargetSlotEntity slot = new TargetSlotEntity();
        slot.setSlotDishTagId(tag1.getId());
        TargetSlot slotDTO = ModelMapper.toModel(slot);
        String targetJson = json(slotDTO);

        String url = "/target/" + this.target1.getTargetId()
                + "/slot";
        this.mockMvc.perform(post(url)
                .with(user(userDetails))
                .contentType(contentType)
                .content(targetJson))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    public void testDeleteSlotFromTarget() throws Exception {
        String url = "/target/" + this.target1.getTargetId() + "/slot/"
                + this.target1Slot.getId();
        this.mockMvc.perform(delete(url)
                .with(user(userDetails))
                .contentType(contentType))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    public void testAddTagToSlot() throws Exception {
        String url = "/target/" + this.target3.getTargetId() + "/slot/"
                + this.targetSlotEntity.getId() + "/tag/" + tag21.getId();
        this.mockMvc.perform(post(url)
                .with(user(newUserDetails))
                .contentType(contentType))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    public void testDeleteTagFromSlot() throws Exception {
        String url = "/target/" + this.target3.getTargetId() + "/slot/"
                + this.targetSlotEntity.getId() + "/tag/" + tag1.getId();
        this.mockMvc.perform(delete(url)
                .with(user(newUserDetails))
                .contentType(contentType))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    public void testAddTagToTarget() throws Exception {
        String url = "/target/" + this.target3.getTargetId()
                +  "/tag/" + tag21.getId();
        this.mockMvc.perform(post(url)
                .with(user(newUserDetails))
                .contentType(contentType))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    public void testDeleteTagFromTarget() throws Exception {
        String url = "/target/" + this.target3.getTargetId()
                + "/tag/" + tag1.getId();
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
