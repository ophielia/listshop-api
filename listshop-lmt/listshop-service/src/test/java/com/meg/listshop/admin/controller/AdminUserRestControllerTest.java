/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.admin.controller;

import com.meg.listshop.Application;
import com.meg.listshop.auth.data.entity.AuthorityName;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.CustomUserDetails;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.data.repository.TokenRepository;
import com.meg.listshop.test.TestConstants;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@Testcontainers
@ActiveProfiles("test")
@Sql(value = {"/sql/com/meg/atable/auth/api/UserRestControllerTest-rollback.sql",
        "/sql/com/meg/atable/auth/api/UserRestControllerTest.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AdminUserRestControllerTest {

    private static final Long USER_WITH_PROPERTIES_ID = 999L;
    private static final String USER_WITH_PROPERTIES_NAME = "rufus@barkingmad.com";
    @Container
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));
    private MockMvc mockMvc;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;
    private UserDetails adminUser;
    private UserDetails userDetailsAnotherChangePassword;
    private UserDetails userWithoutProperties;

    private UserDetails userWithProperties;
    private UserDetails userToDelete;
    private UserDetails userNotFound;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserService userService;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        this.mappingJackson2HttpMessageConverter = Arrays.stream(converters)
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        Assertions.assertNotNull("the JSON message converter must not be null");
    }

    @BeforeEach
    @WithMockUser
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        GrantedAuthority auth = new SimpleGrantedAuthority(AuthorityName.ROLE_ADMIN.name());
        List<GrantedAuthority> authorities = Collections.singletonList(auth);

        UserEntity userAccount = userService.getUserByUserEmail(TestConstants.USER_3_NAME);
        adminUser = new CustomUserDetails(userAccount.getId(),
                TestConstants.USER_3_NAME,
                null,
                "Passw0rd", // $2a$10$RFahccrkDPR1aUHfyS457Oc7n.2f7wU/sDUXQ.99wOvNL3xzaiPxK
                authorities ,
                true,
                null);
    }

    @Test
    @WithMockUser
    void testGetUser() throws Exception {

        String url = "/admin/user/999";
        mockMvc.perform(get(url)
                        .with(user(adminUser))
                        .contentType(contentType)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_properties", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.user_properties[*].key", Matchers.hasItem("test_property")))
                .andExpect(jsonPath("$.user_properties[*].key", Matchers.hasItem("another_property")))
                .andExpect(jsonPath("$.user_properties[*].value", Matchers.hasItem("ho hum value")))
                .andExpect(jsonPath("$.user_properties[*].value", Matchers.hasItem("good value")))
                .andDo(print());

    }

    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
