package com.meg.listshop.lmt.api.model;

import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AbstractListShopResource implements ListShopResource {

    private boolean reflectRequest;

    List<String> links = new ArrayList<>();

    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }

    public ListShopModel fillLinks(HttpServletRequest request, ListShopModel model) {
        links.add(selfLink(request, model).toString());
        return model;
    }

    public URI selfLink(HttpServletRequest request, ListShopModel model) {
        String returnPath = "blank";
        try {
            URL url = new URL(request.getRequestURL().toString());

            if (model.reflectRequest()) {

                return url.toURI();
            }
            UriComponentsBuilder builder = UriComponentsBuilder.newInstance()
                    .scheme(url.getProtocol())
                    .host(url.getHost())
                    .port(url.getPort())
                    .path(model.getRootPath());
            if (model.getResourceId() != null) {
                builder.path("/")
                        .path(model.getResourceId());
            }
            return builder.build().toUri();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return URI.create("");
    }

    public boolean reflectRequest() {
        return reflectRequest;
    }

    public void setReflectRequest(boolean reflectRequest) {
        this.reflectRequest = reflectRequest;
    }
}
