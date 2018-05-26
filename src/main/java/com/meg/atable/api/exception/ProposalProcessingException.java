package com.meg.atable.api.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProposalProcessingException extends Exception {

    private static final Logger logger = LogManager.getLogger(ProposalProcessingException.class);

    public ProposalProcessingException(final String message, final Throwable cause) {
        super(message, cause);
        logger.error("Error processing proposal.");
    }

    public ProposalProcessingException(String msg) {
        super(msg);
        logger.error("Error processing proposal.");
    }
}
