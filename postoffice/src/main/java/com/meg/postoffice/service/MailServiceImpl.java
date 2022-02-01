package com.meg.postoffice.service;

import com.meg.postoffice.service.config.ContentConfiguration;
import freemarker.template.TemplateException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
@EnableConfigurationProperties(ContentConfiguration.class)
public class MailServiceImpl {


    private final ContentConfiguration contentConfiguration;


    public MailServiceImpl(ContentConfiguration contentConfiguration) {
        this.contentConfiguration = contentConfiguration;
    }

    public String buildEmailContent() throws IOException, TemplateException {
       /* StringWriter stringWriter = new StringWriter();
        Map<String, Object> model = new HashMap<>();
        model.put("test", this.contentConfiguration.getTest());
        freeMarkerConfiguration.getTemplate("email.ftlh").process(model, stringWriter);
        return stringWriter.toString();*/
        return "dummy result";
    }
}
