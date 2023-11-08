/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.lmt.api.web.error;

import com.meg.listshop.lmt.api.exception.*;
import com.meg.listshop.lmt.api.model.ApiError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {

    public RestResponseExceptionHandler() {
        super();
    }

    @ExceptionHandler({ObjectNotFoundException.class})
    public ResponseEntity<Object> handleObjectNotFoundException(final Exception ex, final WebRequest request) {
        var message = "Object not found for the passed id.";
        logger.info(ex.getClass().getName());
        logger.error(message, ex);
        //
        var apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getLocalizedMessage(), message);
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler({BadParameterException.class})
    public ResponseEntity<Object> handleBadParameterException(final Exception ex, final WebRequest request) {
        var message = "Bad Parameter - request can't be processed as sent.";
        logger.info(ex.getClass().getName());
        logger.error(message, ex);
        //
        var apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), message);
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler({TokenException.class})
    public ResponseEntity<Object> handleTokenException(final Exception ex, final WebRequest request) {
        var message = "Token Exception - token missing or invalid.";
        logger.info(ex.getClass().getName());
        logger.error(message, ex);
        //
        var apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getLocalizedMessage(), message);
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler({ObjectNotYoursException.class})
    public ResponseEntity<Object> handleObjectNotYoursException(final Exception ex, final WebRequest request) {
        var message = "Object doesn't belong to user.";
        logger.info(ex.getClass().getName());
        logger.error(message, ex);
        //
        var apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), message);
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }


    @ExceptionHandler({ProposalProcessingException.class})
    public ResponseEntity<Object> handleProposalProcessingException(final Exception ex, final WebRequest request) {
        var message = "Error occured generating proposal.";
        logger.info(ex.getClass().getName());
        logger.error(message, ex);
        //
        var apiError = new ApiError(HttpStatus.SERVICE_UNAVAILABLE, ex.getLocalizedMessage(), message);
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler({ActionInvalidException.class})
    public ResponseEntity<Object> handleActionInvalidException(final Exception ex, final WebRequest request) {
        var message = "Error occured generating proposal.";
        logger.info(ex.getClass().getName());
        logger.error(message, ex);
        var apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), message);
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler({ActionIgnoredException.class})
    public ResponseEntity<Object> handleActionIgnoredException(final Exception ex, final WebRequest request) {
        var message = "The server has chosent to not perform this action.";
        logger.info(ex.getClass().getName());
        logger.error(message, ex);
        var apiError = new ApiError(HttpStatus.NOT_ACCEPTABLE, ex.getLocalizedMessage(), message);
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }


    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    public ResponseEntity<Object> handleAuthenticationException(final Exception ex, final WebRequest request) {
        var message = "Error occured authenticating.";
        logger.info(ex.getClass().getName());
        logger.error(message, ex);
        //
        var apiError = new ApiError(HttpStatus.UNAUTHORIZED, ex.getLocalizedMessage(), message);
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler({ProcessingException.class})
    public ResponseEntity<Object> handleProcessingException(final Exception ex, final WebRequest request) {
        var message = "Error occured while processing request.";
        logger.info(ex.getClass().getName());
        logger.error(message, ex);
        //
        var apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage(), message);
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAll(final Exception ex, final WebRequest request) {
        logger.info(ex.getClass().getName());
        logger.error("error", ex);
        //
        var apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage(), "error occurred");
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

}



