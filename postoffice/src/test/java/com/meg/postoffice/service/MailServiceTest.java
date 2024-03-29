/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.postoffice.service;

import com.meg.postoffice.api.model.EmailParameters;
import com.meg.postoffice.service.content.ContentMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class MailServiceTest {


    @Test
    public void testSimpleContentMap() {
        EmailParameters parameters = new EmailParameters();
        parameters.setReceiver("receiver");
        parameters.setSender("sender");
        parameters.setSubject("subject");
        parameters.addParameter("firstParameter", "the first parameter");
        parameters.addParameter("secondParameter", "the second parameter");

        // convert to content map
        ContentMap result = ContentMap.fromEmailParameters(parameters);

        // assert filled out correctly
        Assert.assertEquals("receiver doesn't match", "receiver", result.get(ContentMap.RECEIVER));
        Assert.assertEquals("sender doesn't match", "sender", result.get(ContentMap.SENDER));
        Assert.assertEquals("subject doesn't match", "subject", result.get(ContentMap.SUBJECT));
        Assert.assertEquals("first parameter doesn't match", "the first parameter", result.get("firstParameter"));
        Assert.assertEquals("second parameter doesn't match", "the second parameter", result.get("secondParameter"));
    }

    @Test
    public void testContentMapWithList() {
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
        Assert.assertEquals("receiver doesn't match", "receiver", result.get(ContentMap.RECEIVER));
        Assert.assertEquals("sender doesn't match", "sender", result.get(ContentMap.SENDER));
        Assert.assertEquals("subject doesn't match", "subject", result.get(ContentMap.SUBJECT));
        Assert.assertEquals("results should contain count", "4", result.get("parameterList.count"));
        Assert.assertEquals("results should contain list item 1", "listItem 1", result.get("parameterList.0"));
        Assert.assertEquals("results should contain list item 2", "listItem 2", result.get("parameterList.1"));
        Assert.assertEquals("results should contain list item 3", "listItem 3", result.get("parameterList.2"));
        Assert.assertEquals("results should contain list item 4", "listItem 4", result.get("parameterList.3"));
    }
}