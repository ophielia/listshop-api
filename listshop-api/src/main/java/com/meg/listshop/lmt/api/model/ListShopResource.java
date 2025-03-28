package com.meg.listshop.lmt.api.model;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;

public interface ListShopResource {

    ListShopModel fillLinks(HttpServletRequest request, ListShopModel model);

    URI selfLink(HttpServletRequest request, ListShopModel model);
}
