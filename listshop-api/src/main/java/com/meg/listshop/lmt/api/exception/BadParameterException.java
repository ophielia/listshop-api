package com.meg.listshop.lmt.api.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadParameterException extends Exception {

    private static final Logger logger = LogManager.getLogger(BadParameterException.class);

    public BadParameterException(final String message, final Throwable cause) {
        super(message, cause);
        logger.error("All required parameters not present.");
    }

    public BadParameterException(String msg) {
        super(msg);
        logger.error("All required parameters not present.");
    }
}
