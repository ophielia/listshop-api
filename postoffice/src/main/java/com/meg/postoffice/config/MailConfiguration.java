/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.postoffice.config;

public class MailConfiguration {

    private String test;
    private String name;
    private String username;
    private String password;
    private String host;
    private Integer port;
    private String protocol;
    private Boolean smtpAuth;
    private Boolean enableStartTls;
    private Boolean enableSSL;
    private Boolean debug;
    private String testDiversionEmail;
    private Boolean sendingEnabled;

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Boolean getSmtpAuth() {
        return smtpAuth;
    }

    public void setSmtpAuth(Boolean smtpAuth) {
        this.smtpAuth = smtpAuth;
    }

    public Boolean getEnableStartTls() {
        return enableStartTls;
    }

    public void setEnableStartTls(Boolean enableStartTls) {
        this.enableStartTls = enableStartTls;
    }

    public Boolean getDebug() {
        return debug;
    }

    public void setDebug(Boolean debug) {
        this.debug = debug;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTestDiversionEmail() {
        return testDiversionEmail;
    }

    public void setTestDiversionEmail(String testDiversionEmail) {
        this.testDiversionEmail = testDiversionEmail;
    }

    public Boolean getSendingEnabled() {
        return sendingEnabled != null && sendingEnabled;
    }

    public void setSendingEnabled(Boolean sendingEnabled) {
        this.sendingEnabled = sendingEnabled;
    }

    public Boolean getEnableSSL() {
        return enableSSL;
    }

    public void setEnableSSL(Boolean enableSSL) {
        this.enableSSL = enableSSL;
    }
}
