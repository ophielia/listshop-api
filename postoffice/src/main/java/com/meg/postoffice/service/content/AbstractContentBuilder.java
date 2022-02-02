/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.postoffice.service.content;

import com.meg.postoffice.api.model.EmailParameters;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public class AbstractContentBuilder implements ContentBuilder {

    private String templateName;
    private EmailParameters parameters;
    private Configuration configuration;

    public AbstractContentBuilder(EmailParameters parameters, String templateName, Configuration configuration) {
        this.parameters = parameters;
        this.templateName = templateName;
        this.configuration = configuration;
    }

    public String buildContent() throws IOException, TemplateException {
        var stringWriter = new StringWriter();

        Map<String, String> modelMap = ContentMap.fromEmailParameters(this.parameters);
        modelMap.put("test", "testy testy");
        configuration.getTemplate(templateName).process(modelMap, stringWriter);
        return stringWriter.toString();
    }

}
