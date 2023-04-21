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
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProcessingException extends RuntimeException {

    private static final Logger  logger = LoggerFactory.getLogger(ProcessingException.class);

    public ProcessingException(Long object, String type) {
        super("couldn't find object with id [" + object + "] of type [" + type + "]");
    }

    public ProcessingException(String logmessage, Long object, String type) {
        super("couldn't find object with id [" + object + "] of type [" + type + "]");
        logger.error(logmessage);
    }

    public ProcessingException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ProcessingException(final String message) {
        super(message);
    }

    public ProcessingException(final Throwable cause) {
        super(cause);
    }
}
