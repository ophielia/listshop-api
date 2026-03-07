package com.meg.listshop.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class ControllerUtils {

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
}

