/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.postoffice.service.content;

import freemarker.template.TemplateException;

import java.io.IOException;

public interface ContentBuilder {

    String buildContent() throws IOException, TemplateException;
}
