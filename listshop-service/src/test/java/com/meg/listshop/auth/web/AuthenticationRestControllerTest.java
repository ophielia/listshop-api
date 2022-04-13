package com.meg.listshop.auth.web;

import com.meg.listshop.Application;
import com.meg.listshop.auth.api.model.ClientDeviceInfo;
import com.meg.listshop.auth.api.model.ClientType;
import com.meg.listshop.auth.api.model.JwtAuthorizationRequest;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.auth.service.impl.JwtUser;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.test.TestConstants;
import org.junit.Before;
import org.junit.ClassRule;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
@Sql(value = {"/sql/com/meg/atable/auth/api/AuthenticationRestControllerTest.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sql/com/meg/atable/auth/api/AuthenticationRestControllerTest_rollback.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class AuthenticationRestControllerTest {

    @ClassRule
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();


    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));
    private MockMvc mockMvc;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;
    private UserDetails userDetails;
    private final String validToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtZSIsImF1ZGllbmNlIjoibW9iaWxlIiwiY3JlYXRlZCI6MTU5MTQ0MzI2NzE3MX0.YWjx1Y2vANS_MyGn2BsSKSi7WGBji5DT5b6hao9fdC3MPOwF_syTRNyqcoJO9J9Joj9X5DX7-0cuXRNOnC6cpQ";


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

        assertNotNull("the JSON message converter must not be null");
    }

    @Before
    @WithMockUser
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        UserEntity userAccount = userService.getUserByUserEmail(TestConstants.USER_3_NAME);
        userDetails = new JwtUser(userAccount.getId(),
                TestConstants.USER_3_NAME,
                null,
                "admin",
                null,
                true,
                null);

    }


    @Test
    public void testCreateUserLogin() throws Exception {
        ClientDeviceInfo deviceInfo = new ClientDeviceInfo();
        deviceInfo.setClientType(ClientType.Mobile);
        JwtAuthorizationRequest jwtAuthenticationRequest = new JwtAuthorizationRequest(TestConstants.USER_3_NAME,
                "admin");
        jwtAuthenticationRequest.setDeviceInfo(deviceInfo);

        String authReqJson = json(jwtAuthenticationRequest);

        mockMvc.perform(post("/auth")
                .contentType(contentType)
                .content(authReqJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email").value(TestConstants.USER_3_NAME))
                .andExpect(jsonPath("$.user.token").exists())
                .andDo(print());

    }

    @Test
    @WithMockUser
    public void testLogout() throws Exception {

        mockMvc.perform(get("/auth/logout")
                .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    public void testLogin() throws Exception {
        ClientDeviceInfo deviceInfo = new ClientDeviceInfo();
        deviceInfo.setClientType(ClientType.Mobile);
        JwtAuthorizationRequest jwtAuthenticationRequest = new JwtAuthorizationRequest(TestConstants.USER_3_NAME,
                "admin");
        jwtAuthenticationRequest.setDeviceInfo(deviceInfo);

        mockMvc.perform(post("/auth/authenticate")
                        .contentType(contentType)
                        .content(json(deviceInfo))
                        .header("Authorization", validToken))
                .andDo(print())
                .andExpect(status().isOk());
    }


    private String pullToken(String contentAsString) {
        System.out.println(contentAsString);
        final String searchString = "token\":";
        int indexTokenAt = contentAsString.indexOf(searchString);
        int startToken = contentAsString.indexOf("\"", indexTokenAt + searchString.length());
        int endToken = contentAsString.indexOf("\"", startToken + 1);

        System.out.print(contentAsString.substring(startToken, endToken));
        return contentAsString.substring(startToken + 1, endToken);
    }

    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
