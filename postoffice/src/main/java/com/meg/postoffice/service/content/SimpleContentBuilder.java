package com.meg.postoffice.service.content;

import com.meg.postoffice.api.model.EmailParameters;
import com.meg.postoffice.api.model.EmailType;
import freemarker.template.Configuration;

public class SimpleContentBuilder extends AbstractContentBuilder {

    private String templateName;
    private EmailParameters parameters;


    public SimpleContentBuilder(EmailParameters parameters, Configuration configuration) {
        super(parameters, convertEmailTypeToTemplateName(parameters.getEmailType()), configuration);
    }


    private static String convertEmailTypeToTemplateName(EmailType emailType) {
        var emailTypeString = emailType.toString();
        return emailTypeString + ".ftlh";
    }


}
