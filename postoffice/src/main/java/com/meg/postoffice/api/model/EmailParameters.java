/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.postoffice.api.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmailParameters {

    private EmailType emailType;
    private String receiver;
    private String sender;
    private String subject;
    private Map<String, List<String>> parameters = new HashMap<>();

    public EmailType getEmailType() {
        return emailType;
    }

    public void setEmailType(EmailType emailType) {
        this.emailType = emailType;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Map<String, List<String>> getParameters() {
        return parameters;
    }

    public void addParameter(String key, String value) {
        List<String> values = parameters.get(key);
        if (values == null) {
            values = new ArrayList<>();
        }
        values.add(value);
        parameters.put(key, values);
    }

}
