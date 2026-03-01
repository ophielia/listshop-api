package com.meg.listshop.auth.api.exceptions;

/**
 * Created by margaretmartin on 13/03/2018.
 */
public class UserCreateException extends Throwable {
    public UserCreateException(String message, Exception e) {
        super(message, e);
    }
}
