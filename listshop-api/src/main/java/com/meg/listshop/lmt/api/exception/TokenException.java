/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.lmt.api.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TokenException extends Exception {

    private static final Logger  logger = LoggerFactory.getLogger(TokenException.class);

    public TokenException(final String message, final Throwable cause) {
        super(message, cause);
        logger.error("Token is missing or invalid.");
    }

    public TokenException(String msg) {
        super(msg);
        logger.error("Token is missing or invalid.");
    }
}
