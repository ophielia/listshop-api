package com.meg.listshop.lmt.api.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProposalProcessingException extends Exception {

    private static final Logger  logger = LoggerFactory.getLogger(ProposalProcessingException.class);

    public ProposalProcessingException(final String message, final Throwable cause) {
        super(message, cause);
        logger.error("Error processing proposal.");
    }

    public ProposalProcessingException(String msg) {
        super(msg);
        logger.error("Error processing proposal.");
    }
}
