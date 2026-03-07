package com.meg.listshop.test;

import io.restassured.http.Header;

public class TestUtils {
    public static Header authToken(String token) {
        return new Header( "Authorization", "Bearer " + token);
    }
}
