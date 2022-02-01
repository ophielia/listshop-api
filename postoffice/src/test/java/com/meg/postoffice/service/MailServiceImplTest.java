package com.meg.postoffice.service;

import com.meg.postoffice.service.config.PostOfficeConfiguration;
import freemarker.template.TemplateException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {PostOfficeConfiguration.class})
@TestPropertySource(locations = "/my.properties")
//@SpringBootTest("postoffice.test=Hello")
public class MailServiceImplTest {

    @Autowired
    private MailServiceImpl mailService;

    @Test
    public void contextLoads() throws TemplateException, IOException {
        assertThat(mailService.buildEmailContent()).isNotNull();
    }

    @SpringBootApplication
    static class TestConfiguration {
    }


    @Test
    public void testDevelop() throws TemplateException, IOException {

        String resultTest = mailService.buildEmailContent();
        Assert.assertNotNull(resultTest);
        Assert.assertEquals(2, 1 + 1);
    }

}