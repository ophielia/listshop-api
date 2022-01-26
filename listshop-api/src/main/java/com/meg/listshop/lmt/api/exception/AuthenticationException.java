package com.meg.listshop.lmt.api.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AuthenticationException extends RuntimeException {

    private static final Logger logger = LogManager.getLogger(AuthenticationException.class);

    public AuthenticationException(final String message, final Throwable cause) {
        super(message, cause);
        logger.error("Unable to authorize.");
    }

    public AuthenticationException(String msg) {
        super(msg);
        logger.error("Unable to authorize.");
    }
}
