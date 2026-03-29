/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.common;

import com.github.dockerjava.api.exception.BadRequestException;
import com.meg.listshop.lmt.api.web.controller.v2.DishRestController;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class ControllerUtils {
    private static final Logger logger = LoggerFactory.getLogger(ControllerUtils.class);

    private ControllerUtils() {
    }

    public static URI locationURI(HttpServletRequest request, String rootPath, Long id) throws MalformedURLException {
        URL url = new URL(request.getRequestURL().toString());
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance()
                .scheme(url.getProtocol())
                .host(url.getHost())
                .port(url.getPort())
                .path(rootPath);
        builder.path("/")
                .path(String.valueOf(id));
        return builder.build().toUri();


    }

    public static Long stringToLongOrException(String toConvert) {
        if (toConvert == null) {
            return null;
        }
        try {
            return Long.parseLong(toConvert);
        } catch (NumberFormatException e) {
            throw new BadRequestException(String.format("Id [%s] cannot be converted to Long.", toConvert));
        }
    }

    public static  Long stringToLongOrDefault(String toConvert, Long defaultValue) {
        if (toConvert == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(toConvert);
        } catch (NumberFormatException e) {
            String message = String.format("Id [%s] cannot be converted to Long.", toConvert);
            logger.info(message);
        }
        return defaultValue;
    }
}

