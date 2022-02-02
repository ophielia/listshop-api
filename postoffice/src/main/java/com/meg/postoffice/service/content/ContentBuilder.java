package com.meg.postoffice.service.content;

import freemarker.template.TemplateException;

import java.io.IOException;

public interface ContentBuilder {

    public String buildContent() throws IOException, TemplateException;
}
