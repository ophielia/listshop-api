package com.meg.listshop.lmt.api.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ObjectNotYoursException extends RuntimeException {

    private static final Logger  logger = LoggerFactory.getLogger(ObjectNotYoursException.class);

    public ObjectNotYoursException(Long objectId, Long userId) {
        super("Object [" + objectId + "] found, but doesn't belong to user [" + userId + "]");
    }

    public ObjectNotYoursException(String logmessage, String objectType, Long objectId, String userName) {
        super("Object [" + objectId + "] of type [" + objectType + "] found, but doesn't belong to user [" + userName + "]");
        logger.error(logmessage);
    }

    public ObjectNotYoursException(String logmessage, String objectType, Long objectId, Long userId) {
        super(String.format("Object [%s] of type [%s] found, but doesn't belong to user [%s]", objectId, objectType, userId));
        logger.error(logmessage);
    }

    public ObjectNotYoursException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ObjectNotYoursException(final String message) {
        super(message);
    }

    public ObjectNotYoursException(final Throwable cause) {
        super(cause);
    }
}
