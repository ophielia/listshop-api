package com.meg.listshop.lmt.api.model;

import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AbstractListShopResource implements ListShopResource {

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
            url.getHost();
            url.getProtocol();
            url.getPath();
            return UriComponentsBuilder.newInstance()
                    .scheme(url.getProtocol())
                    .host(url.getHost())
                    .port(url.getPort())
                    .path(model.getRootPath())
                    .path("/")
                    .path(model.getResourceId()).build().toUri();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return URI.create("");
    }


}
