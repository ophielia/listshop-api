package com.meg.listshop.lmt.api.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class ActionIgnoredException extends RuntimeException {

    private static final Logger logger = LogManager.getLogger(ActionIgnoredException.class);

    public ActionIgnoredException(final String message, final Throwable cause) {
        super(message, cause);
        logger.error("Proposed action invalid.");
    }

    public ActionIgnoredException(String msg) {
        super(msg);
        logger.error("Error processing proposal.");
    }
}
