package com.meg.listshop.lmt.api.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ObjectNotFoundException extends RuntimeException {

    private static final Logger logger = LogManager.getLogger(ObjectNotFoundException.class);

    public ObjectNotFoundException(Long object, String type) {
        super("couldn't find object with id [" + object+"] of type [" + type + "]");
    }

    public ObjectNotFoundException(String logmessage,Long object, String type) {
        super("couldn't find object with id [" + object+"] of type [" + type + "]");
        logger.error(logmessage);
    }

    public ObjectNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ObjectNotFoundException(final String message) {
        super(message);
    }

    public ObjectNotFoundException(final Throwable cause) {
        super(cause);
    }
}
