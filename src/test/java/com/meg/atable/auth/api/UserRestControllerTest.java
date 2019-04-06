package com.meg.atable.auth.api;

import com.meg.atable.Application;
import com.meg.atable.auth.api.model.User;
import com.meg.atable.auth.data.entity.UserEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.auth.service.impl.JwtAuthenticationRequest;
import com.meg.atable.auth.service.impl.JwtUser;
import com.meg.atable.lmt.service.DishService;
import com.meg.atable.test.TestConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceType;
import org.springframework.mobile.device.LiteDevice;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
public class UserRestControllerTest {

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));
    private MockMvc mockMvc;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;
    private UserDetails userDetails;

    @Autowired
    private DishService dishService;

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
                "Passw0rd",
                null,
                true,
                null);

    }


    @Test
    public void testCreateUser() throws Exception {
        Device device = new LiteDevice(DeviceType.NORMAL);
        final String username = "dXNlcm5hbWU=";
        final String email = "ZW1haWw=";
        final String password = "UGFzc3cwcmQ=";
        User user = new User(username, email, password);
        final String userjson = json(user);
        final String deviceJson = json(device);

        mockMvc.perform(post("/user")
                .contentType(contentType)
                .content(userjson)
                .characterEncoding("utf-8"))
                //.content(deviceJson))
                .andDo(print());
                /*.andExpect(content().contentType(contentType));*/
        //.andExpect(content().contentType(contentType));
    }

    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
