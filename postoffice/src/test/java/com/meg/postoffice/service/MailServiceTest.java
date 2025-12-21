/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.postoffice.service;

import com.meg.postoffice.api.model.EmailParameters;
import com.meg.postoffice.service.content.ContentMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class MailServiceTest {


    @Test
    void testSimpleContentMap() {
        EmailParameters parameters = new EmailParameters();
        parameters.setReceiver("receiver");
        parameters.setSender("sender");
        parameters.setSubject("subject");
        parameters.addParameter("firstParameter", "the first parameter");
        parameters.addParameter("secondParameter", "the second parameter");

        // convert to content map
        ContentMap result = ContentMap.fromEmailParameters(parameters);

        // assert filled out correctly
        Assertions.assertEquals("receiver", result.get(ContentMap.RECEIVER), "receiver doesn't match");
        Assertions.assertEquals("sender", result.get(ContentMap.SENDER), "sender doesn't match");
        Assertions.assertEquals("subject", result.get(ContentMap.SUBJECT), "subject doesn't match");
        Assertions.assertEquals("the first parameter", result.get("firstParameter"), "first parameter doesn't match");
        Assertions.assertEquals("the second parameter", result.get("secondParameter"), "second parameter doesn't match");
    }

    @Test
    void testContentMapWithList() {
        EmailParameters parameters = new EmailParameters();
        parameters.setReceiver("receiver");
        parameters.setSender("sender");
        parameters.setSubject("subject");
        parameters.addParameter("parameterList", "listItem 1");
        parameters.addParameter("parameterList", "listItem 2");
        parameters.addParameter("parameterList", "listItem 3");
        parameters.addParameter("parameterList", "listItem 4");

        // convert to content map
        ContentMap result = ContentMap.fromEmailParameters(parameters);

        // assert filled out correctly
        Assertions.assertEquals("receiver", result.get(ContentMap.RECEIVER), "receiver doesn't match");
        Assertions.assertEquals("sender", result.get(ContentMap.SENDER), "sender doesn't match");
        Assertions.assertEquals("subject", result.get(ContentMap.SUBJECT), "subject doesn't match");
        Assertions.assertEquals("4", result.get("parameterList.count"), "results should contain count");
        Assertions.assertEquals("listItem 1", result.get("parameterList.0"), "results should contain list item 1");
        Assertions.assertEquals("listItem 2", result.get("parameterList.1"), "results should contain list item 2");
        Assertions.assertEquals("listItem 3", result.get("parameterList.2"), "results should contain list item 3");
        Assertions.assertEquals("listItem 4", result.get("parameterList.3"), "results should contain list item 4");
    }
}
