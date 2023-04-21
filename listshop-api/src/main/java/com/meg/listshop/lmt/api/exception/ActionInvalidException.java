package com.meg.listshop.lmt.api.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ActionInvalidException extends RuntimeException {

    private static final Logger  logger = LoggerFactory.getLogger(ActionInvalidException.class);

    public ActionInvalidException(final String message, final Throwable cause) {
        super(message, cause);
        logger.error("Proposed action invalid." );
    }

    public ActionInvalidException(String msg) {
        super(msg);
        logger.error("Error processing proposal.");
    }
}
