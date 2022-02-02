package com.meg.postoffice.service.content;

import com.meg.postoffice.api.model.EmailParameters;
import com.meg.postoffice.api.model.EmailType;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class AbstractContentBuilder implements ContentBuilder {

    private String templateName;
    private EmailParameters parameters;
    private Configuration configuration;

    public AbstractContentBuilder(EmailParameters parameters, String templateName, Configuration configuration) {
        this.parameters = parameters;
        this.templateName = convertEmailTypeToTemplateName(this.parameters.getEmailType());
        this.configuration = configuration;
    }

    public String buildContent() throws IOException, TemplateException {
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> model = new HashMap<>();
        model.put("test", "testy testy");
        configuration.getTemplate(templateName).process(model, stringWriter);
        return stringWriter.toString();
    }

    private String convertEmailTypeToTemplateName(EmailType emailType) {
        var emailTypeString = emailType.toString();
        return emailTypeString + ".ftlh";
    }


}
