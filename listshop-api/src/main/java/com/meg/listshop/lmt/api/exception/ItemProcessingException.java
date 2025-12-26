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
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ItemProcessingException extends Exception {

    private static final Logger  logger = LoggerFactory.getLogger(ItemProcessingException.class);


    public ItemProcessingException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ItemProcessingException(final String message) {
        super(message);
    }

    public ItemProcessingException(final Throwable cause) {
        super(cause);
    }
}
