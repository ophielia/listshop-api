package com.meg.postoffice.service.content;

import com.meg.postoffice.api.model.EmailParameters;
import freemarker.template.Configuration;

public class ContentBuilderFactory {

    private EmailParameters parameters;
    private Configuration configuration;

    public ContentBuilderFactory(EmailParameters parameters, Configuration configuration) {
        this.parameters = parameters;
        this.configuration = configuration;
    }

    public ContentBuilder build() {
        return new SimpleContentBuilder(parameters, configuration);

    }
}
