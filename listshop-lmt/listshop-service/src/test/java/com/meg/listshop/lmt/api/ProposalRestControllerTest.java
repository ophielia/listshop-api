package com.meg.listshop.lmt.api;

import com.meg.listshop.Application;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.CustomUserDetails;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.test.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.charset.Charset;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ExtendWith(SpringExtension.class)
@Testcontainers
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
class ProposalRestControllerTest {

    @Container
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    private static UserDetails userDetails;
    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserService userService;

    private MockMvc mockMvc;


    @BeforeEach
    @WithMockUser
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();


        UserEntity userAccount = userService.getUserByUserEmail(TestConstants.USER_3_NAME);
        userDetails = new CustomUserDetails(userAccount.getId(),
                TestConstants.USER_3_NAME,
                null,
                null,
                null,
                true,
                null);

    }


    @Test
    @WithMockUser
    void testGenerateProposal() throws Exception {
        String url = "/proposal/target/" + TestConstants.TARGET_1_ID;
        this.mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .contentType(contentType))
                .andExpect(status().isCreated());

    }

    @Test
    @WithMockUser
    void testGetProposal() throws Exception {
        String url = "/proposal/" + TestConstants.PROPOSAL_1_ID;
        this.mockMvc.perform(get(url)
                        .with(user(userDetails))
                        .contentType(contentType))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testRefreshProposal() throws Exception {
        String url = "/proposal/" + TestConstants.PROPOSAL_2_ID;
        this.mockMvc.perform(put(url)
                        .with(user(userDetails))
                        .contentType(contentType))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void testSelectDishInSlot() throws Exception {

        String url = "/proposal/" + TestConstants.PROPOSAL_1_ID
                + "/slot/" + TestConstants.PROPOSAL_1_SLOT_4_ID
                + "/dish/" + TestConstants.PROPOSAL_1_SLOT_4_DISH_ID;
        this.mockMvc.perform(post(url)
                        .with(user(userDetails))
                        .contentType(contentType))
                .andExpect(status().isNoContent());


    }

    @Test
    @WithMockUser
    void testClearDishFromSlot() throws Exception {
        String url = "/proposal/" + TestConstants.PROPOSAL_3_ID
                + "/slot/" + TestConstants.PROPOSAL_3_SLOT_4_ID
                + "/dish/" + TestConstants.PROPOSAL_3_SLOT_4_DISH_ID;
        this.mockMvc.perform(delete(url)
                        .with(user(userDetails))
                        .contentType(contentType))
                .andExpect(status().isNoContent());

    }

    @Test
    @WithMockUser
    void refreshProposalSlot() throws Exception {
        String url = "/proposal/" + TestConstants.PROPOSAL_2_ID
                + "/slot/3";
        this.mockMvc.perform(put(url)
                        .with(user(userDetails))
                        .contentType(contentType))
                .andExpect(status().isNoContent());
    }


}
